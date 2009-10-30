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

package javax.sql.rowset;
public interface WebRowSet extends javax.sql.rowset.CachedRowSet {
	public final static java.lang.String PUBLIC_XML_SCHEMA = "--//Sun Microsystems, Inc.//XSD Schema//EN";
	public final static java.lang.String SCHEMA_SYSTEM_ID = "http://java.sun.com/xml/ns/jdbc/webrowset.xsd";
	void readXml(java.io.InputStream var0) throws java.io.IOException, java.sql.SQLException;
	void readXml(java.io.Reader var0) throws java.sql.SQLException;
	void writeXml(java.io.OutputStream var0) throws java.io.IOException, java.sql.SQLException;
	void writeXml(java.io.Writer var0) throws java.sql.SQLException;
	void writeXml(java.sql.ResultSet var0, java.io.OutputStream var1) throws java.io.IOException, java.sql.SQLException;
	void writeXml(java.sql.ResultSet var0, java.io.Writer var1) throws java.sql.SQLException;
}

