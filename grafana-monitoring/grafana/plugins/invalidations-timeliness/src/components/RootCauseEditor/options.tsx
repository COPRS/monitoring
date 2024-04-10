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

import { SELECTOPTIONS } from 'components/InvalidationEditor/options';
import { SelectCategory } from 'components/InvalidationEditor/types';
import _ from 'lodash';
import { PanelOptions } from 'types';
import { RootCauseEditorOptions } from './types';

export const DEFAULTVALUEROOTCAUSE: RootCauseEditorOptions[] = [];
export const getSelectOptions = (options: PanelOptions): SelectCategory => {
  const selectOptions = _.cloneDeep(SELECTOPTIONS);
  if (
    options?.rootCauseListTimeliness !== undefined &&
    options['rootCauseListTimeliness'].length > 0
  ) {
    selectOptions["TIMELINESS"]['rootCause']['options'] = options['rootCauseListTimeliness'];
    selectOptions["TIMELINESS"]['rootCause']['value'] = options['rootCauseListTimeliness'][0].value;
  } else {
    selectOptions["TIMELINESS"]['rootCause']['options'] = [];
    selectOptions["TIMELINESS"]['rootCause']['value'] = ' ';
  }
  return selectOptions["TIMELINESS"];
};
export const SELECTOPTIONSICONS = [
  {
    label: 'Check',
    imgUrl: 'public/plugins/cs-group-invalidations-timeliness/img/check.svg',
    value: 'public/plugins/cs-group-invalidations-timeliness/img/check.svg',
  },
  {
    label: 'Logo',
    imgUrl: 'public/plugins/cs-group-invalidations-timeliness/img/logo.svg',
    value: 'public/plugins/cs-group-invalidations-timeliness/img/logo.svg',
  },
  {
    label: 'On-Time',
    imgUrl: 'public/plugins/cs-group-invalidations-timeliness/img/on-time.svg',
    value: 'public/plugins/cs-group-invalidations-timeliness/img/on-time.svg',
  },
  {
    label: 'Orbit',
    imgUrl: 'public/plugins/cs-group-invalidations-timeliness/img/orbit.svg',
    value: 'public/plugins/cs-group-invalidations-timeliness/img/orbit.svg',
  },
  {
    label: 'Parabolic-Dishes',
    imgUrl: 'public/plugins/cs-group-invalidations-timeliness/img/parabolic-dishes.svg',
    value: 'public/plugins/cs-group-invalidations-timeliness/img/parabolic-dishes.svg',
  },
  {
    label: 'Question',
    imgUrl: 'public/plugins/cs-group-invalidations-timeliness/img/question.svg',
    value: 'public/plugins/cs-group-invalidations-timeliness/img/question.svg',
  },
  {
    label: 'Satellite',
    imgUrl: 'public/plugins/cs-group-invalidations-timeliness/img/satellite.svg',
    value: 'public/plugins/cs-group-invalidations-timeliness/img/satellite.svg',
  },
  {
    label: 'Settings',
    imgUrl: 'public/plugins/cs-group-invalidations-timeliness/img/settings.svg',
    value: 'public/plugins/cs-group-invalidations-timeliness/img/settings.svg',
  },
];
