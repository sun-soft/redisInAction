package com.wenhq.redis;

import org.junit.Before;

import redis.clients.jedis.Jedis;

public class Config {
//	public final static String host = "127.0.0.1";
	public final static String host = "docker";
	public final static int port = 6379;
	
	public Jedis conn ;
	
	@Before
	public void before() {
		conn = new Jedis(Config.host, Config.port);
		conn.select(1);

	}
}
