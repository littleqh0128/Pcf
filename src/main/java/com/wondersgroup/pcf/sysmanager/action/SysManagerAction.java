package com.wondersgroup.pcf.sysmanager.action;

import com.wondersgroup.pcf.baseframe.base.BaseAction;
import com.wondersgroup.pcf.sysmanager.model.SysModel;

public class SysManagerAction extends BaseAction<SysModel> {
	
	private static final long serialVersionUID = 1L;
	
	/* 数据载体model */
	private SysModel model = new SysModel();
	
	/**
	 * 返回当前处理的Model对象
	 * @return model对象
	 */
	public SysModel getModel() {
		return this.model;
	}
}
