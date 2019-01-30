package org.osgi.test.cases.http.whiteboard.junit.mock;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class MockSCL implements ServletContextListener {

	public MockSCL(AtomicReference<ServletContext> sc) {
		this.sc = Objects.requireNonNull(sc);
	}

	public void contextDestroyed(ServletContextEvent arg0) {
	}

	public void contextInitialized(ServletContextEvent event) {
		sc.set(event.getServletContext());
	}

	public ServletContext getSC() {
		return sc.get();
	}

	private final AtomicReference<ServletContext> sc;

}
