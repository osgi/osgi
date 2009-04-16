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

package org.osgi.test.cases.webcontainer.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @version $Rev$ $Date$
 */
public class Dispatcher {
    
    public static String dispatch(HttpURLConnection conn) throws Exception { 
        String inputLine = new String();
        StringBuffer resbuf = new StringBuffer();
        BufferedReader in = null;
        
        in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        while ((inputLine = in.readLine()) != null) {
            resbuf.append(inputLine);
        }
        in.close();
        return resbuf.toString();
    }
    
    public static URL createURL(String request, Server server) throws Exception {
        final String urlstr = "http://" + server.getHost() + ":" + server.getPort() + request;
        return new URL(urlstr);
    }

}
