package com.wenhq.redis.host;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.wenhq.redis.Config;

import redis.clients.jedis.Jedis;

/** 速度测试
 * @author wenc
 *
 */
public class SpeedTest {
	
	@Test
	public void test() throws InterruptedException {
		this.testMemory();
		this.testRedis();
	}

	@Test
	public void testMemory() throws InterruptedException {
		Map<String, String> uMap = new HashMap<>();
		uMap.put("a", "a1");
		
		long beginTime = new Date().getTime() ;
		int count = 123456789;
		for(int i = 0;i < count;i++) {
			uMap.get("a");
		}
		System.out.println("testMemory 花费时间:" + (new Date().getTime() - beginTime) + " / 次数" + count);
		
	}
	
	@Test
	public void testRedis() throws InterruptedException {
		try (Jedis conn = new Jedis(Config.host, Config.port);) {
			conn.set("a", "a1");
			long beginTime = new Date().getTime() ;
			int count = 12345;
			for(int i = 0;i < count;i++) {
				conn.get("a");
			}
			System.out.println("testRedis 花费时间:" + (new Date().getTime() - beginTime) + " / 次数" + count);
		}

		
	}
}
