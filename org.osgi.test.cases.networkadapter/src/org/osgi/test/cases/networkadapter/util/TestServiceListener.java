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
package org.osgi.test.cases.networkadapter.util;

import java.util.LinkedList;
import java.util.List;

import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

public class TestServiceListener implements ServiceListener {

	private List<ServiceReference< ? >>	list	= new LinkedList<>();
    private int type;

    public TestServiceListener(int type) {
        this.type = type;
    }

    @Override
	public void serviceChanged(ServiceEvent event) {
        if (event.getType() == type) {
			ServiceReference< ? > ref = event.getServiceReference();
            list.add(ref);

            System.out.println("Callback: " + ref);
        }
    }

    public int size() {
        return list.size();
    }

    public void clear() {
        list.clear();
    }

	public ServiceReference< ? > get(int index) {
        return list.get(index);
    }
}
