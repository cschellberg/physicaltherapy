package com.agileapps.pt.util;

import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import com.agileapps.pt.pojos.FormTemplate;

public class PhysicalTherapyUtils {

	public static FormTemplate parseFormTemplate(InputStream is)
			throws Exception {
		Serializer serial = new Persister();
		StringBuilder sb = new StringBuilder();
		byte buffer[] = new byte[1000];
		while ((is.read(buffer)) >= 0) {
			sb.append(new String(buffer));
		}
		FormTemplate formTemplate = serial.read(FormTemplate.class,
				new StringBufferInputStream(sb.toString()));
		return formTemplate;
	}

	public static String answerReplacer(List<String> valueList,
			String currentAnswer, String newAnswer, boolean isAdd) {
		if ( valueList == null ){
			if ( isAdd){
				return newAnswer;
			}else{
				return currentAnswer;
			}
		}
		if (currentAnswer == null) {
			if (isAdd) {
				return newAnswer;
			} else {
				return null;
			}
		} else if (newAnswer == null) {
			return currentAnswer;
		}
		currentAnswer = currentAnswer.trim();
		newAnswer = newAnswer.trim();
		if (isAdd && currentAnswer.contains(newAnswer)) {
			return currentAnswer; // nothing to do
		} else if (!isAdd && currentAnswer.contains(newAnswer)) {
			String tmpStr = StringUtils.remove(currentAnswer, newAnswer).trim();
			String tmpStrParts[] = tmpStr.split(" ");
			StringBuilder sb = new StringBuilder();
			for (String str : tmpStrParts) {
				sb.append(str).append(" ");
			}
			return sb.toString().trim();
		} else if (isAdd && !currentAnswer.contains(newAnswer)) {
			StringBuilder sb = new StringBuilder();
			String tmpStrParts[] = currentAnswer.split(" ");
			if (tmpStrParts.length == 1 && StringUtils.isBlank(tmpStrParts[0])) {
				return newAnswer;
			}
			Map<String, Integer> valueListMap = new HashMap<String, Integer>();
			int cntr = 0;
			for (String str : valueList) {
				valueListMap.put(str, cntr);
				cntr++;
			}
			int newAnswerIndex = getIndex(valueListMap, newAnswer);
			boolean isAdded = false;
			for (String str : tmpStrParts) {
				int currentAnswerIndex = getIndex(valueListMap, str);
				if (newAnswerIndex < currentAnswerIndex) {
					sb.append(newAnswer).append(" ");
					isAdded=true;
				}
				sb.append(str).append(" ");
			}
			if (!isAdded) {
				sb.append(newAnswer);
			}
			return sb.toString().trim();
		} else {
			// it is a remove but it is not there so nothing to do
			return currentAnswer;
		}
	}

	private static int getIndex(Map<String, Integer> valueMap, String key) {
		int retValue = 100;// totally arbitrary
		if (valueMap.containsKey(key)) {
			retValue = valueMap.get(key);
		}
		return retValue;
	}

}
