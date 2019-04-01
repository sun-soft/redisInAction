package com.wenhq.redis.host;

import org.junit.Test;

import com.wenhq.redis.Config;

import redis.clients.jedis.Jedis;

public class SpelTest {
	@Test
	public void testHelloWorld() {
		try (Jedis conn = new Jedis(Config.host, Config.port);) {
			// conn.select(1);
			String key = "hello:";
			String value = " world";
			String result = conn.set(key, value);
			conn.expire(key, 10);
			System.out.println("conn set = " + result);
		}
	}
}
