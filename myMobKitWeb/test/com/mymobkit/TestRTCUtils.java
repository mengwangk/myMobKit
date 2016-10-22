package com.mymobkit;

import com.google.gson.Gson;
import com.mymobkit.common.RTCUtils;
import com.mymobkit.model.MessageInfo;

public class TestRTCUtils {
	public static void main(String[] args){
		String pcConfig = RTCUtils.makePcConfig("abc", "xyz", "123");
		System.out.println(pcConfig);
		
		String constraintString = RTCUtils.makeMediaTrackConstraints("goog=1,google=2,abc=1,xyz=2");
		System.out.println(constraintString);
		constraintString = RTCUtils.makeMediaTrackConstraints("true");
		System.out.println(constraintString);
		
		String mediaStreamConstraints = RTCUtils.makeMediaStreamConstraints("goog=1,google=2,abc=1,xyz=2", "");
		System.out.println(mediaStreamConstraints);
		
		String msg = "{\"type\":\"bye\"}";
		
		MessageInfo msgInfo = new MessageInfo();
		msgInfo.setType("bye");
		
		String json = new Gson().toJson(msgInfo);
		System.out.println(json);
		
		msgInfo =  new Gson().fromJson(msg, MessageInfo.class);
		System.out.println(msgInfo.getType());
	}
}
