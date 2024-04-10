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

package org.example.service.parameter.impl;

import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.common.BaseResult;
import org.example.common.helper.oos.BaseOosHelper;
import org.example.common.model.ListConfigParametersModel;
import org.example.common.param.parameter.ListConfigParametersParam;
import org.example.common.param.parameter.UpdateConfigParameterParam;
import org.example.service.parameter.ParameterManagerService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ParameterManagerServiceImpl implements ParameterManagerService {

    @Resource
    private BaseOosHelper baseOosHelper;

    @Override
    public BaseResult<ListConfigParametersModel> listConfigParameters(ListConfigParametersParam listConfigParametersParam) {
        return baseOosHelper.listConfigParameters(listConfigParametersParam);
    }

    @Override
    public BaseResult<Void> updateConfigParameter(UpdateConfigParameterParam updateConfigParameterParam) {
        return baseOosHelper.updateConfigParameter(updateConfigParameterParam);
    }
}

