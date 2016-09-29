package com.wondersgroup.framework.core.dao;

import com.wondersgroup.framework.core.bo.Page;
import java.io.Serializable;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;

public abstract interface AbstractHibernateExtdDAO extends AbstractExtdDAO
{
  public abstract Object loadWithLazy(Serializable paramSerializable, String[] paramArrayOfString);
  
  public abstract List findByHQL(String paramString, Object paramObject);
  
  public abstract List findByHQL(String paramString, List paramList);
  
  public abstract List findByCriteria(DetachedCriteria paramDetachedCriteria);
  
  public abstract int countByHQL(String paramString);
  
  public abstract int countByHQL(String paramString, List paramList);
  
  public abstract int countByCriteria(DetachedCriteria paramDetachedCriteria);
  
  public abstract Page findByHQLWithPage(String paramString1, int paramInt1, int paramInt2, String paramString2);
  
  public abstract Page findByHQLWithPage(String paramString1, List paramList, int paramInt1, int paramInt2, String paramString2);
  
  public abstract Page findByHQLWithPage(String paramString1, List paramList, int paramInt1, int paramInt2, String paramString2, boolean paramBoolean);
  
  public abstract Page findByCriteriaWithPage(DetachedCriteria paramDetachedCriteria, int paramInt1, int paramInt2);
  
  public abstract Page findByCriteriaWithPage(DetachedCriteria paramDetachedCriteria, int paramInt1, int paramInt2, boolean paramBoolean);
  
  public abstract Page findByCriteriaWithPage(Criteria paramCriteria, int paramInt1, int paramInt2);
  
  public abstract Page findByCriteriaWithPage(Criteria paramCriteria, int paramInt1, int paramInt2, boolean paramBoolean);
  
  public abstract void clear();
  
  public abstract void flush();
  
  public abstract void bulkUpdate(String paramString);
  
  public abstract void bulkUpdate(String paramString, Object paramObject);
  
  public abstract void bulkUpdate(String paramString, Object[] paramArrayOfObject);
}
