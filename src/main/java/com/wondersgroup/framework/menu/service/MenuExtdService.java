package com.wondersgroup.framework.menu.service;

import com.wondersgroup.framework.core.bo.Page;
import com.wondersgroup.framework.menu.bo.MenuResource;
import com.wondersgroup.framework.menu.bo.ProxyInfo;
import com.wondersgroup.framework.resource.connector.service.ResourceExtdService;
import com.wondersgroup.framework.security.bo.SecurityUser;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract interface MenuExtdService extends ResourceExtdService
{
  public abstract Map getParentChildAssort();
  
  public abstract Map getAuthParentChildAssort(String paramString);
  
  public abstract boolean isChildExist(MenuResource paramMenuResource);
  
  public abstract MenuResource[] getChildMenuResourceOrderlyByParentMenu(MenuResource paramMenuResource);
  
  public abstract MenuResource getMenuResourceById(long paramLong);
  
  public abstract MenuResource getMenuResourceByCode(String paramString);
  
  public abstract MenuResource[] getAllMenuResources();
  
  public abstract void createMenuResource(MenuResource paramMenuResource);
  
  public abstract void updateMenuResource(MenuResource paramMenuResource);
  
  public abstract void delMenuResourceById(long paramLong);
  
  public abstract int getMenuResourceCount();
  
  public abstract Map getMenuResourcesByAuth(String paramString);
  
  public abstract MenuResource[] getAuthMenusByParentMenu(MenuResource paramMenuResource, String paramString);
  
  public abstract MenuResource[] getAuthMenusByAppNode(Long paramLong, String paramString);
  
  public abstract MenuResource[] getTopMenuResources();
  
  public abstract MenuResource getMenuInfoById(long paramLong);
  
  public abstract void accreditPermission(String paramString, MenuResource paramMenuResource);
  
  public abstract void createMenuInfoWithPermission(String paramString, MenuResource paramMenuResource);
  
  @Deprecated
  public abstract MenuResource getTopMenuInfoByName(String paramString);
  
  public abstract MenuResource loadMenuResourceWithLazy(long paramLong, String[] paramArrayOfString);
  
  public abstract Map getAuthMenus(String paramString1, String paramString2);
  
  public abstract Collection getAuthUsersByMenuResource(MenuResource paramMenuResource, String paramString);
  
  public abstract void addProxyToUser(ProxyInfo paramProxyInfo);
  
  public abstract void updateProxyInfo(ProxyInfo paramProxyInfo);
  
  public abstract ProxyInfo[] getProxyInfosByUserAndProxy(SecurityUser paramSecurityUser1, SecurityUser paramSecurityUser2);
  
  public abstract Set getProxyMenusByProxyInfo(ProxyInfo paramProxyInfo);
  
  public abstract SecurityUser[] getProxysByUser(SecurityUser paramSecurityUser);
  
  public abstract Page getProxysByUser(SecurityUser paramSecurityUser, int paramInt1, int paramInt2);
  
  public abstract SecurityUser[] getUsersByProxy(SecurityUser paramSecurityUser, boolean paramBoolean);
  
  public abstract Page getUsersByProxy(SecurityUser paramSecurityUser, boolean paramBoolean, int paramInt1, int paramInt2);
  
  public abstract ProxyInfo loadProxyInfoById(long paramLong);
  
  public abstract void removeProxyInfoById(long paramLong);
  
  public abstract MenuResource getParentResourceById(long paramLong);
  
  public abstract Page getUsersByPageAndACLMenu(String paramString1, long paramLong, String paramString2, int paramInt1, int paramInt2);
  
  public abstract Page getUsersByPageAndREVMenu(String paramString1, long paramLong, String paramString2, int paramInt1, int paramInt2);
  
  public abstract List getGroupsByAuthMenu(String paramString1, long paramLong, String paramString2);
  
  public abstract Page getGroupsByPageAndACLMenu(String paramString1, long paramLong, String paramString2, int paramInt1, int paramInt2);
  
  public abstract Page getGroupsByPageAndREVMenu(String paramString1, long paramLong, String paramString2, int paramInt1, int paramInt2);
  
  public abstract List getGroupsByREVMenu(String paramString1, long paramLong, String paramString2);
  
  public abstract List getUsersByREVMenu(String paramString1, long paramLong, String paramString2);
}
