package org.osgi.service.obr.analysis;

import org.osgi.service.obr.Resolution;

public interface AnalysisAdmin {
	Analysis analyse(Resolution resolution);
}
