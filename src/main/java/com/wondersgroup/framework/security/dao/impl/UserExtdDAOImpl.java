package com.wondersgroup.framework.security.dao.impl;

import com.wondersgroup.framework.core.bo.Page;
import com.wondersgroup.framework.core.bo.hibernate.HqlParameter;
import com.wondersgroup.framework.core.dao.impl.AbstractHibernateExtdDAOImpl;
import com.wondersgroup.framework.security.bo.SecurityGroup;
import com.wondersgroup.framework.security.bo.SecurityRole;
import com.wondersgroup.framework.security.bo.SecurityUser;
import com.wondersgroup.framework.security.dao.UserExtdDAO;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

@Repository
public class UserExtdDAOImpl extends AbstractHibernateExtdDAOImpl implements UserExtdDAO
{
  protected Class getEntityClass()
  {
    return SecurityUser.class;
  }
  
  public SecurityUser getLogoutUserByLoginName(String loginName)
  {
    Session session = null;
    try
    {
      session = getSession();
      Criteria criteria = session.createCriteria(getEntityClass());
      criteria.add(Restrictions.eq("loginName", loginName));
      criteria.add(Restrictions.eq("removed", new Integer(1)));
      List result = criteria.list();
      SecurityUser localSecurityUser;
      if (result.size() > 0) {
        return (SecurityUser)result.get(0);
      }
      return null;
    }
	catch(Exception e){
		logger.debug(e.toString());
		throw e;
	}
    
  }
  
  public SecurityUser loadByLoginName(String loginName)
  {
    Session session = null;
    try
    {
      session = getSession();
      Criteria criteria = session.createCriteria(getEntityClass());
      criteria.add(Restrictions.eq("loginName", loginName));
      criteria.add(Restrictions.eq("removed", new Integer(0)));
      List result = criteria.list();
      SecurityUser localSecurityUser;
      if (result.size() > 0) {
        return (SecurityUser)result.get(0);
      }
      return null;
    }
	catch(Exception e){
		logger.debug(e.toString());
		throw e;
	}
  }
  
  public boolean isUserInGroup(SecurityUser user, SecurityGroup group)
  {
    String hql = "select user from SecurityGroup grp join grp.users as user where user = :user and grp = :group";
    List args = new ArrayList();
    args.add(new HqlParameter("user", user));
    args.add(new HqlParameter("group", group));
    List result = findByHQL(hql, args);
    return !result.isEmpty();
  }
  
  public boolean isUserInDynamicGroup(SecurityUser user, SecurityGroup group)
  {
    Map filter = group.getParamsMap();
    Session session = null;
    try
    {
      session = getSession();
      Criteria criteria = session.createCriteria(getEntityClass());
      criteria.add(Restrictions.eq("id", new Long(user.getId())));
      for (Iterator i = filter.keySet().iterator(); i.hasNext();)
      {
        String name = (String)i.next();
        criteria.add(Restrictions.like(name, (String)filter.get(name), MatchMode.ANYWHERE));
      }
      return !criteria.list().isEmpty() ? true : false;
    }
	catch(Exception e){
		logger.debug(e.toString());
		throw e;
	}
  }
  
  public Page getAllUsersInGroupByPage(SecurityGroup group, int pageNo, int pageSize)
  {
    String hql = "select user from SecurityGroup grp join grp.users as user where grp = :group and user.removed = 0 and user.status = 1";
    String countHql = "select count(user) from SecurityGroup g join g.users user where g=:group and user.removed = 0 and user.status = 1";
    List args = new ArrayList();
    args.add(new HqlParameter("group", group));
    return findByHQLWithPage(hql, args, pageNo, pageSize, countHql);
  }
  
  public Page getAllUsersNotInGroupByPage(SecurityGroup group, int pageNo, int pageSize)
  {
    String hql = "select user from SecurityUser user where :group <> all elements(user.groups) and user.name <> 'admin' and user.loginName <> 'admin' and user.removed = 0 and user.status = 1";
    List args = new ArrayList();
    args.add(new HqlParameter("group", group));
    return findByHQLWithPage(hql, args, pageNo, pageSize, null);
  }
  
