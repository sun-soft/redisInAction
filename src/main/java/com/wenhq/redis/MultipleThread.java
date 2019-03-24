package com.wenhq.redis;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import redis.clients.jedis.Jedis;

public class MultipleThread {
	ExecutorService fixedThreadPool = null;  
	public ThreadLocal<Jedis> conn = new ThreadLocal<>();
	
	public MultipleThread(int threadNum) {
		super();
		fixedThreadPool = Executors.newFixedThreadPool(threadNum);
	}

	public void run(Runnable run) {
		fixedThreadPool.execute(run);
	}
	
	public void close() {
		try {
			fixedThreadPool.shutdown();
			fixedThreadPool.awaitTermination(100, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
}
