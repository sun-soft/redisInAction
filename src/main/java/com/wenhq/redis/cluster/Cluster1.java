package com.wenhq.redis.cluster;

import java.util.HashSet;
import java.util.Set;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

public class Cluster1 {

	public static void main(String[] args) {
		Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
		//Jedis Cluster will attempt to discover cluster nodes automatically
		jedisClusterNodes.add(new HostAndPort("192.168.88.136", 7010));
		jedisClusterNodes.add(new HostAndPort("192.168.88.136", 7011));
		jedisClusterNodes.add(new HostAndPort("192.168.88.136", 7012));
		jedisClusterNodes.add(new HostAndPort("192.168.88.136", 7013));
		jedisClusterNodes.add(new HostAndPort("192.168.88.136", 7014));
		jedisClusterNodes.add(new HostAndPort("192.168.88.136", 7015));
		JedisCluster jc = new JedisCluster(jedisClusterNodes);
		jc.del("cluster:*");
		for(int i = 0; i< 10; i++) {
			String key = "{cluster}:" + i; 		//只对字符串内部的内容进行散列
			String value = String.valueOf(i);
			jc.set(key, value);
			if (!value.equals(jc.get(key))) {
				System.out.println("查找错误:" + i);
			}
		}
		jc.set("cluster", "bar");
		String value = jc.get("cluster");
		System.out.print(value);
	}

}
