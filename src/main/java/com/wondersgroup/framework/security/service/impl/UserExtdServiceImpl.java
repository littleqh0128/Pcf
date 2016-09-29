package com.wondersgroup.framework.security.service.impl;

import com.wondersgroup.framework.config.SystemParameter;
import com.wondersgroup.framework.core.bo.Page;
import com.wondersgroup.framework.core.bo.hibernate.HqlParameter;
import com.wondersgroup.framework.core.exception.DAOException;
import com.wondersgroup.framework.core.extendproperty.service.ExtendPropertyConfigExtdService;
import com.wondersgroup.framework.ldap.LDAPFactory;
import com.wondersgroup.framework.ldap.exception.LDAPException;
import com.wondersgroup.framework.ldap.model.LDAPUser;
import com.wondersgroup.framework.organization.bo.OrganNode;
import com.wondersgroup.framework.organization.constants.OrganConstants;
import com.wondersgroup.framework.organization.dao.OrganNodeExtdDao;
import com.wondersgroup.framework.resource.bo.AppNode;
import com.wondersgroup.framework.resource.dao.AppNodeExtdDAO;
import com.wondersgroup.framework.security.authentication.connector.AuthenticationUser;
import com.wondersgroup.framework.security.bo.SecurityGroup;
import com.wondersgroup.framework.security.bo.SecurityRole;
import com.wondersgroup.framework.security.bo.SecurityUser;
import com.wondersgroup.framework.security.bo.UserExtendProperty;
import com.wondersgroup.framework.security.bo.UserLoginHistory;
import com.wondersgroup.framework.security.dao.GroupExtdDAO;
import com.wondersgroup.framework.security.dao.RoleExtdDAO;
import com.wondersgroup.framework.security.dao.UserExtdDAO;
import com.wondersgroup.framework.security.dao.UserExtendPropertyExtdDao;
import com.wondersgroup.framework.security.dao.UserLoginHistoryExtdDAO;
import com.wondersgroup.framework.security.dto.UserDTOExtd;
import com.wondersgroup.framework.security.encoder.PasswordEncoderUtils;
import com.wondersgroup.framework.security.exception.BadCredentialsException;
import com.wondersgroup.framework.security.exception.SecurityException;
import com.wondersgroup.framework.security.service.AuthenticationExtdService;
import com.wondersgroup.framework.security.service.UserExtdService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service("UserExtService")
@Transactional
public class UserExtdServiceImpl implements UserExtdService
{
  public static final int MILLIS_OF_DAY = 86400000;
  public static final int MILLIS_OF_HOUR = 3600000;
  public static final String CA_ERRORLOGIN_LOCKTIME = "ca_errorlogin_locktime";
  public static final float DEFAULT_CA_ERRORLOGIN_LOCKTIME = 1.0F;
  public static final String STRING_EMPTY = "";
  @Autowired
  private UserExtdDAO userDAO;
  @Autowired
  private GroupExtdDAO groupDAO;
  @Autowired
  private RoleExtdDAO roleDAO;
  @Autowired
  private OrganNodeExtdDao organNodeDao;
  @Autowired
  private AppNodeExtdDAO appNodeDAO;
  @Autowired
  private UserExtendPropertyExtdDao userExtendPropertyDao;
  @Autowired
  private AuthenticationExtdService authenticationService;
  @Autowired
  private ExtendPropertyConfigExtdService extendPropertyConfigService;
  @Autowired
  private UserLoginHistoryExtdDAO userLoginHistoryDAO;

  public void createNewUser(SecurityUser user)
  {
    if (user == null) {
      throw new SecurityException("User Principal not existed.");
    }
    String loginName = user.getLoginName();
    SecurityUser securityUser = getUserByLoginName(loginName);
    if ((securityUser != null) && (!securityUser.isEntityRemoved())) {
      throw new SecurityException("LoginName alreay existed");
    }
    user.setPassword(PasswordEncoderUtils.encodePassword(user.getPassword(), ""));
    this.userDAO.save(user);
    SecurityRole role = new SecurityRole();
    role.setName("USER_" + user.getLoginName() + "_" + user.getId());
    user.getRoles().add(role);
    this.userDAO.update(user);
    if (user.getExtendProperty() != null) {
      this.userExtendPropertyDao.save(user.getExtendProperty());
    }
    try
    {
      if (SystemParameter.getBoolean("userorg_ldap", Boolean.FALSE).booleanValue())
      {
        LDAPUser ldapUser = new LDAPUser();
        ldapUser.setLoginName(user.getLoginName());
        ldapUser.setName(user.getName());
        ldapUser.setPassword(user.getPassword());
        LDAPFactory.getInstance().createUser(ldapUser);
      }
    }
    catch (LDAPException e) {}
  }
  
