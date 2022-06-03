package eu.csgroup.coprs.monitoring.tracefilter.rule;

import lombok.Data;

import java.util.Map;

@Data
public class Rule {
    private String name;
    private Map<String, String> conditions;
}
