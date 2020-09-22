package com.pgmacdesign.pgmactips.stackmanagement;

import com.pgmacdesign.pgmactips.utilities.L;
import com.pgmacdesign.pgmactips.utilities.MiscUtilities;
import com.pgmacdesign.pgmactips.utilities.StringUtilities;

import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import androidx.annotation.NonNull;

/**
 * This class is used for managing multiple stacks. An example would be if one needed to maintain
 * stacks of fragments for various 'paths' in an app and need to persist a managed list of
 * each of them.
 *
 * This variation is designed to use raw types; specifically: String, Integer, Long, Double
 *
 */
public class StackRawManager {
	
	private static enum StackType {
		TypeString, TypeInteger, TypeLong, TypeDouble, TypeGenericObject;
	}
	
	//region Static Vars
	private static final String BAD_STRING = "The string you passed was null or empty, please check your params and try again";
	private static final String INSTANTIATION_ERROR = "No stack was created; please check your params and re-instantiate the object";
	private static final String INVALID_KEY = "The key (int) you passed did not return a managed stack. Please check your key (See the ones sent in the constructor)";
	private static final String NO_NEGATIVE_NUMBERS_TO_POP = "Number of items to pop off the stack is <0. Number must be >0";
	private static final String INITIAL_VALUE_EMPTY= "Initial value be empty or null!";
	private static final String INITIAL_MAP_EMPTY = "Initial map cannot be empty or null!";
	private static final String INITIAL_MAP_KEYS_EMPTY = "Initial map keys be empty or null!";
	private static final String INITIAL_MAP_VALUES_EMPTY = "Initial map values be empty or null!";
	//endregion
	
	//region Instance Vars
	private StackType stackType;
	private Map<Integer, CustomStackManagerPOJO> managedStacks;
	private boolean maintainMinimumOneItemInStack, logChangesInStacks,
			allowStackDuplicates, shouldIgnoreStringCase;
	//endregion
	
	//region Builder
	
	/**
	 * Builder Class
	 */
	public static final class Builder {
		
		Map<Integer, String> initialStacksString;
		Map<Integer, Integer> initialStacksInteger;
		Map<Integer, Long> initialStacksLong;
		Map<Integer, Double> initialStacksDouble;
		Map<Integer, Object> initialStacksGenericObject;
		boolean maintainMinimumOneItemInStack;
		boolean logChangesInStacks;
		boolean allowStackDuplicates;
		boolean shouldIgnoreStringCase;
		StackType localStackType;
		
		/**
		 * Shortcut version to create a stack manager with 1 managed stack where the
		 * tag to reference it is always 0 (zero).
		 * @param firstOneInStack First item to put into stack zero
		 */
		public Builder(@NonNull String firstOneInStack) throws IllegalArgumentException {
			if(StringUtilities.isNullOrEmpty(firstOneInStack)){
				throw new IllegalArgumentException(INITIAL_VALUE_EMPTY);
			}
			Map<Integer, String> firstValues = new HashMap<>();
			firstValues.put(0, firstOneInStack);
			if(MiscUtilities.isMapNullOrEmpty(firstValues)){
				throw new IllegalArgumentException(INITIAL_MAP_EMPTY);
			}
			for(Map.Entry<Integer, String> map : firstValues.entrySet()){
				if(map.getKey() == null){
					throw new IllegalArgumentException(INITIAL_MAP_KEYS_EMPTY);
				}
				if(StringUtilities.isNullOrEmpty(map.getValue())){
					throw new IllegalArgumentException(INITIAL_MAP_VALUES_EMPTY);
				}
			}
			this.initialStacksString = firstValues;
			this.logChangesInStacks = false;
			this.shouldIgnoreStringCase = false;
			this.allowStackDuplicates = false;
			this.maintainMinimumOneItemInStack = true;
			this.localStackType = StackType.TypeString;
		}
		
