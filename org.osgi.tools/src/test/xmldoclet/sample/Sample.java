package test.xmldoclet.sample;

import java.io.*;
import java.util.*;

/**
 * 
 * 
 * @param <T> This is a T
 * @param <K> This is a K
 */
public class Sample<T, K extends Serializable> implements Collection<String> {
	public <X extends Collection<X>> Collection<Integer> foo(X x, String ...strings ) { return null; }

	public <X>  int xyz(){ return 0; }
	
	/**
	 * 
	 */
	public List<Map<T, K>>	list;

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

}
