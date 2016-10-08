package org.zhuboss.pc2server.pcproxy.gui.cache;

import java.util.List;

import zhuboss.pc2server.common.CacheResource;

public class CacheData {
	private List<CacheResource> list;
	private String totalSize;
	
	public List<CacheResource> getList() {
		return list;
	}
	public void setList(List<CacheResource> list) {
		this.list = list;
	}
	public String getTotalSize() {
		return totalSize;
	}
	public void setTotalSize(String totalSize) {
		this.totalSize = totalSize;
	}
}
