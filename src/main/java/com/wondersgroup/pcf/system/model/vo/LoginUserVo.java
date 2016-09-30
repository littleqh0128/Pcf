package com.wondersgroup.pcf.system.model.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 封装返回用户登录返回实体类
 * 
 * @author qianhao
 *
 */
public class LoginUserVo implements Serializable {

	private static final long serialVersionUID = 1L;

	protected static final Logger LOG = LoggerFactory.getLogger(LoginUserVo.class);

	/** 用户授权菜单列表 */
	private List<MenuVo> arrMenuList;
	
	/* 菜单id列表 */
	private String breadcrumbMenuIds;
	/* 菜单名称列表 */
	private String breadcrumbMenuNames;
	/* 菜单地址列表 */
	private String breadcrumbMenuUrls;
	
	// 用户ID
	private String stId;
	
	// 登录名
	private String stLoginName;
	
	// 姓名
	private String stName;

	public LoginUserVo() {
		super();
	}

	public List<MenuVo> getArrMenuList() {
		if (arrMenuList == null)
			arrMenuList = new ArrayList<MenuVo>();
		return arrMenuList;
	}

	public void setArrMenuList(List<MenuVo> arrMenuList) {
		this.arrMenuList = arrMenuList;
	}

	public String getBreadcrumbMenuIds() {
		return breadcrumbMenuIds;
	}

	public void setBreadcrumbMenuIds(String breadcrumbMenuIds) {
		this.breadcrumbMenuIds = breadcrumbMenuIds;
	}

	public String getBreadcrumbMenuNames() {
		return breadcrumbMenuNames;
	}

	public void setBreadcrumbMenuNames(String breadcrumbMenuNames) {
		this.breadcrumbMenuNames = breadcrumbMenuNames;
	}

	public String getBreadcrumbMenuUrls() {
		return breadcrumbMenuUrls;
	}

	public void setBreadcrumbMenuUrls(String breadcrumbMenuUrls) {
		this.breadcrumbMenuUrls = breadcrumbMenuUrls;
	}

	public String getStId() {
		return stId;
	}

	public void setStId(String stId) {
		this.stId = stId;
	}

	public String getStLoginName() {
		return stLoginName;
	}

	public void setStLoginName(String stLoginName) {
		this.stLoginName = stLoginName;
	}

	public String getStName() {
		return stName;
	}

	public void setStName(String stName) {
		this.stName = stName;
	}
}