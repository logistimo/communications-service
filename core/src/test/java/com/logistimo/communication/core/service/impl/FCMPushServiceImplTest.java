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

import com.google.firebase.messaging.FirebaseMessaging;

import com.logistimo.communication.common.model.FCMNotificationData;
import com.logistimo.communication.common.model.FCMNotificationRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Created by kumargaurav on 25/09/18.
 */
public class FCMPushServiceImplTest {

  FCMPushServiceImpl service;

  FirebaseMessaging firebaseMessaging;

  @Before
  public void setUp() {
    firebaseMessaging = Mockito.mock(FirebaseMessaging.class);
    service = new FCMPushServiceImpl() {
      protected FirebaseMessaging firebaseMessaging() {
        return firebaseMessaging;
      }
    };
  }

  @Test
  public void testSend() {
    service.send(build());
  }

  private FCMNotificationRequest build() {
    FCMNotificationRequest fcmRequest = new FCMNotificationRequest();
    FCMNotificationData fcmData = new FCMNotificationData();
    fcmData.setType("critical");
    fcmData.setSubtype("");
    fcmData.setGroupTitle("");
    fcmData.setTitle("1 Assets heating");
    fcmData.setText("across 2 stores");
    fcmData.setAction("INFO");
    fcmRequest.setMData(fcmData);
    fcmRequest.setMTo("test-token");
    return fcmRequest;
  }
}
