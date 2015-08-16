package com.devmarvel.creditcardentry.fields;

import com.devmarvel.creditcardentry.R;

import android.content.Context;
import android.text.InputFilter;
import android.util.AttributeSet;

public class ZipCodeText extends CreditEntryFieldBase {

    private String mHelperText;

    public ZipCodeText(Context context) {
        super(context);
        init();
    }

    public ZipCodeText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ZipCodeText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    void init() {
        super.init();
        setHint("   ZIP   ");
        setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
    }

    @Override
    public void setHelperText(String helperText) {
        mHelperText = helperText;
    }

    @Override
    public String getHelperText() {
        return (mHelperText != null ? mHelperText : context.getString(R.string.ZipHelp));
    }

    @Override
    public void textChanged(CharSequence s, int start, int before, int end) {
        String zipCode = s.toString();
        if (zipCode.length() == 5) {
            setValid(true);
            delegate.onZipCodeValid();
        } else {
            setValid(false);
        }
    }
}

