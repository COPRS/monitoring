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

package eu.csgroup.coprs.monitoring.tracefilter.json;

import javax.validation.ConstraintViolation;
import java.util.Set;

public class JsonValidationException extends Exception {
    private final StringBuilder violationMessage = new StringBuilder();

    public JsonValidationException() {
        super();
    }

    public JsonValidationException(String message) {
        super(message);
    }

    public JsonValidationException(Throwable throwable) {
        super(throwable);
    }

    public JsonValidationException(String message, Throwable throwable) {
        super(message, throwable);
    }

    @Override
    public String getLocalizedMessage() {
        String initialMessage  =  super.getLocalizedMessage();
        if (initialMessage == null || initialMessage.isEmpty()) {
            return  "Json object does not validate all constraints\n" + violationMessage;
        } else {
           return  initialMessage + "\n" + violationMessage;
        }
    }

    @Override
    public String getMessage() {
        String initialMessage  =  super.getMessage();
        if (initialMessage == null || initialMessage.isEmpty()) {
            return  "Json object does not validate all constraints\n" + violationMessage;
        } else {
            return  initialMessage + "\n" + violationMessage;
        }
    }

    public <T> void setViolations(Set<ConstraintViolation<T>> violations) {
        violationMessage.delete(0, violationMessage.length());

        for (ConstraintViolation<?> violation : violations) {
            if (! violationMessage.isEmpty()) {
                violationMessage.append("\n");
            }
            violationMessage.append(violation.getRootBeanClass().getSimpleName())
                    .append(".")
                    .append(violation.getPropertyPath())
                    .append(" ")
                    .append(violation.getMessage());
        }
    }
}
