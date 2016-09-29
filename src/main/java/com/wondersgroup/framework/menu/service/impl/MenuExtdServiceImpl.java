package com.wondersgroup.framework.menu.service.impl;

import com.wondersgroup.framework.core.bo.Page;
import com.wondersgroup.framework.core.bo.hibernate.HqlParameter;
import com.wondersgroup.framework.core.exception.BusinessException;
import com.wondersgroup.framework.menu.bo.MenuResource;
import com.wondersgroup.framework.menu.bo.ProxyInfo;
import com.wondersgroup.framework.menu.dao.MenuResourceExtdDAO;
import com.wondersgroup.framework.menu.dao.ProxyInfoExtdDAO;
import com.wondersgroup.framework.menu.exception.MenuException;
import com.wondersgroup.framework.menu.service.MenuExtdService;
import com.wondersgroup.framework.resource.bo.AppNode;
import com.wondersgroup.framework.resource.connector.Resource;
import com.wondersgroup.framework.resource.connector.service.impl.AbstractResourceExtdServiceImpl;
import com.wondersgroup.framework.resource.service.AppNodeExtdService;
import com.wondersgroup.framework.security.bo.ACLOperation;
import com.wondersgroup.framework.security.bo.ACLResource;
import com.wondersgroup.framework.security.bo.ACLResourceType;
import com.wondersgroup.framework.security.bo.SecurityGroup;
import com.wondersgroup.framework.security.bo.SecurityRole;
import com.wondersgroup.framework.security.bo.SecurityUser;
import com.wondersgroup.framework.security.exception.SecurityException;
import com.wondersgroup.framework.security.service.ACLResourceExtdService;
import com.wondersgroup.framework.security.service.ACLExtdService;
import com.wondersgroup.framework.security.service.RoleExtdService;
import com.wondersgroup.framework.security.service.UserExtdService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service("MenuExtService")
@Transactional
public class MenuExtdServiceImpl extends AbstractResourceExtdServiceImpl implements MenuExtdService
{
  private Log logger = LogFactory.getLog(MenuExtdServiceImpl.class);
  private static final String MENU_TYPE_CODE = "Menu";
  private static final String MENU_OPERATION_CODE = "Read1";
  @Autowired
  private MenuResourceExtdDAO menuResourceDAO;
  @Autowired
  private ACLExtdService aclService;
  @Autowired
  private ACLResourceExtdService aclResourceService;
  @Autowired
  private RoleExtdService roleService;
  @Autowired
  private UserExtdService userService;
  @Autowired
  private ProxyInfoExtdDAO proxyInfoDAO;
  @Autowired
  private AppNodeExtdService appNodeService;
  private static final String DEFAULT_WEBAPP_NODE_ID = "";

  private void deleteMenuResourceByParentResource(MenuResource menuResource)
  {
    if (this.menuResourceDAO.isExistChildrenMenuResourceByParentMenu(menuResource))
    {
      Iterator iterator = this.menuResourceDAO.getChildMenuResourcesByParentMenu(menuResource).iterator();
      while (iterator.hasNext())
      {
        MenuResource siblingResource = (MenuResource)iterator.next();
        deleteMenuResourceByParentResource(siblingResource);
        this.menuResourceDAO.delete(siblingResource);
      }
    }
  }
  
  private ACLResourceType getResourceType()
  {
    ACLResourceType type = this.aclResourceService.getResourceTypeByCode("Menu");
    return type;
  }
  
  public Resource[] getResource()
  {
    List result = this.menuResourceDAO.getAllMenuResources();
    return (Resource[])result.toArray(new Resource[result.size()]);
  }
  
  public MenuResource[] getAllMenuResources()
  {
    List result = this.menuResourceDAO.getAllMenuResources();
    return (MenuResource[])result.toArray(new MenuResource[result.size()]);
  }
  
  public Page getResource(Map filter, Map sort, int pageNo, int pageSize)
  {
    return this.menuResourceDAO.findByWithPage(filter, sort, pageNo, pageSize);
  }
  
  public MenuResource getMenuResourceById(long id)
  {
    return (MenuResource)this.menuResourceDAO.load(new Long(id));
  }
  
  public MenuResource getMenuResourceByCode(String code)
  {
    return (MenuResource)this.menuResourceDAO.findUniqueBy("code", code);
  }
  
