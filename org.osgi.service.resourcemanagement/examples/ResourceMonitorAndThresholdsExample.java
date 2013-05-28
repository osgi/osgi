import java.util.Collection;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.resource.MemoryMonitor;
import org.osgi.framework.resource.ResourceContext;
import org.osgi.framework.resource.ResourceMonitorFactory;
import org.osgi.framework.resource.ResourceThreshold;
import org.osgi.framework.resource.exception.ThresholdException;

public class ResourceMonitorAndThresholdsExample {

	private static BundleContext	bundleContext;
	private static ResourceContext	resourceContext;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// the following code may take place in the Resource Manager

		ResourceMonitorFactory memResourceMonitorFactory = null;
		try {
			Collection references = bundleContext.getServiceReferences(ResourceMonitorFactory.class, "mem");
			memResourceMonitorFactory = bundleContext.getService((ServiceReference) references.iterator().next());
		} catch (InvalidSyntaxException e) {
		}

		// create a new CPU Monitor instance
		MemoryMonitor memMonitor = (MemoryMonitor) memResourceMonitorFactory.createResourceMonitor(resourceContext);
		
		ResourceThreshold maxThreshold = null; // = new ResourceThreshold() {
												// ... }
		maxThreshold.setErrorThreshold(9000);
		maxThreshold.setWarningThreshold(8500);
		try {
			memMonitor.setMaximumThreshold(maxThreshold);
		} catch (ThresholdException e) {}

	}

}
