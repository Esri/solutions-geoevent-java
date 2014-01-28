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
package com.esri.geoevent.solutions.processor.symbollookup.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.esri.geoevent.solutions.processor.symbollookup.SymbolLookup;

import com.esri.runtime.ArcGISRuntime;

public class SymbolLookupTest {

	static SymbolLookup symbolLookup = null;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		// Done in setUpBeforeClass so we only do once 
		// (SymbolLookup class takes several seconds to initialize)
		if (symbolLookup == null) 
		{
			ArcGISRuntime.initialize();
			
			symbolLookup = new SymbolLookup();
		}		
	}
	
	@Before
	public void setUp() throws Exception {			
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSymbolIdLookupBadOne() {
		
		System.out.println("testSymbolIdLookupBadOne");
						
		String ret = symbolLookup.symbolIdToName("Test-BadOne-Id");
		
		assertEquals(SymbolLookup.NOT_FOUND, ret);				
	}

	@Test
	public void testSymbolNameLookupBadOne() {
		
		System.out.println("testSymbolNameLookupBadOne");
						
		String ret = symbolLookup.symbolNameToId("Test-BadOne-Name");
		
		assertEquals(SymbolLookup.NOT_FOUND, ret);				
	}
	
	@Test
	public void testSymbolIdLookupNullOne() {
		
		System.out.println("testSymbolIdLookupNullOne");
						
		String ret = symbolLookup.symbolIdToName(null);
		
		assertEquals(SymbolLookup.NOT_FOUND, ret);				
	}

	@Test
	public void testSymbolNameLookupNullOne() {
		
		System.out.println("testSymbolNameLookupNullOne");
						
		String ret = symbolLookup.symbolNameToId(null);
		
		assertEquals(SymbolLookup.NOT_FOUND, ret);				
	}	
	
	@Test
	public void testSymbolIdToName() {
		
		System.out.println("testSymbolIdToName");
		
		String sic2Check = "GHMPOGL-----USG";   
		String expectedName = "General Obstacle Line";
		
		String actualName = symbolLookup.symbolIdToName(sic2Check);
		    		    
		System.out.println("SIC: " + sic2Check + ", returned Name: " + actualName);
		
		assertEquals(expectedName, actualName);						
	}
	
	@Test
	public void testSymbolIdToName2() {
		
		System.out.println("testSymbolIdToName2");
		
		String sic2Check = "GHGPGPWA----USX";   
		String expectedName = "Aim Point H";
		
		String actualName = symbolLookup.symbolIdToName(sic2Check);
		    		    
		System.out.println("SIC: " + sic2Check + ", returned Name: " + actualName);
		
		assertEquals(expectedName, actualName);						
	}	
	
	@Test
	public void testSymbolNameToId() {
		
		System.out.println("testSymbolNameToId");
		
		String name2Check = "Limited Access Area H";
		
		String sidc = symbolLookup.symbolNameToId(name2Check);
		
		String expectedSic = "GHGPGAY-------X";
		    		    
		System.out.println("Name: " + name2Check + ", returned SIC: " + sidc);
		
		assertEquals(expectedSic, sidc);						
	}	
	
	@Test
	public void testSymbolNameToId2() {
		
		System.out.println("testSymbolNameToId2");
		
		String name2Check = "Infantry Motorized F~Battalion/Squadron~Headquarters";
		
		String sidc = symbolLookup.symbolNameToId(name2Check);
		
		String expectedSic = "SFGPUCIM-------";
		    		    
		System.out.println("Name: " + name2Check + ", returned SIC: " + sidc);
		
		assertEquals(expectedSic, sidc);						
	}	
	
	@Test
	public void testSymbolNameToId3() {
		
		System.out.println("testSymbolNameToId3");
		
		String name2Check = "Infantry";
		
		String sidc = symbolLookup.symbolNameToId(name2Check);
		
		String expectedSic = "SFGPUCI--------";
		    		    
		System.out.println("Name: " + name2Check + ", returned SIC: " + sidc);
		
		assertEquals(expectedSic, sidc);						
	}		
	
}
