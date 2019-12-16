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
import com.logistimo.communication.common.model.NotificationContent;
import com.logistimo.communication.common.model.NotificationData;
import com.logistimo.communication.common.model.NotificationLogStatus;
import com.logistimo.communication.common.model.NotificationRequest;
import com.logistimo.communication.common.model.NotificationType;
import com.logistimo.communication.core.actions.AuditAction;
import com.logistimo.communication.core.service.PostMessageHandler;
import com.logistimo.communication.repo.entity.NotificationLog;
import static com.logistimo.communication.common.constants.CommonConstant.SPACE;
import static com.logistimo.communication.common.constants.CommonConstant.NO_CONTENT;

import org.apache.camel.Body;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

/**
 * Created by kumargaurav on 04/09/18.
 */
@Component
public class MessageHandler {

  private static final Logger log = LoggerFactory.getLogger(MessageHandler.class);

  private Validator validator;

  private ProducerTemplate submitter;

  private PostMessageHandler processor;

  private DeDupeUtil deDupeUtil;

  private AuditAction auditAction;

  @Autowired
  public void setValidator(Validator validator) {
    this.validator = validator;
  }

  @EndpointInject(uri = "activemq:notification-final-snapshot")
  public void setSubmitter(ProducerTemplate submitter) {
    this.submitter = submitter;
  }

  @Autowired
  public void setProcessor(PostMessageHandler processor) {
    this.processor = processor;
  }

  @Autowired
  public void setDeDupeUtil(DeDupeUtil deDupeUtil) {
    this.deDupeUtil = deDupeUtil;
  }

  @Autowired
  public void setAuditAction(AuditAction auditAction) {
    this.auditAction = auditAction;
  }

  public void invoke(@Body NotificationRequest request) {
    validate(request);
    NotificationLog auditLog = audit(request);
    request.setId(auditLog.getUid());
    dedupe(request);
    throttle(request);
    NotificationData repackaged = processor.process(request);
    submitter.sendBody(repackaged);
    log.info("Request {} submitted for gateway pipeline",request);
  }

  private void validate(NotificationRequest request) {
    log.info("validate request {}",request);
    Set<ConstraintViolation<NotificationRequest>> violations = validator.validate(request);
    if (!violations.isEmpty() && violations.size() > 0) {
      StringBuilder errBuilder = new StringBuilder();
      violations.forEach(violation -> errBuilder.append(violation.getMessage()));
      log.error("Invalid request with data {} and error {}", request, errBuilder.toString());
      throw new CMValidationException("CM003","Invalid notification payload");
    }
  }

  private void dedupe(NotificationRequest request) {
    if (deDupeUtil.checkDuplicate(request)) {
      auditAction.updateAuditLog(request.getId(),NotificationLogStatus.FAILED.name() + " : " + " duplicate request");
      throw new CMValidationException("CM002", "Duplicate request");
    }
  }

  private void throttle(NotificationRequest request) {
    log.debug("throttle request {}",request);
  }

  private NotificationLog audit(NotificationRequest data) {
    log.info("audit request {}",data);
    NotificationLog notificationLog = new NotificationLog();
    notificationLog.setAppName(data.getApp());
    notificationLog.setNotificationType(data.getNotificationType().name());
    notificationLog.setStatus(NotificationLogStatus.QUEUED.name());
    notificationLog.setCreateDate(new Date());
    notificationLog.setMessage(message(data));
    return auditAction.addAuditLog(notificationLog);
  }

  private String message(NotificationRequest data) {
    NotificationType type = data.getNotificationType();
    StringBuilder builder = new StringBuilder();
    NotificationContent content = data.getContent();
    switch (type) {
      case SMS:
        builder.append(content.getSms());
        break;
      case EMAIL:
        builder.append(content.getEmail());
        break;
      case PUSH:
        builder.append(content.getType()).append(SPACE).append(content.getTitle()).append((SPACE)).append(content.getText());
        break;
      default:
        builder.append(NO_CONTENT);
    }
    return builder.toString();
  }
}
