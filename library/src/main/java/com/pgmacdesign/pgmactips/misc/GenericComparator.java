package com.pgmacdesign.pgmactips.misc;

import com.pgmacdesign.pgmactips.utilities.L;
import com.pgmacdesign.pgmactips.utilities.MiscUtilities;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Credit for the initial version of this code goes to Jeevanandam Madanagopal from
 * https://myjeeva.com/generic-comparator-in-java.html
 * Additional features were added after forking
 */
@SuppressWarnings("ConfusingArgumentToVarargsMethod")
public class GenericComparator  implements Comparator, Serializable {
	private static final int LESSER = -1;
	private static final int EQUAL = 0;
	private static final int GREATER = 1;
	private static final String METHOD_GET_PREFIX = "get";
	private static final String DATATYPE_STRING = "java.lang.String";
	private static final String DATATYPE_DATE = "java.util.Date";
	private static final String DATATYPE_INTEGER = "java.lang.Integer";
	private static final String DATATYPE_LONG = "java.lang.Long";
	private static final String DATATYPE_FLOAT = "java.lang.Float";
	private static final String DATATYPE_DOUBLE = "java.lang.Double";
	private static final String DATATYPE_OBJECT = "java.lang.Object"; //Generic and undefined
	private static final String DATATYPE_ARRAY_OF_STRINGS = "[L" + DATATYPE_STRING + ";";
	private static final String DATATYPE_ARRAY_OF_INTEGERS = "[L" + DATATYPE_INTEGER + ";";
	private static final String DATATYPE_ARRAY_OF_LONGS = "[L" + DATATYPE_LONG + ";";
	private static final String DATATYPE_ARRAY_OF_DOUBLES = "[L" + DATATYPE_DOUBLE + ";";
	private static final String DATATYPE_ARRAY_OF_FLOATS = "[L" + DATATYPE_FLOAT + ";";
	private static final String DATATYPE_ARRAY_OF_OBJECTS = "[L" + DATATYPE_OBJECT + ";";
	private static final String DATATYPE_ARRAY_OF_RAW_INTS = "[I";
	private static final String DATATYPE_ARRAY_OF_RAW_LONGS = "[J";
	private static final String DATATYPE_ARRAY_OF_RAW_DOUBLES = "[D";
	private static final String DATATYPE_ARRAY_OF_RAW_FLOATS = "[F";
	private static final String DATATYPE_LIST = "java.util.List";
	private static final String DATATYPE_SET = "java.util.Set";
	private static final String DATATYPE_MAP = "java.util.Map";
	private static final String DATATYPE_COLLECTION = "java.util.Collection";
	private static final String DATATYPE_RAW_INT = "int";
	private static final String DATATYPE_RAW_LONG = "long";
	private static final String DATATYPE_RAW_FLOAT = "float";
	private static final String DATATYPE_RAW_DOUBLE = "double";
	private enum CompareMode { EQUAL, LESS_THAN, GREATER_THAN, DEFAULT }
	
	// generic comparator attributes
	private String targetMethod;
	private boolean sortAscending, userOverrodeGetMethod;
	
	/**
	 * <p>default constructor - assumes comparator for Type List</p>
	 * @param sortAscending - a {@link boolean} -
	 */
	public GenericComparator(boolean sortAscending) {
		super();
		this.userOverrodeGetMethod = false;
		this.targetMethod = null;
		this.sortAscending = sortAscending;
	}
	
	/**
	 * <p>constructor with <code>sortField</code> parameter for Derived type of <code>Class</code> default sorting is ascending order</p>
	 *
	 * @param sortField - a {@link java.lang.String} - which field requires sorting; as per above example "sorting required for <code>name</code> field"
	 */
	public GenericComparator(String sortField) {
		super();
		this.userOverrodeGetMethod = false;
		this.targetMethod = prepareTargetMethod(sortField);
		this.sortAscending = true;
	}
	
