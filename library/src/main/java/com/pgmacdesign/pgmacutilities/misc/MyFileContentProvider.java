package com.pgmacdesign.pgmacutilities.misc;

/**
 * Created by pmacdowell on 2017-11-16.
 */

public class MyFileContentProvider {//extends ContentProvider {
//    private Uri contentUri;
//    private String fileName, fileExtension;
//    private static final HashMap<String, String> MIME_TYPES =
//            new HashMap<String, String>();
//
//    static {
//        MIME_TYPES.put(".jpg", "image/jpeg");
//        MIME_TYPES.put(".jpeg", "image/jpeg");
//    }
//
//    public Uri getMyContentUri(){
//        return this.contentUri;
//    }
//
//    public MyFileContentProvider(@NonNull String fileContentProviderString){
//        contentUri = Uri.parse(fileContentProviderString);
//        this.fileExtension = ".jpg";
//        this.fileName = "new_photo";
//    }
//
//    public MyFileContentProvider(@NonNull String fileContentProviderString,
//                                 @NonNull String fileName,
//                                 @NonNull String fileExtension){
//        contentUri = Uri.parse(fileContentProviderString);
//        this.fileExtension = fileExtension;
//        this.fileName = fileName;
//    }
//
//    @Override
//    public boolean onCreate() {
//
//        try {
//            File mFile = new File(getContext().getFilesDir(), (fileName + fileExtension));
//            if(!mFile.exists()) {
//                mFile.createNewFile();
//            }
//            getContext().getContentResolver().notifyChange(contentUri, null);
//            return (true);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//
//    }
//
//    @Override
//    public String getType(Uri uri) {
//        String path = uri.toString();
//
//        for (String extension : MIME_TYPES.keySet()) {
//            if (path.endsWith(extension)) {
//                return (MIME_TYPES.get(extension));
//            }
//        }
//        return (null);
//    }
//
//    @Override
//    public ParcelFileDescriptor openFile(Uri uri, String mode)
//            throws FileNotFoundException {
//
//        File f = new File(getContext().getFilesDir(), "newImage.jpg");
//        if (f.exists()) {
//            return (ParcelFileDescriptor.open(f,
//                    ParcelFileDescriptor.MODE_READ_WRITE));
//        }
//        throw new FileNotFoundException(uri.getPath());
//    }
}
