/*
 * $Header$
 *
 * Copyright (c) IBM Corporation (2005)
 *
 * These materials have been contributed  to the OSGi Alliance as 
 * "MEMBER LICENSED MATERIALS" as defined in, and subject to the terms of, 
 * the OSGi Member Agreement, specifically including but not limited to, 
 * the license rights and warranty disclaimers as set forth in Sections 3.2 
 * and 12.1 thereof, and the applicable Statement of Work. 
 *
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.
 */
package org.osgi.impl.service.metatype;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import org.osgi.service.metatype.AttributeDefinition;

/**
 * Implementation of AttributeDefintion
 * 
 * @author Julian Chen
 * @version 1.0
 */
public class AttributeDefinitionImpl extends LocalizationElement implements AttributeDefinition, Cloneable {

	public static final String	EXCEPTION_MESSAGE		= "EXCEPTION_MESSAGE";		//$NON-NLS-1$
	public static final String	NULL_IS_INVALID			= "NULL_IS_INVALID";		//$NON-NLS-1$
	public static final String	VALUE_OUT_OF_RANGE		= "VALUE_OUT_OF_RANGE"; 	//$NON-NLS-1$
	public static final String	VALUE_OUT_OF_OPTION		= "VALUE_OUT_OF_OPTION"; 	//$NON-NLS-1$
	public static final String	TOO_MANY_VALUES			= "TOO_MANY_VALUES";		//$NON-NLS-1$
	public static final String	NULL_OPTIONS			= "NULL_OPTIONS";			//$NON-NLS-1$
	public static final String	INCONSISTENT_OPTIONS	= "INCONSISTENT_OPTIONS";	//$NON-NLS-1$	
	public static final String	INVALID_OPTIONS			= "INVALID_OPTIONS";		//$NON-NLS-1$
	
	String						_name;
	String						_id;
	String						_description;
	int							_cardinality		= 0;
	int							_dataType;
	Object						_minValue			= null;
	Object						_maxValue			= null;
	boolean						_isRequired			= true;

	String []					_defaults			= null;
	Vector						_dfts_vector		= new Vector(7);
	Vector						_values				= new Vector(7);
	Vector						_labels				= new Vector(7);

	/**
	 * Constructor of class AttributeDefinitionImpl.
	 */
	public AttributeDefinitionImpl(String id, String name, String description,
			int type, int cardinality, Object min, Object max,
			boolean isRequired, String localization) {

		this._id = id;
		this._name = name;
		this._description = description;
		this._dataType = type;
		this._cardinality = cardinality;
		this._minValue = min;
		this._maxValue = max;
		this._isRequired = isRequired;
		this._localization = localization;
	}

