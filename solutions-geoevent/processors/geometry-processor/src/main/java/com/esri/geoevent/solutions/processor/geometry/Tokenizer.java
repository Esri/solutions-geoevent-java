package com.esri.geoevent.solutions.processor.geometry;

/*
 * #%L
 * Esri :: AGES :: Solutions :: Processor :: Geometry
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2013 - 2014 Esri
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


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
