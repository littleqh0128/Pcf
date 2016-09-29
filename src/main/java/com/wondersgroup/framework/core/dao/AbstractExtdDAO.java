package com.wondersgroup.framework.core.dao;

import com.wondersgroup.framework.core.bo.Page;
import com.wondersgroup.framework.core.bo.Removable;
import com.wondersgroup.framework.core.bo.Sorts;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract interface AbstractExtdDAO
{
  public abstract void save(Object paramObject);
  
  public abstract void update(Object paramObject);
  
  public abstract void remove(Removable paramRemovable);
  
  public abstract void removeById(Serializable paramSerializable);
  
  public abstract void delete(Object paramObject);
  
  public abstract void deleteById(Serializable paramSerializable);
  
  public abstract Object load(Serializable paramSerializable);
  
  public abstract List findAll();
  
  public abstract List findAll(Sorts paramSorts);
  
  public abstract Object findUniqueBy(String paramString, Object paramObject);
  
  public abstract List findBy(String paramString, Object paramObject);
  
  public abstract List findBy(String paramString, Object paramObject, Sorts paramSorts);
  
  public abstract List findBy(Map paramMap);
  
  public abstract List findBy(Map paramMap, boolean paramBoolean);
  
  public abstract List findBy(Map paramMap, Sorts paramSorts, Boolean paramBoolean);
  
  public abstract List findByLike(String paramString1, String paramString2);
  
  public abstract List findByLike(Map paramMap);
  
  public abstract Page findAllWithPage(int paramInt1, int paramInt2);
  
  public abstract Page findByWithPage(Map paramMap1, Map paramMap2, int paramInt1, int paramInt2);
  
  public abstract Page findByWithPage(Map paramMap1, Map paramMap2, int paramInt1, int paramInt2, boolean paramBoolean);
}
