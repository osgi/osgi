/*
 * Copyright (c) OSGi Alliance (2013, 2020). All Rights Reserved.
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

package org.osgi.service.dal.functions.data;

final class Comparator {

	static int compare(int thisValue, int otherValue) {
		return (thisValue < otherValue) ? -1 :
				(thisValue == otherValue) ? 0 : 1;
	}

	static int compare(boolean thisValue, boolean otherValue) {
		return (thisValue == otherValue) ? 0 :
				(thisValue ? 1 : -1);
	}

	static <T extends Comparable< ? super T>> int compare(T thisValue,
			T otherValue) {
		return (null == thisValue) ?
				((null == otherValue) ? 0 : -1) :
				((null == otherValue) ? 1 : thisValue.compareTo(otherValue));
	}

}
