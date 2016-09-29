package com.wondersgroup.framework.security.dao;

import com.wondersgroup.framework.core.bo.Page;
import com.wondersgroup.framework.core.dao.AbstractHibernateExtdDAO;
import com.wondersgroup.framework.security.bo.SecurityGroup;
import com.wondersgroup.framework.security.bo.SecurityRole;
import com.wondersgroup.framework.security.bo.SecurityUser;
import java.util.List;
import java.util.Map;

public abstract interface UserExtdDAO extends AbstractHibernateExtdDAO
{
  public abstract SecurityUser loadByLoginName(String paramString);
  
  public abstract SecurityUser getLogoutUserByLoginName(String paramString);
  
  public abstract Page getAllUsersInGroupByPage(SecurityGroup paramSecurityGroup, int paramInt1, int paramInt2);
  
  public abstract Page getAllUsersNotInGroupByPage(SecurityGroup paramSecurityGroup, int paramInt1, int paramInt2);
  
  public abstract List getAllRolesOfUser(SecurityUser paramSecurityUser);
  
  public abstract SecurityUser[] getAllUsersInGroup(SecurityGroup paramSecurityGroup);
  
  public abstract SecurityUser[] getAllUsersNotInGroup(SecurityGroup paramSecurityGroup);
  
  public abstract List findUsersByProperties(String paramString1, String paramString2);
  
  public abstract List findUsersByProperties(Map paramMap);
  
  public abstract SecurityUser getUserByDefaultRole(SecurityRole paramSecurityRole);
  
  public abstract List getAllUsers();
  
  public abstract Page getAllUsersByPage(Map paramMap1, Map paramMap2, int paramInt1, int paramInt2);
  
  public abstract List getAllForbiddenUsers();
  
  public abstract boolean isUserInGroup(SecurityUser paramSecurityUser, SecurityGroup paramSecurityGroup);
  
  public abstract boolean isUserInDynamicGroup(SecurityUser paramSecurityUser, SecurityGroup paramSecurityGroup);
  
  public abstract Page getUserNotInNodeByPage(List paramList, String paramString, int paramInt1, int paramInt2);
  
  public abstract Page getUserInIds(List ids);

  public abstract Page getUserInOrgansIds(List ids, List organNodes);
  
  public abstract Page getAllUsersNotInGroupByPageAndName(SecurityGroup paramSecurityGroup, int paramInt1, int paramInt2, String paramString);
  
  public abstract Page getAllOrganUsersByPage(List paramList, int paramInt1, int paramInt2);
  
  public abstract Page getAllGroupsOfUserByPage(SecurityUser paramSecurityUser, int paramInt1, int paramInt2);
  
  public abstract void addGroupToUser(SecurityGroup paramSecurityGroup, SecurityUser paramSecurityUser);
  
  public abstract Page getAllGroupsNotOfUserByPageAndName(SecurityUser paramSecurityUser, int paramInt1, int paramInt2, String paramString);
  
  public abstract Page getAllGroupsNotOfUserByPage(SecurityUser paramSecurityUser, int paramInt1, int paramInt2);
  
  public abstract Page getInActiveUsersByPage(Map paramMap1, Map paramMap2, int paramInt1, int paramInt2);
  
  public abstract Page getForbiddenUsersByPage(Map paramMap1, Map paramMap2, int paramInt1, int paramInt2);
  
  public abstract List getAllInActiveUsers();
  
  public abstract Page getAllOrganUsersLikeNameByPage(String paramString, List paramList, int paramInt1, int paramInt2);
}
