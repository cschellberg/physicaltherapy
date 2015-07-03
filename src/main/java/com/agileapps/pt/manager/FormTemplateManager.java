package com.agileapps.pt.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.agileapps.pt.MainActivity;
import com.agileapps.pt.pojos.Config;
import com.agileapps.pt.pojos.FormTemplate;
import com.agileapps.pt.util.PhysicalTherapyUtils;

public class FormTemplateManager {

	public final static String DEFAULT_CLIENT_INFO_FORM_NAME = "Default Client Info";
	public final static String DEFAULT_FORM_NAME = "Default";
	private final static String DEFAULT_CLIENT_INFO_TEMPLATE = "/assets/DefaultClientInfoTemplate.xml";
	private final static String DEFAULT_FORM_TEMPLATE = "/assets/DefaultFormTemplate.xml";
	private static Map<String, FormTemplate> formTemplateMap = new HashMap<String, FormTemplate>();
	
	private static Map <String,String> formNameToDirMap= new HashMap<String, String>();
	
	static{
		 formNameToDirMap.put(DEFAULT_CLIENT_INFO_FORM_NAME, DEFAULT_CLIENT_INFO_TEMPLATE);
		 formNameToDirMap.put(DEFAULT_FORM_NAME, DEFAULT_FORM_TEMPLATE);
	}


	public static FormTemplate getFormTemplate() throws Exception {
		Config config=ConfigManager.getConfig();
		return getFormTemplate(config.getDefaultClientInfoTemplate(),config.getDefaultFormTemplate());
	}

	public static synchronized FormTemplate getFormTemplate(
			String clientInfoTemplateName, String formTemplateName)
			throws Exception {
		FormTemplate clientInfoTemplate = formTemplateMap
				.get(clientInfoTemplateName);
		Config config=ConfigManager.getConfig();
		String clientInfoTemplateResource=formNameToDirMap.get(config.getDefaultClientInfoTemplate());
		String formTemplateResource=formNameToDirMap.get(config.getDefaultFormTemplate());
		if (clientInfoTemplate == null) {
			clientInfoTemplate = PhysicalTherapyUtils
					.parseFormTemplate(FormTemplateManager.class
							.getResourceAsStream(clientInfoTemplateResource));
			if (clientInfoTemplate == null) {
				throw new RuntimeException(
						"Unable to find client info template with name "
								+ clientInfoTemplateResource);
			} else {
				formTemplateMap.put(clientInfoTemplate.getFormName(),
						clientInfoTemplate);
			}
			Log.i(MainActivity.PT_APP_INFO,
					"A new form template has been created");
		} else {
			Log.i(MainActivity.PT_APP_INFO,
					"Existing form template being returned");
		}
		FormTemplate formTemplate = formTemplateMap.get(formTemplateName);
		if (formTemplate == null) {
			formTemplate = PhysicalTherapyUtils
					.parseFormTemplate(FormTemplateManager.class
							.getResourceAsStream(formTemplateResource));
			if (formTemplate == null) {
				throw new RuntimeException("Unable to find template with name "
						+ formTemplateResource);
			} else {
				formTemplate.getFormTemplatePartList().addAll(0,
						clientInfoTemplate.getFormTemplatePartList());
				formTemplateMap.put(formTemplate.getFormName(), formTemplate);
			}
			Log.i(MainActivity.PT_APP_INFO,
					"A new form template has been created");
		} else {
			Log.i(MainActivity.PT_APP_INFO,
					"Existing form template being returned");
		}
		return formTemplate;
	}

	public static void loadForm(File formFile) throws FileNotFoundException,
			Exception {
		FormTemplate formTemplate = PhysicalTherapyUtils
				.parseFormTemplate(new FileInputStream(formFile));
		formTemplateMap.put(formTemplate.getFormName(), formTemplate);

	}
}
