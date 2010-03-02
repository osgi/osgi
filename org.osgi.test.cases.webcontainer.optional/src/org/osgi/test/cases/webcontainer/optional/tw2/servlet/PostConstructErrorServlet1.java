/*
 * Copyright (c) IBM Corporation (2009). All Rights Reserved.
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

package org.osgi.test.cases.webcontainer.optional.tw2.servlet;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.test.cases.webcontainer.util.ConstantsUtil;
import org.osgi.test.cases.webcontainer.optional.util.Event;
import org.osgi.test.cases.webcontainer.optional.util.EventLogger;

/**
 * @version $Rev$ $Date$
 * 
 *          Servlet implementation class BasicAnnotationServlet2
 */
public class PostConstructErrorServlet1 extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public PostConstructErrorServlet1() {
        super();
        // TODO Auto-generated constructor stub
    }

    @PostConstruct
    public void postConstruct1() {
        EventLogger.logEvent(new Event(this.getClass().getName(),
                ConstantsUtil.POSTCONSTRUCT, ConstantsUtil.POSTCONSTRUCTDESP));
    }

    @PostConstruct
    public void postConstruct2() {
        EventLogger.logEvent(new Event(this.getClass().getName(),
                ConstantsUtil.PREDESTROY, ConstantsUtil.POSTCONSTRUCTDESP2));
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
    }

}
