package com.shadhinlab.reminder.tools;

import android.graphics.drawable.GradientDrawable;
import android.view.View;

public class CustomView {
    public static void cornerView(View view, int stroke, int strokeColor, int bgColor, float topLeft,
                                  float topRight, float bottomRight, float bottomLeft) {
        // Initialize a new GradientDrawable
        GradientDrawable gd = new GradientDrawable();

        // Specify the shape of drawable
        gd.setShape(GradientDrawable.RECTANGLE);

        // Set the fill color of drawable
        gd.setColor(bgColor); // make the background transparent

        // Create a 2 pixels width red colored border for drawable
        gd.setStroke(stroke, strokeColor); // border width and color


        gd.setCornerRadii(new float[]{topLeft, topLeft, topRight, topRight,
                bottomRight, bottomRight, bottomLeft, bottomLeft});

        // Finally, apply the GradientDrawable as TextView background
        view.setBackground(gd);
    }

    public static void cornerView(View view, int stroke, int strokeColor, int bgColor, float radius) {
        // Initialize a new GradientDrawable
        GradientDrawable gd = new GradientDrawable();

        // Specify the shape of drawable
        gd.setShape(GradientDrawable.RECTANGLE);

        // Set the fill color of drawable
        gd.setColor(bgColor); // make the background transparent

        // Create a 2 pixels width red colored border for drawable
        gd.setStroke(stroke, strokeColor); // border width and color

        // Make the border rounded
        gd.setCornerRadius(radius); // border corner radius

        // Finally, apply the GradientDrawable as TextView background
        view.setBackground(gd);
    }
}