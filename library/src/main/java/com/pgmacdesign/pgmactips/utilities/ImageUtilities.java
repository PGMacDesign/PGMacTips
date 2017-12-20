package com.pgmacdesign.pgmactips.utilities;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.widget.ImageView;

import com.pgmacdesign.pgmactips.adaptersandlisteners.OnTaskCompleteListener;
import com.pgmacdesign.pgmactips.misc.PGMacUtilitiesConstants;
import com.pgmacdesign.pgmactips.transformations.CircleTransform;
import com.squareup.picasso.Callback;
import com.squareup.picasso.LruCache;
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

/**
 * Created by pmacdowell on 8/15/2016.
 */
public class ImageUtilities {

    /**
     * Set a circular image into a view and set caching.
     *
     * @param urlThumbnail          URL String to use
     * @param viewToSet             View to set it into
     * @param backupImageResourceId Backup resource id in case the String url fails parsing
     * @param context               Context
     * @param percentMaxCache       Percent max cache to use (float, <1 && >0, IE, 0.45  == 45% of max
     *                              cache. Note that Picasso defaults to 14.3% of max cache if null sent
     * @param context               circularFrameColor Circular frame color (surrounds outside of image)
     * @param context               circularFrameWidth Circular frame width (in pixels)
     * @param <T>                   {T extends View}
     */
    public static <T extends ImageView> void setCircularImageWithPicasso(String urlThumbnail,
                                                                         final T viewToSet,
                                                                         final int backupImageResourceId,
                                                                         Context context,
                                                                         final Float percentMaxCache,
                                                                         final Integer circularFrameColor,
                                                                         final Integer circularFrameWidth) {
        if (context == null) {
            context = viewToSet.getContext();
        }
        final boolean useCustomCachePercent;
        if (percentMaxCache == null) {
            useCustomCachePercent = false;
        } else {
            if (percentMaxCache < 0 || percentMaxCache > 1) {
                useCustomCachePercent = false;
            } else {
                useCustomCachePercent = true;
            }
        }
        final Context fContext = context;


        if (StringUtilities.isNullOrEmpty(urlThumbnail)) {
            try {
                Picasso.with(fContext).load(backupImageResourceId).
                        transform(new CircleTransform(circularFrameColor, circularFrameWidth)).into(viewToSet);

            } catch (Exception e) {
            }
        } else {

            final String innerUrlThumbnail = urlThumbnail;

            try {
                Picasso.with(fContext)
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
                                    float flt = 0;
                                    if (useCustomCachePercent) {
                                        flt = percentMaxCache;
                                    }
                                    ImageUtilities.LoadImagesIntoPicassoCache async = new
                                            ImageUtilities.LoadImagesIntoPicassoCache(toCache, fContext, flt);
                                    async.execute();
                                } catch (Exception e2) {
                                }
                            }

                            @Override
                            public void onError() {
                                //Can trigger if image not in cache
                                Picasso.with(fContext).load(innerUrlThumbnail)
                                        .transform(new CircleTransform(circularFrameColor, circularFrameWidth))
                                        .into(viewToSet, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                //Load the image into cache for next time
                                                try {
                                                    float flt = 0;
                                                    if (useCustomCachePercent) {
                                                        flt = percentMaxCache;
                                                    }
                                                    List<String> toCache = new ArrayList<String>();
                                                    toCache.add(innerUrlThumbnail);
                                                    ImageUtilities.LoadImagesIntoPicassoCache async = new
                                                            ImageUtilities.LoadImagesIntoPicassoCache(toCache, fContext, flt);
                                                    async.execute();
                                                } catch (Exception e2) {}
                                            }

                                            @Override
                                            public void onError() {
                                                Picasso.with(fContext).load(backupImageResourceId)
                                                        .transform(new CircleTransform(circularFrameColor, circularFrameWidth))
                                                        .into(viewToSet);
                                            }
                                        });

                            }
                        });
            } catch (Exception e) {
                try {
                    Picasso.with(fContext).load(backupImageResourceId).
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
     * @param context               Context
     * @param <T>                   {T extends View}
     */
    public static <T extends ImageView> void setCircularImageWithPicasso(String urlThumbnail,
                                                                         final T viewToSet,
                                                                         final int backupImageResourceId,
                                                                         Context context) {
        ImageUtilities.setCircularImageWithPicasso(urlThumbnail, viewToSet, backupImageResourceId,
                context, null, null, null);
    }

    /**
     * Set a circular image into a view and set caching. Overloaded to allow for excluding
     * max cache size float
     *
     * @param urlThumbnail          URL String to use
     * @param viewToSet             View to set it into
     * @param backupImageResourceId Backup resource id in case the String url fails parsing
     * @param context               Context
     * @param <T>                   {T extends View}
     */
    public static <T extends ImageView> void setCircularImageWithPicasso(String urlThumbnail,
                                                                         final T viewToSet,
                                                                         final int backupImageResourceId,
                                                                         Context context,
                                                                         final Integer circularFrameColor,
                                                                         final Integer circularFrameWidth) {
        ImageUtilities.setCircularImageWithPicasso(urlThumbnail, viewToSet, backupImageResourceId,
                context, null, circularFrameColor, circularFrameWidth);
    }

    /**
     * Set a circular image into a view and set caching. Overloaded to allow for excluding
     * max cache size float
     *
     * @param urlThumbnail          URL String to use
     * @param viewToSet             View to set it into
     * @param backupImageResourceId Backup resource id in case the String url fails parsing
     * @param context               Context
     * @param <T>                   {T extends View}
     */
    public static <T extends ImageView> void setCircularImageWithPicassoNoCache(String urlThumbnail,
                                                                                final T viewToSet,
                                                                                final int backupImageResourceId,
                                                                                Context context,
                                                                                final Integer circularFrameColor,
                                                                                final Integer circularFrameWidth) {
        if (context == null) {
            context = viewToSet.getContext();
        }
        final Context fContext = context;


        if (StringUtilities.isNullOrEmpty(urlThumbnail)) {
            try {
                Picasso.with(fContext).load(backupImageResourceId).
                        transform(new CircleTransform()).into(viewToSet);

            } catch (Exception e) {
            }
        } else {

            final String innerUrlThumbnail = urlThumbnail;

            try {
                Picasso.with(fContext)
                        .load(innerUrlThumbnail)
                        .transform(new CircleTransform())
                        .into(viewToSet);
            } catch (Exception e) {
                try {
                    Picasso.with(fContext).load(backupImageResourceId).
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
     * @param context               Context
     * @param <T>                   {T extends View}
     */
    public static <T extends ImageView> void setCircularImageWithPicassoNoCache(String urlThumbnail,
                                                                                final T viewToSet,
                                                                                final int backupImageResourceId,
                                                                                Context context) {
        setCircularImageWithPicassoNoCache(urlThumbnail, viewToSet, backupImageResourceId,
                context, null, null);
    }


    /**
     * Set an image into a view and set caching. Overloaded to allow for excluding
     * max cache size float
     *
     * @param urlThumbnail          URL String to use
     * @param viewToSet             View to set it into
     * @param backupImageResourceId Backup resource id in case the String url fails parsing
     * @param context               Context
     * @param <T>                   {T extends View}
     */
    public static <T extends ImageView> void setImageWithPicasso(String urlThumbnail,
                                                                 final T viewToSet,
                                                                 final int backupImageResourceId,
                                                                 Context context) {
        ImageUtilities.setImageWithPicasso(urlThumbnail, viewToSet, backupImageResourceId, context, null);
    }

    /**
     * Set an image into a view and set caching.
     *
     * @param urlThumbnail          URL String to use
     * @param viewToSet             View to set it into
     * @param backupImageResourceId Backup resource id in case the String url fails parsing
     * @param context               Context
     * @param percentMaxCache       Percent max cache to use (float, <1 && >0, IE, 0.45  == 45% of max
     *                              cache. Note that Picasso defaults to 14.3% of max cache if null sent
     * @param <T>                   {T extends View}
     */
    public static <T extends ImageView> void setImageWithPicasso(String urlThumbnail,
                                                                 final T viewToSet,
                                                                 final int backupImageResourceId,
                                                                 Context context,
                                                                 final Float percentMaxCache) {
        if (urlThumbnail == null) {
            urlThumbnail = "";
        }
        final boolean useCustomCachePercent;
        if (percentMaxCache == null) {
            useCustomCachePercent = false;
        } else {
            if (percentMaxCache < 0 || percentMaxCache > 1) {
                useCustomCachePercent = false;
            } else {
                useCustomCachePercent = true;
            }
        }
        if (urlThumbnail.isEmpty() || urlThumbnail.equalsIgnoreCase("")) {
            viewToSet.setImageResource(backupImageResourceId);
        } else {
            final Context context1;
            if (context != null) {
                context1 = context;
            } else {
                context1 = viewToSet.getContext();
            }
            final String innerUrlThumbnail = urlThumbnail;
            final Context innerContext1 = context1;

            try {
                Picasso.with(context1)
                        .load(urlThumbnail)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(viewToSet, new Callback() {
                            @Override
                            public void onSuccess() {
                                //Nothing, image is set
                            }

                            @Override
                            public void onError() {
                                //Can trigger if image not in cache
                                Picasso.with(innerContext1).load(innerUrlThumbnail)
                                        .into(viewToSet, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                try {
                                                    List<String> toCache = new ArrayList<String>();
                                                    toCache.add(innerUrlThumbnail);
                                                    float flt = 0;
                                                    if (useCustomCachePercent) {
                                                        flt = percentMaxCache;
                                                    }
                                                    ImageUtilities.LoadImagesIntoPicassoCache async = new
                                                            ImageUtilities.LoadImagesIntoPicassoCache(toCache, context1, flt);
                                                    async.execute();
                                                } catch (Exception e2) {}
                                            }

                                            @Override
                                            public void onError() {
                                                Picasso.with(context1)
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

    /**
     * Set an image into a view and set caching. Overloaded to allow for excluding
     * max cache size float
     *
     * @param urlThumbnail          URL String to use
     * @param viewToSet             View to set it into
     * @param backupImageResourceId Backup resource id in case the String url fails parsing
     * @param context               Context
     * @param <T>                   {T extends View}
     */
    public static <T extends ImageView> void setImageWithPicassoNoCache(String urlThumbnail,
                                                                        final T viewToSet,
                                                                        final int backupImageResourceId,
                                                                        Context context) {
        if (urlThumbnail == null) {
            urlThumbnail = "";
        }
        if (urlThumbnail.isEmpty() || urlThumbnail.equalsIgnoreCase("")) {
            viewToSet.setImageResource(backupImageResourceId);
        } else {
            final Context context1;
            if (context != null) {
                context1 = context;
            } else {
                context1 = viewToSet.getContext();
            }
            final String innerUrlThumbnail = urlThumbnail;
            final Context innerContext1 = context1;

            try {
                Picasso.with(context1)
                        .load(urlThumbnail)
                        .into(viewToSet);
            } catch (Exception e) {
                //L.m("catch caught in picasso setImageWithPicasso call");
                viewToSet.setImageResource(backupImageResourceId);
            }

        }
    }

    /**
     * Loads images into the picasso cache by using the fetch() call. Reference:
     * http://stackoverflow.com/questions/23978828/how-do-i-use-disk-caching-in-picasso
     */
    public static class LoadImagesIntoPicassoCache extends AsyncTask<Void, Void, Void> {
        private List<String> imageURLs;
        private Context context;
        private float cacheSizeMaxPercent;

        /**
         * Load Images into cache constructor
         *
         * @param imageURLs A list of the image URLs to set
         * @param context   Context
         */
        public LoadImagesIntoPicassoCache(List<String> imageURLs, Context context) {
            this.imageURLs = imageURLs;
            this.context = context;
            this.cacheSizeMaxPercent = 0;
        }

        /**
         * Overloaded Constructor
         *
         * @param imageURLs           A list of the image URLs to set
         * @param context             Context
         * @param cacheSizeMaxPercent float % for max cache size. If left out or set to null,
         *                            it will default to the auto generated max (which is
         *                            about 1/7 available ram) Link:
         *                            http://stackoverflow.com/questions/20090265/android-picasso-configure-lrucache-size
         */
        public LoadImagesIntoPicassoCache(List<String> imageURLs, Context context, Float cacheSizeMaxPercent) {
            this.imageURLs = imageURLs;
            this.context = context;
            if (cacheSizeMaxPercent == null) {
                this.cacheSizeMaxPercent = 0;
            } else {
                this.cacheSizeMaxPercent = cacheSizeMaxPercent;
            }
        }

        @Override
        protected Void doInBackground(Void... params) {

            Picasso p;

            if (cacheSizeMaxPercent > 0 && cacheSizeMaxPercent < 1) {
                p = new Picasso.Builder(context)
                        .memoryCache(new LruCache((int) (
                                cacheSizeMaxPercent * ImageUtilities.getMaxCacheSize())))
                        .build();
            } else {
                p = new Picasso.Builder(context)
                        .build();
            }

            for (String str : imageURLs) {
                try {
                    if (isCancelled()) {
                        return null;
                    }
                    p.with(context).load(str).fetch();
                    Thread.sleep(500);
                } catch (OutOfMemoryError e1) {
                    //If we run out of memory, make sure to catch it!
                    p.with(context).invalidate(str);
                    return null;
                } catch (Exception e) {
                }
            }

            return null;
        }
    }

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

                // Setting pre rotate
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
     * For rotating images
     *
     * @param bitmap Bitmap to rotate
     * @param degree Degree amount to rotate
     * @return
     */
    public static Bitmap rotate(Bitmap bitmap, int degree) {
        if (bitmap == null) {
            return null;
        }
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        mtx.setRotate(degree);

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    /**
     * Set the drawable to a specific color and return it
     *
     * @param drawable   The drawable to change
     * @param colorToSet The color to set it to
     * @return Drawable
     * @throws NullPointerException, if it fails, throws a null pointer
     */
    public static Drawable colorDrawable(Drawable drawable, int colorToSet) {
        try {
            drawable.mutate().setColorFilter(colorToSet, PorterDuff.Mode.MULTIPLY);
            return drawable;
        } catch (Exception e) {
            e.printStackTrace();
            throw new NullPointerException();
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
    public static Drawable colorDrawable(int drawableId, int colorToSet, Context context) {
        try {
            Drawable drawable = ContextCompat.getDrawable(context, drawableId);
            drawable.mutate().setColorFilter(colorToSet, PorterDuff.Mode.MULTIPLY);
            return drawable;
        } catch (Exception e) {
            e.printStackTrace();
            throw new NullPointerException();
        }
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
                lengthOfTimeToDelay = (long) (PGMacUtilitiesConstants.ONE_SECOND * 2.6);
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
                }, PGMacUtilitiesConstants.ONE_SECOND);
                listener.onTaskComplete("Url Error", PGMacUtilitiesConstants.TAG_PHOTO_BAD_URL);

            } else {
                dialog.setProgress(100);
                if (timeLeft <= 0) {
                    try {
                        dialog.dismiss();
                        listener.onTaskComplete(file1, PGMacUtilitiesConstants.TAG_FILE_DOWNLOADED);
                    } catch (Exception e) {
                    }
                } else {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                dialog.dismiss();
                                listener.onTaskComplete(file1, PGMacUtilitiesConstants.TAG_FILE_DOWNLOADED);
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

    public static void zoomAView() {
        //https://developer.android.com/training/animation/zoom.html
    }

    /**
     * Get the maximum system cache size for the app. Designed after this answer:
     * https://stackoverflow.com/a/15763477/2480714
     * Note! DO NOT USE THE ENTIRE 100%! Use a fraction of it. (IE, 1/8th)
     *
     * @return long maximum cache size. (If an error occurs, return 0)
     */
    public static long getMaxCacheSize() {
        try {
            return (long) ((Runtime.getRuntime().maxMemory()) / (1024));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    ///////////////////////////////////////////
    //Base64 String Image --> String Encoding//
    ///////////////////////////////////////////

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
            listener.onTaskComplete(null, PGMacUtilitiesConstants.TAG_BASE64_IMAGE_ENCODE_FAIL);
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
                            PGMacUtilitiesConstants.TAG_BASE64_IMAGE_ENCODE_SUCCESS);
                } else {
                    listener.onTaskComplete(null,
                            PGMacUtilitiesConstants.TAG_BASE64_IMAGE_ENCODE_FAIL);
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
            listener.onTaskComplete(null, PGMacUtilitiesConstants.TAG_BASE64_IMAGE_ENCODE_FAIL);
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
                            PGMacUtilitiesConstants.TAG_BASE64_IMAGE_ENCODE_SUCCESS);
                } else {
                    listener.onTaskComplete(null,
                            PGMacUtilitiesConstants.TAG_BASE64_IMAGE_ENCODE_FAIL);
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
            listener.onTaskComplete(null, PGMacUtilitiesConstants.TAG_BASE64_IMAGE_ENCODE_FAIL);
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
                            PGMacUtilitiesConstants.TAG_BASE64_IMAGE_ENCODE_SUCCESS);
                } else {
                    listener.onTaskComplete(null,
                            PGMacUtilitiesConstants.TAG_BASE64_IMAGE_ENCODE_FAIL);
                }
            }
        }.execute();
    }

}
