package com.wondersgroup.framework.organization.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wondersgroup.framework.organization.bo.OrganTree;
import com.wondersgroup.framework.organization.dao.OrganModelExtdDao;
import com.wondersgroup.framework.organization.dao.OrganTreeExtdDao;
import com.wondersgroup.framework.organization.exception.OrganException;
import com.wondersgroup.framework.organization.service.OrganTreeExtdService;
import com.wondersgroup.framework.security.bo.SecurityUser;

@Service("OrganTreeExtService")
@Transactional
public class OrganTreeExtdServiceImpl implements OrganTreeExtdService
{
  @Autowired
  private OrganTreeExtdDao organTreeDao;
  @Autowired
  private OrganModelExtdDao organModelDao;
  
  public OrganTree getOrganTreeByCode(String code)
  {
    return (OrganTree)this.organTreeDao.findUniqueBy("code", code);
  }
  
  public OrganTree[] getOrganTreeByName(String name)
  {
    return this.organTreeDao.getOrganTreeByName(name);
  }
  
  public OrganTree loadOrganTreeById(long id)
  {
    return (OrganTree)this.organTreeDao.load(new Long(id));
  }
  
  public void removeOrganTree(OrganTree organTree)
  {
    this.organTreeDao.removeOrganTree(organTree);
  }
  
  public void addOrganTree(OrganTree organTree)
  {
    if (organTree == null) {
      throw new OrganException("OrganTree not existed");
    }
    if (organTree.getTreeType() == null) {
      throw new OrganException("OrganTreeType not existed");
    }
    if (organTree.getCode() == null) {
      throw new OrganException("OrganTree code already existed");
    }
    this.organTreeDao.saveTree(organTree);
  }
  
  public void updateOrganTree(OrganTree organTree)
  {
    this.organTreeDao.update(organTree);
  }
  
  public boolean isAdminInTree(SecurityUser user, OrganTree tree)
  {
    return this.organModelDao.isAdminInTree(user, tree);
  }
}
