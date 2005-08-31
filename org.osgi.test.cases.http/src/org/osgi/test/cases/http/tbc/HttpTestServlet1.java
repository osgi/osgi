/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Telia Research AB. 2000.
 * This source code is owned by Telia Research AB,
 * and is being distributed to OSGi MEMBERS as
 * MEMBER LICENSED MATERIALS under the terms of section 3.2 of
 * the OSGi MEMBER AGREEMENT.
 */
package org.osgi.test.cases.http.tbc;

import java.io.*;
import java.security.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class HttpTestServlet1 extends javax.servlet.http.HttpServlet {
	private static final String	pagePart1	= "<html><head><title>OSGi - HTTP Service test case</title></head><body><h1>";
	private static final String	pagePart2	= "</body></html>";
	private static Hashtable	testCaseMap	= new Hashtable();
	static {
		testCaseMap.put("1", "1 Registration of simple servlet</h1>");
		testCaseMap.put("2", "2 Unregistration of simple servlet</h1>");
		testCaseMap.put("3", "3 Registration of two overlapping servlets</h1>");
		testCaseMap.put("4", "4 Unregistration of overlapping servlet</h1>");
		testCaseMap.put("6", "6 Registering servlet with long name</h1>");
		testCaseMap.put("8", "8 Case sensitivity</h1>");
		testCaseMap.put("15",
				"15 Registration of servlet using default HTTP context</h1>");
		testCaseMap.put("50",
				"50 Register servlet with empty doGet() method</h1>");
		testCaseMap.put("51",
				"51 Verification of statements under handleSecurity()</h1>");
		testCaseMap.put("52", "52 Servlet throws exception in doGet()</h1>");
		testCaseMap.put("53", "53 Servlet returns 4 MB file</h1>");
		//testCaseMap.put ("54", "54 Query version of http service</h1>");
		testCaseMap.put("55", "55 Hang request to http service</h1>");
		testCaseMap.put("56",
				"56 Access of servlet with long parameter values</h1>");
		testCaseMap.put("57",
				"57 Access URL with special parameter values</h1>");
		testCaseMap
				.put(
						"58a",
						"58a Access URL using getParameter with different values for the same parameter</h1>");
		testCaseMap
				.put(
						"58b",
						"58b Access URL using getParameterValues with different values for the same parameter</h1>");
		testCaseMap.put("59", "59 Verify most common MIME types</h1>");
		testCaseMap.put("60", "60 Verify resource name construction</h1>");
		testCaseMap
				.put("61",
						"61 Verification of authentication under handleSecurity()</h1>");
		testCaseMap.put("62", "62 Verification of ServletContext sharing</h1>");
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		String testCase = request.getParameter("TestCase");
		if ("3".equals(testCase) || "4".equals(testCase)) {
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			out.println(pagePart1 + testCaseMap.get(testCase)
					+ "<p>Servlet aa</p>" + pagePart2);
		}
		else
			if ("8".equals(testCase)) {
				response.setContentType("text/html");
				PrintWriter out = response.getWriter();
				out.println(pagePart1 + testCaseMap.get(testCase)
						+ "<p>Lowercase Servlet</p>" + pagePart2);
			}
			else
				if ("50".equals(testCase)) {
				}
				else
					if ("52".equals(testCase)) {
						response.setContentType("text/html");
						PrintWriter out = response.getWriter();
						out.println(pagePart1 + testCaseMap.get(testCase));
						throwIOException();
						out.println(pagePart2);
					}
					else
						if ("53".equals(testCase)) {
							int r = 0;
							int size = 4 * 1024 * 1024;
							char[] osgi = {'O', 'S', 'G', 'I'};
							char[] buf = new char[1024];
							for (int i = 0; i < buf.length; i += 4) {
								System.arraycopy(osgi, 0, buf, 0, osgi.length);
							}
							response.setContentType("text/plain");
							PrintWriter out = response.getWriter();
							for (r = 0; r < size; r = r + buf.length) {
								out.write(buf);
								out.flush();
							}
						}
						//Remove tc54
						//else if ("54".equals (testCase))
						//{
						//  String serverInfo =
						// getServletConfig().getServletContext().getServerInfo();
						//  response.setContentType ("text/html");
						//  PrintWriter out = response.getWriter ();
						//  out.println (pagePart1 + testCaseMap.get (testCase));
						//  out.println ("<p>Server Info: " + serverInfo +
						// "</p>");
						//  out.println (pagePart2);
						//}
						else
							if ("56".equals(testCase) || "57".equals(testCase)
									|| "58a".equals(testCase)) {
								response.setContentType("text/html");
								PrintWriter out = response.getWriter();
								out.println(pagePart1
										+ testCaseMap.get(testCase));
								String[] names = getSortedParameterNames(request);
								for (int i = 0; i < names.length; i++) {
									String p = names[i];
									String[] values = request
											.getParameterValues(p);
									out
											.print("<p>Parameter: " + p
													+ " Value: ");
									String del = "";
									if (values == null)
										out.print("null");
									else
										for (int j = 0; j < values.length; j++) {
											out.print(del + values[j]);
											del = ", ";
										}
									out.println("</p>");
								}
								out.println(pagePart2);
							}
							else
								if ("58b".equals(testCase)) {
									response.setContentType("text/html");
									PrintWriter out = response.getWriter();
									out.println(pagePart1
											+ testCaseMap.get(testCase));
									String[] names = getSortedParameterNames(request);
									for (int i = 0; i < names.length; i++) {
										String p = names[i];
										out.println("<p>Parameter: " + p
												+ " Value: ");
										String[] values = request
												.getParameterValues(p);
										quicksort(values, 0, values.length - 1);
										int loop = 0;
										while (loop < values.length) {
											out.print(values[loop]);
											if (!((loop + 1) >= values.length))
												out.print(",");
											loop++;
										}
										out.print("</p>");
									}
									out.println(pagePart2);
								}
								else
									if ("59".equals(testCase)) {
										String type = request
												.getParameter("type");
										if (type.compareTo("plain") == 0) {
											response
													.setContentType("text/plain");
											PrintWriter out = response
													.getWriter();
											out
													.println("OSGi - HTTP Service test case");
											out
													.println("59 Verify most common MIME types");
										}
										else
											if (type.compareTo("html") == 0) {
												response
														.setContentType("text/html");
												PrintWriter out = response
														.getWriter();
												out.println(pagePart1
														+ testCaseMap
																.get(testCase)
														+ pagePart2);
											}
											else
												if (type.compareTo("jpeg") == 0) {
													response
															.setContentType("image/jpeg");
													ServletOutputStream out = response
															.getOutputStream(); // binary
													// output
													InputStream in = (InputStream) AccessController
															.doPrivileged(new PrivilegedAction() {
																public Object run() {
																	return getClass()
																			.getResourceAsStream(
																					"www/car.jpg");
																}
															});
													byte buffer[] = new byte[4096];
													int read;
													while ((read = in.read(
															buffer, 0,
															buffer.length)) != -1) {
														out.write(buffer, 0,
																read);
													}
												}
									}
									else
										if ("60".equals(testCase)) {
											response
													.setContentType("text/html");
											PrintWriter out = response
													.getWriter();
											out.println(pagePart1
													+ testCaseMap.get(testCase)
													+ "<p>RequestURI = "
													+ request.getRequestURI()
													+ "</p>" + pagePart2);
										}
										else
											if ("61".equals(testCase)) {
												response
														.setContentType("text/html");
												PrintWriter out = response
														.getWriter();
												//retrieving remote user and
												// authentication that is set
												//by the default authentication
												// mechanism
												out
														.println(pagePart1
																+ testCaseMap
																		.get(testCase)
																+ "<p>Remote User is "
																+ request
																		.getRemoteUser()
																+ ", Authentication Type is "
																+ request
																		.getAuthType()
																+ "</p>"
																+ pagePart2);
											}
											else
												if ("62".equals(testCase)) {
													response
															.setContentType("text/html");
													PrintWriter out = response
															.getWriter();
													out
															.println(pagePart1
																	+ testCaseMap
																			.get(testCase));
													ServletContext SC = getServletConfig()
															.getServletContext();
													String paramName = "param1";
													String value = "val1";
													SC.setAttribute(paramName,
															value);
													out
															.println("<p>Writing parameter: "
																	+ paramName
																	+ " with value: "
																	+ value
																	+ "</p>"
																	+ pagePart2);
												}
												else {
													response
															.setContentType("text/html");
													PrintWriter out = response
															.getWriter();
													out
															.println(pagePart1
																	+ testCaseMap
																			.get(testCase)
																	+ pagePart2);
												}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		// set content type and other response header fields first
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("Hanging!");
		try {
			wait(50000);
		}
		catch (InterruptedException ie) {
		}
	}

	private void throwIOException() throws IOException {
		throw new IOException();
	}

	private void quicksort(String[] a, int lo, int hi) {
		int i, j;
		String h, x;
		i = lo;
		j = hi;
		x = a[(lo + hi) / 2];
		do {
			while (a[i].compareTo(x) < 0)
				i++;
			while (a[j].compareTo(x) > 0)
				j--;
			if (i <= j) {
				h = a[i];
				a[i] = a[j];
				a[j] = h;
				i++;
				j--;
			}
		} while (i <= j);
		if (lo < j)
			quicksort(a, lo, j);
		if (i < hi)
			quicksort(a, i, hi);
	}

	private String[] getSortedParameterNames(HttpServletRequest request) {
		Vector names = new Vector();
		for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
			String p = (String) e.nextElement();
			if (!p.equals("TestCase"))
				names.addElement(p);
		}
		int size = names.size();
		String[] result = new String[size];
		names.copyInto(result);
		quicksort(result, 0, result.length - 1);
		return result;
	}
}
