package com.wondersgroup.framework.organization.dao;

import com.wondersgroup.framework.core.bo.Page;
import com.wondersgroup.framework.core.dao.AbstractHibernateExtdDAO;
import com.wondersgroup.framework.organization.bo.OrganModel;
import com.wondersgroup.framework.organization.bo.OrganNode;
import com.wondersgroup.framework.organization.bo.OrganTree;
import com.wondersgroup.framework.security.bo.SecurityRole;
import com.wondersgroup.framework.security.bo.SecurityUser;
import java.util.List;
import java.util.Map;

public abstract interface OrganModelExtdDao extends AbstractHibernateExtdDAO
{
  public abstract OrganModel loadOrganModelByNodeAndTree(OrganNode paramOrganNode, OrganTree paramOrganTree);
  
  public abstract List findAllTreesByNode(OrganNode paramOrganNode);
  
  public abstract OrganModel getOrganModelByNodeAndTree(OrganNode paramOrganNode, OrganTree paramOrganTree);
  
  public abstract OrganModel[] getChildModels(OrganNode paramOrganNode, OrganTree paramOrganTree);
  
  public abstract OrganModel getParentModel(OrganNode paramOrganNode, OrganTree paramOrganTree);
  
  public abstract OrganModel[] getChildModelsByName(OrganNode paramOrganNode, OrganTree paramOrganTree, String paramString);
  
  public abstract OrganModel[] getChildModelsTypeCode(OrganNode paramOrganNode, OrganTree paramOrganTree, String paramString);
  
  public abstract OrganModel[] getBrotherModels(OrganNode paramOrganNode1, OrganNode paramOrganNode2, OrganTree paramOrganTree);
  
  public abstract SecurityRole loadDefaultRoleWithOrgan(OrganNode paramOrganNode, OrganTree paramOrganTree);
  
  public abstract OrganModel getPrevBrotherModel(OrganNode paramOrganNode1, OrganNode paramOrganNode2, OrganTree paramOrganTree);
  
  public abstract void moveNodeInSameTree(OrganNode paramOrganNode1, OrganNode paramOrganNode2, OrganTree paramOrganTree);
  
  public abstract void moveNodeBetweenDifferentTrees(OrganNode paramOrganNode1, OrganTree paramOrganTree1, OrganNode paramOrganNode2, OrganTree paramOrganTree2);
  
  public abstract void removeCascadeOrganModel(int paramInt1, int paramInt2, OrganTree paramOrganTree);
  
  public abstract List findAllSuperiorNodes(OrganTree paramOrganTree, OrganNode paramOrganNode);
  
  public abstract OrganModel[] getAllChildModels(OrganModel paramOrganModel, OrganTree paramOrganTree);
  
  public abstract long getMaxOrderOfChildrenNodes(OrganNode paramOrganNode, OrganTree paramOrganTree);
  
  public abstract OrganModel[] getAllAdminOrganModel(SecurityUser paramSecurityUser, OrganTree paramOrganTree);
  
  public abstract OrganModel[] filterAllOrganModelArray(OrganModel[] paramArrayOfOrganModel);
  
  public abstract Page getAllUserInAdminNodeByPage(OrganModel[] paramArrayOfOrganModel, Map paramMap, int paramInt1, int paramInt2);
  
  public abstract boolean isAdminInTree(SecurityUser paramSecurityUser, OrganTree paramOrganTree);
  
  public abstract boolean judgetNodeExist(OrganNode paramOrganNode, OrganTree paramOrganTree1, OrganTree paramOrganTree2);
  
  public abstract void reorderOrganModel(OrganModel paramOrganModel1, OrganModel paramOrganModel2);
  
  public abstract Page getAllUsersByPage(String paramString, int paramInt1, int paramInt2);
}
