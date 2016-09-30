package com.wondersgroup.pcf.system.model.vo;

import java.util.ArrayList;
import java.util.List;

import com.wondersgroup.framework.core5.model.vo.ValueObject;

/**
 * 菜单值对象
 */
@SuppressWarnings("serial")
public class MenuVo implements ValueObject {
	
	/**
	 * 子菜单（json字符串）
	 */
	private String childMenus;
	
	/**
	 * 菜单Id
	 */
	private Long id;

	/**
	 * 资源名称
	 */
	private String resourceName;

	/**
	 * 描述
	 */
	private String description;

	/**
	 * 显示顺序
	 */
	private int displayOrder;

	/**
	 * 链接路径
	 */
	private String linkPath;

	/**
	 * 父菜单Id
	 */
	private Long parentMenuId;

	/**
	 * javascript事件名称
	 */
	private String handler;

	/**
	 * CSS
	 */
	private String cls;

	/**
	 * 弹出位置
	 */
	private String target;

	/**
	 * 图片路径
	 */
	private String icon;

	/**
	 * 菜单代码
	 */
	private String code;
	
	/**
	 * 子菜单list
	 * @return
	 */
	private List<MenuVo> childMenuList;
	
	/**
	 * 是否子菜单
	 */
	private String hasSub = "";
	
	/**
	 * 是否展开
	 */
	private String expand = "";
	
	/**
	 * 是否处于活动状态
	 */
	private String active = "";
	
	/**
	 * 是否显示为block样式
	 */
	private String block = "";
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}

	public String getLinkPath() {
		return linkPath;
	}

	public void setLinkPath(String linkPath) {
		this.linkPath = linkPath;
	}

	public Long getParentMenuId() {
		return parentMenuId;
	}

	public void setParentMenuId(Long parentMenuId) {
		this.parentMenuId = parentMenuId;
	}

	public String getHandler() {
		return handler;
	}

	public void setHandler(String handler) {
		this.handler = handler;
	}

	public String getCls() {
		return cls;
	}

	public void setCls(String cls) {
		this.cls = cls;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getChildMenus() {
		return childMenus;
	}

	public void setChildMenus(String childMenus) {
		this.childMenus = childMenus;
	}

	public List<MenuVo> getChildMenuList() {
		if (childMenuList == null)
			childMenuList = new ArrayList<MenuVo>();
		return childMenuList;
	}

	public void setChildMenuList(List<MenuVo> childMenuList) {
		this.childMenuList = childMenuList;
	}

	public String getHasSub() {
		return hasSub;
	}

	public void setHasSub(String hasSub) {
		this.hasSub = hasSub;
	}

	public String getExpand() {
		return expand;
	}

	public void setExpand(String expand) {
		this.expand = expand;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getBlock() {
		return block;
	}

	public void setBlock(String block) {
		this.block = block;
	}
}
