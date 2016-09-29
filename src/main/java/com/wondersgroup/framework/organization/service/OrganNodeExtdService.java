package com.wondersgroup.framework.organization.service;

import com.wondersgroup.framework.core.bo.Page;
import com.wondersgroup.framework.organization.bo.OrganModel;
import com.wondersgroup.framework.organization.bo.OrganNode;
import com.wondersgroup.framework.organization.bo.OrganTree;
import com.wondersgroup.framework.security.bo.SecurityGroup;
import com.wondersgroup.framework.security.bo.SecurityRole;
import com.wondersgroup.framework.security.bo.SecurityUser;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract interface OrganNodeExtdService
{
  public abstract boolean isAdminForOrganNodes(OrganNode paramOrganNode, SecurityUser paramSecurityUser);
  
  public abstract void setAdminForOrganNode(OrganNode paramOrganNode, SecurityUser paramSecurityUser);
  
  public abstract boolean isControledNodeByUser(OrganTree paramOrganTree, OrganNode paramOrganNode, SecurityUser paramSecurityUser);
  
  public abstract OrganNode[] getAllRootTypeNodes();
  
  public abstract SecurityRole[] getOrganRole(OrganNode paramOrganNode, OrganTree paramOrganTree);
  
  public abstract OrganTree[] getAllTrees(OrganNode paramOrganNode);
  
  public abstract void updateOrdersForOrganNode(OrganTree paramOrganTree, Map paramMap);
  
  public abstract long getOrdersByOrganTree(OrganNode paramOrganNode, OrganTree paramOrganTree);
  
  public abstract void addOrganNodeToTree(OrganNode paramOrganNode1, OrganNode paramOrganNode2, OrganTree paramOrganTree);
  
  public abstract void removeOrganNodeFromTree(OrganNode paramOrganNode, OrganTree paramOrganTree, boolean paramBoolean);
  
  public abstract void removeOrganNode(OrganNode paramOrganNode);
  
  public abstract void updateOrganNode(OrganNode paramOrganNode);
  
  public abstract void removeUserFromOrganNode(SecurityUser paramSecurityUser, OrganNode paramOrganNode);
  
  public abstract void addUserToOrganNode(SecurityUser paramSecurityUser, OrganNode paramOrganNode);
  
  public abstract OrganNode loadOrganNodeById(long paramLong);
  
  public abstract OrganNode loadOrganNodeByCode(String paramString);
  
  public abstract OrganNode getOrganNodeByCode(String paramString);
  
  public abstract OrganNode[] getOrganNodesByUserId(long paramLong);
  
  public abstract OrganNode[] getOrganNodesByUserAndTree(long paramLong, String paramString);
  
  public abstract OrganNode[] getBrotherNodesByTree(OrganNode paramOrganNode, OrganTree paramOrganTree);
  
  public abstract OrganNode[] getOrganNodesByTypeCode(String paramString);
  
  public abstract OrganNode[] getOrganNodesByTypeCodeWithoutTree(String paramString, OrganTree paramOrganTree);
  
  public abstract List getUsersByOrganOrder(long paramLong);
  
  public abstract void updateOrganOrderByUserOrder(OrganNode paramOrganNode, Set paramSet);
  
  public abstract OrganNode loadOrganNodeWithLazy(long paramLong, String[] paramArrayOfString);
  
  public abstract OrganNode loadOrganNodeByCodeWithLazy(String paramString, String[] paramArrayOfString);
  
  public abstract OrganNode[] getChildNodes(OrganNode paramOrganNode, OrganTree paramOrganTree);
  
  public abstract OrganNode getParentNode(OrganNode paramOrganNode, OrganTree paramOrganTree);
  
  public abstract OrganNode[] getChildNodesByName(OrganNode paramOrganNode, OrganTree paramOrganTree, String paramString);
  
  public abstract OrganNode[] getChildNodesByTypeCode(OrganNode paramOrganNode, OrganTree paramOrganTree, String paramString);
  
  public abstract void cutOrganTree(OrganNode paramOrganNode1, OrganTree paramOrganTree1, OrganNode paramOrganNode2, OrganTree paramOrganTree2);
  
  public abstract OrganNode[] getAllChildNodes(OrganNode paramOrganNode, OrganTree paramOrganTree);
  
  public abstract Page getAllUserInAdminNodeByPage(SecurityUser paramSecurityUser, OrganTree paramOrganTree, Map paramMap, int paramInt1, int paramInt2);
  
  public abstract OrganNode[] getOrganNodesByAdminUserAndTree(SecurityUser paramSecurityUser, OrganTree paramOrganTree);
  
  public abstract void reorderTreeNode(OrganTree paramOrganTree, OrganNode paramOrganNode1, OrganNode paramOrganNode2);
  
  public abstract List getOrganNodesByAdminUserAndTrees(SecurityUser paramSecurityUser, OrganTree[] paramArrayOfOrganTree);
  
  public abstract OrganNode[] getAllParentNodeByTypeCode(OrganTree paramOrganTree, OrganNode paramOrganNode, String paramString);
  
  public abstract SecurityUser[] queryUsersInAdminNode(SecurityUser paramSecurityUser, Map paramMap);
  
  public abstract Page queryUnContainedUsersInAdminNodeByGroup(SecurityGroup paramSecurityGroup, SecurityUser paramSecurityUser, Map paramMap, int paramInt1, int paramInt2);
  
  public abstract Map<String, String> getExtendPropertyMap(OrganNode paramOrganNode);
  
  public abstract void setExtendPropertyMap(OrganNode paramOrganNode, Map<String, String> paramMap);
  
  public abstract OrganModel[] getAdminOrganModels(String paramString1, String paramString2);
}
