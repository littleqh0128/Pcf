package com.wondersgroup.framework.organization.dao.impl;

import com.wondersgroup.framework.core.bo.Page;
import com.wondersgroup.framework.core.bo.hibernate.HqlParameter;
import com.wondersgroup.framework.core.dao.impl.AbstractHibernateExtdDAOImpl;
import com.wondersgroup.framework.organization.bo.OrganModel;
import com.wondersgroup.framework.organization.bo.OrganNode;
import com.wondersgroup.framework.organization.bo.OrganTree;
import com.wondersgroup.framework.organization.dao.OrganModelExtdDao;
import com.wondersgroup.framework.organization.exception.OrganException;
import com.wondersgroup.framework.security.bo.SecurityRole;
import com.wondersgroup.framework.security.bo.SecurityUser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;

@Repository
public class OrganModelExtdDaoImpl extends AbstractHibernateExtdDAOImpl implements OrganModelExtdDao
{
  private int jdbcBatchSize;
  
  protected Class getEntityClass()
  {
    return OrganModel.class;
  }
  
  public void setJdbcBatchSize(int jdbcBatchSize)
  {
    this.jdbcBatchSize = jdbcBatchSize;
  }
  
  public OrganModel getOrganModelByNodeAndTree(OrganNode organNode, OrganTree organTree)
  {
    StringBuffer hql = new StringBuffer("from OrganModel model where model.removed=0 and model.organNode=? and model.organTree=? ");
    
    List result = getHibernateTemplate().find(hql.toString(), new Object[] { organNode, organTree });
    return (result != null) && (result.size() > 0) ? (OrganModel)result.get(0) : null;
  }
  
  public OrganModel[] getChildModels(OrganNode parentOrganNode, OrganTree organTree)
  {
    String hql = "from OrganModel m left join fetch m.organNode cn where m.organTree=? and m.parentNode=? and m.nodeStatus=? and m.removed=0 order by m.orders";
    List result = getHibernateTemplate().find(hql, new Object[] { organTree, parentOrganNode, "0" });
    
    return (result != null) && (result.size() > 0) ? (OrganModel[])result.toArray(new OrganModel[result.size()]) : null;
  }
  
  public OrganModel[] getAllChildModels(OrganModel parentOrganModel, OrganTree organTree)
  {
    String hql = "from OrganModel m left join fetch m.organNode cn where m.organTree=? and m.lft > ? and m.rgt < ? and m.removed=0";
    List result = getHibernateTemplate().find(hql, new Object[] { organTree, new Integer(parentOrganModel.getLft()), new Integer(parentOrganModel.getRgt()) });
    
    return (result != null) && (result.size() > 0) ? (OrganModel[])result.toArray(new OrganModel[result.size()]) : null;
  }
  
  public OrganModel loadOrganModelByNodeAndTree(OrganNode organNode, OrganTree organTree)
  {
    StringBuffer qryHql = new StringBuffer();
    qryHql.append("from OrganModel model where model.removed=0 and model.nodeStatus=?");
    if (organNode != null) {
      qryHql.append(" and model.organNode.id = " + organNode.getId() + " ");
    }
    if (organTree != null) {
      qryHql.append(" and model.organTree.id = " + organTree.getId() + " ");
    }
    Query query = getSession().createQuery(qryHql.toString());
    query.setParameter(0, "0");
    List res = query.list();
    return (res != null) && (res.size() > 0) ? (OrganModel)res.get(0) : null;
  }
  
  public OrganModel loadTotalOrganModelByNodeAndTree(OrganNode organNode, OrganTree organTree)
  {
    StringBuffer qryHql = new StringBuffer();
    qryHql.append("from OrganModel model left join fetch model.organNode cn left join fetch model.parentNode pn left join fetch model.organTree ot left join fetch model.roles roles where model.removed=0 and model.nodeStatus=?");
    if (organNode != null) {
      qryHql.append(" and model.organNode.id = " + organNode.getId() + " ");
    }
    if (organTree != null) {
      qryHql.append(" and model.organTree.id = " + organTree.getId() + " ");
    }
    Query query = getSession().createQuery(qryHql.toString());
    query.setParameter(0, "0");
    List res = query.list();
    return (res != null) && (res.size() > 0) ? (OrganModel)res.get(0) : null;
  }
  
