package com.xqs.reactor_core_learn.publisher;

import reactor.core.publisher.Flux;

public class AsynchronousCreate {
	public static void main(String[] args) {
		create_1();
	}

	private static void create_1() {
		Flux<Integer> flux = Flux.<Integer>create((sink)->{
			for (int i = 0; i < 10; i++) {
				sink.next(i);
			}
		});
		flux.subscribe(i->System.out.println(i));
	}
}