  public Page getAllUsersNotInGroupByPageAndName(SecurityGroup group, int pageNo, int pageSize, String name)
  {
    String hql = "select user from SecurityUser user where :group <> all elements(user.groups) and user.name <> 'admin' and user.loginName <> 'admin' and user.name like '%" + name + "%' and user.removed = 0 and user.status = 1";
    
    List args = new ArrayList();
    args.add(new HqlParameter("group", group));
    
    return findByHQLWithPage(hql, args, pageNo, pageSize, null);
  }
  
  public List getAllRolesOfUser(SecurityUser user)
  {
    String hql = "select user.roles from SecurityUser user where user = ?";
    return findByHQL(hql, user);
  }
  
  public SecurityUser[] getAllUsersInGroup(SecurityGroup group)
  {
    String hql = "select user from SecurityGroup grp join grp.users as user where grp = ? and user.removed = 0 and user.status = 1";
    List results = findByHQL(hql, group);
    return (SecurityUser[])results.toArray(new SecurityUser[results.size()]);
  }
  
  public SecurityUser[] getAllUsersNotInGroup(SecurityGroup group)
  {
    String hql = "select user from SecurityUser user where ? <> all elements(user.groups) and user.name <> 'admin'  and user.removed = 0 and user.status = 1";
    List results = findByHQL(hql, group);
    return (SecurityUser[])results.toArray(new SecurityUser[results.size()]);
  }
  
  public List findUsersByProperties(String key, String value)
  {
    Criteria criteria = getSession().createCriteria(getEntityClass());
    criteria.add(Restrictions.eq(key, value));
    return criteria.list();
  }
  
  public List findUsersByProperties(Map properties)
  {
    Collection result = new ArrayList();
    List userArray = new ArrayList();
    for (Iterator i = properties.keySet().iterator(); i.hasNext();)
    {
      String key = (String)i.next();
      String value = (String)properties.get(key);
      List users = findUsersByProperties(key, value);
      if (userArray.size() == 0) {
        userArray = users;
      } else {
        result = CollectionUtils.intersection(userArray, users);
      }
    }
    return new ArrayList(result);
  }
  
  public SecurityUser getUserByDefaultRole(SecurityRole role)
  {
    String name = role.getName();
    int lastIndex = name.lastIndexOf("_");
    int index = "USER_".length();
    String loginName = name.substring(index, lastIndex);
    return (SecurityUser)findUniqueBy("loginName", loginName);
  }
  
  public List getAllUsers()
  {
    String hql = "select user from SecurityUser user where user.name <> 'admin' and user.removed = 0 and user.status = 1";
    List results = getHibernateTemplate().find(hql);
    return results;
  }
  
  public List getAllForbiddenUsers()
  {
    String hql = "from SecurityUser user where user.status = -1";
    return getHibernateTemplate().find(hql);
  }
  
  public Page getUserNotInNodeByPage(List ids, String name, int pageNo, int pageSize)
  {
    List args = new ArrayList();
    String hql = null;
    if (name != null)
    {
      if ((ids != null) && (ids.size() > 0))
      {
        hql = "select user from SecurityUser user where user not in (:ids) and user.name=:name and user.name <> 'admin' and user.removed = 0 and user.status = 1";
        args.add(new HqlParameter("ids", ids));
      }
      else
      {
        hql = "select user from SecurityUser user where  user.name=:name and user.name <> 'admin' and user.removed = 0 and user.status = 1";
      }
      args.add(new HqlParameter("name", name));
      return findByHQLWithPage(hql, args, pageNo, pageSize, null);
    }
    if ((ids != null) && (ids.size() > 0))
    {
      hql = "select user from SecurityUser user where user not in (:ids) and user.name <> 'admin' and user.removed = 0 and user.status = 1";
      args.add(new HqlParameter("ids", ids));
    }
    else
    {
      hql = "select user from SecurityUser user where  user.name <> 'admin' and user.removed = 0 and user.status = 1";
    }
    return findByHQLWithPage(hql, args, pageNo, pageSize, null);
  }
  
