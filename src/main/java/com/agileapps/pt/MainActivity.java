package com.agileapps.pt;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.agileapps.pt.pojos.FormTemplate;
import com.agileapps.pt.pojos.InputType;
import com.agileapps.pt.util.PDFWriter;

public class MainActivity extends FragmentActivity {

	static int idCounter = 90000;

	public static final String ARG_SECTION_NUMBER = "section_number";
	public static final String HOME_WIDGET = "homeWidget";
	public static final String HOME_WIDGET_TYPE = "homeWidgetType";
	public static final String HOME_WIDGET_VALUE = "homeWidgetValue";
	public static final String HOME_WIDGET_TYPE_INTEGER = "integer";
	public static final String HOME_WIDGET_TYPE_TEXT = "text";
	public static final String PT_APP_INFO = "ptAppInfo";
	public static final String PRINTER_INFO = "printerInfo";

	SectionsPagerAdapter mSectionsPagerAdapter;

	InputType answerWidgetDataType= null;
	Integer answerWidgetId=null;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	private FormTemplate getFormTemplate() {
		FormTemplate formTemplate = null;
		try {
			formTemplate = FormTemplateManager.getFormTemplate();
			if (formTemplate == null) {
				Toast toast = Toast.makeText(getApplicationContext(),
						"Cannot load form template", Toast.LENGTH_LONG);
				toast.show();
			}
			return formTemplate;
		} catch (Throwable ex) {
			Toast toast = Toast.makeText(getApplicationContext(),
					"Cannot load template file because " + ex.getMessage(),
					Toast.LENGTH_LONG);
			toast.show();
			Log.e(PT_APP_INFO, "Cannot load template because " + ex,
					ex);
			return null;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			setContentView(R.layout.activity_main);
			FormTemplate formTemplate = getFormTemplate();
			if (formTemplate == null) {
				return;
			}
			FragmentManager fragmentManager = this.getSupportFragmentManager();
			mSectionsPagerAdapter = new SectionsPagerAdapter(fragmentManager);

			// Set up the ViewPager with the sections adapter.
			mViewPager = (ViewPager) findViewById(R.id.pager);
			mViewPager.setAdapter(mSectionsPagerAdapter);
		} catch (Exception ex) {
			String errorStr = "Cannot initialize physical therapy because "
					+ ex.getMessage();
			Log.e(PT_APP_INFO, errorStr);
			Toast toast = Toast.makeText(getApplicationContext(), errorStr,
					Toast.LENGTH_LONG);
			toast.show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		FormTemplate formTemplate = getFormTemplate();
		if (formTemplate == null) {
			return false;
		}
		// Handle item selection
		try {
			switch (item.getItemId()) {
			case R.id.print_template:
				try {
					PrintDocumentAdapter printDocumentAdapter = PDFWriter
							.getPrinterAdapter(this, formTemplate);
					PrintManager printManager = (PrintManager) this
							.getSystemService(Context.PRINT_SERVICE);
					printManager.print(("pt-" + new java.util.Date()),
							printDocumentAdapter, null);
				} catch (Throwable ex) {
					Toast.makeText(this, "Unable to print form because " + ex,
							Toast.LENGTH_LONG).show();
				}
				break;
			case R.id.exit_app:
				finish();
				break;
			}
		} catch (Throwable ex) {
			Toast toast = Toast.makeText(this.getApplicationContext(),
					"Cannot print file because " + ex, Toast.LENGTH_LONG);
			toast.show();

		}
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i(PT_APP_INFO,"main activity  being restored");
	}

	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(PT_APP_INFO,"main activity  being destroyed");
	}
	
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		
		
		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
			super.restoreState(state, loader);
			Log.i(PT_APP_INFO,"fragement manager state being restored with state  "+state);
		}



		@Override
		public Fragment getItem(int position) {
			Fragment fragment = null;
			switch (position) {
			case 0:
				Part0 part0 = new Part0();
				part0.setTemplate(position);
				fragment = part0;
				break;
			case 1:
				Part1 part1 = new Part1();
				part1.setTemplate(position);
				fragment = part1;
				break;
			case 2:
				Part2 part2 = new Part2();
				part2.setTemplate(position);
				fragment = part2;
				break;
			case 3:
				Part3 part3 = new Part3();
				part3.setTemplate(position);
				fragment = part3;
				break;

			default:
				part0 = new Part0();
				part0.setTemplate(position);
				fragment = part0;
			}
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			return getFormTemplate().getFormTemplatePartList().size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			FormTemplate formTemplate=getFormTemplate();
			if ( position < formTemplate.getFormTemplatePartList().size())
			{
			return formTemplate.getFormTemplatePartList().get(position).getTitle();
			}else{
				Log.e(PT_APP_INFO,"Requesting a template part that does not exist!  Position "+position+" number of template panels "
			       +formTemplate.getFormTemplatePartList().size());
				return "Not found";
			}
		}
	}



	public static class Part0 extends GenericFragment {
		public Part0() {
			super();
			layoutId = R.layout.part_0;
			tableLayoutId = R.id.tableZeroLayout;
		}
	}

	public static class Part1 extends GenericFragment {
		public Part1() {
			super();
			layoutId = R.layout.part_1;
			tableLayoutId = R.id.tableOneLayout;
		}
	}

	public static class Part2 extends GenericFragment {
		public Part2() {
			super();
			layoutId = R.layout.part_2;
			tableLayoutId = R.id.tableTwoLayout;
		}
	}

	public static class Part3 extends GenericFragment {
		public Part3() {
			super();
			layoutId = R.layout.part_3;
			tableLayoutId = R.id.tableThreeLayout;
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case SpeechButtonClickListener.REQ_CODE_SPEECH_INPUT: {
			if (resultCode == RESULT_OK && null != data) {

				ArrayList<String> result = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				String inputStr = result.get(0).trim();
	            if (answerWidgetId == null){
                	Log.e(PT_APP_INFO,"Widget id is null. Cant set text box without it");
                	return;
                }
                if (answerWidgetDataType == null){
                	Log.e(PT_APP_INFO,"Input type is null. Cant set text box without it");
                	return;
                }

				if (answerWidgetDataType == InputType.INTEGER) {
					inputStr = "NA";
					for (String aResult : result) {
						try {
							aResult = aResult.trim();
							Integer.parseInt(aResult);
							inputStr = aResult;
							break;
						} catch (Exception ex) {
							// ignore
						}
					}
				}
				EditText numberInput = (EditText) this
						.findViewById(answerWidgetId);
				numberInput.setText(inputStr);
			}
			break;
		}

		}
	}

}
