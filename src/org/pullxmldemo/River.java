package org.pullxmldemo;

import java.io.Serializable;

public class River implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String name;

	private int length;
	
	private String introduction;
	
	private String imageurl;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public String getImageurl() {
		return imageurl;
	}

	public void setImageurl(String imageurl) {
		this.imageurl = imageurl;
	}

}
