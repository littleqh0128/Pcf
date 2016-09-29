package com.wondersgroup.framework.menu.dao;

import com.wondersgroup.framework.core.bo.Page;
import com.wondersgroup.framework.core.dao.AbstractHibernateExtdDAO;
import com.wondersgroup.framework.menu.bo.ProxyInfo;
import com.wondersgroup.framework.security.bo.SecurityUser;
import java.util.List;
import java.util.Set;

public abstract interface ProxyInfoExtdDAO extends AbstractHibernateExtdDAO
{
  public abstract List getProxysByUser(SecurityUser paramSecurityUser);
  
  public abstract Page getProxysByUser(SecurityUser paramSecurityUser, int paramInt1, int paramInt2);
  
  public abstract List getUsersByProxy(SecurityUser paramSecurityUser);
  
  public abstract ProxyInfo getProxyInfoByUserAndProxy(SecurityUser paramSecurityUser1, SecurityUser paramSecurityUser2);
  
  public abstract List getProxyInfosByUserAndProxy(SecurityUser paramSecurityUser1, SecurityUser paramSecurityUser2);
  
  public abstract Set getProxyMenusByProxyInfo(ProxyInfo paramProxyInfo);
}
