/*
 * Copyright (c) 2004, Nokia
 * COMPANY CONFIDENTAL - for internal use only
 */
package org.osgi.service.condpermadmin;

/**
 * @author Peter Nagy <peter.1.nagy@nokia.com>
 */
public final class ConditionInfo {
	final String type;
	final String[] args;
	final int hashCode;
	final String encoded;

	private String encode() {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		sb.append(type);
		for(int i=0;i<args.length;i++) {
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
	public ConditionInfo(String type,String[] args) {
		this.type = type;
		this.args = (String[]) args.clone();
		encoded = encode();
		hashCode = encoded.hashCode();
	}
	
	public boolean equals(Object obj) {
		if (obj==null) return false;
		if (! (obj instanceof ConditionInfo)) return false;
		return ((ConditionInfo)obj).encoded.equals(this.encoded);
	}
	
	public String[] getArgs() { return (String[]) args.clone(); }
	public String getEncoded() { return encoded; }
	public String getType() { return type; }
	public int hashCode() { return hashCode; }
	public String toString() { return "[ConditionInfo "+getEncoded()+"]";}
	
}
