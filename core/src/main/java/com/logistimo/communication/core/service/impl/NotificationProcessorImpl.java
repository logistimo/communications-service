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

import com.logistimo.communication.common.exceptions.CMBusinessException;
import com.logistimo.communication.common.model.NotificationData;
import com.logistimo.communication.common.model.NotificationLogStatus;
import com.logistimo.communication.common.model.NotificationType;
import com.logistimo.communication.core.actions.AuditAction;
import com.logistimo.communication.core.notifiers.Notifier;
import com.logistimo.communication.core.service.NotificationProcessor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Created by kumargaurav on 07/09/18.
 */
@Component
public class NotificationProcessorImpl implements NotificationProcessor {

  private Set<Notifier> notifiers;

  private AuditAction auditAction;

  @Autowired
  public void setNotifiers(Set<Notifier> notifiers) {
    this.notifiers = notifiers;
  }

  @Autowired
  public void setAuditAction(AuditAction auditAction) {
    this.auditAction = auditAction;
  }

  @Override
  public void process(NotificationData data) {
    boolean isFailoverSupported = data.getFailoverConfigured();
    RetryTemplate retryTemplate = retryTemplate(data.getRetry());
    try {
      if (!isFailoverSupported) {
        retryTemplate.execute(retryContext -> notify(data.getType(), data));
      } else {
        retryTemplate.execute(retryContext -> notify(data.getType(), data),
            retryContext -> notify(data.getFailoverType(), data));
      }
      auditAction.updateAuditLog(data.getId(), NotificationLogStatus.PROCESSED.name());
    } catch (CMBusinessException ex) {
      auditAction.updateAuditLog(data.getId(), NotificationLogStatus.FAILED.name() + " : " + ex.getMessage());
    }
  }

  protected String notify(NotificationType type, NotificationData data) {
    Optional<String> response = notifiers.stream()
        .filter(notifier -> notifier.apply(type))
        .map(notifier -> notifier.execute(data)).findFirst();
    return response.isPresent() ? response.get() : "Success: " + type;
  }

  protected RetryTemplate retryTemplate(int attempts) {

    RetryTemplate retryTemplate = new RetryTemplate();
    //retry policy
    Map<Class<? extends Throwable>, Boolean> retryableExceptions = new HashMap<>();
    retryableExceptions.put(CMBusinessException.class,Boolean.TRUE);
    SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(attempts,retryableExceptions);
    retryTemplate.setRetryPolicy(retryPolicy);
    // back off policy
    ExponentialBackOffPolicy exponentialBackOffPolicy = new ExponentialBackOffPolicy();
    exponentialBackOffPolicy.setInitialInterval(1000);
    exponentialBackOffPolicy.setMultiplier(2.0);
    retryTemplate.setBackOffPolicy(exponentialBackOffPolicy);
    return retryTemplate;
  }
}
