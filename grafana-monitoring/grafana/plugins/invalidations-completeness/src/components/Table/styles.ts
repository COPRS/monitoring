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

import { css } from 'emotion';
import { GrafanaTheme } from '@grafana/data';
import { getScrollbarWidth, styleMixins, stylesFactory } from '@grafana/ui';

export interface TableStyles {
  cellHeight: number;
  cellHeightInner: number;
  cellPadding: number;
  rowHeight: number;
  table: string;
  thead: string;
  headerCell: string;
  headerCellLabel: string;
  headerFilter: string;
  tableCell: string;
  tableCellWrapper: string;
  tableCellSelectorWrapper: string;
  tableCellLink: string;
  row: string;
  theme: GrafanaTheme;
  resizeHandle: string;
  overflow: string;
}

export const getTableStyles = stylesFactory(
  (theme: GrafanaTheme): TableStyles => {
    const { palette, colors } = theme;
    const headerBg = theme.colors.bg2;
    const borderColor = theme.colors.border1;
    const resizerColor = theme.isLight ? palette.blue95 : palette.blue77;
    const padding = 6;
    const lineHeight = theme.typography.lineHeight.md;
    const bodyFontSize = 14;
    const cellHeight = padding * 2 + bodyFontSize * lineHeight;
    const rowHoverBg = styleMixins.hoverColor(theme.colors.bg1, theme as any);
    const scollbarWidth = getScrollbarWidth();

    return {
      theme,
      cellHeight,
      cellPadding: padding,
      cellHeightInner: bodyFontSize * lineHeight,
      rowHeight: cellHeight + 2,
      table: css`
        height: 100%;
        width: 100%;
        overflow: auto;
        display: flex;
      `,
      thead: css`
        label: thead;
        height: ${cellHeight}px;
        overflow-y: auto;
        overflow-x: hidden;
        background: ${headerBg};
        position: relative;
      `,
      headerCell: css`
        padding: ${padding}px;
        overflow: hidden;
        white-space: nowrap;
        color: ${colors.textBlue};
        border-right: 1px solid ${theme.colors.panelBg};
        height: 100%;
        display: flex;

        &:last-child {
          border-right: none;
        }
      `,
      headerCellLabel: css`
        cursor: pointer;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        display: flex;
        margin-right: ${theme.spacing.xs};
      `,
      headerFilter: css`
        label: headerFilter;
        cursor: pointer;
      `,
      row: css`
        label: row;
        border-bottom: 1px solid ${borderColor};

        &:hover {
          background-color: ${rowHoverBg};
        }
      `,
      tableCellWrapper: css`
        border-right: 1px solid ${borderColor};
        display: inline-flex;
        align-items: center;
        height: 100%;

        &:last-child {
          border-right: none;

          > div {
            padding-right: ${scollbarWidth + padding}px;
          }
        }
      `,
      tableCellSelectorWrapper: css`
        border-right: 1px solid ${borderColor};
        display: inline-flex;
        justify-content: center;
        margin-top: 8px;
        height: 100%;

        &:last-child {
          border-right: none;

          > div {
            padding-right: ${scollbarWidth + padding}px;
          }
        }
      `,
      tableCellLink: css`
        text-decoration: underline;
      `,
      tableCell: css`
        padding: ${padding}px;
        text-overflow: ellipsis;
        white-space: nowrap;
        overflow: hidden;
        flex: 1;
      `,
      overflow: css`
        overflow: hidden;
        text-overflow: ellipsis;
      `,
      resizeHandle: css`
        label: resizeHandle;
        cursor: col-resize !important;
        display: inline-block;
        background: ${resizerColor};
        opacity: 0;
        transition: opacity 0.2s ease-in-out;
        width: 8px;
        height: 100%;
        position: absolute;
        right: -4px;
        border-radius: 3px;
        top: 0;
        z-index: ${theme.zIndex.dropdown};
        touch-action: none;

        &:hover {
          opacity: 1;
        }
      `,
    };
  }
);
