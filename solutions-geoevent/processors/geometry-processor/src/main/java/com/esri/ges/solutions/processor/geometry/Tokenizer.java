package com.esri.ges.solutions.processor.geometry;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {
	private List<String>illegalCharacters = new ArrayList<String>();
	public Tokenizer() {
		illegalCharacters.add("!");
		//illegalCharacters.add("@");
		illegalCharacters.add("#");
		illegalCharacters.add("$");
		illegalCharacters.add("%");
		illegalCharacters.add("^");
		illegalCharacters.add("&");
		illegalCharacters.add("*");
		illegalCharacters.add("(");
		illegalCharacters.add(")");
		illegalCharacters.add("-");
		illegalCharacters.add("_");
		illegalCharacters.add("+");
		illegalCharacters.add("=");
		illegalCharacters.add("[");
		illegalCharacters.add("]");
		illegalCharacters.add("{");
		illegalCharacters.add("}");
		illegalCharacters.add("|");
		illegalCharacters.add("\\");
		illegalCharacters.add("/");
		illegalCharacters.add("<");
		illegalCharacters.add(">");
		illegalCharacters.add(",");
		illegalCharacters.add(".");
		illegalCharacters.add("?");
		illegalCharacters.add("`");
		illegalCharacters.add("~");
	}
	
	public String tokenize(String input, String type)
	{
		
		StripIllegalChars(input);
		if(type == "dist")
		{
			input += "_dist";
		}
		
		String output = "[$" + input + "]";
		
		return output;	
	}
	
	private String StripIllegalChars(String input)
	{
		for (String c: illegalCharacters)
		{
			if(input.contains(c))
			{
				input = input.replace(c, "");
			}
		}
		return input;
	}

}
