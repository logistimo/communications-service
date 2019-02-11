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

package com.logistimo.communication.repo.entity;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;

/**
 * Created by kumargaurav on 24/09/18.
 */
@Entity
@Table(name = "NOTIFICATIONLOG")
@AttributeOverrides({
    @AttributeOverride(name = "uid",column = @Column(name = "MSGID"))
})
@Data
public class NotificationLog extends BaseEntity {

  @Column(name = "MSGID",insertable=false, updatable=false)
  private String msgId;

  @Column(name = "APPID", updatable=false)
  private String appId;

  @Column(name = "APPNAME", updatable=false)
  private String appName;

  @Column(name = "STATUS")
  private String status;

  @Column(name = "NOTIFICATIONTYPE")
  private String notificationType;

  @Column(name = "MESSAGE")
  private String message;

}

