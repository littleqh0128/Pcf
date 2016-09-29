package com.wondersgroup.framework.resource.service;

import com.wondersgroup.framework.core.bo.Page;
import com.wondersgroup.framework.resource.bo.AppNode;
import com.wondersgroup.framework.security.bo.SecurityUser;
import java.util.List;
import java.util.Map;

public abstract interface AppNodeExtdService
{
  public abstract AppNode loadById(long paramLong);
  
  public abstract AppNode findNodeByCode(String paramString);
  
  public abstract void addNewAppNode(AppNode paramAppNode);
  
  public abstract void removeAppNode(AppNode paramAppNode);
  
  public abstract void updateAppNode(AppNode paramAppNode);
  
  public abstract AppNode[] findAllNodes();
  
  public abstract Page findByWithPage(Map paramMap1, Map paramMap2, int paramInt1, int paramInt2);
  
  public abstract AppNode[] findAllCentralNodes();
  
  public abstract AppNode[] findNodesWithCentralNode(AppNode paramAppNode);
  
  public abstract SecurityUser[] findUsersByNode(AppNode paramAppNode);
  
  public abstract void addUserToNode(SecurityUser paramSecurityUser, AppNode paramAppNode);
  
  public abstract void removeUserFromNode(SecurityUser paramSecurityUser, AppNode paramAppNode);
  
  public abstract AppNode getAppNodeByEntryPath(String paramString);
  
  public abstract Page getAllUsersNotInAppNodeByPage(AppNode paramAppNode, int paramInt1, int paramInt2, Map paramMap);
  
  public abstract Page getAllUsersInAppNodeByPage(AppNode paramAppNode, int paramInt1, int paramInt2);
  
  public abstract List<SecurityUser> findUsersNotInAppNode(AppNode paramAppNode, Map paramMap);
  
  public abstract boolean isUserInAppNode(SecurityUser paramSecurityUser, AppNode paramAppNode);
}
