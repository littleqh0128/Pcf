package com.wondersgroup.framework.security.service.impl;

import com.wondersgroup.framework.core.bo.Page;
import com.wondersgroup.framework.resource.bo.ResourceGroup;
import com.wondersgroup.framework.resource.connector.provider.GeneralResourceProvider;
import com.wondersgroup.framework.resource.connector.provider.impl.GeneralResourceProviderImpl;
import com.wondersgroup.framework.resource.service.ResourceGroupExtdService;
import com.wondersgroup.framework.security.bo.ACLOperation;
import com.wondersgroup.framework.security.bo.ACLResource;
import com.wondersgroup.framework.security.bo.ACLResourceType;
import com.wondersgroup.framework.security.bo.SecurityRole;
import com.wondersgroup.framework.security.dao.ACLOperationExtdDAO;
import com.wondersgroup.framework.security.dao.ACLResourceExtdDAO;
import com.wondersgroup.framework.security.dao.ACLResourceTypeExtdDAO;
import com.wondersgroup.framework.security.exception.SecurityException;
import com.wondersgroup.framework.security.service.ACLResourceExtdService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("ACLResourceExtService")
@Transactional
public class ACLResourceExtdServiceImpl implements ACLResourceExtdService, ApplicationContextAware
{
  @Autowired
  private ACLResourceExtdDAO aclResourceDAO;
  @Autowired
  private ACLResourceTypeExtdDAO aclResourceTypeDAO;
  @Autowired
  private ACLOperationExtdDAO aclOperationDAO;
  private ApplicationContext ctx;
  @Autowired
  private ResourceGroupExtdService resourceGroupService;

  public ACLResource loadResourceById(long id)
  {
    return (ACLResource)this.aclResourceDAO.load(new Long(id));
  }
  
  public void createNewResourceType(ACLResourceType type)
  {
    if (null == type) {
      throw new SecurityException("Resource Type not existed.");
    }
    if (this.aclResourceTypeDAO.findUniqueBy("code", type.getCode()) != null) {
      throw new SecurityException("AclResourceType code already existed");
    }
    this.aclResourceTypeDAO.save(type);
    refreshResourceProviderMap(type, "add", null);
  }
  
  public void updateResourceType(ACLResourceType type, String prevCode)
  {
    if (null == type) {
      throw new SecurityException("Resource Type not existed.");
    }
    this.aclResourceTypeDAO.update(type);
    refreshResourceProviderMap(type, "update", prevCode);
  }
  
  public void removeResourceType(ACLResourceType type)
  {
    if (null == type) {
      throw new SecurityException("Resource Type not existed.");
    }
    removeResourceType(type.getId());
    refreshResourceProviderMap(type, "remove", null);
  }
  
  public ACLResourceType getResourceTypeByName(String name)
  {
    return (ACLResourceType)this.aclResourceTypeDAO.findUniqueBy("name", name);
  }
  
  public ACLResourceType[] getAllResourceTypes()
  {
    List l = this.aclResourceTypeDAO.findAll();
    return (ACLResourceType[])l.toArray(new ACLResourceType[l.size()]);
  }
  
  public Page getAllResourceTypesByPage(Map filter, Map sort, int pageNo, int pageSize)
  {
    if (filter == null) {
      filter = new HashMap();
    }
    filter.put("removed", new Integer(0));
    return this.aclResourceTypeDAO.findResourceTypesByPage(filter, sort, pageNo, pageSize);
  }
  
  public ACLOperation[] getOperations(ACLResourceType type)
  {
    if (null == type) {
      throw new SecurityException("Resource Type not existed.");
    }
    List result = this.aclResourceTypeDAO.getOperations(type);
    return (ACLOperation[])result.toArray(new ACLOperation[result.size()]);
  }
  
  public ACLResourceType loadResourceTypeById(long id)
  {
    return (ACLResourceType)this.aclResourceTypeDAO.load(new Long(id));
  }
  
  public ACLResourceType loadResourceTypeByName(String name)
  {
    return (ACLResourceType)this.aclResourceTypeDAO.findUniqueBy("name", name);
  }
  
