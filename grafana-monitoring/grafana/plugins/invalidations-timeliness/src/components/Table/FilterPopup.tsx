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

import React, { FC, useCallback, useMemo, useState } from 'react';
import { Field, GrafanaTheme, SelectableValue } from '@grafana/data';
import { css, cx } from 'emotion';
import { TableStyles } from './styles';
import { FilterList } from './FilterList';
import { calculateUniqueFieldValues, getFilteredOptions, valuesToOptions } from './utils';
import {
  Button,
  HorizontalGroup,
  Label,
  stylesFactory,
  useStyles,
  VerticalGroup,
} from '@grafana/ui';

interface Props {
  column: any;
  tableStyles: TableStyles;
  onClose: () => void;
  field?: Field;
}

export const FilterPopup: FC<Props> = ({ column: { preFilteredRows, filterValue, setFilter }, onClose, field }) => {
  const uniqueValues = useMemo(() => calculateUniqueFieldValues(preFilteredRows, field), [preFilteredRows, field]);
  const options = useMemo(() => valuesToOptions(uniqueValues), [uniqueValues]);
  const filteredOptions = useMemo(() => getFilteredOptions(options, filterValue), [options, filterValue]);
  const [values, setValues] = useState<SelectableValue[]>(filteredOptions);

  const onCancel = useCallback((event?: React.MouseEvent) => onClose(), [onClose]);

  const onFilter = useCallback(
    (event: React.MouseEvent) => {
      const filtered = values.length ? values : undefined;

      setFilter(filtered);
      onClose();
    },
    [setFilter, values, onClose]
  );

  const onClearFilter = useCallback(
    (event: React.MouseEvent) => {
      setFilter(undefined);
      onClose();
    },
    [setFilter, onClose]
  );

  const clearFilterVisible = useMemo(() => filterValue !== undefined, [filterValue]);
  const styles: any = useStyles(getStyles);

  return (

    <div className={cx(styles.filterContainer)} onClick={stopPropagation}>
      <VerticalGroup spacing="lg">
        <VerticalGroup spacing="xs">
          <Label>Filter by values:</Label>
          <div className={cx(styles.listDivider)} />
          <FilterList onChange={setValues} values={values} options={options} />
        </VerticalGroup>
        <HorizontalGroup spacing="lg">
          <HorizontalGroup>
            <Button size="sm" onClick={onFilter}>
              Ok
            </Button>
            <Button size="sm" variant="secondary" onClick={onCancel}>
              Cancel
            </Button>
          </HorizontalGroup>
          {clearFilterVisible && (
            <HorizontalGroup>
              <Button size="sm" onClick={onClearFilter}>
                Clear filter
              </Button>
            </HorizontalGroup>
          )}
        </HorizontalGroup>
      </VerticalGroup>
    </div>

  );
};

const getStyles: any = stylesFactory((theme: GrafanaTheme) => ({
  filterContainer: css`
    label: filterContainer;
    width: 100%;
    min-width: 250px;
    height: 100%;
    max-height: 400px;
    background-color: ${theme.colors.bg1};
    border: ${theme.border.width.sm} solid ${theme.colors.border2};
    padding: ${theme.spacing.md};
    margin: ${theme.spacing.sm} 0;
    box-shadow: 0px 0px 20px ${theme.colors.dropdownShadow};
    border-radius: ${theme.spacing.xs};
  `,
  listDivider: css`
    label: listDivider;
    width: 100%;
    border-top: ${theme.border.width.sm} solid ${theme.colors.border2};
    padding: ${theme.spacing.xs} ${theme.spacing.md};
  `,
}));

const stopPropagation = (event: React.MouseEvent) => {
  event.stopPropagation();
};
