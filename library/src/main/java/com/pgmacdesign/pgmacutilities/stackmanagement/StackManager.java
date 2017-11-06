package com.pgmacdesign.pgmacutilities.stackmanagement;

import android.support.annotation.NonNull;

import com.pgmacdesign.pgmacutilities.utilities.L;
import com.pgmacdesign.pgmacutilities.utilities.MiscUtilities;
import com.pgmacdesign.pgmacutilities.utilities.StringUtilities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Created by Patrick-SSD2 on 11/5/2017.
 */

public class StackManager <E extends Enum<E>>  {

	private static final String BAD_ENUM = "The enum you passed was null, please check your params and try again";
	private static final String INSTANTIATION_ERROR = "No stack was created; please check your params and re-instantiate the object";
	private static final String INVALID_KEY = "The key (int) you passed did not return a managed stack. Please check your key (See the ones sent in the constructor)";
	private static final String NO_NEGATIVE_NUMBERS_TO_POP = "Number of items to pop off the stack is <0. Number must be >0";
	
	private Map<Integer, CustomStackManagerPOJO> managedStacks;
	private boolean maintainMinimumOneItemInStack, logChangesInStacks, allowEnumStackDuplicates;
	
	///////////////
	//Constructor//
	///////////////
	
	public StackManager(@NonNull Map<Integer, List<E>> enumTagsAndTypes,
	                    @NonNull Map<Integer, E> firstItemInStack) throws StackManagerException{
		this.maintainMinimumOneItemInStack = false;
		this.allowEnumStackDuplicates = false;
		init(enumTagsAndTypes, firstItemInStack);
	}
	
	public StackManager(@NonNull Map<Integer, List<E>> enumTagsAndTypes,
	                    @NonNull Map<Integer, E> firstItemInStack,
	                    boolean maintainMinimumOneItemInStack) throws StackManagerException{
		this.maintainMinimumOneItemInStack = maintainMinimumOneItemInStack;
		this.allowEnumStackDuplicates = false;
		init(enumTagsAndTypes, firstItemInStack);
	}
	
	public StackManager(@NonNull Map<Integer, List<E>> enumTagsAndTypes,
	                    @NonNull Map<Integer, E> firstItemInStack,
	                    boolean maintainMinimumOneItemInStack,
	                    boolean allowEnumStackDuplicates) throws StackManagerException{
		this.maintainMinimumOneItemInStack = maintainMinimumOneItemInStack;
		this.allowEnumStackDuplicates = allowEnumStackDuplicates;
		init(enumTagsAndTypes, firstItemInStack);
	}
	
	public void enableLogging(boolean enable){
		this.logChangesInStacks = enable;
	}
	
	private void init(@NonNull Map<Integer, List<E>> enumTagsAndTypes,
	                  @NonNull Map<Integer, E> firstItemInStack)throws StackManagerException{
		this.enableLogging(false);
		this.managedStacks = new HashMap<>();
		for(Map.Entry<Integer, List<E>> map : enumTagsAndTypes.entrySet()){
			if(map == null){
				continue;
			}
			Integer key = map.getKey();
			List<E> values = map.getValue();
			if(key == null || MiscUtilities.isListNullOrEmpty(values)){
				continue;
			}
			E firstItem = firstItemInStack.get(key);
			if(firstItem == null){
				continue;
			}
			Stack<E> stackToManage = new Stack<>();
			stackToManage.push(firstItem);
			CustomStackManagerPOJO pojo = new CustomStackManagerPOJO();
			pojo.setEnumTypes(values);
			pojo.setKey(key);
			pojo.setManagedStack(stackToManage);
			managedStacks.put(key, pojo);
		}
		if(MiscUtilities.isMapNullOrEmpty(managedStacks)){
			throw buildException(INSTANTIATION_ERROR, null, null);
		}
	}
	
	///////////////////////////////////////////
	//Public classes for managing Stack State//
	///////////////////////////////////////////
	
	/**
	 * Append enums to the stack
	 * @param tagToMatchToEnums Int tag to match the map in the constructor
	 * @param enumToAdd Enum to add / append to the stack
	 * @return Enum Enum of the one at the top of the stack
	 * @throws StackManagerException {@link StackManagerException}
	 */
	public E appendToStack(int tagToMatchToEnums, E enumToAdd) throws StackManagerException{
		manageNullEnums(enumToAdd);
		CustomStackManagerPOJO pojo = getStackPOJO(tagToMatchToEnums);
		List<E> enums = pojo.getEnumTypes();
		Stack stack = pojo.getManagedStack();
		for(Enum e : enums){
			if(e == null){
				continue;
			}
			if(e == enumToAdd){
				if(allowEnumStackDuplicates){
					stack.push(enumToAdd);
				} else {
					if(!stackContainsEnum(stack, enumToAdd)){
						stack.push(enumToAdd);
					}
				}
			}
		}
		if(logChangesInStacks){
			L.m("stack matching tag " + tagToMatchToEnums + " enum: " + stack.toString());
		}
		return ((E) stack.peek());
	}
	
