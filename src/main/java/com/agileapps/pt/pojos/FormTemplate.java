package com.agileapps.pt.pojos;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

@Root
public class FormTemplate {

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

}
