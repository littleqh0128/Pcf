package com.wondersgroup.pcf.blog.service.impl;

import org.apache.commons.beanutils.BeanUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wondersgroup.framework.security.exception.BadCredentialsException;
import com.wondersgroup.pcf.blog.dao.BlogDao;
import com.wondersgroup.pcf.blog.model.BlogModel;
import com.wondersgroup.pcf.blog.model.bo.Blog;
import com.wondersgroup.pcf.common.base.BaseServiceImpl;
import com.wondersgroup.pcf.common.constants.tables.SystemInfo.UserStatus;
import com.wondersgroup.pcf.common.utils.MD5Util;
import com.wondersgroup.pcf.system.dao.SysUserDao;
import com.wondersgroup.pcf.system.model.SysModel;
import com.wondersgroup.pcf.system.model.bo.SysUser;
import com.wondersgroup.pcf.system.service.LoginService;

@Service
@Transactional
public class BlogServiceImpl extends BaseServiceImpl implements LoginService{
	
	@Autowired
	private BlogDao blogDao;
	
	/**
	 * {@inheritDoc}
	 * @throws Exception 
	 */
	@Override
	public void addBlog(BlogModel blogModel) throws Exception {
		
		Blog blog = new Blog();
		//将视图vo中的数据拷贝到bo中
		BeanUtils.copyProperties(blog, blogModel.getBlogVo());
		blog.setStId(null);
		blog.setStCreator(blogModel.getSessionUser().getStLoginName());
		blog.setDtCreate(DateTime.now());
		blogDao.save(blog);
		//保存成功消息
		addMessages(blogModel, MsgConstants.SAVE_MSG,true);
	}
}
