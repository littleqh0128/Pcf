package com.wondersgroup.framework.security.dao.impl;

import com.wondersgroup.framework.core.dao.impl.AbstractHibernateExtdDAOImpl;
import com.wondersgroup.framework.organization.bo.OrganTree;
import com.wondersgroup.framework.organization.dao.OrganModelExtdDao;
import com.wondersgroup.framework.security.bo.ACLOperation;
import com.wondersgroup.framework.security.bo.ACLResource;
import com.wondersgroup.framework.security.bo.SecurityGroup;
import com.wondersgroup.framework.security.bo.SecurityRole;
import com.wondersgroup.framework.security.bo.SecurityUser;
import com.wondersgroup.framework.security.dao.RoleExtdDAO;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RoleExtdDAOImpl extends AbstractHibernateExtdDAOImpl implements RoleExtdDAO
{
  @Autowired
  public OrganModelExtdDao organModelDao;
  
  protected Class getEntityClass()
  {
    return SecurityRole.class;
  }
  
  public SecurityRole loadDefaultRoleWithUser(SecurityUser user)
  {
    return (SecurityRole)findUniqueBy("name", "USER_" + user.getLoginName() + "_" + user.getId());
  }
  
  public SecurityRole loadDefaultRoleWithGroup(SecurityGroup group)
  {
    return (SecurityRole)findUniqueBy("name", "GROUP_" + group.getName());
  }
  
  public List findSecurityRoleWithResourceAndOperation(ACLResource aclResource, ACLOperation aclOperation)
  {
    Date dueTime = new Date(System.currentTimeMillis());
    Date startTime = new Date(System.currentTimeMillis());
    List result = getHibernateTemplate().find("select distinct permission.roles from ACLPermission permission where  permission.aclResource = ? and permission.aclOperation = ? and permission.dueTime is null ", new Object[] { aclResource, aclOperation });
    
    List result2 = getHibernateTemplate().find("select distinct permission.roles from ACLPermission permission where  permission.aclResource = ? and permission.aclOperation = ? and permission.startTime is null and permission.startTime <= ? and permission.dueTime is not null and permission.dueTime > ? ", new Object[] { aclResource, aclOperation, startTime, dueTime });
    
    result.addAll(result2);
    return new ArrayList(new HashSet(result));
  }
  
  public List findRevokeSecurityRoleWithResourceAndOperation(ACLResource aclResource, ACLOperation aclOperation)
  {
    List result = getHibernateTemplate().find("select distinct permission.revokeRoles from ACLPermission permission where  permission.aclResource = ? and permission.aclOperation = ? and permission.dueTime is null ", new Object[] { aclResource, aclOperation });
    
    return new ArrayList(new HashSet(result));
  }
  
  public List findPermissionWithResourceAndOperation(ACLResource aclResource, ACLOperation aclOperation)
  {
    List permissions = getHibernateTemplate().find("from ACLPermission permission  where permission.aclResource = ? and permission.aclOperation = ? and permission.dueTime is null ", new Object[] { aclResource, aclOperation });
    
    return permissions;
  }
  
  public List findPermission(List resourceIds, String resourceTypeId, String operationId)
  {
    Date time = new Date(System.currentTimeMillis());
    List result = new ArrayList();
    String hql = "select distinct resource.nativeResourceId, permission from ACLPermission permission join permission.aclResource resource where resource.nativeResourceId in (:resourceIds) and  resource.aclResourceType.id = :resourceTypeId and permission.aclOperation.id = :operationId  and ( permission.startTime is null or (permission.startTime is not null and permission.startTime <= :time)) and ( permission.dueTime is null or (permission.dueTime is not null and permission.dueTime > :time))";
    
    Query query = getSession().createQuery(hql);
    int start_index = 0;
    int step_index = 0;
    int step = 900;
    while ((resourceIds != null) && (resourceIds.size() > 0) && (start_index < resourceIds.size()))
    {
      List tmpResourceIds = new ArrayList();
      step_index = start_index;
      for (int i = 0; (step_index < resourceIds.size()) && (i < step); step_index++)
      {
        Object obj = resourceIds.get(step_index);
        start_index++;
        if (!tmpResourceIds.contains(obj)) {
          tmpResourceIds.add(obj);
        }
        i++;
      }
      query.setParameterList("resourceIds", tmpResourceIds);
      query.setInteger("resourceTypeId", Integer.parseInt(resourceTypeId));
      query.setInteger("operationId", Integer.parseInt(operationId));
      query.setDate("time", time);
      List tmpResult = query.list();
      if ((tmpResult != null) && (!tmpResult.isEmpty())) {
        for (int j = 0; j < tmpResult.size(); j++) {
          if (!result.contains(tmpResult.get(j))) {
            result.add(tmpResult.get(j));
          }
        }
      }
    }
    return result;
  }
  
  public Map findRevokeRoleWithResourceAndOperation(List resourceIds, String resourceTypeId, String operationId)
  {
    return null;
  }
  
  public SecurityRole loadDefaultRoleWithNewGroup(SecurityGroup group)
  {
    List result = getHibernateTemplate().find("select role from SecurityGroup s_group join s_group.roles role where s_group.id = ? and role.type = 'D'", new Object[] { new Long(group.getId()) });
    
    return (SecurityRole)result.get(0);
  }
  
  public SecurityRole loadDefaultRoleWithNewUser(SecurityUser user)
  {
    List result = getHibernateTemplate().find("select role from SecurityUser user join user.roles role where user.id = ? and role.type = 'D'", new Object[] { new Long(user.getId()) });
    
    return (SecurityRole)result.get(0);
  }
  
  public List loadAllRoleFromUser(SecurityUser user)
  {
    List userRoles = getHibernateTemplate().find("select role from SecurityUser user join user.roles role where user = ? ", new Object[] { user });
    
    List groupRoles = getHibernateTemplate().find("select role from SecurityGroup grp join grp.users user join grp.roles role where user = ? ", new Object[] { user });
    
    userRoles.addAll(groupRoles);
    
    List organRoles = getHibernateTemplate().find("select distinct role from OrganModel model join model.roles role join model.organNode node join node.users user where user = ? and model.removed = 0", new Object[] { user });
    
    userRoles.addAll(organRoles);
    
    return new ArrayList(new HashSet(userRoles));
  }
  
  public List loadACLPermissionFromRole(SecurityRole role)
  {
    return getHibernateTemplate().find("select aclPermission from SecurityRole role join role.aclPermissions aclPermission where role = ? ", new Object[] { role });
  }
  
  public List loadRevokeACLPermissionFromRole(SecurityRole role)
  {
    return getHibernateTemplate().find("select aclPermission from SecurityRole role join role.revokeAclPermissions aclPermission where role = ? ", new Object[] { role });
  }
  
  public List loadAllRolesFromUser(SecurityUser user, OrganTree tree)
  {
    List userRoles = getHibernateTemplate().find("select role from SecurityUser user join user.roles role where user = ? ", new Object[] { user });
    
    List groupRoles = getHibernateTemplate().find("select role from SecurityGroup grp join grp.users user join grp.roles role where user = ? ", new Object[] { user });
    
    userRoles.addAll(groupRoles);
    
    String hql = "select distinct role from OrganModel model join model.roles role join model.organNode node join model.organTree organTree join node.users user where user = :user and organTree = :organTree and model.removed = 0";
    
    Query query = super.getSession().createQuery(hql);
    query.setParameter("user", user);
    query.setParameter("organTree", tree);
    List organRoles = query.list();
    userRoles.addAll(organRoles);
    
    return new ArrayList(new HashSet(userRoles));
  }
  
  public List loadAdminRolesFromUser(SecurityUser user, OrganTree tree)
  {
    String hql = "select distinct role from OrganModel model join model.roles role join model.organTree organTree join model.organNode node where node.adminUser = :adminUser and organTree = :organTree and model.removed = 0";
    
    Query query = super.getSession().createQuery(hql);
    query.setParameter("adminUser", user);
    query.setParameter("organTree", tree);
    List organRoles = query.list();
    
    return new ArrayList(new HashSet(organRoles));
  }
  
  public List findPermission(String resourceTypeId, String operationId, SecurityRole[] roles)
  {
    Date time = new Date(System.currentTimeMillis());
    StringBuffer hql = new StringBuffer("select distinct resource.nativeResourceId from ACLPermission permission join permission.roles role join permission.aclResource resource").append(" where resource.aclResourceType.id = :resourceTypeId and permission.aclOperation.id = :operationId").append(" and ( permission.startTime is null or (permission.startTime is not null and permission.startTime <= :time))").append(" and ( permission.dueTime is null or (permission.dueTime is not null and permission.dueTime > :time))");
    if (roles != null) {
      for (int i = 0; i < roles.length; i++)
      {
        if (i == 0) {
          hql.append(" and (");
        }
        hql.append(" role.id = ").append(roles[i].getId());
        if (i < roles.length - 1) {
          hql.append(" or ");
        } else {
          hql.append(")");
        }
      }
    }
    Query query = getSession().createQuery(hql.toString());
    query.setInteger("resourceTypeId", Integer.parseInt(resourceTypeId));
    query.setInteger("operationId", Integer.parseInt(operationId));
    query.setDate("time", time);
    return query.list();
  }
  
  public List findRevokePermission(String resourceTypeId, String operationId, SecurityRole[] roles)
  {
    Date time = new Date(System.currentTimeMillis());
    StringBuffer hql = new StringBuffer("select distinct resource.nativeResourceId from ACLPermission permission join permission.revokeRoles role join permission.aclResource resource").append(" where resource.aclResourceType.id = :resourceTypeId and permission.aclOperation.id = :operationId").append(" and ( permission.startTime is null or (permission.startTime is not null and permission.startTime <= :time))").append(" and ( permission.dueTime is null or (permission.dueTime is not null and permission.dueTime > :time))");
    if (roles != null) {
      for (int i = 0; i < roles.length; i++)
      {
        if (i == 0) {
          hql.append(" and (");
        }
        hql.append(" role.id = ").append(roles[i].getId());
        if (i < roles.length - 1) {
          hql.append(" or ");
        } else {
          hql.append(")");
        }
      }
    }
    Query query = getSession().createQuery(hql.toString());
    query.setInteger("resourceTypeId", Integer.parseInt(resourceTypeId));
    query.setInteger("operationId", Integer.parseInt(operationId));
    query.setDate("time", time);
    return query.list();
  }
}
