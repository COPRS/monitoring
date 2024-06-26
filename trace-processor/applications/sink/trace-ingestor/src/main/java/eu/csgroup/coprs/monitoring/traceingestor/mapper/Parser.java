/*
 * Copyright 2023 CS Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.csgroup.coprs.monitoring.traceingestor.mapper;

import eu.csgroup.coprs.monitoring.common.bean.BeanAccessor;
import eu.csgroup.coprs.monitoring.common.bean.BeanProperty;
import eu.csgroup.coprs.monitoring.common.properties.PropertyUtil;
import eu.csgroup.coprs.monitoring.traceingestor.config.Mapping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.InvalidPropertyException;

import java.util.*;

/**
 * Construct tree structure of a trace based on a set of {@link Mapping}/<br>
 * <br>
 * Node is created when parsing of a bean property reach a collection value.
 * If value of a bean property path is a collection, no node is created but a leaf is created instead.
 * <pre><code>
 * {
 *   trace: {
 *     task: {
 *       missing_output: [
 *         {
 *           product_metadata_custom_object: {
 *           },
 *           estimated_count_integer: 10
 *         },
 *         {
 *           product_metadata_custom_object: {
 *           },
 *           estimated_count_integer: 14
 *         },
 *       ]
 *     }
 *   }
 * }
 * </code></pre>
 * If mapping contains the following bean property trace.task.missing_output[estimated_count_integer],
 * the following tree will be created:
 * <pre><code>
 *                             trace.task
 *                              /     \
 *     trace.task.missing_output[0] trace.task.missing_output[1]
 * </code></pre>
 * Whereas setting following bean property trace.task.missing_output no node will be created.
 *
 * @param rules Set of mapping to use to create tree.
 */
@Slf4j
public record Parser(List<Mapping> rules) {
    /**
     * Parse given bean to create tree according to mappings set.
     *
     * @param wrapper bean to parse
     * @return tree structure of the bean associated to given mappings
     */
    public TreePropertyNode parse(BeanAccessor wrapper) {
        return parse(rules.iterator(), wrapper);
    }

    /**
     * Parse given bean to create tree according to mappings set.
     *
     * @param iterator list of mapping to use to create the tree
     * @param wrapper bean to parse
     * @return tree structure of the bean associated to given mappings
     */
    public TreePropertyNode parse(Iterator<Mapping> iterator, BeanAccessor wrapper) {
        final var tree = new TreePropertyNode("");
        while (iterator.hasNext()) {
            parse(tree, iterator.next(), wrapper);
        }

        return tree;
    }

    /**
     * Create/Update leaf representing a value for the given  {@link Mapping} and {@link BeanProperty}.
     * As a mapping can contain multiple bean property, leaf can also contain multiple value (one value per bean property).<br>
     * <br>
     * A leaf is updated only if for the given {@link TreePropertyNode} a leaf exists and his mapping is the same than
     * the given one.
     *
     * @param tree Current node
     * @param rule mapping rule
     * @param beanProperty bean property
     * @param value value to set in leaf for the associated bean property and mapping rule.
     */
    private void createOrUpdateLeaf (TreePropertyNode tree, Mapping rule, BeanProperty beanProperty, Object value) {
        // Check if a leaf for the given mapping rule already exists
        final var existingLeaf = tree.getLeaves()
                .stream()
                .filter(leaf -> leaf.getRule().equals(rule))
                .findFirst();

        // If so set/update the value in the found leaf
        if (existingLeaf.isPresent()) {
            existingLeaf.get().putRawValue(beanProperty, value);
        } else {
            // Otherwise create new leaf and set value
            var leaf = new TreePropertyLeaf(rule);
            leaf.putRawValue(beanProperty, value);

            tree.addLeaf(leaf);
        }
    }

    /**
     * Extract value for the given mapping. If value is directly accessed, create leaf for this value in the current node.
     * If value can't be accessed directly, parse each level of the path to detect collection and create associated node until
     * last level of the path is reached to create the leaf.
     *
     * @param tree Current node
     * @param rule Mapping for which to extract the value
     * @param wrapper Bean in which to extract the value
     */
    private void parse(TreePropertyNode tree, Mapping rule, BeanAccessor wrapper) {
        for (var wrappedBeanProperty : rule.getFrom()) {
            final var beanProperty = wrappedBeanProperty.getWrappedObject();
            Object value;
            try {
                value = wrapper.getPropertyValue(beanProperty);

                createOrUpdateLeaf(tree, rule, beanProperty, value);
            } catch (InvalidPropertyException e) {
                // Split bean property path to find array value to create branch (one value in the array per branch)
                final var splittedPath = PropertyUtil.splitPath(beanProperty.getBeanPropertyPath());
                parsePropertyNode(tree, wrapper, rule, beanProperty, beanProperty.getBeanName(), splittedPath);
            }
        }
    }


