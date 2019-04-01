package com.wenhq.redis.host;

import org.junit.Test;

import com.wenhq.redis.Config;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

public class PipelineTest {
	@Test
	public void testRedis() {
		System.out.println();
		long start = System.currentTimeMillis();
		usePipeline();
		long end = System.currentTimeMillis();
		System.out.println("usePipeline:" + (end - start));

		start = System.currentTimeMillis();
		withoutPipeline();
		end = System.currentTimeMillis();
		System.out.println("withoutPipeline:" + (end - start));
	}
	@Test
	public void readPipeline() {
		try {
			Jedis jedis = new Jedis(Config.host, 6379);
			Pipeline pipeline = jedis.pipelined();
	        Response<String> test1 = pipeline.get("test1");
	        Response<String> test2 = pipeline.get("test2");
//			System.out.println("test1 = " + test1.get());
			pipeline.sync();
			System.out.println("test1 = " + test1.get());
			System.out.println("test2 = " + test2.get());
			jedis.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void withoutPipeline() {
		try {
			Jedis jedis = new Jedis(Config.host, 6379);
			for (int i = 0; i < 10000; i++) {
				jedis.incr("test2");
			}
			jedis.disconnect();
		} catch (Exception e) {
		}
	}

	private void usePipeline() {
		try {
			Jedis jedis = new Jedis(Config.host, 6379);
			Pipeline pipeline = jedis.pipelined();
			for (int i = 0; i < 10000; i++) {
				pipeline.incr("test2");
			}
			pipeline.sync();
			jedis.disconnect();
		} catch (Exception e) {
		}
	}
}
