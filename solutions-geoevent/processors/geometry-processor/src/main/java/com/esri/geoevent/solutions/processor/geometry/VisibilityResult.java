package com.esri.geoevent.solutions.processor.geometry;

import java.util.List;

public class VisibilityResult {
	public static class results
	{
		public String paramName;
		//String
	}
	List<String> messages;
	results _results;
	public VisibilityResult() {	}
	
	public results getResults(){return _results;}
	
	public void setResults(results r)
	{
		this._results = r;
	}
	
	public List<String> getMessages()
	{
		return messages;
	}
	
	public void setMessages(List<String> m)
	{
		this.messages = m;
	}
}
