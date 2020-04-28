package com.xqs.reactor_core_learn.asynchronous.api;

public class Producer {
	public static void main(String[] args) {
		Listener<Integer> l = new IntegerListener<>();
		Integer data = createData();
		l.onData(data);
	}

	private static Integer createData() {
		return 1;
	}
}
