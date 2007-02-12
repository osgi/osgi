package test;

import junit.framework.TestCase;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.Constants;
import org.osgi.tools.xmldoclet.HtmlCleaner;

public class TestHtmlCleaner extends TestCase {

	public void testSimple() {
		assertClean("<ol><li>1</li></ol>", "<ol><li>1");
		assertClean("<ol><li>1<ul><li>22</li></ul></li></ol>",
				"<ol><li>1<ul><li>22</ul>");

		String s = "<p>"
				+ "Otherwise, the following steps are required to start this bundle:\n"
				+ "<ol>"
				+ "<li>If this bundle is in the process of being activated or deactivated\n"
				+ "then this method must wait for activation or deactivation to complete\n"
				+ "before continuing. If this does not occur in a reasonable time, a\n"
				+ "<code>BundleException</code> is thrown to indicate this bundle was\n"
				+ "unable to be started.\n"
				+ "\n"
				+ "<li>If this bundle's state is <code>ACTIVE</code> then this method\n"
				+ "returns immediately.\n"
				+ "\n"
				+ "<li>If the {@link #START_TRANSIENT} option is not set then set this\n"
				+ "bundle's autostart setting to <em>Started with declared activation</em>\n"
				+ "if the {@link #START_ACTIVATION_POLICY} option is set or\n"
				+ "<em>Started with eager activation</em> if not set. When the Framework\n"
				+ "is restarted and the bundle's autostart setting is not <em>Stopped</em>,\n"
				+ "this bundle must be automatically started.\n"
				+ "\n"
				+ "<li>If this bundle's state is not <code>RESOLVED</code>, an attempt\n"
				+ "is made to resolve this bundle. If the Framework cannot resolve this\n"
				+ "bundle, a <code>BundleException</code> is thrown.\n"
				+ "\n"
				+ "<li>If the {@link #START_ACTIVATION_POLICY} option is set and this\n"
				+ "bundle's declared activation policy is\n"
				+ "{@link Constants#ACTIVATION_LAZY lazy} then:\n"
				+ "<ul>\n"
				+ "<li>If this bundle's state is <code>STARTING</code> then this method\n"
				+ "returns immediately.\n"
				+ "<li>This bundle's state is set to <code>STARTING</code>.\n"
				+ "<li>A bundle event of type {@link BundleEvent#LAZY_ACTIVATION} is fired.\n"
				+ "<li>This method returns immediately and the remaining steps will be\n"
				+ "followed when this bundle's activation is later triggered.\n"
				+ "</ul>\n"
				+ "\n"
				+ "<i></i>This bundle's state is set to <code>STARTING</code>.\n"
				+ "\n"
				+ "<li>A bundle event of type {@link BundleEvent#STARTING} is fired.\n"
				+ "\n"
				+ "<li>The {@link BundleActivator#start} method of this bundle's\n"
				+ "<code>BundleActivator</code>, if one is specified, is called. If the\n"
				+ "<code>BundleActivator</code> is invalid or throws an exception then:\n"
				+ "<ul>\n"
				+ "<li>This bundle's state is set to <code>STOPPING</code>.\n"
				+ "<li>A bundle event of type {@link BundleEvent#STOPPING} is fired.\n"
				+ "<li>Any services registered by this bundle must be unregistered.\n"
				+ "<li>Any services used by this bundle must be released.\n"
				+ "<li>Any listeners registered by this bundle must be removed.\n"
				+ "<li>This bundle's state is set to <code>RESOLVED</code>.\n"
				+ "<li>A bundle event of type {@link BundleEvent#STOPPED} is fired.\n"
				+ "<li>A <code>BundleException</code> is then thrown.\n"
				+ "</ul>\n"
				+ "<li>If this bundle's state is <code>UNINSTALLED</code>, because this\n"
				+ "bundle was uninstalled while the <code>BundleActivator.start</code>\n"
				+ "method was running, a <code>BundleException</code> is thrown.\n"
				+ "\n"
				+ "<li>This bundle's state is set to <code>ACTIVE</code>.\n"
				+ "\n"
				+ "<li>A bundle event of type {@link BundleEvent#STARTED} is fired.\n"
				+ "</ol>\n";
		String actual = new HtmlCleaner(s).clean();
		System.out.println(actual);
	}

	void assertClean(String expected, String input) {
		String actual = new HtmlCleaner(input).clean();
		assertEquals(expected, actual);
	}
}
