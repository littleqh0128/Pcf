package com.wondersgroup.framework.security.service;

import com.wondersgroup.framework.core.bo.Page;
import com.wondersgroup.framework.organization.bo.OrganNode;
import com.wondersgroup.framework.resource.bo.AppNode;
import com.wondersgroup.framework.security.authentication.connector.AuthenticationUserConnectorExtd;
import com.wondersgroup.framework.security.bo.SecurityGroup;
import com.wondersgroup.framework.security.bo.SecurityRole;
import com.wondersgroup.framework.security.bo.SecurityUser;
import com.wondersgroup.framework.security.bo.UserLoginHistory;
import com.wondersgroup.framework.security.dto.UserDTOExtd;

import java.util.List;
import java.util.Map;

public abstract interface UserExtdService extends AuthenticationUserConnectorExtd
{
  public abstract void createNewUser(SecurityUser paramSecurityUser);
  
  public abstract void updateUser(SecurityUser paramSecurityUser);
  
  public abstract void removeUser(SecurityUser paramSecurityUser);
  
  public abstract SecurityUser loadUserById(long paramLong);
  
  public abstract SecurityUser getUserByLoginName(String paramString);
  
  public abstract SecurityUser getLogoutUserByLoginName(String paramString);
  
  public abstract SecurityUser loadUserWithLazyById(long paramLong, String[] paramArrayOfString);
  
  public abstract SecurityUser[] findUsersByProperties(String paramString1, String paramString2);
  
  public abstract SecurityUser[] findUsersByProperties(Map paramMap);
  
  /**
   * @deprecated
   */
  public abstract SecurityUser[] findUsersByBasicAndProps(Map paramMap1, Map paramMap2);
  
  public abstract SecurityUser[] findUsersBy(Map paramMap);
  
  public abstract SecurityUser[] getAllUsers();
  
  public abstract Page getAllUsersByPage(Map paramMap1, Map paramMap2, int paramInt1, int paramInt2);
  
  public abstract void forbidUser(SecurityUser paramSecurityUser);
  
  public abstract void allowUser(SecurityUser paramSecurityUser);
  
  public abstract void activateUser(SecurityUser paramSecurityUser);
  
  public abstract SecurityUser[] getAllForbiddenUsers();
  
  public abstract boolean createNewGroup(SecurityGroup paramSecurityGroup);
  
  public abstract void updateGroup(SecurityGroup paramSecurityGroup);
  
  public abstract void removeGroup(SecurityGroup paramSecurityGroup);
  
  public abstract SecurityGroup loadGroupById(long paramLong);
  
  public abstract SecurityGroup loadGroupByCode(String paramString);
  
  public abstract SecurityGroup getGroupByName(String paramString);
  
  public abstract SecurityGroup getGroupByNameWithoutRemoved(String paramString);
  
  public abstract Page getAllGroupsByPage(Map paramMap1, Map paramMap2, int paramInt1, int paramInt2);
  
  public abstract boolean isUserInGroup(SecurityUser paramSecurityUser, SecurityGroup paramSecurityGroup);
  
  public abstract boolean isUserInDynamicGroup(SecurityUser paramSecurityUser, SecurityGroup paramSecurityGroup);
  
  public abstract Page getAllUsersInGroupByPage(SecurityGroup paramSecurityGroup, int paramInt1, int paramInt2);
  
  public abstract Page getAllUsersNotInGroupByPage(SecurityGroup paramSecurityGroup, int paramInt1, int paramInt2);
  
  public abstract void addUserToGroup(SecurityUser paramSecurityUser, SecurityGroup paramSecurityGroup);
  
  public abstract void removeUserFromGroup(SecurityUser paramSecurityUser, SecurityGroup paramSecurityGroup);
  
  public abstract SecurityGroup[] getAllGroups();
  
  public abstract SecurityUser[] getAllUsersInGroup(SecurityGroup paramSecurityGroup);
  
  public abstract SecurityUser[] getAllUsersNotInGroup(SecurityGroup paramSecurityGroup);
  
  public abstract SecurityGroup loadGroupWithLazyById(long paramLong, String[] paramArrayOfString);
  