  public OrganModel[] getChildModelsByName(OrganNode parentOrganNode, OrganTree organTree, String name)
  {
    String hql = "from OrganModel m left join fetch m.organNode cn where m.organTree=? and m.parentNode=? and m.removed=0 ";
    hql = hql + " and m.organNode.name like '%" + name + "%'";
    List result = getHibernateTemplate().find(hql, new Object[] { organTree, parentOrganNode });
    return (result != null) && (result.size() > 0) ? (OrganModel[])result.toArray(new OrganModel[result.size()]) : null;
  }
  
  public OrganModel[] getChildModelsTypeCode(OrganNode parentOrganNode, OrganTree organTree, String typeCode)
  {
    String hql = "from OrganModel m left join fetch m.organNode cn where m.organTree=? and m.parentNode=? and m.organNode.organNodeType.code=? and m.removed=0  order by m.orders";
    List result = getHibernateTemplate().find(hql, new Object[] { organTree, parentOrganNode, typeCode });
    return (result != null) && (result.size() > 0) ? (OrganModel[])result.toArray(new OrganModel[result.size()]) : null;
  }
  
  public OrganModel getParentModel(OrganNode childOrganNode, OrganTree organTree)
  {
    String hql = "from OrganModel m left join fetch m.parentNode cn where m.organTree=? and m.organNode=? and m.removed=0";
    List result = getHibernateTemplate().find(hql, new Object[] { organTree, childOrganNode });
    return (result != null) && (result.size() > 0) ? (OrganModel)result.get(0) : null;
  }
  
  public List findAllTreesByNode(OrganNode organNode)
  {
    StringBuffer qryHql = new StringBuffer();
    qryHql.append("select tree from OrganModel model join model.organTree tree where model.removed=0");
    if (organNode != null) {
      qryHql.append(" and model.organNode.id = " + organNode.getId() + " ");
    }
    Query query = getSession().createQuery(qryHql.toString());
    return query.list();
  }
  
  public OrganModel[] getBrotherModels(OrganNode parentOrganNode, OrganNode childOrganNode, OrganTree organTree)
  {
    String hql = "from OrganModel m left join fetch m.organNode cn where m.organTree=? and m.parentNode=? and m.organNode<>? and m.removed=0 order by m.orders";
    List result = getHibernateTemplate().find(hql, new Object[] { organTree, parentOrganNode, childOrganNode });
    
    return (result != null) && (result.size() > 0) ? (OrganModel[])result.toArray(new OrganModel[result.size()]) : null;
  }
  
  public SecurityRole loadDefaultRoleWithOrgan(OrganNode selectedNode, OrganTree organTree)
  {
    String roleName = "ORGAN_" + organTree.getCode() + "." + selectedNode.getCode();
    List roleList = super.getHibernateTemplate().find("select distinct role from SecurityRole as role where role.name = ?", roleName);
    
    return roleList.isEmpty() ? null : (SecurityRole)roleList.get(0);
  }
  
