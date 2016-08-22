package com.pgmacdesign.pgmacutilities.utilities;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.pgmacdesign.pgmacutilities.adaptersandlisteners.OnTaskCompleteListener;
import com.pgmacdesign.pgmacutilities.nonutilities.CircleTransform;
import com.pgmacdesign.pgmacutilities.nonutilities.PGMacCustomProgressBar;
import com.pgmacdesign.pgmacutilities.nonutilities.PGMacUtilitiesConstants;
import com.squareup.picasso.Callback;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
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


    public static <T extends ImageView> void setCircularImageWithPicasso(String urlThumbnail,
                                                                         final T viewToSet,
                                                                         final int backupImageResourceId,
                                                                         Context context){
        if(context == null){
            context = viewToSet.getContext();
        }
        final Context fContext = context;

        if(StringUtilities.isNullOrEmpty(urlThumbnail)){
            try {
                Picasso.with(fContext).load(backupImageResourceId).
                        transform(new CircleTransform()).into(viewToSet);

            } catch (Exception e){}
        } else {

            final String innerUrlThumbnail = urlThumbnail;

            try {
                Picasso.with(fContext)
                        .load(innerUrlThumbnail)
                        .transform(new CircleTransform())
                        .into(viewToSet, new Callback() {
                            @Override
                            public void onSuccess() {
                                //Load the image into cache for next time
                                try {
                                    List<String> toCache = new ArrayList<String>();
                                    toCache.add(innerUrlThumbnail);
                                    ImageUtilities.LoadImagesIntoPicassoCache async = new
                                            ImageUtilities.LoadImagesIntoPicassoCache(toCache, fContext);
                                    async.execute();
                                } catch (Exception e2){}
                            }
                            @Override
                            public void onError() {
                                Picasso.with(fContext).load(innerUrlThumbnail)
                                        .transform(new CircleTransform())
                                        .into(viewToSet);
                                //Load the image into cache for next time
                                try {
                                    List<String> toCache = new ArrayList<String>();
                                    toCache.add(innerUrlThumbnail);
                                    ImageUtilities.LoadImagesIntoPicassoCache async = new
                                            ImageUtilities.LoadImagesIntoPicassoCache(toCache, fContext);
                                    async.execute();
                                } catch (Exception e2){}
                            }
                        });
            } catch (Exception e){
                try {
                    Picasso.with(fContext).load(backupImageResourceId).
                            transform(new CircleTransform()).into(viewToSet);
                } catch (Exception e1){}
            }
        }
    }
    /**
     * Set a circular image using picasso. Will cache and try to use cache if available
     * @param urlThumbnail
     * @param viewToSet
     * @param backupImageResourceId
     * @param <T>
     */
    public static <T extends ImageView> void setCircularImageWithPicasso(String urlThumbnail,
                                                                         final T viewToSet,
                                                                         final int backupImageResourceId){
        ImageUtilities.setCircularImageWithPicasso(urlThumbnail, viewToSet, backupImageResourceId, null);
    }

    /**
     * Set an image Using picasso. Will start to cache if possible and will reuse said cache if stored
     * @param urlThumbnail
     * @param viewToSet
     * @param backupImageResourceId
     * @param <T>
     */
    public static <T extends ImageView> void setImageWithPicasso(String urlThumbnail,
                                                                 final T viewToSet,
                                                                 final int backupImageResourceId){
        if(urlThumbnail == null){
            urlThumbnail = "";
        }

        if(urlThumbnail.isEmpty() || urlThumbnail.equalsIgnoreCase("")){
            viewToSet.setImageResource(backupImageResourceId);
        } else {
            final Context context1 = viewToSet.getContext();

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
                                //Backup, in case caching didn't work
                                Picasso.with(innerContext1).load(innerUrlThumbnail).into(viewToSet);
                                //Load the image into cache for next time
                                try {
                                    List<String> toCache = new ArrayList<String>();
                                    toCache.add(innerUrlThumbnail);
                                    ImageUtilities.LoadImagesIntoPicassoCache async = new
                                            ImageUtilities.LoadImagesIntoPicassoCache(toCache, context1);
                                    async.execute();
                                } catch (Exception e2){}
                            }
                        });
            } catch (Exception e){
                //L.m("catch caught in picasso setImageWithPicasso call");
                viewToSet.setImageResource(backupImageResourceId);
            }

        }
    }

    /**
     * Loads images into the picasso cache by using the fetch() call. Reference:
     * http://stackoverflow.com/questions/23978828/how-do-i-use-disk-caching-in-picasso
     */
    public static class LoadImagesIntoPicassoCache extends AsyncTask <Void, Void, Void>{
        private List<String> imageURLs;
        private Context context;
        private int cacheSizeMax;

        /**
         * Load Images into cache constructor
         * @param imageURLs A list of the image URLs to set
         * @param context Context
         */
        public LoadImagesIntoPicassoCache(List<String> imageURLs, Context context){
            this.imageURLs = imageURLs;
            this.context = context;
            this.cacheSizeMax = 0;
        }
        /**
         * Overloaded Constructor
         * @param imageURLs A list of the image URLs to set
         * @param context Context
         * @param cacheSizeMax Int for max cache size. If left out or set to null, it will default
         *                     to the auto generated max (which is about 1/7 available ram)
         *                     Link: http://stackoverflow.com/questions/20090265/android-picasso-configure-lrucache-size
         */
        public LoadImagesIntoPicassoCache(List<String> imageURLs, Context context, Integer cacheSizeMax){
            this.imageURLs = imageURLs;
            this.context = context;
            if(cacheSizeMax == null){
                this.cacheSizeMax = 0;
            } else {
                this.cacheSizeMax = cacheSizeMax;
            }
        }

        @Override
        protected Void doInBackground(Void... params) {

            Picasso p;

            if(cacheSizeMax > 0) {
                p = new Picasso.Builder(context)
                        .memoryCache(new LruCache(cacheSizeMax))
                        .build();
            } else {
                p = new Picasso.Builder(context)
                        .build();
            }

            for(String str : imageURLs){
                try {
                    if(isCancelled()){
                        return null;
                    }
                    p.with(context).load(str).fetch();
                    Thread.sleep(500);
                } catch (OutOfMemoryError e1){
                    //If we run out of memory, make sure to catch it!
                    p.with(context).invalidate(str);
                    return null;
                } catch (Exception e){}
            }

            return null;
        }
    }

    /**
     * Set the drawable to a specific color and return it
     * @param drawable The drawable to change
     * @param colorToSet The color to set it to
     * @return Drawable
     * @throws NullPointerException, if it fails, throws a null pointer
     */
    public static Drawable colorDrawable(Drawable drawable, int colorToSet){
        try {
            drawable.mutate().setColorFilter(colorToSet, PorterDuff.Mode.MULTIPLY);
            return drawable;
        } catch (Exception e){
            e.printStackTrace();
            throw new NullPointerException();
        }
    }

    /**
     * Set the drawable to a specific color and return it
     * @param drawableId the int ID of the drawable to change
     * @param colorToSet The color to set it to
     * @return Drawable
     * @throws NullPointerException, if it fails, throws a null pointer
     */
    public static Drawable colorDrawable(int drawableId,  int colorToSet, Context context){
        try {
            Drawable drawable = ContextCompat.getDrawable(context, drawableId);
            drawable.mutate().setColorFilter(colorToSet, PorterDuff.Mode.MULTIPLY);
            return drawable;
        } catch (Exception e){
            e.printStackTrace();
            throw new NullPointerException();
        }
    }

    /**
     * Converts an inputstream to a byte array (Mostly useful for sending images via JSON)
     * @param is Input stream, if using a URI, open it by calling:
     *     InputStream iStream =   context.getContentResolver().openInputStream(uri);
     * @return Byte Array
     */
    public static Bitmap convertISToBitmap(InputStream is){
        try {
            return BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Convert a Bitmap into a byte Array
     * @param bitmap
     * @return
     */
    public static byte[] convertBitmapToByte(Bitmap bitmap){
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            return byteArray;
        } catch (Exception e){
            return null;
        }
    }

    /**
     * Combines multiple bitmaps together into one
     * @param aParts An array of bitmaps
     * @return Returns a bitmap image
     */
    public static Bitmap combineBitmaps(Bitmap[] aParts){
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
     * @param uri
     * @param context
     * @return
     */
    public static Bitmap decodeFileFromPath(Uri uri, Context context){

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
     * @param uri
     * @param context
     * @return
     */
    public static byte[] convertImageToByte(Uri uri, Context context){
        byte[] data = null;
        try {
            ContentResolver cr = context.getContentResolver();
            InputStream inputStream = cr.openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            data = baos.toByteArray();
            if(inputStream != null){
                try {
                    inputStream.close();
                } catch (Exception e2){}
            }
            if(baos != null){
                try {
                    baos.close();
                } catch (Exception e2){}
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
         * @param context
         * @param imageUrl String image Url
         * @param dialog Progress dialog to show
         * @param listener listener to send data back on
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
            if(this.dialog == null){
                this.dialog = PGMacCustomProgressBar.buildSVGDialog(context);
            }
            if(lengthOfTimeToDelay == null){
                lengthOfTimeToDelay = (long)(PGMacUtilitiesConstants.ONE_SECOND * 2.6);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog.show();
        }

        @Override
        protected File doInBackground(Void... params) {
            File file = FileUtilities.generateFileForImage(context);
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
                    int publishNum = ((int)((total*100/lenghtOfFile)));
                    publishProgress(publishNum);
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
                return file;
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if(values[0] != null){
                int prog = values[0];
                if(prog % 10 == 0){
                    if(prog == 100){
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

            if(file == null){
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            dialog.dismiss();
                        } catch (Exception e){}
                    }
                }, PGMacUtilitiesConstants.ONE_SECOND);
                listener.onTaskComplete("Url Error", PGMacUtilitiesConstants.TAG_PHOTO_BAD_URL);

            } else {
                dialog.setProgress(100);
                if(timeLeft <= 0){
                    try {
                        dialog.dismiss();
                        listener.onTaskComplete(file1, PGMacUtilitiesConstants.TAG_FILE_DOWNLOADED);
                    } catch (Exception e){}
                } else {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                dialog.dismiss();
                                listener.onTaskComplete(file1, PGMacUtilitiesConstants.TAG_FILE_DOWNLOADED);
                            } catch (Exception e){}
                        }
                    }, (int)timeLeft);
                }
            }
        }
    }

    public static void zoomAView(){
        //https://developer.android.com/training/animation/zoom.html
    }
}
