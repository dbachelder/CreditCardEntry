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
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
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
import com.devmarvel.creditcardentry.library.CardType;
import com.devmarvel.creditcardentry.library.CardValidCallback;
import com.devmarvel.creditcardentry.library.CreditCard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("ViewConstructor")
public class CreditCardEntry extends HorizontalScrollView implements
        OnTouchListener, OnGestureListener, CreditCardFieldDelegate {

    private final Context context;

    private ImageView cardImage;
    private ImageView backCardImage;
    private final CreditCardText creditCardText;
    private final ExpDateText expDateText;
    private final SecurityCodeText securityCodeText;
    private final ZipCodeText zipCodeText;

    private Map<CreditEntryFieldBase, CreditEntryFieldBase> nextFocusField = new HashMap<>(4);
    private Map<CreditEntryFieldBase, CreditEntryFieldBase> prevFocusField = new HashMap<>(4);
    private List<CreditEntryFieldBase> includedFields = new ArrayList<>(4);

    private final TextView textFourDigits;

    private TextView textHelper;

    private boolean showingBack;
    private boolean scrolling = false;

    private CardValidCallback onCardValidCallback;

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public CreditCardEntry(Context context, boolean includeExp, boolean includeSecurity, boolean includeZip) {
        super(context);

        this.context = context;

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
        includedFields.add(creditCardText);
        CreditEntryFieldBase currentField = creditCardText;

        textFourDigits = new TextView(context);
        textFourDigits.setTextSize(20);
        container.addView(textFourDigits);

        expDateText = new ExpDateText(context);
        if (includeExp) {
            expDateText.setDelegate(this);
            container.addView(expDateText);
            nextFocusField.put(currentField, expDateText);
            prevFocusField.put(expDateText, currentField);
            currentField = expDateText;
            includedFields.add(currentField);
        }

        securityCodeText = new SecurityCodeText(context);
        if (includeSecurity) {
            securityCodeText.setDelegate(this);
            if (!includeZip) {
                securityCodeText.setImeActionLabel("Done", EditorInfo.IME_ACTION_DONE);
            }

            securityCodeText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (EditorInfo.IME_ACTION_DONE == actionId) {
                        onSecurityCodeValid();
                        return true;
                    }
                    return false;
                }
            });
            container.addView(securityCodeText);
            nextFocusField.put(currentField, securityCodeText);
            prevFocusField.put(securityCodeText, currentField);
            currentField = securityCodeText;
            includedFields.add(currentField);
        }

        zipCodeText = new ZipCodeText(context);
        if (includeZip) {
            zipCodeText.setDelegate(this);
            container.addView(zipCodeText);
            zipCodeText.setImeActionLabel("DONE", EditorInfo.IME_ACTION_DONE);
            zipCodeText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (EditorInfo.IME_ACTION_DONE == actionId) {
                        onZipCodeValid();
                        return true;
                    }
                    return false;
                }
            });
            nextFocusField.put(currentField, zipCodeText);
            prevFocusField.put(zipCodeText, currentField);
            currentField = zipCodeText;
            includedFields.add(currentField);
        }

        nextFocusField.put(currentField, null);

        this.addView(container);

        // when the user taps the last 4 digits of the card, they probably want to edit it
        textFourDigits.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                focusOnField(creditCardText);
            }
        });
    }

    @Override
    public void onCardTypeChange(CardType type) {
        cardImage.setImageResource(type.frontResource);
        backCardImage.setImageResource(type.backResource);
        updateCardImage(false);
    }

    @Override
    public void onCreditCardNumberValid() {
        nextField(this.creditCardText);

        updateLast4();
    }

    @Override
    public void onExpirationDateValid() {
        nextField(this.expDateText);
    }

    @Override
    public void onSecurityCodeValid() {
        nextField(securityCodeText);
        updateCardImage(false);
    }

    @Override
    public void onZipCodeValid() {
        nextField(zipCodeText);
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

    public void setCardNumberHint(String hint) {
        creditCardText.setHint(hint);
    }

    public void setCardImageView(ImageView image) {
        cardImage = image;
    }

    @Override
    public void setOnFocusChangeListener(OnFocusChangeListener l) {
        creditCardText.setOnFocusChangeListener(l);
        expDateText.setOnFocusChangeListener(l);
        securityCodeText.setOnFocusChangeListener(l);
        zipCodeText.setOnFocusChangeListener(l);
    }

    @Override
    public void focusOnField(final CreditEntryFieldBase field) {
        if (this.textHelper != null) {
            this.textHelper.setText(field.helperText());
        }

        if (!scrolling) {
            View childAt = getChildAt(0);
            int childWidth = childAt == null ? 0 : childAt.getMeasuredWidth();
            if (field instanceof CreditCardText) {
                scrolling = true;
                new CountDownTimer(300, 16) {
                    public void onTick(long millisUntilFinished) {
                        scrollTo((int) (millisUntilFinished), 0);
                    }

                    public void onFinish() {
                        scrollTo(0, 0);
                        field.requestFocus();
                        scrolling = false;
                    }
                }.start();
            } else if (getScrollX() + getWidth() < childWidth) {
                scrolling = true;
                // if we're not already scrolled all the way right
                final int target = field.getLeft();
                final int duration = 400;
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
                        scrolling = false;
                    }
                }.start();
            } else {
                field.requestFocus();
            }
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
        CreditEntryFieldBase view = prevFocusField.get(field);
        if (view != null) {
            focusOnField(view);
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
        for (CreditEntryFieldBase includedField : includedFields) {
            if (!includedField.isValid()) return false;
        }
        return true;
    }

    /**
     * set the card number will auto focus next field if param is true
     */
    public void setCardNumber(String cardNumber, boolean nextField) {
        final CreditCardFieldDelegate delegate;
        if (!nextField) {
            delegate = creditCardText.getDelegate();
            // temp delegate that only deals with type.. this sucks.. TODO gut this delegate business
            creditCardText.setDelegate(new CreditCardFieldDelegate() {
                @Override
                public void onCardTypeChange(CardType type) {
                    delegate.onCardTypeChange(type);
                }

                @Override
                public void onCreditCardNumberValid() {
                    updateLast4();
                }

                @Override
                public void onBadInput(EditText field) {
                    delegate.onBadInput(field);
                }

                @Override public void onExpirationDateValid() {}
                @Override public void onSecurityCodeValid() {}
                @Override public void onZipCodeValid() { }
                @Override public void focusOnField(CreditEntryFieldBase field) { }
                @Override public void focusOnPreviousField(CreditEntryFieldBase field) { }
            });
        } else {
            delegate = null;
        }

        creditCardText.setText(cardNumber);
        if (!nextField) {
            creditCardText.setDelegate(delegate);
        }
    }

    public void clearAll() {
        creditCardText.setText("");
        expDateText.setText("");
        securityCodeText.setText("");
        zipCodeText.setText("");
        creditCardText.clearFocus();
        expDateText.clearFocus();
        securityCodeText.clearFocus();
        zipCodeText.clearFocus();

        scrollTo(0, 0);
    }

    public CreditCard getCreditCard() {
        return new CreditCard(creditCardText.getText().toString(), expDateText.getText().toString(),
                securityCodeText.getText().toString(), zipCodeText.getText().toString(),
                creditCardText.getType());
    }

    /**
     * request focus for the credit card field
     */
    public void focusCreditCard() {
        focusOnField(creditCardText);
    }

    /**
     * request focus for the expiration field
     */
    public void focusExp() {
        if (includedFields.contains(expDateText)) {
            focusOnField(expDateText);
        }
    }

    /**
     * request focus for the security code field
     */
    public void focusSecurityCode() {
        if (includedFields.contains(securityCodeText)) {
            focusOnField(securityCodeText);
        }
    }

    /**
     * request focus for the zip field (IF it's enabled)
     */
    public void focusZip() {
        if (includedFields.contains(zipCodeText)) {
            focusOnField(zipCodeText);
        }
    }

    private void updateLast4() {
        String number = creditCardText.getText().toString();
        int length = number.length();
        String digits = number.substring(length - 4);
        textFourDigits.setText(digits);
    }

    private void nextField(CreditEntryFieldBase currentField) {
        CreditEntryFieldBase next = nextFocusField.get(currentField);
        if (next == null) {
            entryComplete(currentField);
        } else {
            focusOnField(next);
        }
    }

    private void entryComplete(View clearField) {
        hideKeyboard();
        clearField.clearFocus();
        if (onCardValidCallback != null) onCardValidCallback.cardValid(getCreditCard());
    }

    private void updateCardImage(boolean back) {
        if (showingBack != back) {
            flipCardImage();
        }

        showingBack = back;
    }

    private void flipCardImage() {
        FlipAnimator animator = new FlipAnimator(cardImage, backCardImage);
        if (cardImage.getVisibility() == View.GONE) {
            animator.reverse();
        }
        cardImage.startAnimation(animator);
    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void setOnCardValidCallback(CardValidCallback onCardValidCallback) {
        this.onCardValidCallback = onCardValidCallback;
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
    public void onLongPress(MotionEvent e) {}

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override public void onShowPress(MotionEvent e) {}

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }
}
