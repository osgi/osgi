/**
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
 */

package org.osgi.test.cases.cdi.beans;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;

import org.osgi.test.cases.cdi.interfaces.BeanService;
import org.osgi.test.cases.cdi.interfaces.CdiEventObserverQualifier;

@CdiEventObserverQualifier
@ApplicationScoped
public class CdiEventObserver implements BeanService<List<Object>> {

	@Override
	public String doSomething() {
		return this.toString();
	}

	@Override
	public List<Object> get() {
		return events;
	}

	public void onAnyDocumentEvent(@Observes @Any Object event) {
		events.add(event);
	}

	private final List<Object> events = new CopyOnWriteArrayList<>();

}
