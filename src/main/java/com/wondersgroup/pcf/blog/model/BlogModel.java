package com.wondersgroup.pcf.blog.model;

import com.wondersgroup.pcf.blog.model.bo.Blog;
import com.wondersgroup.pcf.common.base.BaseModel;

public class BlogModel extends BaseModel{
	
	/* 数据视图vo对象 */
	private Blog BlogVo;

	public Blog getBlogVo() {
		if (BlogVo == null)
			BlogVo = new Blog();
		return BlogVo;
	}

	public void setBlogVo(Blog blogVo) {
		BlogVo = blogVo;
	}
}
