package org.osgi.test.service;

import java.lang.reflect.*;

import org.osgi.framework.*;

public interface ContextSharer  {

    public BundleContext getContext(); 
   
    public Object createObject(String clazz) throws Exception;

    public void invoke(Object o, Method m) throws Throwable;
}

