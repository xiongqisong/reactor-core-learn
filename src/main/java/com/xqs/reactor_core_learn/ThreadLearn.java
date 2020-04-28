package com.xqs.reactor_core_learn;

import reactor.core.publisher.Mono;

public class ThreadLearn {
    public static void main(String[] args) throws InterruptedException {
    }

    private static void test1() throws InterruptedException {
        final Mono<String> mono = Mono.just("hello ");
        new Thread(() -> mono.map(msg -> msg + "thread ")
                .subscribe(v -> System.out.println(v + Thread.currentThread().getName()))).start();
    }

}
