package com.wondersgroup.framework.core.extendproperty.service;

import com.wondersgroup.framework.core.extendproperty.bo.ExtendPropertyConfig;
import java.util.List;
import java.util.Map;

public abstract interface ExtendPropertyConfigExtdService
{
  public abstract ExtendPropertyConfig loadExtendPropertyConfig(long paramLong);
  
  public abstract void saveExtendPropertyConfig(ExtendPropertyConfig paramExtendPropertyConfig);
  
  public abstract void updateExtendPropertyConfig(ExtendPropertyConfig paramExtendPropertyConfig);
  
  public abstract void removeExtendPropertyConfig(ExtendPropertyConfig paramExtendPropertyConfig);
  
  public abstract List<ExtendPropertyConfig> getAllExtendPropertyConfigs();
  
  public abstract Map<String, String> getExtendPropertyNames(String paramString);
}
