package com.xqs.reactor_core_learn.asynchronous.api;

import java.util.Map;

public class IntegerListener<Integer> implements Listener<Integer>{
	@Override
	public void onData(Integer data) {
		System.out.println("data: " + data);
	}
}
