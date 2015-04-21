package com.devmarvel.creditcardentry.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.devmarvel.creditcardentry.R;
import com.devmarvel.creditcardentry.internal.CreditCardEntry;

public class CreditCardForm extends RelativeLayout {

	private CreditCardEntry entry;
	private boolean includeZip = true;
	private boolean includeHelper;
	private int textHelperColor = R.color.text_helper_color;

	public CreditCardForm(Context context) {
		this(context, null);
	}

	public CreditCardForm(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CreditCardForm(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		if(!isInEditMode()) {

			// If the attributes are available, use them to color the icon
			if(attrs != null){

				TypedArray typedArray = null;
				try {
					typedArray = context.getTheme().obtainStyledAttributes(
                  attrs,
                  R.styleable.CreditCardForm,
                  0,
                  0
          );

					this.includeZip = typedArray.getBoolean(R.styleable.CreditCardForm_include_zip, true);
					this.includeHelper = typedArray.getBoolean(R.styleable.CreditCardForm_include_helper, true);
					this.textHelperColor = typedArray.getColor(R.styleable.CreditCardForm_helper_text_color, getResources().getColor(textHelperColor));
				} finally {
					if (typedArray != null) typedArray.recycle();
				}
			}
		}

		init(context);
	}

	private void init(Context context) {
		LinearLayout layout = new LinearLayout(context);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		params.addRule(LinearLayout.HORIZONTAL);
		params.setMargins(0, 0, 0, 0);
		layout.setLayoutParams(params);
		layout.setBackgroundResource(R.drawable.background_white);

		FrameLayout frame = new FrameLayout(context);
        LinearLayout.LayoutParams frameParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
        frameParams.gravity = Gravity.CENTER_VERTICAL;

		frame.setLayoutParams(frameParams);
		frame.setFocusable(true);
		frame.setPadding(10, 0, 0, 0);

		ImageView view = new ImageView(context);
		LayoutParams r = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		view.setLayoutParams(r);
		view.setImageResource(R.drawable.unknown_cc);

		frame.addView(view);

		ImageView backView = new ImageView(context);
		r = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		backView.setLayoutParams(r);
		backView.setImageResource(R.drawable.cc_back);
		backView.setVisibility(View.GONE);

		frame.addView(backView);
		layout.addView(frame);

		layout.setId(R.id.cc_layout);

		entry = new CreditCardEntry(context, includeZip);
		r = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		entry.setLayoutParams(r);
		entry.setCardImageView(view);
		entry.setBackCardImage(backView);

		this.addView(layout);

		if (includeHelper) {
			TextView textHelp = new TextView(context);
			textHelp.setText(getResources().getString(R.string.CreditCardNumberHelp));
			textHelp.setTextColor(this.textHelperColor);
			r = new LayoutParams(LayoutParams.WRAP_CONTENT,
              LayoutParams.WRAP_CONTENT);
			r.addRule(RelativeLayout.BELOW, layout.getId());
			r.addRule(RelativeLayout.CENTER_HORIZONTAL);
			r.setMargins(0, 15, 0, 20);
			textHelp.setLayoutParams(r);
			entry.setTextHelper(textHelp);
			this.addView(textHelp);
		}

		layout.addView(entry);
	}

	public void setOnCardValidCallback(CardValidCallback callback) {
		entry.setOnCardValidCallback(callback);
	}

	@SuppressWarnings("unused")
	public boolean isCreditCardValid() {
		return entry.isCreditCardValid();
	}
	
	@SuppressWarnings("unused")
	public CreditCard getCreditCard() {
		return entry.getCreditCard();
	}

	/**
	 * request focus for the credit card field
	 */
	@SuppressWarnings("unused")
	public void focusCreditCard() {
		entry.focusCreditCard();
	}

	/**
	 * request focus for the expiration field
	 */
	@SuppressWarnings("unused")
	public void focusExp() {
		entry.focusExp();
	}

	/**
	 * request focus for the security code field
	 */
	@SuppressWarnings("unused")
	public void focusSecurityCode() {
		entry.focusSecurityCode();
	}

	/**
	 * request focus for the zip field (IF it's enabled)
	 */
	@SuppressWarnings("unused")
	public void focusZip() {
		entry.focusZip();
	}
}
