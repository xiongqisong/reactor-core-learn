package com.xqs.reactor_core_learn.publisher;

import reactor.core.publisher.Flux;

public class SinglePush {
	public static void main(String[] args) {
		push_1();
	}

	private static void push_1() {
		Flux<Integer> flux = Flux.push(sink -> {
			for (int i = 0; i < 10; i++) {
				sink.next(i);
			}
		});
		flux.subscribe(i -> System.out.println(i));
	}
}