  public void moveNodeInSameTree(OrganNode sourceNode, OrganNode targetParentNode, OrganTree targetOrganTree)
  {
    OrganModel sourceOrganModel = loadOrganModelByNodeAndTree(sourceNode, targetOrganTree);
    OrganModel sourceParentOrganModel = getParentModel(sourceNode, targetOrganTree);
    OrganModel sourcePrevBrotherOrganModel = getPrevBrotherModel(sourceParentOrganModel.getOrganNode(), sourceOrganModel.getOrganNode(), targetOrganTree);
    
    sourceOrganModel.setParentNode(targetParentNode);
    save(sourceOrganModel);
    
    int lft = sourceOrganModel.getLft();
    int rgt = sourceOrganModel.getRgt();
    
    super.getHibernateTemplate().bulkUpdate("update OrganModel m set m.nodeStatus = ? where m.lft >= ? and m.rgt <= ? and m.organTree = ? and m.removed = 0", new Object[] { "1", new Integer(lft), new Integer(rgt), targetOrganTree });
    
    int dif = rgt + 1 - lft;
    if (sourcePrevBrotherOrganModel == null)
    {
      super.getHibernateTemplate().bulkUpdate("update OrganModel m set m.lft = m.lft - ? where m.lft > ? and m.organTree = ? and m.removed = 0 and m.nodeStatus = ?", new Object[] { new Integer(dif), new Integer(sourceParentOrganModel.getLft()), targetOrganTree, "0" });
      
      super.getHibernateTemplate().bulkUpdate("update OrganModel m set m.rgt = m.rgt - ? where m.rgt > ? and m.organTree = ? and m.removed = 0 and m.nodeStatus = ?", new Object[] { new Integer(dif), new Integer(sourceParentOrganModel.getLft()), targetOrganTree, "0" });
    }
    else
    {
      super.getHibernateTemplate().bulkUpdate("update OrganModel m set m.lft = m.lft - ? where m.lft > ? and m.organTree = ? and m.removed = 0 and m.nodeStatus = ?", new Object[] { new Integer(dif), new Integer(sourcePrevBrotherOrganModel.getLft()), targetOrganTree, "0" });
      
      super.getHibernateTemplate().bulkUpdate("update OrganModel m set m.rgt = m.rgt - ? where m.rgt > ? and m.organTree = ? and m.removed = 0 and m.nodeStatus = ?", new Object[] { new Integer(dif), new Integer(sourcePrevBrotherOrganModel.getRgt()), targetOrganTree, "0" });
    }
    getSession().clear();
    
    OrganModel targetParentOrganModel = loadOrganModelByNodeAndTree(targetParentNode, targetOrganTree);
    OrganModel[] targetChildOrganModels = getChildModels(targetParentNode, targetOrganTree);
    int offset = 0;
    if ((targetChildOrganModels == null) || (targetChildOrganModels.length == 0))
    {
      offset = lft - targetParentOrganModel.getLft() - 1;
      super.getHibernateTemplate().bulkUpdate("update OrganModel m set m.lft = m.lft + ? where m.lft > ? and m.organTree = ? and m.removed = 0 and m.nodeStatus = ?", new Object[] { new Integer(dif), new Integer(targetParentOrganModel.getLft()), targetOrganTree, "0" });
      
      super.getHibernateTemplate().bulkUpdate("update OrganModel m set m.rgt = m.rgt + ? where m.rgt > ? and m.organTree = ? and m.removed = 0 and m.nodeStatus = ?", new Object[] { new Integer(dif), new Integer(targetParentOrganModel.getLft()), targetOrganTree, "0" });
    }
    else
    {
      OrganModel lastTargetOrganModel = targetChildOrganModels[(targetChildOrganModels.length - 1)];
      offset = lft - lastTargetOrganModel.getLft() - 2;
      super.getHibernateTemplate().bulkUpdate("update OrganModel m set m.lft = m.lft + ? where m.lft > ? and m.organTree = ? and m.removed = 0 and m.nodeStatus = ?", new Object[] { new Integer(dif), new Integer(lastTargetOrganModel.getLft()), targetOrganTree, "0" });
      
      super.getHibernateTemplate().bulkUpdate("update OrganModel m set m.rgt = m.rgt + ? where m.rgt > ? and m.organTree = ? and m.removed = 0 and m.nodeStatus = ?", new Object[] { new Integer(dif), new Integer(lastTargetOrganModel.getRgt()), targetOrganTree, "0" });
    }
    super.getHibernateTemplate().bulkUpdate("update OrganModel m set m.lft = m.lft - ?, m.rgt = m.rgt - ?, m.nodeStatus = ? where m.organTree = ? and m.removed = 0 and m.nodeStatus = ?", new Object[] { new Integer(offset), new Integer(offset), "0", targetOrganTree, "1" });
    
    getSession().clear();
  }
  
