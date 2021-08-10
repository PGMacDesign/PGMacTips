package com.pgmacdesign.pgmactips.misc

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream


//This will be used only on android P-
private val DOWNLOAD_DIR2 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

//Kitkat or above
fun getMimeTypeForUri(context: Context, finalUri: Uri) : String =
		DocumentFile.fromSingleUri(context, finalUri)?.type ?: "application/octet-stream"

/**
 * Pulled from https://stackoverflow.com/a/64357198/2480714
 * This copies a file from the internal directory to the Downloads directory.
 * Note, requires the [Manifest.permission.WRITE_EXTERNAL_STORAGE] perm
 */
fun copyFileToDownloads(context: Context, downloadedFile: File): Uri? {
	val resolver = context.contentResolver
	return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
		val contentValues = ContentValues().apply {
			put(MediaStore.MediaColumns.DISPLAY_NAME, downloadedFile.name)
			put(MediaStore.MediaColumns.SIZE, downloadedFile.length())
		}
		resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
	} else {
		val authority = "${context.packageName}.provider"
		val destinyFile = File(DOWNLOAD_DIR2, downloadedFile.name)
		FileProvider.getUriForFile(context, authority, destinyFile)
	}?.also { downloadedUri ->
		resolver.openOutputStream(downloadedUri).use { outputStream ->
			val brr = ByteArray(1024)
			var len: Int
			val bufferedInputStream = BufferedInputStream(FileInputStream(downloadedFile.absoluteFile))
			while ((bufferedInputStream.read(brr, 0, brr.size).also { len = it }) != -1) {
				outputStream?.write(brr, 0, len)
			}
			outputStream?.flush()
			bufferedInputStream.close()
		}
	}

}