		/**
		 * Shortcut version to create a stack manager with 1 managed stack where the
		 * tag to reference it is always 0 (zero).
		 * @param firstOneInStack First item to put into stack zero
		 */
		public Builder(@NonNull Integer firstOneInStack) throws IllegalArgumentException {
			if(StringUtilities.isNullOrEmpty(firstOneInStack)){
				throw new IllegalArgumentException(INITIAL_VALUE_EMPTY);
			}
			Map<Integer, Integer> firstValues = new HashMap<>();
			firstValues.put(0, firstOneInStack);
			if(MiscUtilities.isMapNullOrEmpty(firstValues)){
				throw new IllegalArgumentException(INITIAL_MAP_EMPTY);
			}
			for(Map.Entry<Integer, Integer> map : firstValues.entrySet()){
				if(map.getKey() == null){
					throw new IllegalArgumentException(INITIAL_MAP_KEYS_EMPTY);
				}
				if(map.getValue() == null){
					throw new IllegalArgumentException(INITIAL_MAP_VALUES_EMPTY);
				}
			}
			this.initialStacksInteger = firstValues;
			this.logChangesInStacks = false;
			this.shouldIgnoreStringCase = false;
			this.allowStackDuplicates = false;
			this.maintainMinimumOneItemInStack = true;
			this.localStackType = StackType.TypeInteger;
		}
		
		/**
		 * Shortcut version to create a stack manager with 1 managed stack where the
		 * tag to reference it is always 0 (zero).
		 * @param firstOneInStack First item to put into stack zero
		 */
		public Builder(@NonNull Long firstOneInStack) throws IllegalArgumentException {
			if(StringUtilities.isNullOrEmpty(firstOneInStack)){
				throw new IllegalArgumentException(INITIAL_VALUE_EMPTY);
			}
			Map<Integer, Long> firstValues = new HashMap<>();
			firstValues.put(0, firstOneInStack);
			if(MiscUtilities.isMapNullOrEmpty(firstValues)){
				throw new IllegalArgumentException(INITIAL_MAP_EMPTY);
			}
			for(Map.Entry<Integer, Long> map : firstValues.entrySet()){
				if(map.getKey() == null){
					throw new IllegalArgumentException(INITIAL_MAP_KEYS_EMPTY);
				}
				if(map.getValue() == null){
					throw new IllegalArgumentException(INITIAL_MAP_VALUES_EMPTY);
				}
			}
			this.initialStacksLong = firstValues;
			this.logChangesInStacks = false;
			this.shouldIgnoreStringCase = false;
			this.allowStackDuplicates = false;
			this.maintainMinimumOneItemInStack = true;
			this.localStackType = StackType.TypeLong;
		}
		
		/**
		 * Shortcut version to create a stack manager with 1 managed stack where the
		 * tag to reference it is always 0 (zero).
		 * @param firstOneInStack First item to put into stack zero
		 */
		public Builder(@NonNull Double firstOneInStack) throws IllegalArgumentException {
			if(StringUtilities.isNullOrEmpty(firstOneInStack)){
				throw new IllegalArgumentException(INITIAL_VALUE_EMPTY);
			}
			Map<Integer, Double> firstValues = new HashMap<>();
			firstValues.put(0, firstOneInStack);
			if(MiscUtilities.isMapNullOrEmpty(firstValues)){
				throw new IllegalArgumentException(INITIAL_MAP_EMPTY);
			}
			for(Map.Entry<Integer, Double> map : firstValues.entrySet()){
				if(map.getKey() == null){
					throw new IllegalArgumentException(INITIAL_MAP_KEYS_EMPTY);
				}
				if(map.getValue() == null){
					throw new IllegalArgumentException(INITIAL_MAP_VALUES_EMPTY);
				}
			}
			this.initialStacksDouble = firstValues;
			this.logChangesInStacks = false;
			this.shouldIgnoreStringCase = false;
			this.allowStackDuplicates = false;
			this.maintainMinimumOneItemInStack = true;
			this.localStackType = StackType.TypeDouble;
		}
		
		/**
		 * This builder starts off with as many managed stacks as are needed depending
		 * on the int tag + String combos sent in. IE, if you send in a map with 2 keys:
		 * 0, and 1, and the first String values are "Apple" and "Orange", there will
		 * now be 2 stacks held within the manager each with one item in them and their
		 * corresponding tags will match those passed in here
		 * @param firstItemInStacks A Map of int keys and String values
		 */
		public Builder(@NonNull Map<Integer, Object> firstItemInStacks) throws IllegalArgumentException{
			if(MiscUtilities.isMapNullOrEmpty(firstItemInStacks)){
				throw new IllegalArgumentException(INITIAL_MAP_EMPTY);
			}
			for(Map.Entry<Integer, Object> map : firstItemInStacks.entrySet()){
				if(map.getKey() == null){
					throw new IllegalArgumentException(INITIAL_MAP_KEYS_EMPTY);
				}
				if(map.getValue() == null){
					throw new IllegalArgumentException(INITIAL_MAP_VALUES_EMPTY);
				}
			}
			this.initialStacksGenericObject = firstItemInStacks;
			this.logChangesInStacks = false;
			this.shouldIgnoreStringCase = false;
			this.allowStackDuplicates = false;
			this.maintainMinimumOneItemInStack = true;
			this.localStackType = StackType.TypeGenericObject;
		}
		
