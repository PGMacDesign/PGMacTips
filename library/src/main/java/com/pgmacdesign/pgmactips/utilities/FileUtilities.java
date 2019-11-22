package com.pgmacdesign.pgmactips.utilities;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.loader.content.CursorLoader;

import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.pgmacdesign.pgmactips.adaptersandlisteners.OnTaskCompleteListener;
import com.pgmacdesign.pgmactips.misc.PGMacTipsConstants;
import com.pgmacdesign.pgmactips.misc.TempString;

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
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.Scanner;

import static com.pgmacdesign.pgmactips.utilities.StringUtilities.getDataColumn;

/**
 * Created by pmacdowell on 8/12/2016.
 */
public class FileUtilities {
	
	/**
	 * Enyum for storage Sizes
	 */
	public enum ByteSizeNames {
		Bytes, Kilobytes, Megabytes, Gigabytes, Terabytes, PetaBytes, Exabytes, ZettaBytes, Yottabytes
	}
	
	/**
	 * Gets the String version of the type
	 *
	 * @param whichOne ByteSizeNames enum to be converted to a String
	 * @return String version of the enum type
	 */
	public static String getByteSizeNamesString(ByteSizeNames whichOne) {
		switch (whichOne) {
			case Bytes:
				return "Bytes";
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
	 *
	 * @param bytesInput Bytes being input for conversion
	 * @param inputType  the input type in the ByteSizeNames enum
	 * @param outputType the output types in the ByteSizeNames enum
	 * @return A converted double. IE, send in 1024 kilobytes, get back 1 megabyte
	 */
	public static double convertSize(double bytesInput, ByteSizeNames inputType, ByteSizeNames outputType) {
		if (bytesInput <= 0) {
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
	 *
	 * @param bytesSize        Bytes being converted. This is bytes, not kilobytes, megabytes, etc
	 * @param whichToConvertTo of type enum BytesizeNames, convert output
	 * @return
	 */
	public static double convertToByteType(double bytesSize, ByteSizeNames whichToConvertTo) {
		if (bytesSize <= 0) {
			return bytesSize;
		}
		//Loop through and determine type, then calculate bytes and return
		switch (whichToConvertTo) {
			case Bytes:
				return bytesSize;
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
	 *
	 * @param inputAmount double amount. Could be 10, 544.44, 9999.99999
	 * @param inputType   The type being input, matches the ByteSizeNames enum
	 * @return Returns the double bytes after conversion
	 */
	public static double convertToBytes(double inputAmount, ByteSizeNames inputType) {
		if (inputAmount <= 0) {
			return inputAmount;
		}
		if (inputType == null) {
			inputType = ByteSizeNames.Bytes;
		}
		//Loop through and determine type, then calculate bytes and return
		switch (inputType) {
			case Bytes:
				return inputAmount;
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
	 *
	 * @param bits
	 * @return
	 */
	public static double convertBitsToBytes(double bits) {
		return (bits * 8);
	}
	
	/**
	 * Simple calculation to convert bytes to bits
	 *
	 * @param bytes
	 * @return
	 */
	public static double convertBytesToBits(double bytes) {
		return (bytes / 8);
	}
	
	/**
	 * Writes a simple Text File (.txt)
	 *
	 * @param path       the String path to write. If null, will use the downloads directory
	 * @param data       The string data to be written
	 * @param nameOfFile name of the file String
	 * @return String location of file
	 */
	public static String writeToFile(String path, String data, String nameOfFile) {
		String fileLocation = null;
		File file = null;
		try {
			
			if (StringUtilities.isNullOrEmpty(nameOfFile)) {
				nameOfFile = "PGMacTips_" + DateUtilities.getCurrentDateLong();
			}
			if (StringUtilities.isNullOrEmpty(path)) {
				path = StringUtilities.getDataDirectoryLocation();
			}
			try {
				file = new File(path, nameOfFile + ".txt");
			} catch (RuntimeException e) {
			}
			if (file == null) {
				try {
					file = new File(StringUtilities.getDataDirectoryLocation(),
							nameOfFile + ".txt");
				} catch (RuntimeException e) {
				}
			}
			if (file == null) {
				try {
					file = new File("/Data/", nameOfFile + ".txt");
				} catch (RuntimeException e) {
				}
			}
			if (file == null) {
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
			try {
				File file2 = new File(fileLocation);
				int length = (int) file2.length();
				
				byte[] bytes = new byte[length];
				
				FileInputStream in = new FileInputStream(file2);
				try {
					in.read(bytes);
				} finally {
					in.close();
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}
			return fileLocation;
		} catch (Exception e) {
			e.printStackTrace();
			return fileLocation;
		}
	}
	
	/**
	 * Write to a file
	 *
	 * @param filePathAndNameWithExtension File name and extension
	 * @param dataToWrite                  Data to write
	 * @return boolean, if successful, true, if not successful, false
	 */
	public static boolean writeFile(@NonNull String filePathAndNameWithExtension,
	                                @NonNull String dataToWrite) {
		return writeFile(filePathAndNameWithExtension, dataToWrite, null);
	}
	
	/**
	 * Write to a file
	 *
	 * @param filePathAndNameWithExtension File name and extension
	 * @param dataToWrite                  Data to write
	 * @param charsetValue                 Charset of the data, IE: "UTF-8"
	 * @return boolean if successful, true, if not successful, false
	 */
	public static boolean writeFile(@NonNull String filePathAndNameWithExtension,
	                                @NonNull String dataToWrite,
	                                @Nullable String charsetValue) {
		if (StringUtilities.isNullOrEmpty(charsetValue)) {
			charsetValue = "UTF-8";
		}
		File file = null;
		try {
			try {
				file = new File(filePathAndNameWithExtension);
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
			if (file == null) {
				return false;
			}
			if (!file.exists()) {
				file.getParentFile().mkdirs();
			}
			FileOutputStream fos = new FileOutputStream(file);
			byte[] dataStream = dataToWrite.getBytes(Charset.forName("UTF-8"));
			fos.write(dataStream);
			fos.flush();
			fos.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Read String data from a file
	 *
	 * @param filePathAndNameWithExtension
	 * @return
	 */
	public static String readFile(@NonNull String filePathAndNameWithExtension) {
		File file = null;
		try {
			try {
				file = new File(filePathAndNameWithExtension);
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
			if (file == null) {
				return null;
			}
			if (!file.exists()) {
				return null;
			}
			StringBuilder fileContents = new StringBuilder((int) file.length());
			Scanner scanner = new Scanner(file);
			if (scanner == null) {
				return null;
			}
			while (scanner.hasNextLine()) {
				fileContents.append(scanner.nextLine() + (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ? System.lineSeparator() : "\n"));
			}
			scanner.close();
			return fileContents.toString();
//			try (Scanner scanner = new Scanner(file)) {
//				while(scanner.hasNextLine()) {
//					fileContents.append(scanner.nextLine() + System.lineSeparator());
//				}
//				return fileContents.toString();
//			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Converts an inputstream to a byte array (Mostly useful for sending images via JSON)
	 *
	 * @param is Input stream, if using a URI, open it by calling:
	 *           InputStream iStream = context.getContentResolver().openInputStream(uri);
	 * @return Byte Array
	 */
	public static byte[] getBytes(InputStream is) {
		try {
			ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];
			
			int len = 0;
			while ((len = is.read(buffer)) != -1) {
				byteBuffer.write(buffer, 0, len);
			}
			return byteBuffer.toByteArray();
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Convert a file to a Uri
	 *
	 * @param file
	 * @return
	 */
	public static Uri convertFileToUri(File file) {
		if (file == null) {
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
		 *
		 * @param listener   Listener to pass back {@link OnTaskCompleteListener}
		 * @param pathToFile The path to the file. If null, will write to downloads
		 * @param data       The data to be written. Cannot be null
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
	 *
	 * @param myObject The string to print (or double, int, whatever)
	 * @param <E>      Extends object
	 */
	public static <E> void logAndWrite(E myObject) {
		L.m(myObject);
		writeToOutput(myObject);
	}
	
	/**
	 * Write data to a text file with data. The name of the file is debugLoggingData and its
	 * location is under the downloads section of the phone's memory (/storage/emulated/0/Download/).
	 * Maxes out at 5mb (which is enormous for a text file)
	 *
	 * @param myObject String to print
	 * @param <E>      Extends Object
	 */
	public static <E> void writeToOutput(E myObject) {
		try {
			FileWriter fw;
			//FileOutputStream outputStream;
			String ss = myObject + "";
			//File file = new File(MyApplication.getAppContext().getExternalFilesDir(Environment.
			//DIRECTORY_DOWNLOADS), "loggingData.txt");
			File file = new File(PGMacTipsConstants.PHONE_URI_TO_WRITE_TO, PGMacTipsConstants.FILE_NAME);
			if (file == null) {
				file = new File(StringUtilities.getDataDirectoryLocation(), PGMacTipsConstants.FILE_NAME);
			}
			if (file == null) {
				return;
			}
			long fileSize = file.length();
			double megabytes = FileUtilities.convertSize(fileSize, FileUtilities.ByteSizeNames.Bytes, FileUtilities.ByteSizeNames.Megabytes);
			if (megabytes > 5) {
				//File is too long, erase and start over
				fw = new FileWriter(file, false);
			} else {
				//File is not too long, append
				fw = new FileWriter(file, true);
			}
			
			fw.write(ss + "\n");
			fw.flush();
			fw.close();
			
		} catch (IOException e) {
			L.m("error writing to file in PGMacTips");
		} catch (Exception e) {
		}
		
	}
	
	/**
	 * Overloaded method that allows for more customization
	 *
	 * @param filePath
	 * @param fileName
	 * @param fileType
	 * @return
	 */ // TODO: 2017-11-16 May need to refactor this out
	public static File generateFileForImage(@NonNull String filePath, @NonNull String fileName,
	                                        @NonNull String fileType) {
		fileName = StringUtilities.removeSpaces(fileName);
		//String state = Environment.getExternalStorageState();
		File file = null;
		try {
			file = new File(filePath, fileName + "_" + DateUtilities.getCurrentDateLong()
					+ fileType);
		} catch (Exception e) {
		}
		return file;
	}
	
	public static File generateFileForImage(@NonNull Context context,
	                                        String imageName,
	                                        String imageExtension) {
		File file = null;
		if (StringUtilities.isNullOrEmpty(imageExtension)) {
			imageExtension = ".jpg";
		}
		if (StringUtilities.isNullOrEmpty(imageName)) {
			imageName = "image_" + new Date().getTime();
		}
		try {
			file = File.createTempFile(imageName, imageExtension,
					context.getExternalFilesDir(Environment.DIRECTORY_PICTURES));
		} catch (Exception e) {
		}
		return file;
	}
	
	/**
	 * Get the file write directory (Mostly used on APIs KitKat+)
	 *
	 * @return
	 */
	public static String getFileWriteDirectory() {
		String str = Environment.getExternalStorageDirectory().getPath();
		return str;
	}
	
	/**
	 * Copy a file from one to another.
	 *
	 * @param fromFile The from file that is being copied
	 * @param toFile   The to file that it will be copied to
	 * @throws IOException Throws an IOException
	 *                     NOTE, if file paths are the same, it will not run
	 */
	public static void copyFile(@NonNull File fromFile, @NonNull File toFile) throws IOException {
		
		try {
			if (fromFile.getPath().equalsIgnoreCase(toFile.getPath())) {
				L.m("File paths are the same");
				return;
			}
		} catch (Exception e) {
		}
		
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
	 *
	 * @param pathFrom From file path being copied
	 * @param pathTo   To File path being copied to
	 * @throws IOException NOTE, if file paths are the same, it will not run
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
		} catch (IOException e) {
		}
	}
	
	/**
	 * Encode a Bitmap to a base 64 String
	 *
	 * @param bm
	 * @return
	 */
	private String encodeImage(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] b = baos.toByteArray();
		String encImage = Base64.encodeToString(b, Base64.DEFAULT);
		
		return encImage;
	}
	
	/**
	 * Encode an image to a base 64 String
	 *
	 * @param path
	 * @return
	 */
	private String encodeImage(String path) {
		File imagefile = new File(path);
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(imagefile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Bitmap bm = BitmapFactory.decodeStream(fis);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] b = baos.toByteArray();
		String encImage = Base64.encodeToString(b, Base64.DEFAULT);
		//Base64.de
		return encImage;
		
	}
	
	//region Determining Storage Available and respective converter methods
	
	/**
	 * Determine whether or not there is an external memory source available
	 *
	 * @return if true, is attached, if false, is not
	 */
	public static boolean externalMemoryAvailable() {
		try {
			return android.os.Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Get the available internal memory size as a String
	 *
	 * @return
	 */
	@SuppressLint("NewApi")
	public static String getAvailableInternalMemorySizeString() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
				? stat.getBlockSizeLong() : (long) stat.getBlockSize();
		long availableBlocks = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
				? stat.getAvailableBlocksLong() : (long) stat.getAvailableBlocks();
		return formatSize(availableBlocks * blockSize);
	}
	
	/**
	 * Get the available internal memory size as a long. (in bytes)
	 *
	 * @return
	 */
	@SuppressLint("NewApi")
	public static long getAvailableInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
				? stat.getBlockSizeLong() : (long) stat.getBlockSize();
		long availableBlocks = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
				? stat.getAvailableBlocksLong() : (long) stat.getAvailableBlocks();
		return (availableBlocks * blockSize);
	}
	
	/**
	 * Get the internal memory size as a String.
	 *
	 * @return
	 */
	@SuppressLint("NewApi")
	public static String getTotalInternalMemorySizeString() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
				? stat.getBlockSizeLong() : (long) stat.getBlockSize();
		long totalBlocks = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
				? stat.getBlockCountLong() : (long) stat.getAvailableBlocks();
		return formatSize(totalBlocks * blockSize);
	}
	
	/**
	 * Get the internal memory size as a long. (in bytes)
	 *
	 * @return
	 */
	@SuppressLint("NewApi")
	public static long getTotalInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
				? stat.getBlockSizeLong() : (long) stat.getBlockSize();
		long totalBlocks = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
				? stat.getBlockCountLong() : (long) stat.getAvailableBlocks();
		return (totalBlocks * blockSize);
	}
	
	/**
	 * Get the available external memory size as a String
	 *
	 * @return
	 */
	@SuppressLint("NewApi")
	public static String getAvailableExternalMemorySizeString() {
		if (externalMemoryAvailable()) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
					? stat.getBlockSizeLong() : (long) stat.getBlockSize();
			long availableBlocks = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
					? stat.getAvailableBlocksLong() : (long) stat.getAvailableBlocks();
			return formatSize(availableBlocks * blockSize);
		} else {
			return null;
		}
	}
	
	/**
	 * Get the available external memory size as a long. (in bytes)
	 *
	 * @return long of memory size in bytes. If it cannot determine a correct value, will return -1
	 */
	@SuppressLint("NewApi")
	public static long getAvailableExternalMemorySize() {
		if (externalMemoryAvailable()) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
					? stat.getBlockSizeLong() : (long) stat.getBlockSize();
			long availableBlocks = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
					? stat.getAvailableBlocksLong() : (long) stat.getAvailableBlocks();
			return (availableBlocks * blockSize);
		} else {
			return -1;
		}
	}
	
	/**
	 * Get the total external memory storage size as a String
	 *
	 * @return
	 */
	@SuppressLint("NewApi")
	public static String getTotalExternalMemorySizeString() {
		if (externalMemoryAvailable()) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
					? stat.getBlockSizeLong() : (long) stat.getBlockSize();
			long totalBlocks = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
					? stat.getBlockCountLong() : (long) stat.getAvailableBlocks();
			return formatSize(totalBlocks * blockSize);
		} else {
			return null;
		}
	}
	
	/**
	 * Get the total external memory size as a long. (in bytes)
	 *
	 * @return long of memory size in bytes. If it cannot determine a correct value, will return -1
	 */
	@SuppressLint("NewApi")
	public static long getTotalExternalMemorySize() {
		if (externalMemoryAvailable()) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
					? stat.getBlockSizeLong() : (long) stat.getBlockSize();
			long totalBlocks = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
					? stat.getBlockCountLong() : (long) stat.getAvailableBlocks();
			return (totalBlocks * blockSize);
		} else {
			return -1;
		}
	}
	
	/**
	 * Get the total folder size of a directory passed
	 *
	 * @param directory
	 * @return
	 */
	public static long getFolderSize(File directory) {
		if (directory == null) {
			return 0;
		}
		long length = 0;
		for (File file : directory.listFiles()) {
			if (file.isFile())
				length += file.length();
			else
				length += getFolderSize(file);
		}
		return length;
	}
	
	/**
	 * Format the size into gb
	 *
	 * @param size
	 * @return
	 */
	private static String formatSize(long size) {
		if (size <= 0) {
			return "0 Bytes";
		}
		double decimalSuffix = 0;
		String suffix = null;
		if (size >= 1024) {
			suffix = "KB";
			size /= 1024;
			if (size >= 1024) {
				suffix = "MB";
				size /= 1024;
				if (size >= 1024) {
					suffix = "GB";
//					decimalSuffix = (double) (size /= (double)1024);
					size /= 1024;
				}
			}
		}
		
		StringBuilder resultBuffer = new StringBuilder(Long.toString(size));
		
		int commaOffset = resultBuffer.length() - 3;
		while (commaOffset > 0) {
			resultBuffer.insert(commaOffset, ',');
			commaOffset -= 3;
		}
		
		if (suffix != null) {
			resultBuffer.append(" ");
			resultBuffer.append(suffix);
		}
		return resultBuffer.toString();
	}
	
	//endregion
	
	//region Utilities for Writing / Reading Encrypted Data using the EncryptionUtilities
	
	
	/**
	 * Creates a file with Encrypted data in it and returns that file
	 * Note, will write to the file system
	 * Utilities {@link EncryptionUtilities}
	 *
	 * @param filePathAndNameWithExtension The full file path and name with extension. IE:
	 *                                     "/
	 * @param password
	 * @param salt
	 * @return String with Decrypted data. Can be null if could not write or properly decrypt data.
	 */
	@RequiresApi(value = 19)
	public static String readEncryptedFile(@NonNull String filePathAndNameWithExtension,
	                                       @NonNull TempString password,
	                                       @NonNull String salt) {
		
		File file = null;
		try {
			try {
				file = new File(filePathAndNameWithExtension);
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
			if (file == null) {
				return null;
			}
			if (!file.exists()) {
				return null;
			}
			StringBuilder fileContents = new StringBuilder((int) file.length());
			Scanner scanner = new Scanner(file);
			if (scanner == null) {
				return null;
			}
			while (scanner.hasNextLine()) {
				fileContents.append(scanner.nextLine() + System.lineSeparator());
			}
			String encryptedString = fileContents.toString();
			String decryptedString = EncryptionUtilities.decryptString(encryptedString, password, salt);
			scanner.close();
			return decryptedString;
//			try (Scanner scanner = new Scanner(file)) {
//				while(scanner.hasNextLine()) {
//					fileContents.append(scanner.nextLine() + System.lineSeparator());
//				}
//				return fileContents.toString();
//			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Creates a file with Encrypted data in it and returns that file
	 * Note, will write to the file system
	 * Utilities {@link EncryptionUtilities}
	 *
	 * @param filePathAndNameWithExtension The full file path and name with extension. IE:
	 *                                     "/
	 * @param password
	 * @param salt
	 * @param dataToWrite
	 * @return File with Encrypted data. Can be null if could not write or properly encrypt data.
	 */
	@RequiresApi(value = 19)
	public static File writeEncryptedFile(@NonNull String filePathAndNameWithExtension,
	                                      @NonNull TempString password,
	                                      @NonNull String salt,
	                                      @NonNull String dataToWrite) {
		String encryptedString = null;
		try {
			encryptedString = EncryptionUtilities.encryptString(dataToWrite, password, salt);
		} catch (Exception gse) {
			gse.printStackTrace();
		}
		if (encryptedString == null) {
			return null;
		}
		File file = null;
		try {
			try {
				file = new File(filePathAndNameWithExtension);
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
			if (file == null) {
				return null;
			}
			if (!file.exists()) {
				file.getParentFile().mkdirs();
			}
			FileOutputStream fos = new FileOutputStream(file);
			byte[] dataStream = encryptedString.getBytes(Charset.forName("UTF-8"));
			fos.write(dataStream);
			fos.flush();
			fos.close();
			return file;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Overloaded to allow for StringBuilder
	 */
	@RequiresApi(value = 19)
	public static File writeEncryptedFile(@NonNull String filePathAndNameWithExtension,
	                                      @NonNull TempString password,
	                                      @NonNull String salt,
	                                      @NonNull StringBuilder dataToWrite) {
		return writeEncryptedFile(filePathAndNameWithExtension, password, salt, dataToWrite.toString());
	}
	
	//endregion
	
	//region File and Path Management
	
	//region Static Int Tags
	/**
	 * Callback for processing percent. Will be a float within the range of >=0 && <= 1
	 */
	public static final int TAG_GET_REAL_FILE_PATH_PROCESSING_PROGRESS_VALUE = 44112;
	/**
	 * Callback for dismissing progress bars. Will only fire on UI-appropriate threads.
	 * Value sent will be null
	 */
	public static final int TAG_GET_REAL_FILE_PATH_DISMISS_PROGRESS_BARS = 44113;
	/**
	 * Callback for a completed process where the operation failed. will be null
	 */
	public static final int TAG_GET_REAL_FILE_PATH_FAILED = 44114;
	/**
	 * Callback for a completed process where the operation succeeded, but result may still
	 * be null depending on the file system logic in place.
	 */
	public static final int TAG_GET_REAL_FILE_PATH_SUCCESS = 44115;
	//endregion
	
	//region Public Methods
	
	/**
	 * Overloaded for naming simplicity so as to have another way to find.
	 * Sends directly to: {@link FileUtilities#getRealPath(Context, Uri)}
	 */
	public static String getFilePath(Context context, Uri fileUri){
		return FileUtilities.getRealPath(context, fileUri);
	}
	
	/**
	 * Overloaded for naming simplicity so as to have another way to find.
	 * Sends directly to: {@link FileUtilities#getRealPath(Context, Uri, OnTaskCompleteListener)}
	 */
	public static void getFilePath(Context context, Uri fileUri,
	                               @NonNull OnTaskCompleteListener callbackListener){
		FileUtilities.getRealPath(context, fileUri, callbackListener);
	}
	
	/**
	 * Get the real file path Asynchronously. This will determine the API level and process
	 * it accordingly. The file path will be passed back upon the callback listener
	 * This runs on a background thread and utilizes the callback listener.
	 * @param context Context to use. As this is running using Async, use Application
	 *                Context here instead of Activity Context
	 * @param fileUri File Uri to convert
	 * @param callbackListener Callback listener to send back results asynchronously
	 */
	public static void getRealPath(Context context, Uri fileUri,
	                               @NonNull OnTaskCompleteListener callbackListener) {
		String realPath;
		// SDK < API11
		if (Build.VERSION.SDK_INT < 11) {
			realPath = getRealPathFromURI_BelowAPI11(context, fileUri);
		}
		// SDK >= 11 && SDK < 19
		else if (Build.VERSION.SDK_INT < 19) {
			realPath = getRealPathFromURI_API11to18(context, fileUri);
		}
		// SDK > 19 (Android 4.4) and up
		else {
			getRealPathFromURI_API19(context, fileUri, callbackListener);
			return;
		}
		if(!StringUtilities.isNullOrEmpty(realPath)){
			callbackListener.onTaskComplete(realPath, TAG_GET_REAL_FILE_PATH_SUCCESS);
		} else {
			callbackListener.onTaskComplete(null, TAG_GET_REAL_FILE_PATH_FAILED);
		}
	}
	
	/**
	 * Get the real file path. This will determine the API level and process it accordingly.
	 * Note that this runs on the main UI thread and if any results from a web-based docs
	 * location (IE Google Docs) are expected or possible, it is recommended to use the overloaded
	 * method with the callback listener so as to not block the main thread. Link:
	 * {@link #getRealPath(Context, Uri, OnTaskCompleteListener)}
	 * This runs on the thread it is called from.
	 * @param context Context to use
	 * @param fileUri File Uri to convert
	 */
	public static String getRealPath(Context context, Uri fileUri) {
		String realPath;
		// SDK < API11
		if (Build.VERSION.SDK_INT < 11) {
			realPath = getRealPathFromURI_BelowAPI11(context, fileUri);
		}
		// SDK >= 11 && SDK < 19
		else if (Build.VERSION.SDK_INT < 19) {
			realPath = getRealPathFromURI_API11to18(context, fileUri);
		}
		// SDK > 19 (Android 4.4) and up
		else {
			realPath = getRealPathFromURI_API19(context, fileUri);
		}
		return realPath;
	}
	
	/**
	 * Appends the file String {@link PGMacTipsConstants#FILE_PREFIX} to the
	 * front of a file if it does not already have it attached.
	 * @param str String to prepend
	 * @return String with the file prefix attached in the front
	 */
	public static String addFilePrefixToFrontOfString(String str) {
		if (StringUtilities.isNullOrEmpty(str)) {
			return str;
		}
		if (str.startsWith(PGMacTipsConstants.FILE_PREFIX)) {
			return str;
		}
		str = PGMacTipsConstants.FILE_PREFIX + str;
		return str;
	}
	
	/**
	 * Get the drive file path from a valid Google Drive path
	 * @param uri
	 * @param context
	 * @return
	 */
	public static String getDriveFilePath(Uri uri, @NonNull Context context) {
		if(uri == null){
			return null;
		}
		Uri returnUri = uri;
		Cursor returnCursor = context.getContentResolver().query(returnUri, null, null, null, null);
		int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
		int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
		returnCursor.moveToFirst();
		String name = (returnCursor.getString(nameIndex));
		String size = (Long.toString(returnCursor.getLong(sizeIndex)));
		long sizeLong = NumberUtilities.parseLongSafe(size, 1);
		File file = new File(context.getCacheDir(), name);
		try {
			InputStream inputStream = context.getContentResolver().openInputStream(uri);
			FileOutputStream outputStream = new FileOutputStream(file);
			int read = 0;
			int maxBufferSize = 1 * 1024 * 1024;
			int bytesAvailable = inputStream.available();
			
			//int bufferSize = 1024;
			int bufferSize = Math.min(bytesAvailable, maxBufferSize);
			final byte[] buffers = new byte[bufferSize];
			
			long trackerValue = 0;
			
			while ((read = inputStream.read(buffers)) != -1) {
				trackerValue += buffers.length;
				outputStream.write(buffers, 0, read);
			}
			inputStream.close();
			outputStream.close();
		} catch (Exception e) {
			Log.e("Exception", e.getMessage());
		}
		return file.getPath();
	}
	
	/**
	 * Get the drive file path from a valid Google Drive path
	 * Overloaded to allow for Async callback to unblock main thread
	 * @param uri
	 * @param context As this is running using Async, use Application Context here instead of Activity Context
	 * @param callbackListener
	 * @return
	 */
	public static void getDriveFilePath(Uri uri, @NonNull Context context, @NonNull OnTaskCompleteListener callbackListener) {
		GetDriveFilePathAsync g = new GetDriveFilePathAsync(uri, context, callbackListener);
		g.execute();
	}
	
	/**
	 * Asynctask to get file on background thread and update listener for foreground updates
	 * using passed callback listener.
	 */
	public static class GetDriveFilePathAsync extends AsyncTask<Void, Float, Void> {
		
		private Uri uri;
		private Context context;
		private OnTaskCompleteListener callbackListener;
		private float lastValueSet;
		
		/**
		 *
		 * @param uri
		 * @param context Make sure to pass Application Context, not Activity Context here to
		 *                prevent Memory leaks
		 * @param callbackListener
		 */
		GetDriveFilePathAsync(Uri uri, @NonNull Context context,
		                      @NonNull OnTaskCompleteListener callbackListener){
			this.uri = uri;
			this.context = context;
			this.callbackListener = callbackListener;
			this.lastValueSet = 0F;
		}
		
		@Override
		protected void onCancelled(Void aVoid) {
			super.onCancelled(aVoid);
			try {
				this.callbackListener.onTaskComplete(null, TAG_GET_REAL_FILE_PATH_FAILED);
				this.context = null;
			} catch (Exception e){}
		}
		
		@Override
		protected void onCancelled() {
			super.onCancelled();
			try {
				this.callbackListener.onTaskComplete(null, TAG_GET_REAL_FILE_PATH_FAILED);
				this.context = null;
			} catch (Exception e){}
		}
		
		@Override
		protected void onProgressUpdate(Float... values) {
			super.onProgressUpdate(values);
			try {
				float flt = values[0];
				if(flt < this.lastValueSet){
					//Not setting value here in the event that it goes >1 and resets back to 0
				} else {
					this.callbackListener.onTaskComplete(flt, TAG_GET_REAL_FILE_PATH_PROCESSING_PROGRESS_VALUE);
				}
				this.lastValueSet = flt;
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		
		@Override
		protected Void doInBackground(Void... voids) {
			if(this.uri == null){
				this.callbackListener.onTaskComplete(null, TAG_GET_REAL_FILE_PATH_FAILED);
				return null;
			}
			Uri returnUri = this.uri;
			Cursor returnCursor = this.context.getContentResolver().query(returnUri, null,
					null, null, null);
			if(returnCursor == null){
				this.callbackListener.onTaskComplete(null, TAG_GET_REAL_FILE_PATH_FAILED);
				return null;
			}
			int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
			int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
			returnCursor.moveToFirst();
			String name = (returnCursor.getString(nameIndex));
			String size = (Long.toString(returnCursor.getLong(sizeIndex)));
			long sizeLong = NumberUtilities.parseLongSafe(size, 1);
			File file = new File(this.context.getCacheDir(), name);
			try {
				InputStream inputStream = this.context.getContentResolver().openInputStream(uri);
				FileOutputStream outputStream = new FileOutputStream(file);
				int read = 0;
				int maxBufferSize = 1 * 1024 * 1024;
				if(inputStream == null){
					this.callbackListener.onTaskComplete(null, TAG_GET_REAL_FILE_PATH_FAILED);
					return null;
				}
				int bytesAvailable = inputStream.available();
				
				//int bufferSize = 1024;
				int bufferSize = Math.min(bytesAvailable, maxBufferSize);
				final byte[] buffers = new byte[bufferSize];
				
				long trackerValue = 0;
				
				while ((read = inputStream.read(buffers)) != -1) {
					trackerValue += buffers.length;
					Float xy = ((float)trackerValue) / ((float)sizeLong);
					if(xy < 0){
						//Value is < 0, meaning it is probably being improperly calculated. Passing zero as a result.
						this.publishProgress(0F);
					} else if (xy > 1){
						//Value is > 0, meaning it is probably being improperly calculated. Passing zero as a result.
						this.publishProgress(0F);
					} else {
						//Value is between 0 and 1, meaning it is correctly calculated. Passing float value.
						this.publishProgress((float)xy);
					}
					outputStream.write(buffers, 0, read);
				}
				inputStream.close();
				outputStream.close();
			} catch (Exception e) {
				Log.e("Exception", e.getMessage());
				this.callbackListener.onTaskComplete(null, TAG_GET_REAL_FILE_PATH_FAILED);
				returnCursor.close();
				return null;
			}
			if(!file.exists()){
				this.callbackListener.onTaskComplete(null, TAG_GET_REAL_FILE_PATH_FAILED);
				returnCursor.close();
				return null;
			}
			this.callbackListener.onTaskComplete(file.getPath(), TAG_GET_REAL_FILE_PATH_SUCCESS);
			returnCursor.close();
			return null;
		}
		
		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			try {
				this.callbackListener.onTaskComplete(null, TAG_GET_REAL_FILE_PATH_DISMISS_PROGRESS_BARS);
				this.context = null;
			} catch (Exception e){}
		}
		
	}
	
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
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}
	
	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is Google Drive.
	 */
	public static boolean isGoogleDriveUri(Uri uri) {
		return "com.google.android.apps.docs.storage".equals(uri.getAuthority()) || "com.google.android.apps.docs.storage.legacy".equals(uri.getAuthority());
	}
	
	/**
	 * Get the Mimetype from the Uri passed
	 * @param context
	 * @param uri
	 * @return
	 */
	public static String getMimetypeFromUri(@NonNull Context context, Uri uri){
		if(uri == null){
			return null;
		}
		try {
			if(uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)){
				final MimeTypeMap mime = MimeTypeMap.getSingleton();
				return mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
			} else {
				return MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
			}
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Convert a file path to an InputStream
	 * @param context
	 * @param uri
	 * @param mimeTypeFilter
	 * @return
	 */
	public static InputStream convertFilePathToInputStream(@NonNull Context context, Uri uri, @Nullable String mimeTypeFilter){
		if(uri == null){
			return null;
		}
		ContentResolver resolver = context.getContentResolver();
		if(StringUtilities.isNullOrEmpty(mimeTypeFilter)){
			mimeTypeFilter = getMimetypeFromUri(context, uri);
		}
		String[] openableMimeTypes = resolver.getStreamTypes(uri, mimeTypeFilter);
		
		if (openableMimeTypes == null ||
				openableMimeTypes.length < 1) {
			return null;
		}
		try {
			return resolver.openTypedAssetFileDescriptor(uri, openableMimeTypes[0], null).createInputStream();
		} catch (IOException|NullPointerException ioe){
			ioe.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Get the file extension (IE .txt)
	 * @param file
	 * @return
	 */
	private String getFileExtension(@NonNull File file) {
		return getFileExtension(file.getName());
	}
	
	/**
	 * Get the file extension (IE .txt)
	 * @param fileName The filename with the extension at the end
	 * @return Return the file extension with a period in the front
	 */
	private String getFileExtension(String fileName) {
		int lastIndexOf = fileName.lastIndexOf(".");
		if (lastIndexOf == -1) {
			return ""; // empty extension
		}
		return ("." + fileName.substring(lastIndexOf));
	}
	
	//endregion
	
	//region Private Methods
	
	/**
	 * Get column data using {@link Cursor}
	 * @param context
	 * @param uri
	 * @param selection
	 * @param selectionArgs
	 * @return
	 */
	private static String getDataColumn(Context context, Uri uri, String selection,
	                                    String[] selectionArgs) {
		
		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = {
				column
		};
		
		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
					null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}
	
	/**
	 * Get the real path from API levels < 11. No need for a callback listener here as no web-based
	 * downloads can trigger in this API range
	 * @param context
	 * @param contentUri
	 * @return
	 */
	private static String getRealPathFromURI_BelowAPI11(Context context, Uri contentUri) {
		String[] proj = {MediaStore.Images.Media.DATA};
		Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
		int column_index = 0;
		String result = "";
		if (cursor != null) {
			column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			result = cursor.getString(column_index);
			cursor.close();
			return result;
		}
		return result;
	}
	
	/**
	 * Get the real path from API levels 11-18. No need for a callback listener here as no web-based
	 * downloads can trigger in this API range
	 * @param context
	 * @param contentUri
	 * @return
	 */
	@SuppressLint("NewApi")
	private static String getRealPathFromURI_API11to18(Context context, Uri contentUri) {
		String[] proj = {MediaStore.Images.Media.DATA};
		String result = null;
		
		CursorLoader cursorLoader = new CursorLoader(context, contentUri, proj, null, null, null);
		Cursor cursor = cursorLoader.loadInBackground();
		
		if (cursor != null) {
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			result = cursor.getString(column_index);
			cursor.close();
		}
		return result;
	}
	
	/**
	 * Get the path from on API levels >= 19. This changed where the values were stored so
	 * additional checks are required to obtain the absolute path.
	 * @param context Context to check.  As this is running using Async, use Application
	 *                Context here instead of Activity Context
	 * @param uri Uri pulled from the callback intent in the onActivityResult
	 * @param callbackListener Callback to send the response Async in the event of a required
	 *                         download or some other trigger event that may take >= 1 second
	 *                         and should be run on a background thread
	 */
	@SuppressLint("NewApi")
	private static void getRealPathFromURI_API19(final Context context, final Uri uri,
	                                             @NonNull OnTaskCompleteListener callbackListener) {
		
		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
		String toReturn;
		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];
				
				// This is for checking Main Memory
				if ("primary".equalsIgnoreCase(type)) {
					if (split.length > 1) {
						toReturn = Environment.getExternalStorageDirectory() + "/" + split[1];
						callbackListener.onTaskComplete(toReturn, TAG_GET_REAL_FILE_PATH_SUCCESS);
						return;
					} else {
						toReturn = Environment.getExternalStorageDirectory() + "/";
						callbackListener.onTaskComplete(toReturn, TAG_GET_REAL_FILE_PATH_SUCCESS);
						return;
					}
					// This is for checking SD Card
				} else {
					toReturn =  "storage" + "/" + docId.replace(":", "/");
					callbackListener.onTaskComplete(toReturn, TAG_GET_REAL_FILE_PATH_SUCCESS);
					return;
				}
				
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {
				String fileName = getFilePathNew(context, uri);
				if (fileName != null) {
					toReturn = Environment.getExternalStorageDirectory().toString() + "/Download/" + fileName;
					callbackListener.onTaskComplete(toReturn, TAG_GET_REAL_FILE_PATH_SUCCESS);
					return;
				}
				
				String id = DocumentsContract.getDocumentId(uri);
				if (id.startsWith("raw:")) {
					id = id.replaceFirst("raw:", "");
					File file = new File(id);
					if (file.exists()) {
						toReturn = id;
						callbackListener.onTaskComplete(toReturn, TAG_GET_REAL_FILE_PATH_SUCCESS);
						return;
					}
				}
				final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
				toReturn = getDataColumn(context, contentUri, null, null);
				callbackListener.onTaskComplete(toReturn, TAG_GET_REAL_FILE_PATH_SUCCESS);
				return;
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
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
				final String[] selectionArgs = new String[]{
						split[1]
				};
				
				toReturn = getDataColumn(context, contentUri, selection, selectionArgs);
				callbackListener.onTaskComplete(toReturn, TAG_GET_REAL_FILE_PATH_SUCCESS);
				return;
			}
			//Google Photos
			else if (isGooglePhotosUri(uri)) {
				callbackListener.onTaskComplete(uri.getLastPathSegment(), TAG_GET_REAL_FILE_PATH_SUCCESS);
				return;
			}
			//Google Drive
			else if(isGoogleDriveUri(uri)){
				getDriveFilePath(uri, context, callbackListener);
				return;
			}
		}
		// MediaStore (and general)
		if ("content".equalsIgnoreCase(uri.getScheme())) {
			// Return the remote address
			if (isGooglePhotosUri(uri)) {
				toReturn = uri.getLastPathSegment();
				callbackListener.onTaskComplete(toReturn, TAG_GET_REAL_FILE_PATH_SUCCESS);
				return;
			}
			if(isGoogleDriveUri(uri)){
				getDriveFilePath(uri, context, callbackListener);
				return;
			}
			toReturn = getDataColumn(context, uri, null, null);
			callbackListener.onTaskComplete(toReturn, TAG_GET_REAL_FILE_PATH_SUCCESS);
			return;
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			toReturn = uri.getPath();
			callbackListener.onTaskComplete(toReturn, TAG_GET_REAL_FILE_PATH_SUCCESS);
			return;
		}
		//If this is reached, likely in a spot in the file system that won't read properly (IE Downloads)
		toReturn = uri.getPath();
		callbackListener.onTaskComplete(toReturn, TAG_GET_REAL_FILE_PATH_SUCCESS);
		return;
	}
	
	/**
	 * Get the path from on API levels >= 19. This changed where the values were stored so
	 * additional checks are required to obtain the absolute path.
	 * Note that this runs on the main UI thread and if any results from a web-based docs
	 * location (IE Google Docs) are expected or possible, it is recommended to use the overloaded
	 * method with the callback listener so as to not block the main thread. Link for call:
	 * {@link #getRealPathFromURI_API19(Context, Uri, OnTaskCompleteListener)}
	 * @param context Context to check
	 * @param uri Uri pulled from the callback intent in the onActivityResult
	 */
	@SuppressLint("NewApi")
	private static String getRealPathFromURI_API19(final Context context, final Uri uri) {
		if(uri == null){
			return null;
		}
		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
		
		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];
				
				// This is for checking Main Memory
				if ("primary".equalsIgnoreCase(type)) {
					if (split.length > 1) {
						return Environment.getExternalStorageDirectory() + "/" + split[1];
					} else {
						return Environment.getExternalStorageDirectory() + "/";
					}
					// This is for checking SD Card
				} else {
					return "storage" + "/" + docId.replace(":", "/");
				}
				
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {
				String fileName = getFilePathNew(context, uri);
				if (fileName != null) {
					return Environment.getExternalStorageDirectory().toString() + "/Download/" + fileName;
				}
				
				String id = DocumentsContract.getDocumentId(uri);
				if (id.startsWith("raw:")) {
					id = id.replaceFirst("raw:", "");
					File file = new File(id);
					if (file.exists()) {
						return id;
					}
				}
				
				final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
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
				final String[] selectionArgs = new String[]{
						split[1]
				};
				
				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
			//Google Photos
			else if (isGooglePhotosUri(uri)) {
				return uri.getLastPathSegment();
			}
			//Google Drive
			else if(isGoogleDriveUri(uri)){
				String str = getDriveFilePath(uri, context);
				Uri uri1 = Uri.parse(str);
				return uri1.getPath();
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {
			// Return the remote address
			if (isGooglePhotosUri(uri)) {
				return uri.getLastPathSegment();
			}
			
			if(isGoogleDriveUri(uri)){
				String str = getDriveFilePath(uri, context);
				Uri uri1 = Uri.parse(str);
				return uri1.getPath();
			}
			
			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}
		return uri.getPath();
	}
	
	/**
	 * Get the file path when an Oreo or higher device is being used
	 * @param context
	 * @param uri
	 * @return
	 */
	private static String getFilePathNew(Context context, Uri uri) {
		
		Cursor cursor = null;
		final String[] projection = {
				MediaStore.MediaColumns.DISPLAY_NAME
		};
		
		try {
			cursor = context.getContentResolver().query(uri, projection, null, null,
					null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}
	
	//endregion
	
	//endregion
}
