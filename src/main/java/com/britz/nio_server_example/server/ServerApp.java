package com.britz.nio_server_example.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Hello world!
 *
 */
public class ServerApp 
{
	ServerSocketChannel serverChannel;
	Selector selector;

	static Map<SelectionKey, ClientSession> clientMap = new HashMap<>();

	public ServerApp(InetSocketAddress inetSocketAddress) {
		try {
			serverChannel = ServerSocketChannel.open();
			serverChannel.configureBlocking(false);
			selector = Selector.open();
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
			serverChannel.bind(inetSocketAddress);
			
			Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
				try {
					loop();
				} catch (Throwable t) {
				t.printStackTrace();
				}
			}, 0, 1000, TimeUnit.MILLISECONDS);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loop() throws IOException {
		System.out.println("Waiting for I/O events...");
		selector.select();

		for (SelectionKey key : selector.selectedKeys()) {
			if (!key.isValid())
				continue;

			if (key.isAcceptable()) {
				SocketChannel acceptedChannel = serverChannel.accept();
				if (acceptedChannel == null)
					continue;
				acceptedChannel.configureBlocking(false);
				SelectionKey readKey = acceptedChannel.register(selector, SelectionKey.OP_READ);
				clientMap.put(readKey, new ClientSession(readKey, acceptedChannel));
				System.out.println(
				    "New client [ip: " + acceptedChannel.getRemoteAddress() + ", total_clients: " + clientMap.size() + "]");
			}

			if (key.isReadable()) {
				ClientSession session = clientMap.get(key);
				session.read();
			}
		}

		selector.selectedKeys().clear();
	}

	public static void main(String[] args) {
		new ServerApp(new InetSocketAddress("0.0.0.0", 1337));
	}
}
