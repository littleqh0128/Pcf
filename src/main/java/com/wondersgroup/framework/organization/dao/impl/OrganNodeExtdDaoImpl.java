package com.wondersgroup.framework.organization.dao.impl;

import com.wondersgroup.framework.core.bo.hibernate.HqlParameter;
import com.wondersgroup.framework.core.dao.impl.AbstractHibernateExtdDAOImpl;
import com.wondersgroup.framework.core.exception.DAOException;
import com.wondersgroup.framework.organization.bo.OrganModel;
import com.wondersgroup.framework.organization.bo.OrganNode;
import com.wondersgroup.framework.organization.bo.OrganTree;
import com.wondersgroup.framework.organization.dao.OrganModelExtdDao;
import com.wondersgroup.framework.organization.dao.OrganNodeExtdDao;
import com.wondersgroup.framework.security.bo.SecurityRole;
import com.wondersgroup.framework.security.bo.SecurityUser;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class OrganNodeExtdDaoImpl extends AbstractHibernateExtdDAOImpl implements OrganNodeExtdDao
{
  @Autowired
  private OrganModelExtdDao organModelDao;
  
  protected Class getEntityClass()
  {
    return OrganNode.class;
  }
  
  public List findAllRootTypeNodes()
  {
    StringBuffer qryHql = new StringBuffer();
    qryHql.append("select node from OrganNode as node join node.organNodeType as type where node.removed=0 ");
    qryHql.append(" and type.top=1 ");
    
    Query query = getSession().createQuery(qryHql.toString());
    return query.list();
  }
  
  public OrganNode loadByCode(String code)
  {
    String hql = "from OrganNode node where node.code=? and node.removed=0";
    List res = findByHQL(hql, code);
    return (res != null) && (res.size() > 0) ? (OrganNode)res.get(0) : null;
  }
  
  public OrganNode getOrganNodeByCode(String code)
  {
    String hql = "from OrganNode node where node.code=? and node.removed=0 ";
    List result = findByHQL(hql, code);
    return (result != null) && (result.size() > 0) ? (OrganNode)result.get(0) : null;
  }
  
  public OrganNode[] getOrganNodesByUserId(long userId)
  {
    String hql = "select user.organNodes from SecurityUser user where user.id=?  and user.removed = 0";
    List result = findByHQL(hql, new Long(userId));
    return (OrganNode[])result.toArray(new OrganNode[result.size()]);
  }
  
  public OrganNode[] getOrganNodesByUserAndTree(long userId, String treeCode)
  {
    String hql = "select node from OrganModel m left join m.organNode node left join node.users user where user.id = :userId and m.organTree.code = :treeCode";
    
    List<HqlParameter> args = new ArrayList();
    args.add(new HqlParameter("userId", Long.valueOf(userId)));
    args.add(new HqlParameter("treeCode", treeCode));
    List result = findByHQL(hql, args);
    return (result != null) && (result.size() > 0) ? (OrganNode[])result.toArray(new OrganNode[result.size()]) : null;
  }
  
  public OrganNode[] getOrganNodesByTypeCode(String typeCode)
  {
    String hql = "from OrganNode node where node.organNodeType.code = ? and node.removed=0";
    List result = findByHQL(hql, typeCode);
    return (OrganNode[])result.toArray(new OrganNode[result.size()]);
  }
  
  public OrganNode[] getOrganNodesByTypeCodeWithoutTree(String typeCode, OrganTree organTree)
  {
    StringBuffer hql = new StringBuffer("select distinct node from OrganModel model join model.organNode node").append(" where node.organNodeType.code = :typeCode and node.removed = 0").append(" and ((model.organTree.id <> :treeId and node.id not in").append(" (select mm.organNode.id from OrganModel mm where mm.organTree.id = :treeId and mm.removed=0))").append(" or (model.organTree.id = :treeId and model.removed = 1))");
    
    Query query = getSession().createQuery(hql.toString());
    query.setParameter("typeCode", typeCode);
    query.setParameter("treeId", Long.valueOf(organTree.getId()));
    List result = query.list();
    return (OrganNode[])result.toArray(new OrganNode[result.size()]);
  }
  
  public void addOrganNodeToTree(OrganNode newNode, OrganNode selectedNode, OrganTree organTree)
  {
    String countHql = "select count(*) from OrganNode node where node = " + newNode.getId();
    int count = countByHQL(countHql);
    if (count == 0) {
      save(newNode);
    }
    int rgt = 0;
    OrganModel model = new OrganModel();
    if (selectedNode != null)
    {
      OrganNode[] childOrganNodes = getChildNodes(selectedNode, organTree);
      if ((childOrganNodes == null) || (childOrganNodes.length == 0))
      {
        OrganModel lastOrganModel = this.organModelDao.loadOrganModelByNodeAndTree(selectedNode, organTree);
        rgt = lastOrganModel.getRgt() - 1;
      }
      else
      {
        OrganNode lastOrganNode = childOrganNodes[(childOrganNodes.length - 1)];
        OrganModel lastOrganModel = this.organModelDao.loadOrganModelByNodeAndTree(lastOrganNode, organTree);
        rgt = lastOrganModel.getRgt();
      }
      super.getHibernateTemplate().bulkUpdate("update OrganModel m set m.rgt = m.rgt + 2 where m.rgt > ? and m.organTree = ? and m.removed = 0", new Object[] { new Integer(rgt), organTree });
      
      super.getHibernateTemplate().bulkUpdate("update OrganModel m set m.lft = m.lft + 2 where m.lft > ? and m.organTree = ? and m.removed = 0", new Object[] { new Integer(rgt), organTree });
      
      super.getSession().clear();
      model.setOrganTree(organTree);
      model.setParentNode(selectedNode);
      model.setOrganNode(newNode);
      
      model.setLft(rgt + 1);
      model.setRgt(rgt + 2);
      
      model.setOrders(this.organModelDao.getMaxOrderOfChildrenNodes(selectedNode, organTree) + 1L);
    }
    else
    {
      model.setOrganTree(organTree);
      model.setOrganNode(newNode);
      model.setLft(rgt + 1);
      model.setRgt(rgt + 2);
      model.setOrders(1L);
    }
    List arguments = new ArrayList();
    HqlParameter param1 = new HqlParameter();
    param1.setName("name");
    param1.setValue("ORGAN_" + organTree.getCode() + "." + newNode.getCode());
    arguments.add(param1);
    String countRoleHql = "select count(*) from SecurityRole role where role.name = :name";
    int countRole = countByHQL(countRoleHql, arguments);
    if (countRole == 0)
    {
      SecurityRole role = new SecurityRole();
      role.setName("ORGAN_" + organTree.getCode() + "." + newNode.getCode());
      model.getRoles().add(role);
    }
    else
    {
      String hql = "from SecurityRole role where role.name = ?";
      List roleList = findByHQL(hql, "ORGAN_" + organTree.getCode() + "." + newNode.getCode());
      
      SecurityRole role = (SecurityRole)roleList.get(0);
      model.getRoles().add(role);
    }
    this.organModelDao.save(model);
  }
  
  public void removeOrganNodeFromTree(OrganNode organNode, OrganTree organTree, boolean cascade)
  {
    organNode = (OrganNode)super.load(new Long(organNode.getId()));
    organNode.getUsers().clear();
    super.save(organNode);
    
    OrganModel model = this.organModelDao.loadOrganModelByNodeAndTree(organNode, organTree);
    int lft = model.getLft();
    int rgt = model.getRgt();
    int dif = rgt + 1 - lft;
    this.organModelDao.remove(model);
    
    getSession().flush();
    if (cascade) {
      this.organModelDao.removeCascadeOrganModel(lft, rgt, organTree);
    }
    super.getHibernateTemplate().bulkUpdate("update OrganModel m set m.rgt = m.rgt - ? where m.rgt > ? and m.organTree = ? and m.removed = 0", new Object[] { new Integer(dif), new Integer(rgt), organTree });
    
    super.getHibernateTemplate().bulkUpdate("update OrganModel m set m.lft = m.lft - ? where m.lft > ? and m.organTree = ? and m.removed = 0", new Object[] { new Integer(dif), new Integer(rgt), organTree });
    
    super.getSession().clear();
  }
  
  public OrganNode[] getChildNodes(OrganNode parentOrganNode, OrganTree organTree)
  {
    OrganModel[] organModels = this.organModelDao.getChildModels(parentOrganNode, organTree);
    List result = new ArrayList();
    if ((organModels != null) && (organModels.length > 0)) {
      for (int i = 0; i < organModels.length; i++) {
        result.add(organModels[i].getOrganNode());
      }
    }
    return (result != null) && (result.size() > 0) ? (OrganNode[])result.toArray(new OrganNode[result.size()]) : null;
  }
  
  public OrganNode[] getAllChildNodes(OrganNode parentNode, OrganTree organTree)
  {
    OrganModel organParentModel = this.organModelDao.loadOrganModelByNodeAndTree(parentNode, organTree);
    OrganModel[] organModels = this.organModelDao.getAllChildModels(organParentModel, organTree);
    List result = new ArrayList();
    if ((organModels != null) && (organModels.length > 0)) {
      for (int i = 0; i < organModels.length; i++) {
        result.add(organModels[i].getOrganNode());
      }
    }
    return (result != null) && (result.size() > 0) ? (OrganNode[])result.toArray(new OrganNode[result.size()]) : null;
  }
  
  public OrganNode getParentNode(OrganNode childOrganNode, OrganTree organTree)
  {
    OrganModel organModel = this.organModelDao.getParentModel(childOrganNode, organTree);
    return (organModel != null) && (!organModel.equals("")) ? organModel.getParentNode() : null;
  }
  
  public OrganNode[] getChildNodesByName(OrganNode parentOrganNode, OrganTree organTree, String name)
  {
    OrganModel[] organModels = this.organModelDao.getChildModelsByName(parentOrganNode, organTree, name);
    List result = new ArrayList();
    if ((organModels != null) && (organModels.length > 0)) {
      for (int i = 0; i < organModels.length; i++) {
        result.add(organModels[i].getOrganNode());
      }
    }
    return (result != null) && (result.size() > 0) ? (OrganNode[])result.toArray(new OrganNode[result.size()]) : null;
  }
  
  public OrganNode[] getChildNodesByTypeCode(OrganNode parentOrganNode, OrganTree organTree, String typeCode)
  {
    OrganModel[] organModels = this.organModelDao.getChildModelsTypeCode(parentOrganNode, organTree, typeCode);
    List result = new ArrayList();
    if ((organModels != null) && (organModels.length > 0)) {
      for (int i = 0; i < organModels.length; i++) {
        result.add(organModels[i].getOrganNode());
      }
    }
    return (result != null) && (result.size() > 0) ? (OrganNode[])result.toArray(new OrganNode[result.size()]) : null;
  }
  
  public OrganNode[] getBrotherNodesByTree(OrganNode childOrganNode, OrganTree organTree)
  {
    OrganNode parentOrganNode = getParentNode(childOrganNode, organTree);
    OrganModel[] organModels = this.organModelDao.getBrotherModels(parentOrganNode, childOrganNode, organTree);
    List result = new ArrayList();
    if ((organModels != null) && (organModels.length > 0)) {
      for (int i = 0; i < organModels.length; i++) {
        result.add(organModels[i].getOrganNode());
      }
    }
    return (result != null) && (result.size() > 0) ? (OrganNode[])result.toArray(new OrganNode[result.size()]) : null;
  }
  
  public OrganNode loadOrganNodeWithUsers(long id)
  {
    String hql = "from OrganNode node left join fetch node.users as users where node.id=? and node.removed=0";
    List res = findByHQL(hql, new Long(id));
    return (res != null) && (res.size() > 0) ? (OrganNode)res.get(0) : null;
  }
  
  public OrganNode loadOrganNodeWithType(OrganNode organNode)
  {
    String hql = "from OrganNode node left join fetch node.organNodeType as type where node.id=? and node.removed=0";
    List res = findByHQL(hql, new Long(organNode.getId()));
    return (res != null) && (res.size() > 0) ? (OrganNode)res.get(0) : null;
  }
  
  public void saveRootNode(OrganTree organTree)
  {
    OrganNode rootNode = organTree.getRootNode();
    String countHql = "select count(*) from OrganNode node where node.id = " + rootNode.getId();
    int count = countByHQL(countHql);
    if (count == 0) {
      save(rootNode);
    }
    OrganModel model = new OrganModel();
    model.setOrganTree(organTree);
    model.setOrganNode(rootNode);
    model.setLft(1);
    model.setRgt(2);
    model.setOrders(1L);
    SecurityRole role = new SecurityRole();
    role.setName("ORGAN_" + organTree.getCode() + "." + rootNode.getCode());
    model.getRoles().add(role);
    this.organModelDao.save(model);
  }
  
  public OrganNode loadByCodeWithLazy(String code, String[] propertyNames)
  {
    OrganNode organNode = loadByCode(code);
    BeanWrapper beanWrapper = new BeanWrapperImpl(organNode);
    for (int i = 0; i < propertyNames.length; i++) {
      try
      {
        Object result = beanWrapper.getPropertyValue(propertyNames[i]);
        Iterator it;
        if ((Collection.class.isInstance(result)) && (result != null))
        {
          Collection c = (Collection)result;
          for (it = c.iterator(); it.hasNext();) {
            Hibernate.initialize(it.next());
          }
        }
        else
        {
          Hibernate.initialize(result);
        }
      }
      catch (HibernateException e)
      {
        throw new DAOException("Can not lazyload " + getEntityClassName() + " with " + code, e);
      }
    }
    return organNode;
  }
  
  public OrganNode[] getAllParentNodeByTypeCode(OrganTree organTree, OrganNode organNode, String typeCode)
  {
    OrganModel sourceOrganModel = this.organModelDao.loadOrganModelByNodeAndTree(organNode, organTree);
    List result = new ArrayList();
    int rgt = 0;
    if (sourceOrganModel != null)
    {
      int lft = sourceOrganModel.getLft();
      rgt = sourceOrganModel.getRgt();
      String hql = "select node from OrganModel m left join m.organNode node left join node.organNodeType type where m.organTree=? and m.lft<? and m.rgt>? and node.organNodeType.code=? and m.removed=0";
      result = getHibernateTemplate().find(hql, new Object[] { organTree, new Integer(lft), new Integer(rgt), typeCode });
    }
    return (result != null) && (result.size() > 0) ? (OrganNode[])result.toArray(new OrganNode[result.size()]) : null;
  }
  
  public OrganNode[] getAllParentNodesByTypeCode(OrganNode organNode, String typeCode)
  {
    List<OrganNode> result = new ArrayList();
    String hql = "select m from OrganModel m left join m.organTree tree where m.removed = 0 and m.nodeStatus = :status" + " and m.organNode = :node";
    
    List<HqlParameter> args = new ArrayList();
    args.add(new HqlParameter("status", "0"));
    args.add(new HqlParameter("node", organNode));
    List<OrganModel> list = findByHQL(hql, args);
    hql = "select node from OrganModel m left join m.organNode node left join node.organNodeType type" + " where m.organTree = :tree and m.lft < :lft and m.rgt > :rgt and node.organNodeType.code = :code and m.removed=0";
    for (OrganModel organModel : list)
    {
      int lft = organModel.getLft();
      int rgt = organModel.getRgt();
      args = new ArrayList();
      args.add(new HqlParameter("tree", organModel.getOrganTree()));
      args.add(new HqlParameter("lft", Integer.valueOf(lft)));
      args.add(new HqlParameter("rgt", Integer.valueOf(rgt)));
      args.add(new HqlParameter("code", typeCode));
      result.addAll(findByHQL(hql, args));
    }
    return (result != null) && (result.size() > 0) ? (OrganNode[])result.toArray(new OrganNode[result.size()]) : null;
  }
  
  public OrganNode[] getAllSubNodesByTypeCode(OrganNode organNode, String typeCode)
  {
    List<OrganNode> result = new ArrayList();
    String hql = "select m from OrganModel m left join m.organTree tree where m.removed = 0 and m.nodeStatus = :status" + " and m.organNode = :node";
    
    List<HqlParameter> args = new ArrayList();
    args.add(new HqlParameter("status", "0"));
    args.add(new HqlParameter("node", organNode));
    List<OrganModel> list = findByHQL(hql, args);
    hql = "select node from OrganModel m left join m.organNode node left join node.organNodeType type" + " where m.organTree = :tree and m.lft > :lft and m.rgt < :rgt and node.organNodeType.code = :code and m.removed=0";
    for (OrganModel organModel : list)
    {
      int lft = organModel.getLft();
      int rgt = organModel.getRgt();
      args = new ArrayList();
      args.add(new HqlParameter("tree", organModel.getOrganTree()));
      args.add(new HqlParameter("lft", Integer.valueOf(lft)));
      args.add(new HqlParameter("rgt", Integer.valueOf(rgt)));
      args.add(new HqlParameter("code", typeCode));
      result.addAll(findByHQL(hql, args));
    }
    return (result != null) && (result.size() > 0) ? (OrganNode[])result.toArray(new OrganNode[result.size()]) : null;
  }
  
  public OrganNode[] getUserOrganNodesByTypeCode(SecurityUser user, String typeCode)
  {
    String hql = "select node from SecurityUser user left join user.organNodes node where user = :user and node.organNodeType.code = :code and node.removed = 0";
    List<HqlParameter> args = new ArrayList();
    args.add(new HqlParameter("user", user));
    args.add(new HqlParameter("code", typeCode));
    List result = findByHQL(hql, args);
    return (result != null) && (result.size() > 0) ? (OrganNode[])result.toArray(new OrganNode[result.size()]) : null;
  }
}