	/**
	 * <p>constructor with <code>sortField, sortAscending</code> parameter for Derived type of <code>Class</code></p>
	 * @param sortField - a {@link java.lang.String} - which field requires sorting; as per above example "sorting required for <code>name</code> field"
	 * @param sortAscending - a {@link boolean} - <code>true</code> ascending order or <code>false</code> descending order
	 */
	public GenericComparator(String sortField, boolean sortAscending) {
		super();
		this.userOverrodeGetMethod = false;
		this.targetMethod = prepareTargetMethod(sortField);
		this.sortAscending = sortAscending;
	}
	
	/**
	 * Overloaded method to allow for custom getter method names. IE, if you named your getter method
	 * something other than the default for the variable. Also allows for customized sorting
	 * @param usingOverride Ignore var. This is simply in place to allow for overloading
	 * @param customOverrideMethodName Method name you labeled, ie: "getName". It needs to match exactly
	 * @param sortAscending
	 */
	public GenericComparator(boolean usingOverride, String customOverrideMethodName, boolean sortAscending) {
		super();
		this.userOverrodeGetMethod = true;
		this.targetMethod = customOverrideMethodName;
		this.sortAscending = sortAscending;
	}
	
	/**
	 *
	 */
	@Override
	public int compare(Object o1, Object o2) {
		int response = LESSER;
		try {
			if(o1 == null){
				return GREATER;
			}
			if(o2 == null){
				return LESSER;
			}
			Object v1 = (null == this.targetMethod) ? o1 : getValue(o1);
			Object v2 = (null == this.targetMethod) ? o2 : getValue(o2);
			GenericComparator.CompareMode cm = findCompareMode(v1, v2);
			
			if (!cm.equals(GenericComparator.CompareMode.DEFAULT)) {
				return compareAlternate(cm);
			}
			final String returnType = (null == this.targetMethod)
					? o1.getClass().getName() : getMethod(o1).getReturnType().getName();
			response = compareActual(v1, v2, returnType);
		} catch (NoSuchMethodException nsme) {
			L.m("Method does not exist: ");
			nsme.printStackTrace();
		} catch (IllegalAccessException iae) {
			iae.printStackTrace();
		} catch (InvocationTargetException ite) {
			ite.printStackTrace();
		}
		return response;
	}
	
	/**
	 * alternate to actual value comparison i.e., either (lsh &amp; rhs) one the value could be null
	 *
	 * @param cm - a enum used to idetify the position for sorting
	 */
	private int compareAlternate(GenericComparator.CompareMode cm) {
		int compareState = LESSER;
		switch(cm) {
			case LESS_THAN:
				compareState = LESSER * determinePosition();
				break;
			case GREATER_THAN:
				compareState = GREATER * determinePosition();
				break;
			case EQUAL:
				compareState = EQUAL * determinePosition();
				break;
		}
		return compareState;
	}
	
