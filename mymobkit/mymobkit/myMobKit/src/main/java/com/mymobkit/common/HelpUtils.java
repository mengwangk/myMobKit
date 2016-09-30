package com.mymobkit.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.mymobkit.R;

/**
 * This is a set of helper methods for showing contextual help information in the app.
 */
public final class HelpUtils {
	public static void showAbout(final Activity activity) {
		FragmentManager fm = activity.getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment prev = fm.findFragmentByTag("dialog_about");
		if (prev != null) {
			ft.remove(prev);
		}
		ft.addToBackStack(null);

		new AboutDialog().show(ft, "dialog_about");
	}

	public static void showUpgrade(final Activity activity) {
        FragmentManager fm = activity.getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("dialog_about");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        new UpgradeDialog().show(ft, "dialog_about");
	}


	public static class AboutDialog extends DialogFragment {

		private static final String VERSION_UNAVAILABLE = "N/A";

		public AboutDialog() {
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Get app version
			PackageManager pm = getActivity().getPackageManager();
			String packageName = getActivity().getPackageName();
			String versionName;
			try {
				PackageInfo info = pm.getPackageInfo(packageName, 0);
				versionName = info.versionName;
			} catch (PackageManager.NameNotFoundException e) {
				versionName = VERSION_UNAVAILABLE;
			}

			// Build the about body view and append the link to see OSS licenses
			SpannableStringBuilder aboutBody = new SpannableStringBuilder();
			aboutBody.append(Html.fromHtml(getString(R.string.about_body, versionName)));

			LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			TextView aboutBodyView = (TextView) layoutInflater.inflate(R.layout.dialog_about, null);
			aboutBodyView.setText(aboutBody);
			aboutBodyView.setMovementMethod(new LinkMovementMethod());

			return new AlertDialog.Builder(getActivity()).setTitle(R.string.label_title_about).setView(aboutBodyView).setPositiveButton(R.string.label_dialog_ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.dismiss();
				}
			}).create();
		}
	}

    public static class UpgradeDialog extends DialogFragment {

        public UpgradeDialog() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Build the about body view and append the link to see OSS licenses
            SpannableStringBuilder aboutBody = new SpannableStringBuilder();
            aboutBody.append(Html.fromHtml(getString(R.string.upgrade_body)));

            LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            TextView textView = (TextView) layoutInflater.inflate(R.layout.dialog_about, null);
            textView.setText(aboutBody);
            textView.setMovementMethod(new LinkMovementMethod());

            return new AlertDialog.Builder(getActivity()).setTitle(R.string.label_title_upgrade).setView(textView).setPositiveButton(R.string.label_dialog_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.dismiss();
                }
            }).create();
        }
    }
}
