package com.wondersgroup.pcf.baseframe.base;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

public abstract class BaseAction<M> extends ActionSupport implements ModelDriven<M> {

	private static final long serialVersionUID = 1L;
	
	protected final Log logger = LogFactory.getLog(getClass());
	
	/**
	 * 得到request
	 * @return
	 */
	public HttpServletRequest getRequest() {
		return ServletActionContext.getRequest();
	}

	/**
	 * 得到response
	 * @return
	 */
	public HttpServletResponse getResponse() {
		return ServletActionContext.getResponse();
	}
}
