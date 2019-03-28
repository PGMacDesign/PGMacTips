package com.pgmacdesign.pgmactips.utilities;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import android.util.Base64;

import com.pgmacdesign.pgmactips.adaptersandlisteners.OnTaskCompleteListener;
import com.pgmacdesign.pgmactips.misc.PGMacTipsConstants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Date;

import static com.pgmacdesign.pgmactips.utilities.StringUtilities.getDataColumn;

/**
 * Created by pmacdowell on 8/12/2016.
 */
public class FileUtilities {

    public enum ByteSizeNames{
        Bytes, Kilobytes, Megabytes, Gigabytes, Terabytes, PetaBytes, Exabytes, ZettaBytes, Yottabytes
    }

    /**
     * Gets the String version of the type
     * @param whichOne ByteSizeNames enum to be converted to a String
     * @return String version of the enum type
     */
    public static String getByteSizeNamesString(ByteSizeNames whichOne){
        switch (whichOne){
            case Bytes:
                return  "Bytes";
            case Kilobytes:
                return "Kilobytes";
            case Megabytes:
                return "Megabytes";
            case Gigabytes:
                return "Gigabytes";
            case Terabytes:
                return "Terabytes";
            case PetaBytes:
                return "PetaBytes";
            case Exabytes:
                return "Exabytes";
            case ZettaBytes:
                return "ZettaBytes";
            case Yottabytes:
                return "Yottabytes";
            default:
                return "Invalid";
        }
    }

    /**
     * Convert a byte type to a different byte type.
     * IE, making this call:
     * convertSize(4096, ByteSizeNames.Kilobytes, ByteSizeNames.Megabytes)
     * will return 4.0 (megabytes)
     * @param bytesInput Bytes being input for conversion
     * @param inputType the input type in the ByteSizeNames enum
     * @param outputType the output types in the ByteSizeNames enum
     * @return A converted double. IE, send in 1024 kilobytes, get back 1 megabyte
     */
    public static double convertSize(double bytesInput, ByteSizeNames inputType, ByteSizeNames outputType){
        if(bytesInput <= 0){
            return bytesInput;
        }
        double convertedAmount = convertToBytes(bytesInput, inputType);
        convertedAmount = convertToByteType(convertedAmount, outputType);
        return convertedAmount;
    }

    /**
     * Converts bytes into the type you want. So you would pass in bytes and get back Megabytes
     * IE, making this call:
     * convertToByteType(4096, ByteSizeNames.Megabytes)
     * will return 4.0 (megabytes)
     * @param bytesSize Bytes being converted. This is bytes, not kilobytes, megabytes, etc
     * @param whichToConvertTo of type enum BytesizeNames, convert output
     * @return
     */
    public static double convertToByteType(double bytesSize, ByteSizeNames whichToConvertTo){
        if(bytesSize <= 0){
            return bytesSize;
        }
        //Loop through and determine type, then calculate bytes and return
        switch (whichToConvertTo){
            case Bytes:
                return  bytesSize;
            case Kilobytes:
                return (bytesSize / 1024);
            case Megabytes:
                return (bytesSize / 1024 / 1024);
            case Gigabytes:
                return (bytesSize / 1024 / 1024 / 1024);
            case Terabytes:
                return (bytesSize / 1024 / 1024 / 1024 / 1024);
            case PetaBytes:
                return (bytesSize / 1024 / 1024 / 1024 / 1024 / 1024);
            case Exabytes:
                return (bytesSize / 1024 / 1024 / 1024 / 1024 / 1024 / 1024);
            case ZettaBytes:
                return (bytesSize / 1024 / 1024 / 1024 / 1024 / 1024 / 1024 / 1024);
            case Yottabytes:
                return (bytesSize / 1024 / 1024 / 1024 / 1024 / 1024 / 1024 / 1024 / 1024);
            default:
                return bytesSize;
        }
    }