  public abstract void updateUser(SecurityUser paramSecurityUser, boolean paramBoolean);
  
  public abstract void updateGroup(SecurityGroup paramSecurityGroup, boolean paramBoolean);
  
  public abstract SecurityUser getUserByDefaultRole(SecurityRole paramSecurityRole);
  
  public abstract void addNodeToUser(SecurityUser paramSecurityUser, AppNode paramAppNode);
  
  public abstract AppNode[] getNodesByUser(SecurityUser paramSecurityUser);
  
  public abstract void updateNodesWithUser(SecurityUser paramSecurityUser, AppNode[] paramArrayOfAppNode);
  
  public abstract void removeNodesFromUser(SecurityUser paramSecurityUser);
  
  /**
   * @deprecated
   */
  public abstract SecurityUser[] getAllUsersInGroup(SecurityGroup paramSecurityGroup, Map paramMap);
  
  public abstract Page getUserNotInNodeByPage(List paramList, String paramString, int paramInt1, int paramInt2);
  
  public abstract Page getUserInIds(List paramList);
  
  public abstract Page getUserInOrgansIds(List ids, List organNodes);
  
  public abstract Page getAllUsersNotInGroupByPageAndName(SecurityGroup paramSecurityGroup, int paramInt1, int paramInt2, String paramString);
  
  public abstract SecurityGroup[] getAllDynaGroups();
  
  public abstract Page getAllDynaGroupsByPage(Map paramMap, int paramInt1, int paramInt2);
  
  public abstract SecurityGroup[] getAllAuthControlDynaGroups();
  
  public abstract SecurityGroup[] getAllAuthControlGroup();
  
  public abstract Page getAllOrganUsersByPage(List paramList, int paramInt1, int paramInt2);
  
  public abstract OrganNode[] getOrgansByUser(SecurityUser paramSecurityUser);
  
  public abstract Page findByHQLWithPage(String paramString1, List paramList, int paramInt1, int paramInt2, String paramString2);
  
  public abstract Page getAllGroupsOfUserByPage(SecurityUser paramSecurityUser, int paramInt1, int paramInt2);
  
  public abstract void addGroupToUser(SecurityGroup paramSecurityGroup, SecurityUser paramSecurityUser);
  
  public abstract void removeGroupFromUser(SecurityGroup paramSecurityGroup, SecurityUser paramSecurityUser);
  
  public abstract Page getAllGroupsNotOfUserByPageAndName(SecurityUser paramSecurityUser, int paramInt1, int paramInt2, String paramString);
  
  public abstract Page getAllGroupsNotOfUserByPage(SecurityUser paramSecurityUser, int paramInt1, int paramInt2);
  
  public abstract Page getInActiveUsersByPage(Map paramMap1, Map paramMap2, int paramInt1, int paramInt2);
  
  public abstract Page getForbiddenUsersByPage(Map paramMap1, Map paramMap2, int paramInt1, int paramInt2);
  
  public abstract List getAllInActiveUsers();
  
  public abstract Page getAllOrganUsersLikeNameByPage(String paramString, List paramList, int paramInt1, int paramInt2);
  
  public abstract SecurityUser[] findByHQL(String paramString, List paramList);
  
  public abstract boolean updateUsersPassword(String paramString1, String paramString2, String paramString3);
  
  public abstract Map<String, String> getExtendPropertyNames();
  
  public abstract Map<String, String> getExtendPropertyMap(SecurityUser paramSecurityUser);
  
  public abstract void setExtendPropertyMap(SecurityUser paramSecurityUser, Map<String, String> paramMap);
  
  public abstract void addUserLoginHistory(UserLoginHistory paramUserLoginHistory);
  
  public abstract List findUserLogins(Map paramMap);
  
  public abstract Page findUserLoginsByPage(Map paramMap, int paramInt1, int paramInt2);
  
  public abstract long getErrorLoginCount(SecurityUser paramSecurityUser);
  
  public abstract SecurityUser[] queryUsers(UserDTOExtd paramUserDTO);
  
  public abstract Page queryUsersWithPage(UserDTOExtd paramUserDTO);
}