  public void moveNodeBetweenDifferentTrees(OrganNode sourceNode, OrganTree sourceOrganTree, OrganNode targetParentNode, OrganTree targetOrganTree)
  {
    OrganModel sourceOrganModel = loadTotalOrganModelByNodeAndTree(sourceNode, sourceOrganTree);
    int lft = sourceOrganModel.getLft();
    int rgt = sourceOrganModel.getRgt();
    
    List sourceOrganModelList = new ArrayList();
    sourceOrganModelList.add(sourceOrganModel);
    Object[] childModels = getAllChildModelsWithTotalProperties(sourceOrganModel, sourceOrganTree);
    if (childModels != null) {
      sourceOrganModelList.addAll(Arrays.asList(childModels));
    }
    copyOrganModelEntity(OrganModel.class, sourceOrganModelList, targetParentNode, targetOrganTree);
    
    int dif = rgt + 1 - lft;
    OrganModel targetParentOrganModel = loadOrganModelByNodeAndTree(targetParentNode, targetOrganTree);
    OrganModel[] targetChildOrganModels = getChildModels(targetParentNode, targetOrganTree);
    int offset = 0;
    if ((targetChildOrganModels == null) || (targetChildOrganModels.length == 0))
    {
      offset = lft - targetParentOrganModel.getLft() - 1;
      super.getHibernateTemplate().bulkUpdate("update OrganModel m set m.lft = m.lft + ? where m.lft > ? and m.organTree = ? and m.removed = 0 and m.nodeStatus = ?", new Object[] { new Integer(dif), new Integer(targetParentOrganModel.getLft()), targetOrganTree, "0" });
      
      super.getHibernateTemplate().bulkUpdate("update OrganModel m set m.rgt = m.rgt + ? where m.rgt > ? and m.organTree = ? and m.removed = 0 and m.nodeStatus = ?", new Object[] { new Integer(dif), new Integer(targetParentOrganModel.getLft()), targetOrganTree, "0" });
    }
    else
    {
      OrganModel lastTargetOrganModel = targetChildOrganModels[(targetChildOrganModels.length - 1)];
      offset = lft - lastTargetOrganModel.getLft() - 2;
      super.getHibernateTemplate().bulkUpdate("update OrganModel m set m.lft = m.lft + ? where m.lft > ? and m.organTree = ? and m.removed = 0 and m.nodeStatus = ?", new Object[] { new Integer(dif), new Integer(lastTargetOrganModel.getLft()), targetOrganTree, "0" });
      
      super.getHibernateTemplate().bulkUpdate("update OrganModel m set m.rgt = m.rgt + ? where m.rgt > ? and m.organTree = ? and m.removed = 0 and m.nodeStatus = ?", new Object[] { new Integer(dif), new Integer(lastTargetOrganModel.getRgt()), targetOrganTree, "0" });
    }
    getSession().clear();
    
    super.getHibernateTemplate().bulkUpdate("update OrganModel m set m.lft = m.lft - ?, m.rgt = m.rgt - ?, m.nodeStatus = ?, m.organTree = ? where m.organTree = ? and m.removed = 0 and m.nodeStatus = ?", new Object[] { new Integer(offset), new Integer(offset), "0", targetOrganTree, sourceOrganTree, "2" });
    
    getSession().clear();
  }
  
  public void reorderOrganModel(OrganModel srcModel, OrganModel targetModel)
  {
    OrganTree organTree = srcModel.getOrganTree();
    int srcOffset = targetModel.getRgt() - targetModel.getLft() + 1;
    int targetOffset = srcModel.getRgt() - srcModel.getLft() + 1;
    
    super.getHibernateTemplate().bulkUpdate("update OrganModel m set m.lft = m.lft + ?, m.rgt = m.rgt + ? where m.lft >= ? and m.rgt <= ? and m.organTree = ? and m.removed = 0", new Object[] { new Integer(srcOffset), new Integer(srcOffset), new Integer(srcModel.getLft()), new Integer(srcModel.getRgt()), organTree });
    
    super.getHibernateTemplate().bulkUpdate("update OrganModel m set m.lft = m.lft - ?, m.rgt = m.rgt - ? where m.lft >= ? and m.rgt <= ? and m.organTree = ? and m.removed = 0", new Object[] { new Integer(targetOffset), new Integer(targetOffset), new Integer(targetModel.getLft()), new Integer(targetModel.getRgt()), organTree });
    
    getSession().clear();
  }
  