		/**
		 * Should ignore the case of the Strings. Really only utilized when the
		 * {@link StackRawManager.Builder#allowStackDuplicates} is false;
		 * Note, Defaults to false.
		 * @param shouldIgnore If true, will ignore case when setting and if the stack
		 *                     duplicates is false, will act accordingly.
		 *                     IE, if you have "Pat" in the stack and pass in "PAT",
		 *                     it will replace the "Pat" with "PAT" and put it on top.
		 *                     If allow duplicates is true and you have "Pat" in the stack
		 *                     and pass in "PAT", it will add "PAT regardless.
		 * @return
		 */
		public Builder setShouldIgnoreStringCase(boolean shouldIgnore){
			this.shouldIgnoreStringCase = shouldIgnore;
			return this;
		}
		
		/**
		 * Set whether logging should occur whenever the stack changes.
		 * Note, Defaults to false.
		 * @param shouldLog If true, logs will occur in the logcat whenever
		 *                  the stack changes with either append or pop.
		 * @return
		 */
		public Builder setShouldLogChangesInStacks(boolean shouldLog){
			this.logChangesInStacks = shouldLog;
			return this;
		}
		
		/**
		 * Should Allow the stack to have duplicates of the same value.
		 * Note, Defaults to false.
		 * @param shouldAllow If true, will allow duplicates of the same String,
		 *                    else, if same string is appended, it will remove it from
		 *                    the position it was in and move it to the top of the list
		 * @return
		 */
		public Builder setShouldAllowStackDuplicates(boolean shouldAllow){
			this.allowStackDuplicates = shouldAllow;
			return this;
		}
		
		/**
		 * Should Maintain at least one item in a stack.
		 * Note, Defaults to true.
		 * @param shouldKeepAtLeastOne If true, popping the stack will always leave
		 *                             the initial one in place, else it will pop all
		 *                             the way down to zero
		 * @return
		 */
		public Builder setShouldMaintainMinimumOneItemInStack(boolean shouldKeepAtLeastOne){
			this.maintainMinimumOneItemInStack = shouldKeepAtLeastOne;
			return this;
		}
		
		/**
		 * Build
		 * @return {@link StackRawManager}
		 */
		public StackRawManager build(){
			return new StackRawManager(this);
		}
	}
	
	//endregion
	
	//region Constructor
	
	/**
	 * Constructor, requires Builder Class.
	 * @param builder {@link Builder}
	 */
	private StackRawManager(StackRawManager.Builder builder){
		if(builder == null){
			return;
		}
		this.maintainMinimumOneItemInStack = builder.maintainMinimumOneItemInStack;
		this.allowStackDuplicates = builder.allowStackDuplicates;
		this.shouldIgnoreStringCase = builder.shouldIgnoreStringCase;
		this.logChangesInStacks = builder.logChangesInStacks;
		this.stackType = builder.localStackType;
		this.init(builder.initialStacksString, builder.initialStacksInteger,
				builder.initialStacksLong, builder.initialStacksDouble, builder.initialStacksGenericObject);
	}
	//endregion
	
	//region Init
	
