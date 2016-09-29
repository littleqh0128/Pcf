package com.wondersgroup.framework.organization.dao;

import com.wondersgroup.framework.core.dao.AbstractHibernateExtdDAO;
import com.wondersgroup.framework.organization.bo.OrganNode;
import com.wondersgroup.framework.organization.bo.OrganTree;
import com.wondersgroup.framework.security.bo.SecurityUser;
import java.util.List;

public abstract interface OrganNodeExtdDao extends AbstractHibernateExtdDAO
{
  public abstract List findAllRootTypeNodes();
  
  public abstract OrganNode loadByCode(String paramString);
  
  public abstract OrganNode loadByCodeWithLazy(String paramString, String[] paramArrayOfString);
  
  public abstract OrganNode getOrganNodeByCode(String paramString);
  
  public abstract OrganNode[] getOrganNodesByUserId(long paramLong);
  
  public abstract OrganNode[] getOrganNodesByUserAndTree(long paramLong, String paramString);
  
  public abstract OrganNode[] getOrganNodesByTypeCode(String paramString);
  
  public abstract OrganNode[] getOrganNodesByTypeCodeWithoutTree(String paramString, OrganTree paramOrganTree);
  
  public abstract void addOrganNodeToTree(OrganNode paramOrganNode1, OrganNode paramOrganNode2, OrganTree paramOrganTree);
  
  public abstract void removeOrganNodeFromTree(OrganNode paramOrganNode, OrganTree paramOrganTree, boolean paramBoolean);
  
  public abstract OrganNode[] getChildNodes(OrganNode paramOrganNode, OrganTree paramOrganTree);
  
  public abstract OrganNode getParentNode(OrganNode paramOrganNode, OrganTree paramOrganTree);
  
  public abstract OrganNode[] getChildNodesByName(OrganNode paramOrganNode, OrganTree paramOrganTree, String paramString);
  
  public abstract OrganNode[] getChildNodesByTypeCode(OrganNode paramOrganNode, OrganTree paramOrganTree, String paramString);
  
  public abstract OrganNode[] getBrotherNodesByTree(OrganNode paramOrganNode, OrganTree paramOrganTree);
  
  public abstract OrganNode loadOrganNodeWithUsers(long paramLong);
  
  public abstract OrganNode loadOrganNodeWithType(OrganNode paramOrganNode);
  
  public abstract void saveRootNode(OrganTree paramOrganTree);
  
  public abstract OrganNode[] getAllChildNodes(OrganNode paramOrganNode, OrganTree paramOrganTree);
  
  public abstract OrganNode[] getAllParentNodeByTypeCode(OrganTree paramOrganTree, OrganNode paramOrganNode, String paramString);
  
  public abstract OrganNode[] getAllParentNodesByTypeCode(OrganNode paramOrganNode, String paramString);
  
  public abstract OrganNode[] getAllSubNodesByTypeCode(OrganNode paramOrganNode, String paramString);
  
  public abstract OrganNode[] getUserOrganNodesByTypeCode(SecurityUser paramSecurityUser, String paramString);
}
