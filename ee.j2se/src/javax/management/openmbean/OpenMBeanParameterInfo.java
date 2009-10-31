/*
 * Copyright (c) OSGi Alliance (2001, 2009). All Rights Reserved.
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

package javax.management.openmbean;
public interface OpenMBeanParameterInfo {
	boolean equals(java.lang.Object var0);
	java.lang.Object getDefaultValue();
	java.lang.String getDescription();
	java.util.Set<?> getLegalValues();
	java.lang.Comparable<?> getMaxValue();
	java.lang.Comparable<?> getMinValue();
	java.lang.String getName();
	javax.management.openmbean.OpenType<?> getOpenType();
	boolean hasDefaultValue();
	boolean hasLegalValues();
	boolean hasMaxValue();
	boolean hasMinValue();
	int hashCode();
	boolean isValue(java.lang.Object var0);
	java.lang.String toString();
}