  public void updateUser(SecurityUser user)
  {
    if (user == null) {
      throw new SecurityException("User Principal not existed.");
    }
    this.userDAO.update(user);
    
    /* 2016/04/21暂时注释，原来的实现有问题，用户扩展信息保存待扩充
    if (user.getExtendProperty() != null) {
      this.userExtendPropertyDao.save(user.getExtendProperty());
    }
    */
    try
    {
      if (SystemParameter.getBoolean("userorg_ldap", Boolean.FALSE).booleanValue())
      {
        LDAPUser ldapUser = new LDAPUser();
        ldapUser.setLoginName(user.getLoginName());
        ldapUser.setName(user.getName());
        LDAPFactory.getInstance().updateUser(ldapUser);
      }
    }
    catch (LDAPException e) {}
  }
  
  public void updateUser(SecurityUser user, boolean isPasswordChanged)
  {
    if (user == null) {
      throw new SecurityException("User Principal not existed.");
    }
    if (isPasswordChanged) {
      user.setPassword(PasswordEncoderUtils.encodePassword(user.getPassword(), ""));
    }
    updateUser(user);
    if (user.getExtendProperty() != null) {
      this.userExtendPropertyDao.save(user.getExtendProperty());
    }
    try
    {
      if (SystemParameter.getBoolean("userorg_ldap", Boolean.FALSE).booleanValue())
      {
        LDAPUser ldapUser = new LDAPUser();
        ldapUser.setLoginName(user.getLoginName());
        ldapUser.setName(user.getName());
        if (isPasswordChanged) {
          ldapUser.setPassword(user.getPassword());
        }
        LDAPFactory.getInstance().updateUser(ldapUser);
      }
    }
    catch (LDAPException e) {}
  }
  
  public void removeUser(SecurityUser user)
  {
    if (user == null) {
      throw new SecurityException("User Principal not existed.");
    }
    user = (SecurityUser)this.userDAO.loadWithLazy(new Long(user.getId()), new String[] { "organNodes" });
    user.getOrganNodes().clear();
    this.userDAO.remove(user);
    try
    {
      if (SystemParameter.getBoolean("userorg_ldap", Boolean.FALSE).booleanValue())
      {
        LDAPUser ldapUser = new LDAPUser();
        ldapUser.setLoginName(user.getLoginName());
        ldapUser.setName(user.getName());
        LDAPFactory.getInstance().removeUser(ldapUser);
      }
    }
    catch (LDAPException e) {}
  }
  
  public SecurityUser loadUserById(long id)
  {
    return (SecurityUser)this.userDAO.load(new Long(id));
  }
  
  public SecurityUser loadUserWithLazyById(long id, String[] properties)
  {
    return (SecurityUser)this.userDAO.loadWithLazy(new Long(id), properties);
  }
  
  public SecurityUser[] findUsersByProperties(Map properties)
  {
    List result = this.userDAO.findUsersByProperties(properties);
    return (SecurityUser[])result.toArray(new SecurityUser[result.size()]);
  }
  
  public SecurityUser[] findUsersByProperties(String key, String value)
  {
    List result = this.userDAO.findUsersByProperties(key, value);
    return (SecurityUser[])result.toArray(new SecurityUser[result.size()]);
  }
  
  public SecurityUser getUserByLoginName(String loginName)
  {
    return this.userDAO.loadByLoginName(loginName);
  }
  
  public SecurityUser getLogoutUserByLoginName(String loginName)
  {
    return this.userDAO.getLogoutUserByLoginName(loginName);
  }
  
  public AuthenticationUser getAuthenticationUserByLoginName(String loginName)
  {
    SecurityUser securityUser = getUserByLoginName(loginName);
    if ((securityUser != null) && (!securityUser.isEntityRemoved()))
    {
      if (securityUser.getStatus() == 1L) {
        return securityUser;
      }
      throw new BadCredentialsException("user.status.unnormal");
    }
    return null;
  }
  
  public SecurityUser[] getAllUsers()
  {
    List users = this.userDAO.getAllUsers();
    return (SecurityUser[])users.toArray(new SecurityUser[users.size()]);
  }
  
  public Page getAllUsersByPage(Map filter, Map sort, int pageNo, int pageSize)
  {
    return this.userDAO.getAllUsersByPage(filter, sort, pageNo, pageSize);
  }
  
  public boolean createNewGroup(SecurityGroup group)
  {
    if (group == null) {
      throw new SecurityException("Group Principal not existed.");
    }
    if ((this.groupDAO.loadByCodeWithoutRemoved(group.getCode()) == null) && (this.groupDAO.loadByNameWithoutRemoved(group.getName()) == null))
    {
      SecurityRole role = new SecurityRole();
      role.setName("GROUP_" + group.getName());
      group.getRoles().add(role);
      this.groupDAO.save(group);
      return true;
    }
    throw new SecurityException("The group already existed.");
  }
  
