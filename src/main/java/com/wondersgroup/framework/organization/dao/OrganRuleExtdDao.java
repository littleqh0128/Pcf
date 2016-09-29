package com.wondersgroup.framework.organization.dao;

import com.wondersgroup.framework.core.bo.Page;
import com.wondersgroup.framework.core.dao.AbstractHibernateExtdDAO;
import com.wondersgroup.framework.organization.bo.OrganNodeType;
import com.wondersgroup.framework.organization.bo.OrganRule;
import com.wondersgroup.framework.organization.bo.OrganTreeType;
import java.util.List;

public abstract interface OrganRuleExtdDao extends AbstractHibernateExtdDAO
{
  public abstract OrganRule loadByCode(String paramString);
  
  public abstract Page findByLikeNameWithPage(String paramString, int paramInt1, int paramInt2);
  
  public abstract List findChildNodeTypesByParent(OrganTreeType paramOrganTreeType, OrganNodeType paramOrganNodeType);
  
  public abstract List findChildNodeTypeCodeArrayByParent(OrganTreeType paramOrganTreeType, OrganNodeType paramOrganNodeType);
  
  public abstract Page findRulesByTreeTypeWithPage(OrganTreeType paramOrganTreeType, int paramInt1, int paramInt2);
  
  public abstract List findRulesBySupSubNodeType(OrganNodeType paramOrganNodeType);
}
