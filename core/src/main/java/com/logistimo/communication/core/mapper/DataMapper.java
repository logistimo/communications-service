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

package com.logistimo.communication.core.mapper;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.Message;

import com.logistimo.communication.common.model.FCMNotificationData;
import com.logistimo.communication.common.model.FCMNotificationRequest;

/**
 * Created by kumargaurav on 24/09/18.
 */
public class DataMapper {

  private DataMapper() {}

  public static Message mapToMsg(FCMNotificationRequest request) {
    Message.Builder builder =  Message.builder();
    FCMNotificationData data = request.getMData();
    builder.setToken(request.getMTo());
    builder.setAndroidConfig(AndroidConfig.builder().setPriority(AndroidConfig.Priority.HIGH).build());
    builder.putData("type",data.getType());
    builder.putData("subtype",data.getSubtype());
    builder.putData("group_title",data.getGroupTitle());
    builder.putData("title",data.getTitle());
    builder.putData("text",data.getText());
    builder.putData("action",data.getAction());
    if(data.getCustomData() != null && data.getCustomData().size() > 0) {
      data.getCustomData().entrySet().stream().forEach(entry ->  builder.putData(entry.getKey(),entry.getValue()));
    }
    return builder.build();
  }
}
