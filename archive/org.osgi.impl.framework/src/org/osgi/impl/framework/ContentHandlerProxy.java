/*
 * $Header$
 *
 * OSGi URL Handler Bundle Reference Implementation
 *
 * Open Services Gateway Initiative (OSGi) Confidential.
 *
 * (C) Copyright IBM Corporation 2000-2001.
 *
 * This source code is licensed to OSGi as MEMBER LICENSED MATERIALS
 * under the terms of Section 3.2 of the OSGi MEMBER AGREEMENT.
 */
package org.osgi.impl.framework;

import org.osgi.framework.*;
import org.osgi.util.tracker.*;
import org.osgi.service.url.*;
import java.net.*;

/**
 * Tracks a specific MIME type handler.  It is instantiated by the 
 * URLHandlerBundle and proxies registered handlers.
 * <p>
 * Note, the behaviour is a bit different from URLStreamHandlerProxy.  This
 * is because java.net.URLConnection does not cache handlers from factories
 * and it will use a simple handler that return 
 * URLConnection.getInputStream() for unhandled MIME types.
 */
public class ContentHandlerProxy extends ContentHandler {
    final Framework framework;
    /** MIME type being tracked */
    String mimetype;
    /** The filter matching the ContentHandler beeing proxied */
    String filter = null;

    /** returns the current content handler or tries to get one if null. */
    ContentHandler getHandler() {
      ServiceReference[] srs = getServiceReferences();
      return selectHighestRankingService(srs);
    }

    private int rankingOf(ServiceReference s) {
      Object v = s.getProperty(Constants.SERVICE_RANKING);
      if (v != null && v instanceof Integer) {
        return ((Integer)v).intValue();
      } else {
        return 0;
      }
    }

    private int serviceIdOf(ServiceReference s) {
      Object v = s.getProperty(Constants.SERVICE_ID);
      if (v != null && v instanceof Integer) {
        return ((Integer)v).intValue();
      } else {
        throw new RuntimeException("Missing service.id on a service.");
      }
    }

    ContentHandler selectHighestRankingService(ServiceReference[] srs) {
      if(srs == null || srs.length < 1) {
        return null;
      }
      ServiceReference bestSofar = srs[0];
      for(int i = 1; i < srs.length; ++i) {
        if(rankingOf(srs[i]) > rankingOf(bestSofar)) {
          bestSofar = srs[i];
        } else if(rankingOf(srs[i]) == rankingOf(bestSofar) &&
          serviceIdOf(srs[i]) < serviceIdOf(bestSofar)) {
          bestSofar = srs[i];
        }
      }
      return (ContentHandler)(((ServiceReferenceImpl)bestSofar).registration.service);
    }

    /**
     * Instantiates a proxy for a specific mimetype.
     * @param bc The BundleContext from the URLHandlerBundle.  It will be
     *           used to access the service registry.
     * @param mimetype The MIME type being watched.
     */
    public ContentHandlerProxy(Framework framework, String mimetype) {
	this.framework = framework;
	this.mimetype = mimetype;
    }

    String getFilter() {
      if(filter == null) {
        filter = 
          "(&("+Constants.OBJECTCLASS+
          "=java.net.ContentHandler)"+
          "(" + URLConstants.URL_CONTENT_MIMETYPE +
          "="+mimetype+"))";
      }
      return filter;
    }
    /** Returns true if there is a handler for this MIME type in the service
     *  registry. */
    boolean exists() { 
      ServiceReference[] srs = getServiceReferences();
      return srs != null && srs.length > 0;
    }

  ServiceReference[] getServiceReferences() {
    try {
      return framework.services.get(null, getFilter());
    } catch (InvalidSyntaxException e) {
      throw new RuntimeException("unexpected InvalidSyntaxException: "+
        e.getMessage());
    }
  }

    /** Renders the content for a connection.  If the handler disappears,
     *  we return conn.getInputStream().  (That is what 
     *  java.net.URLConnection does.) */
    public Object getContent(URLConnection conn) throws java.io.IOException {
	ContentHandler handler = getHandler();
	if (handler == null) return conn.getInputStream();
	return handler.getContent(conn);
    }
}
