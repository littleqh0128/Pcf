package com.wondersgroup.pcf.system.model;

import com.wondersgroup.pcf.common.base.BaseModel;
import com.wondersgroup.pcf.system.model.bo.SysUser;

public class SysModel extends BaseModel{
	
	/* 数据视图vo对象 */
	// 系统用户
	private SysUser sysUserVo;

	public SysUser getSysUserVo() {
		if (sysUserVo == null)
			sysUserVo = new SysUser();
		return sysUserVo;
	}

	public void setSysUserVo(SysUser sysUserVo) {
		this.sysUserVo = sysUserVo;
	}
}