  public void createMenuResource(MenuResource menuResource)
  {
    MenuResource existed = getMenuResourceByCode(menuResource.getCode());
    if (existed != null) {
      throw new BusinessException(existed.getCode() + "already existed");
    }
    this.menuResourceDAO.save(menuResource);
  }
  
  public void delMenuResourceById(long id)
  {
    MenuResource menuResource = getMenuResourceById(id);
    deleteMenuResourceByParentResource(menuResource);
    this.menuResourceDAO.delete(menuResource);
  }
  
  public boolean isChildExist(MenuResource parentMenu)
  {
    return this.menuResourceDAO.isExistChildrenMenuResourceByParentMenu(parentMenu);
  }
  
  public MenuResource[] getChildMenuResourceOrderlyByParentMenu(MenuResource parentMenu)
  {
    List result = this.menuResourceDAO.getChildMenuResourcesByParentMenu(parentMenu);
    Collections.sort(result, new Comparator()
    {
      public int compare(Object a, Object b)
      {
        int indexA = ((MenuResource)a).getDisplayOrder();
        int indexB = ((MenuResource)b).getDisplayOrder();
        return indexA - indexB;
      }
    });
    return (MenuResource[])result.toArray(new MenuResource[result.size()]);
  }
  
  public void updateMenuResource(MenuResource menuResource)
  {
    this.menuResourceDAO.update(menuResource);
  }
  
  public MenuResource[] getTopMenuResources()
  {
    List result = this.menuResourceDAO.getTopMenuResources();
    return (MenuResource[])result.toArray(new MenuResource[result.size()]);
  }
  
  public Map getParentChildAssort()
  {
    Map<MenuResource, List<MenuResource>> result = new HashMap();
    //String queryHql = "from MenuResource order by parentResource.id, displayOrder";
    //List<MenuResource> list = this.menuResourceDAO.findByHQL(queryHql, new ArrayList());
    List<MenuResource> list = this.menuResourceDAO.getAllMenuResources();
    MenuResource[] allMenuResources = (MenuResource[])list.toArray(new MenuResource[list.size()]);
    for (int i = 0; i < allMenuResources.length; i++)
    {
      MenuResource menu = allMenuResources[i];
      MenuResource parentMenu = null;
      if (menu.getParentResource() != null)
      {
        parentMenu = (MenuResource)menu.getParentResource();
        if (result.get(parentMenu) == null) {
          result.put(parentMenu, new ArrayList());
        }
        ((List)result.get(parentMenu)).add(menu);
      }
    }
    return result;
  }
  
  public Map getAuthParentChildAssort(String userId)
  {
    Map<MenuResource, List<MenuResource>> result = new HashMap();
    List<MenuResource> list = this.menuResourceDAO.getAllMenuResources();
    //String queryHql = "from MenuResource order by parentResource.id, displayOrder";
    //List<MenuResource> list = this.menuResourceDAO.findByHQL(queryHql, new ArrayList());
    List<MenuResource> removedList = new ArrayList();
    List resourceIds = new ArrayList();
    for (MenuResource menu : list) {
      resourceIds.add(menu.getResourceId());
    }
    Map map = this.aclService.checkByCode(userId, resourceIds, "Menu", "Read1");
    for (MenuResource menu : list)
    {
      String resouceId = menu.getResourceId();
      boolean flag = ((Boolean)map.get(resouceId)).booleanValue();
      if (!flag) {
        removedList.add(menu);
      }
    }
    list.removeAll(removedList);
    for (MenuResource menu : list)
    {
      MenuResource parentMenu = null;
      if (menu.getParentResource() != null)
      {
        parentMenu = (MenuResource)menu.getParentResource();
        if (result.get(parentMenu) == null) {
          result.put(parentMenu, new ArrayList());
        }
        ((List)result.get(parentMenu)).add(menu);
      }
    }
    return result;
  }
  
  public int getMenuResourceCount()
  {
    return this.menuResourceDAO.getMenuResourceCount();
  }
  
