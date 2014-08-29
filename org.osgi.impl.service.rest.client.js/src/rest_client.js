/*
 * Copyright (c) OSGi Alliance (2013). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * The OSGi REST API client
 * 
 * @author Jan S. Rellermeyer, IBM Research
 *
 * @param baseUrl
 *            the base URL to the REST API.
 * @returns the OSGi REST API client object
 */
function OsgiRestClient(baseUrl) {
	this.baseUrl = baseUrl;

	/**
	 * get the framework start level in JSON FrameworkStartLevel representation.
	 */
	this.getFrameworkStartLevel = function getFrameworkStartLevel() {
		$.ajax({
			url : this.baseUrl + "/framework/startlevel",
			type : "GET",
			dataType : "json",
			success : function(fw_sl) {
				console.log(fw_sl);
				return fw_sl;
			},
			error : function(xhr, status) {
				console.log(status);
				return xhr.responseText;
			}
		});
	}

	/**
	 * set the framework start level
	 * 
	 * @param the new framework startlevel in JSON representation.
	 * @returns the updated framework startlevel in JSON representation.
	 */
	this.setFrameworkStartLevel = function setFrameworkStartLevel(fw_sl) {
		$.ajax({
			url : this.baseUrl + "/framework/startlevel",
			type : "POST",
			dataType : "json",
			data : fw_sl,
			success : function(fw_sl) {
				console.log(fw_sl);
				return fw_sl;
			},
			error : function(xhr, status) {
				console.log(status);
				return xhr.responseText;
			}
		});
	}

	/**
	 * get the bundles
	 * 
	 * @returns the URI paths of the bundles as a JSON array of strings.
	 */
	this.getBundles = function getBundles() {
		$.ajax({
			url : this.baseUrl + "/framework/bundles",
			type : "GET",
			dataType : "json",
			success : function(fw_sl) {
				console.log(fw_sl);
				return fw_sl;
			},
			error : function(xhr, status) {
				console.log(status);
				return xhr.responseText;
			}
		});
	}

	/**
	 * get the Bundle representation of a specific bundle
	 * 
	 * @param b
	 *            the bundle, either the numeric bundle ID or the bundle URI
	 *            path.
	 * @returns the Bundle representation as a JSON object.
	 */
	this.getBundle = function getBundle(b) {
		$.ajax({
			url : this.baseUrl + getBundlePath(b),
			type : "GET",
			dataType : "json",
			success : function(fw_sl) {
				console.log(fw_sl);
				return fw_sl;
			},
			error : function(xhr, status) {
				console.log(status);
				return xhr.responseText;
			}
		});
	}

	/**
	 * Install a new bundle.
	 * 
	 * @param uri
	 *            the URI of the bundle to be installed
	 * @returns the URI path of the newly installed bundle
	 */
	this.installBundle = function installBundle(uri) {
		$.ajax({
			url : this.baseUrl + "/framework/bundles",
			type : "POST",
			dataType : "txt",
			data : uri,
			success : function(fw_sl) {
				console.log(fw_sl);
				return fw_sl;
			},
			error : function(xhr, status) {
				console.log(status);
				return xhr.responseText;
			}
		});
	}

	/**
	 * get the bundle representations of all bundles
	 * 
	 * @returns a JSON array containing all Bundle representations
	 */
	this.getBundleRepresentations = function getBundleRepresentations() {
		$.ajax({
			url : this.baseUrl + "/framework/bundles/representations",
			type : "GET",
			dataType : "json",
			success : function(fw_sl) {
				console.log(fw_sl);
				return fw_sl;
			},
			error : function(xhr, status) {
				console.log(status);
				return xhr.responseText;
			}
		});
	}

	/**
	 * Update a bundle.
	 * 
	 * @param b
	 *            the bundle, either the numeric bundle ID or the bundle URI
	 *            path.
	 * @param uri
	 *            the URI from which to update the bundle.
	 */
	this.updateBundle = function updateBundle(b, uri) {
		$.ajax({
			url : this.baseUrl + getBundlePath(b),
			type : "PUT",
			dataType : "txt",
			data : uri,
			success : function(fw_sl) {
				console.log(fw_sl);
				return fw_sl;
			},
			error : function(xhr, status) {
				console.log(status);
				return xhr.responseText;
			}
		});
	}

	/**
	 * Uninstall a bundle.
	 * 
	 * @param b
	 *            the bundle, either the numeric bundle ID or the bundle URI
	 *            path.
	 */
	this.uninstallBundle = function uninstallBundle(b) {
		$.ajax({
			url : this.baseUrl + getBundlePath(b),
			type : "DELETE",
			success : function(fw_sl) {
				console.log(fw_sl);
				return fw_sl;
			},
			error : function(xhr, status) {
				console.log(status);
				return xhr.responseText;
			}
		});
	}

	/**
	 * Get the state of a bundle.
	 * 
	 * @param b
	 *            the bundle, either the numeric bundle ID or the bundle URI
	 *            path.
	 * @returns the bundle state representation.
	 */
	this.getBundleState = function getBundleState(b) {
		$.ajax({
			url : this.baseUrl + getBundlePath(b) + "/state",
			type : "GET",
			dataType : "json",
			success : function(fw_sl) {
				console.log(fw_sl);
				return fw_sl;
			},
			error : function(xhr, status) {
				console.log(status);
				return xhr.responseText;
			}
		});
	}

	/**
	 * Set the state of a bundle to start or stop it.
	 * 
	 * @param b
	 *            the bundle, either the numeric bundle ID or the bundle URI
	 *            path.
	 * @param state
	 *            the target state.
	 * @returns the updated state of the bundle.
	 */
	this.setBundleState = function setBundleState(b, state) {
		$.ajax({
			url : this.baseUrl + getBundlePath(b) + "/state",
			type : "PUT",
			dataType : "json",
			data : state,
			success : function(fw_sl) {
				console.log(fw_sl);
				return fw_sl;
			},
			error : function(xhr, status) {
				console.log(status);
				return xhr.responseText;
			}
		});
	}

	/**
	 * Start a bundle.
	 * 
	 * @param b
	 *            the bundle, either the numeric bundle ID or the bundle URI
	 *            path.
	 * @returns the updated bundle state representation.
	 */
	this.startBundle = function startBundle(b) {
		return this.setBundleState(b, {
			state : 32
		});
	}

	/**
	 * Stop a bundle.
	 * 
	 * @param b
	 *            the bundle, either the numeric bundle ID or the bundle URI
	 *            path.
	 * @returns the updated bundle state representation.
	 */
	this.stopBundle = function stopBundle(b) {
		return this.setBundleState(b, {
			state : 4
		});
	}

	/**
	 * Get the bundle startlevel.
	 * 
	 * @param b
	 *            the bundle, either the numeric bundle ID or the bundle URI
	 *            path.
	 */
	this.getBundleStartLevel = function getBundleStartLevel(b) {
		$.ajax({
			url : this.baseUrl + getBundlePath(b) + "/startlevel",
			type : "GET",
			dataType : "json",
			success : function(fw_sl) {
				console.log(fw_sl);
				return fw_sl;
			},
			error : function(xhr, status) {
				console.log(status);
				return xhr.responseText;
			}
		});
	}

	/**
	 * Set the startlevel of a bundle.
	 * 
	 * @param b
	 *            the bundle, either the numeric bundle ID or the bundle URI
	 *            path.
	 * @param the
	 *            target startlevel representation.
	 * @returns the updated startlevel representation.
	 */
	this.setBundleStartLevel = function setBundleStartLevel(b, sl) {
		$.ajax({
			url : this.baseUrl + getBundlePath(b) + "/state",
			type : "PUT",
			dataType : "json",
			data : sl,
			success : function(fw_sl) {
				console.log(fw_sl);
				return fw_sl;
			},
			error : function(xhr, status) {
				console.log(status);
				return xhr.responseText;
			}
		});
	}

	/**
	 * Get all services.
	 * 
	 * @returns a JSON array of the URI paths of all services.
	 */
	this.getServices = function getServices() {
		$.ajax({
			url : this.baseUrl + "/framework/services",
			type : "GET",
			dataType : "json",
			success : function(fw_sl) {
				console.log(fw_sl);
				return fw_sl;
			},
			error : function(xhr, status) {
				console.log(status);
				return xhr.responseText;
			}
		});
	}

	/**
	 * Get the representations of all services
	 * 
	 * @return a JSON array containing all representations.
	 */
	this.getServiceRepresentations = function getServiceRepresentations() {
		$.ajax({
			url : this.baseUrl + "/framework/services/representations",
			type : "GET",
			dataType : "json",
			success : function(fw_sl) {
				console.log(fw_sl);
				return fw_sl;
			},
			error : function(xhr, status) {
				console.log(status);
				return xhr.responseText;
			}
		});
	}

	/**
	 * Get the representation of a service.
	 * 
	 * @param s
	 *            the service, either the numeric service ID or the service URI
	 *            path.
	 * @return the service representation.
	 */
	this.getServices = function getService(s) {
		$.ajax({
			url : this.baseUrl + getServicePath(s),
			type : "GET",
			dataType : "json",
			success : function(fw_sl) {
				console.log(fw_sl);
				return fw_sl;
			},
			error : function(xhr, status) {
				console.log(status);
				return xhr.responseText;
			}
		});
	}

	function getBundlePath(b) {
		if (!(isNaN(b - 0))) {
			return "/framework/bundle/" + b;
		} else {
			return b;
		}
	}

	function getServicePath(s) {
		if (!(isNaN(s - 0))) {
			return "/framework/service/" + s;
		} else {
			return s;
		}
	}
}
