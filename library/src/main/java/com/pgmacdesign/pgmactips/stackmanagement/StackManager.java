package com.pgmacdesign.pgmactips.stackmanagement;

import android.support.annotation.NonNull;

import com.pgmacdesign.pgmactips.utilities.L;
import com.pgmacdesign.pgmactips.utilities.MiscUtilities;
import com.pgmacdesign.pgmactips.utilities.StringUtilities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * This class is used for managing multiple stacks. An example would be if one needed to maintain
 * stacks of fragments for various 'paths' in an app and need to persist a managed list of
 * each of them.
 * Created by Patrick-SSD2 on 11/5/2017.
 */
public class StackManager<E extends Enum<E>> {

    private static final String BAD_ENUM = "The enum you passed was null, please check your params and try again";
    private static final String INSTANTIATION_ERROR = "No stack was created; please check your params and re-instantiate the object";
    private static final String INVALID_KEY = "The key (int) you passed did not return a managed stack. Please check your key (See the ones sent in the constructor)";
    private static final String NO_NEGATIVE_NUMBERS_TO_POP = "Number of items to pop off the stack is <0. Number must be >0";

    private Map<Integer, CustomStackManagerPOJO> managedStacks;
    private boolean maintainMinimumOneItemInStack, logChangesInStacks, allowEnumStackDuplicates;

    ///////////////
    //Constructor//
    ///////////////

    /**
     * Overloaded to allow for single stack management
     *
     * @param enumTagsAndTypes Map of the tag (int tag) for the enum list as well as the full enum list
     * @param firstItemInStack Map of the tag (int tag) for the enum and the initial enum value to set
     */
    public StackManager(@NonNull List<E> enumTagsAndTypes,
                        @NonNull E firstItemInStack) throws StackManagerException {
        this.maintainMinimumOneItemInStack = false;
        this.allowEnumStackDuplicates = false;
        Map<Integer, List<E>> toSet = new HashMap<>();
        toSet.put(0, enumTagsAndTypes);
        Map<Integer, E> toSet2 = new HashMap<>();
        toSet2.put(0, firstItemInStack);
        init(toSet, toSet2);
    }

    /**
     * Overloaded to allow for single stack management
     *
     * @param enumTagsAndTypes              Map of the tag (int tag) for the enum list as well as the full enum list
     * @param firstItemInStack              Map of the tag (int tag) for the enum and the initial enum value to set
     * @param maintainMinimumOneItemInStack boolean, if true, will maintain >=1 per stack (Defaults to false)
     */
    public StackManager(@NonNull List<E> enumTagsAndTypes,
                        @NonNull E firstItemInStack,
                        boolean maintainMinimumOneItemInStack) throws StackManagerException {
        this.maintainMinimumOneItemInStack = maintainMinimumOneItemInStack;
        this.allowEnumStackDuplicates = false;
        Map<Integer, List<E>> toSet = new HashMap<>();
        toSet.put(0, enumTagsAndTypes);
        Map<Integer, E> toSet2 = new HashMap<>();
        toSet2.put(0, firstItemInStack);
        init(toSet, toSet2);
    }

    /**
     * Overloaded to allow for single stack management
     *
     * @param enumTagsAndTypes              Map of the tag (int tag) for the enum list as well as the full enum list
     * @param firstItemInStack              Map of the tag (int tag) for the enum and the initial enum value to set
     * @param maintainMinimumOneItemInStack boolean, if true, will maintain >=1 per stack (Defaults to false)
     * @param allowEnumStackDuplicates      boolean, if true, will allow stack duplicates (Defaults to false)
     */
    public StackManager(@NonNull List<E> enumTagsAndTypes,
                        @NonNull E firstItemInStack,
                        boolean maintainMinimumOneItemInStack,
                        boolean allowEnumStackDuplicates) throws StackManagerException {
        this.maintainMinimumOneItemInStack = maintainMinimumOneItemInStack;
        this.allowEnumStackDuplicates = allowEnumStackDuplicates;
        Map<Integer, List<E>> toSet = new HashMap<>();
        toSet.put(0, enumTagsAndTypes);
        Map<Integer, E> toSet2 = new HashMap<>();
        toSet2.put(0, firstItemInStack);
        init(toSet, toSet2);
    }

    /**
     * Constructor for list of enums and their respective int tag as well as the first item in the list
     *
     * @param enumTagsAndTypes Map of the tag (int tag) for the enum list as well as the full enum list
     * @param firstItemInStack Map of the tag (int tag) for the enum and the initial enum value to set
     */
    public StackManager(@NonNull Map<Integer, List<E>> enumTagsAndTypes,
                        @NonNull Map<Integer, E> firstItemInStack) throws StackManagerException {
        this.maintainMinimumOneItemInStack = false;
        this.allowEnumStackDuplicates = false;
        init(enumTagsAndTypes, firstItemInStack);
    }

