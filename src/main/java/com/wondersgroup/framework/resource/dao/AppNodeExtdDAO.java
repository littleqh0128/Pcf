package com.wondersgroup.framework.resource.dao;

import com.wondersgroup.framework.core.bo.Page;
import com.wondersgroup.framework.core.dao.AbstractHibernateExtdDAO;
import com.wondersgroup.framework.resource.bo.AppNode;
import com.wondersgroup.framework.security.bo.SecurityUser;
import java.util.List;
import java.util.Map;

public abstract interface AppNodeExtdDAO extends AbstractHibernateExtdDAO
{
  public abstract List findNodesWithCentralNode(AppNode paramAppNode);
  
  public abstract List findAllCentralNodes();
  
  public abstract Page findByLikeNameWithPage(Map paramMap1, Map paramMap2, int paramInt1, int paramInt2);
  
  public abstract List getAppNodeByEntryPath(String paramString);
  
  public abstract Page getAllUsersNotInAppNodeByPage(AppNode paramAppNode, int paramInt1, int paramInt2, Map paramMap);
  
  public abstract Page getAllUsersInAppNodeByPage(AppNode paramAppNode, int paramInt1, int paramInt2);
  
  public abstract List findUsersNotInAppNode(AppNode paramAppNode, Map paramMap);
  
  public abstract boolean isUserInAppNode(SecurityUser paramSecurityUser, AppNode paramAppNode);
}
