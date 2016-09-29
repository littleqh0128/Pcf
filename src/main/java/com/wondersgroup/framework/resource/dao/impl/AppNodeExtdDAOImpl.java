package com.wondersgroup.framework.resource.dao.impl;

import com.wondersgroup.framework.core.bo.Page;
import com.wondersgroup.framework.core.bo.hibernate.HqlParameter;
import com.wondersgroup.framework.core.dao.impl.AbstractHibernateExtdDAOImpl;
import com.wondersgroup.framework.resource.bo.AppNode;
import com.wondersgroup.framework.resource.dao.AppNodeExtdDAO;
import com.wondersgroup.framework.security.bo.SecurityUser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

@Repository
public class AppNodeExtdDAOImpl extends AbstractHibernateExtdDAOImpl implements AppNodeExtdDAO
{
  protected Class getEntityClass()
  {
    return AppNode.class;
  }
  
  public List findNodesWithCentralNode(AppNode centralNode)
  {
    String query = "from AppNode node where node.centerNode = ? and removed=0";
    return findByHQL(query, centralNode);
  }
  
  public List findAllCentralNodes()
  {
    String query = "from AppNode node where node.nodeType = 0 and removed=0";
    return getHibernateTemplate().find(query);
  }
  
  public Page findByLikeNameWithPage(Map filter, Map sortMap, int pageNo, int pageSize)
  {
    StringBuffer qryHql = new StringBuffer();
    List<HqlParameter> args = new ArrayList();
    if (filter == null) {
      filter = new HashMap();
    }
    qryHql.append("from AppNode node where 1=1 ");
    String nameValue = (String)filter.get("name");
    if (StringUtils.isNotBlank(nameValue))
    {
      qryHql.append(" and node.name like :name");
      args.add(new HqlParameter("name", "%" + nameValue + "%"));
    }
    Long nodeType = (Long)filter.get("nodeType");
    if (nodeType != null)
    {
      qryHql.append(" and node.nodeType = :nodeType");
      args.add(new HqlParameter("nodeType", nodeType));
    }
    Integer removed = (Integer)filter.get("removed");
    if (removed != null)
    {
      qryHql.append(" and node.removed = :removed");
      args.add(new HqlParameter("removed", removed));
    }
    AppNode centerNode = (AppNode)filter.get("centerNode");
    if (centerNode != null)
    {
      qryHql.append(" and node.centerNode = :centerNode");
      args.add(new HqlParameter("centerNode", Long.valueOf(centerNode.getId())));
    }
    return findByHQLWithPage(qryHql.toString(), args, pageNo, pageSize, null);
  }
  
  public List getAppNodeByEntryPath(String appEntryPath)
  {
    return super.findByHQL("from AppNode node where node.indexURL = ? and node.removed = 0", appEntryPath);
  }
  
  public Page getAllUsersNotInAppNodeByPage(AppNode appNode, int pageNo, int pageSize, Map filter)
  {
    StringBuffer hql = new StringBuffer();
    hql.append("select user from SecurityUser user where :appNode <> all elements(user.appNodes) and user.name <> 'admin' and user.loginName <> 'admin' and user.removed = 0 and user.status = 1");
    
    List args = new ArrayList();
    if (!filter.isEmpty())
    {
      hql.append(" and ((1 = 0)");
      for (Object key : filter.keySet())
      {
        hql.append(" or user.").append(String.valueOf(key)).append(" like :").append(String.valueOf(key));
        args.add(new HqlParameter(String.valueOf(key), "%" + filter.get(key) + "%"));
      }
      hql.append(")");
    }
    args.add(new HqlParameter("appNode", appNode));
    return findByHQLWithPage(hql.toString(), args, pageNo, pageSize, null);
  }
  
  public Page getAllUsersInAppNodeByPage(AppNode appNode, int pageNo, int pageSize)
  {
    String hql = "select user from SecurityUser user left join user.appNodes appNodes where :appNode in appNodes and user.name <> 'admin' and user.loginName <> 'admin' and user.removed = 0 and user.status = 1";
    List args = new ArrayList();
    args.add(new HqlParameter("appNode", appNode));
    return findByHQLWithPage(hql, args, pageNo, pageSize, null);
  }
  
  public List findUsersNotInAppNode(AppNode appNode, Map filter)
  {
    StringBuffer hql = new StringBuffer();
    hql.append("select user from SecurityUser user where :appNode <> all elements(user.appNodes) and user.name <> 'admin' and user.loginName <> 'admin' and user.removed = 0 and user.status = 1");
    
    List args = new ArrayList();
    if (!filter.isEmpty())
    {
      hql.append(" and ((1 = 0)");
      for (Object key : filter.keySet())
      {
        hql.append(" or user.").append(String.valueOf(key)).append(" like :").append(String.valueOf(key));
        args.add(new HqlParameter(String.valueOf(key), "%" + filter.get(key) + "%"));
      }
      hql.append(")");
    }
    args.add(new HqlParameter("appNode", appNode));
    return findByHQL(hql.toString(), args);
  }
  
  public boolean isUserInAppNode(SecurityUser user, AppNode appNode)
  {
    StringBuffer hql = new StringBuffer();
    hql.append("select user from SecurityUser user left join user.appNodes appNodes where :user = user and :appNode in appNodes ");
    
    hql.append(" and user.name <> 'admin' and user.loginName <> 'admin' and user.removed = 0 and user.status = 1");
    List args = new ArrayList();
    
    args.add(new HqlParameter("user", user));
    args.add(new HqlParameter("appNode", appNode));
    
    return findByHQL(hql.toString(), args).size() > 0;
  }
}
