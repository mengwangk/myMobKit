package com.mymobkit.preference;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;

public final class LockPatternDialogPreference extends DialogPreference
{
	public LockPatternDialogPreference(Context context)
	{
		this(context, null);
	}

	public LockPatternDialogPreference(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	@Override
	protected void onBindDialogView(View view)
	{
		super.onBindDialogView(view);
	}
}
