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

package com.logistimo.communication.core.actions;

import com.logistimo.communication.common.model.NotificationLogStatus;
import com.logistimo.communication.repo.dao.NotificationLogDAO;
import com.logistimo.communication.repo.entity.NotificationLog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by kumargaurav on 01/10/18.
 */
@Component
public class AuditAction {

  private NotificationLogDAO notificationLogDAO;

  @Autowired
  public void setNotificationLogDAO(
      NotificationLogDAO notificationLogDAO) {
    this.notificationLogDAO = notificationLogDAO;
  }

  public NotificationLog addAuditLog(NotificationLog log) {
    return notificationLogDAO.save(log);
  }

  public void updateAuditLog(String requestId, String status) {
    NotificationLog log = notificationLogDAO.findOne(requestId);
    if (log == null) {
      throw new IllegalArgumentException("Invalid notification log req id");
    }
    log.setStatus(status);
    notificationLogDAO.save(log);
  }
}
