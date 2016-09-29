package com.wondersgroup.framework.core.extendproperty.service.impl;

import com.wondersgroup.framework.core.extendproperty.bo.ExtendPropertyConfig;
import com.wondersgroup.framework.core.extendproperty.dao.ExtendPropertyConfigExtdDao;
import com.wondersgroup.framework.core.extendproperty.service.ExtendPropertyConfigExtdService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("ExtendPropertyConfigExtdService")
@Transactional
public class ExtendPropertyConfigExtdServiceImpl implements ExtendPropertyConfigExtdService
{
  private ExtendPropertyConfigExtdDao extendPropertyConfigDao;
  
  public void setExtendPropertyConfigDao(ExtendPropertyConfigExtdDao extendPropertyConfigDao)
  {
    this.extendPropertyConfigDao = extendPropertyConfigDao;
  }
  
  public List<ExtendPropertyConfig> getAllExtendPropertyConfigs()
  {
    return this.extendPropertyConfigDao.getAllExtendPropertyConfig();
  }
  
  public ExtendPropertyConfig loadExtendPropertyConfig(long id)
  {
    return (ExtendPropertyConfig)this.extendPropertyConfigDao.load(new Long(id));
  }
  
  public Map<String, String> getExtendPropertyNames(String className)
  {
    ExtendPropertyConfig config = this.extendPropertyConfigDao.getExtendPropertyConfigByClass(className);
    if (config != null) {
      return config.getExtendPropertyNames();
    }
    return new HashMap();
  }
  
  public void removeExtendPropertyConfig(ExtendPropertyConfig config)
  {
    this.extendPropertyConfigDao.delete(config);
  }
  
  public void saveExtendPropertyConfig(ExtendPropertyConfig config)
  {
    this.extendPropertyConfigDao.save(config);
  }
  
  public void updateExtendPropertyConfig(ExtendPropertyConfig config)
  {
    this.extendPropertyConfigDao.update(config);
  }
}
