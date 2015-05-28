package com.agileapps.pt;

import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.util.ArrayList;
import java.util.Locale;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.agileapps.pt.pojos.FormTemplate;
import com.agileapps.pt.pojos.FormTemplatePart;
import com.agileapps.pt.pojos.InputType;
import com.agileapps.pt.pojos.QuestionAnswer;
import com.agileapps.pt.util.PhysicalTherapyUtils;

public class MainActivity extends FragmentActivity {

	private static int idCounter=90000;
	private static final int REQ_CODE_SPEECH_INPUT = 1;
	public static final String ARG_SECTION_NUMBER = "section_number";
	public static final String HOME_WIDGET = "homeWidget";
	public static final String HOME_WIDGET_TYPE = "homeWidgetType";
	public static final String HOME_WIDGET_VALUE = "homeWidgetValue";
	public static final String HOME_WIDGET_TYPE_INTEGER = "integer";
	public static final String HOME_WIDGET_TYPE_TEXT = "text";
	private static final String INITIALIZATION_ERROR ="initError";
	private static WidgetData widgetData;
	private static int answerWidgetId=-1;
	private static InputType answerWidgetDataType=InputType.TEXT;
	private FormTemplate formTemplate;

	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try
		{
		setContentView(R.layout.activity_main);

		try {
			this.formTemplate = PhysicalTherapyUtils.parseFormTemplate(getClass().getResourceAsStream(
				"/assets/DefaultFormTemplate.xml"));
		} catch (Throwable ex) {
			Toast toast = Toast.makeText(getApplicationContext(),
					"Cannot load template file because " + ex.getMessage(),
					Toast.LENGTH_LONG);
			toast.show();
		}

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		FragmentManager fragmentManager = this.getSupportFragmentManager();
		mSectionsPagerAdapter = new SectionsPagerAdapter(fragmentManager,
				formTemplate);

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		}catch(Exception ex){
			String errorStr="Cannot initialize physical therapy because " + ex.getMessage();
			Log.e(INITIALIZATION_ERROR,errorStr);
			Toast toast = Toast.makeText(getApplicationContext(),
			errorStr,
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

	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		FormTemplate formTemplate;

		public SectionsPagerAdapter(FragmentManager fm,
				FormTemplate formTemplate) {
			super(fm);
			this.formTemplate = formTemplate;
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = null;
			switch (position) {
			case 0:
				Part0 part0 = new Part0();
				part0.setTemplate(position, formTemplate);
				fragment = part0;
				break;
			case 1:
				Part1 part1 = new Part1();
				part1.setTemplate(position, formTemplate);
				fragment = part1;
				break;
			case 2:
				Part2 part2 = new Part2();
				part2.setTemplate(position, formTemplate);
				fragment = part2;
				break;
			case 3:
				Part3 part3 = new Part3();
				part3.setTemplate(position, formTemplate);
				fragment = part3;
				break;

			default:
				part0 = new Part0();
				part0.setTemplate(position, formTemplate);
				fragment = part0;
			}
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			return formTemplate.getFormTemplatePartList().size();
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

		public void setTemplate(int position, FormTemplate formTemplate) {
			this.position = position;
			this.formTemplatePart = formTemplate.getFormTemplatePartList().get(
					position);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(layoutId, container, false);
			TextView dummyTextView = (TextView) rootView
					.findViewById(R.id.section_label);
			TableLayout tableLayout = (TableLayout) rootView
					.findViewById(tableLayoutId);
			for (final QuestionAnswer questionAnswer : formTemplatePart
					.getQuestionAnswerList()) {
				TableRow tableRow = new TableRow(this.getActivity());
				TableRow.LayoutParams lp =new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				tableRow.setLayoutParams(lp);
				tableLayout.addView(tableRow);
				TextView questionView = new TextView(this.getActivity());
				questionView.setTextSize(25f);
				EditText answerText = new EditText(this.getActivity());
				answerText.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
				answerText.setWidth(500);
				answerText.setId(idCounter++);
				Button speakButton=new Button(this.getActivity());
				speakButton.setText("Speak");
				speakButton.setOnClickListener(new SpeechButtonClickListener (this.getActivity(),questionAnswer.getInputType())) ;
				if ( questionAnswer.getInputType() == InputType.INTEGER){
					answerText.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
					answerText.setWidth(120);
				}else if  ( questionAnswer.getInputType() == InputType.EMAIL){
					answerText.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
				}else if  ( questionAnswer.getInputType() == InputType.PHONE){
					answerText.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
				}
				questionView.setText(questionAnswer.getQuestion());
				tableRow.addView(questionView);
				tableRow.addView(answerText);
				tableRow.addView(speakButton);
				if (questionAnswer.getInputType() == InputType.INTEGER) {
					Button graphButton = new Button(this.getActivity());
					graphButton.setText("Graph");
					tableRow.addView(graphButton);
				}
				if (MainActivity.widgetData != null) {
					TextView textView = (TextView) rootView
							.findViewById(MainActivity.widgetData.id);
					textView.setText(MainActivity.widgetData.value);
				}
			}
			return rootView;
		}
		
		private  class SpeechButtonClickListener implements OnClickListener{
	        
			private InputType inputType=InputType.TEXT;
			private Activity activity;
			
			private SpeechButtonClickListener(Activity activity,InputType inputType){
				this.inputType=inputType;
				this.activity=activity;
			}
			
			public void onClick(View v) {
				this.promptSpeechInput(v);
				
			}
			
			private void promptSpeechInput(View view) {
				 MainActivity.answerWidgetDataType=inputType;
				ViewGroup viewGroup=(ViewGroup)view.getParent();
				EditText editText=(EditText)viewGroup.getChildAt(1);
				answerWidgetId=editText.getId();
				Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
				intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
						RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
				intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
				intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak");
				try {
					activity.startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
				} catch (ActivityNotFoundException a) {
					Toast.makeText(view.getContext(), "Speech Not Supported", Toast.LENGTH_SHORT)
							.show();
				}
			}
		}
		
		
		
	}

	public static class Part0 extends GenericFragment {
		public Part0() {
			super();
			layoutId = R.layout.part_0;
			tableLayoutId=R.id.tableZeroLayout;
		}
	}

	public static class Part1 extends GenericFragment {
		public Part1() {
			super();
			layoutId = R.layout.part_1;
			tableLayoutId=R.id.tableOneLayout;
		}
	}

	public static class Part2 extends GenericFragment {
		public Part2() {
			super();
			layoutId = R.layout.part_2;
			tableLayoutId=R.id.tableTwoLayout;
		}
	}

	public static class Part3 extends GenericFragment {
		public Part3() {
			super();
			layoutId = R.layout.part_3;
			tableLayoutId=R.id.tableThreeLayout;
		}
	}

	static class WidgetData {
		int id;
		String value;

		WidgetData(int id, String value) {
			this.id = id;
			this.value = value;
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
