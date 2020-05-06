package com.xqs.reactor_core_learn.publisher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MyEventProcessor<T> {
	List<MyEventListener> listeners = new ArrayList<>();
	ScheduledExecutorService pool = Executors
			.newSingleThreadScheduledExecutor();

	void register(MyEventListener<T> eventListener) {
		listeners.add(eventListener);
	}

	void dataChunk(List<T> dataChunk) {
		for (MyEventListener i : listeners) {
			pool.schedule(() -> i.onDataChunk(dataChunk), 500,
					TimeUnit.MILLISECONDS);
		}
	}

	void processComplete() {
		for (MyEventListener i : listeners) {
			pool.schedule(() -> i.processComplete(), 500,
					TimeUnit.MILLISECONDS);
		}
	}
}
