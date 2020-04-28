package com.xqs.reactor_core_learn.asynchronous.api;

public interface Listener<T> {
	void onData(T data);
	void register();
}
