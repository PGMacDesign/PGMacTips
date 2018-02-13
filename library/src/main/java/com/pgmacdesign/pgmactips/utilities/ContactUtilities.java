package com.pgmacdesign.pgmactips.utilities;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pgmacdesign.pgmactips.adaptersandlisteners.OnTaskCompleteListener;
import com.pgmacdesign.pgmactips.misc.PGMacTipsConstants;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * NOTE! This requires the permission:
 * <uses-permission android:name="android.permission.READ_CONTACTS"/>
 * to be declared in the Manifest to work properly
 * This class is for interacting with the contact database in the user's phone. Note, it is NOT
 * for adding new contacts, but only for querying and obtaining results
 * Created by pmacdowell on 8/15/2016.
 */
public class ContactUtilities {

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //----------Variables, enums, projections, and sort-by Strings---------------------------------/
    ////////////////////////////////////////////////////////////////////////////////////////////////


    private static final String SORT_BY_DISPLAY_NAME =
            "upper(" + ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + ") ASC";
    private static final String SORT_BY_EMAIL =
            "upper(" + ContactsContract.CommonDataKinds.Email.ADDRESS + ") ASC";
    private static final String SORT_BY_LAST_NAME =
            "upper(" + ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME + ") ASC";
    private static final String SORT_BY_FIRST_NAME =
            "upper(" + ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME + ") ASC";

    private static final String[] EMAIL_PROJECTION = {
            ContactsContract.CommonDataKinds.Email.DATA,
            ContactsContract.CommonDataKinds.Email.ADDRESS,
            ContactsContract.CommonDataKinds.Email.TYPE,
            ContactsContract.CommonDataKinds.Email.STARRED
    };
    private static final String[] EMAIL_PROJECTION_V2 = {
            ContactsContract.CommonDataKinds.Email.DATA,
            ContactsContract.CommonDataKinds.Email.ADDRESS,
            ContactsContract.CommonDataKinds.Email.TYPE,
            ContactsContract.CommonDataKinds.Email.STARRED,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.STARRED,
            ContactsContract.Contacts.PHOTO_URI,
            ContactsContract.Contacts._ID
    };
    private static final String[] NAME_PROJECTION = {
            ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
            ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
            ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME,
            ContactsContract.CommonDataKinds.StructuredName.SUFFIX,
            ContactsContract.CommonDataKinds.StructuredName.PREFIX
    };
    private static final String[] NAME_PROJECTION_V2 = {
            ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
            ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
            ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME,
            ContactsContract.CommonDataKinds.StructuredName.SUFFIX,
            ContactsContract.CommonDataKinds.StructuredName.PREFIX,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.STARRED,
            ContactsContract.Contacts.PHOTO_URI,
            ContactsContract.Contacts._ID
    };
    private static final String[] ADDRESS_PROJECTION = {
            ContactsContract.CommonDataKinds.StructuredPostal.POBOX,
            ContactsContract.CommonDataKinds.StructuredPostal.STREET,
            ContactsContract.CommonDataKinds.StructuredPostal.CITY,
            ContactsContract.CommonDataKinds.StructuredPostal.REGION,
            ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE,
            ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY,
            ContactsContract.CommonDataKinds.StructuredPostal.TYPE
    };
    private static final String[] ADDRESS_PROJECTION_V2 = {
            ContactsContract.CommonDataKinds.StructuredPostal.POBOX,
            ContactsContract.CommonDataKinds.StructuredPostal.STREET,
            ContactsContract.CommonDataKinds.StructuredPostal.CITY,
            ContactsContract.CommonDataKinds.StructuredPostal.REGION,
            ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE,
            ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY,
            ContactsContract.CommonDataKinds.StructuredPostal.TYPE,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.STARRED,
            ContactsContract.Contacts.PHOTO_URI,
            ContactsContract.Contacts._ID
    };
    private static final String[] PHONE_PROJECTION = {
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.TYPE
    };
    private static final String[] PHONE_PROJECTION_V2 = {
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.TYPE,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.STARRED,
            ContactsContract.Contacts.PHOTO_URI,
            ContactsContract.Contacts._ID
    };

    /**
     * The type of search options that can be performed.
     * 1) EMAIL - This will search the address book for contacts nested under the email data table.
     *            Any queries sent in with it will search via the contact display name (raw) and the
     *            email address as well. So searching for: "La" will return both "Larry" the name
     *            and "Laura@something.com" the email as both have 'LA'.
     * 2)ADDRESS - This will search the address book for contacts nested under the postal address
     *             data table. Any query passed will search both the full structured postal address
     *             as well as the raw display name.
     * 3)PHONE - This will search the address book for contacts nested under the phone number
     *           data table. Any query passed will search both the phone number (without any
     *           special characters) as the raw display name.
     * 4)NAME - This will search the address book for contacts nested under the Name
     *          data table. Any query passed will search the raw display name.
     */
    public static enum SearchTypes {
        EMAIL, ADDRESS, PHONE, NAME
    }
    /**
     * These filters add options for query results
     * 1)ADD_ALPHABET_HEADERS - Adds alphabetically ordered headers to the top to match the
     *                          First name of the contact (raw display name). So P would be
     *                          above Patrick in the contact list
     * 2)USE_ALL_ALPHABET_LETTERS - If this flag is passed, it will use all letters in the alphabet
     *                              for the headers. IE, it will append A, B, C, D to the contact
     *                              list regardless of matching names. If false, it will instead
     *                              just put letter headers for actual contacts. For example, Bob
     *                              and David will have 'B' and 'D' but no 'C'  header.
     * 3)MOVE_FAVORITES_TO_TOP_OF_LIST - This flag will move favorites within the contact list to
     *                                   the top of the list. The favorites are selected via the
     *                                   contact app and not via this app.
     * 4)REMOVE_BLOCK_LIST_CONTACTS - This flag will remove the contacts that appear on the global
     *                                  block list below. {@link ContactUtilities#BLOCK_LIST_NUMBERS}
     */
    public static enum SearchQueryFlags {
        ADD_ALPHABET_HEADERS, USE_ALL_ALPHABET_LETTERS,
        MOVE_FAVORITES_TO_TOP_OF_LIST, REMOVE_BLOCK_LIST_CONTACTS
    }

    ////////////////////
    //Global Variables//
    ////////////////////

    private OnTaskCompleteListener listener;
    private Activity activity;
    private Context context;
    //private String query;
    //private int maxNumResults;
    private boolean includeAlphabetHeaders, includeAllLetters,
            moveFavoritesToTop, removeBlockListItems, shouldUpdateProgress;

    //Private Constructor
    private ContactUtilities (Context context, Activity activity,
                              OnTaskCompleteListener listener,
                              boolean includeAlphabetHeaders,
                              boolean includeAllLetters,
                              boolean moveFavoritesToTop,
                              boolean removeBlockListItems,
                              boolean shouldUpdateProgress){
        this.context = context;
        this.activity = activity;
        this.listener = listener;
        this.includeAlphabetHeaders = includeAlphabetHeaders;
        this.includeAllLetters = includeAllLetters;
        this.removeBlockListItems = removeBlockListItems;
        this.moveFavoritesToTop = moveFavoritesToTop;
        this.shouldUpdateProgress = shouldUpdateProgress;
    }

    ////////////////
    //Main Builder//
    ////////////////


    public static final class Builder {
        private List<SearchQueryFlags> searchQueryFlags;
        private Context context;
        private Activity activity;
        private OnTaskCompleteListener listener;
        private boolean shouldUpdateProgress;

        /**
         * Builder for the Contact Utilities class
         * @param context Context
         * @param listener {@link OnTaskCompleteListener}
         *                 The listener to pass data back on. Note! Data sent back will be a
         *                 list of Contact Objects. See {@link Contact}. The data is sent back
         *                 along the Listener with a tag to match the returned results:
         *                 -Email: {@link PGMacTipsConstants#TAG_CONTACT_QUERY_EMAIL}
         *                 -Phone: {@link PGMacTipsConstants#TAG_CONTACT_QUERY_PHONE}
         *                 -Name: {@link PGMacTipsConstants#TAG_CONTACT_QUERY_NAME}
         *                 -Address: {@link PGMacTipsConstants#TAG_CONTACT_QUERY_ADDRESS}
         *                 -Progress Update: {@link PGMacTipsConstants#TAG_CONTACT_QUERY_PROGRESS_UPDATE}
         *                 -No Results: {@link PGMacTipsConstants#TAG_CONTACT_QUERY_NO_RESULTS}
         *                 -Missing READ_CONTACTS permission: {@link PGMacTipsConstants#TAG_CONTACT_QUERY_MISSING_CONTACT_PERMISSION}
         *                 -Some Unknown Error: {@link PGMacTipsConstants#TAG_CONTACT_QUERY_UNKNOWN_ERROR}
         *                 Bear in mind that the progressUpdate will only have data to send if
         *                    the boolean flag for shouldUpdateProgress is set to true.
         *                  {@link ContactUtilities.Builder#shouldUpdateSearchProgress}
         */
        public Builder(@NonNull Context context, @NonNull OnTaskCompleteListener listener){
            this.context = context;
            this.listener = listener;
            this.activity = null;
            this.searchQueryFlags = new ArrayList<>();
        }

        /**
         * If this is called, the listener will pass back progress updates while querying
         * @return this
         */
        public Builder shouldUpdateSearchProgress(){
            this.shouldUpdateProgress = true;
            return this;
        }

        /**
         * Set the activity. If this is set, a check for Contact permissions is made before
         * running operations. If this is not set, the check falls to the developer
         * @param activity Activity to check contact permissions
         * @return this
         */
        public Builder setActivity(@NonNull Activity activity){
            this.activity = activity;
            return this;
        }

        /**
         * Set a list of search Query Flags
         * @param searchQueryFlags {@link ContactUtilities.SearchQueryFlags}
         * @return
         */
        public Builder setSearchQueryFlags(List<SearchQueryFlags> searchQueryFlags){
            this.searchQueryFlags = searchQueryFlags;
            return this;
        }

        /**
         * Set a list of search Query Flags
         * @param searchQueryFlags {@link ContactUtilities.SearchQueryFlags}
         * @return
         */
        public Builder setSearchQueryFlags(SearchQueryFlags[] searchQueryFlags){
            if(!MiscUtilities.isArrayNullOrEmpty(searchQueryFlags)){
                this.searchQueryFlags = Arrays.asList(searchQueryFlags);
            }
            return this;
        }

        /**
         * Adds alphabetically ordered headers to the top to match the
         *                          First name of the contact (raw display name). So P would be
         *                          above Patrick in the contact list
         * @return
         */
        public Builder addAlphabetHeaders(){
            searchQueryFlags.add(SearchQueryFlags.ADD_ALPHABET_HEADERS);
            return this;
        }

        /**
         * If this flag is passed, it will use all letters in the alphabet
         *                              for the headers. IE, it will append A, B, C, D to the contact
         *                              list regardless of matching names. If false, it will instead
         *                              just put letter headers for actual contacts. For example, Bob
         *                              and David will have 'B' and 'D' but no 'C'  header.
         * @return
         */
        public Builder useAllAlphabetHeaders(){
            searchQueryFlags.add(SearchQueryFlags.USE_ALL_ALPHABET_LETTERS);
            return this;
        }

        /**
         * This flag will move favorites within the contact list to
         *                                   the top of the list. The favorites are selected via the
         *                                   contact app and not via this app.
         * @return
         */
        public Builder moveFavoritesToTop(){
            searchQueryFlags.add(SearchQueryFlags.MOVE_FAVORITES_TO_TOP_OF_LIST);
            return this;
        }

        /**
         * This flag will remove the contacts that appear on the global
         *                                  block list below. {@link ContactUtilities#BLOCK_LIST_NUMBERS}
         * @return
         */
        public Builder removeBlockListContacts(){
            searchQueryFlags.add(SearchQueryFlags.REMOVE_BLOCK_LIST_CONTACTS);
            return this;
        }

        public ContactUtilities build(){

            boolean includeAlphabetHeaders = false, includeAllLetters = false,
                    moveFavoritesToTop = false, removeBlockListItems = false;
            for(SearchQueryFlags flag : this.searchQueryFlags){
                switch (flag){
                    case ADD_ALPHABET_HEADERS:
                        includeAlphabetHeaders  = true;
                        break;
                    case USE_ALL_ALPHABET_LETTERS:
                        includeAllLetters = true;
                        break;
                    case REMOVE_BLOCK_LIST_CONTACTS:
                        removeBlockListItems = true;
                        break;
                    case MOVE_FAVORITES_TO_TOP_OF_LIST:
                        moveFavoritesToTop = true;
                        break;
                }
            }

            return new ContactUtilities(context, activity, listener, includeAlphabetHeaders,
                    includeAllLetters, moveFavoritesToTop, removeBlockListItems,
                    this.shouldUpdateProgress);
        }
    }

    /////////////////////////////////////////////
    //Async Query Methods for Single table pull//
    /////////////////////////////////////////////

