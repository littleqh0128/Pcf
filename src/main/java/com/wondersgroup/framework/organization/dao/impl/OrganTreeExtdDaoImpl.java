package com.wondersgroup.framework.organization.dao.impl;

import com.wondersgroup.framework.core.dao.impl.AbstractHibernateExtdDAOImpl;
import com.wondersgroup.framework.organization.bo.OrganTree;
import com.wondersgroup.framework.organization.dao.OrganNodeExtdDao;
import com.wondersgroup.framework.organization.dao.OrganTreeExtdDao;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class OrganTreeExtdDaoImpl extends AbstractHibernateExtdDAOImpl implements OrganTreeExtdDao
{
  @Autowired
  private OrganNodeExtdDao organNodeDao;
  
  protected Class getEntityClass()
  {
    return OrganTree.class;
  }
  
  public OrganTree[] getOrganTreeByName(String name)
  {
    List result = new ArrayList();
    if ((name == null) || (name.equalsIgnoreCase(""))) {
      result = findAll();
    } else {
      result = findByLike("name", name);
    }
    return (OrganTree[])result.toArray(new OrganTree[result.size()]);
  }
  
  public void removeOrganTree(OrganTree organTree)
  {
    remove(organTree);
    if (organTree.getRootNode() != null) {
      this.organNodeDao.removeOrganNodeFromTree(organTree.getRootNode(), organTree, true);
    }
  }
  
  public void saveTree(OrganTree organTree)
  {
    if (organTree.getRootNode() != null) {
      this.organNodeDao.save(organTree.getRootNode());
    }
    super.save(organTree);
    if (organTree.getRootNode() != null) {
      this.organNodeDao.saveRootNode(organTree);
    }
  }
  
  public OrganTree getTreeWithRootNode(OrganTree organTree)
  {
    String hql = "from OrganTree tree left join fetch tree.rootNode as rootNode where tree.id=? and tree.removed=0";
    List res = findByHQL(hql, new Long(organTree.getId()));
    return (res != null) && (res.size() > 0) ? (OrganTree)res.get(0) : null;
  }
}