  public void updateGroup(SecurityGroup group)
  {
    if (group == null) {
      throw new SecurityException("Group Principal not existed.");
    }
    this.groupDAO.update(group);
  }
  
  public void updateGroup(SecurityGroup group, boolean isNameChanged)
  {
    if (group == null) {
      throw new SecurityException("Group Principal not existed.");
    }
    if (isNameChanged)
    {
      SecurityRole defaultRole = this.roleDAO.loadDefaultRoleWithNewGroup(group);
      defaultRole.setName("GROUP_" + group.getName());
    }
    updateGroup(group);
  }
  
  public void removeGroup(SecurityGroup group)
  {
    if (group == null) {
      throw new SecurityException("Group Principal not existed.");
    }
    this.groupDAO.remove(group);
  }
  
  public SecurityGroup loadGroupById(long id)
  {
    return (SecurityGroup)this.groupDAO.load(new Long(id));
  }
  
  public SecurityGroup loadGroupByCode(String code)
  {
    return this.groupDAO.loadByCode(code);
  }
  
  public Page getAllGroupsByPage(Map filter, Map sort, int pageNo, int pageSize)
  {
    if (filter == null) {
      filter = new HashMap();
    }
    if (!filter.containsKey("removed")) {
      filter.put("removed", new Integer(0));
    }
    return this.groupDAO.findByLikeNameWithPage(filter, sort, pageNo, pageSize);
  }
  
  public SecurityGroup[] getAllGroups()
  {
    List groups = this.groupDAO.findAll();
    return (SecurityGroup[])groups.toArray(new SecurityGroup[groups.size()]);
  }
  
  public boolean isUserInGroup(SecurityUser user, SecurityGroup group)
  {
    if (group == null) {
      throw new SecurityException("Group Principal not existed.");
    }
    return this.userDAO.isUserInGroup(user, group);
  }
  
  public boolean isUserInDynamicGroup(SecurityUser user, SecurityGroup group)
  {
    if (group == null) {
      throw new SecurityException("Group Principal not existed.");
    }
    return this.userDAO.isUserInDynamicGroup(user, group);
  }
  
  public SecurityUser[] getAllUsersNotInGroup(SecurityGroup group)
  {
    if (group == null) {
      throw new SecurityException("Group Principal not existed.");
    }
    return this.userDAO.getAllUsersNotInGroup(group);
  }
  
  public Page getAllUsersInGroupByPage(SecurityGroup group, int pageNo, int pageSize)
  {
    if (group == null) {
      throw new SecurityException("Group Principal not existed.");
    }
    return this.userDAO.getAllUsersInGroupByPage(group, pageNo, pageSize);
  }
  
  public Page getAllUsersNotInGroupByPage(SecurityGroup group, int pageNo, int pageSize)
  {
    if (group == null) {
      throw new SecurityException("Group Principal not existed.");
    }
    return this.userDAO.getAllUsersNotInGroupByPage(group, pageNo, pageSize);
  }
  
  public SecurityUser[] getAllUsersInGroup(SecurityGroup group)
  {
    if (group == null) {
      throw new SecurityException("Group Principal not existed.");
    }
    return this.userDAO.getAllUsersInGroup(group);
  }
  
  public void addUserToGroup(SecurityUser user, SecurityGroup group)
  {
    if (user == null) {
      throw new SecurityException("User Principal not existed.");
    }
    if (group == null) {
      throw new SecurityException("Group Principal not existed.");
    }
    if (user.getId() == 0L) {
      createNewUser(user);
    }
    this.groupDAO.addUserToGroup(user, group);
  }
  
  public void removeUserFromGroup(SecurityUser user, SecurityGroup group)
  {
    if (user == null) {
      throw new SecurityException("User Principal not existed.");
    }
    if (group == null) {
      throw new SecurityException("Group Principal not existed.");
    }
    group = (SecurityGroup)this.groupDAO.loadWithLazy(new Long(group.getId()), new String[] { "users" });
    group.getUsers().remove(user);
    this.groupDAO.update(group);
  }
  
  public void addGroupToUser(SecurityGroup group, SecurityUser user)
  {
    if (group == null) {
      throw new SecurityException("Group Principal not existed.");
    }
    if (user == null) {
      throw new SecurityException("User Principal not existed.");
    }
    if (group.getId() == 0L) {
      createNewGroup(group);
    }
    this.userDAO.addGroupToUser(group, user);
  }
  
  public void removeGroupFromUser(SecurityGroup group, SecurityUser user)
  {
    if (group == null) {
      throw new SecurityException("Group Principal not existed.");
    }
    if (user == null) {
      throw new SecurityException("User Principal not existed.");
    }
    group = (SecurityGroup)this.groupDAO.loadWithLazy(new Long(group.getId()), new String[] { "users" });
    
    group.getUsers().remove(user);
    this.groupDAO.update(group);
  }
  
