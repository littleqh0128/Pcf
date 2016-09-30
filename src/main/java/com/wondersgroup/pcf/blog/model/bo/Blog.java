package com.wondersgroup.pcf.blog.model.bo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.wondersgroup.pcf.common.base.BaseBo;

@Entity
@Table(name="T_BLOG")
public class Blog extends BaseBo {
	
	private static final long serialVersionUID = 1L;
	
	// 用户ID
	private String stId;
	
	// 标题
	private String stTitle;
	
	// 正文
	private String stContent;
	
	@Id  
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid")
    @Column(name="ST_ID")
	public String getStId() {
		return stId;
	}
	public void setStId(String stId) {
		this.stId = stId;
	}
	
	@Column(name="ST_TITLE")
	public String getStTitle() {
		return stTitle;
	}
	public void setStTitle(String stTitle) {
		this.stTitle = stTitle;
	}
	
	@Column(name="ST_CONTENT")
	public String getStContent() {
		return stContent;
	}
	public void setStContent(String stContent) {
		this.stContent = stContent;
	}
}
