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

import React, { FC, useCallback, useState } from 'react';
import { TableCellProps } from 'react-table';
import { GrafanaTheme } from '@grafana/data';
import { css } from 'emotion';

import { FILTER_FOR_OPERATOR, FILTER_OUT_OPERATOR, TableFilterActionCallback } from './types';
import { Props, renderCell } from './TableCell';
import { Icon, stylesFactory, Tooltip, useTheme } from '@grafana/ui';

interface FilterableTableCellProps extends Pick<Props, 'cell' | 'field' | 'tableStyles'> {
  onCellFilterAdded: TableFilterActionCallback;
  cellProps: TableCellProps;
}

export const FilterableTableCell: FC<FilterableTableCellProps> = ({
  cell,
  field,
  tableStyles,
  onCellFilterAdded,
  cellProps,
}) => {
  const [showFilters, setShowFilter] = useState(false);
  const onMouseOver = useCallback((event: React.MouseEvent<HTMLDivElement>) => setShowFilter(true), [setShowFilter]);
  const onMouseLeave = useCallback((event: React.MouseEvent<HTMLDivElement>) => setShowFilter(false), [setShowFilter]);
  const onFilterFor = useCallback(
    (event: React.MouseEvent<HTMLDivElement>) =>
      onCellFilterAdded({ key: field.name, operator: FILTER_FOR_OPERATOR, value: cell.value }),
    [cell, field, onCellFilterAdded]
  );
  const onFilterOut = useCallback(
    (event: React.MouseEvent<HTMLDivElement>) =>
      onCellFilterAdded({ key: field.name, operator: FILTER_OUT_OPERATOR, value: cell.value }),
    [cell, field, onCellFilterAdded]
  );
  const theme = useTheme();
  const styles = getFilterableTableCellStyles(theme);

  return (
    <div {...cellProps} className={tableStyles.tableCellWrapper} onMouseOver={onMouseOver} onMouseLeave={onMouseLeave}>
      {renderCell(cell, field, tableStyles)}
      {showFilters && cell.value && (
        <div className={styles.filterWrapper}>
          <div className={styles.filterItem}>
            <Tooltip content="Filter for value" placement="top">
              <Icon name={'search-plus'} onClick={onFilterFor} />
            </Tooltip>
          </div>
          <div className={styles.filterItem}>
            <Tooltip content="Filter out value" placement="top">
              <Icon name={'search-minus'} onClick={onFilterOut} />
            </Tooltip>
          </div>
        </div>
      )}
    </div>
  );
};

const getFilterableTableCellStyles = stylesFactory((theme: GrafanaTheme) => ({
  filterWrapper: css`
    label: filterWrapper;
    display: inline-flex;
    justify-content: space-around;
    cursor: pointer;
  `,
  filterItem: css`
    label: filterItem;
    color: ${theme.colors.textSemiWeak};
    padding: 0 ${theme.spacing.xxs};
  `,
}));
