package com.esri.geoevent.solutions.adapter.cot;

/*
 * #%L
 * CoTTypeDef.java - Esri :: AGES :: Solutions :: Adapter :: CoT - Esri - 2013
 * org.codehaus.mojo-license-maven-plugin-1.5
 * $Id: update-file-header-config.apt.vm 17764 2012-12-12 10:22:04Z tchemit $
 * $HeadURL: https://svn.codehaus.org/mojo/tags/license-maven-plugin-1.5/src/site/apt/examples/update-file-header-config.apt.vm $
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


public class CoTTypeDef {

	private String key;
	private String value;
	private boolean isAPredicate;

	public CoTTypeDef(String k, String v, boolean isPredicate) {
		this.key = k;
		this.value = v;
		this.isAPredicate = isPredicate;

	}

	public String getKey() {
		return this.key;

	}

	public String getValue() {
		return this.value;
	}

	public boolean isPredicate() {
		return this.isAPredicate;

	}

	public void setKey(String k) {
		this.key = k;

	}

	public void setValue(String v) {
		this.value = v;

	}

	public void setPredicateFlag(boolean pf) {
		this.isAPredicate = pf;

	}

}