    /**
     * Constructor for list of enums and their respective int tag as well as the first item in the list
     *
     * @param enumTagsAndTypes              Map of the tag (int tag) for the enum list as well as the full enum list
     * @param firstItemInStack              Map of the tag (int taG) for the enum and the initial enum value to set
     * @param maintainMinimumOneItemInStack boolean, if true, will maintain >=1 per stack (Defaults to false)
     */
    public StackManager(@NonNull Map<Integer, List<E>> enumTagsAndTypes,
                        @NonNull Map<Integer, E> firstItemInStack,
                        boolean maintainMinimumOneItemInStack) throws StackManagerException {
        this.maintainMinimumOneItemInStack = maintainMinimumOneItemInStack;
        this.allowEnumStackDuplicates = false;
        init(enumTagsAndTypes, firstItemInStack);
    }

    /**
     * Constructor for list of enums and their respective int tag as well as the first item in the list
     *
     * @param enumTagsAndTypes              Map of the tag (int tag) for the enum list as well as the full enum list
     * @param firstItemInStack              Map of the tag (int taG) for the enum and the initial enum value to set
     * @param maintainMinimumOneItemInStack boolean, if true, will maintain >=1 per stack (Defaults to false)
     * @param allowEnumStackDuplicates      boolean, if true, will allow stack duplicates (Defaults to false)
     */
    public StackManager(@NonNull Map<Integer, List<E>> enumTagsAndTypes,
                        @NonNull Map<Integer, E> firstItemInStack,
                        boolean maintainMinimumOneItemInStack,
                        boolean allowEnumStackDuplicates) throws StackManagerException {
        this.maintainMinimumOneItemInStack = maintainMinimumOneItemInStack;
        this.allowEnumStackDuplicates = allowEnumStackDuplicates;
        init(enumTagsAndTypes, firstItemInStack);
    }

    /**
     * Enable or disable logging whenever stacks are altered
     *
     * @param enable if true, enable, else, disable
     */
    public void enableLogging(boolean enable) {
        this.logChangesInStacks = enable;
    }

    /**
     * Flip the boolean flag for maintaining minimum one item per stack.
     *
     * @param bool If true, at least 1 item will be kept per stack on popping
     */
    public void setMaintainMinimumOneItemInStack(boolean bool) {
        this.maintainMinimumOneItemInStack = bool;
    }

    /**
     * Flip the boolean flag for allowing enum stack duplicates
     *
     * @param bool If true, stack will allow duplicates, if false, it will push the dupe value
     *             to the top of the stack instead.
     */
    public void setAllowEnumStackDuplicates(boolean bool) {
        this.allowEnumStackDuplicates = bool;
    }

