/*
 * Copyright (c) OSGi Alliance (2009). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.thunk.framework;

import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.wrapped.framework.TFilter;

public class FilterImpl implements Filter {
	final TFilter	filter;

	FilterImpl(TFilter filter) {
		this.filter = filter;
	}

	public boolean match(ServiceReference reference) {
		return filter.match(T.unwrap(reference));
	}

	public boolean match(Map<String, Object> dictionary) {
		return filter.match(new Hashtable<String, Object>(dictionary));
	}

	public boolean matchCase(Map<String, Object> dictionary) {
		return filter.matchCase(new Hashtable<String, Object>(dictionary));
	}
	
	@Override
	public boolean equals(Object o) {
		return filter.equals(T.unwrap((Filter) o));
	}

	@Override
	public int hashCode() {
		return filter.hashCode();
	}

	@Override
	public String toString() {
		return filter.toString();
	}

}
