package com.devmarvel.creditcardentry.fields;

import android.R.color;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.EditText;
import android.widget.TextView;

import com.devmarvel.creditcardentry.R;
import com.devmarvel.creditcardentry.internal.CreditCardFieldDelegate;

import java.lang.reflect.Field;

public abstract class CreditEntryFieldBase extends EditText implements
        TextWatcher, OnKeyListener, OnClickListener {

    CreditCardFieldDelegate delegate;

    final Context context;

    String lastValue = null;

    private boolean valid = false;

    public CreditEntryFieldBase(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public CreditEntryFieldBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(attrs);
    }

    public CreditEntryFieldBase(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init(attrs);
    }

    void init() {
        init(null);
    }

	void init(AttributeSet attrs) {
		setGravity(Gravity.CENTER);
		setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
		setBackgroundColor(getResources().getColor(color.transparent));
		setInputType(InputType.TYPE_CLASS_NUMBER);
		addTextChangedListener(this);
		setOnKeyListener(this);
		setOnClickListener(this);
		setPadding(20, 0, 20, 0);

        setStyle(attrs);
    }

    void setStyle(AttributeSet attrs) {
        if (attrs == null) {
            return;
        }

		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CreditCardForm);
		// If CreditCardForm_default_text_colors is true, we will not set any text or cursor colors
		// and just use the defaults provided by the system / theme.
		if (!typedArray.getBoolean(R.styleable.CreditCardForm_default_text_colors, false)) {
			setTextColor(typedArray.getColor(R.styleable.CreditCardForm_text_color, Color.BLACK));
			setHintTextColor(typedArray.getColor(R.styleable.CreditCardForm_hint_text_color, Color.LTGRAY));
			setCursorDrawableColor(typedArray.getColor(R.styleable.CreditCardForm_cursor_color, Color.BLACK));
		}
		typedArray.recycle();
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int end) {
		if (start == 0 && before == 1 && s.length() == 0) {
			if (delegate != null) {
				delegate.focusOnPreviousField(this);
			}
		} else {
			String tmp = String.valueOf(s);
			if (!tmp.equals(lastValue)) {
				lastValue = tmp;
				textChanged(s, start, before, end);
			}
		}
	}

	public abstract void formatAndSetText(String updatedString);

    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    @Override public void afterTextChanged(Editable s) {}

    public void textChanged(CharSequence s, int start, int before, int end) { }

	@Override
	public InputConnection onCreateInputConnection(@NonNull EditorInfo outAttrs) {
		outAttrs.actionLabel = null;
		outAttrs.inputType = InputType.TYPE_NULL;
		outAttrs.imeOptions = EditorInfo.IME_ACTION_NONE;
		return new BackInputConnection(super.onCreateInputConnection(outAttrs));
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN)
			return false;
		if (keyCode == KeyEvent.KEYCODE_ALT_LEFT
				|| keyCode == KeyEvent.KEYCODE_ALT_RIGHT
				|| keyCode == KeyEvent.KEYCODE_SHIFT_LEFT
				|| keyCode == KeyEvent.KEYCODE_SHIFT_RIGHT)
			return false;

		if (keyCode == KeyEvent.KEYCODE_DEL
				&& this.getText().toString().length() == 0) {
			if (delegate != null) {
				delegate.focusOnPreviousField(this);
			}
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		setSelection(getText().length());
	}

	@SuppressWarnings("unused")
	public CreditCardFieldDelegate getDelegate() {
		return delegate;
	}

	public void setDelegate(CreditCardFieldDelegate delegate) {
		this.delegate = delegate;
	}

	public abstract void setHelperText(String helperText);

	public abstract String getHelperText();

	public boolean isValid() {
		return valid;
	}

	void setValid(boolean valid) {
		this.valid = valid;
	}

	private void backInput() {
		if (this.getText().toString().length() == 0) {
			if (delegate != null) {
				delegate.focusOnPreviousField(this);
			}
		}
	}

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
		bundle.putBoolean("focus", hasFocus());
		bundle.putString("stateToSave", String.valueOf(this.getText()));
        return bundle;
    }

	@Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            state = bundle.getParcelable("instanceState");
            super.onRestoreInstanceState(state);
            String cc = bundle.getString("stateToSave");
            setText(cc);
            boolean focus = bundle.getBoolean("focus", false);
            if (focus)
                requestFocus();
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    private class BackInputConnection extends InputConnectionWrapper {

        public BackInputConnection(InputConnection target) {
            super(target, false);
        }

        @Override
        public boolean sendKeyEvent(KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                backInput();
                // Un-comment if you wish to cancel the backspace:
                // return false;
            }
            return super.sendKeyEvent(event);
        }

        // From Android 4.1 this is called when the DEL key is pressed on the
        // soft keyboard (and
        // sendKeyEvent() is not called). We convert this to a "normal" key
        // event.
        @Override
        public boolean deleteSurroundingText(int beforeLength, int afterLength) {

            if (android.os.Build.VERSION.SDK_INT < 11) {
                return super.deleteSurroundingText(beforeLength, afterLength);
            } else {

                long eventTime = SystemClock.uptimeMillis();

                int flags = KeyEvent.FLAG_SOFT_KEYBOARD
                        | KeyEvent.FLAG_KEEP_TOUCH_MODE
                        | KeyEvent.FLAG_EDITOR_ACTION;

                sendKeyEvent(new KeyEvent(eventTime, eventTime,
                        KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL, 0, 0,
                        KeyCharacterMap.VIRTUAL_KEYBOARD, 0, flags));

                sendKeyEvent(new KeyEvent(SystemClock.uptimeMillis(),
                        eventTime, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL, 0,
                        0, KeyCharacterMap.VIRTUAL_KEYBOARD, 0, flags));
                return true;
            }
        }
    }

    public void setCursorDrawableColor(int color) {
        //http://stackoverflow.com/questions/25996032/how-to-change-programatically-edittext-cursor-color-in-android
        try {
            Field fCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            fCursorDrawableRes.setAccessible(true);
            int mCursorDrawableRes = fCursorDrawableRes.getInt(this);
            Field fEditor = TextView.class.getDeclaredField("mEditor");
            fEditor.setAccessible(true);
            Object editor = fEditor.get(this);
            Class<?> clazz = editor.getClass();
            Field fCursorDrawable = clazz.getDeclaredField("mCursorDrawable");
            fCursorDrawable.setAccessible(true);
            Drawable[] drawables = new Drawable[2];
            drawables[0] = ContextCompat.getDrawable(getContext(), mCursorDrawableRes);
            drawables[1] = ContextCompat.getDrawable(getContext(), mCursorDrawableRes);
            drawables[0].setColorFilter(color, PorterDuff.Mode.SRC_IN);
            drawables[1].setColorFilter(color, PorterDuff.Mode.SRC_IN);
            fCursorDrawable.set(editor, drawables);
        } catch (final Throwable ignored) {
            //
        }
    }

}
