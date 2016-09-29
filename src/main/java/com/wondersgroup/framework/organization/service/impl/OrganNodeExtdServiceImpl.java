package com.wondersgroup.framework.organization.service.impl;

import com.wondersgroup.framework.config.SystemParameter;
import com.wondersgroup.framework.core.bo.Page;
import com.wondersgroup.framework.core.bo.hibernate.HqlParameter;
import com.wondersgroup.framework.core.extendproperty.service.ExtendPropertyConfigExtdService;
import com.wondersgroup.framework.ldap.LDAPFactory;
import com.wondersgroup.framework.ldap.exception.LDAPException;
import com.wondersgroup.framework.ldap.model.LDAPOrgan;
import com.wondersgroup.framework.ldap.model.LDAPUser;
import com.wondersgroup.framework.organization.bo.OrganExtendProperty;
import com.wondersgroup.framework.organization.bo.OrganModel;
import com.wondersgroup.framework.organization.bo.OrganNode;
import com.wondersgroup.framework.organization.bo.OrganNodeType;
import com.wondersgroup.framework.organization.bo.OrganTree;
import com.wondersgroup.framework.organization.dao.NodeUserExtdDAO;
import com.wondersgroup.framework.organization.dao.OrganExtendPropertyExtdDao;
import com.wondersgroup.framework.organization.dao.OrganModelExtdDao;
import com.wondersgroup.framework.organization.dao.OrganNodeExtdDao;
import com.wondersgroup.framework.organization.exception.OrganException;
import com.wondersgroup.framework.organization.service.OrganNodeExtdService;
import com.wondersgroup.framework.organization.service.OrganRuleExtdService;
import com.wondersgroup.framework.security.bo.SecurityGroup;
import com.wondersgroup.framework.security.bo.SecurityRole;
import com.wondersgroup.framework.security.bo.SecurityUser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("OrganNodeExtService")
@Transactional
public class OrganNodeExtdServiceImpl implements OrganNodeExtdService
{
  @Autowired
  private OrganNodeExtdDao organNodeDao;
  @Autowired
  private OrganModelExtdDao organModelDao;
  @Autowired
  private OrganRuleExtdService organRuleService;
  @Autowired
  private NodeUserExtdDAO nodeUserDAO;
  @Autowired
  private OrganExtendPropertyExtdDao organExtendPropertyDao;
  @Autowired
  private ExtendPropertyConfigExtdService extendPropertyConfigService;
  
  public boolean isAdminForOrganNodes(OrganNode organNode, SecurityUser user)
  {
    return organNode.getAdminUser().getId() == user.getId();
  }
  
  public void setAdminForOrganNode(OrganNode organNode, SecurityUser user)
  {
    organNode.setAdminUser(user);
    this.organNodeDao.update(organNode);
  }
  
  public boolean isControledNodeByUser(OrganTree organTree, OrganNode organNode, SecurityUser user)
  {
    if ((organNode.getAdminUser() != null) && (organNode.getAdminUser().getId() == user.getId())) {
      return true;
    }
    List modelList = this.organModelDao.findAllSuperiorNodes(organTree, organNode);
    for (int i = 0; i < modelList.size(); i++)
    {
      OrganModel model = (OrganModel)modelList.get(i);
      if ((model.getOrganNode().getAdminUser() != null) && 
        (model.getOrganNode().getAdminUser().getId() == user.getId())) {
        return true;
      }
    }
    return false;
  }
  
  public OrganNode[] getAllRootTypeNodes()
  {
    List result = this.organNodeDao.findAllRootTypeNodes();
    
    return result == null ? null : (OrganNode[])result.toArray(new OrganNode[result.size()]);
  }
  
  public SecurityRole[] getOrganRole(OrganNode organNode, OrganTree organTree)
  {
    OrganModel organModel = new OrganModel();
    organModel = this.organModelDao.loadOrganModelByNodeAndTree(organNode, organTree);
    organModel = (OrganModel)this.organModelDao.loadWithLazy(new Long(organModel.getId()), new String[] { "roles" });
    return organModel.getRoles() == null ? null : (SecurityRole[])organModel.getRoles().toArray(new SecurityRole[organModel.getRoles().size()]);
  }
  
