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

import React, { FC, useState, useContext } from 'react';
import { Button, ConfirmModal } from '@grafana/ui';
import { MutableDataFrame } from '@grafana/data';
import { unlinkInvalidations, refresh, removeOrphanedInvalidation, removeOrphanedInvalidationCompleteness } from './utils';
import { PanelContext } from 'SimplePanel';


interface Props {
  disabled: boolean;
  selectedRows: MutableDataFrame;
  table: string;
  toggleAllRowsSelected: (value?: boolean | undefined) => void;
}

export const UnlinkInvalidationButton: FC<Props> = ({ disabled, selectedRows, table, toggleAllRowsSelected }) => {
  const { dataSource, timeRange } = useContext(PanelContext);
  const [unlinkModaleIsVisible, setUnlinkModaleIsVisible] = useState(false);
  const onUnlinkClicked = (e: React.MouseEvent<HTMLButtonElement, MouseEvent>) => {
    e.preventDefault();
    setUnlinkModaleIsVisible(true);
  };
  const productIds = selectedRows.fields.find(f => f.name === 'id')?.values.toArray() || [];
  const queryContext = { dataSource, timeRange };
  return (
    <>
      <Button onClick={onUnlinkClicked} disabled={disabled} icon="link">
        Unlink
      </Button>
      <ConfirmModal
        isOpen={unlinkModaleIsVisible}
        title="Unlink invalidation"
        body={
          <p>
            {`Are you sure you want to unlink the invalidation from this Product?`}
            <br />
            {`If no left Product is linked to this invalidation, the latter will be deleted from the database.`}
          </p>
        }
        confirmText="Unlink"
        icon="exclamation-triangle"
        onConfirm={() =>
          unlinkInvalidations(queryContext, productIds)
            .then(r => removeOrphanedInvalidationCompleteness(queryContext, 'invalidation_completeness'))
            .then(r => removeOrphanedInvalidation(queryContext, table))
            .then(r => toggleAllRowsSelected(false))
            .then(r => setUnlinkModaleIsVisible(false))
            .then(refresh)
        }
        onDismiss={() => setUnlinkModaleIsVisible(false)}
      />
    </>
  );
};
