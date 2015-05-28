package com.agileapps.pt.util;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.agileapps.pt.pojos.FormTemplate;
import com.agileapps.pt.pojos.FormTemplatePart;
import com.agileapps.pt.pojos.QuestionAnswer;

public class PhysicalTherapyUtilsTest {

	@Test
	public void parseFormTemplateTest() {
		try {
			InputStream is = new FileInputStream(
					"assets/DefaultFormTemplate.xml");
			assertNotNull(is);
			FormTemplate formTemplate = PhysicalTherapyUtils
					.parseFormTemplate(is);
			assertNotNull(formTemplate);
			validate(formTemplate);
			System.out.println("SUCCESS");
			print(formTemplate);
		} catch (Exception ex) {
			ex.printStackTrace();
			fail("parseFormTemplateTest failed because " + ex);
		}
	}

	private static void validate(FormTemplate formTemplate) throws Exception {
		Set<Integer> idSet = new HashSet<Integer>();
		for (FormTemplatePart formTemplatePart : formTemplate
				.getFormTemplatePartList()) {
			if (formTemplatePart.getTitle() == null) {
				throw new Exception("All form template parts must have a title");
			}
			if (formTemplatePart.getId() == 0) {
				throw new Exception("form template part with title "
						+ formTemplatePart.getTitle()
						+ "cannot have an id equal to 0");
			}

			if (idSet.contains(formTemplatePart.getId())) {
				throw new Exception("form template part with title "
						+ formTemplatePart.getTitle()
						+ " has an id that is used by another template");
			}
			idSet.add(formTemplatePart.getId());
			validate(formTemplatePart);
		}

	}

	private static void validate(FormTemplatePart formTemplatePart)
			throws Exception {
		Set<Integer> idSet = new HashSet<Integer>();
		for (QuestionAnswer questionAnswer : formTemplatePart
				.getQuestionAnswerList()) {
			if (questionAnswer.getQuestion() == null) {
				throw new Exception("Question Answer with id "
						+ questionAnswer.getId() + " does not have a question!");
			}
			if (questionAnswer.getInputType() == null) {
				throw new Exception("Question Answer with id "
						+ questionAnswer.getId()+" "+questionAnswer.getQuestion()
						+ " does not have an input type!");
			}
			if (idSet.contains(questionAnswer.getId())) {
				throw new Exception("Question Answer with id "
						+ questionAnswer.getId()+" "+questionAnswer.getQuestion()
						+ " has an id that is used by another question");
			}
			idSet.add(questionAnswer.getId());

		}
	}

	 private static void print(FormTemplate formTemplate){
	        System.out.println("FormTemplate "+formTemplate.getId());
	        for ( FormTemplatePart formTemplatePart:formTemplate.getFormTemplatePartList()){
	            System.out.println("\tFormTemplatePart "+formTemplatePart.getId()+" "+formTemplatePart.getTitle());
	            for ( QuestionAnswer questionAnswer:formTemplatePart.getQuestionAnswerList()){
	                System.out.println("\t\tQuestionAnswer "+questionAnswer.getId()+" "+questionAnswer.getInputType()+" "+
	                        questionAnswer.getQuestion());
	            }
	        }
	    }
}
