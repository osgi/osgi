// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   MidletContainer.java

package org.osgi.impl.service.midletcontainer;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.ProtectionDomain;
import org.osgi.framework.Bundle;

class MIDletClassLoader extends ClassLoader
{

    public MIDletClassLoader(ClassLoader parent, Bundle bundle, ProtectionDomain protectionDomain)
    {
        super(parent);
        this.bundle = bundle;
        this.protectionDomain = protectionDomain;
    }

    protected Class findClass(String name)
        throws ClassNotFoundException
    {
        byte b[] = loadClassData(name);
        return defineClass(name, b, 0, b.length, protectionDomain);
    }

    private byte[] loadClassData(String name)
        throws ClassNotFoundException
    {
        byte data[];
        String classFile = name.replace('.', '/') + ".class";
        URL url = bundle.getResource(classFile);
        if(url == null)
            throw new ClassNotFoundException();
        URLConnection connection = url.openConnection();
        int length = connection.getContentLength();
        data = new byte[length];
        InputStream input = connection.getInputStream();
        try
        {
            input.read(data);
        }
        finally
        {
            input.close();
        }
        return data;
        Exception e;
        e;
        throw new ClassNotFoundException("Cannot load the required class!", e);
    }

    private Bundle bundle;
    private ProtectionDomain protectionDomain;
}
