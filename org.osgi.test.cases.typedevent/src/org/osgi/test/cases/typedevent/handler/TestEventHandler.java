/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.test.cases.typedevent.handler;

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

	public Map<String,List<T>>					typedStore		= new HashMap<>();
	public Map<String,List<Map<String,Object>>>	untypedStore	= new HashMap<>();
	public Map<String,List<Map<String,Object>>>	unhandledStore	= new HashMap<>();

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
