package com.wondersgroup.framework.resource.connector.service.impl;

import com.wondersgroup.framework.core.bo.Page;
import com.wondersgroup.framework.resource.connector.Resource;
import com.wondersgroup.framework.resource.connector.service.ResourceExtdService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("ResourceExtService")
@Transactional
public abstract class AbstractResourceExtdServiceImpl implements ResourceExtdService
{
  public abstract Resource[] getResource();
  
  public abstract Page getResource(Map paramMap1, Map paramMap2, int paramInt1, int paramInt2);
  
  public Resource[] getAllParentResource(Resource resource)
  {
    List resourceList = new ArrayList();
    while (resource.getParentResource() != null)
    {
      resourceList.add(resource.getParentResource());
      resource = resource.getParentResource();
    }
    return (Resource[])resourceList.toArray(new Resource[resourceList.size()]);
  }
}
