package com.wondersgroup.framework.menu.dao;

import com.wondersgroup.framework.core.bo.Page;
import com.wondersgroup.framework.core.dao.AbstractHibernateExtdDAO;
import com.wondersgroup.framework.menu.bo.MenuResource;
import com.wondersgroup.framework.security.bo.ACLOperation;
import com.wondersgroup.framework.security.bo.ACLResource;
import java.util.List;

public abstract interface MenuResourceExtdDAO extends AbstractHibernateExtdDAO
{
  public abstract List getAllMenuResources();
  
  public abstract List getChildMenuResourcesByParentMenu(MenuResource paramMenuResource);
  
  public abstract int getMenuResourceCount();
  
  public abstract boolean isExistChildrenMenuResourceByParentMenu(MenuResource paramMenuResource);
  
  public abstract List getTopMenuResources();
  
  public abstract void delMenuResourceById(MenuResource paramMenuResource);
  
  public abstract MenuResource getTopMenuInfoByName(String paramString);
  
  public abstract Page getUsersByPageAndACLMenu(ACLResource paramACLResource, ACLOperation paramACLOperation, int paramInt1, int paramInt2);
  
  public abstract Page getUsersByPageAndREVMenu(ACLResource paramACLResource, ACLOperation paramACLOperation, int paramInt1, int paramInt2);
  
  public abstract Page getGroupsByPageAndACLMenu(ACLResource paramACLResource, ACLOperation paramACLOperation, int paramInt1, int paramInt2);
  
  public abstract Page getGroupsByPageAndREVMenu(ACLResource paramACLResource, ACLOperation paramACLOperation, int paramInt1, int paramInt2);
  
  public abstract List getGroupsByREVMenu(ACLResource paramACLResource, ACLOperation paramACLOperation);
  
  public abstract List getUsersByREVMenu(ACLResource paramACLResource, ACLOperation paramACLOperation);
}
