package com.sumdroid.localcomplaint.appConstant;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.IOException;

/**
 * Created by Masum on 3/8/2018.
 */

public class AppConstants {

    //request code

    public static final int IMAGE_REQ_CODE = 1;
    public static final int REQUEST_IMAGE_CAPTURE = 3;

    //firebase reference constant
    public static final String STORAGE_PATH ="complain/";

    public static final String SELECT_IMAGE_TITLE ="Please Select Image";
    public static final String[] ISSUE ={"road","drainage","electricity"};
    public static final String DRAIN="drainage";
    public static final String ROAD="road";
    public static final String ELECTRICITY="electricity";


    public static final String IMAGE_URL_FIELD ="imgUrl" ;
    public static final String TITLE_FIELD ="title" ;
    public static final String DESCRIPTION_FIELD ="description" ;

    public static Bitmap decodeFromFirebaseBase64(String image) throws IOException {
        byte[] decodedByteArray = android.util.Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }
}
