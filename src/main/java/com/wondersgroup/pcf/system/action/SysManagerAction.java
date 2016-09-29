package com.wondersgroup.pcf.system.action;

import com.wondersgroup.pcf.common.base.BaseAction;
import com.wondersgroup.pcf.system.model.SysModel;

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