	/**
	 * Append enums to the stack
	 * @param tagToMatchToEnums Int tag to match the map in the constructor
	 * @param enumsToAdd List of Enums to add / append to the stack
	 * @return Enum of the one at the top of the stack
	 * @throws StackManagerException {@link StackManagerException}
	 */
	public E appendToStack(int tagToMatchToEnums, List<E> enumsToAdd) throws StackManagerException{
		for(E e : enumsToAdd){
			manageNullEnums(e);
		}
		CustomStackManagerPOJO pojo = getStackPOJO(tagToMatchToEnums);
		List<E> enums = pojo.getEnumTypes();
		Stack stack = pojo.getManagedStack();
		for(Enum e : enums){
			if(e == null){
				continue;
			}
			for(E e1 : enumsToAdd) {
				if (e == e1) {
					if(allowEnumStackDuplicates){
						stack.push(e1);
					} else {
						if(!stackContainsEnum(stack, e1)){
							stack.push(e1);
						}
					}
					
				}
			}
		}
		if(logChangesInStacks){
			L.m("stack matching tag " + tagToMatchToEnums + " enum: " + stack.toString());
		}
		return ((E) stack.peek());
	}
	
	/**
	 * Pop the stack matching the int enum used.
	 * @param tagToMatchToEnums Int tag to match the map in the constructor
	 * @return Returns the enum at the top of the stack. If the stack is empty, returns null
	 * @throws StackManagerException {@link StackManagerException}
	 */
	public E popTheStack(int tagToMatchToEnums) throws StackManagerException{
		CustomStackManagerPOJO pojo = getStackPOJO(tagToMatchToEnums);
		Stack stack = pojo.getManagedStack();
		int size = stack.size();
		if(maintainMinimumOneItemInStack){
			if(size > 1){
				stack.pop();
			}
		} else {
			if(size > 0){
				stack.pop();
			}
		}
		if(logChangesInStacks){
			L.m("stack matching tag " + tagToMatchToEnums + " enum: " + stack.toString());
		}
		if(size > 0) {
			return ((E) stack.peek());
		} else {
			return null;
		}
	}
	
	/**
	 * Pop the stack matching the int enum used.
	 * @param tagToMatchToEnums Int tag to match the map in the constructor
	 * @param numToPop Number to pop off the stack
	 * @return Returns the enum at the top of the stack. If the stack is empty, returns null
	 * @throws StackManagerException {@link StackManagerException}
	 */
	public E popTheStack(int tagToMatchToEnums, int numToPop) throws StackManagerException{
		if(numToPop <= 0){
			throw buildException(NO_NEGATIVE_NUMBERS_TO_POP, null, tagToMatchToEnums);
		}
		CustomStackManagerPOJO pojo = getStackPOJO(tagToMatchToEnums);
		Stack stack = pojo.getManagedStack();
		int size = stack.size();
		while(numToPop > 0) {
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
		if(logChangesInStacks){
			L.m("stack matching tag " + tagToMatchToEnums + " enum: " + stack.toString());
		}
		if(size > 0) {
			return ((E) stack.peek());
		} else {
			return null;
		}
	}
	
	//////////////////////////////////////
	//Private classes for arg management//
	//////////////////////////////////////
	
	/**
	 * Checker for whether or not the stack already contains the enum passed
	 * @param stack Stack containing current enums
	 * @param enumToCheck Enum to check if it is already included in the stack
	 * @return boolean, if true, stack already contains the enum object
	 */
	private boolean stackContainsEnum(Stack<E> stack, E enumToCheck){
		try {
			for(E e : stack){
				if(e == enumToCheck){
					return true;
				}
			}
		} catch (Exception e){
			return false;
		}
		return false;
	}
	
	/**
	 * Manage null enums passed. If null is passed, will throw exception
	 * @param enumWorkingOn Matches one(s) passed in Constructor
	 * @throws StackManagerException {@link StackManagerException}
	 */
	private void manageNullEnums(E enumWorkingOn) throws StackManagerException {
		if(enumWorkingOn == null){
			throw buildException(BAD_ENUM, enumWorkingOn, null);
		}
	}
	
	/**
	 * Gets the POJO. If null is returned, will throw exception
	 * @param tag Tag matches one(s) passed in Constructor
	 * @return {@link CustomStackManagerPOJO}
	 * @throws StackManagerException {@link StackManagerException}
	 */
	private CustomStackManagerPOJO getStackPOJO(int tag) throws StackManagerException {
		CustomStackManagerPOJO pojo = managedStacks.get(tag);
		if(pojo == null){
			throw buildException(INVALID_KEY, null, tag);
		}
		return pojo;
	}
	
	///////////////////////////////
	//Object for Stack Management//
	///////////////////////////////
	
	/**
	 * Private class used to manage stacks
	 */
	private class CustomStackManagerPOJO {
		private int key;
		private List<E> enumTypes;
		private Stack<E> managedStack;
		
		int getKey() {
			return key;
		}
		
		void setKey(int key) {
			this.key = key;
		}
		
		List<E> getEnumTypes() {
			return enumTypes;
		}
		
		void setEnumTypes(List<E> enumTypes) {
			this.enumTypes = enumTypes;
		}
		
		Stack getManagedStack() {
			return managedStack;
		}
		
		void setManagedStack(Stack managedStack) {
			this.managedStack = managedStack;
		}
	}
	
	
	//////////////////
	//Misc Utilities//
	//////////////////
	
	/**
	 * Build the the exception to throw
	 * @return {@link StackManagerException}
	 */
	private StackManagerException buildException(String desc, E enumPassed, Integer keyPassed){
		StackManagerException e = new StackManagerException();
		if(StringUtilities.isNullOrEmpty(desc)){
			desc = "An unknown error has occurred";
		}
		e.setErrorMessage(desc);
		e.setKey(keyPassed);
		String enumString = (enumPassed != null) ? enumPassed.toString() : "Null";
		e.setEnumToString(enumString);
		return e;
	}
}
