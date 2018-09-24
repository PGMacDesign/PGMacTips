package com.pgmacdesign.pgmactips;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pgmacdesign.pgmactips.adaptersandlisteners.GenericRecyclerviewAdapter;
import com.pgmacdesign.pgmactips.adaptersandlisteners.OnTaskCompleteListener;
import com.pgmacdesign.pgmactips.biometricutilities.BiometricVerification;
import com.pgmacdesign.pgmactips.biometricutilities.FingerprintException;
import com.pgmacdesign.pgmactips.customui.MultiColorLine;
import com.pgmacdesign.pgmactips.datamodels.SamplePojo;
import com.pgmacdesign.pgmactips.misc.CustomAnnotationsBase;
import com.pgmacdesign.pgmactips.misc.PGMacTipsConstants;
import com.pgmacdesign.pgmactips.misc.TempString;
import com.pgmacdesign.pgmactips.networkclasses.retrofitutilities.RetrofitClient;
import com.pgmacdesign.pgmactips.networkclasses.retrofitutilities.RetrofitParser;
import com.pgmacdesign.pgmactips.networkclasses.retrofitutilities.serviceapiinterfaces.ProfanityCheckerAPICalls;
import com.pgmacdesign.pgmactips.networkclasses.retrofitutilities.serviceapiinterfaces.ProfanityCheckerInterface;
import com.pgmacdesign.pgmactips.stackmanagement.StackManager;
import com.pgmacdesign.pgmactips.stackmanagement.StackManagerException;
import com.pgmacdesign.pgmactips.utilities.CameraMediaUtilities;
import com.pgmacdesign.pgmactips.utilities.ContactUtilities;
import com.pgmacdesign.pgmactips.utilities.DatabaseUtilities;
import com.pgmacdesign.pgmactips.utilities.GsonUtilities;
import com.pgmacdesign.pgmactips.utilities.L;
import com.pgmacdesign.pgmactips.utilities.MalwareUtilities;
import com.pgmacdesign.pgmactips.utilities.MiscUtilities;
import com.pgmacdesign.pgmactips.utilities.NumberUtilities;
import com.pgmacdesign.pgmactips.utilities.PermissionUtilities;
import com.pgmacdesign.pgmactips.utilities.SharedPrefs;
import com.pgmacdesign.pgmactips.utilities.SystemUtilities;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;

/**
 * Test activity for experimenting, please ignore
 * Created by pmacdowell on 8/12/2016.
 */
@CustomAnnotationsBase.RequiresDependency(requiresDependencies = {CustomAnnotationsBase.Dependencies.Retrofit2,
        CustomAnnotationsBase.Dependencies.Retrofit2GSONConverter, CustomAnnotationsBase.Dependencies.GSON,
        CustomAnnotationsBase.Dependencies.OkHttp3LoggingInterceptor, CustomAnnotationsBase.Dependencies.Okio})
class MyTestActivity  extends Activity implements View.OnClickListener {


    private DatabaseUtilities dbUtilities;
    private CameraMediaUtilities cam;
    private Button button;
    private RecyclerView testing_layout_recyclerview;
    private BiometricVerification biometricVerification;
   // private MultipurposeEditText et;
    private static final String CUSTOM_STRING = "-PAT";
    private ContactUtilities contactUtilities;
    private OnTaskCompleteListener contactUtilsListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testing_layout);
        //et = (MultipurposeEditText) this.findViewById(R.id.et);
        //et.setState(MultipurposeEditText.EditTextState.FOCUSED);
        TextView tv1 = new TextView(this);
        tv1.setTextColor(getResources().getColor(R.color.black));
        button = (Button) this.findViewById(R.id.button);
        button.setTag("button");
        button.setTransformationMethod(null);
        button.setOnClickListener(this);
        L.m("setoNClickListener");
	    testing_layout_recyclerview = (RecyclerView) this.findViewById(
	    		R.id.testing_layout_recyclerview);
	    testing_layout_recyclerview.setLayoutManager(new LinearLayoutManager(this));

        //init();
        //init2();
        //init3();
	    //init4();