  public Map getMenuResourcesByAuth(String userId)
  {
    int menuCount = this.menuResourceDAO.getMenuResourceCount();
    if (menuCount == 0) {
      throw new MenuException("Authed Menu not existed!");
    }
    MenuResource[] allMenuResources = getAllMenuResources();
    List resouceIdList = new ArrayList();
    for (int i = 0; i < allMenuResources.length; i++)
    {
      MenuResource resource = allMenuResources[i];
      resouceIdList.add(resource.getResourceId());
    }
    ACLResourceType menuType = getResourceType();
    ACLOperation operation = this.aclResourceService.getOperations(menuType)[0];
    String operationId = String.valueOf(operation.getId());
    String resourceTypeId = String.valueOf(menuType.getId());
    return this.aclService.check(userId, resouceIdList, resourceTypeId, operationId);
  }
 
  public MenuResource[] getAuthMenusByAppNode(Long appNodeId, String userId)
  {
    AppNode appNode = this.appNodeService.loadById(appNodeId.longValue());
    Assert.notNull(appNode, "appNodeId" + appNodeId + "is not a valid appNode's ID");
    MenuResource menuResource = getMenuResourceByCode(appNode.getRootMenuCode());
    return getAuthMenusByParentMenu(menuResource, userId);
  }
  
  public MenuResource[] getAuthMenusByParentMenu(MenuResource menuResource, String userId)
  {
    List result = null;
    List removedList = new ArrayList();
    if (menuResource != null) {
      result = this.menuResourceDAO.getChildMenuResourcesByParentMenu(menuResource);
    } else {
      return new MenuResource[0];
    }
    if ((result == null) || (result.size() == 0)) {
      return new MenuResource[0];
    }
    List resourceIds = new ArrayList();
    for (Iterator iter = result.iterator(); iter.hasNext();)
    {
      MenuResource element = (MenuResource)iter.next();
      resourceIds.add(String.valueOf(element.getId()));
    }
    Map map = this.aclService.checkByCode(userId, resourceIds, "Menu", "Read1");
    for (Iterator iter = result.iterator(); iter.hasNext();)
    {
      MenuResource element = (MenuResource)iter.next();
      String resouceId = String.valueOf(element.getId());
      boolean flag = ((Boolean)map.get(resouceId)).booleanValue();
      if (!flag) {
        removedList.add(element);
      }
    }
    result.removeAll(removedList);
    return (MenuResource[])result.toArray(new MenuResource[result.size()]);
  }
  
  public void accreditPermission(String userId, MenuResource menuInfo)
  {
    ACLResource aclResource = convertMenuResourceToACLResource(menuInfo);
    this.aclResourceService.createNewACLResource(aclResource);
    ACLOperation[] opes = this.aclResourceService.getOperations(getResourceType());
    for (int i = 0; i < opes.length; i++) {
      if (opes[i].getCode().equals("Read1"))
      {
        this.aclService.accredit(this.roleService.loadDefaultRoleWithUser(this.userService.loadUserById(Long.parseLong(userId))), aclResource, opes[i]);
        
        break;
      }
    }
  }
  
  public ACLResource convertMenuResourceToACLResource(MenuResource menu)
  {
    ACLResource aclResource = new ACLResource();
    aclResource.setName(menu.getResourceName());
    aclResource.setAclResourceType(getResourceType());
    aclResource.setNativeResourceId(String.valueOf(menu.getId()));
    return aclResource;
  }

  public void createMenuInfoWithPermission(String userId, MenuResource menuInfo)
  {
    createMenuResource(menuInfo);
    SecurityUser user = this.userService.loadUserById(Long.valueOf(userId).longValue());
    if (!user.getLoginName().equalsIgnoreCase("admin")) {
      accreditPermission(userId, menuInfo);
    }
  }
  
  public MenuResource getTopMenuInfoByName(String resourceName)
  {
    return this.menuResourceDAO.getTopMenuInfoByName(resourceName);
  }
  
  public MenuResource loadMenuResourceWithLazy(long menuId, String[] propertyNames)
  {
    if ((propertyNames == null) || (propertyNames.equals(""))) {
      return (MenuResource)this.menuResourceDAO.load(new Long(menuId));
    }
    return (MenuResource)this.menuResourceDAO.loadWithLazy(new Long(menuId), propertyNames);
  }
  
  public Map getAuthMenus(String menuId, String userId)
  {
    if ((menuId == null) || (userId == null))
    {
      this.logger.error("null param!");
      return null;
    }
    MenuResource menu = (MenuResource)this.menuResourceDAO.load(new Long(menuId));
    MenuResource[] menus = { menu };
    Map result = new HashMap();
    buildMenuMap(result, menus, userId);
    return result;
  }
  
