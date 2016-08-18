package com.pgmacdesign.pgmacutilities.utilities;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.pgmacdesign.pgmacutilities.adaptersandlisteners.OnTaskCompleteListener;
import com.pgmacdesign.pgmacutilities.nonutilities.PGMacUtilitiesConstants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

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
     * Convert a byte type to a different byte type
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
     * Converts bytes into the type you want. So you would pass in bytes and get back Megabytes (IE)
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
     * Converts the input double into bytes
     * @param inputAmount double amount. Could be 10, 544.44, 9999.99999
     * @param inputType The type being input, matches the ByteSizeNames enum
     * @return Returns the double bytes after conversion
     */
    public static double convertToBytes(double inputAmount, ByteSizeNames inputType){
        if(inputAmount <= 0){
            return inputAmount;
        }
        //Loop through and determine type, then calculate bytes and return
        switch (inputType){
            case Bytes:
                return  inputAmount;
            case Kilobytes:
                return (inputAmount * 1024);
            case Megabytes:
                return (inputAmount * 1024 * 1024);
            case Gigabytes:
                return (inputAmount * 1024 * 1024 * 1024);
            case Terabytes:
                return (inputAmount * 1024 * 1024 * 1024 * 1024);
            case PetaBytes:
                return (inputAmount * 1024 * 1024 * 1024 * 1024 * 1024);
            case Exabytes:
                return (inputAmount * 1024 * 1024 * 1024 * 1024 * 1024 * 1024);
            case ZettaBytes:
                return (inputAmount * 1024 * 1024 * 1024 * 1024 * 1024 * 1024 * 1024);
            case Yottabytes:
                return (inputAmount * 1024 * 1024 * 1024 * 1024 * 1024 * 1024 * 1024 * 1024);
            default:
                return inputAmount;
        }
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
                nameOfFile = "PGMacUtilities_" + DateUtilities.getCurrentDateLong();
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
         *                   PGMacUtilities_ + the date in epoch (long) time
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
            listener.onTaskComplete(s, PGMacUtilitiesConstants.TAG_TXT_FILE_CREATION);
        }
    }

    /**
     * Generate an ImageURI
     * @param mContext
     * @return
     */
    public static File generateFileForImage(Context mContext){
        return (generateFileForImage(mContext, null, null));
    }

    /**
     * Overloaded method, generate the Image URI with more control over name and type
     * @param mContext Context
     * @param fileName The file name (IE picture_001)
     * @param fileType The file type (IE .png or .jpg)
     * @return
     */
    public static File generateFileForImage(Context mContext, String fileName, String fileType){
        if(mContext == null){
            return null;
        }
        if(fileType == null){
            fileType = ".png";
        }
        if(!fileType.substring(0,1).equalsIgnoreCase(".")){
            fileType = "." + fileType;
        }
        if(fileName == null){
            fileName = "MyFile";
        }
        fileName = StringUtilities.removeSpaces(fileName);
        String state = Environment.getExternalStorageState();
        File file;
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            file = new File(Environment.getExternalStorageDirectory() + "/DCIM/", fileName +
                    "_" + DateUtilities.getCurrentDateLong() + fileType);
        }else {
            file = new File(mContext.getFilesDir() , fileName +
                    DateUtilities.getCurrentDateLong() + fileType);
        }
        return file;
    }

    /**
     * Overloaded method that allows for more customization
     * @param filePath
     * @param fileName
     * @param fileType
     * @return
     */
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


    public static String getFileWriteDirectory(){
        String str = Environment.getExternalStorageDirectory().getPath();
        return str;
    }
    /*
    private static String tempTest(){
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)){
            String str = Environment.getExternalStorageDirectory().getPath();
        } else {
            //
        }
        //String str = Environment.getExternalStorageState();

        return null;
    }
    */
}