	/**
	 * actual value comparison for sorting; both lsh &amp; rhs value available
	 *
	 * @param v1 - value of lhs
	 * @param v2 - value of rhs
	 * @param returnType - datatype of given values
	 * @return int - compare return value
	 */
	private int compareActual(Object v1, Object v2, String returnType) {
		L.m("Compare return type == " + returnType);
		if(v1 == null){
			return GREATER;
		}
		if(v2 == null){
			return LESSER;
		}
		int acutal = LESSER;
		if (returnType.equals(DATATYPE_INTEGER)) {
			acutal = (((Integer) v1).compareTo((Integer) v2) * determinePosition());
		} else if (returnType.equals(DATATYPE_LONG)) {
			acutal = (((Long) v1).compareTo((Long) v2) * determinePosition());
		} else if (returnType.equals(DATATYPE_STRING)) {
			acutal = (((String) v1).compareTo((String) v2) * determinePosition());
		} else if (returnType.equals(DATATYPE_DATE)) {
			acutal = (((Date) v1).compareTo((Date) v2) * determinePosition());
		} else if (returnType.equals(DATATYPE_FLOAT)) {
			acutal = (((Float) v1).compareTo((Float) v2) * determinePosition());
		} else if (returnType.equals(DATATYPE_DOUBLE)) {
			acutal = (((Double) v1).compareTo((Double) v2) * determinePosition());
		} else if (returnType.equals(DATATYPE_RAW_INT)){
			acutal = (((Integer) v1).compareTo((Integer) v2) * determinePosition());
		} else if (returnType.equals(DATATYPE_RAW_LONG)){
			acutal = (((Long) v1).compareTo((Long) v2) * determinePosition());
		} else if (returnType.equals(DATATYPE_RAW_FLOAT)){
			acutal = (((Float) v1).compareTo((Float) v2) * determinePosition());
		} else if (returnType.equals(DATATYPE_RAW_DOUBLE)){
			acutal = (((Double) v1).compareTo((Double) v2) * determinePosition());
		} else if (returnType.equals(DATATYPE_LIST)){
			int v1Size = ((List)v1).size();
			int v2Size = ((List)v2).size();
			if(v1Size > v2Size){
				acutal = (sortAscending) ? GREATER : LESSER;
			} else if (v1Size < v2Size){
				acutal = (sortAscending) ? LESSER : GREATER;
			} else {
				acutal = EQUAL;
			}
		} else if (returnType.equals(DATATYPE_ARRAY_OF_STRINGS) || returnType.equals(DATATYPE_ARRAY_OF_INTEGERS)
				|| returnType.equals(DATATYPE_ARRAY_OF_LONGS) || returnType.equals(DATATYPE_ARRAY_OF_DOUBLES)
				|| returnType.equals(DATATYPE_ARRAY_OF_FLOATS) || returnType.equals(DATATYPE_ARRAY_OF_OBJECTS)){
			int v1Size = ((Object[])v1).length;
			int v2Size = ((String[])v2).length;
			if(v1Size > v2Size){
				acutal = (sortAscending) ? GREATER : LESSER;
			} else if (v1Size < v2Size){
				acutal = (sortAscending) ? LESSER : GREATER;
			} else {
				acutal = EQUAL;
			}
		} else if (returnType.equals(DATATYPE_ARRAY_OF_RAW_INTS)){
			int v1Size = ((int[])v1).length;
			int v2Size = ((int[])v2).length;
			if(v1Size > v2Size){
				acutal = (sortAscending) ? GREATER : LESSER;
			} else if (v1Size < v2Size){
				acutal = (sortAscending) ? LESSER : GREATER;
			} else {
				acutal = EQUAL;
			}
		} else if (returnType.equals(DATATYPE_ARRAY_OF_RAW_LONGS)){
			int v1Size = ((long[])v1).length;
			int v2Size = ((long[])v2).length;
			if(v1Size > v2Size){
				acutal = (sortAscending) ? GREATER : LESSER;
			} else if (v1Size < v2Size){
				acutal = (sortAscending) ? LESSER : GREATER;
			} else {
				acutal = EQUAL;
			}
		} else if (returnType.equals(DATATYPE_ARRAY_OF_RAW_FLOATS)){
			int v1Size = ((float[])v1).length;
			int v2Size = ((float[])v2).length;
			if(v1Size > v2Size){
				acutal = (sortAscending) ? GREATER : LESSER;
			} else if (v1Size < v2Size){
				acutal = (sortAscending) ? LESSER : GREATER;
			} else {
				acutal = EQUAL;
			}
		} else if (returnType.equals(DATATYPE_ARRAY_OF_RAW_DOUBLES)){
			int v1Size = ((double[])v1).length;
			int v2Size = ((double[])v2).length;
			if(v1Size > v2Size){
				acutal = (sortAscending) ? GREATER : LESSER;
			} else if (v1Size < v2Size){
				acutal = (sortAscending) ? LESSER : GREATER;
			} else {
				acutal = EQUAL;
			}
		} else if (returnType.equals(DATATYPE_SET)){
			int v1Size = ((Set)v1).size();
			int v2Size = ((Set)v2).size();
			if(v1Size > v2Size){
				acutal = (sortAscending) ? GREATER : LESSER;
			} else if (v1Size < v2Size){
				acutal = (sortAscending) ? LESSER : GREATER;
			} else {
				acutal = EQUAL;
			}
		} else if (returnType.equals(DATATYPE_MAP)){
			int v1Size = ((Map)v1).size();
			int v2Size = ((Map)v2).size();
			if(v1Size > v2Size){
				acutal = (sortAscending) ? GREATER : LESSER;
			} else if (v1Size < v2Size){
				acutal = (sortAscending) ? LESSER : GREATER;
			} else {
				acutal = EQUAL;
			}
		} else if (returnType.equals(DATATYPE_COLLECTION)) {
			int v1Size = ((Collection) v1).size();
			int v2Size = ((Collection) v2).size();
			if (v1Size > v2Size) {
				acutal = (sortAscending) ? GREATER : LESSER;
			} else if (v1Size < v2Size){
				acutal = (sortAscending) ? LESSER : GREATER;
			} else {
				acutal = EQUAL;
			}
		} else {
			//Note that this also captures `DATATYPE_OBJECT`
			try {
				L.m("Type == " + returnType + ", No default parsing available, defaulting to calling .toString()");
				acutal = v1.toString().compareTo(v2.toString());
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		return acutal;
	}
	
	/**
	 * preparing target name of getter method for given sort field
	 *
	 * @param name a {@link java.lang.String}
	 * @return methodName a {@link java.lang.String}
	 */
	private String prepareTargetMethod(String name) {
		if(this.userOverrodeGetMethod) {
			return name;
		}
		StringBuffer fieldName =  new StringBuffer(METHOD_GET_PREFIX);
		fieldName.append(name.substring(0, 1).toUpperCase());
		fieldName.append(name.substring(1));
		return fieldName.toString();
	}
	
	/**
	 * fetching method from <code>Class</code> object through reflect
	 *
	 * @param obj - a {@link java.lang.Object} - input object
	 * @return method - a {@link java.lang.reflect.Method}
	 * @throws NoSuchMethodException
	 */
	private final Method getMethod(Object obj) throws NoSuchMethodException {
		return obj.getClass().getMethod(targetMethod, null);
	}
	
	/**
	 * dynamically invoking given method with given object through reflect
	 *
	 * @param method - a {@link java.lang.reflect.Method}
	 * @param obj - a {@link java.lang.Object}
	 * @return object - a {@link java.lang.Object} - return of given method
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	private final static Object invoke(Method method, Object obj) throws InvocationTargetException, IllegalAccessException {
		return method.invoke(obj, null);
	}
	
	/**
	 * fetching a value from given object
	 *
	 * @param obj - a {@link java.lang.Object}
	 * @return object - a {@link java.lang.Object} - return of given method
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 */
	private Object getValue(Object obj) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
		return invoke(getMethod(obj), obj);
	}
	
	/**
	 * identifying the comparison mode for given value
	 *
	 * @param o1 - a {@link java.lang.Object}
	 * @param o2 - a {@link java.lang.Object}
	 * @return compareMode
	 */
	private GenericComparator.CompareMode findCompareMode(Object o1, Object o2) {
		GenericComparator.CompareMode cm = GenericComparator.CompareMode.LESS_THAN;
		
		if(null != o1 & null != o2) {
			cm = GenericComparator.CompareMode.DEFAULT;
		} else if (null == o1 & null != o2) {
			cm = GenericComparator.CompareMode.LESS_THAN;
		} else if (null != o1 & null == o2) {
			cm = GenericComparator.CompareMode.GREATER_THAN;
		} else if (null == o1 & null == o2) {
			cm = GenericComparator.CompareMode.EQUAL;
		}
		
		return cm;
	}
	
	/**
	 * Determining positing for sorting
	 *
	 * @return -1 to change the sort order if appropriate.
	 */
	private int determinePosition() {
		return sortAscending ? GREATER : LESSER;
	}
}