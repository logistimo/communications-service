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
import com.logistimo.communication.common.model.NotificationRequest;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by kumargaurav on 24/07/18.
 */
@Component
public class NotificationRoute extends RouteBuilder {

  private static final Logger logger = LoggerFactory.getLogger(NotificationRoute.class);

  private MessageHandler messageHandler;

  @Autowired
  public void setMessageHandler(MessageHandler messageHandler) {
    this.messageHandler = messageHandler;
  }


  @Override
  public void configure() throws Exception {

    logger.info("Notification route started");
    from("{{start.queue}}")
        .convertBodyTo(NotificationRequest.class)
        .id("NotificationRoute")
        .log(LoggingLevel.INFO, NotificationRequest.class.getName(), "Processing notification: ${body} ")
        .bean(messageHandler,"invoke");
  }
}