  public Map getAllAuthMenusByAppNode(String appNodeId, String userId)
  {
    AppNode appNode = this.appNodeService.loadById(new Long(appNodeId).longValue());
    MenuResource menuResource = getMenuResourceByCode(appNode.getRootMenuCode());
    if (menuResource == null) {
      return new HashMap();
    }
    return null;
  }
  
  public Map getAllMenusByAppNode(String appNodeId)
  {
    AppNode appNode = this.appNodeService.loadById(new Long(appNodeId).longValue());
    MenuResource menuResource = getMenuResourceByCode(appNode.getRootMenuCode());
    if (menuResource == null) {
      return new HashMap();
    }
    return null;
  }
  
  private void buildMenuMap(Map result, MenuResource[] menus, String userId)
  {
    if ((menus == null) || (menus.length < 1)) {
      return;
    }
    MenuResource[] temp = null;
    for (int i = 0; i < menus.length; i++)
    {
      temp = getAuthMenusByParentMenu(menus[i], userId);
      if ((temp != null) && (temp.length > 0))
      {
        result.put(menus[i], temp);
        buildMenuMap(result, temp, userId);
      }
    }
  }
  
  public MenuResource getMenuInfoById(long id)
  {
    return (MenuResource)this.menuResourceDAO.load(new Long(id));
  }
  
  public Collection getAuthUsersByMenuResource(MenuResource menu, String operationCode)
  {
    ACLResourceType aclResType = this.aclResourceService.getResourceTypeByCode("Menu");
    ACLResource aclRes = this.aclResourceService.getResourceByTypeAndNativeId(aclResType, new Long(menu.getId()).toString());
    
    ACLOperation aclOpe = this.aclResourceService.getOperationByCode(aclResType, operationCode);
    List result = this.roleService.findSecurityRoleWithResourceAndOperation(aclRes, aclOpe);
    Set users = new HashSet();
    SecurityRole ele;
    Iterator iter;
    if ((result != null) && (result.size() > 0))
    {
      ele = null;
      for (iter = result.iterator(); iter.hasNext();)
      {
        ele = (SecurityRole)iter.next();
        if (ele.getName().startsWith("USER_"))
        {
          ele = this.roleService.loadRoleByIdWithLazy(ele.getId(), new String[] { "users" });
          users.addAll(ele.getUsers());
        }
        else if (ele.getName().startsWith("GROUP_"))
        {
          SecurityGroup group = this.userService.getGroupByName(ele.getName().substring(6));
          group = this.userService.loadGroupWithLazyById(group.getId(), new String[] { "users" });
          users.addAll(group.getUsers());
        }
      }
    }
    return users;
  }
  
  public Page getUsersByPageAndACLMenu(String resourceId, long resourceTypeId, String operationCode, int pageNo, int pageSize)
  {
    ACLResourceType aclResType = this.aclResourceService.loadResourceTypeById(resourceTypeId);
    ACLResource aclRes = this.aclResourceService.getResourceByTypeAndNativeId(aclResType, resourceId);
    ACLOperation aclOpe = this.aclResourceService.getOperationByCode(aclResType, operationCode);
    Page page = this.menuResourceDAO.getUsersByPageAndACLMenu(aclRes, aclOpe, pageNo, pageSize);
    return page;
  }
  
  public Page getUsersByPageAndREVMenu(String resourceId, long resourceTypeId, String operationCode, int pageNo, int pageSize)
  {
    ACLResourceType aclResType = this.aclResourceService.loadResourceTypeById(resourceTypeId);
    ACLResource aclRes = this.aclResourceService.getResourceByTypeAndNativeId(aclResType, resourceId);
    ACLOperation aclOpe = this.aclResourceService.getOperationByCode(aclResType, operationCode);
    Page page = this.menuResourceDAO.getUsersByPageAndREVMenu(aclRes, aclOpe, pageNo, pageSize);
    return page;
  }
  
