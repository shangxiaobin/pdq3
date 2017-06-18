package com.geo.presto.bean;

import java.util.List;

public class Dynatree {

	private String title;
	private String key;
	private Boolean isFolder = true;// 文件夹
	private Boolean isLazy = false;// 是否是懒加载
	private Boolean expand = true;// 展开还是关闭
	private List<Dynatree> children;

	public Dynatree() {
		super();
	}

	public Dynatree(String title, String key, Boolean isFolder, Boolean isLazy, Boolean expand, List<Dynatree> children) {
		super();
		this.title = title;
		this.key = key;
		this.isFolder = isFolder;
		this.isLazy = isLazy;
		this.expand = expand;
		this.children = children;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Dynatree other = (Dynatree) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Boolean getIsFolder() {
		return isFolder;
	}

	public void setIsFolder(Boolean isFolder) {
		this.isFolder = isFolder;
	}

	public Boolean getIsLazy() {
		return isLazy;
	}

	public void setIsLazy(Boolean isLazy) {
		this.isLazy = isLazy;
	}

	public Boolean getExpand() {
		return expand;
	}

	public void setExpand(Boolean expand) {
		this.expand = expand;
	}

	public List<Dynatree> getChildren() {
		return children;
	}

	public void setChildren(List<Dynatree> children) {
		this.children = children;
	}

}
