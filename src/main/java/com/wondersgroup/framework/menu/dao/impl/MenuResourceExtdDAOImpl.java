package com.wondersgroup.framework.menu.dao.impl;

import com.wondersgroup.framework.core.bo.Page;
import com.wondersgroup.framework.core.bo.hibernate.HqlParameter;
import com.wondersgroup.framework.core.dao.impl.AbstractHibernateExtdDAOImpl;
import com.wondersgroup.framework.menu.bo.MenuResource;
import com.wondersgroup.framework.menu.dao.MenuResourceExtdDAO;
import com.wondersgroup.framework.security.bo.ACLOperation;
import com.wondersgroup.framework.security.bo.ACLResource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class MenuResourceExtdDAOImpl extends AbstractHibernateExtdDAOImpl implements MenuResourceExtdDAO
{
  protected Class getEntityClass()
  {
    return MenuResource.class;
  }
  
  public List getAllMenuResources()
  {
    return getHibernateTemplate().find("from MenuResource menu where menu.valid = 1 order by menu.parentResource, menu.displayOrder");
  }
  
  public List getChildMenuResourcesByParentMenu(MenuResource parentMenuResource)
  {
    return getHibernateTemplate().find("from MenuResource menu left join fetch menu.host left join fetch menu.menuType where menu.parentResource = ? and menu.valid = 1 order by menu.displayOrder", new Object[] { parentMenuResource });
  }
  
  public int getMenuResourceCount()
  {
    return super.countByHQL("select count(*) from MenuResource");
  }
  
  public boolean isExistChildrenMenuResourceByParentMenu(MenuResource menuResource)
  {
    boolean isExisted = false;
    List result = getHibernateTemplate().find("select count(*) from MenuResource menu where menu.parentResource = ?", new Object[] { menuResource });
    if (result.size() > 0) {
      isExisted = true;
    }
    return isExisted;
  }
  
  public List getTopMenuResources()
  {
    return getHibernateTemplate().find("from MenuResource menu where menu.parentResource is null order by menu.displayOrder");
  }
  
  public void delMenuResourceById(MenuResource menuResource)
  {
    delete(menuResource);
  }
  
  public MenuResource getTopMenuInfoByName(String resourceName)
  {
    List res = getHibernateTemplate().find("from MenuResource menu where menu.parentResource is null and menu.resourceName=?", resourceName);
    
    return (res != null) && (res.size() > 0) ? (MenuResource)res.get(0) : null;
  }
  
  public Page getUsersByPageAndACLMenu(ACLResource aclResource, ACLOperation aclOperation, int pageNo, int pageSize)
  {
    Date dueTime = new Date(System.currentTimeMillis());
    List args = new ArrayList();
    args.add(new HqlParameter("aclResource", aclResource));
    args.add(new HqlParameter("aclOperation", aclOperation));
    args.add(new HqlParameter("dueTime", dueTime));
    StringBuffer hql = new StringBuffer();
    hql.append("select distinct user0 from SecurityUser user0 inner join user0.roles role1").append("  where user0.removed=0 and role1 in (select distinct role from ACLPermission permission inner join permission.roles role where ").append("  permission.aclResource = :aclResource and permission.aclOperation = :aclOperation and (permission.dueTime is null or permission.dueTime > :dueTime))").append("or user0 in (select user1 from SecurityUser user1 inner join user1.groups group1 where group1 in").append("(select distinct group0 from SecurityGroup group0 inner join group0.roles role1").append("  where role1 in (select distinct role from ACLPermission permission inner join permission.roles role where ").append("  permission.aclResource = :aclResource and permission.aclOperation = :aclOperation and (permission.dueTime is null or permission.dueTime > :dueTime)))) ");
    
    return findByHQLWithPage(hql.toString(), args, pageNo, pageSize, null);
  }
  
  public Page getUsersByPageAndREVMenu(ACLResource aclResource, ACLOperation aclOperation, int pageNo, int pageSize)
  {
    Date dueTime = new Date(System.currentTimeMillis());
    List args = new ArrayList();
    args.add(new HqlParameter("aclResource", aclResource));
    args.add(new HqlParameter("aclOperation", aclOperation));
    args.add(new HqlParameter("dueTime", dueTime));
    StringBuffer hql = new StringBuffer();
    
    hql.append("select distinct user0 from SecurityUser user0 inner join user0.roles role1").append(" where user0.removed=0 and role1 in (select distinct role from ACLPermission permission inner join permission.revokeRoles role where ").append(" permission.aclResource = :aclResource and permission.aclOperation = :aclOperation and (permission.dueTime is null or permission.dueTime > :dueTime))").append(" or user0 in (select user1 from SecurityUser user1 inner join user1.groups group1 where group1 in").append("(select distinct group0 from SecurityGroup group0 inner join group0.roles role1").append(" where role1 in (select distinct role from ACLPermission permission inner join permission.revokeRoles role where ").append("  permission.aclResource = :aclResource and permission.aclOperation = :aclOperation and (permission.dueTime is null or permission.dueTime > :dueTime)))) ");
    
    return findByHQLWithPage(hql.toString(), args, pageNo, pageSize, null);
  }
  
  public Page getGroupsByPageAndACLMenu(ACLResource aclResource, ACLOperation aclOperation, int pageNo, int pageSize)
  {
    Date dueTime = new Date(System.currentTimeMillis());
    List args = new ArrayList();
    args.add(new HqlParameter("aclResource", aclResource));
    args.add(new HqlParameter("aclOperation", aclOperation));
    args.add(new HqlParameter("dueTime", dueTime));
    StringBuffer hql = new StringBuffer();
    hql.append("select distinct group0 from SecurityGroup group0 inner join group0.roles role1").append("  where role1 in (select distinct role from ACLPermission permission inner join permission.roles role where ").append("  permission.aclResource = :aclResource and permission.aclOperation = :aclOperation and (permission.dueTime is null or permission.dueTime > :dueTime)) ");
    
    return findByHQLWithPage(hql.toString(), args, pageNo, pageSize, null);
  }
  
  public Page getGroupsByPageAndREVMenu(ACLResource aclResource, ACLOperation aclOperation, int pageNo, int pageSize)
  {
    Date dueTime = new Date(System.currentTimeMillis());
    List args = new ArrayList();
    args.add(new HqlParameter("aclResource", aclResource));
    args.add(new HqlParameter("aclOperation", aclOperation));
    args.add(new HqlParameter("dueTime", dueTime));
    StringBuffer hql = new StringBuffer();
    hql.append("select distinct group0 from SecurityGroup group0 inner join group0.roles role1").append(" where role1 in (select distinct role from ACLPermission permission inner join permission.revokeRoles role where ").append(" permission.aclResource = :aclResource and permission.aclOperation = :aclOperation and (permission.dueTime is null or permission.dueTime > :dueTime)) ");
    
    return findByHQLWithPage(hql.toString(), args, pageNo, pageSize, null);
  }
  
  public List getGroupsByREVMenu(ACLResource aclResource, ACLOperation aclOperation)
  {
    Date dueTime = new Date(System.currentTimeMillis());
    List args = new ArrayList();
    args.add(new HqlParameter("aclResource", aclResource));
    args.add(new HqlParameter("aclOperation", aclOperation));
    args.add(new HqlParameter("dueTime", dueTime));
    StringBuffer hql = new StringBuffer();
    hql.append("select distinct group0 from SecurityGroup group0 inner join group0.roles role1").append(" where role1 in (select distinct role from ACLPermission permission inner join permission.revokeRoles role where ").append(" permission.aclResource = :aclResource and permission.aclOperation = :aclOperation and (permission.dueTime is null or permission.dueTime > :dueTime)) ");
    
    return findByHQL(hql.toString(), args);
  }
  
  public List getUsersByREVMenu(ACLResource aclResource, ACLOperation aclOperation)
  {
    Date dueTime = new Date(System.currentTimeMillis());
    List args = new ArrayList();
    args.add(new HqlParameter("aclResource", aclResource));
    args.add(new HqlParameter("aclOperation", aclOperation));
    args.add(new HqlParameter("dueTime", dueTime));
    StringBuffer hql = new StringBuffer();
    
    hql.append("select distinct user0 from SecurityUser user0 inner join user0.roles role1").append(" where role1 in (select distinct role from ACLPermission permission inner join permission.revokeRoles role where ").append(" permission.aclResource = :aclResource and permission.aclOperation = :aclOperation and (permission.dueTime is null or permission.dueTime > :dueTime))").append(" or user0 in (select user1 from SecurityUser user1 inner join user1.groups group1 where group1 in").append("(select distinct group0 from SecurityGroup group0 inner join group0.roles role1").append(" where role1 in (select distinct role from ACLPermission permission inner join permission.revokeRoles role where ").append("  permission.aclResource = :aclResource and permission.aclOperation = :aclOperation and (permission.dueTime is null or permission.dueTime > :dueTime)))) ");
    
    return findByHQL(hql.toString(), args);
  }
}
