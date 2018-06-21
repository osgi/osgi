package org.osgi.impl.bundle.annotations.export.uses;

import org.osgi.annotation.versioning.ConsumerType;
import org.osgi.impl.bundle.annotations.export.nouses.ExportNousesConsumerType;

@ConsumerType
public interface ExportUsesConsumerType {
	void consumer(ExportNousesConsumerType arg);
}