	/**
	 * Init method
	 */
	private void init(Map<Integer, String> firstItemsInStackString,
	                  Map<Integer, Integer> firstItemsInStackInteger,
	                  Map<Integer, Long> firstItemsInStackLong,
	                  Map<Integer, Double> firstItemsInStackDouble,
	                  Map<Integer, Object> firstItemsInStackGenericObject) throws StackRawManagerException {
		this.managedStacks = new HashMap<>();
		if(this.stackType == StackType.TypeString){
			for (Map.Entry<Integer, String> map : firstItemsInStackString.entrySet()) {
				if (map == null) {
					continue;
				}
				Integer key = map.getKey();
				String value = map.getValue();
				if (key == null || StringUtilities.isNullOrEmpty(value)) {
					continue;
				}
				Stack<String> stackToManage = new Stack<>();
				stackToManage.push(value);
				CustomStackManagerPOJO pojo = new CustomStackManagerPOJO();
				pojo.setKey(key);
				pojo.setManagedStack(stackToManage);
				this.managedStacks.put(key, pojo);
			}
		} else if (this.stackType == StackType.TypeInteger){
			for (Map.Entry<Integer, Integer> map : firstItemsInStackInteger.entrySet()) {
				if (map == null) {
					continue;
				}
				Integer key = map.getKey();
				Integer value = map.getValue();
				if (key == null || value == null) {
					continue;
				}
				Stack<Integer> stackToManage = new Stack<>();
				stackToManage.push(value);
				CustomStackManagerPOJO pojo = new CustomStackManagerPOJO();
				pojo.setKey(key);
				pojo.setManagedStack(stackToManage);
				this.managedStacks.put(key, pojo);
			}
		} else if (this.stackType == StackType.TypeLong){
			for (Map.Entry<Integer, Long> map : firstItemsInStackLong.entrySet()) {
				if (map == null) {
					continue;
				}
				Integer key = map.getKey();
				Long value = map.getValue();
				if (key == null || value == null) {
					continue;
				}
				Stack<Long> stackToManage = new Stack<>();
				stackToManage.push(value);
				CustomStackManagerPOJO pojo = new CustomStackManagerPOJO();
				pojo.setKey(key);
				pojo.setManagedStack(stackToManage);
				this.managedStacks.put(key, pojo);
			}
		} else if (this.stackType == StackType.TypeDouble){
			for (Map.Entry<Integer, Double> map : firstItemsInStackDouble.entrySet()) {
				if (map == null) {
					continue;
				}
				Integer key = map.getKey();
				Double value = map.getValue();
				if (key == null || value == null) {
					continue;
				}
				Stack<Double> stackToManage = new Stack<>();
				stackToManage.push(value);
				CustomStackManagerPOJO pojo = new CustomStackManagerPOJO();
				pojo.setKey(key);
				pojo.setManagedStack(stackToManage);
				this.managedStacks.put(key, pojo);
			}
		} else if (this.stackType == StackType.TypeGenericObject){
			for (Map.Entry<Integer, Object> map : firstItemsInStackGenericObject.entrySet()) {
				if (map == null) {
					continue;
				}
				Integer key = map.getKey();
				Object value = map.getValue();
				if (key == null || value == null) {
					continue;
				}
				Stack<Object> stackToManage = new Stack<>();
				stackToManage.push(value);
				CustomStackManagerPOJO pojo = new CustomStackManagerPOJO();
				pojo.setKey(key);
				pojo.setManagedStack(stackToManage);
				this.managedStacks.put(key, pojo);
			}
		}
		if (MiscUtilities.isMapNullOrEmpty(this.managedStacks)) {
			throw buildException(INSTANTIATION_ERROR, null, null);
		}
	}
	
	//endregion
	
	//region Public Methods for Managing State
	
	//region Clearing Stacks
	/**
	 * Clear one stack via the tag sent. Follows the boolean rule set via constructor about maintaining
	 * 1 left in the stack if the boolean {@link com.pgmacdesign.pgmactips.stackmanagement.StackRawManager#maintainMinimumOneItemInStack}
	 * is set to true
	 *
	 * @ {@link StackRawManagerException}
	 */
	public void clearOneStack() {
		clearOneStack(0);
	}
	
	/**
	 * Clear one stack via the tag sent. Follows the boolean rule set via constructor about maintaining
	 * 1 left in the stack if the boolean {@link com.pgmacdesign.pgmactips.stackmanagement.StackRawManager#maintainMinimumOneItemInStack}
	 * is set to true
	 *
	 * @ {@link StackRawManagerException}
	 */
	public void clearOneStack(int tag) {
		try {
			CustomStackManagerPOJO pojo = getStackPOJO(tag);
			if (pojo != null) {
				Stack<String> myStack = (Stack<String>) pojo.getManagedStack();
				if (myStack != null) {
					popTheStack(tag, myStack.size());
				}
			}
		} catch (StackRawManagerException sme) {
			L.m(sme.toString());
		}
	}
	
