package com.wondersgroup.framework.security.dao.impl;

import com.wondersgroup.framework.core.bo.Page;
import com.wondersgroup.framework.core.bo.hibernate.HqlParameter;
import com.wondersgroup.framework.core.dao.impl.AbstractHibernateExtdDAOImpl;
import com.wondersgroup.framework.security.bo.SecurityGroup;
import com.wondersgroup.framework.security.bo.SecurityRole;
import com.wondersgroup.framework.security.bo.SecurityUser;
import com.wondersgroup.framework.security.dao.GroupExtdDAO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

@Repository
public class GroupExtdDAOImpl extends AbstractHibernateExtdDAOImpl implements GroupExtdDAO
{
  public SecurityGroup loadByName(String name)
  {
    Criteria criteria = getSession().createCriteria(getEntityClass());
    criteria.add(Restrictions.eq("name", name));
    List result = criteria.list();
    if (result.size() > 0) {
      return (SecurityGroup)result.get(0);
    }
    return null;
  }
  
  public SecurityGroup loadByNameWithoutRemoved(String name)
  {
    Criteria criteria = getSession().createCriteria(getEntityClass());
    criteria.add(Restrictions.eq("name", name));
    criteria.add(Restrictions.eq("removed", new Integer(0)));
    List result = criteria.list();
    if (result.size() > 0) {
      return (SecurityGroup)result.get(0);
    }
    return null;
  }
  
  protected Class getEntityClass()
  {
    return SecurityGroup.class;
  }
  
  public void merge(Object obj)
  {
    getSession().merge(obj);
  }
  
  public SecurityRole[] getAllRolesOfGroup(SecurityGroup group)
  {
    String hql = "select grp.roles from SecurityGroup grp where grp = ?";
    List results = findByHQL(hql, group);
    return (SecurityRole[])results.toArray(new SecurityRole[results.size()]);
  }
  
  public void addUserToGroup(SecurityUser user, SecurityGroup group)
  {
    group = (SecurityGroup)loadWithLazy(new Long(group.getId()), new String[] { "users" });
    group.getUsers().add(user);
    update(group);
  }
  
  public void removeUserFromGroup(SecurityUser user, SecurityGroup group)
  {
    group = (SecurityGroup)loadWithLazy(new Long(group.getId()), new String[] { "users" });
    group.getUsers().add(user);
    remove(group);
  }
  
  public SecurityGroup loadByCodeWithoutRemoved(String code)
  {
    String hql = "from SecurityGroup grp where grp.code=? and grp.removed=0";
    List res = findByHQL(hql, code);
    return (res != null) && (res.size() > 0) ? (SecurityGroup)res.get(0) : null;
  }
  
  public SecurityGroup loadByCode(String code)
  {
    String hql = "from SecurityGroup grp where grp.code=? ";
    List res = findByHQL(hql, code);
    return (res != null) && (res.size() > 0) ? (SecurityGroup)res.get(0) : null;
  }
  
  public SecurityGroup[] getAllDynaGroups()
  {
    String hql = "from SecurityGroup grp where (grp.dyna=? or grp.dyna=2) and grp.removed=0";
    List results = findByHQL(hql, new Integer(1));
    return (SecurityGroup[])results.toArray(new SecurityGroup[results.size()]);
  }
  
  public Page getAllDynaGroupsByPage(Map filter, int pageNo, int pageSize)
  {
    String hql = "from SecurityGroup grp where (grp.dyna=1 or grp.dyna=2) and grp.removed=0";
    String countHql = "select count(*) from SecurityGroup grp where (grp.dyna=1 or grp.dyna=2) and grp.removed=0";
    List args = new ArrayList();
    if ((filter != null) && (filter.size() > 0))
    {
      Iterator itr = filter.keySet().iterator();
      String obj = null;
      while (itr.hasNext())
      {
        obj = (String)itr.next();
        hql = hql + " and grp." + obj + "=:" + obj + " ";
        args.add(new HqlParameter(obj, filter.get(obj)));
        countHql = countHql + " and grp." + obj + "=:" + obj + " ";
      }
    }
    return findByHQLWithPage(hql, args, pageNo, pageSize, countHql);
  }
  
  public SecurityGroup[] getAllAuthControlGroup()
  {
    String hql = "from SecurityGroup grp where (grp.dyna=? or grp.dyna=0) and grp.removed=0";
    List results = findByHQL(hql, new Integer(1));
    return (SecurityGroup[])results.toArray(new SecurityGroup[results.size()]);
  }
  
  public SecurityGroup[] getAllAuthControlDynaGroups()
  {
    String hql = "from SecurityGroup grp where grp.dyna=? and grp.removed=0";
    List results = findByHQL(hql, new Integer(1));
    return (SecurityGroup[])results.toArray(new SecurityGroup[results.size()]);
  }
  
  public Page findByLikeNameWithPage(Map filter, Map sortMap, int pageNo, int pageSize)
  {
    StringBuffer qryHql = new StringBuffer();
    List args = new ArrayList();
    if (filter == null) {
      filter = new HashMap();
    }
    qryHql.append("from SecurityGroup group where group.removed=0 ");
    String nameValue = (String)filter.get("name");
    if ((nameValue == null) || (nameValue.equalsIgnoreCase(""))) {
      nameValue = "";
    }
    qryHql.append(" and  group.name like :name ");
    args.add(new HqlParameter("name", "%" + nameValue + "%"));
    if (filter.get("dyna") != null) {
      if (filter.get("dyna").toString().equals("0")) {
        qryHql.append(" and group.dyna=" + filter.get("dyna"));
      } else {
        qryHql.append(" and group.dyna in(1,2)");
      }
    }
    return findByHQLWithPage(qryHql.toString(), args, pageNo, pageSize, null);
  }
}
