package org.osgi.impl.bundle.annotations.export.user;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.impl.bundle.annotations.export.uses.ExportUsesProviderType;

@ProviderType
public interface ExportProviderType {
	ExportUsesProviderType provider();
}