	/**
	 * Clear all stacks. Follows the boolean rule set via constructor about maintaining
	 * 1 left in the stack if the boolean {@link com.pgmacdesign.pgmactips.stackmanagement.StackRawManager#maintainMinimumOneItemInStack}
	 * is set to true
	 *
	 * @ {@link StackRawManagerException}
	 */
	public void clearAllStacks() {
		for (Map.Entry<Integer, CustomStackManagerPOJO> map : managedStacks.entrySet()) {
			Integer key = map.getKey();
			CustomStackManagerPOJO pojo = map.getValue();
			if (key != null && pojo != null) {
				Stack<String> myStack = (Stack<String>) pojo.getManagedStack();
				if (myStack != null) {
					if(!myStack.isEmpty()){
						popTheStack(key, myStack.size());
					}
				}
			}
		}
	}
	
	//endregion
	
	//region Appending Items to the Stack
	
	/**
	 * Append Strings to the stack (overloaded to allow for position 0 to be used)
	 *
	 * @param strToAdd String to add to the stack
	 * @return String of the one at the top of the stack
	 * @ {@link StackRawManagerException}
	 */
	public String appendToTheStack(String strToAdd) {
		return appendToTheStack(0, strToAdd);
	}
	
	/**
	 * Append Integers to the stack (overloaded to allow for position 0 to be used)
	 *
	 * @param strToAdd String to add to the stack
	 * @return String of the one at the top of the stack
	 * @ {@link StackRawManagerException}
	 */
	public Integer appendToTheStack(Integer strToAdd) {
		return appendToTheStack(0, strToAdd);
	}
	
	/**
	 * Append Longs to the stack (overloaded to allow for position 0 to be used)
	 *
	 * @param strToAdd String to add to the stack
	 * @return String of the one at the top of the stack
	 * @ {@link StackRawManagerException}
	 */
	public Long appendToTheStack(Long strToAdd) {
		return appendToTheStack(0, strToAdd);
	}
	
	/**
	 * Append Doubles to the stack (overloaded to allow for position 0 to be used)
	 *
	 * @param strToAdd String to add to the stack
	 * @return String of the one at the top of the stack
	 * @ {@link StackRawManagerException}
	 */
	public Double appendToTheStack(Double strToAdd) {
		return appendToTheStack(0, strToAdd);
	}
	
	/**
	 * Append Strings to the stack
	 *
	 * @param tagToMatchToStrings Int tag to match the map in the constructor
	 * @param strToAdd         String to add / append to the stack
	 * @return String str of the one at the top of the stack
	 * @ {@link StackRawManagerException}
	 */
	public String appendToTheStack(int tagToMatchToStrings, String strToAdd) {
		try {
			this.manageNullOrEmptyStrings(strToAdd);
			CustomStackManagerPOJO pojo = getStackPOJO(tagToMatchToStrings);
			Stack stack = pojo.getManagedStack();
			if(stack == null){
				return null;
			}
			if(stack.contains(strToAdd)){
				if (allowStackDuplicates) {
					stack.push(strToAdd);
				} else {
					//Replace position to the top
					stack.remove(strToAdd);
					stack.push(strToAdd);
				}
			} else {
				stack.push(strToAdd);
			}
			if (logChangesInStacks) {
				L.m("stack matching tag " + tagToMatchToStrings + " String: " + stack.toString());
			}
			return ((String) stack.peek());
		} catch (StackRawManagerException sme) {
			L.m(sme.toString());
			return null;
		}
	}
	
	/**
	 * Append Strings to the stack
	 *
	 * @param tagToMatchToStrings Int tag to match the map in the constructor
	 * @param strToAdd         String to add / append to the stack
	 * @return String str of the one at the top of the stack
	 * @ {@link StackRawManagerException}
	 */
	public Integer appendToTheStack(int tagToMatchToStrings, Integer strToAdd) {
		try {
			this.manageBadArgs(strToAdd);
			CustomStackManagerPOJO pojo = getStackPOJO(tagToMatchToStrings);
			Stack stack = pojo.getManagedStack();
			if(stack == null){
				return null;
			}
			if(stack.contains(strToAdd)){
				if (allowStackDuplicates) {
					stack.push(strToAdd);
				} else {
					//Replace position to the top
					stack.remove(strToAdd);
					stack.push(strToAdd);
				}
			} else {
				stack.push(strToAdd);
			}
			if (logChangesInStacks) {
				L.m("stack matching tag " + tagToMatchToStrings + " Integers: " + stack.toString());
			}
			return ((Integer) stack.peek());
		} catch (StackRawManagerException sme) {
			L.m(sme.toString());
			return null;
		}
	}
	
