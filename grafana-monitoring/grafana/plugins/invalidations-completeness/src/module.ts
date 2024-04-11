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

import { PanelPlugin } from '@grafana/data';
import { TableCellDisplayMode } from '@grafana/ui';
import { SELECTOPTIONS } from 'components/InvalidationEditor/options';
import RootCauseEditor from 'components/RootCauseEditor/Editor';
import { tablePanelChangedHandler } from 'migrations';
import { Panel } from 'SimplePanel';
import { PanelOptions } from './types';

export const plugin = new PanelPlugin<PanelOptions>(Panel)
  .setPanelChangeHandler(tablePanelChangedHandler)
  .setNoPadding()
  .useFieldConfig({
    useCustomConfig: builder => {
      builder
        .addNumberInput({
          path: 'width',
          name: 'Column width',
          settings: {
            placeholder: 'auto',
            min: 20,
            max: 300,
          },
          shouldApply: () => true,
        })
        .addRadio({
          path: 'align',
          name: 'Column alignment',
          settings: {
            options: [
              { label: 'auto', value: null },
              { label: 'left', value: 'left' },
              { label: 'center', value: 'center' },
              { label: 'right', value: 'right' },
            ],
          },
          defaultValue: null,
        })
        .addSelect({
          path: 'displayMode',
          name: 'Cell display mode',
          description: 'Color text, background, show as gauge, etc',
          settings: {
            options: [
              { value: TableCellDisplayMode.Auto, label: 'Auto' },
              { value: TableCellDisplayMode.ColorText, label: 'Color text' },
              { value: TableCellDisplayMode.ColorBackground, label: 'Color background' },
              { value: TableCellDisplayMode.GradientGauge, label: 'Gradient gauge' },
              { value: TableCellDisplayMode.LcdGauge, label: 'LCD gauge' },
              { value: TableCellDisplayMode.BasicGauge, label: 'Basic gauge' },
              { value: TableCellDisplayMode.JSONView, label: 'JSON View' },
            ],
          },
        });
    },
  })
  .setPanelOptions(builder => {
    return builder
      .addCustomEditor({
        id: 'rootCauseListCompleteness',
        path: 'rootCauseListCompleteness',
        name: '',
        description: '',
        category: ['Rootcauses Lists'],
        defaultValue: SELECTOPTIONS['COMPLETENESS']['rootCause']['options'],
        editor: RootCauseEditor,
        settings: {
          impact: 'Completeness',
        },
      })
  });
