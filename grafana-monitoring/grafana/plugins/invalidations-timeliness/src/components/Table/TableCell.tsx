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
import { Cell } from 'react-table';
import { Field } from '@grafana/data';

import { getTextAlign } from './utils';
import { TableFilterActionCallback } from './types';
import { TableStyles } from './styles';
import { FilterableTableCell } from './FilterableTableCell';

export interface Props {
  cell: Cell;
  field: Field;
  tableStyles: TableStyles;
  onCellFilterAdded?: TableFilterActionCallback;
}

export const TableCell: FC<Props> = ({ cell, field, tableStyles, onCellFilterAdded }) => {
  const filterable = field.config.filterable;
  const cellProps = cell.getCellProps();

  if (field.config.custom.selector) {
    return (
      <div {...cellProps} className={tableStyles.tableCellSelectorWrapper}>
        {renderCell(cell, field, tableStyles)}
      </div>
    );
  }

  if (cellProps.style) {
    cellProps.style.textAlign = getTextAlign(field);
  }

  if (filterable && onCellFilterAdded) {
    return (
      <FilterableTableCell
        cell={cell}
        field={field}
        tableStyles={tableStyles}
        onCellFilterAdded={onCellFilterAdded}
        cellProps={cellProps}
      />
    );
  }

  return (
    <div {...cellProps} className={tableStyles.tableCellWrapper}>
      {renderCell(cell, field, tableStyles)}
    </div>
  );
};

export const renderCell = (cell: Cell, field: Field, tableStyles: TableStyles) =>
  cell.render('Cell', { field, tableStyles });