	/**
	 * Append Strings to the stack
	 *
	 * @param tagToMatchToStrings Int tag to match the map in the constructor
	 * @param strToAdd         String to add / append to the stack
	 * @return String str of the one at the top of the stack
	 * @ {@link StackRawManagerException}
	 */
	public Long appendToTheStack(int tagToMatchToStrings, Long strToAdd) {
		try {
			this.manageBadArgs(strToAdd);
			CustomStackManagerPOJO pojo = getStackPOJO(tagToMatchToStrings);
			Stack stack = pojo.getManagedStack();
			if(stack == null){
				return null;
			}
			if(stack.contains(strToAdd)){
				if (allowStackDuplicates) {
					stack.push(strToAdd);
				} else {
					//Replace position to the top
					stack.remove(strToAdd);
					stack.push(strToAdd);
				}
			} else {
				stack.push(strToAdd);
			}
			if (logChangesInStacks) {
				L.m("stack matching tag " + tagToMatchToStrings + " Longs: " + stack.toString());
			}
			return ((Long) stack.peek());
		} catch (StackRawManagerException sme) {
			L.m(sme.toString());
			return null;
		}
	}
	
	/**
	 * Append Strings to the stack
	 *
	 * @param tagToMatchToStrings Int tag to match the map in the constructor
	 * @param strToAdd         String to add / append to the stack
	 * @return String str of the one at the top of the stack
	 * @ {@link StackRawManagerException}
	 */
	public Double appendToTheStack(int tagToMatchToStrings, Double strToAdd) {
		try {
			this.manageBadArgs(strToAdd);
			CustomStackManagerPOJO pojo = getStackPOJO(tagToMatchToStrings);
			Stack stack = pojo.getManagedStack();
			if(stack == null){
				return null;
			}
			if(stack.contains(strToAdd)){
				if (allowStackDuplicates) {
					stack.push(strToAdd);
				} else {
					//Replace position to the top
					stack.remove(strToAdd);
					stack.push(strToAdd);
				}
			} else {
				stack.push(strToAdd);
			}
			if (logChangesInStacks) {
				L.m("stack matching tag " + tagToMatchToStrings + " Doubles: " + stack.toString());
			}
			return ((Double) stack.peek());
		} catch (StackRawManagerException sme) {
			L.m(sme.toString());
			return null;
		}
	}
	
	/**
	 * Append Strings to the stack (overloaded to allow for position 0 to be used)
	 *
	 * @param strsToAdd List of Strings to add / append to the stack
	 * @return String of the one at the top of the stack
	 * @ {@link StackRawManagerException}
	 */
	public String appendToTheStack(List<String> strsToAdd) {
		return appendToTheStack(0, strsToAdd);
	}
	
	/**
	 * Append Strings to the stack
	 *
	 * @param tagToMatchToStrings Int tag to match the map in the constructor
	 * @param strsToAdd        List of Strings to add / append to the stack
	 * @return String of the one at the top of the stack
	 * @ {@link StackRawManagerException}
	 */
	public String appendToTheStack(int tagToMatchToStrings, List<String> strsToAdd) {
		try {
			for (String s : strsToAdd) {
				manageNullOrEmptyStrings(s);
			}
			CustomStackManagerPOJO pojo = getStackPOJO(tagToMatchToStrings);
			Stack stack = pojo.getManagedStack();
			if(stack == null){
				return null;
			}
			for(String strToAdd : strsToAdd){
				if(stack.contains(strToAdd)){
					if (allowStackDuplicates) {
						stack.push(strToAdd);
					} else {
						//Replace position to the top
						stack.remove(strToAdd);
						stack.push(strToAdd);
					}
				} else {
					stack.push(strToAdd);
				}
				if (logChangesInStacks) {
					L.m("stack matching tag " + tagToMatchToStrings + " String: " + stack.toString());
				}
			}
			return ((String) stack.peek());
		} catch (StackRawManagerException sme) {
			L.m(sme.toString());
			return null;
		}
	}
	
	//endregion
	
	//region Popping the Stack
	
	
	/**
	 * Pop the stack matching the int String used. (overloaded to allow for position 0 to be used)
	 *
	 * @return Returns the String at the top of the stack. If the stack is empty, returns null
	 * @ {@link StackRawManagerException}
	 */
	public Object popTheStack() {
		return popTheStack(0);
	}
	