  public OrganTree[] getAllTrees(OrganNode organNode)
  {
    List result = this.organModelDao.findAllTreesByNode(organNode);
    return result == null ? null : (OrganTree[])result.toArray(new OrganTree[result.size()]);
  }
  
  public void updateOrdersForOrganNode(OrganTree organTree, Map ordersMap)
  {
    if (ordersMap != null)
    {
      Set orderSet = ordersMap.keySet();
      Iterator itr = orderSet.iterator();
      while (itr.hasNext())
      {
        Long key = (Long)itr.next();
        OrganNode organNode = (OrganNode)this.organNodeDao.load(key);
        OrganModel organModel = this.organModelDao.loadOrganModelByNodeAndTree(organNode, organTree);
        organModel.setOrders(((Long)ordersMap.get(key)).longValue());
        this.organModelDao.update(organModel);
      }
    }
  }
  
  public long getOrdersByOrganTree(OrganNode organNode, OrganTree organTree)
  {
    OrganModel organModel = new OrganModel();
    organModel = this.organModelDao.loadOrganModelByNodeAndTree(organNode, organTree);
    long order = organModel.getOrders();
    return order;
  }
  
  public void addOrganNodeToTree(OrganNode newNode, OrganNode selectedNode, OrganTree organTree)
  {
    this.organNodeDao.addOrganNodeToTree(newNode, selectedNode, organTree);
    if (newNode.getExtendProperty() != null) {
      this.organExtendPropertyDao.save(newNode.getExtendProperty());
    }
    OrganTree[] trees = getAllTrees(newNode);
    try
    {
      if (SystemParameter.getBoolean("userorg_ldap", Boolean.FALSE).booleanValue())
      {
        LDAPOrgan ldapOrgan = new LDAPOrgan();
        ldapOrgan.setCode(newNode.getCode());
        ldapOrgan.setName(newNode.getName());
        if (newNode.getAdminUser() != null) {
          ldapOrgan.setAdmin(newNode.getAdminUser().getName());
        }
        if ((trees != null) && (trees.length > 0)) {
          ldapOrgan.setTreeCode(trees[0].getCode());
        }
        if (selectedNode != null) {
          ldapOrgan.setParentCode(selectedNode.getCode());
        }
        LDAPFactory.getInstance().createOrgan(ldapOrgan);
      }
    }
    catch (LDAPException e) {}
  }
  
  public void addUserToOrganNode(SecurityUser user, OrganNode organNode)
  {
    OrganNode node = this.organNodeDao.loadOrganNodeWithUsers(organNode.getId());
    node.getUsers().add(user);
    this.organNodeDao.update(node);
    
    OrganTree[] trees = getAllTrees(organNode);
    try
    {
      if (SystemParameter.getBoolean("userorg_ldap", Boolean.FALSE).booleanValue())
      {
        LDAPUser ldapUser = new LDAPUser();
        ldapUser.setLoginName(user.getLoginName());
        ldapUser.setName(user.getName());
        LDAPOrgan ldapOrgan = new LDAPOrgan();
        ldapOrgan.setCode(organNode.getCode());
        ldapOrgan.setName(organNode.getName());
        if ((trees != null) && (trees.length > 0)) {
          ldapOrgan.setTreeCode(trees[0].getCode());
        }
        LDAPFactory.getInstance().addUserToOrgan(ldapUser, ldapOrgan);
      }
    }
    catch (LDAPException e) {}
  }
  
  public OrganNode[] getBrotherNodesByTree(OrganNode childOrganNode, OrganTree organTree)
  {
    return this.organNodeDao.getBrotherNodesByTree(childOrganNode, organTree);
  }
  
  public OrganNode getOrganNodeByCode(String code)
  {
    return this.organNodeDao.getOrganNodeByCode(code);
  }
  
