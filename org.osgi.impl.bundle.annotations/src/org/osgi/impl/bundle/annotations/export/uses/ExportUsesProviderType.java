package org.osgi.impl.bundle.annotations.export.uses;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.impl.bundle.annotations.export.nouses.ExportNousesProviderType;

@ProviderType
public interface ExportUsesProviderType {
	ExportNousesProviderType provider();
}
