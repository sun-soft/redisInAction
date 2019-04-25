package com.wenhq.redis.host;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.Test;

import com.wenhq.redis.Config;
import com.wenhq.redis.MultipleThread;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.params.SetParams;

public class LockTest {

	@Test
	public void testWithoutLock() throws InterruptedException {
		String key = "nolocka";
		MultipleThread t = new MultipleThread(10);
		setConn(t);
		t.conn.get().set(key,"0");
		for (int i = 0; i < 1000; i++) {
			t.run(() -> {
				setConn(t);
				Integer value = Integer.valueOf(t.conn.get().get(key)) + 1;
				System.out.println(Thread.currentThread().getName() + " value = " + value);
				t.conn.get().set(key, value.toString());
//				t.conn.get().incr(key);
			});
		}
		t.start();
		String result = t.conn.get().get(key);
		System.out.println("testWithoutLock get = " + result);
	}
	
	@Test
	public void testWithLock() throws InterruptedException {
		String key = "lockb";
		MultipleThread t = new MultipleThread(10);
		setConn(t);
		t.conn.get().set(key,"0");
		for (int i = 0; i < 1000; i++) {
			t.run(() -> {
				setConn(t);
				String lockId = acquireLockWithTimeout(t.conn.get(), key, 1000, 1000);
				if (lockId == null) {
					System.err.println("获取锁失败");
					return;
				}
				try {
					Integer value = Integer.valueOf(t.conn.get().get(key)) + 1;
					System.out.println(Thread.currentThread().getName() + " value = " + value);
					t.conn.get().set(key, value.toString());
	//				t.conn.get().incr(key);
				}finally {
//					releaseLock(t.conn.get(), key, lockId);
					releaseLock2(t.conn.get(), key, lockId);
				}
			});
		}
		t.start();
		String result = t.conn.get().get(key);
		System.out.println("testWithLock get = " + result);
	}

	private void setConn(MultipleThread t) {
		if (t.conn.get() == null) {
			Jedis conn = new Jedis(Config.host, Config.port);
			conn.select(1);
			t.conn.set(conn);
		}
	}
	public String acquireLockWithTimeout(Jedis conn, String lockName, long acquireTimeout, long lockTimeout) {
		String identifier = UUID.randomUUID().toString();
		String lockKey = "lock:" + lockName;

		long end = System.currentTimeMillis() + acquireTimeout;
		while (System.currentTimeMillis() < end) {
			String result = conn.set(lockKey,identifier, SetParams.setParams().nx().px(lockTimeout));
			if ("OK".equals(result)) {
				return identifier;
			}
			try {
				Thread.sleep(1);
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
			}
		}

		// null indicates that the lock was not acquired
		return null;
	}
	
	public String acquireLockWithTimeout2(Jedis conn, String lockName, long acquireTimeout, long lockTimeout) {
		String identifier = UUID.randomUUID().toString();
		String lockKey = "lock:" + lockName;
		int lockExpire = (int) (lockTimeout / 1000);

		long end = System.currentTimeMillis() + acquireTimeout;
		while (System.currentTimeMillis() < end) {
			if (conn.setnx(lockKey, identifier) == 1) {
				conn.expire(lockKey, lockExpire);
				return identifier;
			}
			if (conn.ttl(lockKey) == -1) {
				conn.expire(lockKey, lockExpire);
			}

			try {
				Thread.sleep(1);
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
			}
		}

		// null indicates that the lock was not acquired
		return null;
	}

	public boolean releaseLock(Jedis conn, String lockName, String identifier) {
		String lockKey = "lock:" + lockName;

		while (true) {
			conn.watch(lockKey);
			if (identifier.equals(conn.get(lockKey))) {
				Transaction trans = conn.multi();
				trans.del(lockKey);
				List<Object> results = trans.exec();
				if (results == null) {
					continue;
				}
				return true;
			}

			conn.unwatch();
			break;
		}

		return false;
	}
	
    /**
     * 释放分布式锁
     * @param jedis Redis客户端
     * @param lockKey 锁
     * @param identifier 请求标识
     * @return 是否释放成功
     */
    public static boolean releaseLock2(Jedis jedis, String lockName, String identifier) {
		String lockKey = "lock:" + lockName;
       String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(identifier));

        if ("1".equals(result)) {
            return true;
        }
        return false;

    }
}