  public List getGroupsByAuthMenu(String resourceId, long resourceTypeId, String operationCode)
  {
    ACLResourceType aclResType = this.aclResourceService.loadResourceTypeById(resourceTypeId);
    ACLResource aclRes = this.aclResourceService.getResourceByTypeAndNativeId(aclResType, resourceId);
    ACLOperation aclOpe = this.aclResourceService.getOperationByCode(aclResType, operationCode);
    List result = this.roleService.findSecurityRoleWithResourceAndOperation(aclRes, aclOpe);
    List groups = new ArrayList();
    SecurityRole ele;
    Iterator iter;
    if ((result != null) && (result.size() > 0))
    {
      ele = null;
      for (iter = result.iterator(); iter.hasNext();)
      {
        ele = (SecurityRole)iter.next();
        if (ele.getName().startsWith("GROUP_"))
        {
          SecurityGroup group = this.userService.getGroupByName(ele.getName().substring(6));
          groups.add(group);
        }
      }
    }
    return groups;
  }
  
  public Page getGroupsByPageAndACLMenu(String resourceId, long resourceTypeId, String operationCode, int pageNo, int pageSize)
  {
    ACLResourceType aclResType = this.aclResourceService.loadResourceTypeById(resourceTypeId);
    ACLResource aclRes = this.aclResourceService.getResourceByTypeAndNativeId(aclResType, resourceId);
    ACLOperation aclOpe = this.aclResourceService.getOperationByCode(aclResType, operationCode);
    Page page = this.menuResourceDAO.getGroupsByPageAndACLMenu(aclRes, aclOpe, pageNo, pageSize);
    return page;
  }
  
  public Page getGroupsByPageAndREVMenu(String resourceId, long resourceTypeId, String operationCode, int pageNo, int pageSize)
  {
    ACLResourceType aclResType = this.aclResourceService.loadResourceTypeById(resourceTypeId);
    ACLResource aclRes = this.aclResourceService.getResourceByTypeAndNativeId(aclResType, resourceId);
    ACLOperation aclOpe = this.aclResourceService.getOperationByCode(aclResType, operationCode);
    Page page = this.menuResourceDAO.getGroupsByPageAndREVMenu(aclRes, aclOpe, pageNo, pageSize);
    return page;
  }
  
  public List getGroupsByREVMenu(String resourceId, long resourceTypeId, String operationCode)
  {
    ACLResourceType aclResType = this.aclResourceService.loadResourceTypeById(resourceTypeId);
    ACLResource aclRes = this.aclResourceService.getResourceByTypeAndNativeId(aclResType, resourceId);
    ACLOperation aclOpe = this.aclResourceService.getOperationByCode(aclResType, operationCode);
    return this.menuResourceDAO.getGroupsByREVMenu(aclRes, aclOpe);
  }
  
  public List getUsersByREVMenu(String resourceId, long resourceTypeId, String operationCode)
  {
    ACLResourceType aclResType = this.aclResourceService.loadResourceTypeById(resourceTypeId);
    ACLResource aclRes = this.aclResourceService.getResourceByTypeAndNativeId(aclResType, resourceId);
    ACLOperation aclOpe = this.aclResourceService.getOperationByCode(aclResType, operationCode);
    return this.menuResourceDAO.getUsersByREVMenu(aclRes, aclOpe);
  }
  
  public void addProxyToUser(ProxyInfo proxyInfo)
  {
    SecurityUser user = proxyInfo.getUser();
    SecurityUser proxy = proxyInfo.getProxy();
    if ((user == null) || (proxy == null)) {
      throw new SecurityException("User Principal not existed.");
    }
    if (proxy.equals(user)) {
      throw new SecurityException("Proxy can not be himself");
    }
    this.proxyInfoDAO.save(proxyInfo);
  }
  
  public SecurityUser[] getProxysByUser(SecurityUser user)
  {
    if (user == null) {
      throw new SecurityException("User Principal not existed.");
    }
    List result = new ArrayList();
    List proxyUsers = this.proxyInfoDAO.getProxysByUser(user);
    for (Iterator i = proxyUsers.iterator(); i.hasNext();)
    {
      SecurityUser proxyUser = (SecurityUser)i.next();
      if (result.indexOf(proxyUser) == -1) {
        result.add(proxyUser);
      }
    }
    return (SecurityUser[])result.toArray(new SecurityUser[result.size()]);
  }
  
