/*
 * Copyright (c) The Open Services Gateway Initiative (2004).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the Open Services Gateway Initiative
 * (OSGI) Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of OSGi). OSGi is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and OSGI DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL OSGI BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.service.condpermadmin;

public final class ConditionInfo {
	final String	type;
	final String[]	args;
	final int		hashCode;
	final String	encoded;

	private String encode() {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		sb.append(type);
		for (int i = 0; i < args.length; i++) {
			sb.append(" \"");
			sb.append(args[i]); // TODO: needs escaping
			sb.append("\"");
		}
		sb.append("]");
		return sb.toString();
	}

	public ConditionInfo(String encodedCondition) {
		throw new IllegalArgumentException("not implemented yet!");
	}
    
	public ConditionInfo(String type, String[] args) {
		this.type = type;
		this.args = (String[]) args.clone();
		encoded = encode();
		hashCode = encoded.hashCode();
	}
	
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (!(obj instanceof ConditionInfo))
			return false;
		return ((ConditionInfo) obj).encoded.equals(this.encoded);
	}
	
	public String[] getArgs() {
		return (String[]) args.clone();
	}

	public String getEncoded() {
		return encoded;
	}

	public String getType() {
		return type;
	}

	public int hashCode() {
		return hashCode;
	}

	public String toString() {
		return "[ConditionInfo " + getEncoded() + "]";
	}
}
