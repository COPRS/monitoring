package eu.csgroup.coprs.monitoring.common.properties;

import eu.csgroup.coprs.monitoring.common.bean.ReloadableBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.builder.ConfigurationBuilderEvent;
import org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.event.Event;
import org.apache.commons.configuration2.event.EventListener;
import org.apache.commons.configuration2.reloading.PeriodicReloadingTrigger;
import org.apache.commons.configuration2.tree.DefaultExpressionEngine;
import org.apache.commons.configuration2.tree.DefaultExpressionEngineSymbols;
import org.apache.commons.configuration2.tree.ExpressionEngine;
import org.apache.commons.configuration2.tree.ImmutableNode;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

@Slf4j
public class ReloadableYamlPropertySource extends EnumerablePropertySource implements ReloadableBean {
    ReloadingFileBasedConfigurationBuilder<FileBasedConfiguration> builder;

    private String lastRequested;

    private static String PROPERTY_DELIMITER = ".";

    private static String QUOTED_PROPERTY_DELIMITER = Pattern.quote(PROPERTY_DELIMITER);

    private static String ESCAPED_DELIMITER = "..";

    private AtomicBoolean dirty = new AtomicBoolean(false);

    /**
     * Expression engine comptible with spring convention
     */
    private ExpressionEngine expressionEngine;


    public ReloadableYamlPropertySource(String name, final String path) {
        super(StringUtils.hasText(name) ? name : path);

        Parameters params = new Parameters();
        // Read data from this file
        File propertiesFile = new File(path);

        // Create reader with reload notifier
        builder =
                new ReloadingFileBasedConfigurationBuilder<FileBasedConfiguration>(YAMLConfiguration.class)
                        .configure(params.fileBased()
                                .setFile(propertiesFile));

        builder.addEventListener(
                ConfigurationBuilderEvent.RESET,
                (EventListener<Event>) event -> {
                    dirty.set(true);
                    log.info("Configuration file '%s' loaded".formatted(path));
                });

        // Create expression engine compatible with spring key property
        final var expEngConf = new DefaultExpressionEngineSymbols.Builder()
                .setPropertyDelimiter(PROPERTY_DELIMITER)
                .setEscapedDelimiter(ESCAPED_DELIMITER)
                .setIndexStart("[")
                .setIndexEnd("]")
                .setAttributeStart("[@")
                .setAttributeEnd("]")
                .create();
        expressionEngine = new DefaultExpressionEngine(expEngConf);

        ((YAMLConfiguration) getConfiguration()).setExpressionEngine(expressionEngine);

        // Create reload check
        PeriodicReloadingTrigger trigger = new PeriodicReloadingTrigger(builder.getReloadingController(),
                null, 1, TimeUnit.MINUTES);
        trigger.start();


    }


    public FileBasedConfiguration getConfiguration () {
        try {
            final var currentConfig =  builder.getConfiguration();
            // On configuration reload need to apply our expression engine.
            if (((YAMLConfiguration)currentConfig).getExpressionEngine() != expressionEngine) {
                ((YAMLConfiguration)currentConfig).setExpressionEngine(expressionEngine);
            }
            return currentConfig;
        } catch (Exception e) {
            throw new PropertiesException(e);
        }
    }

    @Override
    public Object getProperty(String s) {
        lastRequested = s;
        log.trace("Required key: %s".formatted(s));

        final var property = getConfiguration().getProperty(s);
        log.trace("Value: %s".formatted(property));
        return property;
    }

    @Override
    public String[] getPropertyNames() {
        final var propertiesConfiguration = getConfiguration();
        final var expEng = ((YAMLConfiguration)propertiesConfiguration).getExpressionEngine();
        final var nodeHandler = ((YAMLConfiguration) propertiesConfiguration).getNodeModel().getNodeHandler();

        return expEng.query(nodeHandler.getRootNode(), lastRequested, nodeHandler)
                .stream()
                .map(qr -> getChildKeys(lastRequested, qr.getNode()))
                .reduce(new Vector<>(), (l,n) -> {
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

    @Override
    public boolean isReloadNeeded() {
        return dirty.get();
    }

    public void setReloaded () {
        dirty.set(false);
    }
}
