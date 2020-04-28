package com.xqs.reactor_core_learn.scheduler;

import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

public class SchedulerLearn {
    public static void main(String[] args) {
        test3();
    }

    /**
     * 2 map在 5 创建的匿名线程中执行 4 map在 1 创建的Scheduler中取出的Worker线程中执行
     */
    private static void test1() {
        Scheduler s = Schedulers.newParallel("parallel-scheduler", 4);// 1

        final Flux<String> flux = Flux.range(1, 2).map(i -> {// 2
            System.out.println(Thread.currentThread().getName() + ": map1");
            return 10 + i;
        }).publishOn(s)// 3
                .map(i -> {// 4
                    return "value " + i;
                });

        new Thread(() -> {
            System.out.println(Thread.currentThread().getName());
            flux.subscribe(r -> System.out.println(Thread.currentThread().getName() + ": " + r));// 5
        }).start();
    }

    private static void test2() {
        Scheduler pool = Schedulers.newParallel("my-pool", 4);

        final Flux<Integer> flux = Flux.range(1, 1).publishOn(pool).map(i -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return i + 10;
        }).map(x -> {
            System.out.println(Thread.currentThread().getName() + " 调用Mt成功，回调操作，更新plan的状态: " + x);
            return x;
        });

        final Flux<Integer> flux2 = Flux.range(2, 1).publishOn(pool).map(i -> {
            try {
                TimeUnit.SECONDS.sleep(1);
                System.out.println(Thread.currentThread().getName() + " 调用Mt成功，更新plan的状态: " + i);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return i + 10;
        });
        flux.subscribe();
        flux2.subscribe();
        System.out.println("主线程执行业务结束");

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        pool.dispose();
        System.out.println("线程池状态：" + (pool.isDisposed() ? "已关闭" : "未关闭"));

    }

    public static void test3() {
        Flux<String> flux = Flux.generate(() -> 0, (state, sink) -> {
            sink.next("3 x " + state + " = " + 3 * state);
            if (state == 10)
                sink.complete();
            return state + 1;
        }, System.out::println);
        flux.subscribe(data->System.out.println(data));
    }

    public static void test4() {
        final BlockingQueue<String> queue = new ArrayBlockingQueue<>(100);

        // 生产者，监听输入流，有文字输入则丢到队列中
        Thread producer = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String data = scanner.nextLine();
                try {
                    queue.put(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        producer.start();

        // 从阻塞队列取出对象打印
//        Flux.generate(()->queue, (state,sink)->{
//        });
        Flux.create(sink -> {
            Thread consumer = new Thread(() -> {
                while (true) {
                    String data;
                    try {
                        data = queue.take();
                        System.out.println("数据：" + data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            consumer.start();
        }).blockLast();
    }
}