    /**
     * Perform a contact query on an asynchronous background thread. Data is passed back
     * on the {@link OnTaskCompleteListener}
     * @param typesToQuery Array of SearchTypes enum objects. These are the types of items
     *                     to actually make a query to. Sending multiple will return multiple
     *                     See {@link SearchTypes}
     * @param maxNumResults The int max number of results per search type.
     *                      If null or zero is passed, it will
     *                      simply have no limit on the max number of results.
     */
    public void getContacts(@Nullable SearchTypes[] typesToQuery, @Nullable Integer maxNumResults){
        queryContacts(typesToQuery, maxNumResults, "");
    }

    /**
     * Perform a contact query on an asynchronous background thread. Data is passed back
     * on the {@link OnTaskCompleteListener}. This is overloaded to allow no query string to be
     * passed in. Treats it as if an empty string was passed.
     * @param typesToQuery Array of SearchTypes enum objects. These are the types of items
     *                     to actually make a query to. Sending multiple will return multiple
     *                     See {@link SearchTypes}
     * @param maxNumResults The int max number of results per search type.
     *                      If null or zero is passed, it will
     *                      simply have no limit on the max number of results.
     */
    public void queryContacts(@Nullable SearchTypes[] typesToQuery, @Nullable Integer maxNumResults){
        queryContacts(typesToQuery, maxNumResults, "");
    }

    /**
     * Perform a contact query on an asynchronous background thread. Data is passed back
     * on the {@link OnTaskCompleteListener}
     * @param typesToQuery Array of SearchTypes enum objects. These are the types of items
     *                     to actually make a query to. Sending multiple will return multiple
     *                     See {@link SearchTypes}. If null is passed, it will query all 4 types.
     * @param maxNumResults The int max number of results per search type.
     *                      If null or zero is passed, it will
     *                      simply have no limit on the max number of results.
     * @param query The query to be included (in String format)
     */
    public void queryContacts(@Nullable SearchTypes[] typesToQuery,
                              @Nullable Integer maxNumResults,
                              @Nullable String query){
        if(MiscUtilities.isArrayNullOrEmpty(typesToQuery)){
            typesToQuery = new SearchTypes[]{SearchTypes.NAME, SearchTypes.PHONE,
                    SearchTypes.EMAIL, SearchTypes.ADDRESS};
        }
        int numResults = (IntegerUtilities.getInt(maxNumResults) <= 0) ? 0
                : (IntegerUtilities.getInt(maxNumResults));

        ContactQueryAsync async = new ContactQueryAsync(this, typesToQuery, numResults, query);
        async.execute();
    }

    public void queryContacts(@Nullable SearchTypes[] typesToQuery,
                              @Nullable Integer maxNumResults,
                              @Nullable Pattern regularExpressionFilter){
        if(MiscUtilities.isArrayNullOrEmpty(typesToQuery)){
            typesToQuery = new SearchTypes[]{SearchTypes.NAME, SearchTypes.PHONE,
                    SearchTypes.EMAIL, SearchTypes.ADDRESS};
        }
        int numResults = (IntegerUtilities.getInt(maxNumResults) <= 0) ? 0
                : (IntegerUtilities.getInt(maxNumResults));

        ContactQueryAsync async = new ContactQueryAsync(this, typesToQuery, numResults,
                regularExpressionFilter);
        async.execute();
    }

    private static class ContactQueryAsync extends AsyncTask<Void, Double, Map<SearchTypes, List<Contact>>> {

        private SearchTypes[] typesToQuery;
        private int maxNumResults;
        private String query;
        private boolean missingPermissions, useRegex;
        private Pattern regexPattern;
        private WeakReference<ContactUtilities> classReference;

        private ContactQueryAsync(@NonNull ContactUtilities referent,
                                  @NonNull SearchTypes[] typesToQuery,
                                  int maxNumResults,
                                  @Nullable String query){
            this.typesToQuery = typesToQuery;
            this.query = query;
            this.maxNumResults = maxNumResults;
            this.missingPermissions = false;
            this.classReference = new WeakReference<ContactUtilities>(referent);
        }

        private ContactQueryAsync(@NonNull ContactUtilities referent,
                                  @NonNull SearchTypes[] typesToQuery,
                                  int maxNumResults,
                                  @Nullable Pattern regularExpressionFilter){
            this.typesToQuery = typesToQuery;
            this.query = query;
            this.maxNumResults = maxNumResults;
            this.missingPermissions = false;
            this.classReference = new WeakReference<ContactUtilities>(referent);
        }

        @Override
        protected void onProgressUpdate(Double... values) {
            if(this.classReference.get().shouldUpdateProgress) {
                if (this.classReference.get().listener != null) {
                    this.classReference.get().listener.onTaskComplete(values,
                            PGMacTipsConstants.TAG_CONTACT_QUERY_PROGRESS_UPDATE);
                }
            }
        }

        @Override
        protected Map<SearchTypes, List<Contact>> doInBackground(Void...args) {

            Map<SearchTypes, List<Contact>> toGenerate = new HashMap<>();

            if(this.classReference.get().activity != null) {
                if (!PermissionUtilities.getContactPermissions(this.classReference.get().activity)) {
                    this.missingPermissions = true;
                    this.classReference.get().listener.onTaskComplete(null,
                            PGMacTipsConstants.TAG_CONTACT_QUERY_MISSING_CONTACT_PERMISSION);
                    return null;
                }
            }

            for(SearchTypes type : this.typesToQuery){
                switch (type){
                    case EMAIL:
                        List<Contact> emailContacts = ContactUtilities.getEmailQuery(
                                this.classReference.get().context, this.query, this.maxNumResults
                        );
                        emailContacts = ContactUtilities.simplifyList(emailContacts);
                        if(this.classReference.get().includeAlphabetHeaders) {
                            emailContacts = ContactUtilities.addAlphabetHeadersToList(
                                    emailContacts, this.classReference.get().includeAllLetters);
                        }
                        if(this.classReference.get().moveFavoritesToTop) {
                            emailContacts = ContactUtilities.moveFavoritesToTop(emailContacts);
                        }
                        toGenerate.put(SearchTypes.EMAIL, emailContacts);
                        break;

                    case PHONE:
                        List<Contact> phoneContacts = ContactUtilities.getPhoneQuery(
                                this.classReference.get().context, this.query, this.maxNumResults,
                                this.classReference.get().removeBlockListItems
                        );
                        phoneContacts = ContactUtilities.simplifyList(phoneContacts);
                        if(this.classReference.get().includeAlphabetHeaders) {
                            phoneContacts = ContactUtilities.addAlphabetHeadersToList(
                                    phoneContacts, this.classReference.get().includeAllLetters);
                        }
                        if(this.classReference.get().moveFavoritesToTop) {
                            phoneContacts = ContactUtilities.moveFavoritesToTop(phoneContacts);
                        }
                        toGenerate.put(SearchTypes.PHONE, phoneContacts);
                        break;

                    case ADDRESS:
                        List<Contact> addressContacts = ContactUtilities.getAddressQuery(
                                this.classReference.get().context, this.query, this.maxNumResults
                        );
                        addressContacts = ContactUtilities.simplifyList(addressContacts);
                        if(this.classReference.get().includeAlphabetHeaders) {
                            addressContacts = ContactUtilities.addAlphabetHeadersToList(
                                    addressContacts, this.classReference.get().includeAllLetters);
                        }
                        if(this.classReference.get().moveFavoritesToTop) {
                            addressContacts = ContactUtilities.moveFavoritesToTop(addressContacts);
                        }
                        toGenerate.put(SearchTypes.ADDRESS, addressContacts);
                        break;

                    case NAME:
                        List<Contact> nameContacts = ContactUtilities.getNameQuery(
                                this.classReference.get().context, this.query, this.maxNumResults
                        );
                        nameContacts = ContactUtilities.simplifyList(nameContacts);
                        if(this.classReference.get().includeAlphabetHeaders) {
                            nameContacts = ContactUtilities.addAlphabetHeadersToList(
                                    nameContacts, this.classReference.get().includeAllLetters);
                        }
                        if(this.classReference.get().moveFavoritesToTop) {
                            nameContacts = ContactUtilities.moveFavoritesToTop(nameContacts);
                        }
                        toGenerate.put(SearchTypes.NAME, nameContacts);
                        break;
                }
            }
            return toGenerate;
        }

        @Override
        protected void onPostExecute(Map<SearchTypes, List<Contact>> contacts) {
            if(this.classReference.get().listener == null){
                return;
            }
            if(this.missingPermissions){
                //Do nothing, result already sent back on listener
                return;
            }
            if(MiscUtilities.isMapNullOrEmpty(contacts)){
                this.classReference.get().listener.onTaskComplete("No Contacts Found",
                        PGMacTipsConstants.TAG_CONTACT_QUERY_NO_RESULTS);
                return;
            }
            for(Map.Entry<SearchTypes, List<Contact>> myMap : contacts.entrySet()){
                SearchTypes typeKey = myMap.getKey();
                List<Contact> contacts1 = myMap.getValue();

                //Skip the loop if null
                if(typeKey == null || contacts1 == null){
                    continue;
                }

                switch (typeKey){
                    case EMAIL:
                        this.classReference.get().listener.onTaskComplete(
                                contacts1, PGMacTipsConstants.TAG_CONTACT_QUERY_EMAIL);
                        break;

                    case PHONE:
                        this.classReference.get().listener.onTaskComplete(
                                contacts1, PGMacTipsConstants.TAG_CONTACT_QUERY_PHONE);
                        break;

                    case ADDRESS:
                        this.classReference.get().listener.onTaskComplete(
                                contacts1, PGMacTipsConstants.TAG_CONTACT_QUERY_ADDRESS);
                        break;

                    case NAME:
                        this.classReference.get().listener.onTaskComplete(
                                contacts1, PGMacTipsConstants.TAG_CONTACT_QUERY_NAME);
                        break;
                }
            }
            super.onPostExecute(contacts);
        }

    }

    ///////////////////////////////////////
    //Query Methods for Single table pull//
    ///////////////////////////////////////

    /**
     * Query the phone table within the contacts database. Used for things like phone number and type
     * @param context Context used to obtain the contentResolver
     * @param query Query to be searched
     * @param maxNumResults max number of results to return. if 0, no limit
     */
    public static List<Contact> getPhoneQuery(Context context, String query,
                                        int maxNumResults, boolean removeBlockListItems) {

        ContentResolver cr = context.getContentResolver();

        List<Contact> contacts = new ArrayList<>();

        if (query != null) {
            query = "%" + query + "%";
        }

        try {
            String phoneWhere = null;
            String[] phoneWhereParams = null;
            if(query != null){
                phoneWhere = ContactsContract.Data.MIMETYPE +
                        " = ? AND " +
                        "(" +
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME +
                        " LIKE ? COLLATE NOCASE " +
                        "OR " +
                        ContactsContract.CommonDataKinds.Phone.NUMBER +
                        " LIKE ? " +
                        ")";
                phoneWhereParams = new String[]{
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE, query, query};
            } else {
                phoneWhere = ContactsContract.Data.MIMETYPE + " = ?";
                phoneWhereParams = new String[]{
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE};
            }

            Cursor pCur = cr.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    PHONE_PROJECTION_V2,
                    phoneWhere,
                    phoneWhereParams,
                    SORT_BY_DISPLAY_NAME);

            if(pCur == null){
                return contacts;
            }

            int counter = 0;
            while (pCur.moveToNext()) {

                if(counter < maxNumResults || maxNumResults == 0){

                    Contact contact = new Contact();

                    String id = getColumnData(pCur, ContactsContract.Contacts._ID);
                    String photoUri = getColumnData(pCur, ContactsContract.Contacts.PHOTO_URI);
                    String displayName = getColumnData(pCur, ContactsContract.Contacts.DISPLAY_NAME);
                    String starred = getColumnData(pCur, ContactsContract.Contacts.STARRED);

                    contact.setId(id);
                    contact.setRawDisplayName(displayName);
                    contact.setPhotoUri(photoUri);
                    if (starred != null) {
                        int starredInt = Integer.parseInt(starred);
                        if (starredInt == 1) {
                            contact.setStarred(true);
                        } else {
                            contact.setStarred(false);
                        }
                    } else {
                        contact.setStarred(false);
                    }

                    String phoneNumberType = getColumnData(pCur,
                            ContactsContract.CommonDataKinds.Phone.TYPE);
                    String phoneNumber = getColumnData(pCur,
                            ContactsContract.CommonDataKinds.Phone.NUMBER);

                    Contact.Phone phone;
                    if(phoneNumberType != null){
                        int x = Integer.parseInt(phoneNumberType);
                        phoneNumberType = ContactsContractSourceCodeStuff.getPhoneType(x);
                        phone = new Contact.Phone(phoneNumber, phoneNumberType);
                    } else {
                        phone = new Contact.Phone(phoneNumber, phoneNumberType);
                    }

                    if(removeBlockListItems) {
                        if (numberOnBlockList(phoneNumber)) {
                            continue;
                        }
                    }

                    List<Contact.Phone> phones = new ArrayList<>();
                    phones.add(phone);
                    contact.setPhone(phones);

                    contacts.add(contact);
                }
            }
            pCur.close();
            //End Phone Number

        } catch (IllegalStateException e) {
            //This will get thrown on contacts without a phone number. No reason to stress over it
            //e.printStackTrace();
        }

