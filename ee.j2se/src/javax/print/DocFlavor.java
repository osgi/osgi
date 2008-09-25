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

package javax.print;
public class DocFlavor implements java.io.Serializable, java.lang.Cloneable {
	public DocFlavor(java.lang.String var0, java.lang.String var1) { }
	public java.lang.String getMediaSubtype() { return null; }
	public java.lang.String getMediaType() { return null; }
	public java.lang.String getMimeType() { return null; }
	public java.lang.String getParameter(java.lang.String var0) { return null; }
	public java.lang.String getRepresentationClassName() { return null; }
	public int hashCode() { return 0; }
	public final static java.lang.String hostEncoding; static { hostEncoding = null; }
	public static class BYTE_ARRAY extends javax.print.DocFlavor {
		public BYTE_ARRAY(java.lang.String var0) { super((java.lang.String) null, (java.lang.String) null); }
		public final static javax.print.DocFlavor.BYTE_ARRAY AUTOSENSE; static { AUTOSENSE = null; }
		public final static javax.print.DocFlavor.BYTE_ARRAY GIF; static { GIF = null; }
		public final static javax.print.DocFlavor.BYTE_ARRAY JPEG; static { JPEG = null; }
		public final static javax.print.DocFlavor.BYTE_ARRAY PCL; static { PCL = null; }
		public final static javax.print.DocFlavor.BYTE_ARRAY PDF; static { PDF = null; }
		public final static javax.print.DocFlavor.BYTE_ARRAY PNG; static { PNG = null; }
		public final static javax.print.DocFlavor.BYTE_ARRAY POSTSCRIPT; static { POSTSCRIPT = null; }
		public final static javax.print.DocFlavor.BYTE_ARRAY TEXT_HTML_HOST; static { TEXT_HTML_HOST = null; }
		public final static javax.print.DocFlavor.BYTE_ARRAY TEXT_HTML_US_ASCII; static { TEXT_HTML_US_ASCII = null; }
		public final static javax.print.DocFlavor.BYTE_ARRAY TEXT_HTML_UTF_16; static { TEXT_HTML_UTF_16 = null; }
		public final static javax.print.DocFlavor.BYTE_ARRAY TEXT_HTML_UTF_16BE; static { TEXT_HTML_UTF_16BE = null; }
		public final static javax.print.DocFlavor.BYTE_ARRAY TEXT_HTML_UTF_16LE; static { TEXT_HTML_UTF_16LE = null; }
		public final static javax.print.DocFlavor.BYTE_ARRAY TEXT_HTML_UTF_8; static { TEXT_HTML_UTF_8 = null; }
		public final static javax.print.DocFlavor.BYTE_ARRAY TEXT_PLAIN_HOST; static { TEXT_PLAIN_HOST = null; }
		public final static javax.print.DocFlavor.BYTE_ARRAY TEXT_PLAIN_US_ASCII; static { TEXT_PLAIN_US_ASCII = null; }
		public final static javax.print.DocFlavor.BYTE_ARRAY TEXT_PLAIN_UTF_16; static { TEXT_PLAIN_UTF_16 = null; }
		public final static javax.print.DocFlavor.BYTE_ARRAY TEXT_PLAIN_UTF_16BE; static { TEXT_PLAIN_UTF_16BE = null; }
		public final static javax.print.DocFlavor.BYTE_ARRAY TEXT_PLAIN_UTF_16LE; static { TEXT_PLAIN_UTF_16LE = null; }
		public final static javax.print.DocFlavor.BYTE_ARRAY TEXT_PLAIN_UTF_8; static { TEXT_PLAIN_UTF_8 = null; }
	}
	public static class CHAR_ARRAY extends javax.print.DocFlavor {
		public CHAR_ARRAY(java.lang.String var0) { super((java.lang.String) null, (java.lang.String) null); }
		public final static javax.print.DocFlavor.CHAR_ARRAY TEXT_HTML; static { TEXT_HTML = null; }
		public final static javax.print.DocFlavor.CHAR_ARRAY TEXT_PLAIN; static { TEXT_PLAIN = null; }
	}
	public static class INPUT_STREAM extends javax.print.DocFlavor {
		public INPUT_STREAM(java.lang.String var0) { super((java.lang.String) null, (java.lang.String) null); }
		public final static javax.print.DocFlavor.INPUT_STREAM AUTOSENSE; static { AUTOSENSE = null; }
		public final static javax.print.DocFlavor.INPUT_STREAM GIF; static { GIF = null; }
		public final static javax.print.DocFlavor.INPUT_STREAM JPEG; static { JPEG = null; }
		public final static javax.print.DocFlavor.INPUT_STREAM PCL; static { PCL = null; }
		public final static javax.print.DocFlavor.INPUT_STREAM PDF; static { PDF = null; }
		public final static javax.print.DocFlavor.INPUT_STREAM PNG; static { PNG = null; }
		public final static javax.print.DocFlavor.INPUT_STREAM POSTSCRIPT; static { POSTSCRIPT = null; }
		public final static javax.print.DocFlavor.INPUT_STREAM TEXT_HTML_HOST; static { TEXT_HTML_HOST = null; }
		public final static javax.print.DocFlavor.INPUT_STREAM TEXT_HTML_US_ASCII; static { TEXT_HTML_US_ASCII = null; }
		public final static javax.print.DocFlavor.INPUT_STREAM TEXT_HTML_UTF_16; static { TEXT_HTML_UTF_16 = null; }
		public final static javax.print.DocFlavor.INPUT_STREAM TEXT_HTML_UTF_16BE; static { TEXT_HTML_UTF_16BE = null; }
		public final static javax.print.DocFlavor.INPUT_STREAM TEXT_HTML_UTF_16LE; static { TEXT_HTML_UTF_16LE = null; }
		public final static javax.print.DocFlavor.INPUT_STREAM TEXT_HTML_UTF_8; static { TEXT_HTML_UTF_8 = null; }
		public final static javax.print.DocFlavor.INPUT_STREAM TEXT_PLAIN_HOST; static { TEXT_PLAIN_HOST = null; }
		public final static javax.print.DocFlavor.INPUT_STREAM TEXT_PLAIN_US_ASCII; static { TEXT_PLAIN_US_ASCII = null; }
		public final static javax.print.DocFlavor.INPUT_STREAM TEXT_PLAIN_UTF_16; static { TEXT_PLAIN_UTF_16 = null; }
		public final static javax.print.DocFlavor.INPUT_STREAM TEXT_PLAIN_UTF_16BE; static { TEXT_PLAIN_UTF_16BE = null; }
		public final static javax.print.DocFlavor.INPUT_STREAM TEXT_PLAIN_UTF_16LE; static { TEXT_PLAIN_UTF_16LE = null; }
		public final static javax.print.DocFlavor.INPUT_STREAM TEXT_PLAIN_UTF_8; static { TEXT_PLAIN_UTF_8 = null; }
	}
	public static class READER extends javax.print.DocFlavor {
		public READER(java.lang.String var0) { super((java.lang.String) null, (java.lang.String) null); }
		public final static javax.print.DocFlavor.READER TEXT_HTML; static { TEXT_HTML = null; }
		public final static javax.print.DocFlavor.READER TEXT_PLAIN; static { TEXT_PLAIN = null; }
	}
	public static class SERVICE_FORMATTED extends javax.print.DocFlavor {
		public SERVICE_FORMATTED(java.lang.String var0) { super((java.lang.String) null, (java.lang.String) null); }
		public final static javax.print.DocFlavor.SERVICE_FORMATTED PAGEABLE; static { PAGEABLE = null; }
		public final static javax.print.DocFlavor.SERVICE_FORMATTED PRINTABLE; static { PRINTABLE = null; }
		public final static javax.print.DocFlavor.SERVICE_FORMATTED RENDERABLE_IMAGE; static { RENDERABLE_IMAGE = null; }
	}
	public static class STRING extends javax.print.DocFlavor {
		public STRING(java.lang.String var0) { super((java.lang.String) null, (java.lang.String) null); }
		public final static javax.print.DocFlavor.STRING TEXT_HTML; static { TEXT_HTML = null; }
		public final static javax.print.DocFlavor.STRING TEXT_PLAIN; static { TEXT_PLAIN = null; }
	}
	public static class URL extends javax.print.DocFlavor {
		public URL(java.lang.String var0) { super((java.lang.String) null, (java.lang.String) null); }
		public final static javax.print.DocFlavor.URL AUTOSENSE; static { AUTOSENSE = null; }
		public final static javax.print.DocFlavor.URL GIF; static { GIF = null; }
		public final static javax.print.DocFlavor.URL JPEG; static { JPEG = null; }
		public final static javax.print.DocFlavor.URL PCL; static { PCL = null; }
		public final static javax.print.DocFlavor.URL PDF; static { PDF = null; }
		public final static javax.print.DocFlavor.URL PNG; static { PNG = null; }
		public final static javax.print.DocFlavor.URL POSTSCRIPT; static { POSTSCRIPT = null; }
		public final static javax.print.DocFlavor.URL TEXT_HTML_HOST; static { TEXT_HTML_HOST = null; }
		public final static javax.print.DocFlavor.URL TEXT_HTML_US_ASCII; static { TEXT_HTML_US_ASCII = null; }
		public final static javax.print.DocFlavor.URL TEXT_HTML_UTF_16; static { TEXT_HTML_UTF_16 = null; }
		public final static javax.print.DocFlavor.URL TEXT_HTML_UTF_16BE; static { TEXT_HTML_UTF_16BE = null; }
		public final static javax.print.DocFlavor.URL TEXT_HTML_UTF_16LE; static { TEXT_HTML_UTF_16LE = null; }
		public final static javax.print.DocFlavor.URL TEXT_HTML_UTF_8; static { TEXT_HTML_UTF_8 = null; }
		public final static javax.print.DocFlavor.URL TEXT_PLAIN_HOST; static { TEXT_PLAIN_HOST = null; }
		public final static javax.print.DocFlavor.URL TEXT_PLAIN_US_ASCII; static { TEXT_PLAIN_US_ASCII = null; }
		public final static javax.print.DocFlavor.URL TEXT_PLAIN_UTF_16; static { TEXT_PLAIN_UTF_16 = null; }
		public final static javax.print.DocFlavor.URL TEXT_PLAIN_UTF_16BE; static { TEXT_PLAIN_UTF_16BE = null; }
		public final static javax.print.DocFlavor.URL TEXT_PLAIN_UTF_16LE; static { TEXT_PLAIN_UTF_16LE = null; }
		public final static javax.print.DocFlavor.URL TEXT_PLAIN_UTF_8; static { TEXT_PLAIN_UTF_8 = null; }
	}
}

