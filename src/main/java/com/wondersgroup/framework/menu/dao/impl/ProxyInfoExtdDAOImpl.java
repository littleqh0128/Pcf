package com.wondersgroup.framework.menu.dao.impl;

import com.wondersgroup.framework.core.bo.Page;
import com.wondersgroup.framework.core.bo.hibernate.HqlParameter;
import com.wondersgroup.framework.core.dao.impl.AbstractHibernateExtdDAOImpl;
import com.wondersgroup.framework.menu.bo.ProxyInfo;
import com.wondersgroup.framework.menu.dao.ProxyInfoExtdDAO;
import com.wondersgroup.framework.security.bo.SecurityUser;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

@Repository
public class ProxyInfoExtdDAOImpl extends AbstractHibernateExtdDAOImpl implements ProxyInfoExtdDAO
{
  public List getProxysByUser(SecurityUser user)
  {
    String query = "select proxyInfo.proxy from ProxyInfo proxyInfo where proxyInfo.user = ? and proxyInfo.proxy.removed = 0";
    List result = super.getHibernateTemplate().find(query, new Object[] { user });
    
    return result;
  }
  
  public Page getProxysByUser(SecurityUser user, int pageNo, int pageSize)
  {
    String query = "select proxyInfo.proxy from ProxyInfo proxyInfo where proxyInfo.user = :user and proxyInfo.proxy.removed = 0";
    List args = new ArrayList();
    args.add(new HqlParameter("user", user));
    return findByHQLWithPage(query, args, pageNo, pageSize, null);
  }
  
  public ProxyInfo getProxyInfoByUserAndProxy(SecurityUser user, SecurityUser proxy)
  {
    String query = "from ProxyInfo proxyInfo left join fetch proxyInfo.proxyMenus m where proxyInfo.user = ? and proxyInfo.proxy = ?";
    List result = getHibernateTemplate().find(query, new Object[] { user, proxy });
    if (result.size() <= 0) {
      return null;
    }
    return (ProxyInfo)result.get(0);
  }
  
  public List getUsersByProxy(SecurityUser proxy)
  {
    String query = "select proxyInfo.user from ProxyInfo proxyInfo where proxyInfo.proxy = ? ";
    return findByHQL(query, proxy);
  }
  
  public List getProxyInfosByUserAndProxy(SecurityUser user, SecurityUser proxy)
  {
    String query = "from ProxyInfo proxyInfo where proxyInfo.user = ? and proxyInfo.proxy = ?";
    List result = getHibernateTemplate().find(query, new Object[] { user, proxy });
    return result;
  }
  
  public Set getProxyMenusByProxyInfo(ProxyInfo proxyInfo)
  {
    ProxyInfo result = (ProxyInfo)super.loadWithLazy(new Long(proxyInfo.getId()), new String[] { "proxyMenus" });
    
    return result.getProxyMenus();
  }
}
