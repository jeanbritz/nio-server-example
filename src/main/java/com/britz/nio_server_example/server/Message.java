package com.britz.nio_server_example.server;

public class Message {
	StringBuilder msg = new StringBuilder();

	public Message() {

	}

	public StringBuilder getMessageHolder() {
		return msg;
	}

	public boolean isComplete() {
		return msg.lastIndexOf("\r") == msg.length();
	}

	@Override
	public String toString() {
		return msg.toString();
	}
}
