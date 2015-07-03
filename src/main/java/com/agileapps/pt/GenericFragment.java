package com.agileapps.pt;

import org.apache.commons.lang3.StringUtils;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import com.agileapps.pt.manager.FormTemplateManager;
import com.agileapps.pt.pojos.FormTemplate;
import com.agileapps.pt.pojos.FormTemplatePart;
import com.agileapps.pt.pojos.InputType;
import com.agileapps.pt.pojos.QuestionAnswer;
import com.agileapps.pt.util.PhysicalTherapyUtils;

public abstract class GenericFragment extends Fragment {
	private static final int BOTTOM_MARGIN = 15;
	private static final int LEFT_MARGIN =40;
	protected int position;
	protected FormTemplatePart formTemplatePart;
	protected int layoutId;
	protected int tableLayoutId;

	public void setTemplate(int position) {
		this.position = position;
	}

	
	@Override
	public void onResume() {
		super.onResume();
		Log.i(MainActivity.PT_APP_INFO,"formTemplate part  "+((formTemplatePart != null)?formTemplatePart.getTitle():"null formtemplate part")+" being restored");
	    if ( formTemplatePart != null ){
	    	Log.i(MainActivity.PT_APP_INFO,"Resetting answers to form template part "+formTemplatePart.getTitle());
	    	for ( QuestionAnswer questionAnswer:formTemplatePart.getQuestionAnswerList()){
	    		String answer=questionAnswer.getAnswer();
	    		if ( StringUtils.isNotBlank(answer)){
	    			Integer widgetIds[]=questionAnswer.getWidgetIds();
	    			if ( widgetIds == null  || widgetIds.length == 0){
	    				Log.e(MainActivity.PT_APP_INFO,"Something went really wrong, question answer was found with no associated widgets");
	    			}else if ( widgetIds.length == 1){
	    			   /*has only one widget attached which means it is a radio box or a text boxt*/
	    				View view=this.getActivity().findViewById(widgetIds[0]);
	    				if ( view != null ){
	    					if ( view instanceof EditText){
	    						((EditText)view).setText(answer);
	    					}else if ( view instanceof RadioGroup){
	    						RadioGroup radioGroup=(RadioGroup)view;
	    						for (int ii=0;ii<radioGroup.getChildCount();ii++){
	    							View subView=radioGroup.getChildAt(ii);
	    							if ( subView instanceof RadioButton ){
	    								RadioButton radioButton=(RadioButton)subView;
	    								if ( answer.equals(radioButton.getText())){
	    									radioButton.setChecked(true);
	    								}else{
	    									radioButton.setChecked(false);
	    								}
	    							}
	    						}
	    					}
	    				}else{
	    					Log.e(MainActivity.PT_APP_INFO, "Null view returned for widget id "+widgetIds[0]);
	    				}
	    			}else{
	    				for ( int widgetId: widgetIds){
	    					View view=this.getActivity().findViewById(widgetId);
	    					if (view == null ){
	    						Log.e(MainActivity.PT_APP_INFO,"No view found for widget id "+widgetId);
	    					}else{
	    						if ( view instanceof CheckBox){
	    							CheckBox checkBox=(CheckBox)view;
	    							if (answer.contains(checkBox.getText())){
	    								checkBox.setChecked(true);
	    							}else{
	    								checkBox.setChecked(false);
	    							}
	    						}
	    					}
	    				}
	    			}
	    		}		    		
	    	}
	    }
	}

	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(MainActivity.PT_APP_INFO,"formTemplate part  "+((formTemplatePart != null)?formTemplatePart.getTitle():"null formtemplate part")+" being destroyed");
	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.formTemplatePart = getFormTemplate().getFormTemplatePartList()
				.get(position);
		View rootView = inflater.inflate(layoutId, container, false);
		TableLayout tableLayout = (TableLayout) rootView
				.findViewById(tableLayoutId);
		for (final QuestionAnswer questionAnswer : formTemplatePart
				.getQuestionAnswerList()) {
			try {
				TableRow tableRow = new TableRow(this.getActivity());
				tableLayout.addView(tableRow);
				TextView questionView = new TextView(this.getActivity());
				questionView.setText(questionAnswer.getQuestion().trim());
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
				Log.e(MainActivity.PT_APP_INFO,
						"Unable to initialize fragment because " + ex, ex);
			}
		}
		return rootView;
	}

	private void addCheckBox(TableRow tableRow,
			QuestionAnswer questionAnswer) {
		questionAnswer.clearWidgetIds();
		for (String value : questionAnswer.getChoiceList()) {
			CheckBox checkBox = new CheckBox(this.getActivity());
			checkBox.setText(value);
			tableRow.addView(checkBox);
			int widgetId = getUniqueWidgetId();
			checkBox.setId(widgetId);
			((TableRow.LayoutParams )checkBox.getLayoutParams()).leftMargin=LEFT_MARGIN;
			((TableRow.LayoutParams )checkBox.getLayoutParams()).bottomMargin=BOTTOM_MARGIN;
			checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				public void onCheckedChanged(CompoundButton compoundButton,
						boolean arg1) {
					CheckBox checkBox = (CheckBox) compoundButton;
					String text = (new StringBuilder()).append(
							checkBox.getText()).toString();
					FormTemplate formTemplate = getFormTemplate();
					QuestionAnswer questionAnswer = formTemplate
							.getQuestionAnswer(checkBox.getId());
					if (checkBox.isChecked()) {
						String answer = PhysicalTherapyUtils
								.answerReplacer(
										questionAnswer.getChoiceList(),
										questionAnswer.getAnswer(), text,
										true);
						questionAnswer.setAnswer(answer.trim());
					} else {
						String oldAnswer=questionAnswer.getAnswer();
						if (oldAnswer == null ){
							oldAnswer="";
						}
						String answer = PhysicalTherapyUtils
								.answerReplacer(
										questionAnswer.getChoiceList(),
										 oldAnswer, text,
										false);
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
			Log.e(MainActivity.PT_APP_INFO,
					"Could not get form template because " + ex);
		}
		return formTemplate;
	}

	private void addRadio(TableRow tableRow, QuestionAnswer questionAnswer) {
		questionAnswer.clearWidgetIds();
		final RadioGroup radioGroup = new RadioGroup(this.getActivity());
		radioGroup.setOrientation(LinearLayout.HORIZONTAL);
		for (String value : questionAnswer.getChoiceList()) {
			RadioButton radioButton = new RadioButton(this.getActivity());
			radioButton.setId(getUniqueWidgetId());
			radioButton.setText(value);
		    radioGroup.addView(radioButton);
			((RadioGroup.LayoutParams )radioButton.getLayoutParams()).leftMargin=LEFT_MARGIN;
			((RadioGroup.LayoutParams )radioButton.getLayoutParams()).bottomMargin=BOTTOM_MARGIN;
		}
		radioGroup
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					public void onCheckedChanged(RadioGroup radioGoup,
							int radioId) {
						FormTemplate formTemplate = getFormTemplate();
						QuestionAnswer questionAnswer = formTemplate
								.getQuestionAnswer(radioGoup.getId());
						RadioButton radioButton = (RadioButton) radioGroup
								.findViewById(radioId);
						    if ( radioButton.isChecked())
						    {	
							questionAnswer.setAnswer(String.valueOf(radioButton
								.getText()));
						    }else{
						    	questionAnswer.setAnswer("");
						    }
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

	private void addTextBox(TableRow tableRow, QuestionAnswer questionAnswer) {
		final EditText answerText = new EditText(this.getActivity());
		questionAnswer.clearWidgetIds();
		answerText.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
		answerText.setWidth(500);
		int widgetId = getUniqueWidgetId();
		answerText.setId(widgetId);
		questionAnswer.addWidgetId(widgetId);
		Button speakButton = new Button(this.getActivity());
		speakButton.setText("Speak");
		speakButton.setOnClickListener(new SpeechButtonClickListener((MainActivity)this
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
		answerText.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable editable) {
				FormTemplate formTemplate = getFormTemplate();
				QuestionAnswer questionAnswer = formTemplate
						.getQuestionAnswer(answerText.getId());
				String answer = editable.toString();
				questionAnswer.setAnswer(answer);
			}

			public void beforeTextChanged(CharSequence editable, int start,
					int count, int after) {
				// TODO Auto-generated method stub

			}

			public void onTextChanged(CharSequence editable, int start,
					int count, int after) {
				// TODO Auto-generated method stub
			}
		});
		tableRow.addView(answerText);
		questionAnswer.addWidgetId(answerText.getId());
		tableRow.addView(speakButton);

	}


}