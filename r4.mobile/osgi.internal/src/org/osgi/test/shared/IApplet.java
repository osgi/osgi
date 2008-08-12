package org.osgi.test.shared;

import java.util.Dictionary;
import org.osgi.test.service.TestCase;

public interface IApplet {
	void setMessage(String message);

	void setError(String message);

	void setProgress(int percentage);

	void setResult(TestCase tc, int errors);

	void setTargetProperties(Dictionary properties);

	void finished();

	void bundlesChanged();

	void targetsChanged();

	void casesChanged();

	void alive(String msg);

	void step(String msg);
}
