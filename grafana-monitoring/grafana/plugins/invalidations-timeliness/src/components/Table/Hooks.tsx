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

import React from 'react';
import { CellProps, HeaderProps, Hooks } from 'react-table';
import { Checkbox } from '@grafana/ui';
import { SELECTION_COLUMN_WIDTH } from './SelectorColumn';

export const selectionHook = (hooks: Hooks<any>) => {
  hooks.allColumns.push(columns => [
    // Let's make a column for selection
    {
      id: '_selector',
      disableResizing: true,
      disableGroupBy: true,
      minWidth: SELECTION_COLUMN_WIDTH,
      width: SELECTION_COLUMN_WIDTH,
      maxWidth: SELECTION_COLUMN_WIDTH,
      // The header can use the table's getToggleAllRowsSelectedProps method
      // to render a checkbox
      Header: ({ getToggleAllRowsSelectedProps }: HeaderProps<any>) => (
        <Checkbox {...getToggleAllRowsSelectedProps() as any} />
      ),
      // The cell can use the individual row's getToggleRowSelectedProps method
      // to the render a checkbox
      Cell: ({ row }: CellProps<any>) => <Checkbox {...row.getToggleRowSelectedProps() as any} />,
    },
    ...columns,
  ]);
};
