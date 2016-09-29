package com.wondersgroup.framework.resource.service.impl;

import com.wondersgroup.framework.core.bo.Page;
import com.wondersgroup.framework.resource.bo.ResourceGroup;
import com.wondersgroup.framework.resource.connector.Resource;
import com.wondersgroup.framework.resource.connector.filter.MapResourceFilter;
import com.wondersgroup.framework.resource.dao.ResourceGroupExtdDAO;
import com.wondersgroup.framework.resource.exception.ResourceException;
import com.wondersgroup.framework.resource.service.ResourceGroupExtdService;
import com.wondersgroup.framework.security.bo.ACLResourceType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("ResourceGroupExtdService")
@Transactional
public class ResourceGroupExtdServiceImpl implements ResourceGroupExtdService, ApplicationContextAware
{
  @Autowired
  private ResourceGroupExtdDAO resourceGroupDAO;
  private ApplicationContext applicationContext;
  
  public void setApplicationContext(ApplicationContext applicationContext)
  {
    this.applicationContext = applicationContext;
  }
  
  public Resource[] getAllResource(ResourceGroup resGroup)
  {
    String className = resGroup.getClassName();
    String strFilter = resGroup.getPropValues();
    String[] propValues = strFilter.split(";");
    Map filter = new HashMap();
    for (int i = 0; i < propValues.length; i++)
    {
      String[] propValue = propValues[i].split(",");
      filter.put(propValue[0], propValue[1]);
    }
    Resource[] result = null;
    try
    {
      Object object = this.applicationContext.getBean(className);
      if (MapResourceFilter.class.isInstance(object))
      {
        MapResourceFilter resourceFilter = (MapResourceFilter)object;
        resourceFilter.setFilter(filter);
        result = resourceFilter.getResource();
      }
    }
    catch (Exception e)
    {
      throw new ResourceException(e.getMessage());
    }
    return result;
  }
  
  public ResourceGroup loadResourceGroupById(long id)
  {
    return (ResourceGroup)this.resourceGroupDAO.load(new Long(id));
  }
  
  public void addResourceGroup(ResourceGroup resGroup)
  {
    if (resGroup == null) {
      throw new ResourceException("ResourceGroup not existed");
    }
    if (getResourceGroupByCode(resGroup.getCode()) != null) {
      throw new ResourceException("ResourceGroup code already existed");
    }
    this.resourceGroupDAO.save(resGroup);
  }
  
  public void removeResourceGroup(ResourceGroup resGroup)
  {
    this.resourceGroupDAO.remove(resGroup);
  }
  
  public void updateResourceGroup(ResourceGroup resGroup)
  {
    this.resourceGroupDAO.update(resGroup);
  }
  
  public ResourceGroup[] getAllResourceGroups()
  {
    List result = this.resourceGroupDAO.findAll();
    return (ResourceGroup[])result.toArray(new ResourceGroup[result.size()]);
  }
  
  public Page getResourceGroupsByPage(Map filter, Map sort, int pageNo, int pageSize)
  {
    return this.resourceGroupDAO.findByWithPage(filter, sort, pageNo, pageSize);
  }
  
  public ResourceGroup getResourceGroupByCode(String code)
  {
    return (ResourceGroup)this.resourceGroupDAO.findUniqueBy("code", code);
  }
  
  public ResourceGroup[] getResourceGroupByType(ACLResourceType resourceType)
  {
    if (resourceType == null) {
      throw new ResourceException("Resource Type not existed");
    }
    if (resourceType.getCatalog() != 2) {
      throw new ResourceException("ResourceType not match DynamicResource");
    }
    List result = this.resourceGroupDAO.findBy("type", resourceType);
    return (ResourceGroup[])result.toArray(new ResourceGroup[result.size()]);
  }
  
  public boolean isResourceInGroup(String resourceId, ResourceGroup resourceGroup)
  {
    Resource[] resources = getAllResource(resourceGroup);
    for (int i = 0; i < resources.length; i++) {
      if (resources[i].getResourceId().equals(resourceId)) {
        return true;
      }
    }
    return false;
  }
  
  public Page getResourceGroupsByTypeByPage(ACLResourceType resourceType, int pageNo, int pageSize)
  {
    return this.resourceGroupDAO.getResourceGroupsByTypeByPage(resourceType, pageNo, pageSize);
  }
}
