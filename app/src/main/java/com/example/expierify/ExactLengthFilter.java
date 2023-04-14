package com.example.expierify;

import android.text.InputFilter;
import android.text.Spanned;

public class ExactLengthFilter implements InputFilter {

    private int maxLength;

    public ExactLengthFilter(int maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        int destLength = dest.length();
        int sourceLength = end - start;
        int newLength = destLength + sourceLength;
        if (newLength > maxLength) {
            // If the new length would exceed the maximum length, return an empty string to prevent the new text from being added
            return "";
        } else {
            // Otherwise, allow the new text to be added
            return source;
        }
    }
}
