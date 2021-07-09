package org.osgi.test.cases.typedevent.junit;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.osgi.service.typedevent.TypedEventHandler;
import org.osgi.service.typedevent.UnhandledEventHandler;
import org.osgi.service.typedevent.UntypedEventHandler;

public class TestEventHandler<T> implements TypedEventHandler<T>,
		UnhandledEventHandler, UntypedEventHandler {
	String name;

	public TestEventHandler() {
		this("unset-" + UUID.randomUUID());
	}

	public TestEventHandler(String name) {
		this.name = name;
	}

	Map<String,List<T>>						typedStore		= new HashMap<>();
	Map<String,List<Map<String,Object>>>	untypedStore	= new HashMap<>();
	Map<String,List<Map<String,Object>>>	unhandledStore	= new HashMap<>();

	@Override
	public void notify(String topic, T event) {

		System.out.printf("[%s] typed: %s :: %s %n", name, topic, event);
		List<T> list = typedStore.computeIfAbsent(topic,
				(k) -> new LinkedList<>());
		list.add(event);

	}

	@Override
	public void notifyUntyped(String topic, Map<String,Object> event) {
		System.out.printf("[%s] untyped: %s :: %s %n", name, topic, event);
		List<Map<String,Object>> list = untypedStore.computeIfAbsent(topic,
				(k) -> new LinkedList<>());
		list.add(event);

	}

	@Override
	public void notifyUnhandled(String topic, Map<String,Object> event) {
		System.out.printf("[%s] undhandled: %s :: %s %n", name, topic, event);
		List<Map<String,Object>> list = unhandledStore.computeIfAbsent(topic,
				(k) -> new LinkedList<>());
		list.add(event);

	}

}
