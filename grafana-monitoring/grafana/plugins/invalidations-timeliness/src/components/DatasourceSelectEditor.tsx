/**
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

import React, { useEffect } from 'react';
import { StandardEditorProps, DataSourceSettings, SelectableValue } from '@grafana/data';
import { Select } from '@grafana/ui';
import { getBackendSrv } from '@grafana/runtime';

export const optionsDataSource: Array<SelectableValue<string>> = []
interface Settings {
  datasourceType: 'postgres';
}

interface Props extends StandardEditorProps<string | string[], Settings> { }

export const DatasourceSelectEditor: React.FC<Props> = ({ item, value, onChange }) => {
  useEffect(() => {
    getBackendSrv()
      .get('api/datasources')
      .then(dss => {
        dss
          .filter((e: DataSourceSettings) => e.type === item.settings?.datasourceType ?? 'postgres')
          .map(
            (e: DataSourceSettings) => {
              optionsDataSource.push({ label: e.name + ' ' + e.database, value: e.name + ' ' + e.database, description: `${e.type} datasource` });
            }
          )
      }
      )
  }, [item.settings?.datasourceType])


  return optionsDataSource.length > 0 ? <Select defaultValue={optionsDataSource[0].value} value={value} onChange={(e: any) => onChange(e.value)} options={optionsDataSource} /> : null;
};
