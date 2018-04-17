```java

@RequiredCDIExtender
@Target(PACKAGE)
@Retention(CLASS)
public @interface Beans {
    /**
     * the set of classes from this package to include OR   
     * auto detect.
     */
    Class[] value() default {};
}

@RequiredCDIExtender
@Target(TYPE)
@Retention(CLASS)
public @interface Bean {
}

//@ApplicationScoped
@ComponentScoped
@ServiceInstance(BUNDLE)
class Foo interface @Service Bar, @Service Other, Serializable {}


```

