package com.wenhq.redis.host;

import java.util.List;
import java.util.UUID;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

public class LockTest {
	
	
	
	public String acquireLockWithTimeout(Jedis conn, String lockName, long acquireTimeout, long lockTimeout) {
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
}
