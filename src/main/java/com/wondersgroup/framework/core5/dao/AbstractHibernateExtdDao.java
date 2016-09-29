package com.wondersgroup.framework.core5.dao;

import com.wondersgroup.framework.core.bo.Page;
import com.wondersgroup.framework.core.bo.Sorts;
import com.wondersgroup.framework.core.bo.hibernate.HqlParameter;
import com.wondersgroup.framework.core5.dao.support.Filters;
import com.wondersgroup.framework.core5.dao.support.QueryCallBack;
import java.io.Serializable;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;

public abstract interface AbstractHibernateExtdDao<T> extends AbstractExtdDao<T>
{
  public abstract int countByCriteria(DetachedCriteria paramDetachedCriteria);
  
  public abstract int countByHQL(String paramString);
  
  public abstract int countByHQL(String paramString, List<HqlParameter> paramList);
  
  public abstract List<T> findByCriteria(DetachedCriteria paramDetachedCriteria);
  
  public abstract List<T> findByHQL(String paramString, Object paramObject);
  
  public abstract List<T> findByHQL(String paramString, List<HqlParameter> paramList);
  
  public abstract Page findByHQLWithPage(String paramString1, int paramInt1, int paramInt2, String paramString2);
  
  public abstract Page findByHQLWithPage(String paramString1, List<HqlParameter> paramList, int paramInt1, int paramInt2, String paramString2);
  
  public abstract Page findByHQLWithPage(String paramString1, List<HqlParameter> paramList, int paramInt1, int paramInt2, String paramString2, boolean paramBoolean);
  
  public abstract T loadWithLazy(Serializable paramSerializable, String... paramVarArgs);
  
  public abstract Object getSequenceNextValue(String paramString);
  
  public abstract <F extends Filters> Page query(Page paramPage, F paramF, Sorts paramSorts, QueryCallBack<F> paramQueryCallBack);
  
  public abstract Page findByCriteriaWithPage(DetachedCriteria paramDetachedCriteria, int paramInt1, int paramInt2);
  
  public abstract Page findByCriteriaWithPage(DetachedCriteria paramDetachedCriteria, int paramInt1, int paramInt2, boolean paramBoolean);
  
  public abstract Page findByCriteriaWithPage(Criteria paramCriteria, int paramInt1, int paramInt2);
  
  public abstract Page findByCriteriaWithPage(Criteria paramCriteria, int paramInt1, int paramInt2, boolean paramBoolean);
  
  public abstract void bulkUpdate(String paramString, Object... paramVarArgs);
  
  public abstract void clear();
  
  public abstract void flush();
}
