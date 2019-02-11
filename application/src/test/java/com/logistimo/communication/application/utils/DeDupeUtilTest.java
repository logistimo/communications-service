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

import com.logistimo.communication.common.model.MetaTag;
import com.logistimo.communication.common.model.NotificationContent;
import com.logistimo.communication.common.model.NotificationRequest;
import com.logistimo.communication.common.model.NotificationType;
import com.logistimo.communication.common.model.Subscriber;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Created by kumargaurav on 10/10/18.
 */
public class DeDupeUtilTest {

  CacheUtil cacheUtil;
  DeDupeUtil deDupeUtil;
  @Before
  public void setUp() {
    cacheUtil = Mockito.mock(CacheUtil.class);
    deDupeUtil = new DeDupeUtil();
    deDupeUtil.setCacheUtil(cacheUtil);
  }

  @Test
  public void testCheckDuplicate () {
    Mockito.when(cacheUtil.keyExists(Mockito.anyString())).thenReturn(Boolean.FALSE);
    Assert.assertEquals(Boolean.FALSE,deDupeUtil.checkDuplicate(build()));
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
