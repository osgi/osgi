package org.osgi.impl.bundle.annotations.export.nouses;

import org.osgi.annotation.versioning.ConsumerType;
import org.osgi.impl.bundle.annotations.export.uses.ExportUsesConsumerType;

@ConsumerType
public interface ExportNousesConsumerType {
	void consumer(ExportUsesConsumerType arg);
}
