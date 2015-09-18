package com.bill.metrobus;

import android.graphics.drawable.Drawable;

public class UserEntity {
	private String alias;
	private Drawable icon;
	
	public UserEntity(String alias) {
		super();
		this.alias = alias;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	
	
}
