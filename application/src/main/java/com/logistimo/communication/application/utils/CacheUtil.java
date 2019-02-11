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

package com.logistimo.communication.application.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * Created by kumargaurav on 04/09/18.
 */
@Component
public class CacheUtil {

  private static final String KEY_PREFIX = "notification#";

  @Value("${key.expiry:5}")
  protected long EXPIRY;

  private RedisTemplate<String,String> redisService;

  @Autowired
  public void setRedisService(
      RedisTemplate<String, String> redisService) {
    this.redisService = redisService;
  }

  public void add(String key, String value) {
    String modKey = KEY_PREFIX + key;
    redisService.opsForValue().set(modKey,value);
    redisService.expire(modKey,EXPIRY, TimeUnit.MINUTES);
  }

  public boolean keyExists(String key) {
    String modKey = KEY_PREFIX + key;
    return StringUtils.isEmpty(redisService.opsForValue().get(modKey)) ? false : true;
  }
}

