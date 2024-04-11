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

package eu.csgroup.coprs.monitoring.common.properties;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.TimeUnit;

/**
 * Singleton instance that manipulate global properties to define refresh period for configuration files
 */
@Getter
@Setter
public class ReloadablePropertySourceEnvironment {
    public static final long DEFAULT_REFRESH_PERIOD = 1;

    public static final TimeUnit DEFAULT_REFRESH_PERIOD_UNIT = TimeUnit.MINUTES;

    private static final ReloadablePropertySourceEnvironment INSTANCE = new ReloadablePropertySourceEnvironment();

    private long refreshPeriodValue = DEFAULT_REFRESH_PERIOD;

    private TimeUnit refreshPeriodUnit = DEFAULT_REFRESH_PERIOD_UNIT;

    private ReloadablePropertySourceEnvironment () {

    }

    public static ReloadablePropertySourceEnvironment getInstance () {
        return INSTANCE;
    }

    /**
     * Change global refresh period
     *
     * @param refreshPeriod period
     * @param timeUnit unit time
     */
    public void setRefreshPeriod(long refreshPeriod, TimeUnit timeUnit) {
        this.refreshPeriodValue = refreshPeriod;
        this.refreshPeriodUnit = timeUnit;
    }
}
