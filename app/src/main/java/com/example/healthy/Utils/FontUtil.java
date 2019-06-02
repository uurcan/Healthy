package com.example.healthy.Utils;

import android.content.Context;
import android.graphics.Typeface;

import java.lang.reflect.Field;

public class FontUtil {

    public static void setFont(Context context,String fontToReplace, String fontDestination){
        Typeface typeface = Typeface.createFromAsset(context.getAssets(),fontDestination);
        changeFont(fontToReplace,typeface);
    }

    private static void changeFont(String fontToReplace, Typeface typeface) {
        try {
            Field field = Typeface.class.getField(fontToReplace);
            field.set(null,typeface);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
