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
		jc.set("foo", "bar");
		String value = jc.get("foo");
		System.out.print(value);
	}

}