  public SecurityGroup getGroupByName(String name)
  {
    return this.groupDAO.loadByName(name);
  }
  
  public SecurityGroup getGroupByNameWithoutRemoved(String name)
  {
    return this.groupDAO.loadByNameWithoutRemoved(name);
  }
  
  public SecurityGroup loadGroupWithLazyById(long id, String[] propertyNames)
  {
    return (SecurityGroup)this.groupDAO.loadWithLazy(new Long(id), propertyNames);
  }
  
  public SecurityUser getUserByDefaultRole(SecurityRole role)
  {
    return this.userDAO.getUserByDefaultRole(role);
  }
  
  public void forbidUser(SecurityUser user)
  {
    if (user == null) {
      throw new SecurityException("User Principal not existed.");
    }
    if (user.getStatus() == 1L) {
      user.setStatus(-1L);
    }
    this.userDAO.update(user);
  }
  
  public SecurityUser[] getAllForbiddenUsers()
  {
    List result = this.userDAO.getAllForbiddenUsers();
    return (SecurityUser[])result.toArray(new SecurityUser[result.size()]);
  }
  
  public void allowUser(SecurityUser user)
  {
    if (user == null) {
      throw new SecurityException("User Principal not existed.");
    }
    if ((user.getStatus() == -1L) && (!user.isEntityRemoved())) {
      user.setStatus(1L);
    }
    this.userDAO.update(user);
  }
  
  public void activateUser(SecurityUser user)
  {
    if (user == null) {
      throw new SecurityException("User Principal not existed.");
    }
    if (user.getStatus() == 0L) {
      user.setStatus(1L);
    }
    this.userDAO.update(user);
  }
  
  public SecurityUser[] findUsersByBasicAndProps(Map basic, Map props)
  {
    List basicResult = new ArrayList();
    List propsResult = new ArrayList();
    if (basic != null) {
      basicResult = this.userDAO.findByLike(basic);
    }
    if (props != null) {
      propsResult = this.userDAO.findUsersByProperties(props);
    }
    Collection result = CollectionUtils.intersection(basicResult, propsResult);
    return (SecurityUser[])result.toArray(new SecurityUser[result.size()]);
  }
  
  public SecurityUser[] findUsersBy(Map filter)
  {
    List result = new ArrayList();
    if (filter != null) {
      try
      {
        result = this.userDAO.findByLike(filter);
      }
      catch (DAOException ex)
      {
        throw new SecurityException(ex.getMessage());
      }
    }
    return (SecurityUser[])result.toArray(new SecurityUser[result.size()]);
  }
  
  public AppNode[] getNodesByUser(SecurityUser user)
  {
    if (user == null) {
      throw new SecurityException("User Principal not existed.");
    }
    user = (SecurityUser)this.userDAO.loadWithLazy(new Long(user.getId()), new String[] { "appNodes" });
    List result = new ArrayList(user.getAppNodes());
    return (AppNode[])result.toArray(new AppNode[result.size()]);
  }
  
  public void addNodeToUser(SecurityUser user, AppNode appNode)
  {
    if (user == null) {
      throw new SecurityException("User Principal not existed.");
    }
    user = (SecurityUser)this.userDAO.loadWithLazy(new Long(user.getId()), new String[] { "appNodes" });
    user.getAppNodes().add(appNode);
    this.userDAO.update(user);
  }
  
  public void updateNodesWithUser(SecurityUser user, AppNode[] appNodes)
  {
    if (user == null) {
      throw new SecurityException("User Principal not existed.");
    }
    user = (SecurityUser)this.userDAO.loadWithLazy(new Long(user.getId()), new String[] { "appNodes" });
    for (int i = 0; i < appNodes.length; i++) {
      user.getAppNodes().add(appNodes[i]);
    }
    this.userDAO.update(user);
  }
  
  public void removeNodesFromUser(SecurityUser user)
  {
    if (user == null) {
      throw new SecurityException("User Principal not existed.");
    }
    user = (SecurityUser)this.userDAO.loadWithLazy(new Long(user.getId()), new String[] { "appNodes" });
    user.getAppNodes().clear();
    this.userDAO.update(user);
  }
  
  public SecurityUser[] getAllUsersInGroup(SecurityGroup group, Map map)
  {
    return null;
  }
  
  public Page getUserNotInNodeByPage(List ids, String name, int pageNo, int pageSize)
  {
    return this.userDAO.getUserNotInNodeByPage(ids, name, pageNo, pageSize);
  }
  
  public Page getUserInIds(List ids)
  {
    return this.userDAO.getUserInIds(ids);
  }
  
  public Page getUserInOrgansIds(List ids, List organNodes)
  {
    return this.userDAO.getUserInOrgansIds(ids, organNodes);
  }
  
