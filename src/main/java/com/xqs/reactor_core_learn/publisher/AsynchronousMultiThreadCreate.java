package com.xqs.reactor_core_learn.publisher;

import java.util.Arrays;
import java.util.List;

import reactor.core.publisher.Flux;

/**
 * 事件也可以看做是一种数据。
 * 传统的监听器模型在面对1个数据n种消费场景时，需要处理器内部维护监听器列表和线程池，以便于异步地逐个通知监听器处理数据。
 * 采用Flux.create并不会让你不需要做这些工作，除非你打算用Flux重新实现以便，那可以简化的部分是维护监听器列表和线程池。
 * Flux.create只是可以听过桥接1个特殊的监听器来达到把处理器的事件转成Flux。
 * @author 11085076
 *
 */
public class AsynchronousMultiThreadCreate {
	// 定义不同事件发生时的处理逻辑及何时发出什么事件
	public static MyEventListener<Object> listener1 = new MyEventListener<Object>() {
		@Override
		public void onDataChunk(List<Object> chunk) {
			// 接收到数据块事件
			System.out.println("get data: " + chunk);

			// 发送完成事件
		}

		@Override
		public void processComplete() {
			System.out.println("process complete~");
			// 实际上还需要向processor发出1个complete事件
		}
	};
	public static MyEventProcessor<Object> myEventProcessor = new MyEventProcessor<>();
	public static List<Object> dataChunk = Arrays.asList(1, 2, 3, 4, 5, 6);
	
	public static void main(String[] args) {
		bridgeCreate_1();
	}

	/**
	 * 传统的监听器模式
	 */
	public static void tranditionalListenerPattern(){
		// 将监听器注册到处理器
		myEventProcessor.register(listener1);
		
		// 处理器触发事件，通过内置线程池异步驱动监听器执行处理操作
		myEventProcessor.dataChunk(dataChunk);
		myEventProcessor.processComplete();
		
		// 如果还有其他监听器要处理数据，则需要实现监听器，并把它注册到处理器上
	}
	
	/**
	 * 通过create桥接传统监听器模式
	 */
	public static void bridgeCreate_1() {
		// 实际上就是把处理器的事件转成了Flux，不用再编写格式监听器了，需要处理事件的话订阅Flux就好
		Flux<Object> flux = Flux.create(sink -> {
			myEventProcessor.register(new MyEventListener<Object>() {

				@Override
				public void processComplete() {
					sink.complete();
				}

				@Override
				public void onDataChunk(List<Object> chunk) {
					sink.next(chunk);
				}
			});
		});
		// 如果需要处理事件，订阅后实现处理逻辑就可以了，其实这和传统的监听器模式是类似的，Flux是实现+订阅，传统是实现+注册
		flux.subscribe(o -> System.out.println("subscriber process: " + o),
				null, () -> System.out.println("subscriber process complete"));
		// 还有其他需要处理事件的订阅者，重复上述范式即可
		flux.subscribe(o -> System.out.println("subscriber2 process: " + o),
				null, () -> System.out.println("subscriber2 process complete"));
		myEventProcessor.dataChunk(dataChunk);
		myEventProcessor.processComplete();
	}
}
