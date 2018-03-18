package com.britz.nio_server_example.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class ClientSession {

	SelectionKey selectionKey;
	SocketChannel socketChannel;
	ByteBuffer byteBuffer;
	Message message = new Message();
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	int cnt = 0;

	ClientSession(SelectionKey selectionKey, SocketChannel socketChannel) {
		this.selectionKey = selectionKey;
		try {
			this.socketChannel = (SocketChannel) socketChannel.configureBlocking(false); // async/non-blocking
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.byteBuffer = ByteBuffer.allocate(10);
	}

	private void disconnect() {
		ServerApp.clientMap.remove(selectionKey);
		try {
			if (selectionKey != null)
				selectionKey.cancel();
			if (socketChannel == null)
				return;
			System.out.println("Closing socket -> " + socketChannel.getRemoteAddress().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void read() {
		if (message == null) {
			message = new Message();
		}
		try {
			int amountRead = -1;

			try {
				amountRead = socketChannel.read((ByteBuffer) byteBuffer.clear());

			} catch (Exception e) {
			}

			if (amountRead == -1)
				disconnect();
			if (amountRead < 1)
				return;
			// System.out.println("Count = " + cnt);
			// System.out.println("Remaining = " + byteBuffer.remaining());
			// System.out.println("Limit = " + byteBuffer.limit());
			baos.write(byteBuffer.array(), 0, byteBuffer.position());
			cnt += byteBuffer.limit();

			// if (message.isComplete()) {
			String msg = baos.toString("UTF-8");
			System.out.println("Message = " + msg);

			// }

			// byteBuffer.flip();
			// socketChannel.write(byteBuffer);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

		}
	}

}