  public Page getAllUsersNotInGroupByPageAndName(SecurityGroup group, int pageNo, int pageSize, String name)
  {
    return this.userDAO.getAllUsersNotInGroupByPageAndName(group, pageNo, pageSize, name);
  }
  
  public SecurityGroup[] getAllDynaGroups()
  {
    return this.groupDAO.getAllDynaGroups();
  }
  
  public SecurityGroup[] getAllAuthControlDynaGroups()
  {
    return this.groupDAO.getAllAuthControlDynaGroups();
  }
  
  public SecurityGroup[] getAllAuthControlGroup()
  {
    return this.groupDAO.getAllAuthControlGroup();
  }
  
  public Page getAllDynaGroupsByPage(Map filter, int pageNo, int pageSize)
  {
    return this.groupDAO.findByLikeNameWithPage(filter, null, pageNo, pageSize);
  }
  
  public Page getAllOrganUsersByPage(List organNodes, int pageNo, int pageSize)
  {
    return this.userDAO.getAllOrganUsersByPage(organNodes, pageNo, pageSize);
  }
  
  public Page getAllOrganUsersLikeNameByPage(String userName, List organNodes, int pageNo, int pageSize)
  {
    return this.userDAO.getAllOrganUsersLikeNameByPage(userName, organNodes, pageNo, pageSize);
  }
  
  public OrganNode[] getOrgansByUser(SecurityUser user)
  {
    if (user == null) {
      throw new SecurityException("User Principal not existed.");
    }
    user = (SecurityUser)this.userDAO.loadWithLazy(new Long(user.getId()), new String[] { "organNodes" });
    
    List result = new ArrayList(user.getOrganNodes());
    
    return (OrganNode[])result.toArray(new OrganNode[result.size()]);
  }
  
  public Page findByHQLWithPage(String qryHql, List args, int pageNo, int pageSize, String countHql)
  {
    return this.userDAO.findByHQLWithPage(qryHql, args, pageNo, pageSize, countHql);
  }
  
  public Page getAllGroupsNotOfUserByPage(SecurityUser user, int pageNo, int pageSize)
  {
    return this.userDAO.getAllGroupsNotOfUserByPage(user, pageNo, pageSize);
  }
  
  public Page getAllGroupsNotOfUserByPageAndName(SecurityUser user, int pageNo, int pageSize, String name)
  {
    return this.userDAO.getAllGroupsNotOfUserByPageAndName(user, pageNo, pageSize, name);
  }
  
  public Page getAllGroupsOfUserByPage(SecurityUser user, int pageNo, int pageSize)
  {
    if (user == null) {
      throw new SecurityException("User Principal not existed.");
    }
    return this.userDAO.getAllGroupsOfUserByPage(user, pageNo, pageSize);
  }
  
  public List getAllInActiveUsers()
  {
    return this.userDAO.getAllInActiveUsers();
  }
  
  public Page getInActiveUsersByPage(Map filter, Map sort, int pageNo, int pageSize)
  {
    return this.userDAO.getInActiveUsersByPage(filter, sort, pageNo, pageSize);
  }
  
  public Page getForbiddenUsersByPage(Map filter, Map sort, int pageNo, int pageSize)
  {
    return this.userDAO.getForbiddenUsersByPage(filter, sort, pageNo, pageSize);
  }
  
  public SecurityUser[] findByHQL(String qryHql, List args)
  {
    List result = this.userDAO.findByHQL(qryHql, args);
    return result != null ? (SecurityUser[])result.toArray(new SecurityUser[result.size()]) : new SecurityUser[0];
  }
  
  public boolean updateUsersPassword(String loginName, String oldPassword, String newPassword)
  {
    Assert.notNull(loginName);
    Assert.notNull(oldPassword);
    Assert.notNull(newPassword);
    
    boolean isOk = this.authenticationService.authentication(loginName, oldPassword);
    if (isOk)
    {
      SecurityUser securityUser = getUserByLoginName(loginName);
      
      securityUser.setPassword(newPassword);
      updateUser(securityUser, true);
    }
    return isOk;
  }
  
  public Map<String, String> getExtendPropertyNames()
  {
    return this.extendPropertyConfigService.getExtendPropertyNames(SecurityUser.class.getName());
  }
  
  public Map<String, String> getExtendPropertyMap(SecurityUser user)
  {
    Map<String, String> result = new HashMap();
    Map<String, String> nameMap = getExtendPropertyNames();
    BeanMap map = new BeanMap(user.getExtendProperty());
    for (String key : nameMap.keySet()) {
      if ((nameMap.get(key) != null) && (!"".equals(nameMap.get(key)))) {
        if (map.get(key) != null) {
          result.put(nameMap.get(key), String.valueOf(map.get(key)));
        } else {
          result.put(nameMap.get(key), null);
        }
      }
    }
    return result;
  }
  
