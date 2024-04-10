/*
*Copyright (c) Alibaba Group;
*Licensed under the Apache License, Version 2.0 (the "License");
*you may not use this file except in compliance with the License.
*You may obtain a copy of the License at

*   http://www.apache.org/licenses/LICENSE-2.0

*Unless required by applicable law or agreed to in writing, software
*distributed under the License is distributed on an "AS IS" BASIS,
*WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*See the License for the specific language governing permissions and
*limitations under the License.
*/

package org.example.service.parameter;

import org.example.common.BaseResult;

import org.example.common.model.ListConfigParametersModel;

import org.example.common.param.ListConfigParametersParam;
import org.example.common.param.UpdateConfigParameterParam;


import javax.validation.Valid;

public interface ParameterManagerService {

    /**
     * List config parameters
     * @param listConfigParametersParam updateConfigParamerParam
     * @return {@link BaseResult< ListConfigParametersModel >}
     */
    BaseResult<ListConfigParametersModel> listConfigParameters(ListConfigParametersParam listConfigParametersParam);

    /**
     * Update config parameters
     * @param updateConfigParameterParam updateConfigParameterParam
     * @return {@link BaseResult<Void>}
     */
    BaseResult<Void> updateConfigParameter(UpdateConfigParameterParam updateConfigParameterParam);
}
