package org.osgi.service.rest.client;

import java.net.URL;
import java.util.Map;

public interface RestClientFactory {

	RestClient createRestClient(URL url);
	
	RestClient createRestClient(URL url, Signer signer);
	
	public interface Signer {
		
		Map<String, String> sign(String url);
		
	}
	
}
