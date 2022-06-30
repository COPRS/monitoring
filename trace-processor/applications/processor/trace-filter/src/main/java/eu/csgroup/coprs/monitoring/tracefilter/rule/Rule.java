package eu.csgroup.coprs.monitoring.tracefilter.rule;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Predicate;
import java.util.regex.Pattern;

@Data
@Slf4j
public class Rule implements Predicate<String> {
    private final String key;
    private final String rawValue;

    private Pattern compiledValue;


    public Pattern compile() {
        if (compiledValue == null) {
            compiledValue = Pattern.compile(rawValue);
        }

        return compiledValue;
    }

    @Override
    public boolean test(String value) {
        return compile().matcher(value).matches();
    }
}
