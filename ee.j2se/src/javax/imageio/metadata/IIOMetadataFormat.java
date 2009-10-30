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

package javax.imageio.metadata;
public interface IIOMetadataFormat {
	public final static int CHILD_POLICY_ALL = 1;
	public final static int CHILD_POLICY_CHOICE = 3;
	public final static int CHILD_POLICY_EMPTY = 0;
	public final static int CHILD_POLICY_MAX = 5;
	public final static int CHILD_POLICY_REPEAT = 5;
	public final static int CHILD_POLICY_SEQUENCE = 4;
	public final static int CHILD_POLICY_SOME = 2;
	public final static int DATATYPE_BOOLEAN = 1;
	public final static int DATATYPE_DOUBLE = 4;
	public final static int DATATYPE_FLOAT = 3;
	public final static int DATATYPE_INTEGER = 2;
	public final static int DATATYPE_STRING = 0;
	public final static int VALUE_ARBITRARY = 1;
	public final static int VALUE_ENUMERATION = 16;
	public final static int VALUE_LIST = 32;
	public final static int VALUE_NONE = 0;
	public final static int VALUE_RANGE = 2;
	public final static int VALUE_RANGE_MAX_INCLUSIVE = 10;
	public final static int VALUE_RANGE_MAX_INCLUSIVE_MASK = 8;
	public final static int VALUE_RANGE_MIN_INCLUSIVE = 6;
	public final static int VALUE_RANGE_MIN_INCLUSIVE_MASK = 4;
	public final static int VALUE_RANGE_MIN_MAX_INCLUSIVE = 14;
	boolean canNodeAppear(java.lang.String var0, javax.imageio.ImageTypeSpecifier var1);
	int getAttributeDataType(java.lang.String var0, java.lang.String var1);
	java.lang.String getAttributeDefaultValue(java.lang.String var0, java.lang.String var1);
	java.lang.String getAttributeDescription(java.lang.String var0, java.lang.String var1, java.util.Locale var2);
	java.lang.String[] getAttributeEnumerations(java.lang.String var0, java.lang.String var1);
	int getAttributeListMaxLength(java.lang.String var0, java.lang.String var1);
	int getAttributeListMinLength(java.lang.String var0, java.lang.String var1);
	java.lang.String getAttributeMaxValue(java.lang.String var0, java.lang.String var1);
	java.lang.String getAttributeMinValue(java.lang.String var0, java.lang.String var1);
	java.lang.String[] getAttributeNames(java.lang.String var0);
	int getAttributeValueType(java.lang.String var0, java.lang.String var1);
	java.lang.String[] getChildNames(java.lang.String var0);
	int getChildPolicy(java.lang.String var0);
	java.lang.String getElementDescription(java.lang.String var0, java.util.Locale var1);
	int getElementMaxChildren(java.lang.String var0);
	int getElementMinChildren(java.lang.String var0);
	int getObjectArrayMaxLength(java.lang.String var0);
	int getObjectArrayMinLength(java.lang.String var0);
	java.lang.Class<?> getObjectClass(java.lang.String var0);
	java.lang.Object getObjectDefaultValue(java.lang.String var0);
	java.lang.Object[] getObjectEnumerations(java.lang.String var0);
	java.lang.Comparable<?> getObjectMaxValue(java.lang.String var0);
	java.lang.Comparable<?> getObjectMinValue(java.lang.String var0);
	int getObjectValueType(java.lang.String var0);
	java.lang.String getRootName();
	boolean isAttributeRequired(java.lang.String var0, java.lang.String var1);
}

