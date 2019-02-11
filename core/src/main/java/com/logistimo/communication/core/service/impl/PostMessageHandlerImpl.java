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

import com.logistimo.communication.common.model.EmailData;
import com.logistimo.communication.common.model.EmailRequest;
import com.logistimo.communication.common.model.FCMNotificationData;
import com.logistimo.communication.common.model.FCMNotificationRequest;
import com.logistimo.communication.common.model.FailoverConfig;
import com.logistimo.communication.common.model.NotificationData;
import com.logistimo.communication.common.model.NotificationRequest;
import com.logistimo.communication.common.model.NotificationType;
import com.logistimo.communication.common.model.SMSRequest;
import com.logistimo.communication.core.service.PostMessageHandler;

import org.springframework.stereotype.Component;

/**
 * Created by kumargaurav on 07/09/18.
 */
@Component
public class PostMessageHandlerImpl implements PostMessageHandler {


  @Override
  public NotificationData process(NotificationRequest request) {

    NotificationType type = request.getNotificationType();
    NotificationData data = new NotificationData();
    data.setId(request.getId());
    data.setType(type);
    data.setRetry(request.getTag().getRetry());
    addData(request,data,type);
    //handle failover
    FailoverConfig config = request.getTag().getFailoverConfig();
    if (config != null) {
      data.setFailoverConfigured(true);
      data.setFailoverType(config.getType());
      addData(request,data,data.getFailoverType());
    }
    return data;
  }

  private void addData(NotificationRequest request, NotificationData data, NotificationType type) {
    switch (type) {
      case SMS:
        addSMSData(request,data);
        break;
      case EMAIL:
        addEMAILData(request,data);
        break;
      case PUSH:
        addFCMData(request,data);
    }
  }
  private void addFCMData(NotificationRequest request, NotificationData data) {
    FCMNotificationRequest fcmRequest = new FCMNotificationRequest();
    FCMNotificationData fcmData = new FCMNotificationData();
    fcmData.setType(request.getContent().getType());
    fcmData.setSubtype(request.getContent().getSubtype());
    fcmData.setGroupTitle(request.getContent().getGroupTitle());
    fcmData.setTitle(request.getContent().getTitle());
    fcmData.setText(request.getContent().getText());
    fcmData.setAction(request.getContent().getAction());
    fcmData.setCustomData(request.getContent().getCustomData());
    fcmRequest.setMData(fcmData);
    fcmRequest.setMTo(request.getSubscriber().getPtoken());
    data.setNotificationRequest(fcmRequest);
  }

  private void addEMAILData(NotificationRequest request, NotificationData data) {
    EmailRequest emailRequest = new EmailRequest();
    EmailData emailData = new EmailData();
    emailData.setContent(request.getContent().getText());
    emailRequest.setEmailData(emailData);
    emailRequest.setEmail(request.getSubscriber().getEmail());
    data.setEmailRequest(emailRequest);
  }

  private void addSMSData(NotificationRequest request, NotificationData data) {
    SMSRequest smsRequest = new SMSRequest();
    smsRequest.setMessage(request.getContent().getSms());
    smsRequest.setNumber(request.getSubscriber().getPhone());
    data.setSmsRequest(smsRequest);
  }
}