  public Page getProxysByUser(SecurityUser user, int pageNo, int pageSize)
  {
    return this.proxyInfoDAO.getProxysByUser(user, pageNo, pageSize);
  }
  
  public ProxyInfo[] getProxyInfosByUserAndProxy(SecurityUser user, SecurityUser proxy)
  {
    List proxyInfos = this.proxyInfoDAO.getProxyInfosByUserAndProxy(user, proxy);
    return (ProxyInfo[])proxyInfos.toArray(new ProxyInfo[proxyInfos.size()]);
  }
  
  public void updateProxyInfo(ProxyInfo proxyInfo)
  {
    SecurityUser user = proxyInfo.getUser();
    SecurityUser proxy = proxyInfo.getProxy();
    if ((user == null) || (proxy == null)) {
      throw new SecurityException("User Principal not existed.");
    }
    if (proxy.equals(user)) {
      throw new SecurityException("Proxy can not be himself");
    }
    this.proxyInfoDAO.update(proxyInfo);
  }
  
  public SecurityUser[] getUsersByProxy(SecurityUser proxy, boolean isValid)
  {
    if (proxy == null) {
      throw new SecurityException("User Principal not existed.");
    }
    Date currentTime = new Date(System.currentTimeMillis());
    StringBuffer query = new StringBuffer("select proxyInfo.user from ProxyInfo proxyInfo where proxyInfo.proxy = :proxy");
    
    List args = new ArrayList();
    args.add(new HqlParameter("proxy", proxy));
    if (isValid)
    {
      query = query.append(" and proxyInfo.startTime <= :currentTime and proxyInfo.endTime > :currentTime");
      args.add(new HqlParameter("currentTime", currentTime));
    }
    List result = this.proxyInfoDAO.findByHQL(query.toString(), args);
    return (SecurityUser[])result.toArray(new SecurityUser[result.size()]);
  }
  
  public Page getUsersByProxy(SecurityUser proxy, boolean isValid, int pageNo, int pageSize)
  {
    if (proxy == null) {
      throw new SecurityException("User Principal not existed.");
    }
    Date currentTime = new Date(System.currentTimeMillis());
    StringBuffer query = new StringBuffer("select proxyInfo.user from ProxyInfo proxyInfo where proxyInfo.proxy = :proxy");
    
    List args = new ArrayList();
    args.add(new HqlParameter("proxy", proxy));
    if (isValid)
    {
      query = query.append(" and proxyInfo.startTime <= :currentTime and proxyInfo.endTime > :currentTime");
      args.add(new HqlParameter("currentTime", currentTime));
    }
    return this.proxyInfoDAO.findByHQLWithPage(query.toString(), args, pageNo, pageSize, null);
  }
  
  public Set getProxyMenusByProxyInfo(ProxyInfo proxyInfo)
  {
    return this.proxyInfoDAO.getProxyMenusByProxyInfo(proxyInfo);
  }
  
  public ProxyInfo loadProxyInfoById(long id)
  {
    return (ProxyInfo)this.proxyInfoDAO.loadWithLazy(new Long(id), new String[] { "user" });
  }
  
  public void removeProxyInfoById(long proxyInfoId)
  {
    this.proxyInfoDAO.deleteById(new Long(proxyInfoId));
  }
  
  public MenuResource getParentResourceById(long id)
  {
    MenuResource menuResource = (MenuResource)this.menuResourceDAO.load(new Long(id));
    return (MenuResource)menuResource.getParentResource();
  }
  
  public Resource getResourceById(String resourceId)
  {
    return getMenuResourceById(new Long(resourceId).longValue());
  }
  
  public Resource[] getTopResources()
  {
    DetachedCriteria criteria = DetachedCriteria.forClass(MenuResource.class);
    criteria.add(Restrictions.isNull("parentResource"));
    List result = this.menuResourceDAO.findByCriteria(criteria);
    return (MenuResource[])result.toArray(new MenuResource[result.size()]);
  }
  
  public Resource[] getChildResources(String parentResourceId)
  {
    DetachedCriteria criteria = DetachedCriteria.forClass(MenuResource.class);
    criteria.add(Restrictions.eq("parentResource", getResourceById(parentResourceId)));
    List result = this.menuResourceDAO.findByCriteria(criteria);
    return (MenuResource[])result.toArray(new MenuResource[result.size()]);
  }
}
