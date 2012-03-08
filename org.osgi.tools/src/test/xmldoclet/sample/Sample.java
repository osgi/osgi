package test.xmldoclet.sample;

import java.io.*;
import java.util.*;

import org.osgi.framework.*;




/**
 * 
 * 
 * @param <T> This is a T
 * @param <K> This is a K
 */
@Ann(integer=1, integers=2, type=Object.class, string="string", enums=Ann.X.A)
public class Sample<T, K extends Serializable> implements Collection<String>, Interface {
	public <X extends Collection<X>> Collection<Integer> foo(X x, String ...strings ) { return null; }

	
	public interface X<S> extends Comparable<Object> {}

	
	public void find( Collection<X< ? >> refs) {}
	
	
	public <X>  int xyz(){ return 0; }
	
	/**
	 * 
	 */
	public List<Map<T, K>>	list;

	/**
	 * The permission to be updated, that is, act as a Managed Service or
	 * Managed Service Factory.The action string {@value #UPDATED}.
	 */
	public final static String	UPDATED				= "updated";

	/**
	 * 
	 * @param <X> an X
	 * @param a an a
	 * @param b a b
	 * @return whatever
	 */
	public <X extends T> X method(T a, X b) {
		return null;
	}
	
	public void list( List<T> a, List<String> b) {
		
	}

	public boolean add(String var0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean addAll(Collection< ? extends String> var0) {
		// TODO Auto-generated method stub
		return false;
	}

	public void clear() {
		// TODO Auto-generated method stub
		
	}

	public boolean contains(Object var0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean containsAll(Collection< ? > var0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	public Iterator<String> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean remove(Object var0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean removeAll(Collection< ? > var0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean retainAll(Collection< ? > var0) {
		// TODO Auto-generated method stub
		return false;
	}

	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> T[] toArray(T[] var0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<String> abc() { return null; }


	/**
	 * {@inheritDoc}
	 */
	public void foo() {
	}
	
}
