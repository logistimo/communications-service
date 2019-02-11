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

import com.logistimo.communication.repo.dao.NotificationLogDAO;
import com.logistimo.communication.repo.entity.NotificationLog;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Created by kumargaurav on 09/10/18.
 */

public class AuditActionTest {

  AuditAction auditAction;

  private NotificationLogDAO notificationLogDAO;

  @Before
  public void setUp () {
    auditAction = new AuditAction();
    notificationLogDAO = Mockito.mock(NotificationLogDAO.class);
    auditAction.setNotificationLogDAO(notificationLogDAO);
  }

  @Test
  public void testAuditAction() {
    Mockito.when(notificationLogDAO.save(Mockito.<NotificationLog>any())).thenReturn(buildResponse());
    NotificationLog log = auditAction.addAuditLog(new NotificationLog());
    Assert.assertNotNull(log.getUid());
  }


  @Test
  public void testUpdateAuditLog() {
    NotificationLog log = buildResponse();
    Mockito.when(notificationLogDAO.findOne(Mockito.<String>any())).thenReturn(log);
    Mockito.when(notificationLogDAO.save(Mockito.<NotificationLog>any())).thenReturn(buildUpdateResponse(log));
    auditAction.updateAuditLog("id11","PROCESSED");
    Assert.assertNotNull(log.getStatus());
  }

  private NotificationLog buildResponse() {
    NotificationLog log = new NotificationLog();
    log.setUid("id11");
    return log;
  }

  private NotificationLog buildUpdateResponse(NotificationLog log) {
    log.setStatus("PROCESSED");
    return log;
  }
}