    /**
     * Converts the input double into bytes.
     * IE, making this call:
     * convertToBytes(4, ByteSizeNames.Megabytes)
     * will return 4096 (bytes)
     * @param inputAmount double amount. Could be 10, 544.44, 9999.99999
     * @param inputType The type being input, matches the ByteSizeNames enum
     * @return Returns the double bytes after conversion
     */
    public static double convertToBytes(double inputAmount, ByteSizeNames inputType){
        if(inputAmount <= 0){
            return inputAmount;
        }
        if(inputType == null){
            inputType = ByteSizeNames.Bytes;
        }
        //Loop through and determine type, then calculate bytes and return
        switch (inputType){
            case Bytes:
                return  inputAmount;
            case Kilobytes:
                return (inputAmount * 1024D);
            case Megabytes:
                return (inputAmount * 1024D * 1024D);
            case Gigabytes:
                return (inputAmount * 1024D * 1024D * 1024D);
            case Terabytes:
                return (inputAmount * 1024D * 1024D * 1024D * 1024D);
            case PetaBytes:
                return (inputAmount * 1024D * 1024D * 1024D * 1024D * 1024D);
            case Exabytes:
                return (inputAmount * 1024D * 1024D * 1024D * 1024D * 1024D * 1024D);
            case ZettaBytes:
                return (inputAmount * 1024D * 1024D * 1024D * 1024D * 1024D * 1024D * 1024D);
            case Yottabytes:
                return (inputAmount * 1024D * 1024D * 1024D * 1024D * 1024D * 1024D * 1024D * 1024D);
            default:
                return inputAmount;
        }
    }

    /**
     * Simple calculation to convert bits to bytes
     * @param bits
     * @return
     */
    public static double convertBitsToBytes(double bits){
        return (bits * 8);
    }

    /**
     * Simple calculation to convert bytes to bits
     * @param bytes
     * @return
     */
    public static double convertBytesToBits(double bytes){
        return (bytes / 8);
    }

    /**
     * Writes a simple Text File (.txt)
     * @param path the String path to write. If null, will use the downloads directory
     * @param data The string data to be written
     * @param nameOfFile name of the file String
     * @return String location of file
     */
    public static String writeToFile(String path, String data, String nameOfFile){
        String fileLocation = null;
        File file = null;
        try {

            if(StringUtilities.isNullOrEmpty(nameOfFile)){
                nameOfFile = "PGMacTips_" + DateUtilities.getCurrentDateLong();
            }
            if(StringUtilities.isNullOrEmpty(path)){
                path = StringUtilities.getDataDirectoryLocation();
            }
            try {
                file = new File(path, nameOfFile + ".txt");
            } catch (RuntimeException e){}
            if(file == null){
                try {
                    file = new File(StringUtilities.getDataDirectoryLocation(),
                            nameOfFile + ".txt");
                } catch (RuntimeException e){}
            }
            if(file == null){
                try {
                    file = new File("/Data/", nameOfFile + ".txt");
                } catch (RuntimeException e){}
            }
            if(file == null){
                L.m("An error occurred while trying to write your file. Maybe a permission error?");
                return null;
            }
            if (!file.exists()) {
                file.getParentFile().mkdirs();
            }

            FileOutputStream fos = new FileOutputStream(file);
            byte[] dataStream = data.getBytes(Charset.forName("UTF-8"));
            fos.write(dataStream);
            fos.flush();
            fos.close();

            fileLocation = file.getAbsolutePath();
            try{
                File file2 = new File(fileLocation);
                int length = (int) file2.length();

                byte[] bytes = new byte[length];

                FileInputStream in = new FileInputStream(file2);
                try {
                    in.read(bytes);
                } finally {
                    in.close();
                }
            } catch (Exception ee){
                ee.printStackTrace();
            }
            return fileLocation;
        } catch (Exception e){
            e.printStackTrace();
            return fileLocation;
        }
    }

