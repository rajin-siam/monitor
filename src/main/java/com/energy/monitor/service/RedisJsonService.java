package com.energy.monitor.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.UnifiedJedis;
import redis.clients.jedis.json.Path2;

import java.util.List;

@Slf4j
@Service
public class RedisJsonService {

    private final JedisPool jedisPool;

    public RedisJsonService(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public void saveJsonValue(List<Triple<Long, String, String>> triples) {
        if (triples.isEmpty()) return;
        try (var jedis = jedisPool.getResource()) {
            var jedisJson = new UnifiedJedis(jedis.getConnection());
            var path = new Path2("$");
            triples.forEach(triple -> {
                if (triple.getLeft() != 0) {
                    jedis.expire(triple.getMiddle(), triple.getLeft());
                } else {
                    var jsonObject = new JSONObject(triple.getRight());
                    jedisJson.jsonSet(triple.getMiddle(), path, jsonObject);
                }
            });
        } catch (Exception e) {
            log.error("Failed to save JSON data to Redis", e);
            throw new RuntimeException("Redis data not saved", e);
        }
    }

    public void saveJsonBatch(List<Triple<Long, String, String>> triples) {
        if (triples.isEmpty()) return;
        try (var jedis = jedisPool.getResource();
             var pipeline = jedis.pipelined()) {
            var path = new Path2("$");
            triples.forEach(triple -> {
                if (triple.getLeft() != 0) {
                    pipeline.expire(triple.getMiddle(), triple.getLeft());
                } else {
                    var jsonObject = new JSONObject(triple.getRight());
                    pipeline.jsonSet(triple.getMiddle(), path, jsonObject);
                }
            });
            pipeline.sync();
        } catch (Exception e) {
            log.error("Failed to sync batch data: ", e);
        }
    }

    public String getJsonValue(String key) {
        try (var jedis = jedisPool.getResource()) {
            var jedisJson = new UnifiedJedis(jedis.getConnection());
            var path = new Path2("$");
            Object result = jedisJson.jsonGet(key, path);
            return result != null ? result.toString() : null;
        } catch (Exception e) {
            log.error("Failed to get JSON data from Redis for key: {}", key, e);
            return null;
        }
    }

    public void deleteKeysWithPipeline(List<String> keys) {
        try (var jedis = jedisPool.getResource()) {
            var pipeline = jedis.pipelined();
            pipeline.del(keys.toArray(String[]::new));
            pipeline.sync();
        } catch (Exception e) {
            log.error("Failed to delete keys from Redis", e);
            throw new RuntimeException("Redis data not deleted", e);
        }
    }

    public void deleteJsonValueByKey(String key) {
        try (var jedis = jedisPool.getResource()) {
            jedis.del(key);
        } catch (Exception e) {
            log.error("Failed to delete key from Redis: {}", key, e);
            throw new RuntimeException("Redis data not deleted", e);
        }
    }
}