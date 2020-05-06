package com.xqs.reactor_core_learn.publisher;

import java.util.List;

public interface MyEventListener<T> {
	/**
	 * 数据块到来事件处理
	 * @param chunk
	 */
	void onDataChunk(List<T> chunk);
	
	/**
	 * 发出数据处理完毕事件
	 */
	void processComplete();
}
