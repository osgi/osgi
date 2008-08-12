package org.osgi.impl.service.upnp.cd.control;

import java.util.*;
import org.osgi.service.upnp.*;
import org.osgi.impl.service.upnp.cd.util.*;

// The object of this is created when a soap request is parsed. It contains methods which are called
// to get the value of the elements.
public class ParsedRequest {
	private String		serviceType;
	private String		actionName;
	private Dictionary	args;
	private String		returnValue;
	private String[]	arglist;

	// This method sets the serviceType.
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	// This method sets the action name
	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	// This method sets the given argument name and value.
	public void setArgument(String argName, String argValue) {
		if (args == null) {
			args = new Hashtable();
			returnValue = argValue;
		}
		args.put(argName, argValue);
	}

	// This method returns the Service type.
	public String getServiceType() {
		return serviceType;
	}

	// This method returns the Action name
	public String getActionName() {
		return actionName;
	}

	// This method returns all the arguments
	public Dictionary getArguments(UPnPService upnpservice, String actionName)
			throws Exception {
		Hashtable stat_dt = new Hashtable();
		UPnPAction upnpaction = upnpservice.getAction(actionName);
		arglist = upnpaction.getInputArgumentNames();
		for (int j = 0; j < arglist.length; j++) {
			UPnPStateVariable USV = (UPnPStateVariable) upnpservice
					.getStateVariable(arglist[j]);
			stat_dt.put(arglist[j], USV.getUPnPDataType());
		}
		return upnp2java_converter(args, stat_dt);
	}

	public Dictionary getParams(UPnPAction upnpaction, Dictionary params)
			throws Exception {
		Hashtable stat_dt = new Hashtable();
		arglist = upnpaction.getOutputArgumentNames();
		for (int j = 0; j < arglist.length; j++) {
			UPnPStateVariable USV = (UPnPStateVariable) upnpaction
					.getStateVariable(arglist[j]);
			stat_dt.put(arglist[j], USV.getUPnPDataType());
		}
		return java2upnp_converter(params, stat_dt);
	}