    /**
     * Parse node where root path doesn't symbolize an array
     *
     * @param tree Parent node
     * @param wrapper Wrapper encapsulating bean in which we have to retrieve values
     * @param rule mapping rule to which bean property depend on
     *             (useful when mapping contains multiple 'from' and we have to set them in the same leaf)
     * @param beanProperty Current bean property to handle
     * @param rootPath current path of the bean property path (which is a portion of the last one)
     * @param splittedPropertyPath Array containing portion of the path (of bean property) that is not yet parsed
     */
    private void parsePropertyNode(TreePropertyNode tree, BeanAccessor wrapper, Mapping rule, BeanProperty beanProperty,  String rootPath, String[] splittedPropertyPath) {
        var currentPath = rootPath;
        var stopParsing = false;
        var index = 0;

        // Parse path until reaching collection value or null value (path not complete in trace)
        while (! stopParsing && index < splittedPropertyPath.length) {
            currentPath = PropertyUtil.getPath(currentPath, splittedPropertyPath[index]);

            try {
                final var object = wrapper.getPropertyValue(new BeanProperty(currentPath));

                // End of the path reached; create leaf
                if (!currentPath.isEmpty() && index == splittedPropertyPath.length - 1) {
                    createOrUpdateLeaf(tree, rule, beanProperty, object);
                } else if (object instanceof final Collection<?> collection) {
                    final var remainingSplittedPropertyPath = Arrays.copyOfRange(splittedPropertyPath, index + 1, splittedPropertyPath.length);

                    parsePropertyNode(tree, wrapper, rule, beanProperty, currentPath, remainingSplittedPropertyPath, collection);
                    // Stop loop because end of the path was parsed in the above function
                    stopParsing = true;
                } else if (object == null) {
                    createOrUpdateLeaf(tree, rule, beanProperty, null);
                }
            } catch (InvalidPropertyException e) {
                // Do not take into account path which refer to map value type
                final var beginBracket = currentPath.indexOf("[");
                final var endBracket = currentPath.indexOf("]");
                // Intended to replace this call: currentPath.matches(".*\\[[a-z0-9_]+\\].*")
                // Avoid Denial of Service (DoS) with regex
                // Do not take into account list case ([1])
                if (endBracket - beginBracket > 1 && currentPath.substring(beginBracket + 1, endBracket).matches("\\d[a-z]|[a-z]")) {
                    // Refer to a field that is not part of trace structure
                    throw new InterruptedOperationException(
                            "Path %s (%s) refer to a field that is not part of trace structure".formatted(currentPath, beanProperty)
                            , e
                    );
                } else {
                    // Exclude property where path does not match with bean tree
                    log.warn(e.getMessage());

                    stopParsing = true;
                }
            }

            index++;
        }
    }

    /**
     * Parse node where current root path symbolize an array
     *
     * @param tree Parent node
     * @param wrapper Wrapper encapsulating bean in which we have to retrieve values
     * @param rule mapping rule to which bean property depend on
     *             (useful when mapping contains multiple 'from' and we have to set them in the same leaf)
     * @param beanProperty Current bean property to handle
     * @param rootPath current path of the bean property path (which is a portion of the last one)
     * @param splittedPropertyPath Array containing portion of the path (of bean property) that is not yet parsed
     * @param node values of the root path
     */
    private void parsePropertyNode(TreePropertyNode tree, BeanAccessor wrapper, Mapping rule, BeanProperty beanProperty, String rootPath, String[] splittedPropertyPath, Collection<?> node) {
        final var newNodes = new ArrayList<TreePropertyNode>();

        for (int index = 0; index < node.size(); index++) {
            final var propertyIndex = "[%s]".formatted(index);
            final var currentPath = PropertyUtil.getPath(rootPath, propertyIndex);

            // Find a node which contains desired path
            var opTreeNode = tree.getNodes()
                    .stream()
                    .filter(subNode -> subNode.getPath().equals(currentPath))
                    .findFirst();

            TreePropertyNode treeNode;
            if (opTreeNode.isEmpty()) {
                // Otherwise create new one
                treeNode = new TreePropertyNode(currentPath);
                tree.addNode(treeNode);
            } else {
                treeNode = opTreeNode.get();
            }

            parsePropertyNode(treeNode, wrapper, rule, beanProperty, currentPath, splittedPropertyPath);
        }

        if (node.isEmpty()) {
            createOrUpdateLeaf(tree, rule, beanProperty, null);
        } else {
            tree.addAllNode(newNodes);
        }
    }
}
