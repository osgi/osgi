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

package org.osgi.test.cases.blueprint.namespace;

import java.net.URL;
import java.text.SimpleDateFormat;

import org.osgi.framework.BundleContext;
import org.osgi.service.blueprint.namespace.NamespaceHandler;
import org.osgi.service.blueprint.namespace.ParserContext;
import org.osgi.service.blueprint.reflect.ComponentMetadata;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Sample handler for a "date" namespace based on the example given here:
 * http://static.springframework.org/spring/docs/2.5.x/reference/extensible-xml.html
 *
 * Handles "dateformat" elements with the following form:
 *
 * <date:dateformat id="dateFormat"
 *   pattern="yyyy-MM-dd HH:mm"
 *   lenient="true"/>
 *
 */
public class DateNamespaceHandler implements NamespaceHandler {

    /**
     * The schema file is packaged in the same bundle as this handler class
     * for convenience, in schemas/date.xsd
     */
    private static final String SCHEMA_LOCATION = "schemas/date.xsd";

    private final BundleContext bundleContext;

    public DateNamespaceHandler(BundleContext context) {
        this.bundleContext = context;
    }

    /**
     * Use the bundleContext to return a URL for accessing the schema definition file
     */
    public URL getSchemaLocation(String namespace)  throws IllegalArgumentException {
        return bundleContext.getBundle().getResource(SCHEMA_LOCATION);
    }

    /**
     * Date elements can never be used nested directly inside a component, so
     * nothing for us to do.
     */
    public ComponentMetadata decorate(Node node, ComponentMetadata component, ParserContext context) {
        return null;
    }


    /**
     * Handle the "dateformat" tag (and others in time...)
     */
    public ComponentMetadata parse(Element element, ParserContext context) {

        if (element.getLocalName().equals("dateformat")) {
            return parseDateFormat(element);
        }
        else {
            throw new IllegalStateException("Asked to parse unknown tag: " + element.getTagName());
        }
    }

    /**
     * A dataformat element defines a component, which could be a top-level named
     * component or an anonymous inner component.
     */
    private ComponentMetadata parseDateFormat(Element element) {
        String name = element.hasAttribute("id") ? element.getAttribute("id") : "";
        LocalComponentMetadataImpl componentMetadata = new LocalComponentMetadataImpl(name);
        componentMetadata.setClassName(SimpleDateFormat.class.getName());

        // required attribute pattern
        String pattern = element.getAttribute("pattern");
        componentMetadata.addConstructorArg(pattern, 0);

        // this however is an optional property
        String lenient = element.getAttribute("lenient");
        if ((lenient != null) && !lenient.equals("")) {
            componentMetadata.addProperty("lenient", lenient);
        }

        return componentMetadata;
    }
}

