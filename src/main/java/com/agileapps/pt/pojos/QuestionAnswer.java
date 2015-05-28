//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.05.23 at 02:43:14 PM EDT 
//

package com.agileapps.pt.pojos;

import org.simpleframework.xml.Element;


public class QuestionAnswer {

	@Element
    private int id;
	@Element
    private InputType inputType;
	@Element
    private String question;
	@Element(required=false)
    private String answer;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public InputType getInputType() {
		return inputType;
	}
	public void setInputType(InputType inputType) {
		this.inputType = inputType;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	@Override
	public String toString() {
		return "QuestionAnswer [id=" + id + ", inputType=" + inputType
				+ ", question=" + question + ", answer=" + answer + "]";
	}

    public void clear()
    {
    	answer=null;
    }
   
}
