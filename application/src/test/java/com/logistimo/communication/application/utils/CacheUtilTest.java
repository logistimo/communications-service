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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

/**
 * Created by kumargaurav on 10/10/18.
 */
public class CacheUtilTest {

  private RedisTemplate<String,String> redisService;

  private ValueOperations valueOperation;

  CacheUtil cacheUtil;

  @Before
  public void setUp() {
    redisService = Mockito.mock(RedisTemplate.class);
    valueOperation = Mockito.mock(ValueOperations.class);
    cacheUtil = new CacheUtil();
    cacheUtil.setRedisService(redisService);
    cacheUtil.EXPIRY = 5;
  }

  @Test
  public void testAdd() {
    String v = "val";
    Mockito.doNothing().when(valueOperation).set("notification#ent1", v);
    Mockito.when(redisService.opsForValue()).thenReturn(valueOperation);
    cacheUtil.add("ent1",v);
  }


  @Test
  public void testKeyExists() {
    String v = "val";
    Mockito.doNothing().when(valueOperation).set("notification#ent1", v);
    Mockito.when(redisService.opsForValue()).thenReturn(valueOperation);
    Assert.assertNotNull(cacheUtil.keyExists("notification#ent1"));
  }
}
