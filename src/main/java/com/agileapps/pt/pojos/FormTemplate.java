package com.agileapps.pt.pojos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

import com.agileapps.pt.MainActivity;

import android.util.Log;

@Root
public class FormTemplate {

	
	public static final String TITLE_DELIMITER = "%%";
	
	public static final String LINE_DELIMITER = "||";

	public static final String QUESTION_DELIMITER = "&&";

	private Map<Integer, QuestionAnswer> widgetIdMap;
	
	@Element
	private int id;
	@ElementList
	private List<FormTemplatePart> formTemplatePartList;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<FormTemplatePart> getFormTemplatePartList() {
		return formTemplatePartList;
	}

	public void setFormTemplatePartList(
			List<FormTemplatePart> formTemplatePartList) {
		this.formTemplatePartList = formTemplatePartList;
	}

	@Override
	public String toString() {
		return "FormTemplate [id=" + id + ", formTemplatePartList="
				+ formTemplatePartList + "]";
	}

	public void clear() {
		for (FormTemplatePart formTemplatePart : formTemplatePartList) {
			formTemplatePart.clear();
		}
	}
	
	public synchronized QuestionAnswer getQuestionAnswer(int widgetId){
		if ( this.widgetIdMap == null ){
			initializeWidgetIdMap();
		}
		QuestionAnswer questionAnswer=widgetIdMap.get(widgetId);
		if (questionAnswer == null  ){
			Log.i(MainActivity.PT_APP_INFO,"Can find question answer for widget "+widgetId+" re-initializing map");
			initializeWidgetIdMap();//re-initialize the map again to see if you can find it.
			 questionAnswer=widgetIdMap.get(widgetId);
		}
		return questionAnswer;
	}

	private void initializeWidgetIdMap() {
		widgetIdMap=new HashMap<Integer,QuestionAnswer>();
		for ( FormTemplatePart formTemplatePart:this.formTemplatePartList){
			for ( QuestionAnswer questionAnswer:formTemplatePart.getQuestionAnswerList()){
				for (Integer widgetId:questionAnswer.getWidgetIds()){
					widgetIdMap.put(widgetId, questionAnswer);
				}
			}
		}
	}

	public String getPrintableString() {
		StringBuilder sb=new StringBuilder();
		for ( FormTemplatePart part:this.formTemplatePartList){
			StringBuilder subSb=new StringBuilder();
			for (QuestionAnswer questionAnswer:part.getQuestionAnswerList()){
				if ( StringUtils.isNotBlank(questionAnswer.getAnswer())){
					subSb.append(questionAnswer.getQuestion().trim()).append(QUESTION_DELIMITER).append(questionAnswer.getAnswer().trim()).append(LINE_DELIMITER);
				}
			}
			String questionAnswers=subSb.toString();
			if ( StringUtils.isNotEmpty(questionAnswers)){
				sb.append(part.getTitle()).append(TITLE_DELIMITER).append(LINE_DELIMITER).append(questionAnswers);
			}
		}
		return sb.toString();
	}

}
