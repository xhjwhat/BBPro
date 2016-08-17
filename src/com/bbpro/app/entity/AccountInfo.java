package com.bbpro.app.entity;

import java.io.Serializable;

import com.bbpro.ap.cache.CachedModel;
import com.bbpro.ap.cache.ModelCache;


public class AccountInfo extends CachedModel{

	public String bbNum;
	public String phone;
	public String password;
	public String hdUrl;
	public String cName;
	public String question;
	public String getBbNum() {
		return bbNum;
	}
	public void setBbNum(String bbNum) {
		this.bbNum = bbNum;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getHdUrl() {
		return hdUrl;
	}
	public void setHdUrl(String hdUrl) {
		this.hdUrl = hdUrl;
	}
	public String getcName() {
		return cName;
	}
	public void setcName(String cName) {
		this.cName = cName;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	@Override
	public String createKey(String id) {
		return "account01";
	}
	@Override
	public boolean reloadFromCachedModel(ModelCache modelCache,
			CachedModel cachedModel) {
		AccountInfo info = (AccountInfo) cachedModel;
		bbNum = info.getBbNum();
		phone = info.getPhone();
		password = info.getPassword();
		hdUrl = info.getHdUrl();
		cName = info.getcName();
		question = info.getQuestion();
		return false;
	}
	
	
	
}
