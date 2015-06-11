package com.agileapps.pt.pojos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

@Root
public class FormTemplate {

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
		return widgetIdMap.get(widgetId);
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

}
