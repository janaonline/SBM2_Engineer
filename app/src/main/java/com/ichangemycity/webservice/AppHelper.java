package com.ichangemycity.webservice;

import android.app.Activity;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import androidx.core.content.ContextCompat;


import com.ichangemycity.model.SelectedImageModel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by pattabi.raman on 03-08-2017.
 */
public class AppHelper {

  /**
     * Turn drawable into byte array.
     *
     * @param drawable data
     * @return byte array
     */
    public static byte[] getFileDataFromDrawable(final Activity context, final SelectedImageModel path) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        Bitmap bitmap =null;
        try {

            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(),path.getUriOfImage());
            bitmap.compress(Bitmap.CompressFormat.JPEG,40, byteArrayOutputStream);
            bitmap.recycle();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return byteArrayOutputStream.toByteArray();
    }

}