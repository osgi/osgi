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

package javax.management.relation;
public class RoleStatus {
	public final static int LESS_THAN_MIN_ROLE_DEGREE = 4;
	public final static int MORE_THAN_MAX_ROLE_DEGREE = 5;
	public final static int NO_ROLE_WITH_NAME = 1;
	public final static int REF_MBEAN_NOT_REGISTERED = 7;
	public final static int REF_MBEAN_OF_INCORRECT_CLASS = 6;
	public final static int ROLE_NOT_READABLE = 2;
	public final static int ROLE_NOT_WRITABLE = 3;
	public RoleStatus() { } 
	public static boolean isRoleStatus(int var0) { return false; }
}

