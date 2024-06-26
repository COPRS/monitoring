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

import { SelectableValue } from '@grafana/data';

export const Responsibility = {
  E2E: 'E2E',
  PDGS: 'PDGS',
} as const;
export type EResponsibility = typeof Responsibility[keyof typeof Responsibility];

export interface FormDTO {
  responsibility: SelectableValue<EResponsibility>;
  rootCause: SelectableValue<string>;
  label: string;
  comment: string;
  anomalyIdentifier: number
}

export interface SelectOptionsInterface {
  [key: string]: {
    [key: string]: {
      disabled: boolean;
      value: string | undefined;
      options: Array<SelectableValue<string>>;
    };
  };
}

export interface SelectCategory {
  [key: string]: {
    disabled: boolean;
    value: string | undefined;
    options: Array<SelectableValue<string>>;
  };
}
