package com.xqs.reactor_core_learn.publisher;

import java.util.concurrent.atomic.AtomicLong;

import reactor.core.publisher.Flux;

public class SynchronousGenerate {
    public static void main(String[] args) {
        generate_3();
    }

    /*
     * 可以使用初始值加Bi函数来程序化的创建Flux
     * 不同于使用Flux的工厂方法创建的Flux，下面演示的是通过程序生成1个Flux，而不是使用固定值来生成Flux
     * 说明Flux生产元素的方式是可编程的
     */
    public static void generate_1() {
    	// ()->0生产了初始值0，(state, sink)->{}是一个Bi函数，函数内部处理初始值，并通过sink.next向Subscriber发射元素，且每次产生1个新的初始值供下次生成新元素给Subscriber
        Flux<String> flux = Flux.generate(() -> 0, (state, sink) -> {
            // 定义本次发射的元素
        	sink.next("3 x " + state + " = " + 3 * state);
            
            // 定义下次循环的初始值
            if (state == 10)
                sink.complete();
            return state + 1;
        });
        // 当有订阅时，Flux才会开始生产元素
        flux.subscribe(r -> System.out.println(r));
        
        /*
         *  其实Flux.subscribe是创建了一个Subscription，它包含了Subscriber和Flux，
         *  Flux.subscribe其实会去调用Subscriber.onSubscribe方法，该方法需要传入Subscription
         *  Subscriber其实是通过调用Subscription.request来触发Flux生成元素，如果是无界请求，则会走Subscription.fastPath
         *  由于是无界请求，所以fastPath会运行在一个死循环中，其中就会通过Bi函数生成元素，由于Subscription本身也是一个sink，如果在Bi函数内部使用sink.next的话，会调用Subscriber.onNext
         *  如此一来就实现了Flux定义生成元素的过程，即创建阶段；Subscriber定义消费元素的过程，及消费阶段；而真正实现lazy运算的关键点在于Subscription，其实它是夹在Flux和Subscriber两者之间的
         *  Subscription真正完成了生成元素和触发下游消费元素两项工作，将整个流串接起来
         */
    }

    /*
     * 可以使用普通对象来程序化的创建Flux
     * 
     */
    public static void generate_2() {
        Flux<String> flux = Flux.generate(AtomicLong::new, (state, sink) -> {
            long i = state.getAndIncrement();
            sink.next("3 x " + i + " = " + 3 * i);
            if (i == 10)
                sink.complete();
            return state;
        });
        flux.subscribe(i -> System.out.println(i));
    }

    public static void generate_3() {
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
