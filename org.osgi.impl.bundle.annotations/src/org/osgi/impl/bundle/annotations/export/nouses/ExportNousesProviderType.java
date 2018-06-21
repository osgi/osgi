package org.osgi.impl.bundle.annotations.export.nouses;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.impl.bundle.annotations.export.uses.ExportUsesProviderType;

@ProviderType
public interface ExportNousesProviderType {
	ExportUsesProviderType provider();
}
