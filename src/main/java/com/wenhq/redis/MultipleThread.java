package com.wenhq.redis;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import redis.clients.jedis.Jedis;

public class MultipleThread {
	ExecutorService fixedThreadPool = null;
	public ThreadLocal<Jedis> conn = new ThreadLocal<>();
	final CountDownLatch startGate = new CountDownLatch(1);
	private long beginTime =0 ;

	public MultipleThread(int threadNum) {
		super();
		fixedThreadPool = Executors.newFixedThreadPool(threadNum);
	}

	public void run(Runnable run) throws InterruptedException {
		startGate.await();
		fixedThreadPool.execute(run);
	}
	
	public void start() {
		beginTime = new Date().getTime();
		startGate.countDown();
	}

	public void close() {
		try {
			fixedThreadPool.shutdown();
			fixedThreadPool.awaitTermination(100, TimeUnit.MINUTES);
			System.out.println("花费时间:" + (new Date().getTime() - beginTime));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