        return contacts;
    }

    /**
     * Query the email table within the contacts database. Used for things like email address, email
     * type.
     * @param context Context used to obtain the contentResolver
     * @param query Query to be searched
     * @param maxNumResults max number of results to return. if 0, no limit
     */
    public static List<Contact> getEmailQuery(Context context, String query, int maxNumResults) {

        ContentResolver cr = context.getContentResolver();

        List<Contact> contacts = new ArrayList<>();

        if (query != null) {
            query = "%" + query + "%";
        }

        try {

            String emailWhere = null;
            String[] emailWhereParams = null;
            if(query != null){
                emailWhere = ContactsContract.Data.MIMETYPE + " = ? AND " +
                        ContactsContract.CommonDataKinds.Email.ADDRESS + " LIKE ? COLLATE NOCASE";
                emailWhereParams = new String[]{
                        ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE, query};
            } else {
                emailWhere = ContactsContract.Data.MIMETYPE + " = ?";
                emailWhereParams = new String[]{
                        ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE};
            }

            Cursor emailCur = cr.query(
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    EMAIL_PROJECTION_V2,
                    emailWhere,
                    emailWhereParams,
                    SORT_BY_DISPLAY_NAME);

            if(emailCur == null){
                return contacts;
            }

            int counter = 0;
            while (emailCur.moveToNext()) {
                if(counter < maxNumResults || maxNumResults == 0) {

                    Contact contact = new Contact();

                    String id = getColumnData(emailCur, ContactsContract.Contacts._ID);
                    String photoUri = getColumnData(emailCur, ContactsContract.Contacts.PHOTO_URI);
                    String displayName = getColumnData(emailCur, ContactsContract.Contacts.DISPLAY_NAME);
                    String starred = getColumnData(emailCur, ContactsContract.Contacts.STARRED);

                    contact.setId(id);
                    contact.setRawDisplayName(displayName);
                    contact.setPhotoUri(photoUri);
                    if (starred != null) {
                        int starredInt = Integer.parseInt(starred);
                        if (starredInt == 1) {
                            contact.setStarred(true);
                        } else {
                            contact.setStarred(false);
                        }
                    } else {
                        contact.setStarred(false);
                    }

                    String email = getColumnData(emailCur,
                            ContactsContract.CommonDataKinds.Email.DATA);
                    String emailType = getColumnData(emailCur,
                            ContactsContract.CommonDataKinds.Email.TYPE);

                    Contact.Email myEmail;

                    if (emailType != null) {
                        int x = Integer.parseInt(emailType);
                        emailType = ContactsContractSourceCodeStuff.getEmailType(x);
                        myEmail = new Contact.Email(email, emailType);
                    } else {
                        myEmail = new Contact.Email(email, null);
                    }

                    List<Contact.Email> emails = new ArrayList<>();
                    emails.add(myEmail);
                    contact.setEmail(emails);

                    contacts.add(contact);
                    counter++;
                }
            }
            emailCur.close();
            //End Phone Number

        } catch (IllegalStateException e) {
            //This will get thrown on contacts without a phone number. No reason to stress over it
            //e.printStackTrace();
        }

        return contacts;
    }


    /**
     * Query the address table within the contacts database. Used for things like full address,
     * zip code, city, state, etc
     * @param context Context used to obtain the contentResolver
     * @param query Query to be searched
     * @param maxNumResults max number of results to return. if 0, no limit
     */
    public static List<Contact> getAddressQuery(Context context, String query, int maxNumResults) {

        ContentResolver cr = context.getContentResolver();

        List<Contact> contacts = new ArrayList<>();

        if (query != null) {
            query = "%" + query + "%";
        }

        try {

            String addrWhere = null;
            String[] addrWhereParams = null;
            if(query != null){
                addrWhere = ContactsContract.Data.MIMETYPE + " = ? AND " +
                        ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS
                        + " LIKE ? COLLATE NOCASE";
                addrWhereParams = new String[]{
                        ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE, query};
            } else {
                addrWhere = ContactsContract.Data.MIMETYPE + " = ?";
                addrWhereParams = new String[]{
                        ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE};
            }

            Cursor addrCur = cr.query(ContactsContract.Data.CONTENT_URI,
                    ADDRESS_PROJECTION_V2,
                    addrWhere,
                    addrWhereParams,
                    SORT_BY_DISPLAY_NAME);

            if(addrCur == null){
                return contacts;
            }

            List<Contact.Address> myAddress = new ArrayList<>();

            int counter = 0;
            while (addrCur.moveToNext()) {

                if(counter < maxNumResults || maxNumResults == 0) {

                    Contact contact = new Contact();

                    String id = getColumnData(addrCur, ContactsContract.Contacts._ID);
                    String photoUri = getColumnData(addrCur, ContactsContract.Contacts.PHOTO_URI);
                    String displayName = getColumnData(addrCur, ContactsContract.Contacts.DISPLAY_NAME);
                    String starred = getColumnData(addrCur, ContactsContract.Contacts.STARRED);

                    contact.setId(id);
                    contact.setRawDisplayName(displayName);
                    contact.setPhotoUri(photoUri);
                    if (starred != null) {
                        int starredInt = Integer.parseInt(starred);
                        if (starredInt == 1) {
                            contact.setStarred(true);
                        } else {
                            contact.setStarred(false);
                        }
                    } else {
                        contact.setStarred(false);
                    }

                    String poBox = getColumnData(addrCur,
                            ContactsContract.CommonDataKinds.StructuredPostal.POBOX);
                    String street = getColumnData(addrCur,
                            ContactsContract.CommonDataKinds.StructuredPostal.STREET);
                    String city = getColumnData(addrCur,
                            ContactsContract.CommonDataKinds.StructuredPostal.CITY);
                    String state = getColumnData(addrCur,
                            ContactsContract.CommonDataKinds.StructuredPostal.REGION);
                    String postalCode = getColumnData(addrCur,
                            ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE);
                    String country = getColumnData(addrCur,
                            ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY);
                    String type = getColumnData(addrCur,
                            ContactsContract.CommonDataKinds.StructuredPostal.TYPE);

                    Contact.Address address = new Contact.Address(poBox, street, city, state,
                            postalCode, country, type);

                    myAddress.add(address);

                    contact.setAddresses(myAddress);

                    contacts.add(contact);
                    counter++;
                }
            }
            addrCur.close();
        } catch (IllegalStateException e) {
            //This will get thrown on contacts without a phone number. No reason to stress over it
            //e.printStackTrace();
        }

        return contacts;
    }

    /**
     * Query the name table within the contacts database. Used for things like first name, last
     * name, middle name, suffix, prefix.
     * @param context Context used to obtain the contentResolver
     * @param query Query to be searched
     * @param maxNumResults max number of results to return. if 0, no limit
     */
    public static List<Contact> getNameQuery(Context context, String query, int maxNumResults) {

        ContentResolver cr = context.getContentResolver();

        List<Contact> contacts = new ArrayList<>();

        if (query != null) {
            query = "%" + query + "%";
        }

        try {

            String nameWhere = null;
            String[] nameWhereParams = null;
            if(query != null){
                nameWhere = ContactsContract.Data.MIMETYPE + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " LIKE ? COLLATE NOCASE";
                nameWhereParams = new String[]{
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE, query};
            } else {
                nameWhere = ContactsContract.Data.MIMETYPE + " = ?";
                nameWhereParams = new String[]{
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE};
            }

            Cursor nameCur = cr.query(ContactsContract.Data.CONTENT_URI,
                    NAME_PROJECTION_V2,
                    nameWhere,
                    nameWhereParams,
                    SORT_BY_DISPLAY_NAME);

            if(nameCur == null){
                return contacts;
            }

            int counter = 0;
            while (nameCur.moveToNext()) {

                if(counter < maxNumResults || maxNumResults == 0) {

                    Contact contact = new Contact();

                    String id = getColumnData(nameCur, ContactsContract.Contacts._ID);
                    String photoUri = getColumnData(nameCur, ContactsContract.Contacts.PHOTO_URI);
                    String displayName = getColumnData(nameCur, ContactsContract.Contacts.DISPLAY_NAME);
                    String starred = getColumnData(nameCur, ContactsContract.Contacts.STARRED);

                    contact.setId(id);
                    contact.setRawDisplayName(displayName);
                    contact.setPhotoUri(photoUri);
                    if (starred != null) {
                        int starredInt = Integer.parseInt(starred);
                        if (starredInt == 1) {
                            contact.setStarred(true);
                        } else {
                            contact.setStarred(false);
                        }
                    } else {
                        contact.setStarred(false);
                    }

                    String displayName2 = getColumnData(nameCur,
                            ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME);
                    String suffix = getColumnData(nameCur,
                            ContactsContract.CommonDataKinds.StructuredName.SUFFIX);
                    String prefix = getColumnData(nameCur,
                            ContactsContract.CommonDataKinds.StructuredName.PREFIX);
                    String middleName = getColumnData(nameCur,
                            ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME);
                    String lastName = getColumnData(nameCur,
                            ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME);
                    String firstName = getColumnData(nameCur,
                            ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME);

                    Contact.NameObject nameObject = new Contact.NameObject();
                    nameObject.setFirstName(firstName);
                    nameObject.setLastName(lastName);
                    nameObject.setMiddleName(middleName);
                    nameObject.setPrefix(prefix);
                    nameObject.setSuffix(suffix);
                    nameObject.setDisplayName(displayName2);
                    contact.setNameObject(nameObject);

                    contacts.add(contact);
                    counter++;
                }
            }
            nameCur.close();
        } catch (IllegalStateException e) {
            //This will get thrown on contacts without a phone number. No reason to stress over it
            //e.printStackTrace();
        }

        return contacts;
    }


    /////////////////////////////////////////////////////////////
    //Custom Query utilizing Regex. Mostly just testing for now//
    /////////////////////////////////////////////////////////////

    /**
     * Query the phone table within the contacts database. Used for things like phone number and type
     * @param context Context used to obtain the contentResolver
     * @param regexPattern Regular Expression Pattern to use in the filtering
     * @param maxNumResults max number of results to return. if 0, no limit
     */
    public static List<Contact> getPhoneQueryRegex(Context context, Pattern regexPattern,
                                              int maxNumResults, boolean removeBlockListItems) {

        ContentResolver cr = context.getContentResolver();

        List<Contact> contacts = new ArrayList<>();

        try {
            String phoneWhere = null;
            String[] phoneWhereParams = null;
            phoneWhere = ContactsContract.Data.MIMETYPE + " = ?";
            phoneWhereParams = new String[]{
                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE};

            Cursor pCur = cr.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    PHONE_PROJECTION_V2,
                    phoneWhere,
                    phoneWhereParams,
                    SORT_BY_DISPLAY_NAME);

            if(pCur == null){
                return contacts;
            }

            int counter = 0;
            while (pCur.moveToNext()) {

                if(counter < maxNumResults || maxNumResults == 0){

                    Contact contact = new Contact();

                    String id = getColumnData(pCur, ContactsContract.Contacts._ID);
                    String photoUri = getColumnData(pCur, ContactsContract.Contacts.PHOTO_URI);
                    String displayName = getColumnData(pCur, ContactsContract.Contacts.DISPLAY_NAME);
                    String starred = getColumnData(pCur, ContactsContract.Contacts.STARRED);

                    contact.setId(id);
                    contact.setRawDisplayName(displayName);
                    contact.setPhotoUri(photoUri);
                    if (starred != null) {
                        int starredInt = Integer.parseInt(starred);
                        if (starredInt == 1) {
                            contact.setStarred(true);
                        } else {
                            contact.setStarred(false);
                        }
                    } else {
                        contact.setStarred(false);
                    }

                    String phoneNumberType = getColumnData(pCur,
                            ContactsContract.CommonDataKinds.Phone.TYPE);
                    String phoneNumber = getColumnData(pCur,
                            ContactsContract.CommonDataKinds.Phone.NUMBER);

                    Contact.Phone phone;
                    if(phoneNumberType != null){
                        int x = Integer.parseInt(phoneNumberType);
                        phoneNumberType = ContactsContractSourceCodeStuff.getPhoneType(x);
                        phone = new Contact.Phone(phoneNumber, phoneNumberType);
                    } else {
                        phone = new Contact.Phone(phoneNumber, phoneNumberType);
                    }

                    if(removeBlockListItems) {
                        if (numberOnBlockList(phoneNumber)) {
                            continue;
                        }
                    }

                    List<Contact.Phone> phones = new ArrayList<>();
                    phones.add(phone);
                    contact.setPhone(phones);

                    if(ContactUtilities.matchesCustomQuery(regexPattern, contact)) {
                        contacts.add(contact);
                        counter++;
                    }
                }
            }
            pCur.close();
            //End Phone Number

        } catch (IllegalStateException e) {
            //This will get thrown on contacts without a phone number. No reason to stress over it
            //e.printStackTrace();
        }

        return contacts;
    }

    /**
     * Query the email table within the contacts database. Used for things like email address, email
     * type.
     * @param context Context used to obtain the contentResolver
     * @param regexPattern Regular Expression Pattern to use in the filtering
     * @param maxNumResults max number of results to return. if 0, no limit
     */
    public static List<Contact> getEmailQueryRegex(Context context, Pattern regexPattern, int maxNumResults) {

        ContentResolver cr = context.getContentResolver();

        List<Contact> contacts = new ArrayList<>();
        try {

            String emailWhere = null;
            String[] emailWhereParams = null;
            emailWhere = ContactsContract.Data.MIMETYPE + " = ?";
            emailWhereParams = new String[]{
                    ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE};

            Cursor emailCur = cr.query(
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    EMAIL_PROJECTION_V2,
                    emailWhere,
                    emailWhereParams,
                    SORT_BY_DISPLAY_NAME);

            if(emailCur == null){
                return contacts;
            }

            int counter = 0;
            while (emailCur.moveToNext()) {
                if(counter < maxNumResults || maxNumResults == 0) {

                    Contact contact = new Contact();

                    String id = getColumnData(emailCur, ContactsContract.Contacts._ID);
                    String photoUri = getColumnData(emailCur, ContactsContract.Contacts.PHOTO_URI);
                    String displayName = getColumnData(emailCur, ContactsContract.Contacts.DISPLAY_NAME);
                    String starred = getColumnData(emailCur, ContactsContract.Contacts.STARRED);

                    contact.setId(id);
                    contact.setRawDisplayName(displayName);
                    contact.setPhotoUri(photoUri);
                    if (starred != null) {
                        int starredInt = Integer.parseInt(starred);
                        if (starredInt == 1) {
                            contact.setStarred(true);
                        } else {
                            contact.setStarred(false);
                        }
                    } else {
                        contact.setStarred(false);
                    }

                    String email = getColumnData(emailCur,
                            ContactsContract.CommonDataKinds.Email.DATA);
                    String emailType = getColumnData(emailCur,
                            ContactsContract.CommonDataKinds.Email.TYPE);

                    Contact.Email myEmail;

                    if (emailType != null) {
                        int x = Integer.parseInt(emailType);
                        emailType = ContactsContractSourceCodeStuff.getEmailType(x);
                        myEmail = new Contact.Email(email, emailType);
                    } else {
                        myEmail = new Contact.Email(email, null);
                    }

                    List<Contact.Email> emails = new ArrayList<>();
                    emails.add(myEmail);
                    contact.setEmail(emails);

                    if(ContactUtilities.matchesCustomQuery(regexPattern, contact)) {
                        contacts.add(contact);
                        counter++;
                    }
                }
            }
            emailCur.close();
            //End Phone Number

        } catch (IllegalStateException e) {
            //This will get thrown on contacts without a phone number. No reason to stress over it
            //e.printStackTrace();
        }

        return contacts;
    }


    /**
     * Query the address table within the contacts database. Used for things like full address,
     * zip code, city, state, etc
     * @param context Context used to obtain the contentResolver
     * @param regexPattern Regular Expression Pattern to use in the filtering
     * @param maxNumResults max number of results to return. if 0, no limit
     */
    public static List<Contact> getAddressQueryRegex(Context context, Pattern regexPattern, int maxNumResults) {

        ContentResolver cr = context.getContentResolver();

        List<Contact> contacts = new ArrayList<>();
        try {

            String addrWhere = null;
            String[] addrWhereParams = null;
            addrWhere = ContactsContract.Data.MIMETYPE + " = ?";
            addrWhereParams = new String[]{
                    ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE};

            Cursor addrCur = cr.query(ContactsContract.Data.CONTENT_URI,
                    ADDRESS_PROJECTION_V2,
                    addrWhere,
                    addrWhereParams,
                    SORT_BY_DISPLAY_NAME);

            if(addrCur == null){
                return contacts;
            }

            List<Contact.Address> myAddress = new ArrayList<>();

            int counter = 0;
            while (addrCur.moveToNext()) {

                if(counter < maxNumResults || maxNumResults == 0) {

                    Contact contact = new Contact();

                    String id = getColumnData(addrCur, ContactsContract.Contacts._ID);
                    String photoUri = getColumnData(addrCur, ContactsContract.Contacts.PHOTO_URI);
                    String displayName = getColumnData(addrCur, ContactsContract.Contacts.DISPLAY_NAME);
                    String starred = getColumnData(addrCur, ContactsContract.Contacts.STARRED);

                    contact.setId(id);
                    contact.setRawDisplayName(displayName);
                    contact.setPhotoUri(photoUri);
                    if (starred != null) {
                        int starredInt = Integer.parseInt(starred);
                        if (starredInt == 1) {
                            contact.setStarred(true);
                        } else {
                            contact.setStarred(false);
                        }
                    } else {
                        contact.setStarred(false);
                    }

                    String poBox = getColumnData(addrCur,
                            ContactsContract.CommonDataKinds.StructuredPostal.POBOX);
                    String street = getColumnData(addrCur,
                            ContactsContract.CommonDataKinds.StructuredPostal.STREET);
                    String city = getColumnData(addrCur,
                            ContactsContract.CommonDataKinds.StructuredPostal.CITY);
                    String state = getColumnData(addrCur,
                            ContactsContract.CommonDataKinds.StructuredPostal.REGION);
                    String postalCode = getColumnData(addrCur,
                            ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE);
                    String country = getColumnData(addrCur,
                            ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY);
                    String type = getColumnData(addrCur,
                            ContactsContract.CommonDataKinds.StructuredPostal.TYPE);

                    Contact.Address address = new Contact.Address(poBox, street, city, state,
                            postalCode, country, type);

                    myAddress.add(address);

                    contact.setAddresses(myAddress);

                    if(ContactUtilities.matchesCustomQuery(regexPattern, contact)) {
                        contacts.add(contact);
                        counter++;
                    }
                }
            }
            addrCur.close();
        } catch (IllegalStateException e) {
            //This will get thrown on contacts without a phone number. No reason to stress over it
            //e.printStackTrace();
        }

        return contacts;
    }

    /**
     * Query the name table within the contacts database. Used for things like first name, last
     * name, middle name, suffix, prefix.
     * @param context Context used to obtain the contentResolver
     * @param regexPattern Regular Expression Pattern to use in the filtering
     * @param maxNumResults max number of results to return. if 0, no limit
     */
    public static List<Contact> getNameQueryRegex(Context context, Pattern regexPattern, int maxNumResults) {

        ContentResolver cr = context.getContentResolver();

        List<Contact> contacts = new ArrayList<>();
        try {
            String nameWhere = ContactsContract.Data.MIMETYPE + " = ?";
            String[] nameWhereParams = new String[]{
                    ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE};

            Cursor nameCur = cr.query(ContactsContract.Data.CONTENT_URI,
                    NAME_PROJECTION_V2,
                    nameWhere,
                    nameWhereParams,
                    SORT_BY_DISPLAY_NAME);

            if(nameCur == null){
                return contacts;
            }

            int counter = 0;
            while (nameCur.moveToNext()) {

                if(counter < maxNumResults || maxNumResults == 0) {

                    Contact contact = new Contact();

                    String id = getColumnData(nameCur, ContactsContract.Contacts._ID);
                    String photoUri = getColumnData(nameCur, ContactsContract.Contacts.PHOTO_URI);
                    String displayName = getColumnData(nameCur, ContactsContract.Contacts.DISPLAY_NAME);
                    String starred = getColumnData(nameCur, ContactsContract.Contacts.STARRED);

                    contact.setId(id);
                    contact.setRawDisplayName(displayName);
                    contact.setPhotoUri(photoUri);
                    if (starred != null) {
                        int starredInt = Integer.parseInt(starred);
                        if (starredInt == 1) {
                            contact.setStarred(true);
                        } else {
                            contact.setStarred(false);
                        }
                    } else {
                        contact.setStarred(false);
                    }

                    String displayName2 = getColumnData(nameCur,
                            ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME);
                    String suffix = getColumnData(nameCur,
                            ContactsContract.CommonDataKinds.StructuredName.SUFFIX);
                    String prefix = getColumnData(nameCur,
                            ContactsContract.CommonDataKinds.StructuredName.PREFIX);
                    String middleName = getColumnData(nameCur,
                            ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME);
                    String lastName = getColumnData(nameCur,
                            ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME);
                    String firstName = getColumnData(nameCur,
                            ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME);

                    Contact.NameObject nameObject = new Contact.NameObject();
                    nameObject.setFirstName(firstName);
                    nameObject.setLastName(lastName);
                    nameObject.setMiddleName(middleName);
                    nameObject.setPrefix(prefix);
                    nameObject.setSuffix(suffix);
                    nameObject.setDisplayName(displayName2);
                    contact.setNameObject(nameObject);

                    if(ContactUtilities.matchesCustomQuery(regexPattern, contact)) {
                        contacts.add(contact);
                        counter++;
                    }
                }
            }
            nameCur.close();
        } catch (IllegalStateException e) {
            //This will get thrown on contacts without a phone number. No reason to stress over it
            //e.printStackTrace();
        }

        return contacts;
    }

    /**
     * Makes a check to compare a query and list of strings (chars) against the name / number.
     * @param pattern Regular Expression Pattern to use in the filtering
     * @param contact Contact object to compare against
     * @return Return true if it matches, false if not
     */
    private static boolean matchesCustomQuery(@NonNull Pattern pattern, @NonNull Contact contact){
        if(pattern == null || contact == null){
            return false;
        }
        String jsonString = GsonUtilities.convertObjectToJson(contact, Contact.class);
        if(StringUtilities.isNullOrEmpty(jsonString)){
            return false;
        }
        return (pattern.matcher(jsonString)).matches();
    }

    /////////////////////////////////////////////////////////////////////////////////////////
    //Query HashMap Builder methods (Same as above, but return Map<contactId, ContactObject//
    /////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Query the phone table within the contacts database. Used for things like phone number and type
     * @param listener listener to pass data back on
     * @param query Query to be searched
     */
    private Map<Integer, Contact> getPhoneQueryMap(OnTaskCompleteListener listener,
                                                  String query) {
        if (listener == null) {
            return null;
        }
        if(this.activity != null) {
            if (!PermissionUtilities.getContactPermissions(activity)) {
                return null;
            }
        }

        ContentResolver cr = context.getContentResolver();

        Map<Integer, Contact> contacts = new HashMap<>();

        if (query != null) {
            query = "%" + query + "%";
        }

        try {

            String phoneWhere = null;
            String[] phoneWhereParams = null;
            if(query != null){
                phoneWhere = ContactsContract.Data.MIMETYPE + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.NUMBER + " LIKE ?";
                phoneWhereParams = new String[]{
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE, query};
            } else {
                phoneWhere = ContactsContract.Data.MIMETYPE + " = ?";
                phoneWhereParams = new String[]{
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE};
            }

            Cursor pCur = cr.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    PHONE_PROJECTION_V2,
                    phoneWhere,
                    phoneWhereParams,
                    SORT_BY_DISPLAY_NAME);

            while (pCur.moveToNext()) {

                Contact contact = new Contact();

                String id = getColumnData(pCur, ContactsContract.Contacts._ID);
                String photoUri = getColumnData(pCur, ContactsContract.Contacts.PHOTO_URI);
                String displayName = getColumnData(pCur, ContactsContract.Contacts.DISPLAY_NAME);
                String starred = getColumnData(pCur, ContactsContract.Contacts.STARRED);

                contact.setId(id);
                contact.setRawDisplayName(displayName);
                contact.setPhotoUri(photoUri);
                if (starred != null) {
                    int starredInt = Integer.parseInt(starred);
                    if (starredInt == 1) {
                        contact.setStarred(true);
                    } else {
                        contact.setStarred(false);
                    }
                } else {
                    contact.setStarred(false);
                }

                String phoneNumberType = getColumnData(pCur,
                        ContactsContract.CommonDataKinds.Phone.TYPE);
                String phoneNumber = getColumnData(pCur,
                        ContactsContract.CommonDataKinds.Phone.NUMBER);

                Contact.Phone phone;
                if(phoneNumberType != null){
                    int x = Integer.parseInt(phoneNumberType);
                    phoneNumberType = ContactsContractSourceCodeStuff.getPhoneType(x);
                    phone = new Contact.Phone(phoneNumber, phoneNumberType);
                } else {
                    phone = new Contact.Phone(phoneNumber, phoneNumberType);
                }

                List<Contact.Phone> phones = new ArrayList<>();
                phones.add(phone);
                contact.setPhone(phones);

                contacts.put(Integer.parseInt(id), contact);

            }
            pCur.close();
            //End Phone Number

        } catch (IllegalStateException e) {
            //This will get thrown on contacts without a phone number. No reason to stress over it
            //e.printStackTrace();
        }
        return contacts;
    }

    /**
     * Query the email table within the contacts database. Used for things like email address, email
     * type.
     * @param listener listener to pass data back on
     * @param query Query to be searched
     */
    private Map<Integer, Contact> getEmailQueryMap(OnTaskCompleteListener listener,
                                                  String query) {
        if (listener == null) {
            return null;
        }
        if(this.activity != null) {
            if (!PermissionUtilities.getContactPermissions(activity)) {
                return null;
            }
        }

        ContentResolver cr = context.getContentResolver();

        Map<Integer, Contact> contacts = new HashMap<>();

        if (query != null) {
            query = "%" + query + "%";
        }

        try {

            String emailWhere = null;
            String[] emailWhereParams = null;
            if(query != null){
                emailWhere = ContactsContract.Data.MIMETYPE + " = ? AND " +
                        ContactsContract.CommonDataKinds.Email.ADDRESS + " LIKE ?";
                emailWhereParams = new String[]{
                        ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE, query};
            } else {
                emailWhere = ContactsContract.Data.MIMETYPE + " = ?";
                emailWhereParams = new String[]{
                        ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE};
            }

            Cursor emailCur = cr.query(
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    EMAIL_PROJECTION_V2,
                    emailWhere,
                    emailWhereParams,
                    SORT_BY_DISPLAY_NAME);

            while (emailCur.moveToNext()) {

                Contact contact = new Contact();

                String id = getColumnData(emailCur, ContactsContract.Contacts._ID);
                String photoUri = getColumnData(emailCur, ContactsContract.Contacts.PHOTO_URI);
                String displayName = getColumnData(emailCur, ContactsContract.Contacts.DISPLAY_NAME);
                String starred = getColumnData(emailCur, ContactsContract.Contacts.STARRED);

                contact.setId(id);
                contact.setRawDisplayName(displayName);
                contact.setPhotoUri(photoUri);
                if (starred != null) {
                    int starredInt = Integer.parseInt(starred);
                    if (starredInt == 1) {
                        contact.setStarred(true);
                    } else {
                        contact.setStarred(false);
                    }
                } else {
                    contact.setStarred(false);
                }

                String email = getColumnData(emailCur,
                        ContactsContract.CommonDataKinds.Email.DATA);
                String emailType = getColumnData(emailCur,
                        ContactsContract.CommonDataKinds.Email.TYPE);

                Contact.Email myEmail;

                if(emailType != null){
                    int x = Integer.parseInt(emailType);
                    emailType = ContactsContractSourceCodeStuff.getEmailType(x);
                    myEmail = new Contact.Email(email, emailType);
                } else {
                    myEmail = new Contact.Email(email, null);
                }

                List<Contact.Email> emails = new ArrayList<>();
                emails.add(myEmail);
                contact.setEmail(emails);

                contacts.put(Integer.parseInt(id), contact);

            }
            emailCur.close();
            //End Phone Number

        } catch (IllegalStateException e) {
            //This will get thrown on contacts without a phone number. No reason to stress over it
            //e.printStackTrace();
        }

        return contacts;
    }


    /**
     * Query the address table within the contacts database. Used for things like full address,
     * zip code, city, state, etc
     * @param listener listener to pass data back on
     * @param query Query to be searched
     */
    private Map<Integer, Contact> getAddressQueryMap(OnTaskCompleteListener listener,
                                                    String query) {
        if (listener == null) {
            return null;
        }
        if(this.activity != null) {
            if (!PermissionUtilities.getContactPermissions(activity)) {
                return null;
            }
        }
        ContentResolver cr = context.getContentResolver();

        long currentTime = DateUtilities.getCurrentDateLong();

        Map<Integer, Contact> contacts = new HashMap<>();

        if (query != null) {
            query = "%" + query + "%";
        }

        try {

            String addrWhere = null;
            String[] addrWhereParams = null;
            if(query != null){
                addrWhere = ContactsContract.Data.MIMETYPE + " = ? AND " +
                        ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS
                        + " LIKE ?";
                addrWhereParams = new String[]{
                        ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE, query};
            } else {
                addrWhere = ContactsContract.Data.MIMETYPE + " = ?";
                addrWhereParams = new String[]{
                        ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE};
            }

            Cursor addrCur = cr.query(ContactsContract.Data.CONTENT_URI,
                    ADDRESS_PROJECTION_V2,
                    addrWhere,
                    addrWhereParams,
                    SORT_BY_DISPLAY_NAME);

            List<Contact.Address> myAddress = new ArrayList<>();
            while (addrCur.moveToNext()) {
                Contact contact = new Contact();

                String id = getColumnData(addrCur, ContactsContract.Contacts._ID);
                String photoUri = getColumnData(addrCur, ContactsContract.Contacts.PHOTO_URI);
                String displayName = getColumnData(addrCur, ContactsContract.Contacts.DISPLAY_NAME);
                String starred = getColumnData(addrCur, ContactsContract.Contacts.STARRED);

                contact.setId(id);
                contact.setRawDisplayName(displayName);
                contact.setPhotoUri(photoUri);
                if (starred != null) {
                    int starredInt = Integer.parseInt(starred);
                    if (starredInt == 1) {
                        contact.setStarred(true);
                    } else {
                        contact.setStarred(false);
                    }
                } else {
                    contact.setStarred(false);
                }

                String poBox = getColumnData(addrCur,
                        ContactsContract.CommonDataKinds.StructuredPostal.POBOX);
                String street = getColumnData(addrCur,
                        ContactsContract.CommonDataKinds.StructuredPostal.STREET);
                String city = getColumnData(addrCur,
                        ContactsContract.CommonDataKinds.StructuredPostal.CITY);
                String state = getColumnData(addrCur,
                        ContactsContract.CommonDataKinds.StructuredPostal.REGION);
                String postalCode = getColumnData(addrCur,
                        ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE);
                String country = getColumnData(addrCur,
                        ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY);
                String type = getColumnData(addrCur,
                        ContactsContract.CommonDataKinds.StructuredPostal.TYPE);

                Contact.Address address = new Contact.Address(poBox, street, city, state,
                        postalCode, country, type);

                myAddress.add(address);

                contact.setAddresses(myAddress);

                contacts.put(Integer.parseInt(id), contact);
            }
            addrCur.close();
        } catch (IllegalStateException e) {
            //This will get thrown on contacts without a phone number. No reason to stress over it
            //e.printStackTrace();
        }

        return contacts;
    }

    /**
     * Query the name table within the contacts database. Used for things like first name, last
     * name, middle name, suffix, prefix.
     * @param listener listener to pass data back on
     * @param query Query to be searched
     */
    private Map<Integer, Contact> getNameQueryMap(OnTaskCompleteListener listener,
                                                 String query) {
        if (listener == null) {
            return null;
        }
        if(this.activity != null) {
            if (!PermissionUtilities.getContactPermissions(activity)) {
                return null;
            }
        }

        ContentResolver cr = context.getContentResolver();

        long currentTime = DateUtilities.getCurrentDateLong();

        Map<Integer, Contact> contacts = new HashMap<>();

        if (query != null) {
            query = "%" + query + "%";
        }

        try {

            String nameWhere = null;
            String[] nameWhereParams = null;
            if(query != null){
                nameWhere = ContactsContract.Data.MIMETYPE + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " LIKE ?";
                nameWhereParams = new String[]{
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE, query};
            } else {
                nameWhere = ContactsContract.Data.MIMETYPE + " = ?";
                nameWhereParams = new String[]{
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE};
            }

            Cursor nameCur = cr.query(ContactsContract.Data.CONTENT_URI,
                    NAME_PROJECTION_V2,
                    nameWhere,
                    nameWhereParams,
                    SORT_BY_DISPLAY_NAME);

            while (nameCur.moveToNext()) {
                Contact contact = new Contact();

                String id = getColumnData(nameCur, ContactsContract.Contacts._ID);
                String photoUri = getColumnData(nameCur, ContactsContract.Contacts.PHOTO_URI);
                String displayName = getColumnData(nameCur, ContactsContract.Contacts.DISPLAY_NAME);
                String starred = getColumnData(nameCur, ContactsContract.Contacts.STARRED);

                contact.setId(id);
                contact.setRawDisplayName(displayName);
                contact.setPhotoUri(photoUri);
                if (starred != null) {
                    int starredInt = Integer.parseInt(starred);
                    if (starredInt == 1) {
                        contact.setStarred(true);
                    } else {
                        contact.setStarred(false);
                    }
                } else {
                    contact.setStarred(false);
                }

                String displayName2 = getColumnData(nameCur,
                        ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME);
                String suffix = getColumnData(nameCur,
                        ContactsContract.CommonDataKinds.StructuredName.SUFFIX);
                String prefix = getColumnData(nameCur,
                        ContactsContract.CommonDataKinds.StructuredName.PREFIX);
                String middleName = getColumnData(nameCur,
                        ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME);
                String lastName = getColumnData(nameCur,
                        ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME);
                String firstName = getColumnData(nameCur,
                        ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME);

                Contact.NameObject nameObject = new Contact.NameObject();
                nameObject.setFirstName(firstName);
                nameObject.setLastName(lastName);
                nameObject.setMiddleName(middleName);
                nameObject.setPrefix(prefix);
                nameObject.setSuffix(suffix);
                nameObject.setDisplayName(displayName2);
                contact.setNameObject(nameObject);

                contacts.put(Integer.parseInt(id), contact);
            }
            nameCur.close();
            L.m("total size of names list = " + contacts.size());
        } catch (IllegalStateException e) {
            //This will get thrown on contacts without a phone number. No reason to stress over it
            //e.printStackTrace();
        }

        long endtime = DateUtilities.getCurrentDateLong();
        long diff = endtime - currentTime;
        L.m("DB Query complete, time for query was = " + diff + " milliseconds");

        return contacts;
    }

    /////////////////////////////////////////////
    //Individual methods with passed contact ID//
    /////////////////////////////////////////////

    /**
     * Get the Name Data
     *
     * @param cr
     * @param id
     * @param contactToUpdate
     */
    private static void getNameData(ContentResolver cr, String id, Contact contactToUpdate) {
        //Name
        try {
            String[] projection = {
                    ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
                    ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
                    ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME,
                    ContactsContract.CommonDataKinds.StructuredName.SUFFIX,
                    ContactsContract.CommonDataKinds.StructuredName.PREFIX
            };
            //Name
            String nameWhere = ContactsContract.Data.CONTACT_ID + " = ? AND "
                    + ContactsContract.Data.MIMETYPE + " = ?";
            String[] nameWhereParams = new String[]{id,
                    ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE};
            Cursor nameCur = cr.query(ContactsContract.Data.CONTENT_URI,
                    projection,
                    nameWhere,
                    nameWhereParams,
                    SORT_BY_FIRST_NAME);
            //L.m("name cursor size = " + nameCur.getCount());
            while (nameCur.moveToNext()) {

                String displayName2 = getColumnData(nameCur,
                        ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME);
                String suffix = getColumnData(nameCur,
                        ContactsContract.CommonDataKinds.StructuredName.SUFFIX);
                String prefix = getColumnData(nameCur,
                        ContactsContract.CommonDataKinds.StructuredName.PREFIX);
                String middleName = getColumnData(nameCur,
                        ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME);
                String lastName = getColumnData(nameCur,
                        ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME);
                String firstName = getColumnData(nameCur,
                        ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME);

                Contact.NameObject nameObject = new Contact.NameObject();
                nameObject.setFirstName(firstName);
                nameObject.setLastName(lastName);
                nameObject.setMiddleName(middleName);
                nameObject.setPrefix(prefix);
                nameObject.setSuffix(suffix);
                nameObject.setDisplayName(displayName2);

                contactToUpdate.setNameObject(nameObject);

            }
            nameCur.close();
            //End Name

        } catch (IllegalStateException e) {
            //This will get thrown on contacts without notes. No reason to stress over it
        }

    }

    /**
     * Get the note data
     *
     * @param cr
     * @param id
     * @param contactToUpdate
     */
    private static void getNoteData(ContentResolver cr, String id, Contact contactToUpdate) {
        //Note
        try {
            String[] projection = {
                    ContactsContract.CommonDataKinds.Note.MIMETYPE
            };

            String noteWhere = ContactsContract.Data.CONTACT_ID + " = ? AND "
                    + ContactsContract.Data.MIMETYPE + " = ?";
            String[] noteWhereParams = new String[]{id,
                    ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};
            Cursor noteCur = cr.query(ContactsContract.Data.CONTENT_URI,
                    projection,
                    noteWhere,
                    noteWhereParams,
                    null);
            List<String> myNotes = new ArrayList<>();
            //L.m("note cursor size = " + noteCur.getCount());
            if (noteCur.moveToFirst()) {
                try {
                    String note = noteCur.getString(noteCur.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
                    myNotes.add(note);
                } catch (IllegalStateException e) {
                }
            }
            contactToUpdate.setNotes(myNotes);
            noteCur.close();

        } catch (IllegalStateException e) {
            //This will get thrown on contacts without notes. No reason to stress over it
        }

    }

    /**
     * Get the address data
     *
     * @param cr
     * @param id
     * @param contactToUpdate
     */
    private static void getAddressData(ContentResolver cr, String id, Contact contactToUpdate) {
        //Address
        try {
            String[] projection = {
                    ContactsContract.CommonDataKinds.StructuredPostal.POBOX,
                    ContactsContract.CommonDataKinds.StructuredPostal.STREET,
                    ContactsContract.CommonDataKinds.StructuredPostal.CITY,
                    ContactsContract.CommonDataKinds.StructuredPostal.REGION,
                    ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE,
                    ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY,
                    ContactsContract.CommonDataKinds.StructuredPostal.TYPE
            };
            String addrWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " +
                    ContactsContract.Data.MIMETYPE + " = ?";
            String[] addrWhereParams = new String[]{id,
                    ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE};
            Cursor addrCur = cr.query(ContactsContract.Data.CONTENT_URI,
                    projection,
                    addrWhere,
                    addrWhereParams,
                    null);
            //L.m("address cursor size = " + addrCur.getCount());
            List<Contact.Address> myAddress = new ArrayList<>();
            while (addrCur.moveToNext()) {
                String poBox = getColumnData(addrCur,
                        ContactsContract.CommonDataKinds.StructuredPostal.POBOX);
                String street = getColumnData(addrCur,
                        ContactsContract.CommonDataKinds.StructuredPostal.STREET);
                String city = getColumnData(addrCur,
                        ContactsContract.CommonDataKinds.StructuredPostal.CITY);
                String state = getColumnData(addrCur,
                        ContactsContract.CommonDataKinds.StructuredPostal.REGION);
                String postalCode = getColumnData(addrCur,
                        ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE);
                String country = getColumnData(addrCur,
                        ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY);
                String type = getColumnData(addrCur,
                        ContactsContract.CommonDataKinds.StructuredPostal.TYPE);

                Contact.Address address = new Contact.Address(poBox, street, city, state,
                        postalCode, country, type);
                myAddress.add(address);
            }
            contactToUpdate.setAddresses(myAddress);
            addrCur.close();
            //End Postal Address
        } catch (IllegalStateException e) {
            //This will get thrown on contacts without an address. No reason to stress over it
        }
    }

    /**
     * Get the email data
     *
     * @param cr
     * @param id
     * @param contactToUpdate
     */
    private static void getEmailData(ContentResolver cr, String id, Contact contactToUpdate) {
        //Email
        try {
            String emailWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " +
                    ContactsContract.Data.MIMETYPE + " = ?";
            String[] emailWhereParams = new String[]{id,
                    ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE};
            //L.m("THIS SHOULD ONLY BE 4 COLUMNS AT MOST");
            Cursor emailCur = cr.query(
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    EMAIL_PROJECTION,
                    emailWhere,
                    emailWhereParams,
                    SORT_BY_EMAIL);
            //L.m("email cursor size = " + emailCur.getCount());
            //L.m("COLUMN SIZE = " + emailCur.getCount());
            List<Contact.Email> emails = new ArrayList<>();

            while (emailCur.moveToNext()) {
                // This would allow you get several email addresses
                // if the email addresses were stored in an array
                String email = getColumnData(emailCur,
                        ContactsContract.CommonDataKinds.Email.DATA);
                String emailType = getColumnData(emailCur,
                        ContactsContract.CommonDataKinds.Email.TYPE);

                Contact.Email myEmail;
                if(emailType != null){
                    int x = Integer.parseInt(emailType);
                    emailType = ContactsContractSourceCodeStuff.getEmailType(x);
                    myEmail = new Contact.Email(email, emailType);
                } else {
                    myEmail = new Contact.Email(email, null);
                }
                emails.add(myEmail);
            }
            contactToUpdate.setEmail(emails);
            emailCur.close();
            //End Email
        } catch (IllegalStateException e) {
            //This will get thrown on contacts without an email address. No reason to stress over it
        }
    }

    /**
     * Get the phone data (Numbers)
     *
     * @param cr
     * @param id
     * @param contactToUpdate
     */
    private static void getPhoneData(ContentResolver cr, String id, Contact contactToUpdate) {
        //Phone Number
        try {
            String phoneWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " +
                    ContactsContract.Data.MIMETYPE + " = ?";
            String[] phoneWhereParams = new String[]{id,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE};
            //L.m("THIS SHOULD ONLY BE 2 COLUMNS AT MOST");
            Cursor pCur = cr.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    PHONE_PROJECTION,
                    phoneWhere,
                    phoneWhereParams,
                    null);
            //L.m("COLUMN SIZE = " + pCur.getCount());
            //Need to make a list as some people have multiple numbers (Mobile, home, work)
            List<Contact.Phone> phones = new ArrayList<>();

            while (pCur.moveToNext()) {

                String phoneNumberType = getColumnData(pCur,
                        ContactsContract.CommonDataKinds.Phone.TYPE);
                String phoneNumber = getColumnData(pCur,
                        ContactsContract.CommonDataKinds.Phone.NUMBER);


                Contact.Phone phone;
                if(phoneNumberType != null){
                    int x = Integer.parseInt(phoneNumberType);
                    phoneNumberType = ContactsContractSourceCodeStuff.getPhoneType(x);
                    phone = new Contact.Phone(phoneNumber, phoneNumberType);
                } else {
                    phone = new Contact.Phone(phoneNumber, phoneNumberType);
                }

                phones.add(phone);
            }
            pCur.close();
            contactToUpdate.setPhone(phones);
            //End Phone Number

        } catch (IllegalStateException e) {
            //This will get thrown on contacts without a phone number. No reason to stress over it
            //e.printStackTrace();
        }
    }

    ////////////////////////////////////
    //Utility methods for parsing data//
    ////////////////////////////////////

    /**
     * Gets the column data, returns a String
     *
     * @param cursor
     * @param columnName
     * @return
     */
    private static String getColumnData(Cursor cursor, String columnName) {
        if (cursor == null) {
            return null;
        }
        if (isValidColumn(cursor, columnName)) {
            try {
                String str = cursor.getString(cursor.getColumnIndex(columnName));
                return str;

            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Checks if it is a valid column within the cursor data
     *
     * @param cursor
     * @param columnName
     * @return
     */
    private static boolean isValidColumn(Cursor cursor, String columnName) {
        int x = cursor.getColumnIndex(columnName);
        if (x < 0) {
            //No column matches that, it doesn't exist
            return false;
        } else {
            //A column matches that, it exists
            return true;
        }
    }

    //////////////////////////////////////////////////////////
    //Contact class to serve as POJO + Other Related Methods//
    //////////////////////////////////////////////////////////

    /**
     * Contact entity object
     */
    public static class Contact {
        private String id;
        private NameObject nameObject;
        private List<Phone> phone;
        private List<Email> email;
        private List<String> notes;
        private List<Address> addresses = new ArrayList<Address>();
        private Organization organization;
        private String rawDisplayName;
        private String photoUri;
        private boolean isStarred = false; //Default
        private boolean isHeader = false;
        private boolean isSelectedInList = false; //For implementing in click listeners
        private String headerString;
        //These 3 are to 'simplify' the data structure and allow for less work in an onBindView call
        private String simplifiedPhoneNumber;
        private String simplifiedPhoneNumberType;
        private String simplifiedEmailType;
        private String simplifiedEmail;
        private String simplifiedAddress;

        public String getSimplifiedPhoneNumberType() {
            return simplifiedPhoneNumberType;
        }

        public void setSimplifiedPhoneNumberType(String simplifiedPhoneNumberType) {
            this.simplifiedPhoneNumberType = simplifiedPhoneNumberType;
        }

        public String getSimplifiedEmailType() {
            return simplifiedEmailType;
        }

        public void setSimplifiedEmailType(String simplifiedEmailType) {
            this.simplifiedEmailType = simplifiedEmailType;
        }

        public String getSimplifiedPhoneNumber() {
            return simplifiedPhoneNumber;
        }

        public void setSimplifiedPhoneNumber(String simplifiedPhoneNumber) {
            this.simplifiedPhoneNumber = simplifiedPhoneNumber;
        }

        public String getSimplifiedEmail() {
            return simplifiedEmail;
        }

        public void setSimplifiedEmail(String simplifiedEmail) {
            this.simplifiedEmail = simplifiedEmail;
        }

        public String getSimplifiedAddress() {
            return simplifiedAddress;
        }

        public void setSimplifiedAddress(String simplifiedAddress) {
            this.simplifiedAddress = simplifiedAddress;
        }

        public boolean isSelectedInList() {
            return isSelectedInList;
        }

        public void setSelectedInList(boolean selectedInList) {
            isSelectedInList = selectedInList;
        }

        public boolean isHeader() {
            return isHeader;
        }

        public void setHeader(boolean header) {
            isHeader = header;
        }

        public String getHeaderString() {
            return headerString;
        }

        public void setHeaderString(String headerString) {
            this.headerString = headerString;
        }

        public boolean isStarred() {
            return isStarred;
        }

        public void setStarred(boolean starred) {
            isStarred = starred;
        }

        public String getPhotoUri() {
            return photoUri;
        }

        public void setPhotoUri(String photoUri) {
            this.photoUri = photoUri;
        }

        public String getRawDisplayName() {
            return rawDisplayName;
        }

        public void setRawDisplayName(String rawDisplayName) {
            this.rawDisplayName = rawDisplayName;
        }

        public NameObject getNameObject() {
            return nameObject;
        }

        public void setNameObject(NameObject nameObject) {
            this.nameObject = nameObject;
        }

        public Organization getOrganization() {
            return organization;
        }

        public void setOrganization(Organization organization) {
            this.organization = organization;
        }

        public List<String> getNotes() {
            return notes;
        }

        public void setNotes(List<String> notes) {
            this.notes = notes;
        }

        public void addNote(String note) {
            this.notes.add(note);
        }

        public List<Address> getAddresses() {
            return addresses;
        }

        public void setAddresses(List<Address> addresses) {
            this.addresses = addresses;
        }

        public void addAddress(Address address) {
            this.addresses.add(address);
        }

        public List<Email> getEmail() {
            return email;
        }

        public void setEmail(List<Email> email) {
            this.email = email;
        }

        public void addEmail(Email e) {
            this.email.add(e);
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public List<Phone> getPhone() {
            return phone;
        }

        public void setPhone(List<Phone> phone) {
            this.phone = phone;
        }

        public void addPhone(Phone phone) {
            this.phone.add(phone);
        }

        //Address
        public static class Address {
            private String poBox;
            private String street;
            private String city;
            private String state;
            private String postalCode;
            private String country;
            private String type;
            private String asString = "";

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getPoBox() {
                return poBox;
            }

            public void setPoBox(String poBox) {
                this.poBox = poBox;
            }

            public String getStreet() {
                return street;
            }

            public void setStreet(String street) {
                this.street = street;
            }

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getState() {
                return state;
            }

            public void setState(String state) {
                this.state = state;
            }

            public String getPostalCode() {
                return postalCode;
            }

            public void setPostalCode(String postalCode) {
                this.postalCode = postalCode;
            }

            public String getCountry() {
                return country;
            }

            public void setCountry(String country) {
                this.country = country;
            }

            public String toString() {
                if (this.asString.length() > 0) {
                    return (this.asString);
                } else {
                    String addr = "";
                    if (this.getPoBox() != null) {
                        addr = addr + this.getPoBox() + "n";
                    }
                    if (this.getStreet() != null) {
                        addr = addr + this.getStreet() + "n";
                    }
                    if (this.getCity() != null) {
                        addr = addr + this.getCity() + ", ";
                    }
                    if (this.getState() != null) {
                        addr = addr + this.getState() + " ";
                    }
                    if (this.getPostalCode() != null) {
                        addr = addr + this.getPostalCode() + " ";
                    }
                    if (this.getCountry() != null) {
                        addr = addr + this.getCountry();
                    }
                    return (addr);
                }
            }

            public Address(String asString, String type) {
                this.asString = asString;
                this.type = type;
            }

            public Address(String poBox, String street, String city, String state,
                           String postal, String country, String type) {
                this.setPoBox(poBox);
                this.setStreet(street);
                this.setCity(city);
                this.setState(state);
                this.setPostalCode(postal);
                this.setCountry(country);
                this.setType(type);
            }
        }

        //Email
        public static class Email {
            private String address;
            private String type;

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public String getType() {
                return type;
            }

            public void setType(String t) {
                this.type = t;
            }

            public Email(String emailAddress, String type) {
                this.address = emailAddress;
                this.type = type;
            }
        }

        //Organization
        public static class Organization {
            private String organization = "";
            private String title = "";

            public String getOrganization() {
                return organization;
            }

            public void setOrganization(String organization) {
                this.organization = organization;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public Organization() {

            }

            public Organization(String org, String title) {
                this.organization = org;
                this.title = title;
            }
        }

        //Phone
        public static class Phone {
            private String number;
            private String type;

            public String getNumber() {
                return number;
            }

            public void setNumber(String number) {
                this.number = number;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public Phone(String n, String t) {
                this.number = n;
                this.type = t;
            }

        }

        //Name Info
        public static class NameObject {
            private String firstName;
            private String lastName;
            private String middleName;
            private String prefix;
            private String suffix;
            private String displayName;

            public String getFirstName() {
                return firstName;
            }

            public void setFirstName(String firstName) {
                this.firstName = firstName;
            }

            public String getLastName() {
                return lastName;
            }

            public void setLastName(String lastName) {
                this.lastName = lastName;
            }

            public String getMiddleName() {
                return middleName;
            }

            public void setMiddleName(String middleName) {
                this.middleName = middleName;
            }

            public String getPrefix() {
                return prefix;
            }

            public void setPrefix(String prefix) {
                this.prefix = prefix;
            }

            public String getSuffix() {
                return suffix;
            }

            public void setSuffix(String suffix) {
                this.suffix = suffix;
            }

            public String getDisplayName() {
                return displayName;
            }

            public void setDisplayName(String displayName) {
                this.displayName = displayName;
            }
        }

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    //----------Methods for mutating or accessing lists of contacts--------------------------------/
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Simplify the list so that if the List of contacts is used in a recyclerview, the onBindView
     * will have less overall work to do.
     * NOTE! ONLY CALL THIS IF YOU HAVE CONTACTS WITH ONE ATTRIBUTE IN THE EMAIL, PHONE LISTS. IF
     * THE LISTS CONTAIN MULTIPLE ADDRESSES OR EMAILS, THIS WILL ONLY KEEP THE LAST IN THE LIST
     * @param contacts List of contacts to both iterate and update
     * @return Returns the altered list with new Strings added
     */
    private static List<Contact> simplifyList(List<Contact> contacts){
        if(contacts == null){
            return contacts;
        }
        for(Contact currentContact : contacts){

            //Address
            List<ContactUtilities.Contact.Address> addresses = currentContact.getAddresses();
            if(!MiscUtilities.isListNullOrEmpty(addresses)){
                for(ContactUtilities.Contact.Address add : addresses){
                    String add1 = add.getState();
                    String add2 = add.getCity();
                    String add3 = add.getState();
                    String address = StringUtilities.buildAStringFromUnknowns(
                            new String[]{add1, add2, add3}, ", ");
                    currentContact.setSimplifiedAddress(address);
                }
            }

            //Email
            List<ContactUtilities.Contact.Email> emails = currentContact.getEmail();
            if(!MiscUtilities.isListNullOrEmpty(emails)){

                String combinedEmails = null;
                String combinedEmailTypes = null;
                for(ContactUtilities.Contact.Email email : emails){

                    String strEmail = email.getAddress();
                    String emailType = email.getType();

                    if(strEmail != null){
                        if(!strEmail.equalsIgnoreCase("null")) {
                            combinedEmails = combinedEmails + ", " + strEmail;
                        }
                    }
                    combinedEmailTypes = emailType;
                }
                if(combinedEmails != null) {
                    //Replace the goofy nulls
                    combinedEmails = combinedEmails.replace("null, ", "");

                    currentContact.setSimplifiedEmail(combinedEmails);
                    currentContact.setSimplifiedEmailType(combinedEmailTypes);
                }
            }

            //Phone Number
            List<ContactUtilities.Contact.Phone> phones = currentContact.getPhone();
            if(!MiscUtilities.isListNullOrEmpty(phones)){
                String combinedNumbers = null;
                String combinedNumberTypes = null;
                for(ContactUtilities.Contact.Phone phone : phones){
                    String strPhone = phone.getNumber();
                    String phoneType = phone.getType();

                    if(strPhone != null){
                        strPhone = strPhone.trim();
                        if(!strPhone.equalsIgnoreCase("null")) {
                            combinedNumbers = combinedNumbers + ", " + strPhone;
                        }
                    }
                    combinedNumberTypes = phoneType;
                }

                if(combinedNumbers != null) {
                    //Replace the goofy nulls
                    combinedNumbers = combinedNumbers.replace("null, ", "");

                    currentContact.setSimplifiedPhoneNumber(combinedNumbers);
                    currentContact.setSimplifiedPhoneNumberType(combinedNumberTypes);
                }
            }


        }
        return contacts;
    }

    /**
     * This checks (via the IDs) if a user is already in the list.
     * @param contacts List to loop through
     * @return Already in list it returns true, false if not
     */
    private static boolean isObjectInList(String id, List<Contact> contacts){
        if(StringUtilities.isNullOrEmpty(id) || contacts == null){
            return false;
        }
        if(contacts.size() <= 0){
            return false;
        }
        for(Contact contact : contacts){
            String id1 = contact.getId();
            if(id1 == null){
                continue;
            }
            if(id.equalsIgnoreCase(id1)){
                return true;
            }
        }
        return false;
    }

    /**
     * This checks (via the IDs) if a user is already in the list. Different than above method
     * in that it returns the position as to where the item already is
     * @param contacts List to loop through
     * @return returns the position of the duplicate. If no duplicate, it returns -1
     */
    private static int isObjectInListPos(String id, List<Contact> contacts){
        if(StringUtilities.isNullOrEmpty(id) || contacts == null){
            return -1;
        }
        if(contacts.size() <= 0){
            return -1;
        }
        int pos = 0;
        for(Contact contact : contacts){
            String id1 = contact.getId();
            if(id1 == null){
                pos++;
                continue;
            }
            if(id.equalsIgnoreCase(id1)){
                return pos;
            }
            pos++;
        }
        return -1;
    }

    /**
     * Adds header objects (Alphabet headers) to the list
     * @param contactList List to be altered
     * @param allLetters Boolean, if true, it will set ALL alphabet letters into a list (IE, Bob
     *                   and Patrick in contacts, but it will still return A - Z).
     *                   If false, only the letters that match the header of the next name will
     *                   be included (IE, Bob and Patrick in contacts, but will only return B and P
     *                   instead of the full A-Z list
     * @return Altered list
     */
    private static List<Contact> addAlphabetHeadersToList(List<Contact> contactList, boolean allLetters){
        if(contactList == null){
            return null;
        }
        if(contactList.size() == 0){
            return contactList;
        }

        //List to return
        List<Contact> toReturn = new ArrayList<>();

        //All letters, A-Z, regardless if contact list
        if(allLetters) {

            //Initialize with first letter
            String currentLetter = "A";

            int position = 0;
            //Iterate over list
            for (Contact contact : contactList) {
                if (position == 0) {
                    Contact contactHeader = new Contact();
                    contactHeader.setHeader(true);
                    contactHeader.setHeaderString(currentLetter);
                    toReturn.add(contactHeader);

                    //Add the contact in behind the header
                    //toReturn.add(contact);

                    position++;
                    //continue;
                }
                String firstLetter = contact.getRawDisplayName();
                //In case this one is empty, bail out
                if (StringUtilities.isNullOrEmpty(firstLetter)) {
                    toReturn.add(contact);
                    position++;
                    continue;
                }

                firstLetter = firstLetter.substring(0, 1);
                firstLetter = firstLetter.toUpperCase();

                if (StringUtilities.isNumeric(firstLetter)) {
                    firstLetter = currentLetter;
                }

                try {
                    int result = firstLetter.compareToIgnoreCase(currentLetter);
                    if (result > 0) {
                        //This means that the letter comes after it in the alphabet (IE B > A)
                        //Check if > 1 in case we skip a letter
                        if (result > 1) {
                            //Skipped multiple letters
                            while (result > 0) {

                                String nextLetter = StringUtilities.incrementString(currentLetter);
                                nextLetter = nextLetter.toUpperCase();

                                Contact headerContact = new Contact();
                                headerContact.setHeader(true);
                                headerContact.setHeaderString(nextLetter);
                                toReturn.add(headerContact);

                                currentLetter = nextLetter;

                                result--;
                            }

                            toReturn.add(contact);
                        } else {
                            //No skip, only incremented by one letter
                            String nextLetter = StringUtilities.incrementString(currentLetter);
                            nextLetter = nextLetter.toUpperCase();

                            Contact headerContact = new Contact();
                            headerContact.setHeader(true);
                            headerContact.setHeaderString(nextLetter);
                            toReturn.add(headerContact);

                            toReturn.add(contact);
                            currentLetter = nextLetter;
                        }
                    } else if (result == 0) {
                        //This means that the letter is == to it in the alphabet (IE A == A)
                        toReturn.add(contact);

                    } else if (result < 0) {
                    /*
                    This means that the letter comes before it in the alphabet (IE A < B)
                    This gets hit when the first item in the list of contacts is later in the
                    alphabet than the first letter. IE, first name on list is Bob Bobson
                    */
                    }

                } catch (Exception e) {
                    //In case something tries to get parsed / incremented and breaks
                    toReturn.add(contact);
                }
                position++;
            }

            //Only the letters that are included
        } else {
            //Initialize with first letter
            String currentLetter = "A";

            int position = 0;
            boolean firstLetterPlaced = false;
            //Iterate over list
            for (Contact contact : contactList) {

                if(contact == null){
                    continue;
                }
                String firstLetter = contact.getRawDisplayName();
                //In case this one is empty, bail out
                if (StringUtilities.isNullOrEmpty(firstLetter)) {
                    toReturn.add(contact);
                    position++;
                    continue;
                }

                firstLetter = firstLetter.substring(0, 1);
                firstLetter = firstLetter.toUpperCase();

                if (StringUtilities.isNumeric(firstLetter)) {
                    firstLetter = currentLetter;
                }

                try {
                    int result = firstLetter.compareToIgnoreCase(currentLetter);
                    if (result > 0) {
                        //This means that the letter comes after it in the alphabet (IE B > A)
                        //Check if > 1 in case we skip a letter
                        if (result > 1) {
                            //Skipped multiple letters
                            while (result > 0) {

                                String nextLetter = StringUtilities.incrementString(currentLetter);
                                nextLetter = nextLetter.toUpperCase();
                                currentLetter = nextLetter;
                                result--;
                            }

                            if(firstLetterPlaced){
                                Contact headerContact = new Contact();
                                headerContact.setHeader(true);
                                headerContact.setHeaderString(currentLetter);
                                toReturn.add(headerContact);
                            } else {
                                firstLetter = contact.getRawDisplayName();
                                firstLetter = firstLetter.substring(0, 1);
                                firstLetter = firstLetter.toUpperCase();
                                firstLetter = ContactUtilities.formatFirstLetterForPage(firstLetter);
                                Contact headerContact = new Contact();
                                headerContact.setHeader(true);
                                headerContact.setHeaderString(firstLetter);
                                toReturn.add(headerContact);
                                firstLetterPlaced = true;
                            }

                            toReturn.add(contact);

                            String nextLetter = StringUtilities.incrementString(currentLetter);
                            nextLetter = nextLetter.toUpperCase();
                            currentLetter = nextLetter;

                            //toReturn.add(contact);
                        } else {

                            //No skip, only incremented by one letter
                            String nextLetter = StringUtilities.incrementString(currentLetter);
                            nextLetter = nextLetter.toUpperCase();
                            nextLetter = ContactUtilities.formatFirstLetterForPage(nextLetter);

                            Contact headerContact = new Contact();
                            headerContact.setHeader(true);
                            headerContact.setHeaderString(nextLetter);
                            toReturn.add(headerContact);

                            toReturn.add(contact);
                            currentLetter = nextLetter;

                        }
                    } else if (result == 0) {

                        if(!firstLetterPlaced){
                            firstLetter = contact.getRawDisplayName();
                            firstLetter = firstLetter.substring(0, 1);
                            firstLetter = firstLetter.toUpperCase();
                            firstLetter = ContactUtilities.formatFirstLetterForPage(firstLetter);
                            Contact headerContact = new Contact();
                            headerContact.setHeader(true);
                            headerContact.setHeaderString(firstLetter);
                            toReturn.add(headerContact);
                            firstLetterPlaced = true;
                        }
                        //This means that the letter is == to it in the alphabet (IE A == A)
                        toReturn.add(contact);

                    } else if (result < 0) {
                        toReturn.add(contact);
                    /*
                    This means that the letter comes before it in the alphabet (IE A < B)
                    This gets hit when the first item in the list of contacts is later in the
                    alphabet than the first letter. IE, first name on list is Bob Bobson
                    */
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    //In case something tries to get parsed / incremented and breaks
                    toReturn.add(contact);
                }
                position++;
            }
        }

        return toReturn;
    }


    ///////////////////
    //Utility Methods//
    ///////////////////

    /**
     * Iterates through the list of all contacts and compares to the list of selected contacts
     * to see if they are on the list. If they are, it sets the full listOfContacts individual
     * object to selected=true and then returns it. This is used for when a new search / query
     * is made and you get back a full list of contacts, but don't know which ones are selected
     * so that a recyclerview 'isSelected' won't work.
     * @param listOfContacts The list of contacts that is unknown as to whether or not it is
     *                       selected. This would be the list brought back from query
     * @param selectedContacts This is the persisted list of contacts where the user has already
     *                         selected some and they are persisted.
     * @param typeSearchingThrough This is the type we are searching through. (To obtain the
     *                             correct String info to compare against)
     * @return List<Contacts> {@link ContactUtilities.Contact}
     */
    public static List<ContactUtilities.Contact> checkAndSetSelected(
            List<ContactUtilities.Contact> listOfContacts,
            List<ContactUtilities.Contact> selectedContacts,
            @NonNull SearchTypes typeSearchingThrough){
        if(MiscUtilities.isListNullOrEmpty(listOfContacts) ||
                MiscUtilities.isListNullOrEmpty(selectedContacts)){
            return listOfContacts;
        }

        List<String> listToCheckAgainst = new ArrayList<>();

        if(typeSearchingThrough == SearchTypes.PHONE){
            for(ContactUtilities.Contact contact : selectedContacts){
                if(contact == null){
                    continue;
                }
                String str = contact.getSimplifiedPhoneNumber();
                if(!StringUtilities.isNullOrEmpty(str)){
                    listToCheckAgainst.add(str);
                }
            }
        } else if(typeSearchingThrough == SearchTypes.EMAIL){
            for(ContactUtilities.Contact contact : selectedContacts){
                if(contact == null){
                    continue;
                }
                String str = contact.getSimplifiedEmail();
                if(!StringUtilities.isNullOrEmpty(str)){
                    listToCheckAgainst.add(str);
                }
            }
        } else if(typeSearchingThrough == SearchTypes.NAME){
            for(ContactUtilities.Contact contact : selectedContacts){
                if(contact == null){
                    continue;
                }
                String str = contact.getRawDisplayName();
                if(!StringUtilities.isNullOrEmpty(str)){
                    listToCheckAgainst.add(str);
                }
            }
        } else if(typeSearchingThrough == SearchTypes.ADDRESS){
            for(ContactUtilities.Contact contact : selectedContacts){
                if(contact == null){
                    continue;
                }
                String str = contact.getSimplifiedAddress();
                if(!StringUtilities.isNullOrEmpty(str)){
                    listToCheckAgainst.add(str);
                }
            }
        } else {
            //Problem
            return listOfContacts;
        }
        //Iterate entire list
        for(int i = 0; i < listOfContacts.size(); i++){
            ContactUtilities.Contact currentContact = listOfContacts.get(i);

            if(currentContact == null){
                continue;
            }

            if(typeSearchingThrough == SearchTypes.PHONE){
                String str = currentContact.getSimplifiedPhoneNumber();
                if(!StringUtilities.isNullOrEmpty(str)){
                    if(listToCheckAgainst.contains(str)){
                        currentContact.setSelectedInList(true);
                        listOfContacts.set(i, currentContact);
                    }
                }
            } else if(typeSearchingThrough == SearchTypes.EMAIL){
                String str = currentContact.getSimplifiedEmail();
                if(!StringUtilities.isNullOrEmpty(str)){
                    if(listToCheckAgainst.contains(str)){
                        currentContact.setSelectedInList(true);
                        listOfContacts.set(i, currentContact);
                    }
                }
            } else if(typeSearchingThrough == SearchTypes.NAME){
                String str = currentContact.getRawDisplayName();
                if(!StringUtilities.isNullOrEmpty(str)){
                    if(listToCheckAgainst.contains(str)){
                        currentContact.setSelectedInList(true);
                        listOfContacts.set(i, currentContact);
                    }
                }
            } else if(typeSearchingThrough == SearchTypes.ADDRESS){
                String str = currentContact.getSimplifiedAddress();
                if(!StringUtilities.isNullOrEmpty(str)){
                    if(listToCheckAgainst.contains(str)){
                        currentContact.setSelectedInList(true);
                        listOfContacts.set(i, currentContact);
                    }
                }
            }
        }
        return listOfContacts;
    }

    /**
     * Deselect all contacts in a list
     * @param listOfContacts List to set and return
     * @return Altered list of the one passed in
     */
    public static List<ContactUtilities.Contact> deselectAllContacts(
            List<ContactUtilities.Contact> listOfContacts){
        if(MiscUtilities.isListNullOrEmpty(listOfContacts)){
            return listOfContacts;
        }
        //Iterate entire list
        for(int i = 0; i < listOfContacts.size(); i++){
            ContactUtilities.Contact currentContact = listOfContacts.get(i);
            if(currentContact == null){
                continue;
            }
            currentContact.setSelectedInList(false);
            listOfContacts.set(i, currentContact);
        }
        return listOfContacts;
    }

    /**
     * Select all contacts in a list
     * @param listOfContacts List to set and return
     * @return Altered list of the one passed in
     */
    public static List<ContactUtilities.Contact> selectAllContacts(
            List<ContactUtilities.Contact> listOfContacts){
        if(MiscUtilities.isListNullOrEmpty(listOfContacts)){
            return listOfContacts;
        }
        //Iterate entire list
        for(int i = 0; i < listOfContacts.size(); i++){
            ContactUtilities.Contact currentContact = listOfContacts.get(i);
            if(currentContact == null){
                continue;
            }
            currentContact.setSelectedInList(true);
            listOfContacts.set(i, currentContact);
        }
        return listOfContacts;
    }

    /**
     * Check if a list contains the email already
     * @param contacts Contact list to compare against
     * @param email email to check
     * @return boolean, true if it already contains it, false if it does not.
     */
    private static boolean listAlreadyContainsEmail(List<Contact> contacts, String email){
        if(contacts == null || StringUtilities.isNullOrEmpty(email)){
            return false;
        }

        for(Contact contact : contacts){
            List<Contact.Email> checkEmails = contact.getEmail();
            for(Contact.Email singleEmail : checkEmails){
                String checkEmail = singleEmail.getAddress();
                if(!StringUtilities.isNullOrEmpty(checkEmail)){
                    if(checkEmail.equalsIgnoreCase(email)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Check if a list contains the phone number already
     * @param contacts Contact list to compare against
     * @param phoneNumber Phone number to check
     * @return boolean, true if it already contains it, false if it does not.
     */
    private static boolean listAlreadyContainsPhoneNumber(List<Contact> contacts, String phoneNumber){
        if(contacts == null || StringUtilities.isNullOrEmpty(phoneNumber)){
            return false;
        }

        for(Contact contact : contacts){
            List<Contact.Phone> checkPhones = contact.getPhone();
            for(Contact.Phone singlePhone : checkPhones){
                String checkPhone = singlePhone.getNumber();
                if(!StringUtilities.isNullOrEmpty(checkPhone)){
                    if(checkPhone.equalsIgnoreCase(phoneNumber)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Checks if the number is one of the ones we want to remove from list.
     * This list includes the build in ones into the phone (IE, #data, *611, etc)
     * @param phoneNumber Phone number to check against
     * @return boolean, true if it is on the list, false if it is not
     */
    public static boolean numberOnBlockList(String phoneNumber){
        try {
            return Arrays.asList(BLOCK_LIST_NUMBERS).contains(phoneNumber);
        } catch (Exception e){
            return false;
        }
    }

    /**
     * List of service numbers. These can be blocked for bringing up a list to send SMS to as they
     * will not take in SMS
     */
    public static final String[] BLOCK_LIST_NUMBERS = {
            //Verizon
            "#225", "#3282", "*226", "#646", "#768", "#874", "*86", "18668946848", "8668946848",
            "18776237433", "8776237433", "8009220204", "18009220204", "18664065154", "8664065154",
            "*228", "*22828", "*526", "*611", "611",
            //AT&T
            "*729", "*72427", "*3282#", "*3286#", "*646#", "*876#", "*225#", "*725#", "*639#",
            "*6737#",
            //Sprint
            "1311", "3223", "7726", "8353", "8757", "9000", "9016", "9099", "9230", "9999",
            //T-Mobile
            "#932#", "#674#", "#674#", "#999#", "#225#", "#264#", "#263#", "#266#", "#646#",
            "#932#", "#686#", "#793#", "#796#", "#763#", "#766#", "#326#", "*#06#", "*#9999#",
            "*#0000#", "#8294", "##21#", "##61#", "##62#", "##67#", "*43#", "#43#"
    };


    /**
     * Simple  utility method for the 'first' letter in a list. The idea being that if you
     * have contacts in your phone with something other than a number or a letter to start,
     * it will screw up the first letter up top section. This will make it either a pound
     * sign (#. I refuse to call it hashtag. Dang young whippersnappers) or a letter.
     * @param str String to alter/ check
     * @return Altered string with proper formatting
     */
    private static String formatFirstLetterForPage(String str){
        if(StringUtilities.isNullOrEmpty(str)){
            return str;
        }
        str = str.trim();
        str = str.substring(0, 1);
        if(str.equalsIgnoreCase("0") ||
                str.equalsIgnoreCase("1") ||
                str.equalsIgnoreCase("2") ||
                str.equalsIgnoreCase("3") ||
                str.equalsIgnoreCase("4") ||
                str.equalsIgnoreCase("5") ||
                str.equalsIgnoreCase("6") ||
                str.equalsIgnoreCase("7") ||
                str.equalsIgnoreCase("8") ||
                str.equalsIgnoreCase("9")
                ){
            str = "#";
        }
        if(str.equalsIgnoreCase("+") ||
                str.equalsIgnoreCase("-") ||
                str.equalsIgnoreCase("(") ||
                str.equalsIgnoreCase(")") ||
                str.equalsIgnoreCase("_") ||
                str.equalsIgnoreCase("!") ||
                str.equalsIgnoreCase("@") ||
                str.equalsIgnoreCase("$") ||
                str.equalsIgnoreCase("%") ||
                str.equalsIgnoreCase("^") ||
                str.equalsIgnoreCase("&") ||
                str.equalsIgnoreCase("*") ||
                str.equalsIgnoreCase("[") ||
                str.equalsIgnoreCase("]") ||
                str.equalsIgnoreCase("{") ||
                str.equalsIgnoreCase("}") ||
                str.equalsIgnoreCase(",") ||
                str.equalsIgnoreCase(".")
                ){
            str = "A";
        }
        return str;
    }
    /**
     * Moves contacts with a favorites tag to the top of the list. If none exist, it will just
     * return the existing list.
     * NOTE: This must be run AFTER adding in the alphabetical characters (A, B, C) else this will
     *     not be able to be up top. (Assuming you wanted alphabetical headers)
     * @param myList List of contacts to filter / adjust
     * @return List of Contact objects
     */
    private static List<Contact> moveFavoritesToTop(List<Contact> myList){
        if(myList == null){
            return myList;
        }
        if(myList.size() <= 0){
            return myList;
        }

        List<Integer> pos = new ArrayList<>();

        Integer counter = 0;
        for(Contact contact : myList){
            boolean isFavorite = contact.isStarred();
            //If favorites, add it to the list
            if(isFavorite){
                pos.add(counter);
            }
            counter++;
        }

        if(pos.size() > 0){
            int[] favPositions = new int[pos.size()];
            for (int i = 0; i < pos.size(); i++){
                favPositions[i] = pos.get(i);
            }
            return moveFavoritesToTop(myList, favPositions);
        } else {
            return myList;
        }
    }

    /**
     * Moves contacts with a favorites tag to the top of the list. If none exist, it will just
     * return the existing list.
     * NOTE: This must be run AFTER adding in the alphabetical characters (A, B, C) else this will
     *     not be able to be up top. (Assuming you wanted alphabetical headers)
     * NOTE: This method takes in an array of positions for the favorites. If the list has not
     *     been built yet, call the overloaded method without said array
     * @param myList List of contacts to filter / adjust
     * @param positions positions of favorites within the myList object
     * @return List of Contact objects
     */
    private static List<Contact> moveFavoritesToTop(List<Contact> myList, int[] positions){
        if(myList == null){
            return myList;
        }
        if(myList.size() <= 0){
            return myList;
        }

        //Loop through, move objects from their position in list to the top
        for(int x : positions){
            try {
                Contact contact = myList.get(x);
                myList.remove(x);
                myList.add(0, contact);
            } catch (Exception e){} //If any issues happen while trying to remove items from the list
        }

        //If any were moved, add a favorites tag up top
        if(positions.length > 0){
            Contact contact = new Contact();
            contact.setHeaderString("Favorites");
            contact.setHeader(true);
            myList.add(0, contact);
        }
        return myList;
    }

    ////////////////////////////////////////////////////////////////////
    //Misc Utilities that are pulled/ altered from Android Source Code//
    ////////////////////////////////////////////////////////////////////

    /**
     * This class is copied from the Android Source code (ContactsContract.java) with some minor
     * changes to allow for auto-converting to Strings
     */
    private static class ContactsContractSourceCodeStuff{
        /*
        This section is copied from the Android Source code (ContactsContract.java). Lines 5867 - 5886
        and 5927 - 5950. Not sure why I was unable to access it, so I copied it here so I can compare
        against the phone 'types'
         */
        private static final int TYPE_HOME = 1;
        private static final int TYPE_MOBILE = 2;
        private static final int TYPE_WORK = 3;
        private static final int TYPE_FAX_WORK = 4;
        private static final int TYPE_FAX_HOME = 5;
        /**
         * Return the string resource that best describes the given type
         */
        public static final String getPhoneType(int type) {
            switch (type) {
                case TYPE_HOME: return "Home";
                case TYPE_MOBILE: return "Mobile";
                case TYPE_WORK: return "Work";
                case TYPE_FAX_WORK: return "Fax";
                case TYPE_FAX_HOME: return "Fax";
                default: return "Other";
            }
        }

        /*
        This section is copied from the Android Source code (ContactsContract.java). Lines 6127 - 6130
        and 6142 - 6149. Not sure why I was unable to access it, so I copied it here so I can compare
        against the phone 'types'
         */
        private static final int TYPE_HOME_EMAIL = 1;
        private static final int TYPE_WORK_EMAIL = 2;
        private static final int TYPE_OTHER_EMAIL = 3;
        private static final int TYPE_MOBILE_EMAIL = 4;
        /**
         * Return the string resource that best describes the given type
         */
        public static final String getEmailType(int type) {
            switch (type) {
                case TYPE_HOME_EMAIL: return "Personal";
                case TYPE_WORK_EMAIL: return "Work";
                case TYPE_OTHER_EMAIL: return "Other";
                case TYPE_MOBILE_EMAIL: return "Mobile";
                default: return "Other";
            }
        }
    }

}
