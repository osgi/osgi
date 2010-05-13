/**
 * Copyright (c) 1999, 2000 Gatespace AB. All Rights Reserved.
 *
 * Gatespace grants the OSGi Alliance an irrevocable,
 * perpetual, non-exclusive, worldwide, paid-up right and license to
 * reproduce, display, perform, prepare and have prepared derivative works
 * based upon and distribute and sublicense this material and derivative
 * works thereof as set out in the OSGi MEMBER AGREEMENT as of January 24
 * 2000, for use in accordance with Section 2.2 of the BY-LAWS of the
 * OSGi MEMBER AGREEMENT.
 */
package org.osgi.impl.service.http;

import java.util.*;
import javax.servlet.http.*;

//  ******************** HttpSessionImpl ********************
/**
 * * Implements session handling for servlets. * *
 * 
 * @author Gatespace AB (osgiref@gatespace.com) *
 * @version $Id$
 */
public final class HttpSessionImpl implements HttpSession {
	private String			sessionId;
	private long			creationDate, lastAccessDate;
	private Hashtable		values				= new Hashtable();
	private boolean			isInvalidated		= false;
	private boolean			isNew				= true;
	private ServletEngine	servletEngine;
	private int				maxInactiveInterval	= 1800;

	public HttpSessionImpl(ServletEngine servletEngine, AliasRegistration reg,
			String sessionId) {
		this.servletEngine = servletEngine;
		this.sessionId = sessionId;
		creationDate = lastAccessDate = System.currentTimeMillis();
	}

	boolean isInvalidated() {
		return isInvalidated;
	}

	void setSessionAccessed() {
		lastAccessDate = System.currentTimeMillis();
		if (isNew)
			isNew = false;
	}

	//----------------------------------------------------------------------------
	//	IMPLEMENTS - HttpSession (no doc here, see javax.servlet.http)
	//----------------------------------------------------------------------------
	public String getId() {
		return sessionId;
	}

	/**
	 * *
	 * 
	 * @deprecated Not used in JSDK 2.1
	 */
	public HttpSessionContext getSessionContext() {
		if (isInvalidated)
			throw new IllegalStateException("Session is invalid");
		return servletEngine;
	}

	public long getCreationTime() {
		if (isInvalidated)
			throw new IllegalStateException("Session is invalid");
		return creationDate;
	}

	public long getLastAccessedTime() {
		if (isInvalidated)
			throw new IllegalStateException("Session is invalid");
		return lastAccessDate;
	}

	public void invalidate() {
		if (isInvalidated)
			throw new IllegalStateException("Session is already invalidated");
		isInvalidated = true;
	}

	public void putValue(String name, Object value) {
		if (isInvalidated)
			throw new IllegalStateException("Session is invalid");
		values.put(name, value);
		if (value instanceof HttpSessionBindingListener)
			((HttpSessionBindingListener) value)
					.valueBound(new HttpSessionBindingEvent(this, name));
	}

	public Object getValue(String name) {
		if (isInvalidated)
			throw new IllegalStateException("Session is invalid");
		return values.get(name);
	}

	public void removeValue(String name) {
		if (isInvalidated)
			throw new IllegalStateException("Session is invalid");
		Object tmp = values.remove(name);
		if (tmp != null && tmp instanceof HttpSessionBindingListener)
			((HttpSessionBindingListener) tmp)
					.valueUnbound(new HttpSessionBindingEvent(this, name));
	}

	public String[] getValueNames() {
		if (isInvalidated)
			throw new IllegalStateException("Session is invalid");
		String tmp[] = new String[values.size()];
		int i = 0;
		for (Enumeration e = values.keys(); e.hasMoreElements(); i++)
			tmp[i] = (String) e.nextElement();
		return tmp;
	}

	public boolean isNew() {
		if (isInvalidated)
			throw new IllegalStateException("Session is invalid");
		return isNew;
	}

	public int getMaxInactiveInterval() {
		return maxInactiveInterval;
	}

	public void setMaxInactiveInterval(int interval) {
		maxInactiveInterval = interval;
	}
}
