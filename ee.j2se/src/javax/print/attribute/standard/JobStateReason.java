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

package javax.print.attribute.standard;
public class JobStateReason extends javax.print.attribute.EnumSyntax implements javax.print.attribute.Attribute {
	protected JobStateReason(int var0) { super(0); }
	public final java.lang.Class getCategory() { return null; }
	public final java.lang.String getName() { return null; }
	public final static javax.print.attribute.standard.JobStateReason ABORTED_BY_SYSTEM; static { ABORTED_BY_SYSTEM = null; }
	public final static javax.print.attribute.standard.JobStateReason COMPRESSION_ERROR; static { COMPRESSION_ERROR = null; }
	public final static javax.print.attribute.standard.JobStateReason DOCUMENT_ACCESS_ERROR; static { DOCUMENT_ACCESS_ERROR = null; }
	public final static javax.print.attribute.standard.JobStateReason DOCUMENT_FORMAT_ERROR; static { DOCUMENT_FORMAT_ERROR = null; }
	public final static javax.print.attribute.standard.JobStateReason JOB_CANCELED_AT_DEVICE; static { JOB_CANCELED_AT_DEVICE = null; }
	public final static javax.print.attribute.standard.JobStateReason JOB_CANCELED_BY_OPERATOR; static { JOB_CANCELED_BY_OPERATOR = null; }
	public final static javax.print.attribute.standard.JobStateReason JOB_CANCELED_BY_USER; static { JOB_CANCELED_BY_USER = null; }
	public final static javax.print.attribute.standard.JobStateReason JOB_COMPLETED_SUCCESSFULLY; static { JOB_COMPLETED_SUCCESSFULLY = null; }
	public final static javax.print.attribute.standard.JobStateReason JOB_COMPLETED_WITH_ERRORS; static { JOB_COMPLETED_WITH_ERRORS = null; }
	public final static javax.print.attribute.standard.JobStateReason JOB_COMPLETED_WITH_WARNINGS; static { JOB_COMPLETED_WITH_WARNINGS = null; }
	public final static javax.print.attribute.standard.JobStateReason JOB_DATA_INSUFFICIENT; static { JOB_DATA_INSUFFICIENT = null; }
	public final static javax.print.attribute.standard.JobStateReason JOB_HOLD_UNTIL_SPECIFIED; static { JOB_HOLD_UNTIL_SPECIFIED = null; }
	public final static javax.print.attribute.standard.JobStateReason JOB_INCOMING; static { JOB_INCOMING = null; }
	public final static javax.print.attribute.standard.JobStateReason JOB_INTERPRETING; static { JOB_INTERPRETING = null; }
	public final static javax.print.attribute.standard.JobStateReason JOB_OUTGOING; static { JOB_OUTGOING = null; }
	public final static javax.print.attribute.standard.JobStateReason JOB_PRINTING; static { JOB_PRINTING = null; }
	public final static javax.print.attribute.standard.JobStateReason JOB_QUEUED; static { JOB_QUEUED = null; }
	public final static javax.print.attribute.standard.JobStateReason JOB_QUEUED_FOR_MARKER; static { JOB_QUEUED_FOR_MARKER = null; }
	public final static javax.print.attribute.standard.JobStateReason JOB_RESTARTABLE; static { JOB_RESTARTABLE = null; }
	public final static javax.print.attribute.standard.JobStateReason JOB_TRANSFORMING; static { JOB_TRANSFORMING = null; }
	public final static javax.print.attribute.standard.JobStateReason PRINTER_STOPPED; static { PRINTER_STOPPED = null; }
	public final static javax.print.attribute.standard.JobStateReason PRINTER_STOPPED_PARTLY; static { PRINTER_STOPPED_PARTLY = null; }
	public final static javax.print.attribute.standard.JobStateReason PROCESSING_TO_STOP_POINT; static { PROCESSING_TO_STOP_POINT = null; }
	public final static javax.print.attribute.standard.JobStateReason QUEUED_IN_DEVICE; static { QUEUED_IN_DEVICE = null; }
	public final static javax.print.attribute.standard.JobStateReason RESOURCES_ARE_NOT_READY; static { RESOURCES_ARE_NOT_READY = null; }
	public final static javax.print.attribute.standard.JobStateReason SERVICE_OFF_LINE; static { SERVICE_OFF_LINE = null; }
	public final static javax.print.attribute.standard.JobStateReason SUBMISSION_INTERRUPTED; static { SUBMISSION_INTERRUPTED = null; }
	public final static javax.print.attribute.standard.JobStateReason UNSUPPORTED_COMPRESSION; static { UNSUPPORTED_COMPRESSION = null; }
	public final static javax.print.attribute.standard.JobStateReason UNSUPPORTED_DOCUMENT_FORMAT; static { UNSUPPORTED_DOCUMENT_FORMAT = null; }
}

