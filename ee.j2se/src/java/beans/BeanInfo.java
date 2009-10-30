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

package java.beans;
public interface BeanInfo {
	public final static int ICON_COLOR_16x16 = 1;
	public final static int ICON_COLOR_32x32 = 2;
	public final static int ICON_MONO_16x16 = 3;
	public final static int ICON_MONO_32x32 = 4;
	java.beans.BeanInfo[] getAdditionalBeanInfo();
	java.beans.BeanDescriptor getBeanDescriptor();
	int getDefaultEventIndex();
	int getDefaultPropertyIndex();
	java.beans.EventSetDescriptor[] getEventSetDescriptors();
	java.awt.Image getIcon(int var0);
	java.beans.MethodDescriptor[] getMethodDescriptors();
	java.beans.PropertyDescriptor[] getPropertyDescriptors();
}