//        init5();

    }

    private SharedPrefs sp;
    private void init5(){
        try {
            sp = SharedPrefs.getSharedPrefsInstance(this, "pattest1", new TempString("pattest4"));
//            init5Clear();
//            init5Save();
        } catch (GeneralSecurityException ge){
            ge.printStackTrace();
            sp = SharedPrefs.getSharedPrefsInstance(this, "pattest1");
        }
//        sp = SharedPrefs.getSharedPrefsInstance(this, "pattest1");
//        init5Clear();
//        if(true){
//            return;
//        }
        init5Save();
        L.m("saved data using init5");
//        init6Save();
//        init5Get(sp);

//        if(true){
//            return;
//        }

        L.m("\n");
        L.m("Printing out data before changePassword called");
        MiscUtilities.printOutHashMap(sp.getAllPrefs());
        L.m("\n");

        sp.destroySensitiveData();
        sp.save("non_encrypted_stuff", "sure, why not");
        L.m("\n");
        L.m("Printing out data after destroyAllData() and adding 1");
        MiscUtilities.printOutHashMap(sp.getAllPrefs());
        L.m("\n");

        if(true){
            return;
        }

        try {
            sp.disableEncryption();
            sp.save("non_encrypted_stuff", "sure, why not");
            L.m("\n");
            L.m("Printing out data after disabling encryption and adding 1");
            MiscUtilities.printOutHashMap(sp.getAllPrefs());
            L.m("\n");

            sp.reEnableEncryption();

            L.m("\n");
            L.m("Printing out data after calling reEnableEncryption()");
            MiscUtilities.printOutHashMap(sp.getAllPrefs());
            L.m("\n");

        } catch (Exception e){
            e.printStackTrace();
        }

        if(true){
            return;
        }
        try {
            sp.changePassword(new TempString("pattest991"));
        } catch (GeneralSecurityException ge){
            ge.printStackTrace();
        }
        L.m("\n");
        L.m("Printing out data after changePassword called");
        MiscUtilities.printOutHashMap(sp.getAllPrefs());
        L.m("\n");
//        init5Clear();
    }

    private static final String KEY_STR = "STRING_TEST";
    private static final String KEY_BOOL = "BOOL_TEST";
    private static final String KEY_DBL = "DOUBLE_TEST";
    private static final String KEY_LONG = "LONG_TEST";
    private static final String KEY_INT = "INT_TEST";

    private void init5Clear(){
        sp.clearPref(KEY_STR);
        sp.clearPref(KEY_BOOL);
        sp.clearPref(KEY_DBL);
        sp.clearPref(KEY_LONG);
        sp.clearPref(KEY_INT);
    }

    private void init5Save(){
        L.m("save shared prefs encrypted");
        sp.save(KEY_STR, "worked?");
//        sp.save(KEY_BOOL, true);
//        sp.save(KEY_DBL, 123.123123);
//        sp.save(KEY_LONG, 555555555555555L);
//        sp.save(KEY_INT, 123);
    }

    private void init6Save(){
        L.m("save shared prefs encrypted");
        sp.save(KEY_STR, "DIFFERENT_worked?");
        sp.save(KEY_BOOL, true);
        sp.save(KEY_DBL, 321.321321321);
        sp.save(KEY_LONG, 11111111111L);
        sp.save(KEY_INT, 222);
    }

    private void init5Get(){
        L.m("get shared prefs encrypted");
        String str = sp.getString(KEY_STR, "nope");
        boolean bool = sp.getBoolean(KEY_BOOL, false);
        double dbl = sp.getDouble(KEY_DBL, -1.1);
        long lng = sp.getLong(KEY_LONG, -1);
        int intx = sp.getInt(KEY_INT, -1);
        L.m("str == " + str);
        L.m("bool == " + bool);
        L.m("dbl == " + dbl);
        L.m("lng == " + lng);
        L.m("intx == " + intx);
    }

    @SuppressLint("MissingPermission")
    private void init3(){
        if(Build.VERSION.SDK_INT >= 23) {
            this.biometricVerification = new BiometricVerification(
                    new OnTaskCompleteListener() {
                        @SuppressLint("NewApi")
                        @Override
                        public void onTaskComplete(Object result, int customTag) {
                            switch (customTag){
                                case BiometricVerification.TAG_AUTHENTICATION_FAIL:
                                    //Authentication failed / finger does not match
                                    boolean fail = (boolean) result;
                                    break;

                                case BiometricVerification.TAG_AUTHENTICATION_SUCCESS:
                                    //Authentication success / finger matches
                                    boolean success = (boolean) result;
                                    break;

                                case BiometricVerification.TAG_AUTHENTICATION_ERROR:
                                    //Error (IE called stopFingerprintAuth() or onStop() triggered)
                                    String knownAuthenticationError = (String) result;
                                    break;

                                case BiometricVerification.TAG_AUTHENTICATION_HELP:
                                    //Authentication did not work, help string passed
                                    String helpString = (String) result;
                                    break;

                                case BiometricVerification.TAG_GENERIC_ERROR:
                                    //Some error has occurred
                                    String genericError = (String) result;
                                    break;
                            }
                        }
                    }, this, "my_key_name");
            try {
                if(this.biometricVerification.isCriteriaMet()) {
                    this.biometricVerification.startFingerprintAuth();
                }
            } catch (FingerprintException e){
                e.printStackTrace();
            }
        }
    }

    private void init2(){
	    GenericRecyclerviewAdapter adapter = new GenericRecyclerviewAdapter(new GenericRecyclerviewAdapter.MultipurposeRecyclerviewLink() {
		    @Override
		    public void onBindViewTriggered(RecyclerView.ViewHolder holder0, int position) {
		    	L.m("onBindView triggered from the activity side, working?");
			    MyTestHolder holder = (MyTestHolder) holder0;
			    holder.button.setText("AWESOMESAUCE");
		    }
	    }, this, R.layout.testing_layout, MyTestHolder.class);
	    testing_layout_recyclerview.setHasFixedSize(true);
	    testing_layout_recyclerview.setAdapter(adapter);
	    SamplePojo sp1 = new SamplePojo();
	    List<SamplePojo> samplePojos = new ArrayList<>();
	    samplePojos.add(sp1);
	    samplePojos.add(sp1);
	    samplePojos.add(sp1);
	    samplePojos.add(sp1);
	    adapter.setListObjects(samplePojos);
    }

	public static class MyTestHolder extends RecyclerView.ViewHolder {
    	private Button button;
    	private MultiColorLine multi_color_line;
		public MyTestHolder(View itemView) {
			super(itemView);
			multi_color_line = (MultiColorLine) itemView.findViewById(R.id.multi_color_line);
			button = (Button) itemView.findViewById(R.id.button);
		}
	}

    private <E extends Enum<E>> void  init(){

        //contactQuery();
        //temp();
        //temp2();



        //Custom stuff here
        dbUtilities = new DatabaseUtilities(this);

        //writeDBStuff();
        //moveDBFile();
        //queryDB();
        //deleteStuff();
        //deleteCustom();
        //deleteAll();
        //superDeleteEverything();

	    List<Enum> testEnum1s = new ArrayList<>();
	    testEnum1s.add(TestEnum1.ONE);
	    testEnum1s.add(TestEnum1.TWO);
	    testEnum1s.add(TestEnum1.THREE);
	    testEnum1s.add(TestEnum1.FOUR);
	    testEnum1s.add(TestEnum1.FIVE);
	    List<Enum> testEnum2s = new ArrayList<>();
	    testEnum2s.add(TestEnum2.A);
	    testEnum2s.add(TestEnum2.B);
	    testEnum2s.add(TestEnum2.C);
	    testEnum2s.add(TestEnum2.D);
	    List<Enum> testEnum3s = new ArrayList<>();
	    testEnum3s.add(TestEnum3.Pat);
	    testEnum3s.add(TestEnum3.Mac);

	    Map<Integer, List<Enum>> myEnums = new HashMap<>();
	    myEnums.put(1, testEnum1s);
	    myEnums.put(2, testEnum2s);
	    myEnums.put(3, testEnum3s);

	    Map<Integer, Enum> myInitialEnums = new HashMap<>();
	    myInitialEnums.put(1, TestEnum1.ONE);
	    myInitialEnums.put(2, TestEnum2.A);
	    myInitialEnums.put(3, TestEnum3.Pat);
	    try {
		    StackManager s = new StackManager(myEnums, myInitialEnums);
		    s.appendToTheStack(1, TestEnum1.THREE);

	    } catch (StackManagerException e1){
	    	L.m("e1 == " + e1.toString());
	    }
    }

    public static enum TestEnum1 {
        ONE, TWO, THREE, FOUR, FIVE
    }
	public static enum TestEnum2 {
		A, B, C, D
	}
	public static enum TestEnum3 {
		Pat, Mac
	}


	private void moveDBFile(){
        //Check camera permissions
        PermissionUtilities perm = PermissionUtilities.getInstance(this);
        if(perm.startPermissionsRequest(new PermissionUtilities.permissionsEnum[]{
                PermissionUtilities.permissionsEnum.WRITE_EXTERNAL_STORAGE,
                PermissionUtilities.permissionsEnum.READ_EXTERNAL_STORAGE})) {
            dbUtilities.copyDBToDownloadDirectory(MyTestActivity.this, null);
        }
    }

    private void temp(){
        L.m(SystemUtilities.getPackageName());

    }
    private void temp2(){
        L.m("further package testing");
        //4 - test
        ArrayList<PackageInfo> res = new ArrayList<PackageInfo>();
        int counter = 0;
        for(PackageInfo packageInfo : res){
            L.m("position " + counter + " - " + packageInfo.packageName);
            // TODO: 8/18/2016 read through package info in docs sometime. tons of info
        }
        //PackageManager pm = getApplicationContext().getPackageManager();
        //List<PackageInfo> packs = pm.getInstalledPackages(0);
    }
    public String getPackageName(Context context) {
        return context.getPackageName();
    }

    @SuppressLint("MissingPermission")
    private void contactQuery(){
	    this.contactUtilsListener = new OnTaskCompleteListener() {
		    @Override
		    public void onTaskComplete(Object result, int customTag) {
			    switch (customTag){

				    case PGMacTipsConstants.TAG_CONTACT_QUERY_EMAIL:
					    List<ContactUtilities.Contact> allContacts5 = (List<ContactUtilities.Contact>) result;
					    L.m("size of returned list (Email) == " +
							    (MiscUtilities.isListNullOrEmpty(allContacts5) ? 0
									    : allContacts5.size()));
					    break;

				    case PGMacTipsConstants.TAG_CONTACT_QUERY_PHONE:
					    List<ContactUtilities.Contact> allContacts4 = (List<ContactUtilities.Contact>) result;
					    L.m("size of returned list (phone) == " +
							    (MiscUtilities.isListNullOrEmpty(allContacts4) ? 0
									    : allContacts4.size()));
					    break;

				    case PGMacTipsConstants.TAG_CONTACT_QUERY_ADDRESS:
					    List<ContactUtilities.Contact> allContacts3 = (List<ContactUtilities.Contact>) result;
					    L.m("size of returned list (address) == " +
							    (MiscUtilities.isListNullOrEmpty(allContacts3) ? 0
									    : allContacts3.size()));
					    break;

				    case PGMacTipsConstants.TAG_CONTACT_QUERY_NAME:
					    List<ContactUtilities.Contact> allContacts2 = (List<ContactUtilities.Contact>) result;
					    L.m("size of returned list (name) == " +
							    (MiscUtilities.isListNullOrEmpty(allContacts2) ? 0
									    : allContacts2.size()));
					    break;

				    case PGMacTipsConstants.TAG_CONTACT_QUERY_PROGRESS_UPDATE:
					    //L.m("TAG_CONTACT_QUERY_PROGRESS_UPDATE");
					    Float flt = (Float) result;
					    if(flt != null){
					    	L.m("Progress Update: " + NumberUtilities.getFloat(flt) + "%");
					    }
					    break;

				    case PGMacTipsConstants.TAG_CONTACT_QUERY_NO_RESULTS:
					    L.m("TAG_CONTACT_QUERY_NO_RESULTS");
					    break;

				    case PGMacTipsConstants.TAG_CONTACT_QUERY_MISSING_CONTACT_PERMISSION:
					    L.m("TAG_CONTACT_QUERY_MISSING_CONTACT_PERMISSION");
					    break;

				    case PGMacTipsConstants.TAG_CONTACT_QUERY_UNKNOWN_ERROR:
					    L.m("TAG_CONTACT_QUERY_UNKNOWN_ERROR");
					    break;

				    case PGMacTipsConstants.TAG_CONTACT_QUERY_ALL_MERGED_RESULTS:
					    List<ContactUtilities.Contact> allContacts = (List<ContactUtilities.Contact>) result;
					    L.m("size of returned list == " +
							    (MiscUtilities.isListNullOrEmpty(allContacts) ? 0
									    : allContacts.size()));
					    break;

			    }
		    }
	    };
	    this.contactUtilities = new ContactUtilities
			    .Builder(this, contactUtilsListener)
			    .onlyIncludeContactsWithPhotos()
			    .shouldUpdateSearchProgress()
			    .build();

//	    this.contactUtilities.queryContacts(
//	    		new ContactUtilities.SearchTypes[]{ContactUtilities.SearchTypes.ADDRESS,
//					    ContactUtilities.SearchTypes.EMAIL, ContactUtilities.SearchTypes.NAME,
//					    ContactUtilities.SearchTypes.PHONE}, null);

	    this.contactUtilities.getAllContacts();
	    //this.contactUtilities.getAllContacts("Pat");
    }


    private void deleteAll(){
        dbUtilities.deleteAllPersistedObjects(true, false);
    }


    private void superDeleteEverything(){
        dbUtilities.deleteEntireDB(true, false);
    }

    private void testPhoto(){
        cam = new CameraMediaUtilities(this, this, new OnTaskCompleteListener() {
            @Override
            public void onTaskComplete(Object result, int customTag) {
                L.m("custom tag = " + customTag);
            }
        });
        cam.startPhotoProcess(CameraMediaUtilities.SourceType.GALLERY);
    }

    private void testLoadingAnimation(){
        //Removed on 2017-07-05 Due to problems with compiling
        //Dialog progressDialog = PGMacCustomProgressBar.buildCaliforniaSVGDialog(this, true);
        //progressDialog.show();
    }

    private void doWebCall(){


        ProfanityCheckerAPICalls.checkProfanityAsynchronous(this,
                new OnTaskCompleteListener() {
                    @Override
                    public void onTaskComplete(Object result, int customTag) {
                        L.m("web call done");
                        if(customTag == PGMacTipsConstants.TAG_RETROFIT_CALL_SUCCESS_STRING){
                            L.m("result == " + result.toString());
                        } else if(customTag == PGMacTipsConstants.TAG_RETROFIT_CALL_SUCCESS_BOOLEAN){
                            L.m("result == " + ((Boolean)result).toString());
                        }
                    }
                }, "word");
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(CameraMediaUtilities.doesCodeBelongToUtility(requestCode)){
            cam.afterOnActivityResult(requestCode, resultCode, data);
        }
    }

    private void checkMalware(){
        List<String> mylist = MalwareUtilities.checkForMalware(this);
        L.m("Number of infections: " + mylist.size());
        L.Toast(this, "Number of infections: " + mylist.size());
    }
    @SuppressLint("MissingPermission")
    @Override
    public void onClick(View view) {
        if(dbUtilities == null) {
            dbUtilities = new DatabaseUtilities(this);
        }
        SamplePojo samplePojo = new SamplePojo();
        samplePojo.setAge(2);
        samplePojo.setGender("panstuffffff");
        samplePojo.setId(123123);
        samplePojo.setName("name");
        samplePojo.setStrs(Arrays.asList("test1", "test2", "test3", "okiedokie"));
        samplePojo.setFauxEnums(Arrays.asList(SamplePojo.MyFauxTestEnum.One,
                SamplePojo.MyFauxTestEnum.Two, SamplePojo.MyFauxTestEnum.Three));
        boolean bool = dbUtilities.persistObject(SamplePojo.class, samplePojo);
        L.m("save success? - " + bool);

        SamplePojo ss = (SamplePojo) dbUtilities.getPersistedObject(SamplePojo.class);
        if(ss == null){
            L.m("could not retrieve object");
        } else {
            L.m("successfully retrieved object: " + GsonUtilities.convertObjectToJson(ss, SamplePojo.class));
        }

        boolean dePersisted = dbUtilities.dePersistObject(SamplePojo.class);
        L.m("Successfully depersisted the object? == " + dePersisted);
        if(true){
            return;
        }


//        init5Get();
        //doWebCall();
        /*
        TimerUtilities.startTimer(new OnTaskCompleteListener() {
            @Override
            public void onTaskComplete(Object result, int customTag) {
                L.m("result received from timerutilities");
                testLoadingAnimation();
            }
        });
        */
        //doWebCall();
        //showGIFLoader();
        //loadTestCall();

	    if(PermissionUtilities.permissionsRequestShortcutReturn(this,
			    new PermissionUtilities.permissionsEnum[]{
	    		PermissionUtilities.permissionsEnum.READ_CONTACTS})) {
	    	contactQuery();
	    }

    }

    private void makeMultiColorLine() {
        MultiColorLine line = (MultiColorLine) this.findViewById(R.id.multi_color_line);
        line.setAnimateStrokes(true, 1000);
        line.setDrawAsSingleLine(true);
        line.setDrawBoarderWithLine(false);
        line.setDrawDiagonally(false);
        line.setFps(MultiColorLine.FPS.FPS_90);
        line.setWidthOfLineStroke(40);
        line.setWidthOfBoarderStroke(8);
        line.setColorOfBoarderStroke(getResources().getColor(R.color.aqua));
        List<MultiColorLine.CustomStrokeObject> strokes = new ArrayList<>();

        MultiColorLine.CustomStrokeObject l1 = new MultiColorLine.CustomStrokeObject(
                50, 0, getResources().getColor(R.color.red)
        );
        MultiColorLine.CustomStrokeObject l2 = new MultiColorLine.CustomStrokeObject(
                50, 50, getResources().getColor(R.color.blue)
        );
        strokes.add(l1);
        strokes.add(l2);
        line.setLineStrokes(strokes);
    }

    private void showGIFLoader(){
        //ProgressBarUtilities.showGIFProgressDialog(this, R.drawable.got_fighttex_house_stark); //<-- Reference the gif you want here
        //ProgressBarUtilities.showGIFProgressDialog(this, R.drawable.but_why_gif);
    }

    private void loadTestCall(){
        String BASE_URL = "http://www.purgomalum.com/";
        ProfanityCheckerInterface serviceInterface = new RetrofitClient.Builder(
                ProfanityCheckerInterface.class, BASE_URL)
                .setTimeouts(60000,60000)
                .setLogLevel(HttpLoggingInterceptor.Level.NONE)
                .build().buildServiceClient();
        SamplePojo pojo = new SamplePojo();
        Call call = serviceInterface.checkProfanity2(pojo);
        RetrofitParser.parse(new OnTaskCompleteListener() {
            @Override
            public void onTaskComplete(Object result, int customTag) {
                L.m("CALLBACK TAG == " + customTag);
            }
        }, call, RetrofitParser.TYPE_INTEGER, RetrofitParser.TYPE_BOOLEAN, 1, 0, true);
    }
}