    /**
     * Converts an inputstream to a byte array (Mostly useful for sending images via JSON)
     * @param is Input stream, if using a URI, open it by calling:
     *     InputStream iStream = context.getContentResolver().openInputStream(uri);
     * @return Byte Array
     */
    public static byte[] getBytes(InputStream is){
        try {
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];

            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            return byteBuffer.toByteArray();
        } catch (Exception e){
            return null;
        }
    }

    /**
     * Convert a file to a Uri
     * @param file
     * @return
     */
    public static Uri convertFileToUri(File file){
        if(file == null){
            return null;
        }
        Uri imgUri = Uri.fromFile(file);
        return imgUri;
    }

    /**
     * See Javadoc below in constructor
     */
    public static class FileGeneratorAsync extends AsyncTask<Void, Void, String> {

        private OnTaskCompleteListener listener;
        private String nameOfFile;
        private String data;
        private String pathToFile;

        /**
         * Generate a file via an asynchronous call. Runs on background thread and passes the
         * data back upon the passed listener
         * @param listener Listener to pass back {@link OnTaskCompleteListener}
         * @param pathToFile The path to the file. If null, will write to downloads
         * @param data The data to be written. Cannot be null
         * @param nameOfFile The name of the file. If null, will be auto-generated as
         *                   PGMacTips_ + the date in epoch (long) time
         */
        public FileGeneratorAsync(OnTaskCompleteListener listener,
                                  String pathToFile, @NonNull String data, String nameOfFile) {
            this.listener = listener;
            this.data = data;
            this.pathToFile = pathToFile;
            this.nameOfFile = nameOfFile;
        }
        @Override
        protected String doInBackground(Void... params) {
            return (FileUtilities.writeToFile(pathToFile, data, nameOfFile));
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            listener.onTaskComplete(s, PGMacTipsConstants.TAG_TXT_FILE_CREATION);
        }
    }

    /**
     * Quick println in the logcat and write it to the file under downloads
     * @param myObject The string to print (or double, int, whatever)
     * @param <E> Extends object
     */
    public static <E> void logAndWrite (E myObject){
        L.m(myObject);
        writeToOutput(myObject);
    }

    /**
     * Write data to a text file with data. The name of the file is debugLoggingData and its
     * location is under the downloads section of the phone's memory (/storage/emulated/0/Download/).
     * Maxes out at 5mb (which is enormous for a text file)
     * @param myObject String to print
     * @param <E> Extends Object
     */
    public static <E> void writeToOutput(E myObject){
        try {
            FileWriter fw;
            //FileOutputStream outputStream;
            String ss = myObject + "";
            //File file = new File(MyApplication.getAppContext().getExternalFilesDir(Environment.
            //DIRECTORY_DOWNLOADS), "loggingData.txt");
            File file = new File(PGMacTipsConstants.PHONE_URI_TO_WRITE_TO, PGMacTipsConstants.FILE_NAME);
            if(file == null){
                file = new File(StringUtilities.getDataDirectoryLocation(), PGMacTipsConstants.FILE_NAME);
            }
            if(file == null){
                return;
            }
            long fileSize = file.length();
            double megabytes = FileUtilities.convertSize(fileSize, FileUtilities.ByteSizeNames.Bytes, FileUtilities.ByteSizeNames.Megabytes);
            if(megabytes > 5){
                //File is too long, erase and start over
                fw = new FileWriter(file, false);
            } else {
                //File is not too long, append
                fw = new FileWriter(file, true);
            }

            fw.write(ss + "\n");
            fw.flush();
            fw.close();

        } catch (IOException e){
            L.m("error writing to file in PGMacTips");
        } catch (Exception e){}

    }
    /**
     * Overloaded method that allows for more customization
     * @param filePath
     * @param fileName
     * @param fileType
     * @return
     */ // TODO: 2017-11-16 May need to refactor this out
    public static File generateFileForImage(@NonNull String filePath, @NonNull String fileName,
                                            @NonNull String fileType){
        fileName = StringUtilities.removeSpaces(fileName);
        //String state = Environment.getExternalStorageState();
        File file = null;
        try {
            file = new File(filePath, fileName + "_" + DateUtilities.getCurrentDateLong()
                    + fileType);
        } catch (Exception e){}
        return file;
    }

    public static File generateFileForImage(@NonNull Context context,
                                            String imageName,
                                            String imageExtension){
        File file = null;
        if(StringUtilities.isNullOrEmpty(imageExtension)){
            imageExtension = ".jpg";
        }
        if(StringUtilities.isNullOrEmpty(imageName)){
            imageName = "image_" + new Date().getTime();
        }
        try {
            file = File.createTempFile(imageName, imageExtension,
                    context.getExternalFilesDir(Environment.DIRECTORY_PICTURES));
        } catch (Exception e){}
        return file;
    }

    /**
     * Get the file write directory (Mostly used on APIs KitKat+)
     * @return
     */
    public static String getFileWriteDirectory(){
        String str = Environment.getExternalStorageDirectory().getPath();
        return str;
    }

    /**
     * Copy a file from one to another.
     * @param fromFile The from file that is being copied
     * @param toFile The to file that it will be copied to
     * @throws IOException Throws an IOException
     * NOTE, if file paths are the same, it will not run
     */
    public static void copyFile(@NonNull File fromFile, @NonNull File toFile) throws IOException {

        try {
            if(fromFile.getPath().equalsIgnoreCase(toFile.getPath())){
                L.m("File paths are the same");
                return;
            }
        } catch (Exception e){}

        FileChannel outputChannel = null;
        FileChannel inputChannel = null;
        try {
            inputChannel = new FileInputStream(fromFile).getChannel();
            outputChannel = new FileOutputStream(toFile).getChannel();
            inputChannel.transferTo(0, inputChannel.size(), outputChannel);
            inputChannel.close();
        } finally {
            if (inputChannel != null) inputChannel.close();
            if (outputChannel != null) outputChannel.close();
        }
    }

    /**
     * Copy a file (Overloaded). Takes in String paths to use instead of files
     * @param pathFrom From file path being copied
     * @param pathTo To File path being copied to
     * @throws IOException
     * NOTE, if file paths are the same, it will not run
     */
    public static void copyFile(@NonNull String pathFrom, @NonNull String pathTo) throws IOException {
        if (pathFrom.equalsIgnoreCase(pathTo)) {
            L.m("File paths are the same");
            return;
        }
        try {
            File inFile = new File(pathFrom);
            File outFile = new File(pathTo);
            FileUtilities.copyFile(inFile, outFile);
        } catch (IOException e){}
    }




    //All below is to be implemented later. From: https://stackoverflow.com/questions/42264204/bitmapfactory-cant-decode-a-bitmap-from-uri-after-photos-taken-on-android-nouga


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }



    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getRealPathFromURI_API19(Context context, Uri uri) {

        if (isExternalStorageDocument(uri)) {

            // ExternalStorageProvider
            final String docId = DocumentsContract.getDocumentId(uri);
            final String[] split = docId.split(":");
            final String type = split[0];

            if ("primary".equalsIgnoreCase(type)) {
                return Environment.getExternalStorageDirectory() + "/"
                        + split[1];
            }
        } else if (isDownloadsDocument(uri)) {

            // DownloadsProvider

            final String id = DocumentsContract.getDocumentId(uri);
            final Uri contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    Long.valueOf(id));

            return getDataColumn(context, contentUri, null, null);

        } else if (isMediaDocument(uri)) {


            final String docId = DocumentsContract.getDocumentId(uri);
            final String[] split = docId.split(":");
            final String type = split[0];

            Uri contentUri = null;
            if ("image".equals(type)) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            } else if ("video".equals(type)) {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            } else if ("audio".equals(type)) {
                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            }

            final String selection = "_id=?";
            final String[] selectionArgs = new String[]{split[1]};

            return getDataColumn(context, contentUri, selection,
                    selectionArgs);


        } else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;

    }

    /**
     * Encode a Bitmap to a base 64 String
     * @param bm
     * @return
     */
    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
    }

    /**
     * Encode an image to a base 64 String
     * @param path
     * @return
     */
    private String encodeImage(String path) {
        File imagefile = new File(path);
        FileInputStream fis = null;
        try{
            fis = new FileInputStream(imagefile);
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
        Bitmap bm = BitmapFactory.decodeStream(fis);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        //Base64.de
        return encImage;

    }
}
