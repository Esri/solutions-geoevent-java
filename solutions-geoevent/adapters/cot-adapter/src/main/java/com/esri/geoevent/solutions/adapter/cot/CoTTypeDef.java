package com.esri.geoevent.solutions.adapter.cot;

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
