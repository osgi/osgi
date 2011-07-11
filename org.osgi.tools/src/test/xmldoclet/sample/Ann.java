package test.xmldoclet.sample;

public @interface Ann {
	public enum X { A, B };
	
	int integer();
	int[] integers();
	String string();
	Class<?> type();
	X[] enums();
	
	int deflt() default 1;
}
