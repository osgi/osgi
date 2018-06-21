package org.osgi.impl.bundle.annotations.export.user;

import org.osgi.annotation.versioning.ConsumerType;
import org.osgi.impl.bundle.annotations.export.uses.ExportUsesConsumerType;

@ConsumerType
public interface ExportConsumerType {
	void consumer(ExportUsesConsumerType arg);
}
