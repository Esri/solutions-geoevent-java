/*
 | Copyright 2013 Esri
 |
 | Licensed under the Apache License, Version 2.0 (the "License");
 | you may not use this file except in compliance with the License.
 | You may obtain a copy of the License at
 |
 |    http://www.apache.org/licenses/LICENSE-2.0
 |
 | Unless required by applicable law or agreed to in writing, software
 | distributed under the License is distributed on an "AS IS" BASIS,
 | WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 | See the License for the specific language governing permissions and
 | limitations under the License.
 */
package com.esri.geoevent.solutions.processor.symbollookup;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.esri.core.symbol.advanced.SymbolDictionary;
import com.esri.core.symbol.advanced.SymbolDictionary.DictionaryType;
import com.esri.core.symbol.advanced.SymbolProperties;

public class SymbolLookup {

	public static String NOT_FOUND = "NOT FOUND"; 
	private static Map<String,SymbolProperties> sidc2SymbolProps = new HashMap<String,SymbolProperties>();
	private static Map<String,SymbolProperties> symbolName2SymbolProps = new HashMap<String,SymbolProperties>();
	private static boolean initialized = false;
	
	private static SymbolDictionary sd = null; 
	
	private List<SymbolProperties> symbols; 
	
	public SymbolLookup() 
	{
		initialize();
	}
	
	private void initialize()
	{
		// we only want to do this once
		if (initialized)
			return;
		
		initialized = true;
		
		System.out.println("Initializing SymbolLookup");
		
		sd = new SymbolDictionary(DictionaryType.Mil2525C);	
		
		try {
			symbols = sd.findSymbols(null, null);
			
			for(SymbolProperties s : symbols )
            {
                String name = s.getName();
                
                try 
                {
                	if ((s == null) || (name == null) || name.isEmpty() || !(s.getValues().containsKey("SymbolID")))
                		continue;                
                }
                catch (Exception ex) {
        			// Should not happen
        			ex.printStackTrace();
        		}                

                String symbolId = s.getValues().get("SymbolID").replace('*','-');
                
                // TODO: figure out where Java Runtime put geometry property/attribute
                // we may have to use this one if it is not exposed
                // @SuppressWarnings("unused")
				// String geoType = s.getValues().get("GeometryConversionType");

                // IMPORTANT: note toUpperCase() - all keys will be upper case
                if (!sidc2SymbolProps.containsKey(symbolId))
                	sidc2SymbolProps.put(symbolId.toUpperCase(), s);
                // TODO: decide if a warning should be issued for these repeats 
                // else
                //	System.out.println(symbolId + " - aleady in table, can't add");                 
                
                if (!symbolName2SymbolProps.containsKey(name))
                	symbolName2SymbolProps.put(name.toUpperCase(), s);
                // TODO: decide if a warning should be issued for these repeats 
                // else
                //	System.out.println(name + " - aleady in table, can't add");                                

                // To see symbols list/table:
                // System.out.println(name + ":" + symbolId + ":" + geoType);
            }
			
			System.out.println("Initialization complete: unique symbols found=" + sidc2SymbolProps.size());

		} catch (IOException e) {
			// Should not happen
			e.printStackTrace();
		}
		
	}
	
	public String symbolIdToName(String symbolId) 
	{
		String symbolName = NOT_FOUND; 
		
		if ((symbolId == null) || symbolId.isEmpty())
			return symbolName;
		
		String maskedSymbolId = getMaskedSymbolId(symbolId);

        if (!sidc2SymbolProps.containsKey(maskedSymbolId))
        {
        	// try again with "F"/friendly version (some only have this version)
        	maskedSymbolId = getMaskedSymbolId(symbolId, "F");
        	
        	if (!sidc2SymbolProps.containsKey(maskedSymbolId))
        	{
	        	System.out.println(maskedSymbolId + "-not found in table, can't continue");
	        	return symbolName;
        	}
        }
        
        SymbolProperties sp = sidc2SymbolProps.get(maskedSymbolId);
        
        if (sp == null) // should not happen
        	return symbolName;
        
        symbolName = sp.getName();
				
		return symbolName;
	}
	
	public String symbolNameToId(String symbolName) 
	{	
		String symbolId = NOT_FOUND;
		
		if ((symbolName == null) || symbolName.isEmpty())
			return symbolId;

		String lookupSymbolName = symbolName.toUpperCase();
		
		if (lookupSymbolName.contains("~"))
		{
			lookupSymbolName = lookupSymbolName.split("~")[0];
		}		
		
		try 
		{
	        if (!symbolName2SymbolProps.containsKey(lookupSymbolName))
	        {
	        	lookupSymbolName = lookupSymbolName + " F";
	        	
	        	if (!symbolName2SymbolProps.containsKey(lookupSymbolName))
	        	{
	        		System.out.println(symbolName + "-not found in table, can't continue");
	        		return symbolId;
	        	}
	        }
	        
	        SymbolProperties sp = symbolName2SymbolProps.get(lookupSymbolName);
	        
	        if (sp == null) // should not happen
	        	return symbolId;
	        
	        symbolId = sp.getValues().get("SymbolID").replace('*','-');
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		return symbolId;		
	}	
	
	private String getMaskedSymbolId(String symbolId, String affiliation)
	{
		if (symbolId.length() < 15)
			return symbolId;
		
		StringBuilder sb = new StringBuilder();
		
		Character codingScheme = symbolId.charAt(0);
		
		sb.append(codingScheme);
		sb.append(affiliation);
		sb.append(symbolId.charAt(2));
		sb.append('P');
				
		for (int i = 4; i<10; i++)
			sb.append(symbolId.charAt(i));
		
		for (int i = 10; i<14; i++)
			sb.append('-');
		
		if (codingScheme == 'G')			
			sb.append('X');
		else 
			sb.append('-');
					
		return sb.toString().toUpperCase();		
	}
	
	private String getMaskedSymbolId(String symbolId)
	{
		if ((symbolId == null) || (symbolId.length() < 15))
			return symbolId;
		
		String affiliation = getAffiliationChar(symbolId);		
					
		return getMaskedSymbolId(symbolId, affiliation);
	}
	
	private String getAffiliationChar(String symbolId)
	{		
        Character ch = symbolId.toUpperCase().charAt(1);
        Character affilCh = ch;
        
        switch (ch) 
        {
        	// Simple cases (no change of affil char)
        	case 'F':
        	case 'H':
        	case 'U':
        	case 'N':
                affilCh = ch;
                break;
           	// Friendlies
        	case 'M':
        	case 'A':
        	case 'D':
        	case 'J':
        	case 'K':
                affilCh = 'F';
                break;
            // Hostile
           	case 'S':
                affilCh = 'H';
                break;   
            // Neutral
           	case 'L':
                affilCh = 'N';
                break;     
            // Unknown
        	case 'P':
        	case 'G':
        	case 'W':
        	case '-':
        	default :
                affilCh = 'U';
                break;                        
        }

        return affilCh.toString();
	}
            			
}
