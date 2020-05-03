package com.devmarvel.creditcardentry.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.core.os.ParcelableCompat;
import androidx.core.os.ParcelableCompatCreatorCallbacks;
import android.util.AttributeSet;
import android.util.SparseArray;
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
	private boolean includeExp = true;
	private boolean includeSecurity = true;
	private boolean includeZip = true;
	private boolean includeHelper;
	private int textHelperColor;
	private Drawable inputBackground;
	private boolean useDefaultColors;
	private boolean animateOnError;
	private String cardNumberHint = "1234 5678 9012 3456";

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
			if (attrs != null) {

				TypedArray typedArray = null;
				try {
					typedArray = context.getTheme().obtainStyledAttributes(
							attrs,
							R.styleable.CreditCardForm,
							0,
							0
					);

					this.cardNumberHint = typedArray.getString(R.styleable.CreditCardForm_card_number_hint);
					this.includeExp = typedArray.getBoolean(R.styleable.CreditCardForm_include_exp, true);
					this.includeSecurity = typedArray.getBoolean(R.styleable.CreditCardForm_include_security, true);
					this.includeZip = typedArray.getBoolean(R.styleable.CreditCardForm_include_zip, true);
					this.includeHelper = typedArray.getBoolean(R.styleable.CreditCardForm_include_helper, true);
					this.textHelperColor = typedArray.getColor(R.styleable.CreditCardForm_helper_text_color, getResources().getColor(R.color.text_helper_color));
					this.inputBackground = typedArray.getDrawable(R.styleable.CreditCardForm_input_background);
					this.useDefaultColors = typedArray.getBoolean(R.styleable.CreditCardForm_default_text_colors, false);
					this.animateOnError = typedArray.getBoolean(R.styleable.CreditCardForm_animate_on_error, true);
				} finally {
					if (typedArray != null) typedArray.recycle();
				}
			}

			// defaults if not set by user
			if(cardNumberHint == null) cardNumberHint = "1234 5678 9012 3456";
			if(inputBackground == null) {
				//noinspection deprecation
				inputBackground = context.getResources().getDrawable(R.drawable.background_white);
			}
		}

		init(context, attrs, defStyle);
	}

	private void init(Context context, AttributeSet attrs, int style) {
		// the wrapper layout
		LinearLayout layout;
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			layout = new LinearLayout(context);
		} else {
			layout = new LinearLayout(context);
		}

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            //ignore RTL layout direction
            layout.setLayoutDirection(LAYOUT_DIRECTION_LTR);
        }

		layout.setId(R.id.cc_form_layout);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		params.addRule(LinearLayout.HORIZONTAL);
		params.setMargins(0, 0, 0, 0);
		layout.setLayoutParams(params);
		layout.setPadding(0, 0, 0, 0);
		//noinspection deprecation
		layout.setBackgroundDrawable(inputBackground);

		// set up the card image container and images
		FrameLayout cardImageFrame = new FrameLayout(context);
		LinearLayout.LayoutParams frameParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		frameParams.gravity = Gravity.CENTER_VERTICAL;
		cardImageFrame.setLayoutParams(frameParams);
		cardImageFrame.setFocusable(true);
		cardImageFrame.setFocusableInTouchMode(true);
		cardImageFrame.setPadding(10, 0, 0, 0);

		ImageView cardFrontImage = new ImageView(context);
		LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		cardFrontImage.setLayoutParams(layoutParams);
		cardFrontImage.setImageResource(CardType.INVALID.frontResource);
		cardImageFrame.addView(cardFrontImage);

		ImageView cardBackImage = new ImageView(context);
		layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		cardBackImage.setLayoutParams(layoutParams);
		cardBackImage.setImageResource(CardType.INVALID.backResource);
		cardBackImage.setVisibility(View.GONE);
		cardImageFrame.addView(cardBackImage);
		layout.addView(cardImageFrame);

		// add the data entry form
		LinearLayout.LayoutParams entryParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		entryParams.gravity = Gravity.CENTER_VERTICAL;
		entry = new CreditCardEntry(context, includeExp, includeSecurity, includeZip, attrs, style);
        entry.setId(R.id.cc_entry);

		// this obnoxious 6 for bottom padding is to make the damn text centered on the image... if you know a better way... PLEASE HELP
		entry.setPadding(0, 0, 0, 6);
		entry.setLayoutParams(entryParams);

		// set any passed in attrs
		entry.setCardImageView(cardFrontImage);
		entry.setBackCardImage(cardBackImage);
		entry.setCardNumberHint(cardNumberHint);

		entry.setAnimateOnError(animateOnError);

		this.addView(layout);

		// set up optional helper text view
		if (includeHelper) {
			TextView textHelp = new TextView(context);
            textHelp.setId(R.id.text_helper);
			textHelp.setText(getResources().getString(R.string.CreditCardNumberHelp));
			if (useDefaultColors) {
				textHelp.setTextColor(this.textHelperColor);
			}
			layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			layoutParams.addRule(RelativeLayout.BELOW, layout.getId());
			layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
			layoutParams.setMargins(0, 15, 0, 20);
			textHelp.setLayoutParams(layoutParams);
			entry.setTextHelper(textHelp);
			this.addView(textHelp);
		}

		layout.addView(entry);
	}

	public void setOnCardValidCallback(CardValidCallback callback) {
		entry.setOnCardValidCallback(callback);
	}

	/**
	 * all internal components will be attached this same focus listener
	 */
	@Override
	public void setOnFocusChangeListener(OnFocusChangeListener l) {
		entry.setOnFocusChangeListener(l);
	}

	@Override
	public OnFocusChangeListener getOnFocusChangeListener() {
		return entry.getOnFocusChangeListener();
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

	/**
	 * clear and reset the entire form
	 */
	@SuppressWarnings("unused")
	public void clearForm() {
		entry.clearAll();
	}

	/**
	 * @param cardNumber the card number to show
	 * @param focusNextField true to go to next field (only works if the number is valid)
	 */
	public void setCardNumber(String cardNumber, boolean focusNextField) {
		entry.setCardNumber(cardNumber, focusNextField);
	}

	/**
	 * @param expirationDate the exp to show
	 * @param focusNextField true to go to next field (only works if the number is valid)
	 */
	@SuppressWarnings("unused")
	public void setExpDate(String expirationDate, boolean focusNextField) {
		entry.setExpDate(expirationDate, focusNextField);
	}

	/**
	 * @param securityCode the security code to show
	 * @param focusNextField true to go to next field (only works if the number is valid)
	 */
	@SuppressWarnings("unused")
	public void setSecurityCode(String securityCode, boolean focusNextField) {
		entry.setSecurityCode(securityCode, focusNextField);
	}

	/**
	 * @param zip the zip to show
	 * @param focusNextField true to go to next field (only works if the number is valid)
	 */
	@SuppressWarnings("unused")
	public void setZipCode(String zip, boolean focusNextField) {
		entry.setZipCode(zip, focusNextField);
	}

	@Override
	protected void dispatchSaveInstanceState(@NonNull SparseArray<Parcelable> container) {
		dispatchFreezeSelfOnly(container);
	}

	@Override
	protected void dispatchRestoreInstanceState(@NonNull SparseArray<Parcelable> container) {
		dispatchThawSelfOnly(container);
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		SavedState ss = (SavedState) state;
		super.onRestoreInstanceState(ss.getSuperState());
		for (int i = 0; i < getChildCount(); i++) {
			getChildAt(i).restoreHierarchyState(ss.childrenStates);
		}
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		SavedState ss = new SavedState(superState);
		ss.childrenStates = new SparseArray();
		for (int i = 0; i < getChildCount(); i++) {
			getChildAt(i).saveHierarchyState(ss.childrenStates);
		}
		return ss;
	}

	static class SavedState extends BaseSavedState {
		SparseArray childrenStates;

		SavedState(Parcelable superState) {
			super(superState);
		}

		private SavedState(Parcel in, ClassLoader classLoader) {
			super(in);
			childrenStates = in.readSparseArray(classLoader);
		}

		@Override
		public void writeToParcel(Parcel out, int flags) {
			super.writeToParcel(out, flags);
			out.writeSparseArray(childrenStates);
		}

		public static final Creator<SavedState> CREATOR
				= ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks<SavedState>() {
			@Override
			public SavedState createFromParcel(Parcel in, ClassLoader loader) {
				return new SavedState(in, loader);
			}

			@Override
			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		});
	}

    /** helper & hint setting **/

    public void setCreditCardTextHelper(String text) {
        entry.setCreditCardTextHelper(text);
    }

    public void setCreditCardTextHint(String text) {
        entry.setCreditCardTextHint(text);
    }

    public void setExpDateTextHelper(String text) {
        entry.setExpDateTextHelper(text);
    }

    public void setExpDateTextHint(String text) {
        entry.setExpDateTextHint(text);
    }

    public void setSecurityCodeTextHelper(String text) {
        entry.setSecurityCodeTextHelper(text);
    }

    public void setSecurityCodeTextHint(String text) {
        entry.setSecurityCodeTextHint(text);
    }

    public void setZipCodeTextHelper(String text) {
        entry.setZipCodeTextHelper(text);
    }

    public void setZipCodeTextHint(String text) {
        entry.setZipCodeTextHint(text);
    }
}