  public void addOperationToResourceType(ACLOperation op, ACLResourceType type)
  {
    if (null == op) {
      throw new SecurityException("Resource Operation not existed");
    }
    op.setAclResourceType(type);
    this.aclOperationDAO.save(op);
  }
  
  public void removeOperationFromResourceType(ACLOperation op, ACLResourceType type)
  {
    if (null == op) {
      throw new SecurityException("Resource Operation not existed");
    }
    if (null == type) {
      throw new SecurityException("Resource Type not existed.");
    }
    type = (ACLResourceType)this.aclResourceTypeDAO.loadWithLazy(new Long(type.getId()), new String[] { "aclOperations" });
    
    type.getAclOperations().remove(op);
    this.aclResourceTypeDAO.update(type);
  }
  
  public void removeOperation(ACLOperation aclOperation)
  {
    if (null == aclOperation) {
      throw new SecurityException("Resource Operation not existed");
    }
    this.aclOperationDAO.remove(aclOperation);
  }
  
  public ACLOperation[] getAllOperations()
  {
    List l = this.aclOperationDAO.findAll();
    return (ACLOperation[])l.toArray(new ACLOperation[l.size()]);
  }
  
  public ACLResource[] getAllResources()
  {
    List l = this.aclResourceDAO.findAll();
    return (ACLResource[])l.toArray(new ACLResource[l.size()]);
  }
  
  public ACLResourceType loadResourceTypeByIdWithLazy(long id, String[] propertyNames)
  {
    return (ACLResourceType)this.aclResourceTypeDAO.loadWithLazy(new Long(id), propertyNames);
  }
  
  public Page getOperationsByPage(ACLResourceType type, int pageNo, int pageSize)
  {
    if (null == type) {
      throw new SecurityException("Resource Type not existed.");
    }
    return this.aclResourceTypeDAO.getOperationsByPage(type, pageNo, pageSize);
  }
  
  public ACLOperation loadOperationById(long id)
  {
    return (ACLOperation)this.aclOperationDAO.load(new Long(id));
  }
  
  public void updateOperation(ACLOperation op)
  {
    this.aclOperationDAO.update(op);
  }
  
  public ACLResource getResourceByTypeAndNativeId(ACLResourceType type, String nativeId)
  {
    if (null == type) {
      throw new SecurityException("Resource Type not existed.");
    }
    return this.aclResourceDAO.getResourceByTypeAndNativeId(type, nativeId);
  }
  
  public ACLResource createNewACLResource(ACLResource resource)
  {
    if (null == resource) {
      throw new SecurityException("ACLResource not existed");
    }
    ACLResource aclResource = getResourceByTypeAndNativeId(resource.getAclResourceType(), resource.getNativeResourceId());
    if (aclResource == null)
    {
      this.aclResourceDAO.save(resource);
    }
    else
    {
      aclResource.setName(resource.getName());
      aclResource.setDescription(resource.getDescription());
      this.aclResourceDAO.update(aclResource);
    }
    return getResourceByTypeAndNativeId(resource.getAclResourceType(), resource.getNativeResourceId());
  }
  
  public void deleteResource(ACLResource aclResource)
  {
    if (null == aclResource) {
      throw new SecurityException("ACLResource not existed");
    }
    ACLResource resource = getResourceByTypeAndNativeId(aclResource.getAclResourceType(), aclResource.getNativeResourceId());
    if (resource != null) {
      this.aclResourceDAO.delete(resource);
    }
  }
  
  public void removeResourceType(long typeId)
  {
    ACLResourceType type = (ACLResourceType)this.aclResourceTypeDAO.loadWithLazy(new Long(typeId), new String[] { "aclOperations" });
    
    Set operations = type.getAclOperations();
    if (operations != null)
    {
      Iterator ite = operations.iterator();
      while (ite.hasNext()) {
        this.aclOperationDAO.remove((ACLOperation)ite.next());
      }
    }
    if (type.getCatalog() == 2)
    {
      ResourceGroup[] groups = this.resourceGroupService.getResourceGroupByType(type);
      if (groups.length > 0) {
        for (int i = 0; i < groups.length; i++)
        {
          groups[i].setType(null);
          this.resourceGroupService.removeResourceGroup(groups[i]);
        }
      }
    }
    this.aclResourceTypeDAO.remove(type);
  }
  
