/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 OSGi Alliance
 */

/* $Header$ */

package java.util.jar;
public class Attributes implements java.lang.Cloneable, java.util.Map {
	public Attributes() { }
	public Attributes(java.util.jar.Attributes var0) { }
	public Attributes(int var0) { }
	public void clear() { }
	public boolean containsKey(java.lang.Object var0) { return false; }
	public boolean containsValue(java.lang.Object var0) { return false; }
	public java.util.Set entrySet() { return null; }
	public java.lang.Object get(java.lang.Object var0) { return null; }
	public boolean isEmpty() { return false; }
	public java.util.Set keySet() { return null; }
	public java.lang.Object put(java.lang.Object var0, java.lang.Object var1) { return null; }
	public void putAll(java.util.Map var0) { }
	public java.lang.Object remove(java.lang.Object var0) { return null; }
	public int size() { return 0; }
	public java.util.Collection values() { return null; }
	public java.lang.Object clone() { return null; }
	public int hashCode() { return 0; }
	public boolean equals(java.lang.Object var0) { return false; }
	public java.lang.String getValue(java.util.jar.Attributes.Name var0) { return null; }
	public java.lang.String getValue(java.lang.String var0) { return null; }
	public java.lang.String putValue(java.lang.String var0, java.lang.String var1) { return null; }
	protected java.util.Map map;
	public static class Name {
		public Name(java.lang.String var0) { }
		public java.lang.String toString() { return null; }
		public boolean equals(java.lang.Object var0) { return false; }
		public int hashCode() { return 0; }
		public final static java.util.jar.Attributes.Name CLASS_PATH; static { CLASS_PATH = null; }
		public final static java.util.jar.Attributes.Name MANIFEST_VERSION; static { MANIFEST_VERSION = null; }
		public final static java.util.jar.Attributes.Name MAIN_CLASS; static { MAIN_CLASS = null; }
		public final static java.util.jar.Attributes.Name SIGNATURE_VERSION; static { SIGNATURE_VERSION = null; }
		public final static java.util.jar.Attributes.Name CONTENT_TYPE; static { CONTENT_TYPE = null; }
		public final static java.util.jar.Attributes.Name SEALED; static { SEALED = null; }
		public final static java.util.jar.Attributes.Name IMPLEMENTATION_TITLE; static { IMPLEMENTATION_TITLE = null; }
		public final static java.util.jar.Attributes.Name IMPLEMENTATION_VERSION; static { IMPLEMENTATION_VERSION = null; }
		public final static java.util.jar.Attributes.Name IMPLEMENTATION_VENDOR; static { IMPLEMENTATION_VENDOR = null; }
		public final static java.util.jar.Attributes.Name SPECIFICATION_TITLE; static { SPECIFICATION_TITLE = null; }
		public final static java.util.jar.Attributes.Name SPECIFICATION_VERSION; static { SPECIFICATION_VERSION = null; }
		public final static java.util.jar.Attributes.Name SPECIFICATION_VENDOR; static { SPECIFICATION_VENDOR = null; }
		public final static java.util.jar.Attributes.Name EXTENSION_LIST; static { EXTENSION_LIST = null; }
		public final static java.util.jar.Attributes.Name EXTENSION_NAME; static { EXTENSION_NAME = null; }
		public final static java.util.jar.Attributes.Name EXTENSION_INSTALLATION; static { EXTENSION_INSTALLATION = null; }
		public final static java.util.jar.Attributes.Name IMPLEMENTATION_VENDOR_ID; static { IMPLEMENTATION_VENDOR_ID = null; }
		public final static java.util.jar.Attributes.Name IMPLEMENTATION_URL; static { IMPLEMENTATION_URL = null; }
	}
}

