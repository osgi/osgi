package org.osgi.impl.bundle.annotations.export.substitution;

public class ExportSubstitution {
	public static class Default implements
			org.osgi.impl.bundle.annotations.export.ExportConsumerType {
		//
	}

	public static class Calculated implements
			org.osgi.impl.bundle.annotations.export.calculated.ExportProviderType {
		//
	}

	public static class Consumer implements
			org.osgi.impl.bundle.annotations.export.consumer.ExportProviderType {
		//
	}

	public static class Noimport implements
			org.osgi.impl.bundle.annotations.export.noimport.ExportProviderType {
		//
	}

	public static class Provider implements
			org.osgi.impl.bundle.annotations.export.provider.ExportConsumerType {
		//
	}
}
