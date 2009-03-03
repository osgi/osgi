package org.osgi.test.cases.blueprint.tests;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import org.osgi.test.cases.blueprint.components.injection.ConstructorInjection;
import org.osgi.test.cases.blueprint.components.injection.PropertyInjection;
import org.osgi.test.cases.blueprint.framework.ConstructorMetadataValidator;
import org.osgi.test.cases.blueprint.framework.MetadataEventSet;
import org.osgi.test.cases.blueprint.framework.PropertyMetadataValidator;
import org.osgi.test.cases.blueprint.framework.StandardTestController;
import org.osgi.test.cases.blueprint.framework.StringParameter;
import org.osgi.test.cases.blueprint.framework.TestNullValue;
import org.osgi.test.cases.blueprint.framework.TestParameter;
import org.osgi.test.cases.blueprint.framework.TestProperty;
import org.osgi.test.cases.blueprint.framework.TestStringValue;
import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public class TestClasstypeInjection extends DefaultTestBundleControl {

    private void addConstructorValidator(MetadataEventSet startEvents, String id, Object value, Class parameterType, String stringValue, Class valueType) {
        // this is what we expect to see set as a result
        startEvents.validateComponentArgument(id, "arg1", value, parameterType);
        startEvents.addValidator(new ConstructorMetadataValidator(id, new TestParameter[] {
        // all of these tests just have the type argument.
                new StringParameter(parameterType, stringValue, valueType) }));
    }

    // we only add metadata validators for a subset of these tests...they get fairly repetitive.
    // this version just test that the correct value is set
    private void addConstructorValidator(MetadataEventSet startEvents, String id, Object value, Class parameterType) {
        // this is what we expect to see set as a result
        startEvents.validateComponentArgument(id, "arg1", value, parameterType);
    }

    // this version handles the null values
    private void addConstructorValidator(MetadataEventSet startEvents, String id, Class parameterType) {
        // this is what we expect to see set as a result
        startEvents.validateComponentArgument(id, "arg1", null, parameterType);
        startEvents.addValidator(new ConstructorMetadataValidator(id, new TestParameter[] {
                // this is a Null value type to validate against.
                new TestParameter(new TestNullValue(), parameterType)}));
    }

    private void addConstructorTestItem(MetadataEventSet startEvents)throws Exception{
        // URL tests
        this.addConstructorValidator(startEvents, "compURL", new URL("http://www.osgi.org"), URL.class, "http://www.osgi.org", null);
        this.addConstructorValidator(startEvents, "compURLNull", URL.class);
        this.addConstructorValidator(startEvents, "compURL_EleValue", new URL("http://www.osgi.org"), null, "http://www.osgi.org", URL.class);

        // Date tests
        this.addConstructorValidator(startEvents, "compDate", (new SimpleDateFormat("MM/dd/yyyy")).parse("6/1/1999"), Date.class, "6/1/1999", null);
        this.addConstructorValidator(startEvents, "compDate2", (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).parse("2000-12-31 23:59:59"), Date.class, "2000-12-31 23:59:59", null);
        this.addConstructorValidator(startEvents, "compDate3", new Date(0), Date.class, "January 1, 1970, 00:00:00 GMT", null);
        this.addConstructorValidator(startEvents, "compDateNull", Date.class);
        this.addConstructorValidator(startEvents, "compDate_EleValue", (new SimpleDateFormat("MM/dd/yyyy")).parse("1/1/1970"), null, "1970-1-1", Date.class);

        // Locale tests
        this.addConstructorValidator(startEvents, "compLocale", new Locale("en"), Locale.class, "en", null);
        this.addConstructorValidator(startEvents, "compLocale2", new Locale("zh", "cn"), Locale.class, "zh_CN", null);
        this.addConstructorValidator(startEvents, "compLocale3", new Locale("en", "US", "WIN"), Locale.class, "en_US_WIN", null);
        this.addConstructorValidator(startEvents, "compLocaleNull", Locale.class);
        this.addConstructorValidator(startEvents, "compLocale_EleValue", new Locale("en_US"), null, "en_US", Locale.class);

        // "Class" tests
        this.addConstructorValidator(startEvents, "compClass", String.class, Class.class, "java.lang.String", null);
        this.addConstructorValidator(startEvents, "compClassNull", Class.class);     // this String.class is at will
        this.addConstructorValidator(startEvents, "compClass_EleValue", ConstructorInjection.class, null, "org.osgi.test.cases.blueprint.components.injection.ConstructorInjection", ConstructorInjection.class);

        // "Properties" tests
        Properties properties = new Properties();
        properties.put("Name", "Rex");
        this.addConstructorValidator(startEvents, "compProperties", properties, Properties.class, "Name=Rex", null);
        Properties properties_EleValue = new Properties();
        properties_EleValue.put("Name", "Rex");
        properties_EleValue.put("Organization", "OSGi Alliance");
        properties_EleValue.put("Nationality", "China");
        properties_EleValue.put("Language", "\u4e2d\u6587, English");
        properties_EleValue.put("Graduated", "Shanghai Jiao Tong University");
        properties_EleValue.put("Major", "Computer Science");
        properties_EleValue.put("Gender", "");
        this.addConstructorValidator(startEvents, "compProperties_EleValue", properties_EleValue, Properties.class);
        this.addConstructorValidator(startEvents, "compPropertiesEmpty", new Properties(), Properties.class);
        this.addConstructorValidator(startEvents, "compPropertiesNull", null, Properties.class);
    }

    public void testConstructorInjection() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(), getWebServer() + "www/classtype_constructor_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        this.addConstructorTestItem(startEvents);

        controller.run();
    }

    public void testInstanceFactoryConstructorInjection() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(), getWebServer() + "www/classtype_factory_constructor_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        this.addConstructorTestItem(startEvents);

        controller.run();
    }

    public void testStaticFactoryConstructorInjection() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(), getWebServer() + "www/classtype_static_factory_constructor_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        this.addConstructorTestItem(startEvents);

        controller.run();
    }

    private void addPropertyValidator(MetadataEventSet startEvents, String compName, String propertyName,
            Object propertyValue, Class propertyType, String stringValue, Class type) {
        startEvents.validateComponentProperty(compName, propertyName, propertyValue, propertyType);
        startEvents.addValidator(new PropertyMetadataValidator(compName, new TestProperty[] {
                new TestProperty(new TestStringValue(type, stringValue), propertyName)}));
    }

    private void addPropertyValidator(MetadataEventSet startEvents, String compName, String propertyName,
            Object propertyValue, Class propertyType) {
        startEvents.validateComponentProperty(compName, propertyName, propertyValue, propertyType);
    }

    private void addPropertyValidator(MetadataEventSet startEvents, String compName, String propertyName,
            Class propertyType) {
        startEvents.validateComponentProperty(compName, propertyName, null, propertyType);
        startEvents.addValidator(new PropertyMetadataValidator(compName, new TestProperty[] {
            new TestProperty(new TestNullValue(), propertyName)}));
    }

    private void addPropertyTestItem(MetadataEventSet startEvents)throws Exception{
        // URL tests
        this.addPropertyValidator(startEvents, "compURL", "url", new URL("http://www.osgi.org"), URL.class, "http://www.osgi.org", null);
        this.addPropertyValidator(startEvents, "compURLNull", null, URL.class);
        this.addPropertyValidator(startEvents, "compURL_EleValue", "url", new URL("http://www.osgi.org"), null, "http://www.osgi.org", URL.class);

        // Date tests
        this.addPropertyValidator(startEvents, "compDate", "date", (new SimpleDateFormat("MM/dd/yyyy")).parse("6/1/1999"), Date.class);
        this.addPropertyValidator(startEvents, "compDate2", "date", (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).parse("2000-12-31 23:59:59"), Date.class);
        this.addPropertyValidator(startEvents, "compDate3", "date", new Date(0), Date.class);
        this.addPropertyValidator(startEvents, "compDateNull", "date", Date.class);
        this.addPropertyValidator(startEvents, "compDate_EleValue", "date", (new SimpleDateFormat("MM/dd/yyyy")).parse("1/1/1970"), Date.class);

        // Locale tests
        this.addPropertyValidator(startEvents, "compLocale", "locale", new Locale("en"), Locale.class);
        this.addPropertyValidator(startEvents, "compLocale2", "locale", new Locale("zh", "cn"), Locale.class);
        this.addPropertyValidator(startEvents, "compLocale3", "locale", new Locale("en", "US", "WIN"), Locale.class);
        this.addPropertyValidator(startEvents, "compLocaleNull", "locale", Locale.class);
        this.addPropertyValidator(startEvents, "compLocale_EleValue", "locale", new Locale("en_US"), Date.class);

        // "Class" tests
        this.addPropertyValidator(startEvents, "compClass", "class", String.class, Class.class);
        this.addPropertyValidator(startEvents, "compClassNull", "class", Class.class);
        // this case is different from the ConstructorInjection
        this.addPropertyValidator(startEvents, "compClass_EleValue", "class", PropertyInjection.class, Class.class, "org.osgi.test.cases.blueprint.components.injection.ConstructorInjection", Class.class);

        // "Properties" tests
        Properties properties = new Properties();
        properties.put("Name", "Rex");
        this.addPropertyValidator(startEvents, "compProperties", "properties", properties, Properties.class);
        Properties properties_EleValue = new Properties();
        properties_EleValue.put("Name", "Rex");
        properties_EleValue.put("Organization", "OSGi Alliance");
        properties_EleValue.put("Nationality", "China");
        properties_EleValue.put("Language", "\u4e2d\u6587, English");
        properties_EleValue.put("Graduated", "Shanghai Jiao Tong University");
        properties_EleValue.put("Major", "Computer Science");
        properties_EleValue.put("Gender", "");
        this.addPropertyValidator(startEvents, "compProperties_EleValue", "properties", properties_EleValue, Properties.class);
        this.addPropertyValidator(startEvents, "compPropertiesEmpty", "properties", new Properties(), Properties.class);
        this.addPropertyValidator(startEvents, "compPropertiesNull", "properties", null, Properties.class);
    }

    public void testPropertyInjection() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer() + "www/classtype_property_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        this.addPropertyTestItem(startEvents);

        controller.run();
    }

    public void testInstanceFactoryPropertyInjection () throws Exception{
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer() + "www/classtype_factory_property_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        this.addPropertyTestItem(startEvents);

        controller.run();
    }

    public void testStaticFactoryPropertyInjection () throws Exception{
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer() + "www/classtype_static_factory_property_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        this.addPropertyTestItem(startEvents);

        controller.run();
    }

}
