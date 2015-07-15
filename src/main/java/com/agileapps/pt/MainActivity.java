package com.agileapps.pt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.simpleframework.xml.core.Persister;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.agileapps.pt.manager.FormTemplateManager;
import com.agileapps.pt.pojos.FormTemplate;
import com.agileapps.pt.pojos.FormTemplatePart;
import com.agileapps.pt.pojos.InputType;
import com.agileapps.pt.pojos.QuestionAnswer;
import com.agileapps.pt.util.PDFWriter;
import com.agileapps.pt.util.PhysicalTherapyUtils;

public class MainActivity extends FragmentActivity {

	public static final String ARG_SECTION_NUMBER = "section_number";
	public static final String HOME_WIDGET = "homeWidget";
	public static final String HOME_WIDGET_TYPE = "homeWidgetType";
	public static final String HOME_WIDGET_VALUE = "homeWidgetValue";
	public static final String HOME_WIDGET_TYPE_INTEGER = "integer";
	public static final String HOME_WIDGET_TYPE_TEXT = "text";
	public static final String PT_APP_INFO = "ptAppInfo";
	public static final String PRINTER_INFO = "printerInfo";
	public static final int REQ_CODE_SPEECH_INPUT = 1;
	public static final int REQUEST_ACCOUNT_PICKER = 2;
	public static final int REQUEST_AUTHORIZATION = 3;
	public static final int RESULT_STORE_FILE = 4;
	public static final String FORM_DIR = "pt_forms";
	static int idCounter = 90000;
	public static final String FRAGMENT_PREFIX = "template_part_";

	InputType answerWidgetDataType = null;
	Integer answerWidgetId = null;
	ViewPager mViewPager;

