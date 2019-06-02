package com.example.healthy.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

import androidx.core.content.ContextCompat;

public class Utils {

    public static Drawable setDrawableSelector(Context context, int normal, int selected) {
        Drawable state_normal = ContextCompat.getDrawable(context, normal);
        Drawable state_pressed = ContextCompat.getDrawable(context, selected);

        assert state_normal != null;
        Bitmap state_normal_bitmap = ((BitmapDrawable)state_normal).getBitmap();

        Bitmap disabledBitmap = Bitmap.createBitmap(
                state_normal.getIntrinsicWidth(),
                state_normal.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(disabledBitmap);
        Paint paint = new Paint();
        paint.setAlpha(126);
        canvas.drawBitmap(state_normal_bitmap, 0, 0, paint);
        BitmapDrawable state_normal_drawable = new BitmapDrawable(context.getResources(), disabledBitmap);
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[]{android.R.attr.state_selected},
                state_pressed);
        drawable.addState(new int[]{android.R.attr.state_enabled},
                state_normal_drawable);

        return drawable;
    }
}