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

import React, { FC, memo, useCallback, useEffect, useMemo } from 'react';
import { DataFrame, Field, getFieldDisplayName } from '@grafana/data';
import {
  Cell,
  Column,
  HeaderGroup,
  useAbsoluteLayout,
  useFilters,
  UseFiltersState,
  useResizeColumns,
  UseResizeColumnsState,
  useSortBy,
  UseSortByState,
  useRowSelect,
  UseRowSelectState,
  useTable,
} from 'react-table';
import { FixedSizeList } from 'react-window';
import { getColumns, getHeaderAlign, } from './utils';
import {
  TableColumnResizeActionCallback,
  TableFilterActionCallback,
  TableSortByActionCallback,
  TableSortByFieldState,
} from './types';
import { getTableStyles, TableStyles } from './styles';
import { TableCell } from './TableCell';
import { Filter } from './Filter';
import { Icon, useTheme } from '@grafana/ui';
import _ from 'lodash';
import { getSelectorField } from './SelectorColumn';

const COLUMN_MIN_WIDTH = 150;

export interface Props {
  ariaLabel?: string;
  data: DataFrame;
  width: number;
  height: number;
  /** Minimal column width specified in pixels */
  columnMinWidth?: number;
  noHeader?: boolean;
  resizable?: boolean;
  initialSortBy?: TableSortByFieldState[];
  onColumnResize?: TableColumnResizeActionCallback;
  onSortByChange?: TableSortByActionCallback;
  onCellFilterAdded?: TableFilterActionCallback;
  selectableRows?: boolean;
  selectedRowIds?: Record<string, boolean>;
  onSelectionRowIds?: any;
  toggleAllRowsSelectedCallback: (func: (value?: boolean | undefined) => void) => void;
}

interface ReactTableInternalState
  extends UseResizeColumnsState<{}>,
  UseSortByState<{}>,
  UseFiltersState<{}>,
  UseRowSelectState<{}> { }

function useTableStateReducer(props: Props) {
  const { data, onColumnResize, onSortByChange } = props;
  return useCallback(
    (newState: ReactTableInternalState, action: any) => {
      switch (action.type) {
        case 'columnDoneResizing':
          if (onColumnResize) {
            const info = (newState.columnResizing.headerIdWidths as any)[0];
            const columnIdString = info[0];
            const fieldIndex = parseInt(columnIdString, 10);
            const width = Math.round(newState.columnResizing.columnWidths[columnIdString] as number);

            const field = data.fields[fieldIndex];
            if (!field) {
              return newState;
            }

            const fieldDisplayName = getFieldDisplayName(field, data);
            onColumnResize(fieldDisplayName, width);
          }
        case 'toggleSortBy':
          if (onSortByChange) {
            const sortByFields: TableSortByFieldState[] = [];

            for (const sortItem of newState.sortBy) {
              const id = parseInt(sortItem.id, 10);
              // if selectableRows we have a additional column which is not in data.fields
              const correctedId = props.selectableRows ? id - 1 : id;
              const field = data.fields[correctedId];
              if (!field) {
                continue;
              }

              sortByFields.push({
                displayName: getFieldDisplayName(field, data),
                desc: sortItem.desc,
              });
            }

            onSortByChange(sortByFields);
          }
          break;
      }

      return newState;
    },
    [onColumnResize, onSortByChange, data, props.selectableRows]
  );
}

function getInitialState(props: Props, columns: Column[]): Partial<ReactTableInternalState> {
  const state: Partial<ReactTableInternalState> = {};

  if (props.initialSortBy) {
    state.sortBy = [];

    for (const sortBy of props.initialSortBy) {
      for (const col of columns) {
        if (col.Header === sortBy.displayName) {
          state.sortBy.push({ id: col.id as string, desc: sortBy.desc });
        }
      }
    }
  }

  if (props.selectableRows && props.selectedRowIds) {
    state.selectedRowIds = props.selectedRowIds;
  }

  return state;
}

