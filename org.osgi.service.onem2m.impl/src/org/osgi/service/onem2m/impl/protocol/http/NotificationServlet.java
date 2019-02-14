package org.osgi.service.onem2m.impl.protocol.http;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.onem2m.NotificationListener;
import org.osgi.service.onem2m.dto.PrimitiveContentDTO;
import org.osgi.service.onem2m.dto.RequestPrimitiveDTO;
import org.osgi.service.onem2m.dto.ResourceDTO;
import org.osgi.service.onem2m.impl.serialization.BaseSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationServlet extends HttpServlet {

	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServlet.class);

	private final NotificationListener listener;
	private final BaseSerialize serialize;

	public NotificationServlet(NotificationListener listener, BaseSerialize serialize){
		this.listener = listener;
		this.serialize = serialize;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.debug("Get!!! path = " + req.getPathInfo());
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.debug("Post!!! path = " + req.getPathInfo());

		StringBuilder sb = new StringBuilder();
		BufferedReader reader = req.getReader();
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append('\n');
			}
		} finally {
			reader.close();
		}
		LOGGER.debug("receive Body = \n" + sb.toString());

		ResourceDTO resource = new ResourceDTO();
		try {
			resource = serialize.responseToResource(sb.toString());
		} catch (Exception e) {
			LOGGER.warn("Json serialize error.", e);
		}
		PrimitiveContentDTO content = new PrimitiveContentDTO();
		content.resource = resource;
		RequestPrimitiveDTO primitive = new RequestPrimitiveDTO();
		primitive.content = content;

		listener.notified(primitive);
	}
}
