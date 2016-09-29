package com.wondersgroup.framework.security.dao.impl;

import com.wondersgroup.framework.core.bo.Page;
import com.wondersgroup.framework.core.bo.hibernate.HqlParameter;
import com.wondersgroup.framework.core.dao.impl.AbstractHibernateExtdDAOImpl;
import com.wondersgroup.framework.security.bo.ACLResourceType;
import com.wondersgroup.framework.security.dao.ACLResourceTypeExtdDAO;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public class ACLResourceTypeExtdDAOImpl extends AbstractHibernateExtdDAOImpl implements ACLResourceTypeExtdDAO
{
  protected Class getEntityClass()
  {
    return ACLResourceType.class;
  }
  
  public Page getOperationsByPage(ACLResourceType type, int pageNo, int pageSize)
  {
    String hql = "from ACLOperation operation  where operation.aclResourceType = :type and operation.removed = 0";
    List args = new ArrayList();
    args.add(new HqlParameter("type", type));
    return findByHQLWithPage(hql, args, pageNo, pageSize, null);
  }
  
  public List getOperations(ACLResourceType type)
  {
    String hql = "from ACLOperation operation  where operation.aclResourceType=? and operation.removed = 0";
    return findByHQL(hql, type);
  }
  
  public Page findResourceTypesByPage(Map filter, Map sort, int pageNo, int pageSize)
  {
    String nameParam = (String)filter.get("name");
    String qryHql = "select type from ACLResourceType type where type.removed = 0";
    String countHql = "select count(type) from ACLResourceType type where type.removed = 0";
    List args = new ArrayList();
    if (nameParam != null)
    {
      qryHql = qryHql + " and type.name like :name";
      countHql = countHql + " and type.name like :name";
      args.add(new HqlParameter("name", "%" + nameParam + "%"));
    }
    return super.findByHQLWithPage(qryHql, args, pageNo, pageSize, countHql);
  }
}
