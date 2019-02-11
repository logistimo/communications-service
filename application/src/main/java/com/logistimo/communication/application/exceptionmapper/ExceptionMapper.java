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

package com.logistimo.communication.application.exceptionmapper;

import com.logistimo.communication.common.exceptions.CMBusinessException;
import com.logistimo.communication.common.exceptions.CMValidationException;
import com.logistimo.communication.common.exceptions.ErrorResource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Created by kumargaurav on 04/09/18.
 */
@ControllerAdvice
public class ExceptionMapper extends ResponseEntityExceptionHandler {

  @ResponseBody
  @ExceptionHandler(CMValidationException.class)
  public ResponseEntity<ErrorResource> handleException(CMValidationException exception) {
    return new ResponseEntity<>(
        new ErrorResource(exception.getMessage(), exception.getCode(),
        HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
  }

  @ResponseBody
  @ExceptionHandler(CMBusinessException.class)
  public ResponseEntity<ErrorResource> handleException(CMBusinessException exception) {

    return new ResponseEntity<>(
        new ErrorResource(exception.getMessage(), exception.getCode(),
            HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);

  }
}
