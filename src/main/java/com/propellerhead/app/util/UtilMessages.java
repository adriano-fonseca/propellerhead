package com.propellerhead.app.util;

import java.util.HashMap;

public class UtilMessages {

	public static  HashMap<String, String>  getMessage(String key, String msg){
		HashMap<String, String> map = new HashMap<String, String>();
		map = new HashMap<String, String>();
		map.put(key, msg);
		return map;
	}
	
	

	public enum Messages {
		RECORD_NOT_FOUND("Record Not Found"), 
		RECORD_REMOVED("Record Removed"), 
		PUT_TO_UPDATE("Use PUT to update Notes");
		
		private final String msg;
		
		Messages(String value){
			this.msg = value;
		}
		
		public String getMsg(){
			return this.msg;
		}
	}
}
