/*
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

package javax.print.attribute.standard;
public class JobState extends javax.print.attribute.EnumSyntax implements javax.print.attribute.PrintJobAttribute {
	protected JobState(int var0) { super(0); }
	public final java.lang.Class getCategory() { return null; }
	public final java.lang.String getName() { return null; }
	public final static javax.print.attribute.standard.JobState ABORTED; static { ABORTED = null; }
	public final static javax.print.attribute.standard.JobState CANCELED; static { CANCELED = null; }
	public final static javax.print.attribute.standard.JobState COMPLETED; static { COMPLETED = null; }
	public final static javax.print.attribute.standard.JobState PENDING; static { PENDING = null; }
	public final static javax.print.attribute.standard.JobState PENDING_HELD; static { PENDING_HELD = null; }
	public final static javax.print.attribute.standard.JobState PROCESSING; static { PROCESSING = null; }
	public final static javax.print.attribute.standard.JobState PROCESSING_STOPPED; static { PROCESSING_STOPPED = null; }
	public final static javax.print.attribute.standard.JobState UNKNOWN; static { UNKNOWN = null; }
}

