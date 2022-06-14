package eu.csgroup.coprs.monitoring.common.properties;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.reloading.PeriodicReloadingTrigger;
import org.apache.commons.configuration2.tree.DefaultExpressionEngine;
import org.apache.commons.configuration2.tree.DefaultExpressionEngineSymbols;
import org.apache.commons.configuration2.tree.ImmutableNode;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Slf4j
public class ReloadableYamlPropertySource extends EnumerablePropertySource {
    FileBasedConfiguration propertiesConfiguration;

    private String lastRequested;

    private static String PROPERTY_DELIMITER = ".";

    private static String QUOTED_PROPERTY_DELIMITER = Pattern.quote(PROPERTY_DELIMITER);

    private static String ESCAPED_DELIMITER = "..";

    public ReloadableYamlPropertySource(String name, FileBasedConfiguration propertiesConfiguration) {
        super(name);
        this.propertiesConfiguration = propertiesConfiguration;
    }

    public ReloadableYamlPropertySource(String name, String path) {
        super(StringUtils.hasText(name) ? path : name);

        Parameters params = new Parameters();
        // Read data from this file
        File propertiesFile = new File(path);

        // Create reader with reload notifier
        ReloadingFileBasedConfigurationBuilder<FileBasedConfiguration> builder =
                new ReloadingFileBasedConfigurationBuilder<FileBasedConfiguration>(YAMLConfiguration.class)
                        .configure(params.fileBased()
                                .setFile(propertiesFile));

        // Create reload check
        PeriodicReloadingTrigger trigger = new PeriodicReloadingTrigger(builder.getReloadingController(),
                null, 1, TimeUnit.MINUTES);
        trigger.start();

        // Create expression engine compatible with spring key property
        try {
            propertiesConfiguration = builder.getConfiguration();
            final var expEngConf = new DefaultExpressionEngineSymbols.Builder()
                    .setPropertyDelimiter(PROPERTY_DELIMITER)
                    .setEscapedDelimiter(ESCAPED_DELIMITER)
                    .setIndexStart("[")
                    .setIndexEnd("]")
                    .setAttributeStart("[@")
                    .setAttributeEnd("]")
                    .create();
            final var expEng = new DefaultExpressionEngine(expEngConf);
            ((YAMLConfiguration)propertiesConfiguration).setExpressionEngine(expEng);
        } catch (Exception e) {
            throw new PropertiesException(e);
        }
    }

    @Override
    public Object getProperty(String s) {
        lastRequested = s;
        log.trace("Required key: %s".formatted(s));

        final var property = propertiesConfiguration.getProperty(s);
        log.trace("Value: %s".formatted(property));
        return property;
    }

    @Override
    public String[] getPropertyNames() {
        final var expEng = ((YAMLConfiguration)propertiesConfiguration).getExpressionEngine();
        final var nodeHandler = ((YAMLConfiguration) propertiesConfiguration).getNodeModel().getNodeHandler();

        return expEng.query(nodeHandler.getRootNode(), lastRequested, nodeHandler)
                .stream()
                .map(qr -> getChildKeys(lastRequested, qr.getNode()))
                .reduce(new Vector<String>(), (l,n) -> {
                    l.addAll(n);
                    return l;
                })
                .toArray(String[]::new);
    }

    private List<String> getChildKeys (String keyPrefix, ImmutableNode rootNode) {
        return rootNode.getChildren()
            .stream()
            .map(n -> "%s.%s".formatted(keyPrefix, convertKey(n.getNodeName())))
            .peek(t -> log.trace("Found key: %s".formatted(t)))
            .toList();
    }

    private String convertKey (String apacheKey) {
        final var springKey = apacheKey.replaceAll(QUOTED_PROPERTY_DELIMITER, ESCAPED_DELIMITER);
        return springKey;
    }
}
