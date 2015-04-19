package com.devmarvel.creditcardentry.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Display;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devmarvel.creditcardentry.R;
import com.devmarvel.creditcardentry.fields.CreditCardText;
import com.devmarvel.creditcardentry.fields.CreditEntryFieldBase;
import com.devmarvel.creditcardentry.fields.ExpDateText;
import com.devmarvel.creditcardentry.fields.SecurityCodeText;
import com.devmarvel.creditcardentry.fields.ZipCodeText;
import com.devmarvel.creditcardentry.internal.CreditCardUtil.CardType;
import com.devmarvel.creditcardentry.library.CardValidCallback;
import com.devmarvel.creditcardentry.library.CreditCard;

@SuppressLint("ViewConstructor")
public class CreditCardEntry extends HorizontalScrollView implements
		OnTouchListener, OnGestureListener, CreditCardFieldDelegate {

	private final Context context;
	private final boolean includeZip;

	private ImageView cardImage;
	private ImageView backCardImage;
	private final CreditCardText creditCardText;
	private final ExpDateText expDateText;
	private final SecurityCodeText securityCodeText;
	private final ZipCodeText zipCodeText;

	private final TextView textFourDigits;

	private TextView textHelper;

	private boolean showingBack;
	private CardValidCallback onCardValidCallback;

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public CreditCardEntry(Context context, boolean includeZip) {
		super(context);

		this.context = context;
		this.includeZip = includeZip;

		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();

		int width;

		if (android.os.Build.VERSION.SDK_INT < 13) {
			width = display.getWidth(); // deprecated
		} else {
			Point size = new Point();
			display.getSize(size);
			width = size.x;
		}

		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER_VERTICAL;
		setLayoutParams(params);
		this.setHorizontalScrollBarEnabled(false);
		this.setOnTouchListener(this);
		this.setSmoothScrollingEnabled(true);

		LinearLayout container = new LinearLayout(context);
		container.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		container.setOrientation(LinearLayout.HORIZONTAL);

		creditCardText = new CreditCardText(context);
		creditCardText.setDelegate(this);
		creditCardText.setWidth(width);
		container.addView(creditCardText);

		textFourDigits = new TextView(context);
		textFourDigits.setTextSize(20);
		container.addView(textFourDigits);

		expDateText = new ExpDateText(context);
		expDateText.setDelegate(this);
		container.addView(expDateText);

		securityCodeText = new SecurityCodeText(context);
		securityCodeText.setDelegate(this);
		container.addView(securityCodeText);

		zipCodeText = new ZipCodeText(context);
		if (includeZip) {
			zipCodeText.setDelegate(this);
			container.addView(zipCodeText);
		}

		this.addView(container);

		// when the user taps the last 4 digits of the card, they probably want to edit it
		textFourDigits.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				focusOnField(creditCardText);
			}
		});

		creditCardText.requestFocus();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return true;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		return true;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public void onCardTypeChange(CardType type) {
		cardImage.setImageResource(CreditCardUtil.cardImageForCardType(type, false));
		backCardImage.setImageResource(CreditCardUtil.cardImageForCardType(type, true));
		updateCardImage(false);
	}

	@Override
	public void onCreditCardNumberValid() {
		focusOnField(expDateText);

		String number = creditCardText.getText().toString();
		int length = number.length();
		String digits = number.substring(length - 4);
		textFourDigits.setText(digits);
	}

	@Override
	public void onExpirationDateValid() {
		focusOnField(securityCodeText);
	}

	@Override
	public void onSecurityCodeValid() {
		if(includeZip) {
			focusOnField(zipCodeText);
		} else {
			hideKeyboard();
			securityCodeText.clearFocus();
			if(onCardValidCallback != null) onCardValidCallback.cardValid(getCreditCard());
		}
	}

	@Override
	public void onZipCodeValid() {
		hideKeyboard();
		zipCodeText.clearFocus();
		if(onCardValidCallback != null) onCardValidCallback.cardValid(getCreditCard());
	}

	@Override
	public void onBadInput(final EditText field) {
		Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake);
		field.startAnimation(shake);
		field.setTextColor(Color.RED);

		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				field.setTextColor(Color.BLACK);
			}
		}, 1000);
	}

	public void setCardImageView(ImageView image) {
		cardImage = image;
	}

	private void updateCardImage(boolean back) {
		if (showingBack != back) {
			flipCardImage();
		}

		showingBack = back;
	}

	private void flipCardImage() {
		FlipAnimator animator = new FlipAnimator(cardImage, backCardImage,
				backCardImage.getWidth() / 2, backCardImage.getHeight() / 2);
		if (cardImage.getVisibility() == View.GONE) {
			animator.reverse();
		}
		cardImage.startAnimation(animator);
	}

	@Override
	public void focusOnField(final CreditEntryFieldBase field) {
		if (this.textHelper != null) {
			this.textHelper.setText(field.helperText());
		}

		View childAt = getChildAt(0);
		int childWidth = childAt == null ? 0 : childAt.getMeasuredWidth();
		if (field instanceof CreditCardText) {
			new CountDownTimer(500, 16) {
				public void onTick(long millisUntilFinished) 	{ scrollTo((int) (millisUntilFinished), 0); }
				public void onFinish() 												{
					scrollTo(0, 0);
					field.requestFocus();
				}
			}.start();
		} else if(getScrollX() + getWidth() < childWidth) {
			// if we're not already scrolled all the way right
			final int target = field.getLeft();
			final int duration = 500;
			new CountDownTimer(duration, 16) {
				final int startingPoint = getScrollX();

				public void onTick(long millisUntilFinished) {
					long increment = target * (duration - millisUntilFinished) / duration;
					long scrollTo = startingPoint + increment;
						scrollTo((int) scrollTo, 0);
				}

				public void onFinish() {
					scrollTo(target, 0);
					field.requestFocus();
				}
			}.start();
		} else {
			field.requestFocus();
		}

		if (field instanceof SecurityCodeText) {
			((SecurityCodeText) field).setType(creditCardText.getType());
			updateCardImage(true);
		} else {
			updateCardImage(false);
		}
	}

	@Override
	public void focusOnPreviousField(CreditEntryFieldBase field) {
		if (field instanceof ExpDateText) {
			focusOnField(creditCardText);
		} else if (field instanceof SecurityCodeText) {
			focusOnField(expDateText);
		} else if (field instanceof ZipCodeText) {
			focusOnField(securityCodeText);
		}
	}

	@SuppressWarnings("unused")
	public ImageView getBackCardImage() {
		return backCardImage;
	}

	public void setBackCardImage(ImageView backCardImage) {
		this.backCardImage = backCardImage;
	}

	@SuppressWarnings("unused")
	public TextView getTextHelper() {
		return textHelper;
	}

	public void setTextHelper(TextView textHelper) {
		this.textHelper = textHelper;
	}

	public boolean isCreditCardValid() {
		return creditCardText.isValid() && expDateText.isValid()
				&& securityCodeText.isValid() && (!includeZip || zipCodeText.isValid());
	}

	public CreditCard getCreditCard() {
		return new CreditCard(creditCardText.getText().toString(), expDateText.getText().toString(),
													securityCodeText.getText().toString(), zipCodeText.getText().toString());
	}

	private void hideKeyboard() {
		InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(this.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	public void setOnCardValidCallback(CardValidCallback onCardValidCallback) {
		this.onCardValidCallback = onCardValidCallback;
	}
}
