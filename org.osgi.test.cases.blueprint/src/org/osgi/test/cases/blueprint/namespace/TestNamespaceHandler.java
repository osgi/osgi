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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.service.blueprint.namespace.ComponentDefinitionRegistry;
import org.osgi.service.blueprint.namespace.ComponentNameAlreadyInUseException;
import org.osgi.service.blueprint.namespace.NamespaceHandler;
import org.osgi.service.blueprint.namespace.ParserContext;
import org.osgi.service.blueprint.reflect.BeanArgument;
import org.osgi.service.blueprint.reflect.ComponentMetadata;
import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.BaseTestComponent;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Simple handler for testing the processing of
 * multiple namespaces by a single handler.  This handler
 * will only process decorate events, as the elements
 * and attributes it defines are extensions to existing
 * elements.
 */
public class TestNamespaceHandler extends BaseTestComponent implements NamespaceHandler {

    /**
     * The schema file is packaged in the same bundle as this handler class
     * for convenience, in schemas/date.xsd
     */
    private static final String SCHEMA_LOCATION = "schemas/date.xsd";

    private final Map schemas = new HashMap();

    private final BundleContext bundleContext;

    public TestNamespaceHandler(BundleContext context) {
        super("TestNamespaceHandler");
        this.bundleContext = context;
        // create a map of the schemas we handle
        schemas.put("http://www.osgi.org/schema/blueprint/test/test1/v1.0.0",
            bundleContext.getBundle().getResource("schemas/test1.xsd"));
        schemas.put("http://www.osgi.org/schema/blueprint/test/test1",
            bundleContext.getBundle().getResource("schemas/test1.xsd"));
        schemas.put("http://www.osgi.org/schema/blueprint/test/test2/v1.0.0",
            bundleContext.getBundle().getResource("schemas/test2.xsd"));
        schemas.put("http://www.osgi.org/schema/blueprint/test/test2",
            bundleContext.getBundle().getResource("schemas/test2.xsd"));
    }

    /**
     * Use the bundleContext to return a URL for accessing the schema definition file
     */
    public URL getSchemaLocation(String namespace)  throws IllegalArgumentException {
        URL url = (URL)schemas.get(namespace);
        if (url == null) {
            throw new IllegalArgumentException("Unknown schema: " + namespace);
        }
        return url;
    }

    /**
     * Date elements can never be used nested directly inside a component, so
     * nothing for us to do.
     */
    public ComponentMetadata decorate(Node node, ComponentMetadata component, ParserContext context) {
        AssertionService.sendEvent(this, AssertionService.METHOD_CALLED);

        if (node instanceof Attr) {
            Attr attribute = (Attr)node;
            if (attribute.getLocalName().equals("sleepy")) {
                // make a copy of the metadata
                BeanMetadataImpl metadata = (BeanMetadataImpl)NamespaceUtil.cloneMetadata(component);
                metadata.setLazyInit(attribute.getValue().equals("on"));
                return metadata;
            }
            else if (attribute.getLocalName().equals("init")) {
                // make a copy of the metadata
                BeanMetadataImpl metadata = (BeanMetadataImpl)NamespaceUtil.cloneMetadata(component);
                metadata.setInitMethodName(attribute.getValue());
                return metadata;
            }
            else if (attribute.getLocalName().equals("copy-component")) {
                // make a copy of the metadata, but don't decorate it.  This is used on all of the component elements,
                // not just BeanComponent
                ComponentMetadata metadata = (ComponentMetadata)NamespaceUtil.cloneMetadata(component);
                return metadata;
            }
            else if (attribute.getLocalName().equals("raise-error")) {
                // make a copy of the metadata
                if (attribute.getValue().equals("yes")) {
                    throw new IllegalArgumentException("raise-error=\"yes\" triggered exception");
                }
                // we can also use this to trigger replacement of a component
                return (ComponentMetadata)NamespaceUtil.cloneMetadata(component);
            }
            throw new IllegalArgumentException("Unknown attribute " + attribute.getLocalName());
        }
        else {
            Element element = (Element)node;
            // requesting a bundle property be set
            if (element.getLocalName().equalsIgnoreCase("bundle")) {
                // make a copy of the metadata
                BeanMetadataImpl metadata = (BeanMetadataImpl)NamespaceUtil.cloneMetadata(component);
                // the assigned value is a component reference
                RefMetadataImpl bundleRef = new RefMetadataImpl("bundle");
                BeanPropertyImpl property = new BeanPropertyImpl("bundle", bundleRef);
                metadata.addProperty(property);
                return metadata;
            }
            else if (element.getLocalName().equals("copy")) {
                // make a copy of the metadata, but don't decorate it.  This is used on all of the component elements,
                // not just BeanComponent
                return (ComponentMetadata)NamespaceUtil.cloneMetadata(component);
            }
            // we have a different value tag
            else if (element.getLocalName().equalsIgnoreCase("bundle-value")) {
                // make a copy of the metadata for our enclosing component
                BeanMetadataImpl metadata = (BeanMetadataImpl)NamespaceUtil.cloneMetadata(context.getEnclosingComponent());
                // we need to replace the definition of the
                Element parent = (Element)element.getParentNode();
                if (!parent.getLocalName().equals("property")) {
                    throw new IllegalArgumentException("<bundle-value> only valid as a child of <property> element");
                }
                // the assigned value is a component reference
                RefMetadataImpl bundleRef = new RefMetadataImpl("bundle");
                BeanPropertyImpl property = new BeanPropertyImpl(parent.getAttribute("name"), bundleRef);
                // replace the property definition with the fully realized value
                metadata.replaceProperty(property);
                return metadata;
            }
            // we have a different value tag
            else if (element.getLocalName().equalsIgnoreCase("raise-error")) {
                throw new IllegalArgumentException("<raise-error> triggered exception");
            }
            throw new IllegalArgumentException("Unknown element " + element.getLocalName());
        }
    }


