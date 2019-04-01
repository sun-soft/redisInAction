package com.wenhq.redis.host;

import java.util.List;

import org.junit.Test;

import com.wenhq.redis.Config;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

public class TransationTest {
	@Test
	public void testRedis() {
		Jedis jedis = new Jedis(Config.host, 6379);
		String list = "tran:";
		addItem(jedis, list);
		
	}

	public boolean addItem(Jedis conn, String list) {
		long end = System.currentTimeMillis() + 5000;

		while (System.currentTimeMillis() < end) {
			conn.watch(list);
			if (conn.scard(list) > 5) {
				conn.unwatch();
				return false;
			}

			Transaction trans = conn.multi();
			for(int i = 0 ;i<3; i++) {
				trans.sadd(list, Math.random() * 100 + "===" + i);
			}
			Response<Long> pop = trans.append(list, "error");	//出错，但是redis会继续执行
			trans.sadd(list, "last item");
			List<Object> results = trans.exec();
			System.out.println("pop = " + pop.get());
			if (results == null) {
				System.err.println("watch  错误");
				continue;
			}
			return true;
		}
		return false;
	}
}
