/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

/**
 * The OSGi REST API client
 * 
 * all members allow the user to specify the following callback functions for
 * asynchronous status notifications:
 * 
 * function success(result) function failure(code, response) function
 * error(message)
 * 
 * callbacks that are not declared by the user are substituted with default
 * functions which print to the console.
 * 
 * @param baseUrl
 *            the base URL to the REST API.
 * @returns the OSGi REST API client object
 */
function OSGiRestClient(baseUrl) {
	this.baseUrl = baseUrl || "";

	function checkParam(f) {
		return ('undefined' === typeof f) ? function() {
			console.log(arguments)
		} : f;
	}

	function restCall(args, callbacks) {
		// mandatory: args.uri, args.request
		if ("undefined" === args.uri) {
			throw "No URI was passed (args.uri)";
		}
		if ("undefined" === args.request) {
			throw "No request type was passed (args.request)";
		}

		var c = callbacks || {};

		var success = checkParam(c.success);
		var failure = checkParam(c.failure);
		var error = checkParam(c.error);

		var expectedStatus = args.expectedStatus || 200;

		var req = new XMLHttpRequest();
		req.onreadystatechange = function() {
			if (req.readyState == 4) {
				if (req.status == expectedStatus) {
					if (expectedStatus === 200) {
						console.log(req.getResponseHeader("Content-Type"));

						if (req.getResponseHeader("Content-Type").indexOf(
								"text/plain") != -1) {
							success(req.responseText);
						} else {
							success(JSON.parse(req.responseText));
						}
					} else {
						success(undefined);
					}
				} else {
					try {
						failure(req.status, JSON.parse(req.responseText));
					} catch (err) {
						failure(req.status, req.responseText);
					}
				}
			}
		};
		try {
			req.open(args.request, args.uri);
			if ("undefined" !== typeof args.accept) {
				req.setRequestHeader("Accept", args.accept);
			}
			if ("undefined" !== typeof args.contentType) {
				req.setRequestHeader("Content-Type", args.contentType);
			}
			req.send(args.data);
		} catch (err) {
			error(err);
		}
	}

	/**
	 * get the framework start level in JSON FrameworkStartLevel representation.
	 * 
	 * @param callbacks
	 *            an optional object containing callback functions for status
	 *            updates.
	 */
	this.getFrameworkStartLevel = function getFrameworkStartLevel(callbacks) {
		restCall({
			uri : this.baseUrl + "/framework/startlevel",
			request : "GET",
			accept : "application/org.osgi.framework.startlevel+json"
		}, callbacks);
	};

	/**
	 * set the framework start level
	 * 
	 * @param the
	 *            new framework startlevel in JSON representation.
	 * @param callbacks
	 *            an optional object containing callback functions for status
	 *            updates. On success, the callback will have the updated
	 *            framework startlevel as an object that corresponds to the JSON
	 *            representation.
	 */
	this.setFrameworkStartLevel = function setFrameworkStartLevel(fw_sl,
			callbacks) {
		restCall({
			uri : this.baseUrl + "/framework/startlevel",
			request : "PUT",
			data : JSON.stringify(fw_sl),
			accept : "application/org.osgi.framework.startlevel+json",
			expectedStatus : 204
		}, callbacks)
	};

	/**
	 * get the bundles
	 * 
	 * @param callbacks
	 *            an optional object containing callback functions for status
	 *            updates. On success, the callback will have the URI paths of
	 *            the bundles as an array of strings.
	 */
	this.getBundles = function getBundles(callbacks) {
		restCall({
			uri : this.baseUrl + "/framework/bundles",
			request : "GET",
			accept : "application/org.osgi.framework.bundles+json"
		}, callbacks);
	}

	/**
	 * get the bundle representations of all bundles
	 * 
	 * @param callbacks
	 *            an optional object containing callback functions for status
	 *            updates. On success, the callback will have an array
	 *            containing all Bundle representation objects.
	 */
	this.getBundleRepresentations = function getBundleRepresentations(callbacks) {
		restCall({
			uri : this.baseUrl + "/framework/bundles/representations",
			request : "GET",
			accept : "application/org.osgi.bundles.representations+json"
		}, callbacks);
	}

	/**
	 * get the Bundle representation of a specific bundle
	 * 
	 * @param b
	 *            the bundle, either the numeric bundle ID or the bundle URI
	 *            path.
	 * @param callbacks
	 *            an optional object containing callback functions for status
	 *            updates. On success, the callback will have the Bundle
	 *            representation as an object that corresponds to the JSON
	 *            representation.
	 */
	this.getBundle = function getBundle(b, callbacks) {
		restCall({
			uri : this.baseUrl + getBundlePath(b),
			request : "GET",
			accept : "application/org.osgi.framework.bundle+json"
		}, callbacks);
	}

	/**
	 * Get the state of a bundle.
	 * 
	 * @param b
	 *            the bundle, either the numeric bundle ID or the bundle URI
	 *            path.
	 * @param callbacks
	 *            an optional object containing callback functions for status
	 *            updates. On success, the callback will have the bundle state
	 *            as an object that corresponds to the JSON representation.
	 */
	this.getBundleState = function getBundleState(b, callbacks) {
		restCall({
			uri : this.baseUrl + getBundlePath(b) + "/state",
			request : "GET",
			accept : "application/org.osgi.bundle.state+json"
		}, callbacks);
	}

	/**
	 * Start a bundle.
	 * 
	 * @param b
	 *            the bundle, either the numeric bundle ID or the bundle URI
	 *            path.
	 * @param options
	 *            the options passed to the bundle's start method as an integer. (optional)
	 * @param callbacks
	 *            an optional object containing callback functions for status
	 *            updates. On success, the callback will have the updated bundle
	 *            state object.
	 */
	this.startBundle = function startBundle(b, options, callbacks) {
		if ((isNaN(options - 0))) {
			callbacks = options
			options = 0
		}
		this.setBundleState(b, {
			state : 32,
			options : options
		}, callbacks);
	}

	/**
	 * Stop a bundle.
	 * 
	 * @param b
	 *            the bundle, either the numeric bundle ID or the bundle URI
	 *            path.
	 * @param options
	 *            the options passed to the bundle's start method as an integer. (optional)
	 * @param callbacks
	 *            an optional object containing callback functions for status
	 *            updates. On success, the callback will have the updated bundle
	 *            state object.
	 */
	this.stopBundle = function stopBundle(b, options, callbacks) {
		if ((isNaN(options - 0))) {
			callbacks = options
			options = 0
		}
		this.setBundleState(b, {
			state : 4,
			options : options
		}, callbacks);
	}

	/**
	 * Set the state of a bundle to start or stop it.
	 * 
	 * @param b
	 *            the bundle, either the numeric bundle ID or the bundle URI
	 *            path.
	 * @param state
	 *            the target state.
	 * @param callbacks
	 *            an optional object containing callback functions for status
	 *            updates. On success, the callback will have the updated bundle
	 *            state object.
	 */
	this.setBundleState = function setBundleState(b, state, callbacks) {
		restCall({
			uri : this.baseUrl + getBundlePath(b) + "/state",
			request : "PUT",
			data : JSON.stringify(state),
			accept : "application/org.osgi.bundle.state+json",
		}, callbacks)
	}

	/**
	 * Get the bundle headers.
	 * 
	 * @param b
	 *            the bundle, either the numeric bundle ID or the bundle URI
	 *            path.
	 * @param callbacks
	 *            an optional object containing callback functions for status
	 *            updates. On success, the callback will have the bundle header
	 *            as an object.
	 */
	this.getBundleHeader = function getBundleHeader(b, callbacks) {
		restCall({
			uri : this.baseUrl + getBundlePath(b) + "/header",
			request : "GET",
			accept : "application/org.osgi.bundle.header+json"
		}, callbacks);
	}

	/**
	 * Get the bundle startlevel.
	 * 
	 * @param b
	 *            the bundle, either the numeric bundle ID or the bundle URI
	 *            path.
	 * @param callbacks
	 *            an optional object containing callback functions for status
	 *            updates.
	 */
	this.getBundleStartLevel = function getBundleStartLevel(b, callbacks) {
		restCall({
			uri : this.baseUrl + getBundlePath(b) + "/startlevel",
			request : "GET",
			accept : "application/org.osgi.bundle.startlevel+json"
		}, callbacks);
	}

	/**
	 * Set the startlevel of a bundle.
	 * 
	 * @param b
	 *            the bundle, either the numeric bundle ID or the bundle URI
	 *            path.
	 * @param sl
	 *            the target startlevel representation.
	 * @param callbacks
	 *            an optional object containing callback functions for status
	 *            updates. On success, the callback will have the updated
	 *            startlevel object that corresponds to the JSON representation.
	 */
	this.setBundleStartLevel = function setBundleStartLevel(b, sl, callbacks) {
		restCall({
			uri : this.baseUrl + getBundlePath(b) + "/startlevel",
			request : "PUT",
			data : JSON.stringify(sl),
			accept : "application/org.osgi.bundle.startlevel+json",
		}, callbacks)
	}

	/**
	 * Install a new bundle.
	 * 
	 * @param arg
	 *            the URI of the bundle to be installed as a String or an
	 *            ArrayBuffer object containing the bytes of a bundle to be
	 *            uploaded.
	 * @param callbacks
	 *            an optional object containing callback functions for status
	 *            updates. On success, the callback will have the URI path of
	 *            the newly installed bundle as a String.
	 */
	this.installBundle = function installBundle(arg, callbacks) {
		restCall({
			uri : this.baseUrl + "/framework/bundles",
			request : "POST",
			data : arg,
			accept : "text/plain",
			contentType : "string" === typeof arg ? "text/plain"
					: "vnd.osgi.bundle"
		}, callbacks)
	}

	/**
	 * Update a bundle.
	 * 
	 * @param b
	 *            the bundle, either the numeric bundle ID or the bundle URI
	 *            path.
	 * @param arg
	 *            the URI from which to update the bundle or a new bundle in the
	 *            form of an ArrayBuffer.
	 * @param callbacks
	 *            an optional object containing callback functions for status
	 *            updates.
	 */
	this.updateBundle = function updateBundle(b, arg, callbacks) {
		restCall({
			uri : this.baseUrl + getBundlePath(b),
			request : "PUT",
			data : arg,
			contentType : "string" === typeof arg ? "text/plain"
					: "vnd.osgi.bundle",
			expectedStatus : 204
		}, callbacks)
	}

	/**
	 * Uninstall a bundle.
	 * 
	 * @param b
	 *            the bundle, either the numeric bundle ID or the bundle URI
	 *            path.
	 * @param callbacks
	 *            an optional object containing callback functions for status
	 *            updates.
	 */
	this.uninstallBundle = function uninstallBundle(b, callbacks) {
		restCall({
			uri : this.baseUrl + getBundlePath(b),
			request : "DELETE",
			expectedStatus : 204
		}, callbacks)
	}

	/**
	 * Get all services.
	 * 
	 * @param callbacks
	 *            an optional object containing callback functions for status
	 *            updates. On success, the callback will have an array of the
	 *            URI paths of all services.
	 */
	this.getServices = function getServices(callbacks) {
		restCall({
			uri : this.baseUrl + "/framework/services",
			request : "GET",
			accept : "application/org.osgi.services+json"
		}, callbacks);
	}

	/**
	 * Get the representations of all services
	 * 
	 * @param callbacks
	 *            an optional object containing callback functions for status
	 *            updates. On success, the callback will have an array
	 *            containing all service representation objects.
	 */
	this.getServiceRepresentations = function getServiceRepresentations(
			callbacks) {
		restCall({
			uri : this.baseUrl + "/framework/services/representations",
			request : "GET",
			accept : "application/org.osgi.services.representations+json"
		}, callbacks);
	}

	/**
	 * Get the representation of a service.
	 * 
	 * @param s
	 *            the service, either the numeric service ID or the service URI
	 *            path.
	 * @param callbacks
	 *            an optional object containing callback functions for status
	 *            updates. On success, the callback will have the service
	 *            representation object which corresponds to the JSON
	 *            representation.
	 */
	this.getService = function getService(s, callbacks) {
		restCall({
			uri : this.baseUrl + getServicePath(s),
			request : "GET",
			accept : "application/org.osgi.service+json"
		}, callbacks);
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