export const Table: FC<Props> = memo((props: Props) => {
  const {
    ariaLabel,
    data,
    height,
    onCellFilterAdded,
    width,
    columnMinWidth = COLUMN_MIN_WIDTH,
    noHeader,
    resizable = true,
    selectableRows = false,
    onSelectionRowIds,
    toggleAllRowsSelectedCallback,
  } = props;
  const theme: any = useTheme();
  const tableStyles = getTableStyles(theme);

  // React table data array. This data acts just like a dummy array to let react-table know how many rows exist
  // The cells use the field to look up values
  const memoizedData = useMemo(() => {
    if (!data.fields.length) {
      return [];
    }
    // as we only use this to fake the length of our data set for react-table we need to make sure we always return an array
    // filled with values at each index otherwise we'll end up trying to call accessRow for null|undefined value in
    // https://github.com/tannerlinsley/react-table/blob/7be2fc9d8b5e223fc998af88865ae86a88792fdb/src/hooks/useTable.js#L585
    return Array(data.length).fill(0);
  }, [data]);

  // React-table column definitions
  const memoizedColumns = useMemo(() => getColumns(data, width, columnMinWidth, selectableRows), [
    data,
    width,
    columnMinWidth,
    selectableRows,
  ]);

  // Internal react table state reducer
  const stateReducer = useTableStateReducer(props);

  const options: any = useMemo(
    () => ({
      columns: memoizedColumns,
      data: memoizedData,
      disableResizing: !resizable,
      stateReducer: stateReducer,
      initialState: getInitialState(props, memoizedColumns),
    }),
    [memoizedColumns, memoizedData, stateReducer, resizable, props]
  );

  const {
    getTableProps,
    headerGroups,
    rows,
    prepareRow,
    totalColumnsWidth,
    // TODO make it dependent of selectableRows
    state: { selectedRowIds },
    toggleAllRowsSelected,
  } = useTable(options, useFilters, useSortBy, useAbsoluteLayout, useResizeColumns, useRowSelect);

  useEffect(() => {
    toggleAllRowsSelectedCallback(() => toggleAllRowsSelected);
  }, [toggleAllRowsSelectedCallback, toggleAllRowsSelected]);

  const RenderRow = React.useCallback(
    ({ index, style }: any) => {
      const row = rows[index];
      prepareRow(row);
      const fields = [getSelectorField(data.length), ...data.fields];
      return (
        <div {...row.getRowProps({ style })} className={tableStyles.row}>
          {row.cells.map((cell: Cell, index: number) => (
            <TableCell
              key={index}
              field={fields[index]}
              tableStyles={tableStyles}
              cell={cell}
              onCellFilterAdded={onCellFilterAdded}
            />
          ))}
        </div>
      );
    },
    [prepareRow, rows, onCellFilterAdded, tableStyles, data.fields, data.length]
  );

  const headerHeight = noHeader ? 0 : tableStyles.cellHeight;

  useEffect(() => {
    onSelectionRowIds?.(selectedRowIds);
  }, [selectedRowIds, onSelectionRowIds]);

  return (
    <div {...getTableProps()} className={tableStyles.table} aria-label={ariaLabel}>
      {/* <CustomScrollbar hideVerticalTrack={true}> */}
      <div style={{ width: `${totalColumnsWidth}px` }}>
        {!noHeader && (
          <div>
            {headerGroups.map((headerGroup: HeaderGroup) => {
              return (
                <div key={headerGroup.id}>
                  <div className={tableStyles.thead} {...headerGroup.getHeaderGroupProps()}>
                    {headerGroup.headers.map((column: Column, index: number) =>
                      <div key={column.id}>
                        {renderHeaderCell(column, tableStyles, data.fields[index])}
                      </div>
                    )}
                  </div></div>
              );
            })}
          </div>
        )}
        <FixedSizeList
          height={height - headerHeight}
          itemCount={rows.length}
          itemSize={tableStyles.rowHeight}
          width={'100%'}
          style={{ overflow: 'hidden auto' }}
        >
          {RenderRow}
        </FixedSizeList>
      </div>
      {/* </CustomScrollbar> */}
    </div>
  );
});

Table.displayName = 'Table';

function renderHeaderCell(column: any, tableStyles: TableStyles, field?: Field) {
  const headerProps = column.getHeaderProps();

  if (column.canResize) {
    headerProps.style.userSelect = column.isResizing ? 'none' : 'auto'; // disables selecting text while resizing
  }

  headerProps.style.position = 'absolute';
  headerProps.style.justifyContent = getHeaderAlign(field);

  return (
    <div className={tableStyles.headerCell} {...headerProps}>
      {column.canSort && (
        <>
          <div
            {...column.getSortByToggleProps()}
            className={tableStyles.headerCellLabel}
            title={column.render('Header')}
          >
            <div>{column.render('Header')}</div>
            <div>
              {column.isSorted && (column.isSortedDesc ? <Icon name="arrow-down" /> : <Icon name="arrow-up" />)}
            </div>
          </div>
          {column.canFilter && <Filter column={column} tableStyles={tableStyles} field={field} />}
        </>
      )}
      {!column.canSort && column.render('Header')}
      {!column.canSort && column.canFilter && <Filter column={column} tableStyles={tableStyles} field={field} />}
      {column.canResize && <div {...column.getResizerProps()} className={tableStyles.resizeHandle} />}
    </div>
  );
}