  public OrganNode[] getOrganNodesByUserId(long userId)
  {
    return this.organNodeDao.getOrganNodesByUserId(userId);
  }
  
  public OrganNode[] getOrganNodesByUserAndTree(long userId, String treeCode)
  {
    return this.organNodeDao.getOrganNodesByUserAndTree(userId, treeCode);
  }
  
  public OrganNode[] getOrganNodesByTypeCode(String typeCode)
  {
    return this.organNodeDao.getOrganNodesByTypeCode(typeCode);
  }
  
  public OrganNode[] getOrganNodesByTypeCodeWithoutTree(String typeCode, OrganTree organTree)
  {
    return this.organNodeDao.getOrganNodesByTypeCodeWithoutTree(typeCode, organTree);
  }
  
  public OrganNode loadOrganNodeById(long id)
  {
    return (OrganNode)this.organNodeDao.load(new Long(id));
  }
  
  public OrganNode loadOrganNodeWithLazy(long id, String[] propertyNames)
  {
    return (OrganNode)this.organNodeDao.loadWithLazy(new Long(id), propertyNames);
  }
  
  public void removeOrganNodeFromTree(OrganNode organNode, OrganTree organTree, boolean cascade)
  {
    OrganTree[] trees = getAllTrees(organNode);
    this.organNodeDao.removeOrganNodeFromTree(organNode, organTree, cascade);
    try
    {
      if (SystemParameter.getBoolean("userorg_ldap", Boolean.FALSE).booleanValue())
      {
        LDAPOrgan ldapOrgan = new LDAPOrgan();
        ldapOrgan.setCode(organNode.getCode());
        ldapOrgan.setName(organNode.getName());
        if ((trees != null) && (trees.length > 0)) {
          ldapOrgan.setTreeCode(trees[0].getCode());
        }
        LDAPFactory.getInstance().removeOrgan(ldapOrgan);
      }
    }
    catch (LDAPException e) {}
  }
  
  public void removeOrganNode(OrganNode organNode)
  {
    OrganTree[] trees = getAllTrees(organNode);
    this.organNodeDao.remove(organNode);
    try
    {
      if (SystemParameter.getBoolean("userorg_ldap", Boolean.FALSE).booleanValue())
      {
        LDAPOrgan ldapOrgan = new LDAPOrgan();
        ldapOrgan.setCode(organNode.getCode());
        ldapOrgan.setName(organNode.getName());
        if ((trees != null) && (trees.length > 0)) {
          ldapOrgan.setTreeCode(trees[0].getCode());
        }
        LDAPFactory.getInstance().removeOrgan(ldapOrgan);
      }
    }
    catch (LDAPException e) {}
  }
  
  public void removeUserFromOrganNode(SecurityUser user, OrganNode organNode)
  {
    OrganNode node = loadOrganNodeWithLazy(organNode.getId(), new String[] { "users" });
    node.getUsers().remove(user);
    this.organNodeDao.update(node);
    try
    {
      if (SystemParameter.getBoolean("userorg_ldap", Boolean.FALSE).booleanValue())
      {
        LDAPUser ldapUser = new LDAPUser();
        ldapUser.setLoginName(user.getLoginName());
        ldapUser.setName(user.getName());
        LDAPOrgan ldapOrgan = new LDAPOrgan();
        ldapOrgan.setCode(organNode.getCode());
        ldapOrgan.setName(organNode.getName());
        OrganTree[] trees = getAllTrees(organNode);
        if ((trees != null) && (trees.length > 0)) {
          ldapOrgan.setTreeCode(trees[0].getCode());
        }
        LDAPFactory.getInstance().removeUserFromOrgan(ldapUser, ldapOrgan);
      }
    }
    catch (LDAPException e) {}
  }
  
