package osgi.jndi.test;

import java.util.*;

import javax.naming.*;

public class DummyContext implements Context {
	String dummy;
	
	public DummyContext(String string) {
		this.dummy = string;
	}

	public Object addToEnvironment(String propName, Object propVal)
			throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public void bind(Name name, Object obj) throws NamingException {
		// TODO Auto-generated method stub

	}

	public void bind(String name, Object obj) throws NamingException {
		// TODO Auto-generated method stub

	}

	public void close() throws NamingException {
		// TODO Auto-generated method stub

	}

	public Name composeName(Name name, Name prefix) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public String composeName(String name, String prefix)
			throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public Context createSubcontext(Name name) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public Context createSubcontext(String name) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public void destroySubcontext(Name name) throws NamingException {
		// TODO Auto-generated method stub

	}

	public void destroySubcontext(String name) throws NamingException {
		// TODO Auto-generated method stub

	}

	public Hashtable< ? , ? > getEnvironment() throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getNameInNamespace() throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public NameParser getNameParser(Name name) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public NameParser getNameParser(String name) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public NamingEnumeration<NameClassPair> list(Name name)
			throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public NamingEnumeration<NameClassPair> list(String name)
			throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public NamingEnumeration<Binding> listBindings(Name name)
			throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public NamingEnumeration<Binding> listBindings(String name)
			throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object lookup(Name name) throws NamingException {
		return lookup(name.toString());
	}

	public Object lookup(String name) throws NamingException {
		return dummy + "-" + name;
	}

	public Object lookupLink(Name name) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object lookupLink(String name) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public void rebind(Name name, Object obj) throws NamingException {
		// TODO Auto-generated method stub

	}

	public void rebind(String name, Object obj) throws NamingException {
		// TODO Auto-generated method stub

	}

	public Object removeFromEnvironment(String propName) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public void rename(Name oldName, Name newName) throws NamingException {
		// TODO Auto-generated method stub

	}

	public void rename(String oldName, String newName) throws NamingException {
		// TODO Auto-generated method stub

	}

	public void unbind(Name name) throws NamingException {
		// TODO Auto-generated method stub

	}

	public void unbind(String name) throws NamingException {
		// TODO Auto-generated method stub

	}

}