	private Dictionary upnp2java_converter(Dictionary outparms,
			Hashtable stat_dt) throws Exception {
		for (Enumeration e = outparms.keys(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			String value = (String) outparms.get(key);
			String type = (String) stat_dt.get(key);
			outparms.remove(key);
			if (type.equals(SoapTypeConstants.SOAP_TYPE_UI1)
					|| type.equals(SoapTypeConstants.SOAP_TYPE_UI2)
					|| type.equals(SoapTypeConstants.SOAP_TYPE_INT))
				outparms.put(key, SoapTypeConverter.UI1_Converter
						.convertToJavaType(value));
			else
				if (type.equals(SoapTypeConstants.SOAP_TYPE_I1)
						|| type.equals(SoapTypeConstants.SOAP_TYPE_I2)
						|| type.equals(SoapTypeConstants.SOAP_TYPE_I4))
					outparms.put(key, SoapTypeConverter.UI1_Converter
							.convertToJavaType(value));
				else
					if (type.equals(SoapTypeConstants.SOAP_TYPE_UI4))
						outparms.put(key, SoapTypeConverter.UI4_Converter
								.convertToJavaType(value));
					else
						if (type.equals(SoapTypeConstants.SOAP_TYPE_R4)
								|| type
										.equals(SoapTypeConstants.SOAP_TYPE_FLOAT))
							outparms.put(key, SoapTypeConverter.R4_Converter
									.convertToJavaType(value));
						else
							if (type.equals(SoapTypeConstants.SOAP_TYPE_R8)
									|| type
											.equals(SoapTypeConstants.SOAP_TYPE_NUMBER)
									|| type
											.equals(SoapTypeConstants.SOAP_TYPE_FIXED_14_4))
								outparms.put(key,
										SoapTypeConverter.R8_Converter
												.convertToJavaType(value));
							else
								if (type
										.equals(SoapTypeConstants.SOAP_TYPE_CHAR))
									outparms.put(key,
											SoapTypeConverter.CHAR_Converter
													.convertToJavaType(value));
								else
									if (type
											.equals(SoapTypeConstants.SOAP_TYPE_STRING)
											|| type
													.equals(SoapTypeConstants.SOAP_TYPE_URI)
											|| type
													.equals(SoapTypeConstants.SOAP_TYPE_UUID))
										outparms
												.put(
														key,
														SoapTypeConverter.STRING_Converter
																.convertToJavaType(value));
									else
										if (type
												.equals(SoapTypeConstants.SOAP_TYPE_DATE))
											outparms
													.put(
															key,
															SoapTypeConverter.DATE_Converter
																	.convertToJavaType(value));
										else
											if (type
													.equals(SoapTypeConstants.SOAP_TYPE_BOOLEAN))
												outparms
														.put(
																key,
																SoapTypeConverter.BOOLEAN_Converter
																		.convertToJavaType(value));
											else
												if (type
														.equals(SoapTypeConstants.SOAP_TYPE_BIN_HEX)
														|| type
																.equals(SoapTypeConstants.SOAP_TYPE_BIN_BASE64))
													outparms
															.put(
																	key,
																	SoapTypeConverter.BIN_HEX_Converter
																			.convertToJavaType(value));
		}
		return outparms;
	}

	private Dictionary java2upnp_converter(Dictionary inparms,
			Hashtable Input_dict) throws Exception {
		for (Enumeration e = inparms.keys(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			Object value = (Object) inparms.get(key);
			String type = (String) Input_dict.get(key);
			inparms.remove(key);
			if (type.equals(SoapTypeConstants.SOAP_TYPE_UI1)
					|| type.equals(SoapTypeConstants.SOAP_TYPE_UI2))
				inparms.put(key, SoapTypeConverter.UI1_Converter
						.convertToString(value));
			else
				if (type.equals(SoapTypeConstants.SOAP_TYPE_INT))
					inparms.put(key, SoapTypeConverter.INT_Converter
							.convertToString(value));
				else
					if (type.equals(SoapTypeConstants.SOAP_TYPE_I1)
							|| type.equals(SoapTypeConstants.SOAP_TYPE_I2)
							|| type.equals(SoapTypeConstants.SOAP_TYPE_I4))
						inparms.put(key, SoapTypeConverter.UI1_Converter
								.convertToString(value));
					else
						if (type.equals(SoapTypeConstants.SOAP_TYPE_UI4))
							inparms.put(key, SoapTypeConverter.UI4_Converter
									.convertToString(value));
						else
							if (type.equals(SoapTypeConstants.SOAP_TYPE_R4)
									|| type
											.equals(SoapTypeConstants.SOAP_TYPE_FLOAT))
								inparms.put(key, SoapTypeConverter.R4_Converter
										.convertToString(value));
							else
								if (type.equals(SoapTypeConstants.SOAP_TYPE_R8))
									inparms.put(key,
											SoapTypeConverter.R8_Converter
													.convertToString(value));
								else
									if (type
											.equals(SoapTypeConstants.SOAP_TYPE_NUMBER))
										inparms
												.put(
														key,
														SoapTypeConverter.NUMBER_Converter
																.convertToString(value));
									else
										if (type
												.equals(SoapTypeConstants.SOAP_TYPE_FIXED_14_4))
											inparms
													.put(
															key,
															SoapTypeConverter.FIXED_14_4_Converter
																	.convertToString(value));
										else
											if (type
													.equals(SoapTypeConstants.SOAP_TYPE_CHAR))
												inparms
														.put(
																key,
																SoapTypeConverter.CHAR_Converter
																		.convertToString(value));
											else
												if (type
														.equals(SoapTypeConstants.SOAP_TYPE_STRING)
														|| type
																.equals(SoapTypeConstants.SOAP_TYPE_URI)
														|| type
																.equals(SoapTypeConstants.SOAP_TYPE_UUID))
													inparms
															.put(
																	key,
																	SoapTypeConverter.STRING_Converter
																			.convertToString(value));
												else
													if (type
															.equals(SoapTypeConstants.SOAP_TYPE_DATE))
														inparms
																.put(
																		key,
																		SoapTypeConverter.DATE_Converter
																				.convertToString(value));
													else
														if (type
																.equals(SoapTypeConstants.SOAP_TYPE_BOOLEAN))
															inparms
																	.put(
																			key,
																			SoapTypeConverter.BOOLEAN_Converter
																					.convertToString(value));
														else
															if (type
																	.equals(SoapTypeConstants.SOAP_TYPE_BIN_HEX)
																	|| type
																			.equals(SoapTypeConstants.SOAP_TYPE_BIN_BASE64))
																inparms
																		.put(
																				key,
																				SoapTypeConverter.BIN_HEX_Converter
																						.convertToString(value));
		}
		return inparms;
	}

	// This method returns the argument value of the given name
	public Object getArgumentValue(String name) {
		if (name == null) {
			return null;
		}
		return args.get(name);
	}

	// This method returns the out argument value
	public String getReturnValue() {
		return returnValue;
	}
}
