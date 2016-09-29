package com.wondersgroup.framework.core5.dao;

import com.wondersgroup.framework.core.bo.Page;
import com.wondersgroup.framework.core.bo.Removable;
import com.wondersgroup.framework.core.bo.Sorts;
import com.wondersgroup.framework.core5.model.RemovablePeer;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract interface AbstractExtdDao<T>
{
  public abstract void delete(T paramT);
  
  public abstract void deleteById(Serializable paramSerializable);
  
  public abstract List<T> findAll();
  
  public abstract List<T> findAll(Sorts paramSorts);
  
  public abstract Page findAllWithPage(int paramInt1, int paramInt2);
  
  public abstract List<T> findBy(Map<String, Object> paramMap);
  
  public abstract List<T> findBy(Map<String, Object> paramMap, boolean paramBoolean);
  
  public abstract List<T> findBy(Map<String, Object> paramMap, Sorts paramSorts, Boolean paramBoolean);
  
  public abstract List<T> findBy(String paramString, Object paramObject);
  
  public abstract List<T> findBy(String paramString, Object paramObject, Sorts paramSorts);
  
  public abstract List<T> findByLike(Map<String, String> paramMap);
  
  public abstract List<T> findByLike(String paramString1, String paramString2);
  
  public abstract Page findByWithPage(Map<String, Object> paramMap, Map<String, String> paramMap1, int paramInt1, int paramInt2);
  
  public abstract Page findByWithPage(Map<String, Object> paramMap, Map<String, String> paramMap1, int paramInt1, int paramInt2, boolean paramBoolean);
  
  public abstract T findUniqueBy(String paramString, Object paramObject);
  
  public abstract T load(Serializable paramSerializable);
  
  @Deprecated
  public abstract void remove(RemovablePeer paramRemovablePeer);
  
  public abstract void remove(Removable paramRemovable);
  
  public abstract void removeById(Serializable paramSerializable);
  
  public abstract void save(T paramT);
  
  public abstract void update(T paramT);
  
//  public abstract <E> E executeStoreFunction(String paramString, Class<E> paramClass);
//  
//  public abstract void executeStoreProcedure(String paramString);
//  
//  public abstract Object[] executeStoreProcedure(String paramString, SpParameters paramSpParameters, Object... paramVarArgs);
//  
//  public abstract void executeStoreProcedure(String paramString, SpSupportBean paramSpSupportBean);
}
