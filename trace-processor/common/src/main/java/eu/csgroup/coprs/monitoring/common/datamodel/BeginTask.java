package eu.csgroup.coprs.monitoring.common.datamodel;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BeginTask extends Task {
    private String child_of_task;
    //@JsonProperty("follow_from_task")
    private String followsFromTask;
}
