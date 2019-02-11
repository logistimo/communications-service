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

package com.logistimo.communication.application.controllers;

import com.logistimo.communication.common.model.RegisterServiceRequest;
import com.logistimo.communication.common.model.RegisterServiceResponse;
import com.logistimo.communication.core.service.RegistrationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by kumargaurav on 28/06/18.
 */
@RestController
public class RegistryController {


  private RegistrationService registrationService;

  @Autowired
  public void setRegistrationService(
      RegistrationService registrationService) {
    this.registrationService = registrationService;
  }

  @RequestMapping(path = "/register-provider")
  public void registerNofiticationProvider () {


  }

  @RequestMapping(path = "/register-policy")
  public void registerNofiticationPolicy () {


  }

  @RequestMapping(path = "/service-registration",method = RequestMethod.POST)
  public @ResponseBody RegisterServiceResponse registerApp (@RequestBody RegisterServiceRequest request) {
    return registrationService.register(request);
  }
}
