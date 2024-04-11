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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode()
@ToString(onlyExplicitlyIncluded = true )
public class TreePropertyNode implements TreeProperty {

    @ToString.Include
    private final String path;

    private final List<TreePropertyLeaf> leaves = new ArrayList<>();

    private final List<TreePropertyNode> nodes = new ArrayList<>();


    public void addLeaf(TreePropertyLeaf leaf) {
        add(leaves, leaf);
    }

    public void addNode(TreePropertyNode node) {
        add(nodes, node);
    }

    public void addAllNode (List<TreePropertyNode> nodes) {
        this.nodes.addAll(nodes);
    }


    private <T extends TreeProperty> void add(List<T> list, T treeProperty) {
        list.add(treeProperty);
    }

    public TreePropertyNode copy(String path) {
        final var newNode = new TreePropertyNode(path);

        for (var node: this.getNodes()) {
            newNode.addNode(node.copy(node.path));
        }

        for (var leaf: this.getLeaves()) {
            newNode.addLeaf(leaf.copy());
        }

        return newNode;
    }
}