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

package com.logistimo.communication.application.actions;

import com.logistimo.communication.application.utils.DeDupeUtil;
import com.logistimo.communication.common.exceptions.CMValidationException;
import com.logistimo.communication.common.model.FCMNotificationData;
import com.logistimo.communication.common.model.FCMNotificationRequest;
import com.logistimo.communication.common.model.MetaTag;
import com.logistimo.communication.common.model.NotificationContent;
import com.logistimo.communication.common.model.NotificationData;
import com.logistimo.communication.common.model.NotificationRequest;
import com.logistimo.communication.common.model.NotificationType;
import com.logistimo.communication.common.model.Subscriber;
import com.logistimo.communication.core.actions.AuditAction;
import com.logistimo.communication.core.service.PostMessageHandler;
import com.logistimo.communication.repo.entity.NotificationLog;

import org.apache.camel.ProducerTemplate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validator;
import javax.validation.metadata.ConstraintDescriptor;

/**
 * Created by kumargaurav on 15/10/18.
 */
public class MessageHandlerTest {

  Validator validator;

  ProducerTemplate submitter;

  PostMessageHandler processor;

  DeDupeUtil deDupeUtil;

  AuditAction auditAction;

  MessageHandler messageHandler;

  @Before
  public void setUp () {

    validator = Mockito.mock(Validator.class);
    submitter = Mockito.mock(ProducerTemplate.class);
    processor = Mockito.mock(PostMessageHandler.class);
    deDupeUtil = Mockito.mock(DeDupeUtil.class);
    auditAction = Mockito.mock(AuditAction.class);
    messageHandler = new MessageHandler();
    messageHandler.setValidator(validator);
    messageHandler.setSubmitter(submitter);
    messageHandler.setProcessor(processor);
    messageHandler.setDeDupeUtil(deDupeUtil);
    messageHandler.setAuditAction(auditAction);
  }

  @Test
  public void testInvoke() {
    Mockito.when(auditAction.addAuditLog(Mockito.<NotificationLog>any())).thenReturn(buildLog());
    Mockito.when(validator.validate(Mockito.<NotificationRequest>any())).thenReturn(new HashSet<ConstraintViolation<NotificationRequest>>());
    Mockito.when(deDupeUtil.checkDuplicate(Mockito.<NotificationRequest>any())).thenReturn(Boolean.FALSE);
    Mockito.when(processor.process(Mockito.<NotificationRequest>any())).thenReturn(buildData());
    Mockito.doNothing().when(submitter).sendBody(Mockito.<NotificationData>any());
    messageHandler.invoke(buildRequest());
  }

  @Test(expected = CMValidationException.class)
  public void testInvokeException() {
    Mockito.when(auditAction.addAuditLog(Mockito.<NotificationLog>any())).thenReturn(buildLog());
    Mockito.when(validator.validate(Mockito.<NotificationRequest>any())).thenReturn(buildErr());
    messageHandler.invoke(new NotificationRequest());
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

  private NotificationData buildData() {
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

  private NotificationLog buildLog() {
    NotificationLog log = new NotificationLog();
    log.setUid(UUID.randomUUID().toString());
    return log;
  }

  public Set buildErr () {
    Set<ConstraintViolation<NotificationRequest>> violations = new HashSet <ConstraintViolation<NotificationRequest>> ();
    violations.add(new ConstraintViolation<NotificationRequest>() {
      @Override
      public String getMessage() {
        return "Please provide app name";
      }

      @Override
      public String getMessageTemplate() {
        return null;
      }

      @Override
      public NotificationRequest getRootBean() {
        return null;
      }

      @Override
      public Class<NotificationRequest> getRootBeanClass() {
        return null;
      }

      @Override
      public Object getLeafBean() {
        return null;
      }

      @Override
      public Object[] getExecutableParameters() {
        return new Object[0];
      }

      @Override
      public Object getExecutableReturnValue() {
        return null;
      }

      @Override
      public Path getPropertyPath() {
        return null;
      }

      @Override
      public Object getInvalidValue() {
        return null;
      }

      @Override
      public ConstraintDescriptor<?> getConstraintDescriptor() {
        return null;
      }

      @Override
      public <U> U unwrap(Class<U> aClass) {
        return null;
      }
    });
    return violations;
  }
}
