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
package org.osgi.test.cases.jakartaws.junit.handler;

import java.util.concurrent.atomic.AtomicInteger;

import jakarta.xml.ws.handler.LogicalHandler;
import jakarta.xml.ws.handler.LogicalMessageContext;
import jakarta.xml.ws.handler.MessageContext;

public class TestLogicalHandler implements LogicalHandler<LogicalMessageContext> {

	public AtomicInteger handledMessages = new AtomicInteger();

	@Override
	public boolean handleMessage(LogicalMessageContext context) {
		int msg = handledMessages.incrementAndGet();
		System.out.println("TestLogicalHandler.handleMessage no. " + msg);
		return true;
	}

	@Override
	public boolean handleFault(LogicalMessageContext context) {
		System.out.println("TestLogicalHandler.handleFault()");
		return true;
	}

	@Override
	public void close(MessageContext context) {
		System.out.println("TestLogicalHandler.close()");
	}

}
