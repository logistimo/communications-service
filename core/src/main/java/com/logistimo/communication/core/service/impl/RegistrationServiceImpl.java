/*
 * Copyright © 2019 Logistimo.
 *
 * This file is part of Logistimo.
 *
 * Logistimo software is a mobile & web platform for supply chain management and remote temperature monitoring in
 * low-resource settings, made available under the terms of the GNU Affero General Public License (AGPL).
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * You can be released from the requirements of the license by purchasing a commercial license. To know more about
 * the commercial license, please contact us at opensource@logistimo.com
 */

package com.logistimo.communication.core.service.impl;

import com.logistimo.communication.common.model.RegisterServiceRequest;
import com.logistimo.communication.common.model.RegisterServiceResponse;
import com.logistimo.communication.common.exceptions.CMValidationException;
import com.logistimo.communication.core.utils.ApiKeyGenerator;
import com.logistimo.communication.core.service.RegistrationService;
import com.logistimo.communication.core.utils.MessageUtils;
import com.logistimo.communication.repo.dao.ClientInfoDAO;
import com.logistimo.communication.repo.entity.ClientInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

/**
 * Created by kumargaurav on 31/08/18.
 */
@Component
public class RegistrationServiceImpl implements RegistrationService {

  private ClientInfoDAO clientInfoDAO;

  private Validator validator;

  @Autowired
  public void setClientInfoDAO(ClientInfoDAO clientInfoDAO) {
    this.clientInfoDAO = clientInfoDAO;
  }

  @Autowired
  public void setValidator(Validator validator) {
    this.validator = validator;
  }

  @Override
  public RegisterServiceResponse register(RegisterServiceRequest request) {
    validate(request);
    ClientInfo clientInfo = save(request);
    return getResponse(clientInfo);
  }

  protected void validate(RegisterServiceRequest request){
    Set<ConstraintViolation<RegisterServiceRequest>> violations = validator.validate(request);
    if (!violations.isEmpty() && violations.size() > 0) {
      String code = "CM001";
      throw new CMValidationException(code, MessageUtils.constructMessage(code));
    }
  }

  private ClientInfo save (RegisterServiceRequest request) {
    ClientInfo client = new ClientInfo();
    client.setAppName(request.getAppName());
    client.setAppType(request.getAppType());
    client.setApiKey(ApiKeyGenerator.generateKey());
    client.setApiKeyIssuedOn(new Date());
    if(null != request.getRedirectUris() && !request.getRedirectUris().isEmpty()) {
      client.setRedirectUris(StringUtils.arrayToCommaDelimitedString(request.getRedirectUris().toArray()));
    }
    return clientInfoDAO.save(client);
  }

  private RegisterServiceResponse getResponse (ClientInfo clientInfo) {
    RegisterServiceResponse res = new RegisterServiceResponse();
    res.setAppId(clientInfo.getAppId());
    res.setApiKey(clientInfo.getApiKey());
    res.setAppType(clientInfo.getAppType());
    if (!StringUtils.isEmpty(clientInfo.getRedirectUris())) {
      res.setRedirectUris(Arrays.asList(clientInfo.getRedirectUris().split(",")));
    }
    res.setApiKeyIssuedOn(String.valueOf(clientInfo.getApiKeyIssuedOn()));
    return res;
  }

}