	/*
	 * 
	 */
	public synchronized Object clone() {

		AttributeDefinitionImpl ad = new AttributeDefinitionImpl(_id, _name,
				_description, _dataType, _cardinality, _minValue, _maxValue,
				_isRequired, _localization);

		if (_defaults != null) {
			ad.setDefaultValue((String[]) _defaults.clone(), false);
		}
		if ((_labels != null) && (_values != null)) {
			ad.setOption((Vector) _labels.clone(),
					(Vector) _values.clone(), false);
		}

		return ad;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.service.metatype.AttributeDefinition#getName()
	 */
	public String getName() {
		return getLocalized(_name);
	}

	/**
	 * Method to set the name of AttributeDefinition.
	 */
	void setName(String name) {
		this._name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.service.metatype.AttributeDefinition#getID()
	 */
	public String getID() {
		return _id;
	}

	/**
	 * Method to set the ID of AttributeDefinition.
	 */
	void setID(String id) {
		this._id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.service.metatype.AttributeDefinition#getDescription()
	 */
	public String getDescription() {
		return getLocalized(_description);
	}

	/**
	 * Method to set the description of AttributeDefinition.
	 */
	void setDescription(String description) {
		this._description = description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.service.metatype.AttributeDefinition#getCardinality()
	 */
	public int getCardinality() {
		return _cardinality;
	}

	/**
	 * Method to set the cardinality of AttributeDefinition.
	 */
	void setCardinality(int cardinality) {
		this._cardinality = cardinality;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.service.metatype.AttributeDefinition#getType()
	 */
	public int getType() {
		return _dataType;
	}

	/**
	 * Method to set the data type of AttributeDefinition.
	 */
	void setType(int type) {
		this._dataType = type;
	}

	/**
	 * Method to get the required flag of AttributeDefinition.
	 */
	boolean isRequired() {
		return _isRequired;
	}

	/**
	 * Method to set the required flag of AttributeDefinition.
	 */
	void setRequired(boolean isRequired) {
		this._isRequired = isRequired;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.service.metatype.AttributeDefinition#getOptionLabels()
	 */
	public String[] getOptionLabels() {

		if ((_labels == null) || (_labels.size() == 0)) {
			return null;
		}

		String[] returnedLabels = new String[_labels.size()];
		Enumeration labelKeys = _labels.elements();
		int i = 0;
		while (labelKeys.hasMoreElements()) {
			String labelKey = (String) labelKeys.nextElement();
			returnedLabels[i] = getLocalized(labelKey);
			i++;
		}
		return returnedLabels;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.service.metatype.AttributeDefinition#getOptionValues()
	 */
	public String[] getOptionValues() {

		if ((_values == null) || (_values.size() == 0)) {
			return null;
		}

		String[] returnedValues = new String[_values.size()];
		Enumeration valueKeys = _values.elements();
		int i = 0;
		while (valueKeys.hasMoreElements()) {
			String valueKey = (String) valueKeys.nextElement();
			returnedValues[i] = getLocalized(valueKey);
			i++;
		}
		return returnedValues;
	}

	/**
	 * Method to set the Option values of AttributeDefinition.
	 */
	void setOption(Vector labels, Vector values, boolean needValidation) {

		if ((labels==null) || (values==null)) {
			Logging.log(Logging.ERROR, this,
					"setOption(Vector, Vector, boolean)", //$NON-NLS-1$
					Msg.formatter.getString(NULL_OPTIONS));
			return;
		}

		if (labels.size() != values.size()) {
			Logging.log(Logging.ERROR, this,
					"setOption(Vector, Vector, boolean)", //$NON-NLS-1$
					Msg.formatter.getString(INCONSISTENT_OPTIONS));
			return;
		}

		_labels = labels;
		_values = values;

		if (needValidation) {
			for (int index=0; index<_labels.size(); index++) {
				String reason = validate((String) _values.get(index));
				if ((reason != null) && reason.length()>0) {
					Logging.log(Logging.WARN,
							Msg.formatter.getString(INVALID_OPTIONS,
									(String) _values.get(index), reason));
					_labels.remove(index);
					_values.remove(index);
					index--; // Because this one has been removed.
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.service.metatype.AttributeDefinition#getDefaultValue()
	 */
	public String[] getDefaultValue() {
		return _defaults;
	}

	/**
	 * Method to set the default value of AttributeDefinition.
	 * The given parameter is a comma delimited list needed to be parsed.
	 */
	void setDefaultValue(String defaults_str, boolean needValidation) {

		ValueTokenizer vt = new ValueTokenizer(defaults_str);
		setDefaultValue(vt.getValuesAsArray(), needValidation);
	}

	/**
	 * Method to set the default value of AttributeDefinition.
	 * The given parameter is a String array of multi values.
	 */
	void setDefaultValue(String[] defaults, boolean needValidation) {

		_defaults = defaults;
		// Do we also need to make sure if defaults are validated?
	}

	/**
	 * Method to set the validation value - min of AttributeDefinition.
	 */
	void setMinValue(Object minValue) {
		this._minValue = minValue;
	}

	/**
	 * Method to set the validation value - max of AttributeDefinition.
	 */
	void setMaxValue(Object maxValue) {
		this._maxValue = maxValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.service.metatype.AttributeDefinition#validate(java.lang.String)
	 */
	public String validate(String value) {

		if (value == null) {
			return Msg.formatter.getString(NULL_IS_INVALID);
		}
		if ((_minValue == null) && (_maxValue == null)) {
			if (_dataType != STRING) {
				// No validation present
				return null;
			}
			else {
				if (_values.size() < 1)
					// No validation present
					return null;
			}
		}

		// Addtional validation for STRING.
		if (_dataType == STRING
				&& _values.size() > 0
				&& !_values.contains(value)) {
			return Msg.formatter.getString(VALUE_OUT_OF_OPTION, value);
		}

		try {
			if (_cardinality != 0) {
				ValueTokenizer vt = new ValueTokenizer(value);
				Vector value_vector = vt.getValuesAsVector();

				if (value_vector.size() > Math.abs(_cardinality)) {
					return Msg.formatter.getString(TOO_MANY_VALUES, value,
							new Integer(Math.abs(_cardinality)));
				}
				for (int i=0; i< value_vector.size(); i++) {
					String return_msg = validateRange(
							(String) value_vector.get(i));
					if (!"".equals(return_msg)) { //$NON-NLS-1$
						// Returned String states why the value is invalid.
						return return_msg;
					}
				}
				// No problems detected
				return ""; //$NON-NLS-1$
			}
			else {
				// Only when cardinality is '0', it comes here.
					String return_msg = validateRange(value);
					return return_msg;
			}
		}
		catch (Throwable t) {
			return Msg.formatter.getString(EXCEPTION_MESSAGE, t.getClass()
					.getName(), t.getMessage());
		}
	}

	/**
	 * Internal Method - to validate data in range.
	 */
	private String validateRange(String value) {

		boolean rangeError = false;

		switch (_dataType) {
			case STRING :
				if ((_minValue != null) && (_maxValue != null)) {
					if (value.length() > ((Integer) _maxValue).intValue()
							|| value.length() < ((Integer) _minValue).intValue()) {
						rangeError = true;
					}
				}
				break;
			case LONG :
				Long longVal = new Long(value);
				if (_minValue != null
						&& longVal.compareTo((Long) _minValue) < 0) {
					rangeError = true;
				}
				else
					if (_maxValue != null
							&& longVal.compareTo((Long) _maxValue) > 0) {
						rangeError = true;
					}
				break;
			case INTEGER :
				Integer intVal = new Integer(value);
				if (_minValue != null
						&& intVal.compareTo((Integer) _minValue) < 0) {
					rangeError = true;
				}
				else
					if (_maxValue != null
							&& intVal.compareTo((Integer) _maxValue) > 0) {
						rangeError = true;
					}
				break;
			case SHORT :
				Short shortVal = new Short(value);
				if (_minValue != null
						&& shortVal.compareTo((Short) _minValue) < 0) {
					rangeError = true;
				}
				else
					if (_maxValue != null
							&& shortVal.compareTo((Short) _maxValue) > 0) {
						rangeError = true;
					}
				break;
			case CHARACTER :
				Character charVal = new Character(value.charAt(0));
				if (_minValue != null
						&& charVal.compareTo((Character) _minValue) < 0) {
					rangeError = true;
				}
				else
					if (_maxValue != null
							&& charVal.compareTo((Character) _maxValue) > 0) {
						rangeError = true;
					}
				break;
			case BYTE :
				Byte byteVal = new Byte(value);
				if (_minValue != null
						&& byteVal.compareTo((Byte) _minValue) < 0) {
					rangeError = true;
				}
				else
					if (_maxValue != null
							&& byteVal.compareTo((Byte) _maxValue) > 0) {
						rangeError = true;
					}
				break;
			case DOUBLE :
				Double doubleVal = new Double(value);
				if (_minValue != null
						&& doubleVal.compareTo((Double) _minValue) < 0) {
					rangeError = true;
				}
				else
					if (_maxValue != null
							&& doubleVal.compareTo((Double) _maxValue) > 0) {
						rangeError = true;
					}
				break;
			case FLOAT :
				Float floatVal = new Float(value);
				if (_minValue != null
						&& floatVal.compareTo((Float) _minValue) < 0) {
					rangeError = true;
				}
				else
					if (_maxValue != null
							&& floatVal.compareTo((Float) _maxValue) > 0) {
						rangeError = true;
					}
				break;
			case BIGINTEGER :
				try {
					Class bigIntClazz = Class.forName("java.math.BigInteger");
					Constructor bigIntConstructor = bigIntClazz
							.getConstructor(new Class[] {String.class});
					Comparable bigIntObject = (Comparable) bigIntConstructor
							.newInstance(new Object[] {value});
					if (_minValue != null
							&& bigIntObject.compareTo(_minValue) < 0) {
						rangeError = true;
					}
					else
						if (_maxValue != null
								&& bigIntObject.compareTo(_maxValue) > 0) {
							rangeError = true;
						}
				}
				catch (ClassNotFoundException e) {
					e.printStackTrace();
					return null;
				}
				catch (SecurityException e) {
					e.printStackTrace();
					return null;
				}
				catch (NoSuchMethodException e) {
					e.printStackTrace();
					return null;
				}
				catch (IllegalArgumentException e) {
					e.printStackTrace();
					return null;
				}
				catch (InstantiationException e) {
					e.printStackTrace();
					return null;
				}
				catch (IllegalAccessException e) {
					e.printStackTrace();
					return null;
				}
				catch (InvocationTargetException e) {
					e.printStackTrace();
					return null;
				}
				break;
			case BIGDECIMAL :
				try {
					Class bigDecimalClazz = Class
							.forName("java.math.BigDecimal");
					Constructor bigDecimalConstructor = bigDecimalClazz
							.getConstructor(new Class[] {String.class});
					Comparable bigDecimalObject = (Comparable) bigDecimalConstructor
							.newInstance(new Object[] {value});
					if (_minValue != null
							&& bigDecimalObject.compareTo(_minValue) < 0) {
						rangeError = true;
					}
					else
						if (_maxValue != null
								&& bigDecimalObject.compareTo(_maxValue) > 0) {
							rangeError = true;
						}
				}
				catch (ClassNotFoundException e) {
					e.printStackTrace();
					return null;
				}
				catch (SecurityException e) {
					e.printStackTrace();
					return null;
				}
				catch (NoSuchMethodException e) {
					e.printStackTrace();
					return null;
				}
				catch (IllegalArgumentException e) {
					e.printStackTrace();
					return null;
				}
				catch (InstantiationException e) {
					e.printStackTrace();
					return null;
				}
				catch (IllegalAccessException e) {
					e.printStackTrace();
					return null;
				}
				catch (InvocationTargetException e) {
					e.printStackTrace();
					return null;
				}
				break;
			case BOOLEAN :
			// shouldn't ever get boolean - this is a range validation
			default :
				return null;
		}

		if (rangeError) {
			return (Msg.formatter.getString(VALUE_OUT_OF_RANGE, value));
		}
		else {
			// No problems detected
			return (""); //$NON-NLS-1$
		}
	}
}
