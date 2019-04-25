package com.wenhq.redis.host;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.wenhq.redis.Config;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

public class JedisSentinelTest {
	public static void main(String[] args) throws Exception {
		Set<String> sentinels = new HashSet<String>();
		sentinels.add(Config.host + ":5000");

		JedisSentinelPool jedisSentinelPool = new JedisSentinelPool("mymaster", sentinels);
		Jedis jedis = null;
		while (true) {
			Thread.sleep(1000);

			try {
				jedis = jedisSentinelPool.getResource();
				System.out.println("端口:" + jedis.getClient().getPort());
				Date now = new Date();
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				String format_now = dateFormat.format(now);

				jedis.set("hello2", "world");
				String value = jedis.get("hello");
				System.out.println(format_now + ' ' + value);

			} catch (Exception e) {
				System.out.println(e);
			} finally {
				if (jedis != null)
					try {
						jedis.close();
					} catch (Exception e) {
						System.out.println(e);
					}
			}
		}
	}
}
