/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
package org.osgi.test.cases.webservice.webservices;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import jakarta.xml.ws.handler.LogicalHandler;
import jakarta.xml.ws.handler.LogicalMessageContext;
import jakarta.xml.ws.handler.MessageContext;

public class ReversingHandler implements LogicalHandler<LogicalMessageContext> {

    @Override
    public boolean handleMessage(LogicalMessageContext context) {
    	Boolean outbound = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
    	if(!outbound) {
	    	try {
	    		StringWriter sw = new StringWriter();
				StreamResult result = new StreamResult(sw);
	
				Transformer t = TransformerFactory.newInstance().newTransformer();
				t.transform(context.getMessage().getPayload(), result);
				
				XMLEventReader reader = XMLInputFactory.newFactory()
						.createXMLEventReader(new StringReader(sw.toString()));
				
				sw = new StringWriter();
				
				XMLEventWriter writer = XMLOutputFactory.newFactory()
						.createXMLEventWriter(sw);
				
				XMLEventFactory eventFactory = XMLEventFactory.newFactory();
				while(reader.hasNext()) {
					XMLEvent nextEvent = reader.nextEvent();
					if(nextEvent.isCharacters()) {
						StringBuilder sb = new StringBuilder(nextEvent.asCharacters().getData()); 
						nextEvent = eventFactory.createCharacters(sb.reverse().toString());
					}
					writer.add(nextEvent);
				}
				
				context.getMessage().setPayload(new StreamSource(new StringReader(sw.toString())));
			} catch (Exception e) {
				throw new RuntimeException("Failed to change case of inputs", e);
			}
    	}
        return true;
    }

    public boolean handleFault(LogicalMessageContext context) {
        return true;
    }

    @Override
    public void close(MessageContext context) {
    }
}