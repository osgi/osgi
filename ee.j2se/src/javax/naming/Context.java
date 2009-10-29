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

package javax.naming;
public abstract interface Context {
	public abstract java.lang.Object addToEnvironment(java.lang.String var0, java.lang.Object var1) throws javax.naming.NamingException;
	public abstract void bind(java.lang.String var0, java.lang.Object var1) throws javax.naming.NamingException;
	public abstract void bind(javax.naming.Name var0, java.lang.Object var1) throws javax.naming.NamingException;
	public abstract void close() throws javax.naming.NamingException;
	public abstract java.lang.String composeName(java.lang.String var0, java.lang.String var1) throws javax.naming.NamingException;
	public abstract javax.naming.Name composeName(javax.naming.Name var0, javax.naming.Name var1) throws javax.naming.NamingException;
	public abstract javax.naming.Context createSubcontext(java.lang.String var0) throws javax.naming.NamingException;
	public abstract javax.naming.Context createSubcontext(javax.naming.Name var0) throws javax.naming.NamingException;
	public abstract void destroySubcontext(java.lang.String var0) throws javax.naming.NamingException;
	public abstract void destroySubcontext(javax.naming.Name var0) throws javax.naming.NamingException;
	public abstract java.util.Hashtable getEnvironment() throws javax.naming.NamingException;
	public abstract java.lang.String getNameInNamespace() throws javax.naming.NamingException;
	public abstract javax.naming.NameParser getNameParser(java.lang.String var0) throws javax.naming.NamingException;
	public abstract javax.naming.NameParser getNameParser(javax.naming.Name var0) throws javax.naming.NamingException;
	public abstract javax.naming.NamingEnumeration list(java.lang.String var0) throws javax.naming.NamingException;
	public abstract javax.naming.NamingEnumeration list(javax.naming.Name var0) throws javax.naming.NamingException;
	public abstract javax.naming.NamingEnumeration listBindings(java.lang.String var0) throws javax.naming.NamingException;
	public abstract javax.naming.NamingEnumeration listBindings(javax.naming.Name var0) throws javax.naming.NamingException;
	public abstract java.lang.Object lookup(java.lang.String var0) throws javax.naming.NamingException;
	public abstract java.lang.Object lookup(javax.naming.Name var0) throws javax.naming.NamingException;
	public abstract java.lang.Object lookupLink(java.lang.String var0) throws javax.naming.NamingException;
	public abstract java.lang.Object lookupLink(javax.naming.Name var0) throws javax.naming.NamingException;
	public abstract void rebind(java.lang.String var0, java.lang.Object var1) throws javax.naming.NamingException;
	public abstract void rebind(javax.naming.Name var0, java.lang.Object var1) throws javax.naming.NamingException;
	public abstract java.lang.Object removeFromEnvironment(java.lang.String var0) throws javax.naming.NamingException;
	public abstract void rename(java.lang.String var0, java.lang.String var1) throws javax.naming.NamingException;
	public abstract void rename(javax.naming.Name var0, javax.naming.Name var1) throws javax.naming.NamingException;
	public abstract void unbind(java.lang.String var0) throws javax.naming.NamingException;
	public abstract void unbind(javax.naming.Name var0) throws javax.naming.NamingException;
	public final static java.lang.String APPLET = "java.naming.applet";
	public final static java.lang.String AUTHORITATIVE = "java.naming.authoritative";
	public final static java.lang.String BATCHSIZE = "java.naming.batchsize";
	public final static java.lang.String DNS_URL = "java.naming.dns.url";
	public final static java.lang.String INITIAL_CONTEXT_FACTORY = "java.naming.factory.initial";
	public final static java.lang.String LANGUAGE = "java.naming.language";
	public final static java.lang.String OBJECT_FACTORIES = "java.naming.factory.object";
	public final static java.lang.String PROVIDER_URL = "java.naming.provider.url";
	public final static java.lang.String REFERRAL = "java.naming.referral";
	public final static java.lang.String SECURITY_AUTHENTICATION = "java.naming.security.authentication";
	public final static java.lang.String SECURITY_CREDENTIALS = "java.naming.security.credentials";
	public final static java.lang.String SECURITY_PRINCIPAL = "java.naming.security.principal";
	public final static java.lang.String SECURITY_PROTOCOL = "java.naming.security.protocol";
	public final static java.lang.String STATE_FACTORIES = "java.naming.factory.state";
	public final static java.lang.String URL_PKG_PREFIXES = "java.naming.factory.url.pkgs";
}