  public void setExtendPropertyMap(SecurityUser user, Map<String, String> extendPropertyMap)
  {
    UserExtendProperty extendProperty = user.getExtendProperty();
    if (extendProperty == null) {
      extendProperty = new UserExtendProperty();
    }
    Map<String, String> nameMap = getExtendPropertyNames();
    for (String key : nameMap.keySet()) {
      try
      {
        if ((nameMap.get(key) != null) && (!"".equals(nameMap.get(key)))) {
          BeanUtils.setProperty(extendProperty, key, extendPropertyMap.get(nameMap.get(key)));
        }
      }
      catch (Exception ex) {}
    }
    extendProperty.setUser(user);
    user.setExtendProperty(extendProperty);
  }
  
  public void addUserLoginHistory(UserLoginHistory userLoginHistory)
  {
    this.userLoginHistoryDAO.save(userLoginHistory);
  }
  
  public List findUserLogins(Map filter)
  {
    return this.userLoginHistoryDAO.findUserLogins(filter);
  }
  
  public Page findUserLoginsByPage(Map filter, int pageNo, int pageSize)
  {
    return this.userLoginHistoryDAO.findUserLoginsByPage(filter, pageNo, pageSize);
  }
  
  public long getErrorLoginCount(SecurityUser user)
  {
    StringBuffer queryHql = new StringBuffer();
    queryHql.append("select max(loginTime) from UserLoginHistory").append(" where userName = :name and userState = :online");
    
    List<HqlParameter> args = new ArrayList();
    args.add(new HqlParameter("name", user.getName()));
    args.add(new HqlParameter("online", Integer.valueOf(1)));
    List list = this.userLoginHistoryDAO.findByHQL(queryHql.toString(), args);
    long lastOnline = 0L;
    if ((list != null) && (list.size() > 0) && (list.get(0) != null)) {
      lastOnline = ((Long)list.get(0)).longValue();
    }
    queryHql = new StringBuffer();
    queryHql.append("select count(login), max(login.loginTime) from UserLoginHistory login").append(" where login.userName = :name and login.userState = :failed").append(" and login.loginTime >= :startDate and login.loginTime < :endDate").append(" and login.loginTime > :lastOnline");
    
    args.clear();
    args.add(new HqlParameter("name", user.getName()));
    args.add(new HqlParameter("failed", Integer.valueOf(2)));
    Date date = new Date(System.currentTimeMillis());
    Float locktime = SystemParameter.getFloat("ca_errorlogin_locktime", Float.valueOf(1.0F));
    if (locktime.floatValue() < 0.0F) {
      locktime = new Float(1.0F);
    }
    args.add(new HqlParameter("startDate", Long.valueOf(String.valueOf(date.getTime() - 86400000L - (locktime.floatValue() * 3600000.0F)))));
    args.add(new HqlParameter("endDate", Long.valueOf(date.getTime())));
    args.add(new HqlParameter("lastOnline", Long.valueOf(lastOnline)));
    List result = this.userLoginHistoryDAO.findByHQL(queryHql.toString(), args);
    if ((result != null) && (result.size() > 0))
    {
      Object[] object = (Object[])result.get(0);
      if ((object[1] != null) && (((Long)object[1]).longValue() > date.getTime() - (locktime.floatValue() * 3600000.0F))) {
        return ((Long)object[0]).longValue();
      }
    }
    return 0L;
  }
  
  public SecurityUser[] queryUsers(UserDTOExtd dto)
  {
    StringBuffer hql = new StringBuffer();
    List<HqlParameter> args = new ArrayList();
    
    prepareParameters(dto, hql, args);
    return findByHQL(hql.toString(), args);
  }
  
  public Page queryUsersWithPage(UserDTOExtd dto)
  {
    StringBuffer hql = new StringBuffer();
    List<HqlParameter> args = new ArrayList();
    int pageSize = dto.getLimit();
    int nextPageNo = dto.getStart() / dto.getLimit() + 1;
    
    String countHql = prepareParameters(dto, hql, args);
    return findByHQLWithPage(hql.toString(), args, nextPageNo, pageSize, countHql);
  }
  
  private String prepareParameters(UserDTOExtd dto, StringBuffer hql, List<HqlParameter> args)
  {
    StringBuffer temp = new StringBuffer();
    String countSQL = "";
    temp.append(" where user.loginName <> 'admin' and user.removed = 0");
    preparePropertyParameters(dto, temp, args);
    prepareStatusParameters(dto, temp, args);
    prepareRelatedParameters(dto, temp, args);
    if (prepareAdminParameters(dto, temp, args))
    {
      hql.append("select distinct user from SecurityUser user left join user.extendProperty ex").append(" inner join user.organNodes nodes inner join nodes.organModels models").append(temp);
      countSQL = "select count(distinct user.id) from SecurityUser user left join user.extendProperty ex" + " inner join user.organNodes nodes inner join nodes.organModels models" + temp;
      if (StringUtils.isNotEmpty(dto.getOrder()))
      	hql.append(" order by " + dto.getOrder());
      return countSQL;
    }
    hql.append("select distinct user from SecurityUser user left join user.extendProperty ex").append(temp);
    countSQL = "select count(*) " + hql.substring(hql.indexOf("from "));
    if (StringUtils.isNotEmpty(dto.getOrder()))
      	hql.append(" order by " + dto.getOrder());
    return countSQL;
  }
  