  public void updateOrganNode(OrganNode organNode)
  {
    this.organNodeDao.update(organNode);
    if (organNode.getExtendProperty() != null) {
      this.organExtendPropertyDao.save(organNode.getExtendProperty());
    }
    try
    {
      if (SystemParameter.getBoolean("userorg_ldap", Boolean.FALSE).booleanValue())
      {
        LDAPOrgan ldapOrgan = new LDAPOrgan();
        ldapOrgan.setCode(organNode.getCode());
        ldapOrgan.setName(organNode.getName());
        if (organNode.getAdminUser() != null) {
          ldapOrgan.setAdmin(organNode.getAdminUser().getName());
        }
        OrganTree[] trees = getAllTrees(organNode);
        if ((trees != null) && (trees.length > 0)) {
          ldapOrgan.setTreeCode(trees[0].getCode());
        }
        LDAPFactory.getInstance().updateOrgan(ldapOrgan);
      }
    }
    catch (LDAPException e) {}
  }
  
  public OrganNode[] getChildNodes(OrganNode parentOrganNode, OrganTree organTree)
  {
    return this.organNodeDao.getChildNodes(parentOrganNode, organTree);
  }
  
  public OrganNode getParentNode(OrganNode childOrganNode, OrganTree organTree)
  {
    return this.organNodeDao.getParentNode(childOrganNode, organTree);
  }
  
  public OrganNode[] getChildNodesByName(OrganNode parentOrganNode, OrganTree organTree, String name)
  {
    return this.organNodeDao.getChildNodesByName(parentOrganNode, organTree, name);
  }
  
  public OrganNode[] getChildNodesByTypeCode(OrganNode parentOrganNode, OrganTree organTree, String typeCode)
  {
    return this.organNodeDao.getChildNodesByTypeCode(parentOrganNode, organTree, typeCode);
  }
  
  public List getUsersByOrganOrder(long nodeId)
  {
    return this.nodeUserDAO.getUserIdByOrder(nodeId);
  }
  
  public void updateOrganOrderByUserOrder(OrganNode organNode, Set users)
  {
    SecurityUser element;
    Iterator iter;
    if ((users != null) && (users.size() > 0))
    {
      element = null;
      for (iter = users.iterator(); iter.hasNext();)
      {
        element = (SecurityUser)iter.next();
        this.nodeUserDAO.updateNodeUserOrder(organNode.getId(), element.getId(), element.getOrders());
      }
    }
  }
  
  public void cutOrganTree(OrganNode sourceNode, OrganTree sourceOrganTree, OrganNode targetParentNode, OrganTree targetOrganTree)
  {
    OrganNodeType[] targetNodeTypes = this.organRuleService.getChildNodeTypesWithTreeAndParentNode(targetOrganTree, targetParentNode);
    
    sourceNode = this.organNodeDao.loadOrganNodeWithType(sourceNode);
    OrganNodeType sourceNodeType = sourceNode.getOrganNodeType();
    boolean isValidate = false;
    for (int i = 0; i < targetNodeTypes.length; i++) {
      if (sourceNodeType.getCode().equalsIgnoreCase(targetNodeTypes[i].getCode()))
      {
        isValidate = true;
        break;
      }
    }
    if (sourceOrganTree.getCode() != targetOrganTree.getCode()) {
      isValidate = judgetNodeExist(sourceNode, sourceOrganTree, targetOrganTree);
    }
    if (isValidate)
    {
      OrganTree[] trees = getAllTrees(sourceNode);
      if (sourceOrganTree.getCode() != targetOrganTree.getCode()) {
        copyNodeBetweenDifferentTrees(sourceNode, sourceOrganTree, targetParentNode, targetOrganTree);
      } else {
        cutNodeInSameTree(sourceNode, targetParentNode, targetOrganTree);
      }
      try
      {
        if (SystemParameter.getBoolean("userorg_ldap", Boolean.FALSE).booleanValue())
        {
          LDAPOrgan ldapOrgan = new LDAPOrgan();
          ldapOrgan.setCode(sourceNode.getCode());
          ldapOrgan.setName(sourceNode.getName());
          if (sourceNode.getAdminUser() != null) {
            ldapOrgan.setAdmin(sourceNode.getAdminUser().getName());
          }
          if ((trees != null) && (trees.length > 0)) {
            ldapOrgan.setTreeCode(trees[0].getCode());
          }
          LDAPUser[] users = LDAPFactory.getInstance().getUsersByOrgan(ldapOrgan, LDAPUser.class);
          LDAPFactory.getInstance().removeOrgan(ldapOrgan);
          
          trees = getAllTrees(sourceNode);
          if ((trees != null) && (trees.length > 0)) {
            ldapOrgan.setTreeCode(trees[0].getCode());
          }
          if (targetParentNode != null) {
            ldapOrgan.setParentCode(targetParentNode.getCode());
          }
          LDAPFactory.getInstance().createOrgan(ldapOrgan);
          for (LDAPUser user : users) {
            LDAPFactory.getInstance().addUserToOrgan(user, ldapOrgan);
          }
        }
      }
      catch (LDAPException e) {}
    }
    else
    {
      throw new OrganException("��������������������������");
    }
  }
  
