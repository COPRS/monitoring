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

import React, { FC } from 'react';
import { ThresholdsConfig, ThresholdsMode, VizOrientation } from '@grafana/data';
import { TableCellProps, TableCellDisplayMode } from './types';
import { BarGauge, BarGaugeDisplayMode } from '@grafana/ui';

const defaultScale: ThresholdsConfig = {
  mode: ThresholdsMode.Absolute,
  steps: [
    {
      color: 'blue',
      value: -Infinity,
    },
    {
      color: 'green',
      value: 20,
    },
  ],
};

export const BarGaugeCell: FC<TableCellProps> = props => {
  const { field, column, tableStyles, cell } = props;

  if (!field.display) {
    return null;
  }

  let { config } = field;
  if (!config.thresholds) {
    config = {
      ...config,
      thresholds: defaultScale,
    };
  }

  const displayValue = field.display(cell.value);
  let barGaugeMode = BarGaugeDisplayMode.Gradient;

  if (field.config.custom && field.config.custom.displayMode === TableCellDisplayMode.LcdGauge) {
    barGaugeMode = BarGaugeDisplayMode.Lcd;
  } else if (field.config.custom && field.config.custom.displayMode === TableCellDisplayMode.BasicGauge) {
    barGaugeMode = BarGaugeDisplayMode.Basic;
  }

  let width;
  if (column.width) {
    width = (column.width as number) - tableStyles.cellPadding * 2;
  } else {
    width = tableStyles.cellPadding * 2;
  }

  return (
    <div className={tableStyles.tableCell}>
      <BarGauge
        width={width}
        height={tableStyles.cellHeightInner}
        field={config}
        display={field.display}
        value={displayValue}
        orientation={VizOrientation.Horizontal}
        theme={tableStyles.theme as any}
        itemSpacing={1}
        lcdCellWidth={8}
        displayMode={barGaugeMode}
      />
    </div>
  );
};