  private void preparePropertyParameters(UserDTOExtd dto, StringBuffer hql, List<HqlParameter> args)
  {
    StringBuffer temp = new StringBuffer();
    String logic = "";
    if (dto.isAnd()) {
      logic = " and ";
    } else {
      logic = " or ";
    }
    if (isNotEmptyString(dto.getUserName()))
    {
      temp.append(logic).append("user.name like :name");
      args.add(new HqlParameter("name", "%" + dto.getUserName() + "%"));
    }
    if (isNotEmptyString(dto.getLoginName()))
    {
      temp.append(logic).append("user.loginName like :loginName");
      args.add(new HqlParameter("loginName", "%" + dto.getLoginName() + "%"));
    }
    if (isNotEmptyString(dto.getSex()))
    {
      temp.append(logic).append("user.sex like :sex");
      args.add(new HqlParameter("sex", "%" + dto.getSex() + "%"));
    }
    if (isNotEmptyString(dto.getAddress()))
    {
      temp.append(logic).append("user.address like :address");
      args.add(new HqlParameter("address", "%" + dto.getAddress() + "%"));
    }
    if (isNotEmptyString(dto.getEmail()))
    {
      temp.append(logic).append("user.email like :email");
      args.add(new HqlParameter("email", "%" + dto.getEmail() + "%"));
    }
    if (isNotEmptyString(dto.getHonePhone()))
    {
      temp.append(logic).append("user.honePhone like :honePhone");
      args.add(new HqlParameter("honePhone", "%" + dto.getHonePhone() + "%"));
    }
    if (isNotEmptyString(dto.getOfficePhone()))
    {
      temp.append(logic).append("user.officePhone like :officePhone");
      args.add(new HqlParameter("officePhone", "%" + dto.getOfficePhone() + "%"));
    }
    if (isNotEmptyString(dto.getMobile1()))
    {
      temp.append(logic).append("user.mobile1 like :mobile1");
      args.add(new HqlParameter("mobile1", "%" + dto.getMobile1() + "%"));
    }
    if (isNotEmptyString(dto.getMobile2()))
    {
      temp.append(logic).append("user.mobile2 like :mobile2");
      args.add(new HqlParameter("mobile2", "%" + dto.getMobile2() + "%"));
    }
    if (isNotEmptyString(dto.getFax()))
    {
      temp.append(logic).append("user.fax like :fax");
      args.add(new HqlParameter("fax", "%" + dto.getFax() + "%"));
    }
    Map map = dto.getExtendPropertyMap();
    Map<String, String> nameMap;
    if ((map != null) && (!map.isEmpty()))
    {
      nameMap = getExtendPropertyNames();
      for (String key : nameMap.keySet()) {
        if ((!"".equals(nameMap.get(key))) && (map.containsKey(nameMap.get(key))) && (map.get(nameMap.get(key)) != null) && (isNotEmptyString(String.valueOf(map.get(nameMap.get(key))))))
        {
          temp.append(logic).append("ex.").append(key).append(" like :").append(key);
          args.add(new HqlParameter(key, "%" + map.get(nameMap.get(key)) + "%"));
        }
      }
    }
    if (temp.length() > 0) {
      if (dto.isAnd()) {
        hql.append(" and (1=1").append(temp).append(")");
      } else {
        hql.append(" and (1=0").append(temp).append(")");
      }
    }
  }
  
  private void prepareStatusParameters(UserDTOExtd dto, StringBuffer hql, List<HqlParameter> args)
  {
    if (dto.getCreateDateFrom() != null)
    {
      hql.append(" and user.createDate >= :createDateFrom");
      args.add(new HqlParameter("createDateFrom", dto.getCreateDateFrom()));
    }
    if (dto.getCreateDateTo() != null)
    {
      hql.append(" and user.createDate >= :createDateTo");
      args.add(new HqlParameter("createDateTo", dto.getCreateDateTo()));
    }
    if (dto.getAccountType() != null)
    {
      hql.append(" and user.accountType = :accountType ");
      args.add(new HqlParameter("accountType", Integer.valueOf(dto.getAccountType().intValue())));
    }
    if (dto.getStatus() != null)
    {
      hql.append(" and user.status = :status");
      args.add(new HqlParameter("status", dto.getStatus()));
    }
  }
  
