package com.xqs.reactor_core_learn;

import java.util.concurrent.atomic.AtomicLong;

import reactor.core.publisher.Flux;

public class SynchronousGenerate {
    public static void main(String[] args) {
        test2();
    }

    public static void test1() {
        Flux<String> flux = Flux.generate(() -> 0, (state, sink) -> {
            sink.next("3 x " + state + " = " + 3 * state);
            if (state == 10)
                sink.complete();
            return state + 1;
        });
        flux.subscribe(r -> System.out.println(r));
    }

    public static void test2() {
        Flux<String> flux = Flux.generate(AtomicLong::new, (state, sink) -> {
            long i = state.getAndIncrement();
            sink.next("3 x " + i + " = " + 3 * i);
            if (i == 10)
                sink.complete();
            return state;
        });
        flux.subscribe(i -> System.out.println(i));
    }

    public static void test3() {
        Flux<String> flux = Flux.generate(AtomicLong::new, (state, sink) -> {
            long i = state.getAndIncrement();
            sink.next("3 x " + i + " = " + 3 * i);
            if (i == 10)
                sink.complete();
            return state;
        }, (state) -> System.out.println("state: " + state));
        flux.subscribe(i -> System.out.println(i));
    }
}
