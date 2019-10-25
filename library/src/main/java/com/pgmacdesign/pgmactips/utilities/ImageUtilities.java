package com.pgmacdesign.pgmactips.utilities;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;

import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;

import com.pgmacdesign.pgmactips.R;
import com.pgmacdesign.pgmactips.adaptersandlisteners.OnTaskCompleteListener;
import com.pgmacdesign.pgmactips.datamodels.ImageMimeType;
import com.pgmacdesign.pgmactips.misc.CustomAnnotationsBase;
import com.pgmacdesign.pgmactips.misc.PGMacTipsConstants;
import com.pgmacdesign.pgmactips.transformations.CircleTransform;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

/**
 * Created by pmacdowell on 8/15/2016.
 */
public class ImageUtilities {

    public static final String INVALID_PIXEL_POSITIONS =
            "Invalid pixel positions. Please check the passed params and start again";

    //region Picasso
    
    //region Picasso Circular Images With Caching
    /**
     * Set a circular image into a view and set caching.
     *
     * @param urlThumbnail          URL String to use
     * @param viewToSet             View to set it into
     * @param backupImageResourceId Backup resource id in case the String url fails parsing
     * @param <T>                   {T extends View}
     */
    @CustomAnnotationsBase.RequiresDependency(requiresDependency = CustomAnnotationsBase.Dependencies.Picasso)
    public static <T extends ImageView> void setCircularImageWithPicasso(String urlThumbnail,
                                                                         final T viewToSet,
                                                                         final int backupImageResourceId,
                                                                         final Integer circularFrameColor,
                                                                         final Integer circularFrameWidth) {

        if (StringUtilities.isNullOrEmpty(urlThumbnail)) {
            try {
                Picasso.get().load(backupImageResourceId).
                        transform(new CircleTransform(circularFrameColor, circularFrameWidth)).into(viewToSet);

            } catch (Exception e) {
            }
        } else {

            final String innerUrlThumbnail = urlThumbnail;

            try {
                Picasso.get()
                        .load(innerUrlThumbnail)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .transform(new CircleTransform(circularFrameColor, circularFrameWidth))
                        .into(viewToSet, new Callback() {
                            @Override
                            public void onSuccess() {
                                //Load the image into cache for next time
                                try {
                                    List<String> toCache = new ArrayList<String>();
                                    toCache.add(innerUrlThumbnail);
                                    ImageUtilities.LoadImagesIntoPicassoCache async = new
                                            ImageUtilities.LoadImagesIntoPicassoCache(toCache);
                                    async.execute();
                                } catch (Exception e2) {
                                }
                            }

                            @Override
                            public void onError(Exception e) {
                                //Can trigger if image not in cache
                                Picasso.get().load(innerUrlThumbnail)
                                        .transform(new CircleTransform(circularFrameColor, circularFrameWidth))
                                        .into(viewToSet, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                //Load the image into cache for next time
                                                try {
                                                    List<String> toCache = new ArrayList<String>();
                                                    toCache.add(innerUrlThumbnail);
                                                    ImageUtilities.LoadImagesIntoPicassoCache async = new
                                                            ImageUtilities.LoadImagesIntoPicassoCache(toCache);
                                                    async.execute();
                                                } catch (Exception e2) {}
                                            }

                                            @Override
                                            public void onError(Exception e) {
                                                Picasso.get().load(backupImageResourceId)
                                                        .transform(new CircleTransform(circularFrameColor, circularFrameWidth))
                                                        .into(viewToSet);
                                            }
                                        });

                            }
                            
                        });
            } catch (Exception e) {
                try {
                    Picasso.get().load(backupImageResourceId).
                            transform(new CircleTransform(circularFrameColor, circularFrameWidth)).into(viewToSet);
                } catch (Exception e1) {
                }
            }
        }
    }

    /**
     * Set a circular image into a view and set caching. Overloaded to allow for excluding
     * max cache size float
     *
     * @param urlThumbnail          URL String to use
     * @param viewToSet             View to set it into
     * @param backupImageResourceId Backup resource id in case the String url fails parsing
     * @param <T>                   {T extends View}
     */
    @CustomAnnotationsBase.RequiresDependency(requiresDependency = CustomAnnotationsBase.Dependencies.Picasso)
    public static <T extends ImageView> void setCircularImageWithPicasso(String urlThumbnail,
                                                                         final T viewToSet,
                                                                         final int backupImageResourceId) {
        ImageUtilities.setCircularImageWithPicasso(urlThumbnail, viewToSet, backupImageResourceId,
                 null, null);
    }

    //endregion

    //region Picasso Circular Images Without Caching
    /**
     * Set a circular image into a view and set caching. Overloaded to allow for excluding
     * max cache size float
     *
     * @param urlThumbnail          URL String to use
     * @param viewToSet             View to set it into
     * @param backupImageResourceId Backup resource id in case the String url fails parsing
     * @param <T>                   {T extends View}
     */
    @CustomAnnotationsBase.RequiresDependency(requiresDependency = CustomAnnotationsBase.Dependencies.Picasso)
    public static <T extends ImageView> void setCircularImageWithPicassoNoCache(String urlThumbnail,
                                                                                final T viewToSet,
                                                                                final int backupImageResourceId,
                                                                                final Integer circularFrameColor,
                                                                                final Integer circularFrameWidth) {
        if (StringUtilities.isNullOrEmpty(urlThumbnail)) {
            try {
                Picasso.get().load(backupImageResourceId).
                        transform(new CircleTransform()).into(viewToSet);

            } catch (Exception e) {
            }
        } else {

            final String innerUrlThumbnail = urlThumbnail;

            try {
                Picasso.get()
                        .load(innerUrlThumbnail)
                        .transform(new CircleTransform())
                        .into(viewToSet);
            } catch (Exception e) {
                try {
                    Picasso.get().load(backupImageResourceId).
                            transform(new CircleTransform()).into(viewToSet);
                } catch (Exception e1) {
                }
            }
        }
    }


    /**
     * Set a circular image into a view and set caching. Overloaded to allow for excluding
     * max cache size float
     *
     * @param urlThumbnail          URL String to use
     * @param viewToSet             View to set it into
     * @param backupImageResourceId Backup resource id in case the String url fails parsing
     * @param <T>                   {T extends View}
     */
    @CustomAnnotationsBase.RequiresDependency(requiresDependency = CustomAnnotationsBase.Dependencies.Picasso)
    public static <T extends ImageView> void setCircularImageWithPicassoNoCache(String urlThumbnail,
                                                                                final T viewToSet,
                                                                                final int backupImageResourceId) {
        setCircularImageWithPicassoNoCache(urlThumbnail, viewToSet, backupImageResourceId,
                null, null);
    }

    //endregion

    //region Picasso Images With Caching
    /**
     * Set an image into a view and set caching.
     *
     * @param urlThumbnail          URL String to use
     * @param viewToSet             View to set it into
     * @param backupImageResourceId Backup resource id in case the String url fails parsing
     * @param <T>                   {T extends View}
     */
    @CustomAnnotationsBase.RequiresDependency(requiresDependency = CustomAnnotationsBase.Dependencies.Picasso)
    public static <T extends ImageView> void setImageWithPicasso(String urlThumbnail,
                                                                 final T viewToSet,
                                                                 final int backupImageResourceId) {
        if (urlThumbnail == null) {
            urlThumbnail = "";
        }
        if (urlThumbnail.isEmpty() || urlThumbnail.equalsIgnoreCase("")) {
            viewToSet.setImageResource(backupImageResourceId);
        } else {
            final String innerUrlThumbnail = urlThumbnail;

            try {
                Picasso.get()
                        .load(urlThumbnail)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(viewToSet, new Callback() {
                            @Override
                            public void onSuccess() {
                                //Nothing, image is set
                            }

                            @Override
                            public void onError(Exception e) {
                                //Can trigger if image not in cache
                                Picasso.get().load(innerUrlThumbnail)
                                        .into(viewToSet, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                try {
                                                    List<String> toCache = new ArrayList<String>();
                                                    toCache.add(innerUrlThumbnail);
                                                    ImageUtilities.LoadImagesIntoPicassoCache async = new
                                                            ImageUtilities.LoadImagesIntoPicassoCache(toCache);
                                                    async.execute();
                                                } catch (Exception e2) {}
                                            }

                                            @Override
                                            public void onError(Exception e) {
                                                Picasso.get()
                                                        .load(backupImageResourceId)
                                                        .into(viewToSet);
                                            }
                                        });
                                //Load the image into cache for next time

                            }
                        });
            } catch (Exception e) {
                //L.m("catch caught in picasso setImageWithPicasso call");
                viewToSet.setImageResource(backupImageResourceId);
            }

        }
    }

    //endregion

    //region Picasso Images Without Caching
    /**
     * Set an image into a view and set caching. Overloaded to allow for excluding
     * max cache size float
     *
     * @param urlThumbnail          URL String to use
     * @param viewToSet             View to set it into
     * @param backupImageResourceId Backup resource id in case the String url fails parsing
     * @param <T>                   {T extends View}
     */
    @CustomAnnotationsBase.RequiresDependency(requiresDependency = CustomAnnotationsBase.Dependencies.Picasso)
    public static <T extends ImageView> void setImageWithPicassoNoCache(String urlThumbnail,
                                                                        final T viewToSet,
                                                                        final int backupImageResourceId) {
        if (urlThumbnail == null) {
            urlThumbnail = "";
        }
        if (urlThumbnail.isEmpty() || urlThumbnail.equalsIgnoreCase("")) {
            viewToSet.setImageResource(backupImageResourceId);
        } else {
            final String innerUrlThumbnail = urlThumbnail;
            try {
                Picasso.get()
                        .load(urlThumbnail)
                        .into(viewToSet);
            } catch (Exception e) {
                //L.m("catch caught in picasso setImageWithPicasso call");
                viewToSet.setImageResource(backupImageResourceId);
            }

        }
    }

    //endregion

    //region Picasso Remove Images from cache Async Method
    /**
     * Remove images from the Picasso cache. This runs on a background thread and does not return anything
     * @param imageUrlsToRemove
     */
    @CustomAnnotationsBase.RequiresDependency(requiresDependency = CustomAnnotationsBase.Dependencies.Picasso)
    public static void removeImageFromPicassoCache(List<String> imageUrlsToRemove){
        try {
            RemoveImagesFromPicassoCache async = new RemoveImagesFromPicassoCache(imageUrlsToRemove);
            async.execute();
        } catch (Exception e){
            L.m("Could not remove images from Picasso cache: " + e.getMessage());
        }
    }

    /**
     * Removes images from the picasso cache. Reference:
     * http://www.zoftino.com/android-picasso-image-downloading-and-caching-library-tutorial
     */
    @CustomAnnotationsBase.RequiresDependency(requiresDependency = CustomAnnotationsBase.Dependencies.Picasso)
    public static class RemoveImagesFromPicassoCache extends AsyncTask<Void, Void, Void> {
        private List<String> imageURLs;

        /**
         * Load Images into cache constructor
         *
         * @param imageURLs A list of the image URLs to set
         */
        public RemoveImagesFromPicassoCache(List<String> imageURLs) {
            this.imageURLs = imageURLs;
        }

        @Override
        protected Void doInBackground(Void... params) {
            for (String str : imageURLs) {
                try {
                    if (isCancelled()) {
                        return null;
                    }
                    Picasso.get().invalidate(str);
                    Thread.sleep(10);
                } catch (OutOfMemoryError e1) {
                    Picasso.get().invalidate(str);
                    return null;
                } catch (Exception e) {
                }
            }

            return null;
        }
    }

    //endregion

    //region Picasso Add / Load Images into the cache Async Method
    /**
     * Loads images into the picasso cache by using the fetch() call. Reference:
     * http://stackoverflow.com/questions/23978828/how-do-i-use-disk-caching-in-picasso
     */
    @CustomAnnotationsBase.RequiresDependency(requiresDependency = CustomAnnotationsBase.Dependencies.Picasso)
    public static class LoadImagesIntoPicassoCache extends AsyncTask<Void, Void, Void> {
        private List<String> imageURLs;

        /**
         * Load Images into cache constructor
         *
         * @param imageURLs A list of the image URLs to set
         */
        public LoadImagesIntoPicassoCache(List<String> imageURLs) {
            this.imageURLs = imageURLs;
        }

        @Override
        protected Void doInBackground(Void... params) {
            for (String str : imageURLs) {
                try {
                    if (isCancelled()) {
                        return null;
                    }

                    Picasso.get().load(str).fetch();
                    Thread.sleep(20);
                } catch (OutOfMemoryError e1) {
                    //If we run out of memory, make sure to catch it!
                    Picasso.get().invalidate(str);
                    return null;
                } catch (Exception e) {
                }
            }

            return null;
        }
    }

    //endregion

    //endregion
    
    //region Bitmap Operations

    /**
     * Adjust the photo orientation
     *
     * @param pathToFile
     * @return
     */
    public static Bitmap adjustPhotoOrientation(String pathToFile) {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(pathToFile);
            ExifInterface exif = new ExifInterface(pathToFile);
            int exifOrientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            int rotate = 0;

            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
            }
            if (rotate != 0) {
                int w = bitmap.getWidth();
                int h = bitmap.getHeight();

                // Setting pre rotateImage
                Matrix mtx = new Matrix();
                mtx.preRotate(rotate);

                // Rotating Bitmap & convert to ARGB_8888, required by tess
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
                bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Rotates an image 90 degrees counter-clockwise
     *
     * @param bitmap Bitmap to rotateImage
     * @return
     */
    public static Bitmap rotateImageCounterClockwise(@NonNull Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        return rotateImage(bitmap, 270);
    }

    /**
     * Rotates an image 90 degrees clockwise
     *
     * @param bitmap Bitmap to rotateImage
     * @return
     */
    public static Bitmap rotateImageClockwise(@NonNull Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        return rotateImage(bitmap, 90);
    }

    /**
     * For rotating images
     *
     * @param bitmap Bitmap to rotateImage
     * @param degree Degree amount to rotateImage. If <0 or >360, will set to 90 degrees.
     *               (Range is 0-360)
     * @return
     */
    public static Bitmap rotateImage(@NonNull Bitmap bitmap,
                                     @IntRange(from = 0, to = 360) int degree) {
        if (bitmap == null) {
            return null;
        }
        //Shouldn't be possible, but, you know... reasons.
        degree = (degree < 0 || degree > 360) ? 90 : degree;
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        mtx.setRotate(degree);

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    /**
     * Converts an inputstream to a byte array (Mostly useful for sending images via JSON)
     *
     * @param is Input stream, if using a URI, open it by calling:
     *           InputStream iStream =   context.getContentResolver().openInputStream(uri);
     * @return Byte Array
     */
    public static Bitmap convertISToBitmap(InputStream is) {
        try {
            return BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Convert a Bitmap into a byte Array
     *
     * @param bitmap
     * @return
     */
    public static byte[] convertBitmapToByte(Bitmap bitmap) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            return byteArray;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Combines multiple bitmaps together into one
     *
     * @param aParts An array of bitmaps
     * @return Returns a bitmap image
     */
    public static Bitmap combineBitmaps(Bitmap[] aParts) {
        Bitmap[] parts = aParts;
        Bitmap result = Bitmap.createBitmap(parts[0].getWidth() * 2, parts[0].getHeight() * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        for (int i = 0; i < parts.length; i++) {
            canvas.drawBitmap(parts[i], parts[i].getWidth() * (i % 2), parts[i].getHeight() * (i / 2), paint);
        }
        return result;
    }

    /**
     * Decode a bitmap from a resource
     *
     * @param res
     * @param resId
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static String convertBitmapToBase64String(@NonNull Bitmap bitmap){
        return ImageUtilities.convertBitmapToBase64String(bitmap, ImageMimeType.PNG);
    }

    public static String convertBitmapToBase64String(@NonNull Bitmap bitmap, @Nullable ImageMimeType mimeType){
        if(bitmap == null){
            return null;
        }
        try {
            if(mimeType == null){
                mimeType = ImageMimeType.PNG;
            }
            Bitmap.CompressFormat c;
            switch (mimeType){
                case JPEG:
                    c = Bitmap.CompressFormat.JPEG;
                    break;
                default:
                case GIF:
                case UNKNOWN:
                case PNG:
                    c = Bitmap.CompressFormat.PNG;
                    break;
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(c, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (Exception e){
            return null;
        }
    }

    public static String convertBitmapToBase64String(@NonNull Bitmap bitmap, @Nullable Bitmap.CompressFormat compressFormat){
        if(bitmap == null){
            return null;
        }
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(((compressFormat == null) ? Bitmap.CompressFormat.PNG : compressFormat), 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (Exception e){
            return null;
        }
    }

    /**
     * Convert a base64String into a bitmap
     * @param base64Image Base64 String
     * @return {@link Bitmap}
     */
    public static Bitmap convertBase64StringToBitmap(@NonNull String base64Image){
        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            if(bitmap == null){
                throw new Exception();
            }
            return bitmap;
        } catch (Exception e){
            e.printStackTrace();
            try {
                final String pureBase64Encoded = base64Image.substring(base64Image.indexOf(",")  + 1);
                byte[] decodedString1 = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
                bitmap = BitmapFactory.decodeByteArray(decodedString1, 0, decodedString.length);
                return bitmap;
            } catch (Exception ee){
                ee.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Download a file from the web. This defaults to a photo (png)
     */
    public static class DownloadImageFromWeb extends AsyncTask<Void, Integer, File> {

        private Context context;
        private String imageUrl;
        private OnTaskCompleteListener listener;
        private ProgressDialog dialog;
        private long lengthOfTimeToDelay, startTime, endTime;

        /**
         * Download an image from the web into a file and send that file back via the listener
         *
         * @param context
         * @param imageUrl            String image Url
         * @param dialog              Progress dialog to show
         * @param listener            listener to send data back on
         * @param lengthOfTimeToDelay Length of time to 'delay' in that if the time to download
         *                            the file is shorter than this, it will add time on the
         *                            progress dialog to allow time to finish. This is best used
         *                            when you have a "download animation" that you want seen, your
         *                            internet is too fast, and you don't see the animation.
         */
        public DownloadImageFromWeb(Context context, String imageUrl, ProgressDialog dialog,
                                    OnTaskCompleteListener listener, Long lengthOfTimeToDelay) {
            this.context = context;
            this.imageUrl = imageUrl;
            this.listener = listener;
            this.dialog = dialog;
            if (this.dialog == null) {
                //Removed on 2017-07-05 Due to problems with compiling
                //this.dialog = PGMacCustomProgressBar.buildElasticDialog(context);
                this.dialog = new ProgressDialog(context);
            }
            if (lengthOfTimeToDelay == null) {
                lengthOfTimeToDelay = (long) (PGMacTipsConstants.ONE_SECOND * 2.6);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog.show();
        }

        @Override
        protected File doInBackground(Void... params) {
            File file = FileUtilities.generateFileForImage(context, null, null);
            int count;
            this.startTime = DateUtilities.getCurrentDateLong();
            try {
                URL url = new URL(imageUrl);
                URLConnection conexion = url.openConnection();
                conexion.connect();
                int lenghtOfFile = conexion.getContentLength();
                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(file);
                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    int publishNum = ((int) ((total * 100 / lenghtOfFile)));
                    publishProgress(publishNum);
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
                return file;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (values[0] != null) {
                int prog = values[0];
                if (prog % 10 == 0) {
                    if (prog == 100) {
                        prog = 99;
                    }
                    dialog.setProgress(prog);
                }
            }
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            final File file1 = file;
            this.endTime = DateUtilities.getCurrentDateLong();
            long totalTime = (endTime - startTime);
            long timeLeft = lengthOfTimeToDelay - totalTime;
            Handler handler = new Handler();

            if (file == null) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            dialog.dismiss();
                        } catch (Exception e) {
                        }
                    }
                }, PGMacTipsConstants.ONE_SECOND);
                listener.onTaskComplete("Url Error", PGMacTipsConstants.TAG_PHOTO_BAD_URL);

            } else {
                dialog.setProgress(100);
                if (timeLeft <= 0) {
                    try {
                        dialog.dismiss();
                        listener.onTaskComplete(file1, PGMacTipsConstants.TAG_FILE_DOWNLOADED);
                    } catch (Exception e) {
                    }
                } else {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                dialog.dismiss();
                                listener.onTaskComplete(file1, PGMacTipsConstants.TAG_FILE_DOWNLOADED);
                            } catch (Exception e) {
                            }
                        }
                    }, (int) timeLeft);
                }
            }
        }
    }

    /**
     * Determine if a bitmap is too large as compared to passed param
     * @param bmp Bitmap to check
     * @param desiredSizeInBytes Desired size (in Bytes) to check against
     * @return boolean, if true, bitmap is larger than the desired size, else, it is not.
     */
    public static boolean isImageTooLarge(@NonNull Bitmap bmp, long desiredSizeInBytes){
        long bitmapSize = (bmp.getRowBytes() * bmp.getHeight());
        float shrinkFactor = desiredSizeInBytes / bitmapSize;
        if(shrinkFactor >= 1){
            return false;
        } else {
            return true;
        }
    }

    /**
     * Determine the float value needed to resize the image so that it is less in size (Bytes)
     * than the value passed
     * @param bmp Bitmap to check
     * @param desiredSizeInBytes Desired size in bytes of the image
     * @return float value to resize. IE, if 0.34 is returned, the bitmap in question needs
     *         to be shrunk down by 34% to reach the desired size
     */
    public static float getImageResizeFactor(@NonNull Bitmap bmp, long desiredSizeInBytes){
        long bitmapSize = (bmp.getRowBytes() * bmp.getHeight());
        return (desiredSizeInBytes / bitmapSize);
    }

    /**
     * Resize a photo
     * @param bmp Bitmap to resize
     * @param factorToDivide Factor to divide by. if (IE) 2 is passed, it will cut the
     *                       image in half, 10 will cut it down 10x in size. Note that
     *                       scaling too much will result in geometric size jumps.
     * @return Resized bitmap. If it fails, will send back original
     */
    public static Bitmap resizePhoto(@NonNull Bitmap bmp, int factorToDivide){
        if(factorToDivide <= 1){
            factorToDivide = 2;
        }
        try {
            return Bitmap.createScaledBitmap(bmp, (int)(bmp.getWidth() / factorToDivide),
                    (int)(bmp.getHeight() / factorToDivide), true);
        } catch (Exception e){
            return bmp;
        }
    }

    /**
     * Resize a photo
     * @param bmp Bitmap to resize
     * @param desiredImageSizeInBytes The desired image size in bytes. IE, sending 5000000
     *                                (5 Million) would be a 5 Megapixel (MP) image.
     * @return Resized bitmap. If it fails, will send back original
     */
    public static Bitmap resizePhoto(@NonNull Bitmap bmp, long desiredImageSizeInBytes){
        try {
            double flt = (double) desiredImageSizeInBytes;
            double height = Math.sqrt(flt /
                    (((double) bmp.getWidth()) / bmp.getHeight()));
            double width = (height / bmp.getHeight()) * bmp.getWidth();
            return Bitmap.createScaledBitmap(bmp, (int)(width),
                    (int)(height), true);
        } catch (Exception e){
            e.printStackTrace();
            return bmp;
        }
    }

    /**
     * Decode a file from the path and return a bitmap
     *
     * @param uri
     * @param context
     * @return
     */
    public static Bitmap decodeFileFromPath(Uri uri, Context context) {

        InputStream in = null;
        try {
            in = context.getContentResolver().openInputStream(uri);

            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            BitmapFactory.decodeStream(in, null, o);
            in.close();


            int scale = 1;
            int inSampleSize = 1024;
            if (o.outHeight > inSampleSize || o.outWidth > inSampleSize) {
                scale = (int) Math.pow(2, (int) Math.round(Math.log(inSampleSize / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            in = context.getContentResolver().openInputStream(uri);
            Bitmap b = BitmapFactory.decodeStream(in, null, o2);
            in.close();

            return b;

        } catch (FileNotFoundException e) {
            //e.printStackTrace();
        } catch (IOException e) {
            //e.printStackTrace();
        }
        return null;
    }

    //endregion
    
    //region Drawable Operations
    
    /**
     * Convert a Drawable to a Bitmap
     * @param drawableResId Drawable resource ID (IE: R.drawable.something)
     * @return Converted Bitmap
     */
    public static Bitmap convertDrawableToBitmap(@Nonnull Context context, int drawableResId){
        try {
            return convertDrawableToBitmap(ContextCompat.getDrawable(context, drawableResId));
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Convert a Drawable to a Bitmap
     * @param drawable Drawable to convert
     * @return Converted Bitmap
     */
    public static Bitmap convertDrawableToBitmap(@Nonnull Drawable drawable){
        if(drawable == null){
            return null;
        }
        Bitmap bitmap = null;
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        
        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }
        
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
    
    /**
     * Set the drawable to a specific color and return it
     *
     * @param drawable   The drawable to change
     * @param colorToSet The color to set it to
     * @return Drawable
     * @throws NullPointerException, if it fails, throws a null pointer
     */
    public static Drawable colorDrawable(@NonNull Context context, @NonNull Drawable drawable, int colorToSet) {
        try {
            drawable.mutate().setColorFilter(ContextCompat.getColor(context, colorToSet), PorterDuff.Mode.SRC_ATOP);
            return drawable;
        } catch (Exception e) {
            try {
                drawable.mutate().setColorFilter(colorToSet, PorterDuff.Mode.SRC_ATOP);
                return drawable;
            } catch (Exception e1){
                return drawable;
            }
        }
    }

    /**
     * Set the drawable to a specific color and return it
     *
     * @param drawableId the int ID of the drawable to change
     * @param colorToSet The color to set it to
     * @return Drawable
     * @throws NullPointerException, if it fails, throws a null pointer
     */
    public static Drawable colorDrawable(@NonNull Context context, int drawableId, int colorToSet) {
        try {
            Drawable drawable = ContextCompat.getDrawable(context, drawableId);
            drawable.mutate().setColorFilter(ContextCompat.getColor(context, colorToSet), PorterDuff.Mode.SRC_ATOP);
            return drawable;
        } catch (Exception e) {
            try {
                Drawable drawable = ContextCompat.getDrawable(context, drawableId);
                drawable.mutate().setColorFilter(colorToSet, PorterDuff.Mode.SRC_ATOP);
                return drawable;
            } catch (Exception e1){
                return null;
            }
        }
    }
    
    /**
     * Adjust the drawable color. Note that for SVG Vector Drawables, it is recommended to use
     * {@link ImageUtilities#changeViewColor(Context, ImageView, int)}
     *
     * @param context    Context
     * @param drawableId Drawable ID, IE, R.drawable.your_icon or R.mipmap.your_icon
     * @param colorId    The color ID, IE, R.color.black or android.R.color.red
     * @return Augmented {@link Drawable}
     * @throws android.content.res.Resources.NotFoundException If the resources cannot be found, throws exception
     */
    public static Drawable changeDrawableColor(@NonNull Context context,
                                               int drawableId,
                                               int colorId) throws android.content.res.Resources.NotFoundException {
        if (context == null) {
            return null;
        }
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable == null) {
            throw new Resources.NotFoundException("Drawable not found");
        }
        int color;
        try {
            color = ContextCompat.getColor(context, colorId);
        } catch (Resources.NotFoundException nfe){
            color = colorId;
        }
        // TODO: 4/10/19 this mutate() call may need to be in the original instantiation
        drawable = drawable.mutate();
        drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP));
        return drawable;
    }
    
    /**
     * Adjust the drawable color. Note that for SVG Vector Drawables, it is recommended to use
     * {@link ImageUtilities#changeViewColor(Context, ImageView, int)}
     *
     * @param context    Context
     * @param drawableId Drawable ID, IE, R.drawable.your_icon or R.mipmap.your_icon
     * @param hexColor   The hex color to use, IE, #ffffff or #f0f0f0
     * @return Augmented {@link Drawable}
     * @throws android.content.res.Resources.NotFoundException If the resources cannot be found, throws exception
     */
    public static Drawable changeDrawableColor(@NonNull Context context,
                                               int drawableId,
                                               @NonNull String hexColor) throws android.content.res.Resources.NotFoundException {
        if (context == null) {
            return null;
        }
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable == null) {
            throw new Resources.NotFoundException("Drawable not found");
        }
        int colorId;
        try {
            colorId = Color.parseColor(hexColor);
        } catch (IllegalArgumentException ile) {
            //May change this logic in the future, changing it from an ile to a resource exception so the end-user only has to account for one exception
            throw new Resources.NotFoundException("The Hex color passed could not be parsed");
        }
        // TODO: 4/10/19 this mutate() call may need to be in the original instantiation
        drawable = drawable.mutate();
        drawable.setColorFilter(new PorterDuffColorFilter(colorId, PorterDuff.Mode.SRC_ATOP));
        return drawable;
    }
    
    //endregion

    //region ValueAnimator (Color Changing Animation of an Image View)
    
    /**
     * Build an Image Color Animator {@link ValueAnimator}
     * Code pulled from - https://medium.com/@ali.muzaffar/android-change-colour-of-drawable-asset-programmatically-with-animation-e42ca595fabb
     *
     * @param imageView The imageview to adjust
     * @param context   The context
     * @param colorId   The color (IR  R.color.black)
     * @return {@link ValueAnimator} object. to start the animation / operation, call
     * {@link ValueAnimator#start()}
     */
    public static ValueAnimator buildImageColorAnimator(@NonNull final ImageView imageView,
                                                        @NonNull Context context, int colorId) {
        return buildImageColorAnimator(imageView, context, colorId,
                null, null, null);
    }
    
    /**
     * Build an Image Color Animator {@link ValueAnimator}
     * Code pulled from - https://medium.com/@ali.muzaffar/android-change-colour-of-drawable-asset-programmatically-with-animation-e42ca595fabb
     *
     * @param imageView                         The imageview to adjust
     * @param context                           The context
     * @param colorId                           The color (IR  R.color.black)
     * @param updateListener                    Optional update listener once the animation is complete. Note, if this
     *                                          is customized, it is recommended that you add in this code:
     *                                          ```
     *                                          float multiplier = (Float) animation.getAnimatedValue();
     *                                          int alphaColor = adjustAlpha(color, multiplier);
     *                                          imageView.setColorFilter(alphaColor, PorterDuff.Mode.SRC_ATOP);
     *                                          if(multiplier == 0.0){
     *                                          imageView.setColorFilter(null);
     *                                          }
     *                                          ```
     * @param durationOfAnimationInMilliseconds Number of milliseconds for animation to take place.
     *                                          If null or zero, no animation will take place
     * @param animationRepeatCount              Number of times the animation should repeat
     * @return {@link ValueAnimator} object. to start the animation / operation, call
     * {@link ValueAnimator#start()}
     */
    public static ValueAnimator buildImageColorAnimator(@NonNull final ImageView imageView,
                                                        @NonNull Context context, int colorId,
                                                        @Nullable ValueAnimator.AnimatorUpdateListener updateListener,
                                                        @Nullable Long durationOfAnimationInMilliseconds,
                                                        @Nullable Integer animationRepeatCount) {
        if (imageView == null || context == null) {
            return null;
        }
        int color1;
        try {
            color1 = ContextCompat.getColor(context, colorId);
        } catch (Resources.NotFoundException nfe) {
            color1 = colorId;
        }
        final int color = color1;
        final ValueAnimator valueAnimator = ObjectAnimator.ofFloat(0F, 1F);
        if (updateListener == null) {
            updateListener = new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        float multiplier = (Float) animation.getAnimatedValue();
                        int alphaColor = adjustAlpha(color, multiplier);
                        imageView.setColorFilter(alphaColor, PorterDuff.Mode.SRC_ATOP);
                        if (multiplier == 0.0) {
                            imageView.setColorFilter(null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
        }
        valueAnimator.addUpdateListener(updateListener);
        if (durationOfAnimationInMilliseconds == null) {
            durationOfAnimationInMilliseconds = -1L;
        }
        if (durationOfAnimationInMilliseconds <= 0) {
            valueAnimator.setDuration(0);
            valueAnimator.setRepeatCount(-1);
            valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
            return valueAnimator;
        }
        valueAnimator.setDuration(durationOfAnimationInMilliseconds);
        if (animationRepeatCount == null) {
            animationRepeatCount = -1;
        }
        valueAnimator.setRepeatCount(animationRepeatCount);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        return valueAnimator;
    }
    
    //endregion
    
    //region ImageView and TextView Color Changing
    

    
    /**
     * Adjust the drawable color. This uses the setTint() logic and requires an API of 21 or
     * higher to work. Note that this is ideal for augmenting the SVG Vector Drawables
     *
     * @param context Context
     * @param iv      the ImageView you are using
     * @param colorId The color ID, IE, R.color.black or android.R.color.red
     * @return Augmented {@link Drawable}
     * @throws android.content.res.Resources.NotFoundException If the resources cannot be found, throws exception
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static Drawable changeViewColor(@NonNull Context context,
                                           @NonNull ImageView iv,
                                           int colorId) throws android.content.res.Resources.NotFoundException {
        if (context == null || iv == null) {
            return null;
        }
        Drawable drawable = iv.getDrawable();
        if (drawable == null) {
            drawable = new ColorDrawable();
        }
        int color;
        try {
            color = ContextCompat.getColor(context, colorId);
        } catch (Resources.NotFoundException nfe){
            color = colorId;
        }
        drawable.setTint(color);
        return drawable;
    }
    
    /**
     * Adjust the drawable color. This uses the setTint() logic and requires an API of 21 or
     * higher to work. Note that this is ideal for augmenting the SVG Vector Drawables
     *
     * @param context  Context
     * @param iv       the ImageView you are using
     * @param hexColor The hex color to use, IE, #ffffff or #f0f0f0
     * @return Augmented {@link Drawable}
     * @throws android.content.res.Resources.NotFoundException If the resources cannot be found, throws exception
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static Drawable changeViewColor(@NonNull Context context,
                                           @NonNull ImageView iv,
                                           String hexColor) throws android.content.res.Resources.NotFoundException {
        if (context == null || iv == null) {
            return null;
        }
        Drawable drawable = iv.getDrawable();
        if (drawable == null) {
            drawable = new ColorDrawable();
        }
        int colorId;
        try {
            colorId = Color.parseColor(hexColor);
        } catch (IllegalArgumentException ile) {
            //May change this logic in the future, changing it from an ile to a resource exception so the end-user only has to account for one exception
            throw new Resources.NotFoundException("The Hex color passed could not be parsed");
        }
        drawable.setTint(colorId);
        return drawable;
    }
    
    //endregion
    
    //region Misc Operations

    /**
     * Convert an image to a byte array
     *
     * @param uri
     * @param context
     * @return
     */
    public static byte[] convertImageToByte(Uri uri, Context context) {
        byte[] data = null;
        try {
            ContentResolver cr = context.getContentResolver();
            InputStream inputStream = cr.openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            data = baos.toByteArray();
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e2) {
                }
            }
            if (baos != null) {
                try {
                    baos.close();
                } catch (Exception e2) {
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return data;
    }


    /**
     * TBD in the future
     */
    private static void zoomAView() {
        //https://developer.android.com/training/animation/zoom.html
    }

    //endregion
    
    //region Alpha Alterations
    
    /**
     * Adjust the alpha of a color by the factor passed
     *
     * @param color  Color to adjust
     * @param factor Factor to adjust it by. If 0 is passed, means the overlay is transparent.
     *               If 1 is passed, means the value is the actual color hex code
     * @return Adjusted color int
     */
    public static int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }
    
    //endregion
    
    //region Base64 Encoding

    /**
     * Encode a Bitmap to a base 64 String
     *
     * @param bm The Bitmap to convert
     * @return Base 64 Encoded String
     */
    public static String encodeImage(Bitmap bm) {
        if (bm == null) {
            return null;
        }

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();
            String encImage = Base64.encodeToString(b, Base64.DEFAULT);
            return encImage;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Encode an image on a background thread
     *
     * @param listener {@link OnTaskCompleteListener}
     * @param bm       Bitmap to convert
     */
    public static void encodeImage(@NonNull final OnTaskCompleteListener listener,
                                   final Bitmap bm) {
        if (bm == null) {
            listener.onTaskComplete(null, PGMacTipsConstants.TAG_BASE64_IMAGE_ENCODE_FAIL);
            return;
        }
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] b = baos.toByteArray();
                    String encImage = Base64.encodeToString(b, Base64.DEFAULT);
                    return encImage;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String str) {
                if (!StringUtilities.isNullOrEmpty(str)) {
                    listener.onTaskComplete(str,
                            PGMacTipsConstants.TAG_BASE64_IMAGE_ENCODE_SUCCESS);
                } else {
                    listener.onTaskComplete(null,
                            PGMacTipsConstants.TAG_BASE64_IMAGE_ENCODE_FAIL);
                }
            }
        }.execute();
    }

    /**
     * Encode an image to a base 64 String
     *
     * @param file The image File to convert
     * @return Base 64 Encoded String
     */
    public static String encodeImage(File file) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            Bitmap bm = BitmapFactory.decodeStream(fis);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (bm == null) {
                return null;
            }
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();
            String encImage = Base64.encodeToString(b, Base64.DEFAULT);
            //Base64.de
            return encImage;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * Encode an image to a base64 String on a background thread
     *
     * @param listener {@link OnTaskCompleteListener}
     * @param file     The File to convert
     */
    public static void encodeImage(@NonNull final OnTaskCompleteListener listener,
                                   final File file) {
        if (file == null) {
            listener.onTaskComplete(null, PGMacTipsConstants.TAG_BASE64_IMAGE_ENCODE_FAIL);
            return;
        }
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                try {
                    Bitmap bm = BitmapFactory.decodeStream(fis);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    if (bm == null) {
                        return null;
                    }
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] b = baos.toByteArray();
                    String encImage = Base64.encodeToString(b, Base64.DEFAULT);
                    //Base64.de
                    return encImage;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String str) {
                if (!StringUtilities.isNullOrEmpty(str)) {
                    listener.onTaskComplete(str,
                            PGMacTipsConstants.TAG_BASE64_IMAGE_ENCODE_SUCCESS);
                } else {
                    listener.onTaskComplete(null,
                            PGMacTipsConstants.TAG_BASE64_IMAGE_ENCODE_FAIL);
                }
            }
        }.execute();
    }

    /**
     * Encode an image to a base 64 String
     *
     * @param path The path String to the image file
     * @return Base 64 Encoded String
     */
    public static String encodeImage(String path) {
        File imagefile = new File(path);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(imagefile);
            Bitmap bm = BitmapFactory.decodeStream(fis);
            if (bm == null) {
                return null;
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();
            String encImage = Base64.encodeToString(b, Base64.DEFAULT);
            return encImage;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * Encode an image to a base 64 String on a background thread
     *
     * @param listener {@link OnTaskCompleteListener}
     * @param path     The Path String to the image
     */
    public static void encodeImage(@NonNull final OnTaskCompleteListener listener,
                                   final String path) {
        if (StringUtilities.isNullOrEmpty(path)) {
            listener.onTaskComplete(null, PGMacTipsConstants.TAG_BASE64_IMAGE_ENCODE_FAIL);
            return;
        }
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                File imagefile = new File(path);
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(imagefile);
                    Bitmap bm = BitmapFactory.decodeStream(fis);
                    if (bm == null) {
                        return null;
                    }
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] b = baos.toByteArray();
                    String encImage = Base64.encodeToString(b, Base64.DEFAULT);
                    return encImage;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String str) {
                if (!StringUtilities.isNullOrEmpty(str)) {
                    listener.onTaskComplete(str,
                            PGMacTipsConstants.TAG_BASE64_IMAGE_ENCODE_SUCCESS);
                } else {
                    listener.onTaskComplete(null,
                            PGMacTipsConstants.TAG_BASE64_IMAGE_ENCODE_FAIL);
                }
            }
        }.execute();
    }

    /**
     * Determine the Mime Type of an image from the base64 String
     * @param bitmap Bitmap image to determine type from
     * @return {@link ImageMimeType}
     */
    public static ImageMimeType determineImageMimeType(@NonNull Bitmap bitmap){
        if(bitmap == null){
            return ImageMimeType.UNKNOWN;
        }
        try {
            return ImageUtilities.determineImageMimeType(ImageUtilities.convertBitmapToBase64String(bitmap));
        } catch (Exception e){
            return ImageMimeType.UNKNOWN;
        }
    }

    /**
     * Determine the Mime Type of an image from the base64 String
     * @param base64Image Base64 String of an image
     * @return {@link ImageMimeType}
     */
    public static ImageMimeType determineImageMimeType(@NonNull String base64Image){
        if(StringUtilities.isNullOrEmpty(base64Image)){
            return ImageMimeType.UNKNOWN;
        }
        if(base64Image.charAt(0)=='/'){
            return ImageMimeType.JPEG;
        }else if(base64Image.charAt(0)=='R'){
            return ImageMimeType.GIF;
        }else if(base64Image.charAt(0)=='i'){
            return ImageMimeType.PNG;
        } else {
            return ImageMimeType.UNKNOWN;
        }
    }

    //endregion

    //region Image Getter Utilities

    /**
     * Get the Left, Top, Right, and Bottom sides of an image.
     * @param topLeftX topLeft X position (from x,y coordinates)
     * @param topLeftY topLeft Y position (from x,y coordinates)
     * @param bottomLeftX bottomLeft X position (from x,y coordinates)
     * @param bottomLeftY bottomRight Y position (from x,y coordinates)
     * @param topRightX topRight X position (from x,y coordinates)
     * @param topRightY topRight Y position (from x,y coordinates)
     * @param bottomRightX bottomRight X position (from x,y coordinates)
     * @param bottomRightY bottomRight Y position (from x,y coordinates)
     * @return
     */
    public static int[] getLTRBSidesOfImage(int topLeftX, int topLeftY,
                                            int bottomLeftX, int bottomLeftY,
                                            int topRightX, int topRightY,
                                            int bottomRightX, int bottomRightY){
        if(topLeftX < 0 || topRightX < 0 || topLeftY < 0 || topRightY < 0 || bottomLeftX < 0 ||
                bottomLeftY < 0 || bottomRightX < 0 || bottomRightY < 0) {
            L.m(INVALID_PIXEL_POSITIONS);
            return null;
        }
        return new int[]{
                ImageUtilities.getLeftSideXCoord(topLeftX, bottomLeftX),
                ImageUtilities.getTopSideYCoord(topLeftY, topRightY),
                ImageUtilities.getRightSideXCoord(topRightX, bottomRightX),
                ImageUtilities.getBottomSideYCoord(bottomLeftY, bottomRightY)
        };
    }

    private static int getLeftSideXCoord(int topLeftX, int bottomLeftX){
        return (topLeftX < bottomLeftX) ? topLeftX : bottomLeftX;
    }

    private static int getRightSideXCoord(int topRightX, int bottomRightX){
        return (topRightX > bottomRightX) ? topRightX : bottomRightX;
    }

    private static int getTopSideYCoord(int topLeftY, int topRightY){
        return (topLeftY < topRightY) ? topLeftY : topRightY;
    }

    private static int getBottomSideYCoord(int bottomLeftY, int bottomRightY){
        return (bottomLeftY > bottomRightY) ? bottomLeftY : bottomRightY;
    }

    //endregion

    //region Image Cropping

    /**
     * Crop an image to the positions passed. If any # <0 is passed or an error occurs, it will
     * return the original bitmap passed
     * @param imageToBeCropped Image to be cropped
     * @param topLeftX topLeft X position (from x,y coordinates)
     * @param topLeftY topLeft Y position (from x,y coordinates)
     * @param bottomLeftX bottomLeft X position (from x,y coordinates)
     * @param bottomLeftY bottomRight Y position (from x,y coordinates)
     * @param topRightX topRight X position (from x,y coordinates)
     * @param topRightY topRight Y position (from x,y coordinates)
     * @param bottomRightX bottomRight X position (from x,y coordinates)
     * @param bottomRightY bottomRight Y position (from x,y coordinates)
     * @return Cropped image. Will return same passed bitmap if operation fails
     */
    public static Bitmap cropImage(@NonNull Bitmap imageToBeCropped,
                                   int topLeftX, int topLeftY,
                                   int bottomLeftX, int bottomLeftY,
                                   int topRightX, int topRightY,
                                   int bottomRightX, int bottomRightY){
        if(topLeftX < 0 || topRightX < 0 || topLeftY < 0 || topRightY < 0 || bottomLeftX < 0 ||
                bottomLeftY < 0 || bottomRightX < 0 || bottomRightY < 0){
            L.m(INVALID_PIXEL_POSITIONS);
            return imageToBeCropped;
        }

        return cropImage(imageToBeCropped, ImageUtilities.getLeftSideXCoord(topLeftX, bottomLeftX),
                ImageUtilities.getTopSideYCoord(topLeftY, topRightY),
                ImageUtilities.getRightSideXCoord(topRightX, bottomRightX),
                ImageUtilities.getBottomSideYCoord(bottomLeftY, bottomRightY));
    }

    /**
     * Crop an image to the positions passed. If any # <0 is passed or an error occurs, it will
     * return the original bitmap passed
     * @param imageToBeCropped Image to be cropped
     * @param topLeftX topLeft X position (from x,y coordinates)
     * @param topLeftY topLeft Y position (from x,y coordinates)
     * @param bottomLeftX bottomLeft X position (from x,y coordinates)
     * @param bottomLeftY bottomRight Y position (from x,y coordinates)
     * @param topRightX topRight X position (from x,y coordinates)
     * @param topRightY topRight Y position (from x,y coordinates)
     * @param bottomRightX bottomRight X position (from x,y coordinates)
     * @param bottomRightY bottomRight Y position (from x,y coordinates)
     * @param sidesBufferPercent Float percentage to allow buffer on sides for cropping. IE, sending
     *                           in 0.10 would add a 10% buffer on each side to 'zoom out' on
     *                           the cropped image. Useful if you want some extra side room
     * @return Cropped image. Will return same passed bitmap if operation fails
     */
    public static Bitmap cropImage(@NonNull Bitmap imageToBeCropped,
                                   int topLeftX, int topLeftY,
                                   int bottomLeftX, int bottomLeftY,
                                   int topRightX, int topRightY,
                                   int bottomRightX, int bottomRightY,
                                   @Nullable Float sidesBufferPercent){
        if(topLeftX < 0 || topRightX < 0 || topLeftY < 0 || topRightY < 0 || bottomLeftX < 0 ||
                bottomLeftY < 0 || bottomRightX < 0 || bottomRightY < 0){
            L.m(INVALID_PIXEL_POSITIONS);
            return imageToBeCropped;
        }

        return cropImage(imageToBeCropped, ImageUtilities.getLeftSideXCoord(topLeftX, bottomLeftX),
                ImageUtilities.getTopSideYCoord(topLeftY, topRightY),
                ImageUtilities.getRightSideXCoord(topRightX, bottomRightX),
                ImageUtilities.getBottomSideYCoord(bottomLeftY, bottomRightY), sidesBufferPercent);
    }

    /**
     * Crop an image to the positions passed. If any # <0 is passed or an error occurs, it will
     * return the original bitmap passed
     * @param imageToBeCropped Image to be cropped
     * @param startingXPos Starting X position (left side of the image)
     * @param startingYPos Starting Y position (Top side of the image)
     * @param endingXPos Ending X position (Right side of the image)
     * @param endingYPos Ending Y position (Bottom side of the image)
     * @return Cropped image. Will return same passed bitmap if operation fails
     */
    public static Bitmap cropImage(@NonNull Bitmap imageToBeCropped,
                                   int startingXPos, int startingYPos,
                                   int endingXPos, int endingYPos){
        if(startingYPos < 0 || startingXPos < 0 || endingYPos < 0 || endingXPos < 0){
            L.m(INVALID_PIXEL_POSITIONS);
            return imageToBeCropped;
        }
        try {
            return Bitmap.createBitmap(imageToBeCropped, startingXPos, startingYPos,
                    (endingXPos - startingXPos), (endingYPos - startingYPos));
        } catch (IllegalArgumentException ile){
            ile.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        return imageToBeCropped;
    }

    /**
     * Crop an image to the positions passed. If any # <0 is passed or an error occurs, it will
     * return the original bitmap passed
     * @param imageToBeCropped Image to be cropped
     * @param startingXPos Starting X position (left side of the image)
     * @param startingYPos Starting Y position (Top side of the image)
     * @param endingXPos Ending X position (Right side of the image)
     * @param endingYPos Ending Y position (Bottom side of the image)
     * @param sidesBufferPercent Float percentage to allow buffer on sides for cropping. IE, sending
     *                           in 0.10 would add a 10% buffer on each side to 'zoom out' on
     *                           the cropped image. Useful if you want some extra side room
     * @return Cropped image. Will return same passed bitmap if operation fails
     */
    public static Bitmap cropImage(@NonNull Bitmap imageToBeCropped,
                                   int startingXPos, int startingYPos,
                                   int endingXPos, int endingYPos,
                                   @Nullable Float sidesBufferPercent){
        if(startingYPos < 0 || startingXPos < 0 || endingYPos < 0 || endingXPos < 0){
            L.m(INVALID_PIXEL_POSITIONS);
            return imageToBeCropped;
        }
        if(sidesBufferPercent == null){
            sidesBufferPercent = 0F;
        }
        if(sidesBufferPercent < 0 || sidesBufferPercent >= 1){
            sidesBufferPercent = 0F;
        }
        startingXPos = (int)(startingXPos - (startingXPos * sidesBufferPercent));
        endingXPos = (int)(endingXPos + (endingXPos * sidesBufferPercent));
        startingYPos = (int)(startingYPos - (startingYPos * sidesBufferPercent));
        endingYPos = (int)(endingYPos + (endingYPos * sidesBufferPercent));

        startingXPos = (startingXPos < 0) ? 0 : startingXPos;
        startingYPos = (startingYPos < 0) ? 0 : startingYPos;
        endingXPos = (endingXPos > imageToBeCropped.getWidth()) ?
                imageToBeCropped.getWidth() : endingXPos;
        endingYPos = (endingYPos > imageToBeCropped.getHeight()) ?
                imageToBeCropped.getHeight() : endingYPos;

        return ImageUtilities.cropImage(imageToBeCropped,
                startingXPos, startingYPos, endingXPos, endingYPos);
    }

    //endregion
    
    //region Image Absolute URI Utilities
	
	
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
	 * Get the real file path. This will determine the API level and process it accordingly.
	 * The file path will be passed back upon the callback listener
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
	 * Get the real path from API levels < 11. No need for a callback listener here as no web-based
	 * downloads can trigger in this API range
	 * @param context
	 * @param contentUri
	 * @return
	 */
	public static String getRealPathFromURI_BelowAPI11(Context context, Uri contentUri) {
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
	public static String getRealPathFromURI_API11to18(Context context, Uri contentUri) {
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
	public static void getRealPathFromURI_API19(final Context context, final Uri uri,
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
				String fileName = getFilePath(context, uri);
				if (fileName != null) {
					toReturn =  Environment.getExternalStorageDirectory().toString() + "/Download/" + fileName;
					callbackListener.onTaskComplete(toReturn, TAG_GET_REAL_FILE_PATH_SUCCESS);
					return;
				}
				
				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
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
	public static String getRealPathFromURI_API19(final Context context, final Uri uri) {
		
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
				String fileName = getFilePath(context, uri);
				if (fileName != null) {
					return Environment.getExternalStorageDirectory().toString() + "/Download/" + fileName;
				}
				
				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
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
		}
		// MediaStore (and general)
		if ("content".equalsIgnoreCase(uri.getScheme())) {
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
		//If this is reached, likely in a spot in the file system that won't read properly (IE Downloads)
		return uri.getPath();
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
	private static class GetDriveFilePathAsync extends AsyncTask<Void, Float, Void> {
		
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
	
	public static String getDataColumn(Context context, Uri uri, String selection,
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
	
	public static String getFilePath(Context context, Uri uri) {
		
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
	
	//endregion
	//endregion
	
}
