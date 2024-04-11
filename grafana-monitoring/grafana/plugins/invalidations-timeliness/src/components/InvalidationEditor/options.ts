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

import { Responsibility, SelectOptionsInterface } from './types';

export const CATEGORYOPTIONS = [
  {
    label: Responsibility.PDGS,
    value: Responsibility.PDGS,
    description: 'Invalidation due to sensing root cause',
    imgUrl: 'public/plugins/cs-group-invalidations-timeliness/img/orbit.svg',
  },
  {
    label: Responsibility.E2E,
    value: Responsibility.E2E,
    description: 'Invalidation due to antenna root cause',
    imgUrl: 'public/plugins/cs-group-invalidations-timeliness/img/parabolic-dishes.svg',
  },
];

export const IMPACTOPTIONS = [
  {
    label: 'Completeness',
    value: 'COMPLETENESS',
    description: 'Invalidation has implication on completeness',
    imgUrl: 'public/plugins/cs-group-invalidations-timeliness/img/check.svg',
  },
  {
    label: 'Timeliness',
    value: 'TIMELINESS',
    description: 'Invalidation has implication on timeliness',
    imgUrl: 'public/plugins/cs-group-invalidations-timeliness/img/on-time.svg',
  },
];

export const TIMELINESSOPTIONS = [
  {
    label: 'MPS',
    value: 'MPS',
    description: 'MPS - network issue',
    imgUrl: 'public/plugins/cs-group-invalidations-timeliness/img/orbit.svg',
  },
  {
    label: 'SGS',
    value: 'SGS',
    description: 'SGS - network issue',
    imgUrl: 'public/plugins/cs-group-invalidations-timeliness/img/settings.svg',
  },
  {
    label: 'MTI',
    value: 'MTI',
    description: 'MTI - network issue',
    imgUrl: 'public/plugins/cs-group-invalidations-timeliness/img/settings.svg',
  },
  {
    label: 'NSG',
    value: 'NSG',
    description: 'NSG - network issue',
    imgUrl: 'public/plugins/cs-group-invalidations-timeliness/img/settings.svg',
  },
  {
    label: 'EDRS-A',
    value: 'EDRS-A',
    description: 'EDRS-A - network issue',
    imgUrl: 'public/plugins/cs-group-invalidations-timeliness/img/settings.svg',
  },
  {
    label: 'PDGS',
    value: 'PDGS',
    description: 'PDGS - network issue',
    imgUrl: 'public/plugins/cs-group-invalidations-timeliness/img/question.svg',
  },
  {
    label: 'PDGS',
    value: 'PDGS',
    description: 'PDGS - other',
    imgUrl: 'public/plugins/cs-group-invalidations-timeliness/img/question.svg',
  },
]
export const SELECTOPTIONS: SelectOptionsInterface = {

  TIMELINESS: {
    rootCause: {
      disabled: false,
      value: '',
      options: [
        {
          label: 'MPS',
          value: 'MPS',
          description: 'MPS - network issue',
          imgUrl: 'public/plugins/cs-group-invalidations-timeliness/img/orbit.svg',
        },
        {
          label: 'SGS',
          value: 'SGS',
          description: 'SGS - network issue',
          imgUrl: 'public/plugins/cs-group-invalidations-timeliness/img/settings.svg',
        },
        {
          label: 'MTI',
          value: 'MTI',
          description: 'MTI - network issue',
          imgUrl: 'public/plugins/cs-group-invalidations-timeliness/img/settings.svg',
        },
        {
          label: 'NSG',
          value: 'NSG',
          description: 'NSG - network issue',
          imgUrl: 'public/plugins/cs-group-invalidations-timeliness/img/settings.svg',
        },
        {
          label: 'EDRS-A',
          value: 'EDRS-A',
          description: 'EDRS-A - network issue',
          imgUrl: 'public/plugins/cs-group-invalidations-timeliness/img/settings.svg',
        },
        {
          label: 'PDGS',
          value: 'PDGS',
          description: 'PDGS - network issue',
          imgUrl: 'public/plugins/cs-group-invalidations-timeliness/img/question.svg',
        },
        {
          label: 'PDGS',
          value: 'PDGS',
          description: 'PDGS - other',
          imgUrl: 'public/plugins/cs-group-invalidations-timeliness/img/question.svg',
        },
      ],
    },
  },
};