  private boolean judgetNodeExist(OrganNode organNode, OrganTree sourceOrganTree, OrganTree targetOrganTree)
  {
    return this.organModelDao.judgetNodeExist(organNode, sourceOrganTree, targetOrganTree);
  }
  
  private void copyNodeBetweenDifferentTrees(OrganNode sourceNode, OrganTree sourceOrganTree, OrganNode targetParentNode, OrganTree targetOrganTree)
  {
    this.organModelDao.moveNodeBetweenDifferentTrees(sourceNode, sourceOrganTree, targetParentNode, targetOrganTree);
  }
  
  private void cutNodeInSameTree(OrganNode sourceNode, OrganNode targetParentNode, OrganTree targetOrganTree)
  {
    this.organModelDao.moveNodeInSameTree(sourceNode, targetParentNode, targetOrganTree);
  }
  
  public OrganNode[] getAllChildNodes(OrganNode parentNode, OrganTree organTree)
  {
    return this.organNodeDao.getAllChildNodes(parentNode, organTree);
  }
  
  public Page getAllUserInAdminNodeByPage(SecurityUser admin, OrganTree organTree, Map filter, int pageNo, int pageSize)
  {
    StringBuffer hql = new StringBuffer();
    List args = new ArrayList();
    StringBuffer queryHql = new StringBuffer();
    StringBuffer countHql = new StringBuffer();
    
    hql.append(" from SecurityUser user").append(" inner join user.organNodes nodes inner join nodes.organModels models").append(" where user.removed = 0");
    
    prepareBaseParameters(filter, hql, args);
    prepareAdminParameters(String.valueOf(organTree.getId()), String.valueOf(admin.getId()), hql, args);
    
    queryHql = new StringBuffer().append("select distinct user").append(hql);
    countHql = new StringBuffer().append("select count(distinct user.id)").append(hql);
    
    return this.organModelDao.findByHQLWithPage(queryHql.toString(), args, pageNo, pageSize, countHql.toString());
  }
  
  public OrganNode[] getOrganNodesByAdminUserAndTree(SecurityUser admin, OrganTree organTree)
  {
    List result = new ArrayList();
    OrganModel[] adminOrganModels = this.organModelDao.getAllAdminOrganModel(admin, organTree);
    for (int i = 0; i < adminOrganModels.length; i++) {
      result.add(adminOrganModels[i].getOrganNode());
    }
    return result == null ? null : (OrganNode[])result.toArray(new OrganNode[result.size()]);
  }
  
