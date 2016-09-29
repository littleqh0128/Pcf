package com.wondersgroup.framework.resource.dao.impl;

import com.wondersgroup.framework.core.bo.Page;
import com.wondersgroup.framework.core.bo.hibernate.HqlParameter;
import com.wondersgroup.framework.core.dao.impl.AbstractHibernateExtdDAOImpl;
import com.wondersgroup.framework.resource.bo.ResourceGroup;
import com.wondersgroup.framework.resource.dao.ResourceGroupExtdDAO;
import com.wondersgroup.framework.security.bo.ACLResourceType;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class ResourceGroupExtdDAOImpl extends AbstractHibernateExtdDAOImpl implements ResourceGroupExtdDAO
{
  protected Class getEntityClass()
  {
    return ResourceGroup.class;
  }
  
  public Page getResourceGroupsByTypeByPage(ACLResourceType resourceType, int pageNo, int pageSize)
  {
    String qryHql = "select resourceGroup from ResourceGroup resourceGroup where resourceGroup.type =:type and removed = 0 order by resourceGroup.id";
    String countHql = "select count(resourceGroup) from ResourceGroup resourceGroup where resourceGroup.type =:type and removed = 0 ";
    List args = new ArrayList();
    args.add(new HqlParameter("type", resourceType));
    return findByHQLWithPage(qryHql, args, pageNo, pageSize, countHql);
  }
}
