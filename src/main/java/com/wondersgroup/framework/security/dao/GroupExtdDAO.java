package com.wondersgroup.framework.security.dao;

import com.wondersgroup.framework.core.bo.Page;
import com.wondersgroup.framework.core.dao.AbstractHibernateExtdDAO;
import com.wondersgroup.framework.security.bo.SecurityGroup;
import com.wondersgroup.framework.security.bo.SecurityRole;
import com.wondersgroup.framework.security.bo.SecurityUser;
import java.util.Map;

public abstract interface GroupExtdDAO extends AbstractHibernateExtdDAO
{
  public abstract void merge(Object paramObject);
  
  public abstract SecurityGroup loadByCodeWithoutRemoved(String paramString);
  
  public abstract SecurityGroup loadByCode(String paramString);
  
  public abstract SecurityGroup loadByNameWithoutRemoved(String paramString);
  
  public abstract SecurityGroup loadByName(String paramString);
  
  public abstract SecurityRole[] getAllRolesOfGroup(SecurityGroup paramSecurityGroup);
  
  public abstract void addUserToGroup(SecurityUser paramSecurityUser, SecurityGroup paramSecurityGroup);
  
  public abstract void removeUserFromGroup(SecurityUser paramSecurityUser, SecurityGroup paramSecurityGroup);
  
  public abstract SecurityGroup[] getAllDynaGroups();
  
  public abstract Page getAllDynaGroupsByPage(Map paramMap, int paramInt1, int paramInt2);
  
  public abstract SecurityGroup[] getAllAuthControlDynaGroups();
  
  public abstract SecurityGroup[] getAllAuthControlGroup();
  
  public abstract Page findByLikeNameWithPage(Map paramMap1, Map paramMap2, int paramInt1, int paramInt2);
}
