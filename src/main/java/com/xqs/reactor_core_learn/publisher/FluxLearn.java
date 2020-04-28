package com.xqs.reactor_core_learn.publisher;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.reactivestreams.Subscription;

import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

public class FluxLearn {
    public static void main(String[] args) {
        test9();
    }

    // 无处理
    public static void test1() {
        Flux<Integer> ints = Flux.range(1, 3);
        ints.subscribe();
    }

    // 普通处理
    public static void test2() {
        Flux<Integer> ints = Flux.range(1, 3);
        ints.subscribe(i -> System.out.println(i));
    }

    // 普通处理，异常处理
    public static void test3() {
        Flux<Integer> ints = Flux.range(1, 4).map(i -> {
            if (i <= 3)
                return i;
            throw new RuntimeException("Got to 4");
        });
        ints.subscribe(i -> System.out.println(i), error -> System.err.println("Error: " + error));
    }

    // 普通处理，异常处理，处理完毕后附加操作
    public static void test4() {
        Flux<Integer> ints = Flux.range(1, 4);
        ints.subscribe(i -> System.out.println(i), error -> System.err.println("Error: " + error),
                () -> System.out.println("Done"));
    }

    // 普通处理，异常处理，处理完毕后附加操作，对subscribe的结果再度处理（会影响实际的处理过程）
    public static void test5() {
        // 1.如果sub.request(4)，则处理完毕后附加操作会执行
        // 2.如果sub>request(2)，则处理完毕后附加操作不会执行，因为只需要2个元素，流水线就只会处理2次，不会将物料
        // 全部处理完
        Flux<Integer> ints = Flux.range(1, 4);
        ints.subscribe(i -> System.out.println(i), error -> System.err.println("Error: " + error),
                () -> System.out.println("Done"), sub -> sub.request(4));// 调整sub.request(n)的n，观察输出
    }

    public static void test6() {
        Flux.range(1, 10).doOnRequest(r -> System.out.println("request of " + r))
                .subscribe(new BaseSubscriber<Integer>() {
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        request(1);
                    }

                    @Override
                    protected void hookOnNext(Integer value) {
                        System.out.println("Cancelling after having received " + value);
                        cancel();
                    }
                });
    }

    // interval返回无限流，流可以是有限的，也可以是无限的，可以是空的，也可以不是空的
    public static void test7() {
        /*
         * 1.interval是运行在Schedulers.parallel返回线程池上，该线程池默认创建的线程为守护线程，
         * 因此如果main函数结束了 ，处理Flux流内数据的线程也就结束了，就看不到任何输出信息了
         */
        Flux.interval(Duration.ofMillis(200)).subscribe(a -> System.out.println(a));
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
        }

        // 2.
        Flux.interval(Duration.ofMillis(200)).subscribeOn(Schedulers.newParallel("myScheduler"))
                .subscribe(a -> System.out.println(a));
    }

    public static void test8() {
        Flux.generate(() -> 0, (state, sink) -> {
            sink.next("3 x " + state + " = " + 3 * state);
            if (state == 10)
                sink.complete();
            return state + 1;
        });
    }

    public static void test9() {
        final Result t = new Result(0);
        Flux.just("blue", "white", "red").log().map(str -> {
            try {
                Thread.sleep(str.length() * 25);
            } catch (Exception e) {
                System.out.println(Thread.currentThread().getName() + " 超时");
            }
            int result = 0;
            if (str.length() > 3)
                result = 1;
            System.out.println(Thread.currentThread().getName() + " 计算完成");
            return result;
        }).doOnNext(i -> System.out.println(i)).subscribeOn(Schedulers.parallel()).doOnError(error -> System.out.println(error)).blockLast();

//        System.out.println(t.getCount());
    }

    public static void test10() {
        Flux.range(0, 10).doOnNext(i -> System.out.println("count - " + i + " - " + Thread.currentThread()
                + " - number: " + ThreadLocalRandom.current().nextLong())).subscribeOn(Schedulers.parallel())
                .blockLast();
    }

    public static void findMethodOfReturnType(Class clazz) {
        List<Method> collect = Arrays.stream(clazz.getMethods())
                .filter(method -> method.getReturnType().equals(void.class)).collect(Collectors.toList());
        for (Method method : collect) {
            System.out.println(method.toString());
        }
    }
}

class Result {
    private int count;

    public Result(int count) {
        super();
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}