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

package com.logistimo.communication.application.routes;

import com.logistimo.communication.common.model.FCMNotificationData;
import com.logistimo.communication.common.model.FCMNotificationRequest;
import com.logistimo.communication.common.model.NotificationData;
import com.logistimo.communication.core.service.NotificationProcessor;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Properties;

/**
 * Created by kumargaurav on 15/10/18.
 */
public class ProcessorRouteTest extends CamelTestSupport {

  NotificationProcessor notificationProcessor;

  ProcessorRoute processorRoute;

  static BrokerService brokerSvc;

  final static String ENDPOINT = "activemq:queue:notification-final";

  @Override
  public RoutesBuilder createRouteBuilder() throws Exception {
    processorRoute = new ProcessorRoute();
    notificationProcessor = Mockito.mock(NotificationProcessor.class);
    processorRoute.setProcessor(notificationProcessor);
    return processorRoute;
  }

  @Override
  protected Properties useOverridePropertiesWithPropertiesComponent() {
    Properties properties = new Properties();
    properties.put("next.queue",ENDPOINT);
    return properties;
  }

  @BeforeClass
  public static void setUpClass() throws Exception {
    System.setProperty("org.apache.activemq.SERIALIZABLE_PACKAGES","*");
    ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory("vm://localhost?create=false");
    brokerSvc = new BrokerService();
    brokerSvc.setBrokerName("TestBroker");
    brokerSvc.addConnector("tcp://localhost:61616");
    brokerSvc.start();
  }

  @AfterClass
  public static void tearDownClass() throws Exception {
    if(brokerSvc != null) {
      brokerSvc.stop();
    }
  }

  @Test
  public void testNotificationRoute() {

    Mockito.doNothing().when(notificationProcessor).process(buildRequest());
    template.sendBody(ENDPOINT,buildRequest());
  }

  private NotificationData buildRequest() {
    NotificationData data = new NotificationData();
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
    data.setNotificationRequest(fcmRequest);
    data.setFailoverConfigured(false);
    return data;
  }
}
