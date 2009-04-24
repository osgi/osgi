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

package org.osgi.impl.wrapped.framework;

import java.util.Dictionary;

import org.osgi.framework.Filter;
import org.osgi.wrapped.framework.TFilter;
import org.osgi.wrapped.framework.TServiceReference;

public class TFilterImpl implements TFilter {
	final Filter	filter;

	TFilterImpl(Filter filter) {
		this.filter = filter;
	}

	public boolean match(TServiceReference reference) {
		return filter.match(T.getWrapped(reference));
	}

	@SuppressWarnings("unchecked")
	public boolean match(Dictionary dictionary) {
		return filter.match(dictionary);
	}

	@SuppressWarnings("unchecked")
	public boolean matchCase(Dictionary dictionary) {
		return filter.matchCase(dictionary);
	}
	
	@Override
	public boolean equals(Object arg0) {
		return filter.equals(arg0);
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
