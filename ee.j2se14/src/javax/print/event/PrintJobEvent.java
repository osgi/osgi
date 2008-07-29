/*
 * $Date$
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

package javax.print.event;
public class PrintJobEvent extends javax.print.event.PrintEvent {
	public PrintJobEvent(javax.print.DocPrintJob var0, int var1) { super((java.lang.Object) null); }
	public int getPrintEventType() { return 0; }
	public javax.print.DocPrintJob getPrintJob() { return null; }
	public final static int DATA_TRANSFER_COMPLETE = 106;
	public final static int JOB_CANCELED = 101;
	public final static int JOB_COMPLETE = 102;
	public final static int JOB_FAILED = 103;
	public final static int NO_MORE_EVENTS = 105;
	public final static int REQUIRES_ATTENTION = 104;
}

