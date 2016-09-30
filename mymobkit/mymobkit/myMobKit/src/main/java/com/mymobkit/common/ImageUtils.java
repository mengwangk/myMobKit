package com.mymobkit.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mymobkit.model.YuvImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.makeLogTag;

public final class ImageUtils {

    private static final String TAG = makeLogTag(ImageUtils.class);

    private static final int IMAGE_QUALITY = 80;

   /* private static List<Long> pTimes = new ArrayList<Long>(100);

    private static long calculateAverage(List <Long> marks) {
        Long sum = 0l;
        if(!marks.isEmpty()) {
            for (Long mark : marks) {
                sum += mark;
            }
            return sum.longValue() / marks.size();
        }
        return sum;
    }*/

    public static byte[] yuvToJpeg(final byte[] data, final int width, final int height, final int previewFormat, final int quality, final Rect rect) {
        try {
            if (data == null)
                return null;
            final ByteArrayOutputStream bao = new ByteArrayOutputStream();
            //long b4 = System.currentTimeMillis();
            YuvImage image = new YuvImage(data, previewFormat, width, height, null);
            image.compressToJpeg(rect, quality, bao);
            //pTimes.add((System.currentTimeMillis() - b4));
            //if (pTimes.size() == 100) {
            //    LOGE(TAG, "time ---- " + calculateAverage(pTimes));
            //  pTimes.clear();
            //}
            return bao.toByteArray();
        } catch (Exception ex) {
            LOGE(TAG, "[toJpeg] Image conversion error", ex);
        }
        return null;
    }

    /*
    public static Bitmap loadBitmap(final String filePath) {
        try {
            //BitmapFactory.Options options = new BitmapFactory.Options();
            //options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            return bitmap;
        } catch (Exception ex) {
            LOGE(TAG, "[loadBitmap] Unable to load bitmap from " + filePath, ex);
        }
        return null;
    }
    */

    public static boolean deleteFile(final String filePath) {
        try {
            File file = new File(filePath);
            return file.delete();
        } catch (Exception ex) {
            LOGE(TAG, "[deleteFile] Unable to delete bitmap from " + filePath, ex);
        }
        return false;
    }

    public static byte[] readFile(String filePath) {
        return readFile(new File(filePath));
    }

    public static byte[] readFile(File filePath) {
        try {
            // Open file
            RandomAccessFile f = new RandomAccessFile(filePath, "r");
            try {
                // Get and check length
                long longlength = f.length();
                int length = (int) longlength;
                if (length != longlength)
                    throw new IOException("File size >= 2 GB");
                // Read file and return data
                byte[] data = new byte[length];
                f.readFully(data);
                return data;
            } finally {
                f.close();
            }
        } catch (Exception ex) {
            LOGE(TAG, "[readFile] Unable to read file from " + filePath, ex);
        }
        return null;
    }

    @Nullable
    public static byte[] resizeImage(final Context context, @NonNull final byte[] data, final int thumbnailSize) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inInputShareable = true;
        options.inPurgeable = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);
        if ((options.outWidth == -1) || (options.outHeight == -1))
            return null;

        int originalSize = (options.outHeight > options.outWidth) ? options.outHeight : options.outWidth;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = originalSize / thumbnailSize;
        final Bitmap scaledBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, opts);

        if (scaledBitmap!= null){
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, stream);
            return stream.toByteArray();
        }
        return null;
    }



   /* public static byte[] fastCompress(final byte[] yuvBuffer, final int quality, final int width, final int height) throws TJException {
        final int outSubsamp = TJ.SAMP_420;
        final int flags = 0;
        byte[] jpegBuf;
        byte[] rgbBuf = new byte[width * height * 3];


        decodeYUV420SP(rgbBuf, yuvBuffer, width, height);

        tjc.setSubsamp(outSubsamp);
        tjc.setJPEGQuality(quality);
        tjc.setSourceImage(rgbBuf, 0, 0, width, 0, height, TJ.PF_RGB);
        long b4 = System.currentTimeMillis();
        jpegBuf = tjc.compress(flags);
        LOGE(TAG, "compress --- " + (System.currentTimeMillis() - b4));
        return jpegBuf;
    }


    private static void decodeYUV420SP(byte[] rgbBuf, byte[] yuv420sp, int width, int height) {
        final int frameSize = width * height;
        if (rgbBuf == null)
            throw new NullPointerException("buffer 'rgbBuf' is null");
        if (rgbBuf.length < frameSize * 3)
            throw new IllegalArgumentException("buffer 'rgbBuf' size "
                    + rgbBuf.length + " < minimum " + frameSize * 3);

        if (yuv420sp == null)
            throw new NullPointerException("buffer 'yuv420sp' is null");

        if (yuv420sp.length < frameSize * 3 / 2)
            throw new IllegalArgumentException("buffer 'yuv420sp' size "
                    + yuv420sp.length + " < minimum " + frameSize * 3 / 2);

        int i = 0, y = 0;
        int uvp = 0, u = 0, v = 0;
        int y1192 = 0, r = 0, g = 0, b = 0;

        for (int j = 0, yp = 0; j < height; j++) {
            uvp = frameSize + (j >> 1) * width;
            u = 0;
            v = 0;
            for (i = 0; i < width; i++, yp++) {
                y = (0xff & ((int) yuv420sp[yp])) - 16;
                if (y < 0)
                    y = 0;
                if ((i & 1) == 0) {
                    v = (0xff & yuv420sp[uvp++]) - 128;
                    u = (0xff & yuv420sp[uvp++]) - 128;
                }

                y1192 = 1192 * y;
                r = (y1192 + 1634 * v);
                g = (y1192 - 833 * v - 400 * u);
                b = (y1192 + 2066 * u);

                if (r < 0)
                    r = 0;
                else if (r > 262143)
                    r = 262143;
                if (g < 0)
                    g = 0;
                else if (g > 262143)
                    g = 262143;
                if (b < 0)
                    b = 0;
                else if (b > 262143)
                    b = 262143;

                rgbBuf[yp * 3] = (byte) (r >> 10);
                rgbBuf[yp * 3 + 1] = (byte) (g >> 10);
                rgbBuf[yp * 3 + 2] = (byte) (b >> 10);
            }
        }
    }*/
}
