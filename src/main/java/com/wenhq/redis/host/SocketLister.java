package com.wenhq.redis.host;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketLister {

	public static void main(String[] args) throws IOException {
		try (ServerSocket serverSocket = new ServerSocket(6379);) {
			Socket accept = serverSocket.accept();
			byte[] result = new byte[1024];
			accept.getInputStream().read(result);
			System.out.println(new String(result));
		}
	}
}
