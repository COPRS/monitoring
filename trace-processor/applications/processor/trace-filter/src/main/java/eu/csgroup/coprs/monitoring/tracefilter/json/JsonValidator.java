package eu.csgroup.coprs.monitoring.tracefilter.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * Deserialize a string in json format and parse parameterized class to find and validate all
 * {@link javax.validation.constraints} annotation to check validity of the object.
 */
@Component
public class JsonValidator {
    @Autowired
    @Getter
    ObjectMapper mapper;

    public <T> T readAndValidate (String content, Class<T> valueType) throws JsonProcessingException, JsonValidationException {
        // Deserialize
        final var res = mapper.readValue(content, valueType);

        // Validate
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            final var violations = validator.validate(res);

            if (!violations.isEmpty()) {
                final var exception = new JsonValidationException();
                exception.setViolations(violations);
                throw exception;
            }
        }

        return res;
    }
}
