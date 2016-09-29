package com.wondersgroup.framework.organization.dao;

import com.wondersgroup.framework.core.dao.AbstractHibernateExtdDAO;
import com.wondersgroup.framework.organization.bo.OrganTree;

public abstract interface OrganTreeExtdDao extends AbstractHibernateExtdDAO
{
  public abstract OrganTree[] getOrganTreeByName(String paramString);
  
  public abstract void removeOrganTree(OrganTree paramOrganTree);
  
  public abstract void saveTree(OrganTree paramOrganTree);
  
  public abstract OrganTree getTreeWithRootNode(OrganTree paramOrganTree);
}
