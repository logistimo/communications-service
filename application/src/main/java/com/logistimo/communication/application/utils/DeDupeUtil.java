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

package com.logistimo.communication.application.utils;

import com.logistimo.communication.common.exceptions.CMValidationException;
import com.logistimo.communication.common.model.NotificationContent;
import com.logistimo.communication.common.model.NotificationRequest;
import com.logistimo.communication.common.model.Subscriber;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by kumargaurav on 13/07/18.
 */
@Component
public class DeDupeUtil {

  private CacheUtil cacheUtil;

  @Autowired
  public void setCacheUtil(CacheUtil cacheUtil) {
    this.cacheUtil = cacheUtil;
  }

  public boolean checkDuplicate(NotificationRequest request){
    String key = hash(request);
    boolean duplicateReq = cacheUtil.keyExists(key);
    if (!duplicateReq){
      cacheUtil.add(key,"NOTIF");

    }
    return duplicateReq;
  }

  private String hash(NotificationRequest request) {
    int hash = 7;
    String app = request.getApp();
    Subscriber subscriber = request.getSubscriber();
    NotificationContent content = request.getContent();
    hash = 31 * hash + (app == null ? 0 : app.hashCode());
    hash = 31 * hash + (subscriber.getUserId() == null ? 0 : subscriber.getUserId().hashCode());
    hash = 31 * hash + (subscriber.getPhone() == null ? 0 : subscriber.getPhone().hashCode());
    hash = 31 * hash + (subscriber.getEmail() == null ? 0 : subscriber.getEmail().hashCode());
    hash = 31 * hash + (subscriber.getPtoken() == null ? 0 : subscriber.getPtoken().hashCode());
    hash = 31 * hash + (content.getEmail() == null ? 0 : content.getEmail().hashCode());
    hash = 31 * hash + (content.getSms() == null ? 0 : content.getSms().hashCode());
    hash = 31 * hash + (content.getType() == null ? 0 : content.getType().hashCode());
    hash = 31 * hash + (content.getSubtype() == null ? 0 : content.getSubtype().hashCode());
    hash = 31 * hash + (content.getText() == null ? 0 : content.getText().hashCode());
    hash = 31 * hash + (content.getTitle() == null ? 0 : content.getTitle().hashCode());
    hash = 31 * hash + (content.getGroupTitle() == null ? 0 : content.getGroupTitle().hashCode());
    hash = 31 * hash + (content.getAction() == null ? 0 : content.getAction().hashCode());
    return String.valueOf(hash);
  }
}
