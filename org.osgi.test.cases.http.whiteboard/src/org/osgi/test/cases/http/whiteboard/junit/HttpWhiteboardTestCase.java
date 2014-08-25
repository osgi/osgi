/*
 * Copyright (c) OSGi Alliance (2014). All Rights Reserved.
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
package org.osgi.test.cases.http.whiteboard.junit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.test.support.OSGiTestCase;

public class HttpWhiteboardTestCase extends OSGiTestCase {
	public void testSimpleWhiteboardServlet() throws Exception {
        @SuppressWarnings("serial")
        Servlet myServlet = new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                resp.getWriter().println("hello");
            }
        };

        Dictionary<String, Object> props = new Hashtable<String, Object>();
	    props.put("osgi.http.whiteboard.servlet.pattern", "/s1");
        getContext().registerService(Servlet.class, myServlet, props);

        // TODO make port configurable
        assertServletResponse("hello", new URL("http://localhost:8080/s1"));
	}

    private static void assertServletResponse(String expected, URL url) throws InterruptedException {
        String response = "";
        for (int i = 0; i < 10; i++) {
            try {
                response = new String(drainInputStream(url.openStream()));
                if (response.trim().equals(expected))
                    return;
            } catch (IOException e) {
                response = e.getMessage();
            }
            System.out.println("Unexpected response: " + response + " Retrying.");
            Thread.sleep(500);
        }

        fail("Unable to obtain expected response. Last response was: " + response + " Expected: " + expected);
    }

    private static void drainInputStream(InputStream is, OutputStream os) throws IOException {
        byte[] bytes = new byte[4096];

        int length = 0;
        int offset = 0;

        while ((length = is.read(bytes, offset, bytes.length - offset)) != -1) {
            offset += length;

            if (offset == bytes.length) {
                os.write(bytes, 0, bytes.length);
                offset = 0;
            }
        }
        if (offset != 0) {
            os.write(bytes, 0, offset);
        }
    }

    private static byte[] drainInputStream(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            drainInputStream(is, baos);
            return baos.toByteArray();
        } finally {
            is.close();
        }
    }
}
