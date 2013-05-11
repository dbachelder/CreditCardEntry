package com.devmarvel.creditcardentry.fields;

import android.R.color;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.SystemClock;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.devmarvel.creditcardentry.internal.CreditCardUtil.CreditCardFieldDelegate;

public abstract class CreditEntryFieldBase extends EditText implements
		TextWatcher, OnKeyListener, OnClickListener {

	protected CreditCardFieldDelegate delegate;

	protected Context context;
	
	private boolean valid = false;

	public CreditEntryFieldBase(Context context) {
		super(context);
		this.context = context;
		init();
	}

	public CreditEntryFieldBase(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	public CreditEntryFieldBase(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		init();
	}

	public void init() {
		setGravity(Gravity.CENTER);
		setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
		setBackgroundColor(color.transparent);
		setFocusableInTouchMode(false);
		setInputType(InputType.TYPE_CLASS_NUMBER);
		addTextChangedListener(this);
		setOnKeyListener(this);
		setOnClickListener(this);

	}

	public void onTextChanged(CharSequence s, int start, int before, int end) {
		if (start == 0 && before == 1 && s.length() == 0) {
			if (delegate != null) {
				delegate.focusOnPreviousField(this);
			}
		}
	}

	@Override
	public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
		outAttrs.actionLabel = null;
		outAttrs.inputType = InputType.TYPE_NULL;
		outAttrs.imeOptions = EditorInfo.IME_ACTION_NONE;
		return new BackInputConnection(super.onCreateInputConnection(outAttrs),
				false);
	}

	@SuppressLint("InlinedApi")
	private class BackInputConnection extends InputConnectionWrapper {

		public BackInputConnection(InputConnection target, boolean mutable) {
			super(target, mutable);
		}

		@Override
		public boolean sendKeyEvent(KeyEvent event) {
			if (event.getAction() == KeyEvent.ACTION_DOWN
					&& event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
				CreditEntryFieldBase.this.backInput();
				// Un-comment if you wish to cancel the backspace:
				// return false;
			}
			return super.sendKeyEvent(event);
		}

		// From Android 4.1 this is called when the DEL key is pressed on the
		// soft keyboard (and
		// sendKeyEvent() is not called). We convert this to a "normal" key
		// event.
		@SuppressLint("InlinedApi")
		@Override
		public boolean deleteSurroundingText(int beforeLength, int afterLength) {
			int currentapiVersion = android.os.Build.VERSION.SDK_INT;

			if (currentapiVersion < 11) {
				return super.deleteSurroundingText(beforeLength, afterLength);
			} else {

				long eventTime = SystemClock.uptimeMillis();
				sendKeyEvent(new KeyEvent(eventTime, eventTime,
						KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL, 0, 0,
						KeyCharacterMap.VIRTUAL_KEYBOARD, 0,
						KeyEvent.FLAG_SOFT_KEYBOARD
								| KeyEvent.FLAG_KEEP_TOUCH_MODE
								| KeyEvent.FLAG_EDITOR_ACTION));
				sendKeyEvent(new KeyEvent(SystemClock.uptimeMillis(),
						eventTime, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL, 0,
						0, KeyCharacterMap.VIRTUAL_KEYBOARD, 0,
						KeyEvent.FLAG_SOFT_KEYBOARD
								| KeyEvent.FLAG_KEEP_TOUCH_MODE
								| KeyEvent.FLAG_EDITOR_ACTION));
				return true;
			}
		}
	}

	public void backInput() {
		if (this.getText().toString().length() == 0) {
			if (delegate != null) {
				delegate.focusOnPreviousField(this);
			}
		}
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
		setFocusableInTouchMode(true);
		requestFocus();
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT);
		setSelection(getText().toString().length());
		setFocusableInTouchMode(false);
	}

	public CreditCardFieldDelegate getDelegate() {
		return delegate;
	}

	public void setDelegate(CreditCardFieldDelegate delegate) {
		this.delegate = delegate;
	}

	public abstract String helperText();

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

}
