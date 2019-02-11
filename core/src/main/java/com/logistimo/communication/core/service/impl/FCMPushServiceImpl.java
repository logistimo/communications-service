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

import com.google.api.client.http.HttpResponseException;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;

import com.logistimo.communication.common.exceptions.CMBusinessException;
import com.logistimo.communication.common.model.FCMNotificationRequest;
import com.logistimo.communication.core.mapper.DataMapper;
import com.logistimo.communication.core.service.PushService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by kumargaurav on 24/09/18.
 */
@Component
public class FCMPushServiceImpl implements PushService {

  private static final Logger log = LoggerFactory.getLogger(FCMPushServiceImpl.class);
  private static FirebaseApp firebaseApp;

  @Override
  public void send(FCMNotificationRequest data) {

    String response;
    Message message = buildMessage(data);
    try{
      response =  firebaseMessaging().send(message);
      log.debug("Push notification response {} for request {} ",response, data);
    } catch (FirebaseMessagingException ex) {
      String code = String.valueOf(((HttpResponseException) ((FirebaseMessagingException) ex).getCause()).getStatusCode());
      String msg = ((HttpResponseException) ((FirebaseMessagingException) ex).getCause()).getStatusMessage();
      handleError(code,msg);
    }
  }

  private Message buildMessage(FCMNotificationRequest data) {
    return DataMapper.mapToMsg(data);
  }

  private static FirebaseApp app () {
    if(firebaseApp == null) {
      firebaseApp = FirebaseApp.initializeApp();
    }
    return firebaseApp;
  }

  protected FirebaseMessaging firebaseMessaging () {
    return FirebaseMessaging.getInstance(app());
  }

  private void handleError(String code, String message) {
    throw new CMBusinessException(code, message);
  }
}