	/**
	 * Pop the stack matching the int String used.
	 *
	 * @param tagToMatchToStrings Int tag to match the map in the constructor
	 * @return Returns the String at the top of the stack. If the stack is empty, returns null
	 * @ {@link StackRawManagerException}
	 */
	public Object popTheStack(int tagToMatchToStrings) {
		try {
			CustomStackManagerPOJO pojo = getStackPOJO(tagToMatchToStrings);
			Stack stack = pojo.getManagedStack();
			if(stack == null){
				return null;
			}
			int size = stack.size();
			if (maintainMinimumOneItemInStack) {
				if (size > 1) {
					stack.pop();
				}
			} else {
				if (size > 0) {
					stack.pop();
				}
			}
			if (logChangesInStacks) {
				L.m("stack matching tag " + tagToMatchToStrings + " String: " + stack.toString());
			}
			if (size > 0) {
				return stack.peek();
//				switch (this.stackType){
//					case TypeString:
//						return ((String) stack.peek());
//
//					case TypeLong:
//						return ((Long) stack.peek());
//
//					case TypeDouble:
//						return ((Double) stack.peek());
//
//					case TypeInteger:
//						return ((Integer) stack.peek());
//
//					case TypeGenericObject:
//						return ((Object) stack.peek());
//
//				}
			} else {
				return null;
			}
		} catch (StackRawManagerException sme) {
			L.m(sme.toString());
			return null;
		}
	}
	
	/**
	 * Pop the stack matching the int String used.
	 *
	 * @param tagToMatchToStrings Int tag to match the map in the constructor
	 * @param numToPop          Number to pop off the stack
	 * @return Returns the String at the top of the stack. If the stack is empty, returns null
	 * @ {@link StackRawManagerException}
	 */
	public Object popTheStack(int tagToMatchToStrings, int numToPop) {
		try {
			if (numToPop <= 0) {
				throw buildException(NO_NEGATIVE_NUMBERS_TO_POP, null, tagToMatchToStrings);
			}
			CustomStackManagerPOJO pojo = getStackPOJO(tagToMatchToStrings);
			Stack stack = pojo.getManagedStack();
			if (stack == null) {
				return null;
			}
			int size = stack.size();
			while (numToPop > 0) {
				if (maintainMinimumOneItemInStack) {
					if (size > 1) {
						stack.pop();
					}
				} else {
					if (size > 0) {
						stack.pop();
					}
				}
				numToPop--;
			}
			if (logChangesInStacks) {
				L.m("stack matching tag " + tagToMatchToStrings + " String: " + stack.toString());
			}
			if (size > 0) {
				return (stack.peek());
			} else {
				return null;
			}
		} catch (EmptyStackException ese){
			L.m(ese.getMessage());
			return null;
		} catch (StackRawManagerException sme) {
			L.m(sme.toString());
			return null;
		}
	}
	
	//endregion
	
	//region Getter Methods
	
	/**
	 * Gets the Stack<String>. If no stack is found, print exception and return null
	 * Overloaded to allow for single stack management to be used
	 *
	 * @return {@link Stack<String>}
	 * @ {@link StackRawManagerException}
	 */
	public Stack<Object> getStack() {
		return getStack(0);
	}
	
	/**
	 * Gets the Stack<String>. If no stack is found, print exception and return null
	 *
	 * @param tag Tag matches one(s) passed in Constructor
	 * @return {@link Stack<String>}
	 * @ {@link StackRawManagerException}
	 */
	public Stack<Object> getStack(int tag) {
		try {
			CustomStackManagerPOJO pojo = getStackPOJO(tag);
			Stack<Object> stackToManage = pojo.getManagedStack();
			return stackToManage;
		} catch (StackRawManagerException sme) {
			L.m(sme.toString());
			return null;
		}
	}
	
	//endregion
	
	//endregion
	
	//region Public Misc Methods
	
	/**
	 * Get the stack size of the position (int tag) passed
	 * @param pos
	 * @return
	 */
	public int getStackSize(int pos) {
		try {
			Stack stack = getStack(pos);
			return ((stack == null) ? 0 : stack.size());
		} catch (StackRawManagerException sme) {
			L.m(sme.toString());
			return 0;
		}
	}
	
	/**
	 * Get the stack size of the first managed stack
	 * @return
	 */
	public int getStackSize() {
		return getStackSize(0);
	}
	
	//endregion
	
	//region Private Classes
	
