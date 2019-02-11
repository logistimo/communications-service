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

import com.logistimo.communication.common.model.MetaTag;
import com.logistimo.communication.common.model.NotificationContent;
import com.logistimo.communication.common.model.NotificationData;
import com.logistimo.communication.common.model.NotificationRequest;
import com.logistimo.communication.common.model.NotificationType;
import com.logistimo.communication.common.model.Subscriber;
import com.logistimo.communication.core.service.PostMessageHandler;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by kumargaurav on 09/10/18.
 */
public class PostMessageHandlerImplTest {


  @Test
  public void testProcess() {
    PostMessageHandler handler = new PostMessageHandlerImpl();
    NotificationData repackaged = handler.process(build());
    Assert.assertNotNull(repackaged);
    Assert.assertNotNull(repackaged.getNotificationRequest());
  }

  private NotificationRequest build () {
    NotificationRequest request = new NotificationRequest();
    request.setApp("logistimo");
    request.setNotificationType(NotificationType.PUSH);
    Subscriber subscriber = new Subscriber();
    subscriber.setUserId("test-user");
    subscriber.setPtoken("test-token");
    request.setSubscriber(subscriber);
    NotificationContent content = new NotificationContent();
    content.setType("critical");
    content.setSubtype("");
    content.setGroupTitle("");
    content.setTitle("1 Assets heating");
    content.setText("across 2 stores");
    content.setAction("INFO");
    request.setContent(content);
    MetaTag tag = new MetaTag();
    tag.setRetry(3);
    request.setTag(tag);
    return request;
  }
}
