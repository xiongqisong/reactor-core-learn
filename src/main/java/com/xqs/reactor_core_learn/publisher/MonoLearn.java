package com.xqs.reactor_core_learn.publisher;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import reactor.core.publisher.Mono;

public class MonoLearn {
	private Mono<Void> exception(){
		throw new IllegalArgumentException();
	}
	static final BiFunction<String, Integer, MonoLearn> cons = MonoLearn::new;
	
	public static void main(String[] args) {
		//MonoLearn obj = MonoLearn.cons.apply("hello", 10);
	}
	
	public MonoLearn(){
		System.out.println("no args");
	}
	
	public MonoLearn(String name, int age){
		System.out.println(name + " " + age);
	}
}
