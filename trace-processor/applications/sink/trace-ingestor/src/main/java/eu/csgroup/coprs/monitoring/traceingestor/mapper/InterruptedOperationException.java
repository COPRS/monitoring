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

import java.io.Serial;

public class InterruptedOperationException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -2203129201027853275L;

    public InterruptedOperationException(String message) {
        super(message);
    }

    public InterruptedOperationException (Exception e) {
        super(e);
    }

    public InterruptedOperationException(String message, Exception e) {
        super(message, e);
    }
}
