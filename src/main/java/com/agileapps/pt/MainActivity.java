package com.agileapps.pt;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.agileapps.pt.pojos.FormTemplate;
import com.agileapps.pt.pojos.FormTemplatePart;
import com.agileapps.pt.pojos.InputType;
import com.agileapps.pt.pojos.QuestionAnswer;
import com.agileapps.pt.util.PDFWriter;
import com.agileapps.pt.util.PhysicalTherapyUtils;

public class MainActivity extends FragmentActivity {

	private static int idCounter = 90000;
	private static final int REQ_CODE_SPEECH_INPUT = 1;
	public static final String ARG_SECTION_NUMBER = "section_number";
	public static final String HOME_WIDGET = "homeWidget";
	public static final String HOME_WIDGET_TYPE = "homeWidgetType";
	public static final String HOME_WIDGET_VALUE = "homeWidgetValue";
	public static final String HOME_WIDGET_TYPE_INTEGER = "integer";
	public static final String HOME_WIDGET_TYPE_TEXT = "text";
	private static final String INITIALIZATION_ERROR = "initError";
	private static int answerWidgetId = -1;
	private static InputType answerWidgetDataType = InputType.TEXT;

	SectionsPagerAdapter mSectionsPagerAdapter;

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
			Log.e(INITIALIZATION_ERROR, "Cannot load template because " + ex,
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
			Log.e(INITIALIZATION_ERROR, errorStr);
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

	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
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
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section0).toUpperCase(l);
			case 1:
				return getString(R.string.title_section1).toUpperCase(l);
			case 2:
				return getString(R.string.title_section2).toUpperCase(l);
			case 3:
				return getString(R.string.title_section3).toUpperCase(l);
			}

			return null;
		}
	}

	public abstract static class GenericFragment extends Fragment {
		protected int position;
		protected FormTemplatePart formTemplatePart;
		protected int layoutId;
		protected int tableLayoutId;

		public void setTemplate(int position) {
			this.position = position;
			this.formTemplatePart = getFormTemplate().getFormTemplatePartList().get(
					position);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(layoutId, container, false);
			TableLayout tableLayout = (TableLayout) rootView
					.findViewById(tableLayoutId);
			for (final QuestionAnswer questionAnswer : formTemplatePart
					.getQuestionAnswerList()) {
				try {
					TableRow tableRow = new TableRow(this.getActivity());
					TableRow.LayoutParams lp = new TableRow.LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.MATCH_PARENT,
							LayoutParams.MATCH_PARENT);
					tableRow.setLayoutParams(lp);
					tableLayout.addView(tableRow);
					TextView questionView = new TextView(this.getActivity());
					questionView.setText(questionAnswer.getQuestion());
					questionView.setTextSize(25f);
					tableRow.addView(questionView);
					if (questionAnswer.getInputType() != InputType.CHECKBOX
							&& questionAnswer.getInputType() != InputType.RADIO) {
						addTextBox(tableRow, questionAnswer);
					} else if (questionAnswer.getInputType() == InputType.CHECKBOX) {
						addCheckBox(tableRow, questionAnswer);
					} else {
						addRadio(tableRow, questionAnswer);
					}
				} catch (Exception ex) {
					Log.e(INITIALIZATION_ERROR,
							"Unable to initialize fragment because " + ex, ex);
				}
			}
			return rootView;
		}

		private void addCheckBox(TableRow tableRow,
				QuestionAnswer questionAnswer) {
			for (String value : questionAnswer.getChoiceList()) {
				CheckBox checkBox = new CheckBox(this.getActivity());
				checkBox.setText(value);
				tableRow.addView(checkBox);
				int widgetId = getUniqueWidgetId();
				checkBox.setId(widgetId);
				checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					public void onCheckedChanged(CompoundButton compoundButton,
							boolean arg1) {
						CheckBox checkBox = (CheckBox) compoundButton;
						String text = (new StringBuilder()).append(checkBox.getText()).toString();
						FormTemplate formTemplate = getFormTemplate();
						QuestionAnswer questionAnswer = formTemplate
								.getQuestionAnswer(checkBox.getId());
						if (checkBox.isChecked()) {
								String answer= PhysicalTherapyUtils.answerReplacer(questionAnswer.getChoiceList(),questionAnswer.getAnswer(),text,true);
							questionAnswer.setAnswer(answer.trim());
						}else{
							String answer= PhysicalTherapyUtils.answerReplacer(questionAnswer.getChoiceList(),questionAnswer.getAnswer(),text,false);
							questionAnswer.setAnswer(answer.trim());
						}
					}

				});
				questionAnswer.addWidgetId(widgetId);
			}
		}

		private FormTemplate getFormTemplate() {
			FormTemplate formTemplate = null;
			try {
				formTemplate = FormTemplateManager.getFormTemplate();
			} catch (Exception ex) {
				Log.e(INITIALIZATION_ERROR,
						"Could not get form template because " + ex);
			}
			return formTemplate;
		}

		private void addRadio(TableRow tableRow, QuestionAnswer questionAnswer) {
			RadioGroup radioGroup = new RadioGroup(this.getActivity());
			radioGroup.setOrientation(LinearLayout.HORIZONTAL);
			for (String value : questionAnswer.getChoiceList()) {
				RadioButton radioButton = new RadioButton(this.getActivity());
				radioButton.setId(getUniqueWidgetId());
				radioButton.setText(value);
				radioGroup.addView(radioButton);
			}
			radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
				public void onCheckedChanged(RadioGroup radioGoup, int radioId) {
					//radioGroup.getContext().
				}
				
			});
			int widgetId = getUniqueWidgetId();
			radioGroup.setId(widgetId);
			questionAnswer.addWidgetId(widgetId);
			tableRow.addView(radioGroup);
		}

		private int getUniqueWidgetId() {
			AppWidgetProviderInfo appWidgetInfo = null;
			int counter = 0;
			while (appWidgetInfo == null && counter < 10000) {
				counter++;
				appWidgetInfo = AppWidgetManager
						.getInstance(this.getActivity()).getAppWidgetInfo(
								idCounter);
				if (appWidgetInfo == null) {
					int retId = idCounter;
					idCounter++;
					return retId;
				}
			}
			throw new RuntimeException(
					"Cannot find uniqueId to assign to widget");
		}

		private void addTextBox(TableRow tableRow, QuestionAnswer questionAnswer) {
			EditText answerText = new EditText(this.getActivity());
			answerText.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
			answerText.setWidth(500);
			int widgetId = getUniqueWidgetId();
			answerText.setId(widgetId);
			questionAnswer.addWidgetId(widgetId);
			Button speakButton = new Button(this.getActivity());
			speakButton.setText("Speak");
			speakButton.setOnClickListener(new SpeechButtonClickListener(this
					.getActivity(), questionAnswer.getInputType()));
			if (questionAnswer.getInputType() == InputType.INTEGER) {
				answerText
						.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
				answerText.setWidth(120);
			} else if (questionAnswer.getInputType() == InputType.EMAIL) {
				answerText
						.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
			} else if (questionAnswer.getInputType() == InputType.PHONE) {
				answerText
						.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
			}
			tableRow.addView(answerText);
			questionAnswer.addWidgetId(answerText.getId());
			tableRow.addView(speakButton);

		}

		private class SpeechButtonClickListener implements OnClickListener {

			private InputType inputType = InputType.TEXT;
			private Activity activity;

			private SpeechButtonClickListener(Activity activity,
					InputType inputType) {
				this.inputType = inputType;
				this.activity = activity;
			}

			public void onClick(View v) {
				this.promptSpeechInput(v);

			}

			private void promptSpeechInput(View view) {
				MainActivity.answerWidgetDataType = inputType;
				ViewGroup viewGroup = (ViewGroup) view.getParent();
				EditText editText = (EditText) viewGroup.getChildAt(1);
				answerWidgetId = editText.getId();
				Intent intent = new Intent(
						RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
				intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
						RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
				intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
						Locale.getDefault());
				intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak");
				try {
					activity.startActivityForResult(intent,
							REQ_CODE_SPEECH_INPUT);
				} catch (ActivityNotFoundException a) {
					Toast.makeText(view.getContext(), "Speech Not Supported",
							Toast.LENGTH_SHORT).show();
				}
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
		case REQ_CODE_SPEECH_INPUT: {
			if (resultCode == RESULT_OK && null != data) {

				ArrayList<String> result = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				String inputStr = result.get(0).trim();
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
