package com.mymobkit.preference;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.Button;

public final class NonEmptyEditTextPreference extends EditTextPreference
{
	public NonEmptyEditTextPreference(Context ctx, AttributeSet attrs, int defStyle)
	{
		super(ctx, attrs, defStyle);
	}

	public NonEmptyEditTextPreference(Context ctx, AttributeSet attrs)
	{
		super(ctx, attrs);
	}

	private class EditTextWatcher implements TextWatcher
	{
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int before, int count) {
		}

		@Override
		public void afterTextChanged(Editable s)
		{
			onEditTextChanged();
		}
	}

	EditTextWatcher textWatcher = new EditTextWatcher();

	/**
	 * Return true in order to enable positive button or false to disable it.
	 */
	protected boolean onCheckValue(String value)
	{
		if (value != null) value = value.trim();
		return !TextUtils.isEmpty(value);
	}

	protected void onEditTextChanged()
	{
		boolean enable = onCheckValue(getEditText().getText().toString());
		Dialog dlg = getDialog();
		if (dlg instanceof AlertDialog)
		{
			AlertDialog alertDlg = (AlertDialog) dlg;
			Button btn = alertDlg.getButton(AlertDialog.BUTTON_POSITIVE);
			btn.setEnabled(enable);
		}
	}

	@Override
	protected void showDialog(Bundle state)
	{
		super.showDialog(state);

		getEditText().removeTextChangedListener(textWatcher);
		getEditText().addTextChangedListener(textWatcher);
		onEditTextChanged();
	}
}