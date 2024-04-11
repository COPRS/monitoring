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

package eu.csgroup.coprs.monitoring.common.ingestor;

import eu.csgroup.coprs.monitoring.common.datamodel.entities.DefaultEntity;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.Map;


public class DataBaseIngestionTimer {

    private static DataBaseIngestionTimer instance;

    private final ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();

    private final Long[] globalIngestionTime;

    private final Map<Class<? extends DefaultEntity>, Long[]> unitaryIngestionTime;


    public static DataBaseIngestionTimer getInstance() {
        if (instance == null) {
            instance = new DataBaseIngestionTimer();
        }
        return instance;
    }

    private DataBaseIngestionTimer() {
        this.globalIngestionTime = new Long[2];
        this.unitaryIngestionTime = new HashMap<>();

    }

    public void reset() {
        this.unitaryIngestionTime.clear();
    }

    public void startGlobalTimer() {
        this.globalIngestionTime[0] = this.threadBean.getThreadCpuTime(Thread.currentThread().getId());
    }

    public void endGlobalTimer() {
        this.globalIngestionTime[1] = this.threadBean.getThreadCpuTime(Thread.currentThread().getId());
    }

    public Long resolveGlobalTimer() {
        return (globalIngestionTime[1] - globalIngestionTime[0]) / 1000000;
    }

    public void startUnitaryTimer(Class<? extends DefaultEntity> entityClass) {
        //weird syntax proposed by IDe to say "if the entry isn't there, instantiate new array of longs"
        this.unitaryIngestionTime.computeIfAbsent(entityClass, k -> new Long[2]);

        this.unitaryIngestionTime.get(entityClass)[0] = this.threadBean.getThreadCpuTime(Thread.currentThread().getId());
    }

    public void endUnitaryTimer(Class<? extends DefaultEntity> entityClass) {
        this.unitaryIngestionTime.computeIfAbsent(entityClass, k -> new Long[2]);

        this.unitaryIngestionTime.get(entityClass)[1] = this.threadBean.getThreadCpuTime(Thread.currentThread().getId());
    }

    public Long resolveUnitaryTimer(Class<? extends DefaultEntity> entityClass) {
        Long[] arr = this.unitaryIngestionTime.get(entityClass);
        return (arr[1] - arr[0]) / 1000000;
    }


}