    /**
     * Init method
     */
    private void init(@NonNull Map<Integer, List<E>> enumTagsAndTypes,
                      @NonNull Map<Integer, E> firstItemInStack) throws StackManagerException {
        this.enableLogging(false);
        this.managedStacks = new HashMap<>();
        for (Map.Entry<Integer, List<E>> map : enumTagsAndTypes.entrySet()) {
            if (map == null) {
                continue;
            }
            Integer key = map.getKey();
            List<E> values = map.getValue();
            if (key == null || MiscUtilities.isListNullOrEmpty(values)) {
                continue;
            }
            E firstItem = firstItemInStack.get(key);
            if (firstItem == null) {
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
        if (MiscUtilities.isMapNullOrEmpty(managedStacks)) {
            throw buildException(INSTANTIATION_ERROR, null, null);
        }
    }

    ///////////////////////////////////////////
    //Public methods for managing Stack State//
    ///////////////////////////////////////////

    /**
     * Clear one stack via the tag sent. Follows the boolean rule set via constructor about maintaining
     * 1 left in the stack if the boolean {@link StackManager#maintainMinimumOneItemInStack}
     * is set to true
     *
     * @ {@link StackManagerException}
     */
    public void clearOneStack() {
        clearOneStack(0);
    }

    /**
     * Clear one stack via the tag sent. Follows the boolean rule set via constructor about maintaining
     * 1 left in the stack if the boolean {@link StackManager#maintainMinimumOneItemInStack}
     * is set to true
     *
     * @ {@link StackManagerException}
     */
    public void clearOneStack(int tag) {
        try {
            CustomStackManagerPOJO pojo = getStackPOJO(tag);
            if (pojo != null) {
                Stack<E> myStack = pojo.getManagedStack();
                if (myStack != null) {
                    popTheStack(tag, myStack.size());
                }
            }
        } catch (StackManagerException sme) {
            L.m(sme.toString());
        }
    }

    /**
     * Clear all stacks. Follows the boolean rule set via constructor about maintaining
     * 1 left in the stack if the boolean {@link StackManager#maintainMinimumOneItemInStack}
     * is set to true
     *
     * @ {@link StackManagerException}
     */
    public void clearAllStacks() {
        for (Map.Entry<Integer, CustomStackManagerPOJO> map : managedStacks.entrySet()) {
            Integer key = map.getKey();
            CustomStackManagerPOJO pojo = map.getValue();
            if (key != null && pojo != null) {
                Stack<E> myStack = pojo.getManagedStack();
                if (myStack != null) {
                    popTheStack(key, myStack.size());
                }
            }
        }
    }

    /**
     * Append enums to the stack (overloaded to allow for position 0 to be used)
     *
     * @param enumToAdd Enum to add / append to the stack
     * @return Enum Enum of the one at the top of the stack
     * @ {@link StackManagerException}
     */
    public E appendToTheStack(E enumToAdd) {
        return appendToTheStack(0, enumToAdd);
    }

    /**
     * Append enums to the stack
     *
     * @param tagToMatchToEnums Int tag to match the map in the constructor
     * @param enumToAdd         Enum to add / append to the stack
     * @return Enum Enum of the one at the top of the stack
     * @ {@link StackManagerException}
     */
    public E appendToTheStack(int tagToMatchToEnums, E enumToAdd) {
        try {
            manageNullEnums(enumToAdd);
            CustomStackManagerPOJO pojo = getStackPOJO(tagToMatchToEnums);
            List<E> enums = pojo.getEnumTypes();
            Stack stack = pojo.getManagedStack();
            for (Enum e : enums) {
                if (e == null) {
                    continue;
                }
                if (e == enumToAdd) {
                    if (allowEnumStackDuplicates) {
                        stack.push(enumToAdd);
                    } else {
                        if (!stackContainsEnum(stack, enumToAdd)) {
                            stack.push(enumToAdd);
                        } else {
                            //Remove from pos in list and re-add it to the top
                            stack.remove(enumToAdd);
                            stack.push(enumToAdd);
                        }
                    }
                }
            }
            if (logChangesInStacks) {
                L.m("stack matching tag " + tagToMatchToEnums + " enum: " + stack.toString());
            }
            return ((E) stack.peek());
        } catch (StackManagerException sme) {
            L.m(sme.toString());
            return null;
        }
    }

    /**
     * Append enums to the stack (overloaded to allow for position 0 to be used)
     *
     * @param enumsToAdd List of Enums to add / append to the stack
     * @return Enum of the one at the top of the stack
     * @ {@link StackManagerException}
     */
    public E appendToTheStack(List<E> enumsToAdd) {
        return appendToTheStack(0, enumsToAdd);
    }

    /**
     * Append enums to the stack
     *
     * @param tagToMatchToEnums Int tag to match the map in the constructor
     * @param enumsToAdd        List of Enums to add / append to the stack
     * @return Enum of the one at the top of the stack
     * @ {@link StackManagerException}
     */
    public E appendToTheStack(int tagToMatchToEnums, List<E> enumsToAdd) {
        try {
            for (E e : enumsToAdd) {
                manageNullEnums(e);
            }
            CustomStackManagerPOJO pojo = getStackPOJO(tagToMatchToEnums);
            List<E> enums = pojo.getEnumTypes();
            Stack stack = pojo.getManagedStack();
            for (Enum e : enums) {
                if (e == null) {
                    continue;
                }
                for (E e1 : enumsToAdd) {
                    if (e == e1) {
                        if (allowEnumStackDuplicates) {
                            stack.push(e1);
                        } else {
                            if (!stackContainsEnum(stack, e1)) {
                                stack.push(e1);
                            } else {
                                //Remove from pos in list and re-add it to the top
                                stack.remove(e1);
                                stack.push(e1);
                            }
                        }

                    }
                }
            }
            if (logChangesInStacks) {
                L.m("stack matching tag " + tagToMatchToEnums + " enum: " + stack.toString());
            }
            return ((E) stack.peek());
        } catch (StackManagerException sme) {
            L.m(sme.toString());
            return null;
        }
    }

    /**
     * Pop the stack matching the int enum used. (overloaded to allow for position 0 to be used)
     *
     * @return Returns the enum at the top of the stack. If the stack is empty, returns null
     * @ {@link StackManagerException}
     */
    public E popTheStack() {
        return popTheStack(0);
    }

    /**
     * Pop the stack matching the int enum used.
     *
     * @param tagToMatchToEnums Int tag to match the map in the constructor
     * @return Returns the enum at the top of the stack. If the stack is empty, returns null
     * @ {@link StackManagerException}
     */
    public E popTheStack(int tagToMatchToEnums) {
        try {
            CustomStackManagerPOJO pojo = getStackPOJO(tagToMatchToEnums);
            Stack stack = pojo.getManagedStack();
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
                L.m("stack matching tag " + tagToMatchToEnums + " enum: " + stack.toString());
            }
            if (size > 0) {
                return ((E) stack.peek());
            } else {
                return null;
            }
        } catch (StackManagerException sme) {
            L.m(sme.toString());
            return null;
        }
    }

    /**
     * Pop the stack matching the int enum used.
     *
     * @param tagToMatchToEnums Int tag to match the map in the constructor
     * @param numToPop          Number to pop off the stack
     * @return Returns the enum at the top of the stack. If the stack is empty, returns null
     * @ {@link StackManagerException}
     */
    public E popTheStack(int tagToMatchToEnums, int numToPop) {
        try {
            if (numToPop <= 0) {
                throw buildException(NO_NEGATIVE_NUMBERS_TO_POP, null, tagToMatchToEnums);
            }
            CustomStackManagerPOJO pojo = getStackPOJO(tagToMatchToEnums);
            Stack stack = pojo.getManagedStack();
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
                L.m("stack matching tag " + tagToMatchToEnums + " enum: " + stack.toString());
            }
            if (size > 0) {
                return ((E) stack.peek());
            } else {
                return null;
            }
        } catch (StackManagerException sme) {
            L.m(sme.toString());
            return null;
        }
    }