  public OrganModel getPrevBrotherModel(OrganNode parentOrganNode, OrganNode organNode, OrganTree targetOrganTree)
  {
    OrganModel organModel = loadOrganModelByNodeAndTree(organNode, targetOrganTree);
    Map filterMap = new HashMap();
    filterMap.put("parentNode", parentOrganNode);
    filterMap.put("organTree", targetOrganTree);
    filterMap.put("orders", new Long(organModel.getOrders() - 1L));
    List organModelList = super.findBy(filterMap);
    if (organModelList.size() == 1) {
      return (OrganModel)organModelList.get(0);
    }
    return null;
  }
  
  public void removeCascadeOrganModel(int lft, int rgt, OrganTree organTree)
  {
    super.getHibernateTemplate().bulkUpdate("update OrganModel m set m.removed=1 where m.organTree=? and m.lft >? and m.rgt <? and m.removed=0", new Object[] { organTree, new Integer(lft), new Integer(rgt) });
    
    getSession().clear();
  }
  
  public List findAllSuperiorNodes(OrganTree organTree, OrganNode organNode)
  {
    OrganModel sourceOrganModel = loadOrganModelByNodeAndTree(organNode, organTree);
    int lft = sourceOrganModel.getLft();
    int rgt = sourceOrganModel.getRgt();
    String hql = "from OrganModel m left join fetch m.organNode where m.organTree=? and m.lft<? and m.rgt>? and m.removed=0";
    List result = getHibernateTemplate().find(hql, new Object[] { organTree, new Integer(lft), new Integer(rgt) });
    
    return result != null ? result : null;
  }
  
  public long getMaxOrderOfChildrenNodes(OrganNode selectedNode, OrganTree organTree)
  {
    Query query = getSession().createQuery("select max(m.orders) from OrganModel m where m.organTree = ? and m.parentNode = ? ");
    
    query.setParameter(0, organTree);
    query.setParameter(1, selectedNode);
    Object obj = query.uniqueResult();
    if (obj == null) {
      return 0L;
    }
    return ((Long)query.uniqueResult()).longValue();
  }
  
  public OrganModel[] filterAllOrganModelArray(OrganModel[] adminOrganModels)
  {
    if (adminOrganModels.length > 0)
    {
      Collection result = new ArrayList();
      for (int i = 0; i < adminOrganModels.length; i++)
      {
        OrganModel adminModel = adminOrganModels[i];
        OrganModel[] childModels = getAllChildModels(adminModel, adminModel.getOrganTree());
        if (childModels != null) {
          result = CollectionUtils.union(result, Arrays.asList(childModels));
        }
        result.add(adminModel);
      }
      return (OrganModel[])result.toArray(new OrganModel[result.size()]);
    }
    return new OrganModel[0];
  }
  
  public OrganModel[] getAllAdminOrganModel(SecurityUser admin, OrganTree organTree)
  {
    String hql = "from OrganModel m left join fetch m.organNode n left join fetch m.organTree t left join fetch n.adminUser where m.removed=0 and n.adminUser = ? and m.organTree = ?";
    
    List result = getHibernateTemplate().find(hql, new Object[] { admin, organTree });
    return result != null ? (OrganModel[])result.toArray(new OrganModel[result.size()]) : new OrganModel[0];
  }
  
