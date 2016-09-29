package com.wondersgroup.pcf.common.utils;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;

public class MessageUtil {
    
    /**
     * 取得消息定义(Message)(带参数列表 n个参数)
     */ 
    public static String getMessage(String sMsgKey, String... sParamLst) {
 
    	try {
    		String msg = getMessageCustom("config/i18n/message", sMsgKey, sParamLst);
    		return msg;
    	}
    	catch (Exception exp) {
    		System.out.println(exp.toString());
    	}

    	return "";
    } 
    
    /**
     * 取得消息定义(UI)(带参数列表 n个参数)
     */ 
    public static String getUI(String sMsgKey, String ...sParamLst) {
 
    	try {
    		String msg = getMessageCustom("config/i18n/ui", sMsgKey, sParamLst);
    		return msg;
    	}
    	catch (Exception exp) {
    		System.out.println(exp.toString());
    	}

    	return "";
    } 
    
    /**
     * 取得提示信息(带参数列表 n个参数)
     * @throws Exception 
     */ 
    public static String getMessageCustom(String sPropertyName, String sMsgKey, String[] sParamLst) throws Exception {
    	String sMessage = "";
    	ResourceBundle res = ResourceBundle.getBundle(sPropertyName, Locale.getDefault());
    	if (res != null && res.containsKey(sMsgKey)){
	    	sMessage = res.getString(sMsgKey);
	    	if (sParamLst != null && sParamLst.length > 0)
	    	{
	    		sMessage = formatMsg(sMessage, sParamLst);
	    	}
    	}
    	else
    		throw new Exception("资源文件不存在或者资源文件中不包含指定的项目");
    	return sMessage;
    } 
    
	/**
	 * 格式化消息(配对的参数设置)
	 */
	public static String formatMsg(String sMsgText, String sParam) {

		if (StringUtils.isEmpty(sParam))
			return sMsgText;

		return formatMsg(sMsgText, new String[] { sParam });
	}
	
	/**
	 * 格式化消息(配对的参数设置)
	 */
	public static String formatMsg(String sMsgText, String oParams[]) {

		if (oParams == null || StringUtils.isEmpty(sMsgText)) {
			return sMsgText;
		}

		MessageFormat mf = new MessageFormat(sMsgText, Locale.getDefault());
		return mf.format(oParams);
	}
}
