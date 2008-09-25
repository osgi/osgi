/*
 * $Revision$
 *
 * Copyright (c) OSGi Alliance (2001, 2008). All Rights Reserved.
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
public abstract interface IIOMetadataFormat {
	public abstract boolean canNodeAppear(java.lang.String var0, javax.imageio.ImageTypeSpecifier var1);
	public abstract int getAttributeDataType(java.lang.String var0, java.lang.String var1);
	public abstract java.lang.String getAttributeDefaultValue(java.lang.String var0, java.lang.String var1);
	public abstract java.lang.String getAttributeDescription(java.lang.String var0, java.lang.String var1, java.util.Locale var2);
	public abstract java.lang.String[] getAttributeEnumerations(java.lang.String var0, java.lang.String var1);
	public abstract int getAttributeListMaxLength(java.lang.String var0, java.lang.String var1);
	public abstract int getAttributeListMinLength(java.lang.String var0, java.lang.String var1);
	public abstract java.lang.String getAttributeMaxValue(java.lang.String var0, java.lang.String var1);
	public abstract java.lang.String getAttributeMinValue(java.lang.String var0, java.lang.String var1);
	public abstract java.lang.String[] getAttributeNames(java.lang.String var0);
	public abstract int getAttributeValueType(java.lang.String var0, java.lang.String var1);
	public abstract java.lang.String[] getChildNames(java.lang.String var0);
	public abstract int getChildPolicy(java.lang.String var0);
	public abstract java.lang.String getElementDescription(java.lang.String var0, java.util.Locale var1);
	public abstract int getElementMaxChildren(java.lang.String var0);
	public abstract int getElementMinChildren(java.lang.String var0);
	public abstract int getObjectArrayMaxLength(java.lang.String var0);
	public abstract int getObjectArrayMinLength(java.lang.String var0);
	public abstract java.lang.Class getObjectClass(java.lang.String var0);
	public abstract java.lang.Object getObjectDefaultValue(java.lang.String var0);
	public abstract java.lang.Object[] getObjectEnumerations(java.lang.String var0);
	public abstract java.lang.Comparable getObjectMaxValue(java.lang.String var0);
	public abstract java.lang.Comparable getObjectMinValue(java.lang.String var0);
	public abstract int getObjectValueType(java.lang.String var0);
	public abstract java.lang.String getRootName();
	public abstract boolean isAttributeRequired(java.lang.String var0, java.lang.String var1);
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
}

