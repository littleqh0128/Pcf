package com.wondersgroup.framework.resource.connector.service;

import com.wondersgroup.framework.core.bo.Page;
import com.wondersgroup.framework.resource.connector.Resource;
import java.util.Map;

public abstract interface ResourceExtdService
{
  public abstract Resource[] getResource();
  
  public abstract Page getResource(Map paramMap1, Map paramMap2, int paramInt1, int paramInt2);
  
  public abstract Resource[] getAllParentResource(Resource paramResource);
  
  public abstract Resource getResourceById(String paramString);
  
  public abstract Resource[] getTopResources();
  
  public abstract Resource[] getChildResources(String paramString);
}