  public void setApplicationContext(ApplicationContext applicationContext)
    throws BeansException
  {
    this.ctx = applicationContext;
  }
  
  private void refreshResourceProviderMap(ACLResourceType type, String handleType, String prevCode)
  {
    if (type.getCatalog() != 1) {
      return;
    }
    GeneralResourceProvider generalResourceProvider = (GeneralResourceProviderImpl)this.ctx.getBean("generalResourceProviderImpl");
    
    BeanWrapper beanWrapper = new BeanWrapperImpl(generalResourceProvider);
    Map resMap = (Map)beanWrapper.getPropertyValue("resourceMap");
    String resType = type.getClassName().split(":")[1];
    Object classObj = null;
    if (resType.equals("S")) {
      try
      {
        classObj = this.ctx.getBean(type.getClassName().split(":")[0]);
      }
      catch (BeansException e) {}
    } else {
      classObj = type.getClassName().split(":")[0];
    }
    if (handleType.equals("add"))
    {
      resMap.put(type.getCode(), classObj);
    }
    else if (handleType.equals("remove"))
    {
      resMap.remove(type.getCode());
    }
    else if (handleType.equals("update"))
    {
      resMap.remove(prevCode);
      resMap.put(type.getCode(), classObj);
    }
  }
  
  public ACLOperation getOperationByCode(ACLResourceType resType, String code)
  {
    return this.aclOperationDAO.getOpertionByCode(resType, code);
  }
  
  public ACLResourceType getResourceTypeByCode(String code)
  {
    return (ACLResourceType)this.aclResourceTypeDAO.findUniqueBy("code", code);
  }
  
  public ACLResourceType[] getResourceTypesByCatalog(int catalog)
  {
    List allResourceTypes = this.aclResourceTypeDAO.findAll();
    List result = new ArrayList();
    for (Iterator iter = allResourceTypes.iterator(); iter.hasNext();)
    {
      ACLResourceType type = (ACLResourceType)iter.next();
      if (type.getCatalog() == catalog) {
        result.add(type);
      }
    }
    return (ACLResourceType[])result.toArray(new ACLResourceType[result.size()]);
  }
  
  public ACLResource[] getACLResourceByTypeAndRole(SecurityRole role, ACLResourceType type)
  {
    return this.aclResourceDAO.getACLResourceByTypeAndRole(role, type);
  }
  
  public ACLResource[] getRevokeACLResourceByTypeAndRole(SecurityRole role, ACLResourceType type)
  {
    return this.aclResourceDAO.getRevokeACLResourceByTypeAndRole(role, type);
  }
  
  public ACLResource[] getPublishACLResourceByTypeAndRole(SecurityRole role, ACLResourceType type)
  {
    return this.aclResourceDAO.getPublishACLResourceByTypeAndRole(role, type);
  }
  
  public List getRevokeOperationWithRoleAndResource(SecurityRole role, ACLResource resource)
  {
    return this.aclOperationDAO.getRevokeOperationWithRoleAndResource(role, resource);
  }
  
  public List getACLOperationWithRoleAndResource(SecurityRole role, ACLResource resource)
  {
    return this.aclOperationDAO.getACLOperationWithRoleAndResource(role, resource);
  }
  
  public List getPublishOperationWithRoleAndResource(SecurityRole role, ACLResource resource)
  {
    return this.aclOperationDAO.getPublishOperationWithRoleAndResource(role, resource);
  }
  
  public ACLResource[] getAclResourceByResourceType(long resourceTypeId)
  {
    return this.aclResourceDAO.getACLResourceByType(resourceTypeId);
  }
  
  public ACLResource[] getAclResourceByNativeResourceId(String nativeResourceId)
  {
    return this.aclResourceDAO.getAclResourceByNativeResourceId(nativeResourceId);
  }
}
