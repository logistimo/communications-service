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

package com.logistimo.communication.core.notifiers;

import com.logistimo.communication.common.model.NotificationData;
import com.logistimo.communication.common.model.NotificationType;
import com.logistimo.communication.core.service.PushService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by kumargaurav on 06/07/18.
 */
@Component
public class PushNotifier extends Notifier {

  private PushService pushService;

  @Autowired
  public void setPushService(PushService pushService) {
    this.pushService = pushService;
  }

  @Override
  public boolean apply(NotificationType channel) {
    return NotificationType.PUSH.equals(channel);
  }

  @Override
  protected void process(NotificationData data) {
    pushService.send(data.getNotificationRequest());
  }

}