  public Page getAllUserInAdminNodeByPage(OrganModel[] allOrganModelArray, Map filter, int pageNo, int pageSize)
  {
    StringBuffer hql = new StringBuffer();
    StringBuffer countHql = new StringBuffer();
    
    hql.append("select distinct user from SecurityUser user inner join user.organNodes nodes inner join nodes.organModels models  where user.removed = 0 and models in (:models) ");
    
    countHql.append("select count(distinct user.id) from SecurityUser user inner join user.organNodes nodes inner join nodes.organModels models  where user.removed = 0 and models in (:models) ");
    
    List args = new ArrayList();
    args.add(new HqlParameter("models", Arrays.asList(allOrganModelArray)));
    if (filter == null) {
      filter = new HashMap();
    }
    String organId = (String)filter.get("organId");
    String nameValue = (String)filter.get("name");
    String userStatus = (String)filter.get("userStatus");
    
    String loginName = (String)filter.get("loginName");
    String sex = (String)filter.get("sex");
    String email = (String)filter.get("email");
    String address = (String)filter.get("address");
    String homePhone = (String)filter.get("honePhone");
    String officePhone = (String)filter.get("officePhone");
    String fax = (String)filter.get("fax");
    if ((organId != null) && (!organId.equalsIgnoreCase("")))
    {
      hql.append(" and nodes.id = ");
      hql.append(Integer.valueOf(organId));
      hql.append(" ");
    }
    if ((nameValue != null) && (!nameValue.equalsIgnoreCase("")))
    {
      hql.append(" and user.name like '%");
      hql.append(nameValue);
      
      hql.append("%' ");
    }
    if ((loginName != null) && (!loginName.equalsIgnoreCase("")))
    {
      hql.append(" and user.loginName like '%");
      hql.append(loginName);
      
      hql.append("%' ");
    }
    if ((email != null) && (!email.equalsIgnoreCase("")))
    {
      hql.append(" and user.email like '%");
      hql.append(email);
      
      hql.append("%' ");
    }
    if ((address != null) && (!address.equalsIgnoreCase("")))
    {
      hql.append(" and user.address like '%");
      hql.append(address);
      
      hql.append("%' ");
    }
    if ((homePhone != null) && (!homePhone.equalsIgnoreCase("")))
    {
      hql.append(" and user.honePhone like '%");
      hql.append(homePhone);
      
      hql.append("%' ");
    }
    if ((officePhone != null) && (!officePhone.equalsIgnoreCase("")))
    {
      hql.append(" and user.officePhone like '%");
      hql.append(officePhone);
      
      hql.append("%' ");
    }
    if ((fax != null) && (!fax.equalsIgnoreCase("")))
    {
      hql.append(" and user.fax like '%");
      hql.append(fax);
      
      hql.append("%' ");
    }
    if ((userStatus != null) && (!userStatus.equalsIgnoreCase("")))
    {
      hql.append(" and user.status = ");
      hql.append(Integer.valueOf(userStatus));
      hql.append(" ");
    }
    hql.append(" order by user.id");
    
    return super.findByHQLWithPage(hql.toString(), args, pageNo, pageSize, countHql.toString());
  }
  
  public boolean isAdminInTree(SecurityUser user, OrganTree organTree)
  {
    OrganModel rootModel = getRootNodeModel(organTree);
    String hql = "select count(m) from OrganModel m where m.removed = 0 and m.organNode.adminUser = ? and m.organTree = ? and m.lft >= ? and m.rgt <= ?";
    
    Query query = getSession().createQuery(hql);
    query.setParameter(0, user);
    query.setParameter(1, organTree);
    query.setParameter(2, new Integer(rootModel.getLft()));
    query.setParameter(3, new Integer(rootModel.getRgt()));
    Object obj = query.uniqueResult();
    if (obj == null) {
      return false;
    }
    return ((Number)obj).intValue() > 0;
  }
  
