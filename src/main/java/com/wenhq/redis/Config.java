package com.wenhq.redis;

import org.junit.Before;

import redis.clients.jedis.Jedis;

public class Config {
	public final static String host = "192.168.88.136";
	public final static int port = 6379;
	
	public Jedis conn ;
	
	@Before
	public void before() {
		conn = new Jedis(Config.host, Config.port);
		conn.select(1);

	}
}
