package com.xqs.reactor_core_learn.subscriber;

import java.util.concurrent.TimeUnit;

import reactor.core.publisher.Flux;

public class SubscriberLearn {
    public static void main(String[] args) {
        test1();
        System.out.println("main end");
    }

    public static void test1() {
        SampleSubscriber<Integer> ss = new SampleSubscriber<>();
        Flux<Integer> ints = Flux.range(1, 4);
        ints.subscribe(i -> {
            System.out.println(i);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, error -> System.err.println("Error: " + error), () -> {
            System.out.println("Done");
        }, s -> s.request(2));
    }
}
