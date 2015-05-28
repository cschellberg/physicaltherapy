package com.agileapps.pt.util;

import java.io.InputStream;
import java.io.StringBufferInputStream;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import com.agileapps.pt.pojos.FormTemplate;

public class PhysicalTherapyUtils {
	
	public static FormTemplate parseFormTemplate(InputStream is) throws Exception{
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

}
