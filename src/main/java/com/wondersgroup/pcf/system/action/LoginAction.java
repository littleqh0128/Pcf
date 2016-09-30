package com.wondersgroup.pcf.system.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.wondersgroup.framework.common.web.constant.LoginConstant;
import com.wondersgroup.framework.security.exception.BadCredentialsException;
import com.wondersgroup.pcf.common.base.BaseAction;
import com.wondersgroup.pcf.common.constants.MessageConst;
import com.wondersgroup.pcf.common.utils.MenuUtil;
import com.wondersgroup.pcf.common.utils.MessageUtil;
import com.wondersgroup.pcf.system.model.SysModel;
import com.wondersgroup.pcf.system.model.vo.LoginUserVo;
import com.wondersgroup.pcf.system.model.vo.MenuVo;
import com.wondersgroup.pcf.system.service.LoginService;

public class LoginAction extends BaseAction<SysModel> {
	
	private static final long serialVersionUID = 1L;
	
	@Autowired
	private LoginService loginService;
	
	/* 数据载体model */
	private SysModel model = new SysModel();
	
	/**
	 * 登录页面
	 * @return
	 * @throws Exception
	 */
	public String login() throws Exception{
		return INPUT;
	}
	
	/**
	 * 登录检查
	 * @return
	 * @throws Exception
	 */
	public String check() throws Exception{
		
		if (StringUtils.isEmpty(model.getSysUserVo().getStLoginName()))
			// 请输入用户名
			this.addActionError(
					MessageUtil.getMessage(MessageConst.Error.ERRORS_REQUIRED, 
							new String[] {MessageUtil.getUI(MessageConst.UI.UI_LOGINNAME)}));
		
		if (StringUtils.isEmpty(model.getSysUserVo().getStPassword()))
			// 请输入密码
			this.addActionError(
					MessageUtil.getMessage(MessageConst.Error.ERRORS_REQUIRED, 
							new String[] {MessageUtil.getUI(MessageConst.UI.UI_PASSWORD)}));
		if (this.hasActionErrors())
			return INPUT;
			
		try{
			// 尝试验证用户
			loginService.authentication(model);
		}
		catch(BadCredentialsException e){
			// 用户不存在
			if(BadCredentialsException.USER_NOT_EXISTED.equals(e.getMessage())){
				// 用户名或密码错误, 请重新输入。
				this.addActionError(
						MessageUtil.getMessage(MessageConst.Error.ERROR_LOGIN_INVALID));
			}
			// 密码错误
			if(BadCredentialsException.PASSWORD_ERROR.equals(e.getMessage())){
				// 用户名或密码错误, 请重新输入。
				this.addActionError(
						MessageUtil.getMessage(MessageConst.Error.ERROR_LOGIN_INVALID));
			}
			// 用户未激活
			if(BadCredentialsException.USER_STATUS_UNNORMAL.equals(e.getMessage())){
				// 用户状态无效, 请联系管理员。
				this.addActionError(
						MessageUtil.getMessage(MessageConst.Error.ERROR_LOGIN_STATUS_UNNORMAL));
			}
			return INPUT;
		}
		catch(Exception e){
			logger.debug(e.toString());
			return INPUT;
		}

		// 验证成功
		LoginUserVo loginUserVo = new LoginUserVo();
		BeanUtils.copyProperties(loginUserVo, model.getSysUserVo());
		// 设置用户信息
		super.getRequest().getSession().setAttribute(LoginConstant.SECURITY_LOGIN_USER, loginUserVo);
		
		/*********************************************************************
		 * 获取菜单信息
		 *********************************************************************/
		// 当前用户菜单列表
		List<MenuVo> menuList = new ArrayList<MenuVo>();
		MenuVo topMenuVO = new MenuVo();
		topMenuVO.setId(Long.parseLong(MenuUtil.INDEX_MENU_ID));
		topMenuVO.setResourceName(MenuUtil.INDEX_MENU_NAME);
		topMenuVO.setLinkPath(MenuUtil.INDEX_MENU_URL);
		topMenuVO.setActive(MenuUtil.CSS_ACTIVE);
		menuList.add(0, topMenuVO);
		
		MenuVo workMenuVO = new MenuVo();
		workMenuVO.setId(new Long(0));
		workMenuVO.setResourceName("撰写");
		workMenuVO.setLinkPath(MenuUtil.INDEX_MENU_URL);
		topMenuVO.setActive(MenuUtil.CSS_ACTIVE);
		menuList.add(workMenuVO);
		
		// 设置当前用户菜单
		loginUserVo.setArrMenuList(menuList);
		
		/*********************************************************************
		 * 面包屑菜单信息
		 *********************************************************************/
		loginUserVo.setBreadcrumbMenuIds(MenuUtil.INDEX_MENU_ID);
		loginUserVo.setBreadcrumbMenuNames(MenuUtil.INDEX_MENU_NAME);
		loginUserVo.setBreadcrumbMenuUrls(MenuUtil.FLAG_HASURL);
		
		/*********************************************************************
		 * 构筑Session信息
		 *********************************************************************/
		// 设置用户信息
		super.getRequest().getSession().setAttribute(
				LoginConstant.SECURITY_LOGIN_USER, loginUserVo);

		return SUCCESS;
	}
	
	/**
	 * 门户页面
	 * @return
	 * @throws Exception
	 */
	public String index() throws Exception{
		return SUCCESS;
	}
	
	/**
	 * 返回当前处理的Model对象
	 * @return model对象
	 */
	public SysModel getModel() {
		return this.model;
	}
}
