package org.osgi.test.cases.webservice.webservices;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

@WebService(targetNamespace = WSEcho.ECHO_NS)
public class WSEcho {
    public static final String ECHO_NS = "http://echo.webservice.osgi.org";

	@WebMethod(operationName = "echoAction", action = "echo")
    public String echo(@WebParam(name = "textIn") String text) {
    	return text;
    }
}