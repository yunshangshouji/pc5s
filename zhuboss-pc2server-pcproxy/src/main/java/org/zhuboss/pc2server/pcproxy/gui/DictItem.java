package org.zhuboss.pc2server.pcproxy.gui;

public class DictItem {
	private String text;
	private String value;
	
	public DictItem(String value, String text) {
		this.value = value;
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return this.getText();
	}
	
}