	/**
	 * Checker for whether or not the stack already contains the String passed
	 *
	 * @param stack       Stack containing current Strings
	 * @param strToCheck String to check if it is already included in the stack
	 * @return boolean, if true, stack already contains the String object
	 */
	private boolean stackContainsString(Stack<String> stack, String strToCheck) {
		try {
			for (String e : stack) {
				if(StackRawManager.this.shouldIgnoreStringCase){
					if (e.equalsIgnoreCase(strToCheck)) {
						return true;
					}
				} else {
					if (e.equals(strToCheck)) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}
	
	/**
	 * Checker for whether or not the stack already contains the String passed
	 *
	 * @param stack       Stack containing current Strings
	 * @param strToCheck String to check if it is already included in the stack
	 * @return boolean, if true, stack already contains the String object
	 */
	private boolean stackContainsString(Stack<Integer> stack, Integer strToCheck) {
		try {
			for (Integer e : stack) {
				if (e.equals(strToCheck)) {
					return true;
				}
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}
	
	/**
	 * Checker for whether or not the stack already contains the String passed
	 *
	 * @param stack       Stack containing current Strings
	 * @param strToCheck String to check if it is already included in the stack
	 * @return boolean, if true, stack already contains the String object
	 */
	private boolean stackContainsString(Stack<Long> stack, Long strToCheck) {
		try {
			for (Long e : stack) {
				if (e.equals(strToCheck)) {
					return true;
				}
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}
	
	/**
	 * Checker for whether or not the stack already contains the String passed
	 *
	 * @param stack       Stack containing current Strings
	 * @param strToCheck String to check if it is already included in the stack
	 * @return boolean, if true, stack already contains the String object
	 */
	private boolean stackContainsString(Stack<Double> stack, Double strToCheck) {
		try {
			for (Double e : stack) {
				if (e.equals(strToCheck)) {
					return true;
				}
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}
	
	/**
	 * Manage null Strings passed. If null is passed, will throw exception
	 *
	 * @param strWorkingOn
	 * @ {@link StackRawManagerException}
	 */
	private void manageNullOrEmptyStrings(String strWorkingOn) {
		if (StringUtilities.isNullOrEmpty(strWorkingOn)) {
			throw buildException(BAD_STRING, strWorkingOn, null);
		}
	}
	
	/**
	 * Manage null Objects passed. If null is passed, will throw exception
	 *
	 * @param obj
	 * @ {@link StackRawManagerException}
	 */
	private void manageBadArgs(Object obj) {
		if(obj == null){
			throw buildException(BAD_STRING, obj, null);
		} else {
			if(obj instanceof String){
				manageNullOrEmptyStrings((String)obj);
			}
		}
	}
	
	/**
	 * Gets the POJO. If null is returned, will throw exception
	 *
	 * @param tag Tag matches one(s) passed in Constructor
	 * @return {@link CustomStackManagerPOJO}
	 * @ {@link StackRawManagerException}
	 */
	private CustomStackManagerPOJO getStackPOJO(int tag) throws StackRawManagerException {
		if (tag < 0 || tag >= this.managedStacks.size()) {
			throw buildException(INVALID_KEY, null, tag);
		}
		CustomStackManagerPOJO pojo = managedStacks.get(tag);
		if (pojo == null) {
			throw buildException(INVALID_KEY, null, tag);
		}
		return pojo;
	}
	
	//endregion
	
	//region POJO For Stack Management
	
	/**
	 * Private class used to manage stacks
	 */
	private class CustomStackManagerPOJO {
		private int key;
		private Stack<String> managedStack;
		
		int getKey() {
			return key;
		}
		
		void setKey(int key) {
			this.key = key;
		}
		
		Stack getManagedStack() {
			return managedStack;
		}
		
		void setManagedStack(Stack managedStack) {
			this.managedStack = managedStack;
		}
	}
	
	//endregion
	
	//region Misc Utilities
	
	/**
	 * Build the the exception to throw
	 *
	 * @return {@link StackRawManagerException}
	 */
	private StackRawManagerException buildException(String desc, Object strPassed, Integer keyPassed) {
		StackRawManagerException e = new StackRawManagerException();
		if (StringUtilities.isNullOrEmpty(desc)) {
			desc = "An unknown error has occurred";
		}
		e.setErrorMessage(desc);
		e.setKey(keyPassed);
		String str = (strPassed != null) ? strPassed.toString() : "Null";
		e.setStr(str);
		return e;
	}
	
	//endregion
	
}