  public Page getUserInIds(List ids)
  {
    List args = new ArrayList();
    String hql = null;
    hql = "select user from SecurityUser user where user in (:ids) and user.name <> 'admin' and user.removed = 0 and user.status = 1";
    args.add(new HqlParameter("ids", ids));
    return findByHQLWithPage(hql, args, 1, ids.size(), null);
  }
  
  public Page getUserInOrgansIds(List ids, List organNodes)
  {
    List args = new ArrayList();
    String organNodesHql = "and organNode in (:organNodes)";
    String hql = "select user from SecurityUser user join user.organNodes organNode where user in (:ids) and user.name <> 'admin' and user.removed = 0 and user.status = 1 " + organNodesHql;
    String countHql = "select count(user) " + hql.substring(hql.indexOf(" from"));
    args.add(new HqlParameter("ids", ids));
    args.add(new HqlParameter("organNodes", organNodes));
    return findByHQLWithPage(hql, args, 1, ids.size(), countHql);
  }
  
  
  public Page getAllOrganUsersByPage(List organNodes, int pageNo, int pageSize)
  {
    String organNodesHql = "and organNode in (:organNodes)";
    String hql = "select user from SecurityUser user join user.organNodes organNode where user.name <> 'admin' and user.removed = 0 and user.status = 1 " + organNodesHql + " order by user.id";
    
    String countHql = "select count(user) from SecurityUser user join user.organNodes organNode where user.name <> 'admin' and user.removed=0 and user.status = 1 " + organNodesHql;
    
    List args = new ArrayList();
    if ((organNodes != null) && (organNodes.size() > 0))
    {
      args.add(new HqlParameter("organNodes", organNodes));
    }
    else
    {
      hql = hql.replaceAll(organNodesHql, "and organNode is null");
      countHql = countHql.replaceAll(organNodesHql, "and organNode is null");
    }
    return findByHQLWithPage(hql, args, pageNo, pageSize, countHql);
  }
  
  public Page getAllOrganUsersLikeNameByPage(String userName, List organNodes, int pageNo, int pageSize)
  {
    String organNodesHql = "and organNode in (:organNodes)";
    String hql = "select user from SecurityUser user join user.organNodes organNode where user.name <> 'admin' and user.removed = 0 and user.status = 1 " + organNodesHql;
    
    String countHql = "select count(user) from SecurityUser user join user.organNodes organNode where user.name <> 'admin' and user.removed=0 and user.status = 1 " + organNodesHql;
    
    List args = new ArrayList();
    if ((userName != null) && (!userName.trim().equals("")))
    {
      hql = hql + " and user.name like :name  order by user.id ";
      
      countHql = countHql + " and user.name like :name ";
      args.add(new HqlParameter("name", "%" + userName + "%"));
    }
    if ((organNodes != null) && (organNodes.size() > 0))
    {
      args.add(new HqlParameter("organNodes", organNodes));
    }
    else
    {
      hql = hql.replaceAll(organNodesHql, "and organNode is null");
      countHql = countHql.replaceAll(organNodesHql, "and organNode is null");
    }
    return findByHQLWithPage(hql, args, pageNo, pageSize, countHql);
  }
  
  public void addGroupToUser(SecurityGroup group, SecurityUser user)
  {
    user = (SecurityUser)loadWithLazy(new Long(user.getId()), new String[] { "groups" });
    user.getGroups().add(group);
    update(user);
  }
  
