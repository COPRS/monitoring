package eu.csgroup.coprs.monitoring.common.datamodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    @NotNull
    @Size(max=1024, message="Message content cannot exceed 1024 characters")
    private String content;

}
