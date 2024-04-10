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

package org.example.common.helper.oos;

import com.aliyun.oos20190601.models.CreateParameterResponse;
import com.aliyun.oos20190601.models.CreateSecretParameterResponse;
import com.aliyun.oos20190601.models.GetParameterResponse;
import com.aliyun.oos20190601.models.GetParameterResponseBody;
import com.aliyun.oos20190601.models.GetSecretParameterResponse;
import com.aliyun.oos20190601.models.GetSecretParameterResponseBody;
import com.aliyun.oos20190601.models.UpdateParameterResponse;
import com.aliyun.oos20190601.models.UpdateSecretParameterResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.example.common.BaseResult;
import org.example.common.adapter.OosClient;
import org.example.common.errorinfo.ErrorInfo;
import org.example.common.exception.BizException;
import org.example.common.model.ConfigParameterModel;
import org.example.common.model.ListConfigParametersModel;

import org.example.common.param.parameter.ListConfigParametersParam;
import org.example.common.param.parameter.UpdateConfigParameterParam;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BaseOosHelper {

    private final OosClient oosClient;

    public BaseOosHelper(OosClient oosClient) {
        this.oosClient = oosClient;
    }

    public BaseResult<Void> updateConfigParameter(UpdateConfigParameterParam updateConfigParameterParam){
        try {
            //是否加密
            if (updateConfigParameterParam.getEncrypted() == Boolean.TRUE) {
                //尝试查询
                GetSecretParameterResponse response = oosClient.getSecretParameter(updateConfigParameterParam.getName());
                Optional<String> optionalValue = Optional.ofNullable(response)
                        .map(GetSecretParameterResponse::getBody)
                        .map(GetSecretParameterResponseBody::getParameter)
                        .map(GetSecretParameterResponseBody.GetSecretParameterResponseBodyParameter::getValue);
                //是否可以查询到
                if (optionalValue.isPresent()) {
                    //若可以查询到，则更新
                    UpdateSecretParameterResponse updateSecretParameterResponse = oosClient.updateSecretParameter(updateConfigParameterParam.getName(), updateConfigParameterParam.getValue());
                    if (updateSecretParameterResponse.getBody().getParameter().getId() != null && !updateSecretParameterResponse.getBody().getParameter().getId().isEmpty()) {
                        return BaseResult.success();
                    } else {
                        return BaseResult.fail("updateConfigParameter::updateSecretParameter fail:"+ErrorInfo.RESOURCE_NOT_FOUND);
                    }
                } else {
                    //若不能查询到，则创建
                    CreateSecretParameterResponse createSecretParameterResponse = oosClient.createSecretParameter(updateConfigParameterParam.getName(), updateConfigParameterParam.getValue());
                    if (createSecretParameterResponse.getBody().getParameter().getId() != null && !createSecretParameterResponse.getBody().getParameter().getId().isEmpty()) {
                        return BaseResult.success();
                    } else {
                        return BaseResult.fail("updateConfigParameter::createSecretParameter fail:"+ErrorInfo.RESOURCE_NOT_FOUND);
                    }
                }
            } else if (updateConfigParameterParam.getEncrypted() == Boolean.FALSE) {
                //尝试查询
                GetParameterResponse response = oosClient.getParameter(updateConfigParameterParam.getName());
                Optional<String> optionalValue = Optional.ofNullable(response)
                        .map(GetParameterResponse::getBody)
                        .map(GetParameterResponseBody::getParameter)
                        .map(GetParameterResponseBody.GetParameterResponseBodyParameter::getValue);
                //是否可以查询到
                if (optionalValue.isPresent()) {
                    //若可以查询到，则更新
                    UpdateParameterResponse updateParameterResponse = oosClient.updateParameter(updateConfigParameterParam.getName(), updateConfigParameterParam.getValue());
                    if (updateParameterResponse.getBody().getParameter().getId() != null && !updateParameterResponse.getBody().getParameter().getId().isEmpty()) {
                        return BaseResult.success();
                    } else {
                        return BaseResult.fail("updateConfigParameter::updateParameter fail:"+ErrorInfo.RESOURCE_NOT_FOUND);
                    }
                } else {
                    //若不能查询到，则创建
                    CreateParameterResponse createParameterResponse = oosClient.createParameter(updateConfigParameterParam.getName(), updateConfigParameterParam.getValue());
                    if (createParameterResponse.getBody().getParameter().getId() != null && !createParameterResponse.getBody().getParameter().getId().isEmpty()) {
                        return BaseResult.success();
                    } else {
                        return BaseResult.fail("updateConfigParameter::createParameter fail:"+ErrorInfo.RESOURCE_NOT_FOUND);
                    }
                }
            }
        } catch (Exception e) {
            log.error("updateConfigParameter error", e);
            throw new BizException(ErrorInfo.RESOURCE_NOT_FOUND);
        }
        return BaseResult.fail("updateConfigParameter fail, updateConfigParamerParam.getEncrypted() is an unexpected value:"+ErrorInfo.RESOURCE_NOT_FOUND);
    }

    public BaseResult<ListConfigParametersModel> listConfigParameters(ListConfigParametersParam listConfigParametersParam) {
        List<String> names = listConfigParametersParam.getName();
        List<Boolean> encrypteds = listConfigParametersParam.getEncrypted();
        if (names == null || encrypteds == null || names.size() != encrypteds.size()) {
            return BaseResult.fail("listConfigParameters: Either 'names' or 'encrypted' is null, or their size do not match.");
        }

        ListConfigParametersModel listConfigParametersModel = new ListConfigParametersModel();
        List<ConfigParameterModel> configParameterModels = new ArrayList<>();

        IntStream.range(0, names.size())
                .forEach(index -> {
                    try {
                        String nameItem = names.get(index);
                        Boolean encryptedItem = encrypteds.get(index);

                        if (encryptedItem == Boolean.TRUE) {
                            GetSecretParameterResponse response = oosClient.getSecretParameter(nameItem);
                            ConfigParameterModel configParameterModel = extractSecretParameterDetails(response);
                            configParameterModels.add(configParameterModel);
                        } else if (encryptedItem == Boolean.FALSE) {
                            GetParameterResponse response = oosClient.getParameter(nameItem);
                            ConfigParameterModel configParameterModel = extractParameterDetails(response);
                            configParameterModels.add(configParameterModel);
                        }
                    } catch (Exception e) {
                        log.error("ListSecretParameter error", e);
                        throw new BizException(ErrorInfo.RESOURCE_NOT_FOUND);
                    }
                });
        listConfigParametersModel.setConfigParameterModels(configParameterModels);
        return BaseResult.success(listConfigParametersModel);
    }

    public String getSecretParameter(String name) {
        try {
            GetSecretParameterResponse response = oosClient.getSecretParameter(name);
            Optional<String> optionalValue = Optional.ofNullable(response)
                    .map(GetSecretParameterResponse::getBody)
                    .map(GetSecretParameterResponseBody::getParameter)
                    .map(GetSecretParameterResponseBody.GetSecretParameterResponseBodyParameter::getValue);
            return optionalValue.orElseThrow(() -> new BizException(ErrorInfo.RESOURCE_NOT_FOUND));
        } catch (Exception e) {
            log.error("getSecretParameter error", e);
            throw new BizException(ErrorInfo.RESOURCE_NOT_FOUND);
        }
    }


    private ConfigParameterModel extractSecretParameterDetails(GetSecretParameterResponse getSecretParameterResponse) {
        ConfigParameterModel configParameterModel = new ConfigParameterModel();
        configParameterModel.setName(getSecretParameterResponse.getBody().getParameter().getName());
        configParameterModel.setType(getSecretParameterResponse.getBody().getParameter().getType());
        configParameterModel.setId(getSecretParameterResponse.getBody().getParameter().getId());
        return configParameterModel;
    }

    private ConfigParameterModel extractParameterDetails(GetParameterResponse getParameterResponse) {
        ConfigParameterModel configParameterModel = new ConfigParameterModel();
        configParameterModel.setName(getParameterResponse.getBody().getParameter().getName());
        configParameterModel.setType(getParameterResponse.getBody().getParameter().getType());
        configParameterModel.setId(getParameterResponse.getBody().getParameter().getId());
        return configParameterModel;
    }
}