  public Page getAllGroupsNotOfUserByPage(SecurityUser user, int pageNo, int pageSize)
  {
    StringBuffer hql = new StringBuffer("select group from SecurityGroup group where group.id not in").append(" (select grp.id from SecurityUser u join u.groups as grp where u = :user and grp.removed = 0)").append(" and group.dyna = 0 and group.removed = 0");
    
    List args = new ArrayList();
    args.add(new HqlParameter("user", user));
    return findByHQLWithPage(hql.toString(), args, pageNo, pageSize, null);
  }
  
  public Page getAllGroupsNotOfUserByPageAndName(SecurityUser user, int pageNo, int pageSize, String name)
  {
    StringBuffer hql = new StringBuffer("select group from SecurityGroup group where group.id not in").append(" (select grp.id from SecurityUser u join u.groups as grp where u = :user and grp.removed = 0)").append(" and group.dyna = 0 and group.removed = 0 and group.name like :name");
    
    List args = new ArrayList();
    args.add(new HqlParameter("user", user));
    args.add(new HqlParameter("name", "%" + name + "%"));
    return findByHQLWithPage(hql.toString(), args, pageNo, pageSize, null);
  }
  
  public Page getAllGroupsOfUserByPage(SecurityUser user, int pageNo, int pageSize)
  {
    String hql = "select group from SecurityUser u join u.groups as group where u = :user and group.removed = 0";
    List args = new ArrayList();
    args.add(new HqlParameter("user", user));
    return findByHQLWithPage(hql, args, pageNo, pageSize, null);
  }
  
  public Page getInActiveUsersByPage(Map filter, Map sort, int pageNo, int pageSize)
  {
    String hql = " from SecurityUser user where user.status = 0 and user.removed = 0";
    return findByHQLWithPage(hql, pageNo, pageSize, null);
  }
  
  public List getAllInActiveUsers()
  {
    String hql = " from SecurityUser user where user.status = 0 and user.removed = 0";
    return getHibernateTemplate().find(hql);
  }
  
  public Page getForbiddenUsersByPage(Map filter, Map sort, int pageNo, int pageSize)
  {
    if (filter == null) {
      filter = new HashMap();
    }
    filter.remove("status");
    filter.put("status", new Long(-1L));
    return getAllUsersWithStatusByPage(filter, sort, pageNo, pageSize);
  }
  
  public Page getAllUsersByPage(Map filter, Map sort, int pageNo, int pageSize)
  {
    if (filter == null) {
      filter = new HashMap();
    }
    filter.remove("status");
    filter.put("status", new Long(1L));
    return getAllUsersWithStatusByPage(filter, sort, pageNo, pageSize);
  }
  
  private Page getAllUsersWithStatusByPage(Map filter, Map sort, int pageNo, int pageSize)
  {
    String hql = "select user from SecurityUser user where user.loginName <> 'admin' and user.removed = 0 and user.status =:status";
    String countHql = "select count(user) from SecurityUser user where user.loginName <> 'admin' and user.removed=0 and user.status =:status";
    String filterPart = "";
    List args = new ArrayList();
    args.add(new HqlParameter("status", filter.get("status")));
    Iterator i;
    if (filter != null)
    {
      filter.remove("removed");
      filter.remove("status");
      for (i = filter.keySet().iterator(); i.hasNext();)
      {
        String name = (String)i.next();
        if (name.equals("organNode"))
        {
          filterPart = filterPart + " and :organNode in elements(user.organNodes) ";
          args.add(new HqlParameter("organNode", filter.get(name)));
        }
        else if (name.equals("name"))
        {
          filterPart = filterPart + " and user." + name + " like '%" + filter.get(name) + "%'";
        }
        else if (name.equals("status"))
        {
          filterPart = filterPart + " and user." + name + " =" + filter.get(name);
        }
        else
        {
          filterPart = filterPart + " and user." + name + " = '" + filter.get(name) + "'";
        }
      }
    }
    return findByHQLWithPage(hql + filterPart, args, pageNo, pageSize, countHql + filterPart);
  }
}