  private void prepareRelatedParameters(UserDTOExtd dto, StringBuffer hql, List<HqlParameter> args)
  {
    if (isNotEmptyString(dto.getGroupId()))
    {
      SecurityGroup group = loadGroupById(new Long(dto.getGroupId()).longValue());
      if (group != null)
      {
        if (dto.isInGroup()) {
          hql.append(" and :group in elements(user.groups)");
        } else {
          hql.append(" and :group <> all elements (user.groups)");
        }
        args.add(new HqlParameter("group", group));
      }
    }
    if (isNotEmptyString(dto.getOrganNodeId()))
    {
      OrganNode organNode = (OrganNode)this.organNodeDao.load(new Long(dto.getOrganNodeId()));
      if (organNode != null)
      {
        if (dto.isInOrganNode()) {
          hql.append(" and :organNode in elements(user.organNodes)");
        } else {
          hql.append(" and :organNode <> all elements (user.organNodes)");
        }
        args.add(new HqlParameter("organNode", organNode));
      }
    }
    if (isNotEmptyString(dto.getAppNodeId()))
    {
      AppNode appNode = (AppNode)this.appNodeDAO.load(new Long(dto.getAppNodeId()));
      if (appNode != null)
      {
        if (dto.isInAppNode()) {
          hql.append(" and :appNode in elements(user.appNodes)");
        } else {
          hql.append(" and :appNode <> all elements (user.appNodes)");
        }
        args.add(new HqlParameter("appNode", appNode));
      }
    }
    if (isNotEmptyString(dto.getProxyUserId()))
    {
      SecurityUser proxyUser = loadUserById(new Long(dto.getProxyUserId()).longValue());
      if (proxyUser != null)
      {
        if (dto.isInProxy()) {
          hql.append(" and user in (select info.proxy from ProxyInfo as info where info.user = :proxyUser)");
        } else {
          hql.append(" and user not in (select info.proxy from ProxyInfo as info where info.user = :proxyUser)");
        }
        hql.append(" and user <> :proxyUser");
        args.add(new HqlParameter("proxyUser", proxyUser));
      }
    }
  }
  
  private boolean prepareAdminParameters(UserDTOExtd dto, StringBuffer hql, List<HqlParameter> args)
  {
    String organTreeId = dto.getOrganTreeId();
    String adminUserId = dto.getAdminUserId();
    String departmentName = dto.getDepartmentName();
    String positionName = dto.getPositionName();
    boolean result = false;
    if ((organTreeId != null) || ((adminUserId != null) && (!isNotEmptyString(dto.getDepartmentName())) && (!isNotEmptyString(dto.getPositionName()))))
    {
      hql.append(" and models in (select model from OrganModel model").append(" left join model.organNode node left join model.organTree tree,").append(" OrganModel m left join m.organNode n left join m.organTree t").append(" where model.removed = 0 and m.removed = 0 and model.lft >= m.lft and model.rgt <= m.rgt").append(" and tree.id = t.id");
      if (organTreeId != null)
      {
        hql.append(" and tree.id = :organTreeId");
        args.add(new HqlParameter("organTreeId", new Long(organTreeId)));
      }
      if (adminUserId != null)
      {
        hql.append(" and n.adminUser.id = :adminUserId");
        args.add(new HqlParameter("adminUserId", new Long(adminUserId)));
      }
      if ((organTreeId != null) && (adminUserId == null)) {
        hql.append(" and m.parentNode is null");
      }
      hql.append(")");
      result = true;
    }
    if ((isNotEmptyString(dto.getDepartmentName())) || (isNotEmptyString(dto.getPositionName())))
    {
      hql.append(" and models in (select model from OrganModel model").append(" left join model.organNode node left join model.organTree tree left join node.organNodeType nodeType,").append(" OrganModel m left join m.organNode n left join m.organTree t left join n.organNodeType nt").append(" where model.removed = 0 and m.removed = 0 and model.lft > m.lft and model.rgt < m.rgt").append(" and tree.id = t.id");
      if (isNotEmptyString(dto.getDepartmentName()))
      {
        hql.append(" and n.name like :departmentName").append(" and nt.code = :departmentCode");
        args.add(new HqlParameter("departmentName", "%" + departmentName + "%"));
        args.add(new HqlParameter("departmentCode", OrganConstants.getDepartmentTypeCode()));
      }
      if (isNotEmptyString(dto.getPositionName()))
      {
        hql.append(" and node.name like :positionName").append(" and nodeType.code = :positionCode");
        args.add(new HqlParameter("positionName", "%" + positionName + "%"));
        args.add(new HqlParameter("positionCode", OrganConstants.getPositionTypeCode()));
      }
      hql.append(")");
      result = true;
    }
    return result;
  }
  
  private boolean isNotEmptyString(String str)
  {
    boolean isEmpty = false;
    if ((str == null) || ("".equals(str))) {
      isEmpty = true;
    }
    return !isEmpty;
  }
}
