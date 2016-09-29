package com.wondersgroup.pcf.common.base;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import com.wondersgroup.framework.core.bo.Removable;

@MappedSuperclass
public abstract class BaseBo implements Removable,Serializable{
	
	private static final long serialVersionUID = 1L;
	/* 创建人 */
	private String stCreator;
	/* 创建时间 */
	private Date dtCreate;
	/* 创建时间 :时间段查询用到，@Transient*/
	private Date dtCreateBegin;
	/* 创建时间:时间段查询时用到 @Transient*/
	private Date dtCreateEnd;
	/* 最后修改人 */
	private String stModifier;
	/* 最后修改时间 */
	private Date dtModify;
	/* 删除标志 */
	private int removed;
	/* 删除人 */
	private String stRemover;
	/* 删除时间 */
	private Date dtRemove;
	
	public BaseBo() {
		this.removed = 0;
		this.dtCreate = new Date();
		this.dtModify = new Date();
	}

	@Column(name = "ST_CREATOR")
	public String getStCreator() {
		return stCreator;
	}

	public void setStCreator(String stCreator) {
		this.stCreator = stCreator;
	}
	
	@Column(name = "DT_CREATE")
	public Date getDtCreate() {
		return dtCreate;
	}

	public void setDtCreate(Date dtCreate) {
		this.dtCreate = dtCreate;
	}

	@Column(name = "ST_MODIFIER")
	public String getStModifier() {
		return stModifier;
	}

	public void setStModifier(String stModifier) {
		this.stModifier = stModifier;
	}
	
	@Column(name = "DT_MODIFY")
	public Date getDtModify() {
		return dtModify;
	}

	public void setDtModify(Date dtModify) {
		this.dtModify = dtModify;
	}
	
	@Column(name = "REMOVED")
	public int getRemoved() {
		return removed;
	}
	
	public void setRemoved(int removed) {
		this.removed = removed;
	}
	
	@Column(name = "ST_REMOVER")
	public String getStRemover() {
		return stRemover;
	}

	public void setStRemover(String stRemover) {
		this.stRemover = stRemover;
	}
	
	@Column(name = "DT_REMOVE")
	public Date getDtRemove() {
		return dtRemove;
	}

	public void setDtRemove(Date dtRemove) {
		this.dtRemove = dtRemove;
	}
	
	@Transient
	public boolean isEntityRemoved() {
		return this.removed == 1;
	}
	@Transient
	public Date getDtCreateEnd() {
		return dtCreateEnd;
	}

	public void setDtCreateEnd(Date dtCreateEnd) {
		this.dtCreateEnd = dtCreateEnd;
	}

	@Transient
	public Date getDtCreateBegin() {
		return dtCreateBegin;
	}

	public void setDtCreateBegin(Date dtCreateBegin) {
		this.dtCreateBegin = dtCreateBegin;
	}
}