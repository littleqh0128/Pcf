package com.wondersgroup.pcf.blog.model.bo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.wondersgroup.pcf.common.base.BaseBo;

@Entity
@Table(name="SYS_USER")
public class BlogTag extends BaseBo {
	
	private static final long serialVersionUID = 1L;
	
	// 用户ID
	private String stId;
	
	// 登录名
	private String stLoginName;
	
	// 姓名
	private String stName;
	
	// 密码
	private String stPassword;

	// 状态
	private String stStatus;
	
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
	
	@Column(name="ST_LOGIN_NAME")
	public String getStLoginName() {
		return stLoginName;
	}
	public void setStLoginName(String stLoginName) {
		this.stLoginName = stLoginName;
	}
	
	@Column(name="ST_NAME")
	public String getStName() {
		return stName;
	}
	public void setStName(String stName) {
		this.stName = stName;
	}
	
	@Column(name="ST_PASSWORD")
	public String getStPassword() {
		return stPassword;
	}
	public void setStPassword(String stPassword) {
		this.stPassword = stPassword;
	}
	
	@Column(name="ST_STATUS")
	public String getStStatus() {
		return stStatus;
	}
	public void setStStatus(String stStatus) {
		this.stStatus = stStatus;
	}
}
