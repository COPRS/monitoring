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

import React, { useState, useContext } from 'react';
import { Button, ConfirmModal } from '@grafana/ui';
import { MutableDataFrame } from '@grafana/data';
import { PanelContext } from 'SimplePanel';
import { refresh, removeOrphanedInvalidation, removeOrphanedInvalidationCompleteness, unlinkInvalidations } from './utils';
import { QueryContext } from './types';

export const DeleteInvalidationButton = ({
  disabled,
  selectedRows,
  table,
  toggleAllRowsSelected,
}: {
  disabled: boolean;
  selectedRows: MutableDataFrame;
  table: string;
  toggleAllRowsSelected: (value?: boolean | undefined) => void;
}) => {
  const { dataSource, timeRange } = useContext(PanelContext);
  const queryContext = { dataSource, timeRange };
  const [deleteModaleIsVisible, setDeleteModaleIsVisible] = useState(false);
  const onDeleteClicked = (e: React.MouseEvent<HTMLButtonElement, MouseEvent>) => {
    e.preventDefault();
    setDeleteModaleIsVisible(true);
  };
  const inval_ids = selectedRows.fields.find(f => f.name === 'inval_id')?.values.toArray();

  return (
    <>
      <Button onClick={onDeleteClicked} disabled={disabled} icon="trash-alt">
        Delete
      </Button>
      <ConfirmModal
        isOpen={deleteModaleIsVisible}
        title="Delete invalidation"
        body={
          <p>
            {`Are you sure you want to delete this invalidation?`}
            <br />
            {`It will affect any other product linked to it!`}
          </p>
        }
        confirmText="Delete"
        icon="exclamation-triangle"
        onConfirm={() => {
          Promise.all(
            inval_ids && inval_ids.map(async (id: any) => {
              await getProductsLinkedToInvalidation(queryContext, id, table).then(async data => {

                await unlinkInvalidations(queryContext, data?.data[0]?.fields.find((f: any) => f.name === 'id')?.values?.buffer)
              })
              return id
            }
            ) || []
          )
            .then(r => removeOrphanedInvalidationCompleteness(queryContext, table))
            .then(r => removeOrphanedInvalidation(queryContext, table))
            .then(r => toggleAllRowsSelected(false))
            .then(r => setDeleteModaleIsVisible(false))
            .then(refresh);
        }}
        onDismiss={() => setDeleteModaleIsVisible(false)}
      />
    </>
  );
};
const getProductsLinkedToInvalidation = async (queryContext: QueryContext, inval_id: number, table: string) => {
  const rawSql = `SELECT * FROM missing_products as mp INNER JOIN invalidation_completeness ON mp.id = ANY(missing_products_ids) WHERE parent_id=${inval_id}`;
  const { dataSource, timeRange } = queryContext;
  return dataSource.query(rawSql, timeRange);
};


