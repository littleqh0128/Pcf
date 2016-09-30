package com.wondersgroup.pcf.blog.service;

import com.wondersgroup.pcf.blog.model.BlogModel;

public interface BlogService {

	/**
	 * 新增博文
	 * @param blogModel 博文信息
	 * @return
	 * @throws Exception
	 */
	public void addBlog(BlogModel blogModel) throws Exception;
	
	/**
	 * 编辑博文
	 * @param blogModel 博文信息
	 * @return
	 * @throws Exception
	 */
	public void editBlog(BlogModel blogModel) throws Exception;
}