    /**
     * Handle the "dateformat" tag (and others in time...)
     */
    public ComponentMetadata parse(Element element, ParserContext context) {
        AssertionService.sendEvent(this, AssertionService.METHOD_CALLED);
        // creates a component using a provided class
        if (element.getLocalName().equalsIgnoreCase("good")) {
            String id = element.getAttribute("id");
            BeanMetadataImpl bean = new BeanMetadataImpl(id);
            // this explicitly sets the class rather than using class name
            bean.setRuntimeClass(NamespaceGoodService.class);
            bean.addArgument(new BeanArgumentImpl(new ValueMetadataImpl(id)));
            return bean;
        }
        // creates a component using a provided class for the *factory*
        else if (element.getLocalName().equalsIgnoreCase("good-factory")) {
            String id = element.getAttribute("id");
            BeanMetadataImpl bean = new BeanMetadataImpl(id);
            // this explicitly sets the class rather than using class name
            bean.setRuntimeClass(NamespaceGoodFactory.class);
            bean.setFactoryMethodName("create");
            bean.addArgument(new BeanArgumentImpl(new ValueMetadataImpl(id)));
            return bean;
        }
        // this is a blanket replacement of all named components
        else if (element.getLocalName().equalsIgnoreCase("replace-all")) {
            ComponentDefinitionRegistry registry = context.getComponentDefinitionRegistry();
            Set names = registry.getComponentDefinitionNames();
            Iterator i = names.iterator();
            while (i.hasNext()) {
                try {
                    String name = (String)i.next();
                    // this should be there
                    AssertionService.assertTrue(this, "incorrect result from ComponentDefinitionRegistry.containsComponentDefinition(" + name + ")",
                        registry.containsComponentDefinition(name));
                    // retrieve the definition associated with this name
                    ComponentMetadata metadata = registry.getComponentDefinition(name);
                    AssertionService.assertNotNull(this, "null component returned from registry for (" + name + ")", metadata);
                    // clone this so we can perform a replacement with out version
                    metadata = (ComponentMetadata)NamespaceUtil.cloneMetadata(metadata);
                    try {
                        // and register the replacement version
                        registry.registerComponentDefinition(metadata);
                        AssertionService.fail(this, "Exception expected on registerComponentDefinition()");
                    } catch (ComponentNameAlreadyInUseException e) {
                        // this is expected
                    }
                    // remove the definition
                    registry.removeComponentDefinition(name);
                    AssertionService.assertFalse(this, "incorrect result from ComponentDefinitionRegistry.containsComponentDefinition(" + name + ")",
                        registry.containsComponentDefinition(name));
                    AssertionService.assertNull(this, "non-null component returned from registry for (" + name + ")", registry.getComponentDefinition(name));
                    // and register the replacement version
                    registry.registerComponentDefinition(metadata);
                    // and verify that the registered component returns something good
                    AssertionService.assertTrue(this, "incorrect result from ComponentDefinitionRegistry.containsComponentDefinition(" + name + ")",
                        registry.containsComponentDefinition(name));
                    AssertionService.assertNotNull(this, "non-null component returned from registry for (" + name + ")", registry.getComponentDefinition(name));
                } catch (Throwable e) {
                    AssertionService.fail(this, "Unexpected exception", e);
                }
            }
            return null;
        }
        // we have a different value tag
        else if (element.getLocalName().equalsIgnoreCase("bundle-value")) {
            // make a copy of the metadata for our enclosing component
            BeanMetadataImpl metadata = (BeanMetadataImpl)NamespaceUtil.cloneMetadata(context.getEnclosingComponent());
            // we need to replace the definition of the
            Element parent = (Element)element.getParentNode();
            if (!parent.getLocalName().equals("property")) {
                throw new IllegalArgumentException("<bundle-value> only valid as a child of <property> element");
            }
            // the assigned value is a component reference
            RefMetadataImpl bundleRef = new RefMetadataImpl("bundle");
            BeanPropertyImpl property = new BeanPropertyImpl(parent.getAttribute("name"), bundleRef);
            // replace the property definition with the fully realized value
            metadata.replaceProperty(property);
            return metadata;
        }
        // we have a different value tag
        else if (element.getLocalName().equalsIgnoreCase("raise-error")) {
            throw new IllegalArgumentException("<raise-error> triggered exception");
        }
        throw new IllegalArgumentException("Unknown element " + element.getLocalName());
    }
}

