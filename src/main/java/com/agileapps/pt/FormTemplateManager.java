package com.agileapps.pt;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.agileapps.pt.pojos.FormTemplate;
import com.agileapps.pt.util.PhysicalTherapyUtils;

public class FormTemplateManager {

	private static Map<String, FormTemplate> formTemplateMap = new HashMap<String, FormTemplate>();

	private final static String DEFAULT_TEMPLATE = "/assets/DefaultFormTemplate.xml";

	public static FormTemplate getFormTemplate() throws Exception {
		return getFormTemplate(DEFAULT_TEMPLATE);
	}

	public static  synchronized FormTemplate getFormTemplate(String templateResource) throws Exception{
		FormTemplate formTemplate=formTemplateMap.get(templateResource);
		if (formTemplate == null ){
			formTemplate=PhysicalTherapyUtils
			.parseFormTemplate(FormTemplateManager.class.getResourceAsStream(templateResource));
			if ( formTemplate == null){
				throw new RuntimeException("Unable to find template with name "+templateResource);
			}else{
				formTemplateMap.put(templateResource, formTemplate);
			}
			Log.i(MainActivity.PT_APP_INFO,"A new form template has been created");
		}else{
			Log.i(MainActivity.PT_APP_INFO,"Existing form template being returned");
		}
		return formTemplate;
	}
}