    /**
     * Gets the Stack<E>. If no stack is found, print exception and return null
     * Overloaded to allow for single stack management to be used
     *
     * @return {@link Stack<E>}
     * @ {@link StackManagerException}
     */
    public Stack<E> getStack() {
        return getStack(0);
    }

    /**
     * Gets the Stack<E>. If no stack is found, print exception and return null
     *
     * @param tag Tag matches one(s) passed in Constructor
     * @return {@link Stack<E>}
     * @ {@link StackManagerException}
     */
    public Stack<E> getStack(int tag) {
        try {
            CustomStackManagerPOJO pojo = getStackPOJO(tag);
            Stack<E> stackToManage = pojo.getManagedStack();
            return stackToManage;
        } catch (StackManagerException sme) {
            L.m(sme.toString());
            return null;
        }
    }

    //////////////////////////////////////
    //Private classes for arg management//
    //////////////////////////////////////

    /**
     * Checker for whether or not the stack already contains the enum passed
     *
     * @param stack       Stack containing current enums
     * @param enumToCheck Enum to check if it is already included in the stack
     * @return boolean, if true, stack already contains the enum object
     */
    private boolean stackContainsEnum(Stack<E> stack, E enumToCheck) {
        try {
            for (E e : stack) {
                if (e == enumToCheck) {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    /**
     * Manage null enums passed. If null is passed, will throw exception
     *
     * @param enumWorkingOn Matches one(s) passed in Constructor
     * @ {@link StackManagerException}
     */
    private void manageNullEnums(E enumWorkingOn) {
        if (enumWorkingOn == null) {
            throw buildException(BAD_ENUM, enumWorkingOn, null);
        }
    }

    /**
     * Gets the POJO. If null is returned, will throw exception
     *
     * @param tag Tag matches one(s) passed in Constructor
     * @return {@link CustomStackManagerPOJO}
     * @ {@link StackManagerException}
     */
    private CustomStackManagerPOJO getStackPOJO(int tag) throws StackManagerException {
        if (tag < 0 || tag >= this.managedStacks.size()) {
            throw buildException(INVALID_KEY, null, tag);
        }
        CustomStackManagerPOJO pojo = managedStacks.get(tag);
        if (pojo == null) {
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

    public int getStackSize(int pos) {
        try {
            Stack<E> stack = getStack(pos);
            return ((stack == null) ? 0 : stack.size());
        } catch (StackManagerException sme) {
            L.m(sme.toString());
            return 0;
        }
    }

    public int getStackSize() {
        return getStackSize(0);
    }

    /**
     * Build the the exception to throw
     *
     * @return {@link StackManagerException}
     */
    private StackManagerException buildException(String desc, E enumPassed, Integer keyPassed) {
        StackManagerException e = new StackManagerException();
        if (StringUtilities.isNullOrEmpty(desc)) {
            desc = "An unknown error has occurred";
        }
        e.setErrorMessage(desc);
        e.setKey(keyPassed);
        String enumString = (enumPassed != null) ? enumPassed.toString() : "Null";
        e.setEnumToString(enumString);
        return e;
    }
}