	private FragmentStatePagerAdapter sectionsPagerAdapter;

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
			Log.e(PT_APP_INFO, "Cannot load template because " + ex, ex);
			return null;
		}
	}

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			FragmentManager.enableDebugLogging(true);
			setContentView(R.layout.activity_main);
			createFragments();
		} catch (Exception ex) {
			String errorStr = "Cannot initialize physical therapy because "
					+ ex.getMessage();
			Log.e(PT_APP_INFO, errorStr,ex);
			Toast toast = Toast.makeText(getApplicationContext(), errorStr,
					Toast.LENGTH_LONG);
			toast.show();
		}
	}

	private void createFragments(){
		FormTemplate formTemplate = getFormTemplate();
		if (formTemplate == null) {
			return;
		}
		FragmentManager fragmentManager = this.getSupportFragmentManager();
		sectionsPagerAdapter = new SectionsPagerAdapter(fragmentManager);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(sectionsPagerAdapter);
		for (int ii = 0; ii < formTemplate.getFormTemplatePartList().size(); ii++) {
			getFragment(fragmentManager, ii);
		}
	}
	
	private Fragment getFragment(FragmentManager fragmentManager,int position) {
		String tag = FRAGMENT_PREFIX +position;
		Fragment fragment = fragmentManager.findFragmentByTag(tag);
		if ( fragment != null){
			return fragment;
		}
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		GenericFragment genericFragment = new GenericFragmentImpl();
		fragmentTransaction.add(R.id.pager, genericFragment, tag);
		fragmentTransaction.commit();
		return genericFragment;
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
			case R.id.clear_template:
				formTemplate.clear(true);
				clearWidgets(formTemplate,true);
				break;
			case R.id.clear_template_partial:
				formTemplate.clear(false);
				clearWidgets(formTemplate,false);
				break;	
			case R.id.save_template:
				saveForm(formTemplate);
				break;
			case R.id.retrieve_or_delete:
				Intent retrieveFormIntent = new Intent(this,
						FormChooserActivity.class);
				startActivity(retrieveFormIntent);
				finish();
				break;
			case R.id.download_templates:
				Intent downloadTemplatesIntent = new Intent(this,
						TemplateDownloaderActivity.class);
				startActivity(downloadTemplatesIntent);
				finish();
				break;
			case R.id.action_settings:
				Intent settingsIntent = new Intent(this,
						ConfigurationActivity.class);
				startActivity(settingsIntent);
				finish();
				break;
			case R.id.change_template:
				Intent changeTemplatesIntent = new Intent(this,
						ChangeTemplateActivity.class);
				startActivity(changeTemplatesIntent);
				finish();
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

	private void saveForm(FormTemplate formTemplate) throws IOException {
		FileOutputStream fos = null;
		try {
			String filePath = PhysicalTherapyUtils.getFilePath(formTemplate)
					.getAbsolutePath();
			File filesDir = new File(Environment.getExternalStorageDirectory(),
					FORM_DIR);
			File formFile = new File(filesDir, filePath);
			if (!filesDir.exists()) {
				if (!filesDir.mkdir()) {
					Log.e(PT_APP_INFO, "Unable to create pt_forms directory");
				}
			}
			if (!formFile.getParentFile().mkdirs()) {
				Log.e(PT_APP_INFO, "Cannot create directories "
						+ formFile.getParentFile().getAbsolutePath());
			}
			fos = new FileOutputStream(formFile);
			Persister persister = new Persister();
			persister.write(formTemplate, fos);
		} catch (Exception ex) {
			Log.e(PT_APP_INFO, "Unable to save form because  " + ex);
		} finally {
			if (fos != null) {
				fos.close();
			}
		}
	}

	private void clearWidgets(FormTemplate formTemplate,boolean all) {
		int cntr=0;
		for (FormTemplatePart formTemplatePart : formTemplate
				.getFormTemplatePartList()) {
			if ( !all && cntr == 0){
				cntr++;
				continue;
			}
			for (QuestionAnswer questionAnswer : formTemplatePart
					.getQuestionAnswerList()) {
				Integer widgetIds[] = questionAnswer.getWidgetIds();
				for (Integer widgetId : widgetIds) {
					View view = this.findViewById(widgetId);
					if (view != null) {
						if (view instanceof EditText) {
							((EditText) view).setText("");
						} else if (view instanceof CheckBox) {
							CheckBox checkBox = (CheckBox) view;
							checkBox.setChecked(false);
						} else if (view instanceof RadioGroup) {
							RadioGroup radioGroup = (RadioGroup) view;
							for (int ii = 0; ii < radioGroup.getChildCount(); ii++) {
								RadioButton radioButton = (RadioButton) radioGroup
										.getChildAt(ii);
								radioButton.setChecked(false);
							}

						}
					}
				}
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i(PT_APP_INFO, "main activity  being restored");
	}

	
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}



	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(PT_APP_INFO, "main activity  being destroyed");
	}

	public class SectionsPagerAdapter extends FragmentStatePagerAdapter{

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
			super.restoreState(state, loader);
			Log.i(PT_APP_INFO,
					"fragement manager state being restored with state  "
							+ state);
		}

		@Override
		public Fragment getItem(int position) {
			FragmentManager fragmentManager = getSupportFragmentManager();
			return MainActivity.this.getFragment(fragmentManager, position);
		}

		@Override
		public int getCount() {
			FormTemplate formTemplate=getFormTemplate();
			return formTemplate.getFormTemplatePartList().size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			FormTemplate formTemplate = getFormTemplate();
			if (position < formTemplate.getFormTemplatePartList().size()) {
				return formTemplate.getFormTemplatePartList().get(position)
						.getTitle();
			} else {
				Log.e(PT_APP_INFO,
						"Requesting a template part that does not exist!  Position "
								+ position + " number of template panels "
								+ formTemplate.getFormTemplatePartList().size());
				return "Not found";
			}
		}
	}

	public static class GenericFragmentImpl extends GenericFragment {
		public GenericFragmentImpl() {
			super();
			// layoutId = R.layout.generic_fragment;
			// tableLayoutId = R.id.tableZeroLayout;
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQ_CODE_SPEECH_INPUT:
			if (resultCode == RESULT_OK && null != data) {
				ArrayList<String> result = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				String inputStr = result.get(0).trim();
				if (answerWidgetId == null) {
					Log.e(PT_APP_INFO,
							"Widget id is null. Cant set text box without it");
					return;
				}
				if (answerWidgetDataType == null) {
					Log.e(PT_APP_INFO,
							"Input type is null. Cant set text box without it");
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

	public static int getUniqueWidgetId(Context context) {
		AppWidgetProviderInfo appWidgetInfo = null;
		int counter = 0;
		while (appWidgetInfo == null && counter < 10000) {
			counter++;
			appWidgetInfo = AppWidgetManager
					.getInstance(context).getAppWidgetInfo(
							MainActivity.idCounter);
			if (appWidgetInfo == null) {
				int retId = MainActivity.idCounter;
				MainActivity.idCounter++;
				return retId;
			}
		}
		throw new RuntimeException(
				"Cannot find uniqueId to assign to widget");
	}

}