  public boolean judgetNodeExist(OrganNode organNode, OrganTree sourceOrganTree, OrganTree targetOrganTree)
  {
    OrganModel parentOrganModel = getOrganModelByNodeAndTree(organNode, sourceOrganTree);
    StringBuffer hql = new StringBuffer("select count(model) from OrganModel model left join model.organNode node").append(" where model.removed = 0 and model.organTree = :targetTree and node in").append(" (select n from OrganModel m left join m.organNode n").append(" where m.organTree = :sourceTree and m.lft >= :lft and m.rgt <= :rgt and m.removed = 0)");
    
    Query query = getSession().createQuery(hql.toString());
    query.setParameter("sourceTree", sourceOrganTree);
    query.setParameter("targetTree", targetOrganTree);
    query.setParameter("lft", Integer.valueOf(parentOrganModel.getLft()));
    query.setParameter("rgt", Integer.valueOf(parentOrganModel.getRgt()));
    Object obj = query.uniqueResult();
    if (obj == null) {
      return false;
    }
    return ((Number)obj).intValue() == 0;
  }
  
  private OrganModel getRootNodeModel(OrganTree organTree)
  {
    List modelList = super.getHibernateTemplate().find("select distinct model from OrganModel as model where model.organNode = ?", organTree.getRootNode());
    
    return (OrganModel)modelList.get(0);
  }
  
  private Object[] getAllChildModelsWithTotalProperties(OrganModel parentOrganModel, OrganTree organTree)
  {
    String hql = "from OrganModel m left join fetch m.organNode cn left join fetch m.parentNode pn left join fetch m.organTree ot left join fetch m.roles roles where m.organTree=? and m.lft > ? and m.rgt < ? and m.removed=0";
    
    List result = getHibernateTemplate().find(hql, new Object[] { organTree, new Integer(parentOrganModel.getLft()), new Integer(parentOrganModel.getRgt()) });
    
    return (result != null) && (result.size() > 0) ? (OrganModel[])result.toArray(new OrganModel[result.size()]) : null;
  }
  
  private OrganNode[] getAllChildNodes(OrganModel parentOrganModel, OrganTree organTree)
  {
    String hql = "select cn from OrganModel m left join m.organNode cn where m.organTree=? and m.lft > ? and m.rgt < ? and m.removed=0";
    List result = getHibernateTemplate().find(hql, new Object[] { organTree, new Integer(parentOrganModel.getLft()), new Integer(parentOrganModel.getRgt()) });
    
    return (result != null) && (result.size() > 0) ? (OrganNode[])result.toArray(new OrganNode[result.size()]) : null;
  }
  
  private void copyOrganModelEntity(Class entityClass, List sourceOrganModelList, OrganNode targetParentNode, OrganTree targetOrganTree)
  {
    try
    {
      Session session = getSessionFactory().getCurrentSession();
      int length = sourceOrganModelList.size();
      for (int i = 0; i < length; i++)
      {
        Object newEntity = entityClass.newInstance();
        OrganModel sourceModel = (OrganModel)sourceOrganModelList.get(i);
        BeanUtils.copyProperties(sourceModel, newEntity, new String[] { "id", "roles" });
        
        OrganModel targetModel = (OrganModel)newEntity;
        targetModel.setNodeStatus("2");
        
        Set ghostRoles = new HashSet();
        SecurityRole role = new SecurityRole();
        role.setName("ORGAN_" + targetOrganTree.getCode() + "." + sourceModel.getOrganNode().getCode());
        
        ghostRoles.add(role);
        targetModel.setRoles(ghostRoles);
        if (i == 0) {
          targetModel.setParentNode(targetParentNode);
        }
        save(targetModel);
        if (i % this.jdbcBatchSize == 0)
        {
          session.flush();
          session.clear();
        }
      }
      session.flush();
      session.clear();
    }
    catch (Exception ex)
    {
      throw new OrganException("New Entity Error!", ex);
    }
  }
  
  public Page getAllUsersByPage(String name, int pageNo, int pageSize)
  {
    DetachedCriteria criteria = DetachedCriteria.forClass(SecurityUser.class);
    criteria.add(Restrictions.ne("loginName", "admin"));
    if ((name != null) && (!name.equals(""))) {
      criteria.add(Restrictions.ilike("name", name));
    }
    criteria.add(Restrictions.eq("removed", new Integer(0)));
    criteria.add(Restrictions.eq("status", new Long(1L)));
    return super.findByCriteriaWithPage(criteria, pageNo, pageSize);
  }
}
