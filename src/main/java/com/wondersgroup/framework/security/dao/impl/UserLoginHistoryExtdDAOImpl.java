package com.wondersgroup.framework.security.dao.impl;

import com.wondersgroup.framework.core.bo.Page;
import com.wondersgroup.framework.core.dao.impl.AbstractHibernateExtdDAOImpl;
import com.wondersgroup.framework.security.dao.UserLoginHistoryExtdDAO;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public class UserLoginHistoryExtdDAOImpl extends AbstractHibernateExtdDAOImpl implements UserLoginHistoryExtdDAO
{
  public List findUserLogins(Map filter)
  {
    StringBuffer hql = new StringBuffer();
    hql.append("select loginHistory from  UserLoginHistory loginHistory where 1=1 ");
    for (Iterator i = filter.keySet().iterator(); i.hasNext();)
    {
      String key = (String)i.next();
      if ((filter.get(key) != null) && (!filter.get(key).equals(""))) {
        if (key.equals("loginName")) {
          hql.append(" and loginHistory.loginName like '%" + filter.get(key) + "%' ");
        } else if (key.equals("userName")) {
          hql.append(" and loginHistory.userName like '%" + filter.get(key) + "%' ");
        } else if (key.equals("userState")) {
          hql.append(" and loginHistory.userState = " + filter.get(key));
        } else if (key.equals("startTime")) {
          hql.append(" and loginHistory.loginTime >= " + filter.get(key).toString());
        } else if (key.equals("endTime")) {
          hql.append(" and loginHistory.loginTime <= " + (Long.parseLong(filter.get(key).toString()) + 86400000L));
        } else if (key.equals("userId")) {
          hql.append(" and loginHistory.userId = " + filter.get(key));
        } else if (key.equals("appId")) {
          hql.append(" and loginHistory.appId = " + filter.get(key));
        } else if (key.equals("onlineTime")) {
          hql.append(" and loginHistory.appId like '" + filter.get(key) + "%' ");
        } else {
          hql.append(" and loginHistory." + key + " = '" + filter.get(key) + "'");
        }
      }
    }
    hql.append(" order by loginHistory.loginTime desc");
    return findByHQL(hql.toString(), null);
  }
  
  public Page findUserLoginsByPage(Map filter, int pageNo, int pageSize)
  {
    StringBuffer hql = new StringBuffer();
    hql.append("select loginHistory from  UserLoginHistory loginHistory where 1=1 ");
    for (Iterator i = filter.keySet().iterator(); i.hasNext();)
    {
      String key = (String)i.next();
      if ((filter.get(key) != null) && (!filter.get(key).equals(""))) {
        if (key.equals("loginName")) {
          hql.append(" and loginHistory.loginName like '%" + filter.get(key) + "%' ");
        } else if (key.equals("userName")) {
          hql.append(" and loginHistory.userName like '%" + filter.get(key) + "%' ");
        } else if (key.equals("userState")) {
          hql.append(" and loginHistory.userState = " + filter.get(key));
        } else if (key.equals("startTime")) {
          hql.append(" and loginHistory.loginTime >= " + filter.get(key).toString());
        } else if (key.equals("endTime")) {
          hql.append(" and loginHistory.loginTime <= " + (Long.parseLong(filter.get(key).toString()) + 86400000L));
        } else if (key.equals("userId")) {
          hql.append(" and loginHistory.userId = " + filter.get(key));
        } else if (key.equals("appId")) {
          hql.append(" and loginHistory.appId = " + filter.get(key));
        } else if (key.equals("onlineTime")) {
          hql.append(" and loginHistory.appId like '" + filter.get(key) + "%' ");
        } else {
          hql.append(" and loginHistory." + key + " = '" + filter.get(key) + "'");
        }
      }
    }
    hql.append(" order by loginHistory.loginTime desc");
    return findByHQLWithPage(hql.toString(), pageNo, pageSize, null);
  }
}