  public List getOrganNodesByAdminUserAndTrees(SecurityUser admin, OrganTree[] organTrees)
  {
    if (admin == null) {
      admin = new SecurityUser();
    }
    List result = new ArrayList();
    OrganTree organTree = null;
    OrganModel[] adminOrganModel = null;
    OrganModel[] allOrganModelArray = null;
    for (int j = 0; j < organTrees.length; j++)
    {
      organTree = organTrees[j];
      try
      {
        adminOrganModel = this.organModelDao.getAllAdminOrganModel(admin, organTree);
        allOrganModelArray = this.organModelDao.filterAllOrganModelArray(adminOrganModel);
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
      if (allOrganModelArray.length > 0) {
        for (int i = 0; i < allOrganModelArray.length; i++) {
          try
          {
            result.add(allOrganModelArray[i].getOrganNode());
          }
          catch (Exception e)
          {
            e.printStackTrace();
          }
        }
      }
    }
    return result == null ? null : result;
  }
  
  public OrganNode loadOrganNodeByCode(String code)
  {
    return this.organNodeDao.loadByCode(code);
  }
  
  public OrganNode loadOrganNodeByCodeWithLazy(String code, String[] propertyNames)
  {
    return this.organNodeDao.loadByCodeWithLazy(code, propertyNames);
  }
  
  public void reorderTreeNode(OrganTree organTree, OrganNode srcNode, OrganNode targetNode)
  {
    OrganModel srcModel = this.organModelDao.loadOrganModelByNodeAndTree(srcNode, organTree);
    OrganModel targetModel = this.organModelDao.loadOrganModelByNodeAndTree(targetNode, organTree);
    long oldOrders = srcModel.getOrders();
    srcModel.setOrders(targetModel.getOrders());
    targetModel.setOrders(oldOrders);
    this.organModelDao.save(srcModel);
    this.organModelDao.save(targetModel);
    this.organModelDao.reorderOrganModel(srcModel, targetModel);
  }
  
  public OrganNode[] getAllParentNodeByTypeCode(OrganTree organTree, OrganNode organNode, String typeCode)
  {
    return this.organNodeDao.getAllParentNodeByTypeCode(organTree, organNode, typeCode);
  }
  
  public SecurityUser[] queryUsersInAdminNode(SecurityUser admin, Map filter)
  {
    StringBuffer hql = new StringBuffer();
    List args = new ArrayList();
    
    hql.append("select distinct user from SecurityUser user").append(" inner join user.organNodes nodes inner join nodes.organModels models").append(" where user.removed = 0 and user.status = 1");
    
    prepareBaseParameters(filter, hql, args);
    prepareAdminParameters(null, String.valueOf(admin.getId()), hql, args);
    
    List result = this.organModelDao.findByHQL(hql.toString(), args);
    return result != null ? (SecurityUser[])result.toArray(new SecurityUser[result.size()]) : new SecurityUser[0];
  }
  
  public Page queryUnContainedUsersInAdminNodeByGroup(SecurityGroup group, SecurityUser admin, Map filter, int pageNo, int pageSize)
  {
    StringBuffer hql = new StringBuffer();
    List args = new ArrayList();
    StringBuffer queryHql = new StringBuffer();
    StringBuffer countHql = new StringBuffer();
    
    hql.append(" from SecurityUser user").append(" inner join user.organNodes nodes inner join nodes.organModels models").append(" where user.removed = 0 and user.status = 1");
    
    prepareBaseParameters(filter, hql, args);
    prepareAdminParameters(null, String.valueOf(admin.getId()), hql, args);
    hql.append(" and :group <> all elements (user.groups)");
    args.add(new HqlParameter("group", group));
    
    queryHql = new StringBuffer().append("select distinct user").append(hql);
    countHql = new StringBuffer().append("select count(distinct user.id)").append(hql);
    
    return this.organModelDao.findByHQLWithPage(queryHql.toString(), args, pageNo, pageSize, countHql.toString());
  }
  
  private void prepareBaseParameters(Map filter, StringBuffer hql, List args)
  {
    if ((filter != null) && (!filter.isEmpty()))
    {
      hql.append(" and (1 = 0");
      for (Object key : filter.keySet()) {
        if (String.valueOf(key).equals("organId"))
        {
          hql.append(" or nodes.id = :organId");
          args.add(new HqlParameter("organId", Long.valueOf(Long.parseLong((String)filter.get(key)))));
        }
        else if (String.valueOf(key).equals("userStatus"))
        {
          hql.append(" or user.status = :status");
          args.add(new HqlParameter("status", Long.valueOf(Long.parseLong((String)filter.get(key)))));
        }
        else
        {
          hql.append(" or user.").append(String.valueOf(key)).append(" like :").append(String.valueOf(key));
          args.add(new HqlParameter(String.valueOf(key), "%" + filter.get(key) + "%"));
        }
      }
      hql.append(")");
    }
  }
  
  private Map<String, String> getExtendPropertyNames()
  {
    return this.extendPropertyConfigService.getExtendPropertyNames(OrganNode.class.getName());
  }
  
  public Map<String, String> getExtendPropertyMap(OrganNode organNode)
  {
    Map<String, String> result = new HashMap();
    Map<String, String> nameMap = getExtendPropertyNames();
    BeanMap map = new BeanMap(organNode.getExtendProperty());
    for (String key : nameMap.keySet()) {
      if ((nameMap.get(key) != null) && (!"".equals(nameMap.get(key)))) {
        if (map.get(key) != null) {
          result.put(nameMap.get(key), String.valueOf(map.get(key)));
        } else {
          result.put(nameMap.get(key), null);
        }
      }
    }
    return result;
  }
  
  public void setExtendPropertyMap(OrganNode organNode, Map<String, String> extendPropertyMap)
  {
    OrganExtendProperty extendProperty = organNode.getExtendProperty();
    if (extendProperty == null) {
      extendProperty = new OrganExtendProperty();
    }
    Map<String, String> nameMap = getExtendPropertyNames();
    for (String key : nameMap.keySet()) {
      try
      {
        if ((nameMap.get(key) != null) && (!"".equals(nameMap.get(key)))) {
          BeanUtils.setProperty(extendProperty, key, extendPropertyMap.get(nameMap.get(key)));
        }
      }
      catch (Exception ex) {}
    }
    extendProperty.setOrganNode(organNode);
    organNode.setExtendProperty(extendProperty);
  }
  
  public OrganModel[] getAdminOrganModels(String organTreeId, String adminUserId)
  {
    if ((organTreeId == null) && (adminUserId == null)) {
      return new OrganModel[0];
    }
    StringBuffer hql = new StringBuffer();
    List args = new ArrayList();
    hql.append("from OrganModel model left join fetch model.organNode node left join fetch model.organTree tree").append(" where model.removed = 0");
    if (organTreeId != null)
    {
      hql.append(" and tree.id = :organTreeId");
      args.add(new HqlParameter("organTreeId", new Long(organTreeId)));
    }
    if (adminUserId != null)
    {
      hql.append(" and node.adminUser.id = :adminUserId");
      args.add(new HqlParameter("adminUserId", new Long(adminUserId)));
    }
    if ((organTreeId != null) && (adminUserId == null)) {
      hql.append(" and model.parentNode is null");
    }
    List result = this.organModelDao.findByHQL(hql.toString(), args);
    OrganModel[] adminOrganModel = (OrganModel[])result.toArray(new OrganModel[result.size()]);
    OrganModel[] allOrganModelArray = this.organModelDao.filterAllOrganModelArray(adminOrganModel);
    return allOrganModelArray;
  }
  
  private void prepareAdminParameters(String organTreeId, String adminUserId, StringBuffer hql, List args)
  {
    hql.append(" and models in (select model from OrganModel model, OrganModel m").append(" left join model.organNode node left join model.organTree tree").append(" left join m.organNode n left join m.organTree t").append(" where model.removed = 0 and m.removed = 0 and model.lft >= m.lft and model.rgt <= m.rgt").append(" and tree.id = t.id");
    if (organTreeId != null)
    {
      hql.append(" and tree.id = :organTreeId");
      args.add(new HqlParameter("organTreeId", new Long(organTreeId)));
    }
    if (adminUserId != null)
    {
      hql.append(" and n.adminUser.id = :adminUserId");
      args.add(new HqlParameter("adminUserId", new Long(adminUserId)));
    }
    if ((organTreeId != null) && (adminUserId == null)) {
      hql.append(" and m.parentNode is null");
    }
    hql.append(")");
  }
}
