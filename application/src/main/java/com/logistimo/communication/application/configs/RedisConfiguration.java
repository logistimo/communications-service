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

package com.logistimo.communication.application.configs;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.logistimo.communication.application.configs.condition.SentinelCondition;
import com.logistimo.communication.application.configs.condition.StandaloneCondition;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

/**
 * Created by kumargaurav on 06/03/17.
 */
@Configuration
public class RedisConfiguration extends CachingConfigurerSupport {

  private static final String
      REDIS_SENTINEL_MASTER_CONFIG_PROPERTY =
      "spring.redis.sentinel.master";
  private static final String REDIS_SENTINEL_NODES_CONFIG_PROPERTY = "spring.redis.sentinel.nodes";

  @Resource
  Environment env;

  @Value("${app.issentinel:false}")
  private Boolean isSentinel;

  public static ObjectMapper createRedisObjectmapper() {
    return new ObjectMapper()
        .enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY)//\\
        .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
        .configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true)
        .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
        .configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false)
        .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
        .configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, false)
        .configure(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY, false)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS, false)
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false) //\\
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
  }

  @Bean
  @Conditional(StandaloneCondition.class)
  public JedisConnectionFactory jedisConnectionFactory () {
    JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
    jedisConnectionFactory.setHostName(env.getProperty("app.redis.host"));
    jedisConnectionFactory.setPort(Integer.valueOf(env.getProperty("app.redis.port")));
    return jedisConnectionFactory;
  }

  @Bean
  @Conditional(SentinelCondition.class)
  public JedisConnectionFactory sentinelJedisConnectionFactory() {
    JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(sentinelConfig());
    jedisConnectionFactory.setUsePool(true);
    return jedisConnectionFactory;
  }

  @Bean
  @Conditional(SentinelCondition.class)
  public RedisSentinelConfiguration sentinelConfig() {

    String master = env.getProperty("app.redis.sentinel.master");
    String node = env.getProperty("app.redis.sentinel.nodes");
    String[] nodes = node.split(",");
    Set<String> nodeset = new HashSet<>(Arrays.asList(nodes));
    MapPropertySource
        propertySource =
        new MapPropertySource("RedisSentinelConfiguration", asMap(master, nodeset));
    RedisSentinelConfiguration
        SENTINEL_CONFIG =
        new RedisSentinelConfiguration(propertySource);
    return SENTINEL_CONFIG;
  }

  @Bean
  public RedisTemplate<Object,Object> redisTemplate  () {
    RedisTemplate redisTemplate = new StringRedisTemplate();
    if (isSentinel) {
      redisTemplate.setConnectionFactory(sentinelJedisConnectionFactory());
    } else {
      redisTemplate.setConnectionFactory(jedisConnectionFactory());
    }
    redisTemplate.setExposeConnection(true);
    redisTemplate
        .setValueSerializer(new GenericJackson2JsonRedisSerializer(createRedisObjectmapper()));
    return redisTemplate;
  }


  private Map<String, Object> asMap(String master, Set<String> sentinelHostAndPorts) {
    Map<String, Object> map = new HashMap<>();
    map.put(REDIS_SENTINEL_MASTER_CONFIG_PROPERTY, master);
    map.put(REDIS_SENTINEL_NODES_CONFIG_PROPERTY,
        StringUtils.collectionToCommaDelimitedString(sentinelHostAndPorts));
    return map;
  }

}
