package com.britz.nio_server_example.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;


public class ClientApp {

	public static void main(String... args) {
		Socket socket = null;
		try {
			socket = new Socket("127.0.0.1", 1337);
			OutputStreamWriter os = new OutputStreamWriter(socket.getOutputStream());
			os.write("Hello World\nHow are you?\r");

			os.flush();

			InputStream is = socket.getInputStream();
			int read = -1;
			while ((read = is.read()) != -1) {
				if ((char) read == '\r') {
					break;
				}
				System.out.print((char) read);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (socket != null) {
				socket.close();
				}
			} catch (IOException e) {
				// Do nothing
			}
		}

	}

}
