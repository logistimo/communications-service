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

import com.logistimo.communication.application.actions.MessageHandler;
import com.logistimo.communication.common.model.MetaTag;
import com.logistimo.communication.common.model.NotificationContent;
import com.logistimo.communication.common.model.NotificationRequest;
import com.logistimo.communication.common.model.NotificationType;
import com.logistimo.communication.common.model.Subscriber;

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
public class NotificationRouteTest extends CamelTestSupport {


  MessageHandler messageHandler;

  NotificationRoute notificationRoute;

  static BrokerService brokerSvc;

  final static String ENDPOINT = "activemq:queue:notification";

  @Override
  public RoutesBuilder createRouteBuilder() throws Exception {
    notificationRoute = new NotificationRoute();
    messageHandler = Mockito.mock(MessageHandler.class);
    notificationRoute.setMessageHandler(messageHandler);
    return notificationRoute;
  }

  @Override
  protected Properties useOverridePropertiesWithPropertiesComponent() {
    Properties properties = new Properties();
    properties.put("start.queue",ENDPOINT);
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

    Mockito.doNothing().when(messageHandler).invoke(buildRequest());
    template.sendBody(ENDPOINT,buildRequest());
  }

  private NotificationRequest buildRequest() {
    NotificationRequest request = new NotificationRequest();
    request.setApp("logistimo");
    request.setNotificationType(NotificationType.PUSH);
    Subscriber subscriber = new Subscriber();
    subscriber.setPtoken("cULkDm6gCpo:APA91bGdp-jbE_rJp5D8yrXh0FZ3ROC6PlrSSDYkY91QZsxfAd5FgmjBUgq4Rfss3g9uiQkJa0pDTD9ihO0dsJj-vVlbanrO_HckAjBi-uZr9UGf44l6KQUAZQeznV4dl-AwdKcwVZC6");
    request.setSubscriber(subscriber);
    NotificationContent content = new NotificationContent();
    content.setType("Event sumamry");
    content.setSubtype("critical#inventory#stock_outs");
    content.setGroupTitle("Critical events");
    content.setTitle("Stock out");
    content.setText("10 materials stocked out");
    content.setAction("INFO");
    request.setContent(content);
    MetaTag tag = new MetaTag();
    tag.setRetry(3);
    request.setTag(tag);
    return request;
  }

}
