package com.devmarvel.creditcardentry.fields;

import android.content.Context;
import android.text.InputFilter;
import android.util.AttributeSet;

import com.devmarvel.creditcardentry.R;

public class ZipCodeText extends CreditEntryFieldBase {

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
	public String helperText() {
		return context.getString(R.string.ZipHelp);
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
