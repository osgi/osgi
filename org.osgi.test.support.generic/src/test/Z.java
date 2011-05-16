package test;

import java.io.*;
import java.util.*;

class X<A> {
}

interface Y<B> {
}

public class Z<C> extends X<String> implements Y<Integer> {

	public class V<D> {
		public <E> void fooLCO(E e, C c, D d) {
		}
	}

	@SuppressWarnings("serial")
	public abstract class U extends Z<String> implements Appendable, Serializable {}
	
	// Constructors
	public Z() {
	}

	public Z(C c) {
	}

	// Fields
	public X<Y<C>>				field;
	public Z<Long>.V<Integer>	referenceToNestedClass;
	public V<C>					vc	= new V<C>();

	// Methods
	public <T> void method() {
	}

	// Test all possible declarations
	public <E> void foo(E e) {
	}

	public <E extends InputStream & Appendable> void fooCI(E e) {
	}

	public <E extends InputStream & Appendable & Serializable> void fooCII(E e) {
	}

	public <E extends Serializable & Appendable> void fooII(E e) {
	}

	public <E extends Appendable> void fooI(E e) {
	}

	public <E extends InputStream> void fooC(E e) {
	}

	public <E extends C> void fooP(E e) {
	}

	public <E, F> void foo(E e, F f) {
	}

	// test with variable in signature
	public <E> void fooLC(E e, C f) {
	}

	// test wildcards
	public Collection< ? >							wildcard_001;
	public Collection< ? extends Appendable>		wildcard_002;
	public Collection< ? super Appendable>			wildcard_003;
	public Collection< ? extends C>					wildcard_004;
	public Collection< ? super C>					wildcard_005;
	public Collection< ? extends Z<C>.V<Integer>>	wildcard_006;
	public Collection< ? super Z<C>.V<Integer>>		wildcard_007;
	
	
	// test compatibility
	public <E extends Appendable> Collection<E> compatibility_001() {return null; }
	public <F extends Appendable> Collection<F> compatibility_002() {return null; }
	public <F extends InputStream> Collection<F> compatibility_003() {return null; }
	
	public C[] typevarArray;

}
