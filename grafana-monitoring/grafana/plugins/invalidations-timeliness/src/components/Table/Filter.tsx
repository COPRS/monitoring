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

import React, { FC, useCallback, useMemo, useRef, useState } from 'react';
import { css, cx } from 'emotion';
import { Field, GrafanaTheme } from '@grafana/data';

import { TableStyles } from './styles';
import { FilterPopup } from './FilterPopup';
import { Icon, Popover, stylesFactory, useStyles } from '@grafana/ui';

interface Props {
  column: any;
  tableStyles: TableStyles;
  field?: Field;
}

export const Filter: FC<Props> = ({ column, field, tableStyles }) => {
  const ref = useRef<HTMLDivElement>(null);
  const [isPopoverVisible, setPopoverVisible] = useState<boolean>(false);
  const styles = useStyles(getStyles);
  const filterEnabled = useMemo(() => Boolean(column.filterValue), [column.filterValue]);
  const onShowPopover = useCallback(() => setPopoverVisible(true), [setPopoverVisible]);
  const onClosePopover = useCallback(() => setPopoverVisible(false), [setPopoverVisible]);

  if (!field || !field.config.custom?.filterable) {
    return null;
  }

  return (
    <span
      className={cx(tableStyles.headerFilter, filterEnabled ? styles.filterIconEnabled : styles.filterIconDisabled)}
      ref={ref}
      onClick={onShowPopover}
    >
      <Icon name="filter" />
      {isPopoverVisible && ref.current && (
        <Popover
          content={<FilterPopup column={column} tableStyles={tableStyles} field={field} onClose={onClosePopover} />}
          placement="bottom-start"
          referenceElement={ref.current}
          show
        />
      )}
    </span>
  );
};

const getStyles = stylesFactory((theme: GrafanaTheme) => ({
  filterIconEnabled: css`
    label: filterIconEnabled;
    color: ${theme.colors.textBlue};
  `,
  filterIconDisabled: css`
    label: filterIconDisabled;
    color: ${theme.colors.textFaint};
  `,
}));
