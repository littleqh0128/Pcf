package com.wondersgroup.framework.organization.service.impl;

import com.wondersgroup.framework.core.bo.Page;
import com.wondersgroup.framework.core.bo.hibernate.HqlParameter;
import com.wondersgroup.framework.organization.bo.OrganNode;
import com.wondersgroup.framework.organization.bo.OrganNodeType;
import com.wondersgroup.framework.organization.bo.OrganRule;
import com.wondersgroup.framework.organization.bo.OrganTree;
import com.wondersgroup.framework.organization.bo.OrganTreeType;
import com.wondersgroup.framework.organization.dao.OrganRuleExtdDao;
import com.wondersgroup.framework.organization.service.OrganRuleExtdService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("OrganRuleExtService")
@Transactional
public class OrganRuleExtdServiceImpl implements OrganRuleExtdService
{
  @Autowired
  private OrganRuleExtdDao organRuleDao;

  public boolean addOrganRule(OrganRule organRule)
  {
    boolean result = false;
    if (this.organRuleDao.loadByCode(organRule.getCode()) == null)
    {
      this.organRuleDao.save(organRule);
      result = true;
    }
    return result;
  }
  
  public void updateOrganRule(OrganRule organRule)
  {
    this.organRuleDao.update(organRule);
  }
  
  public void deleteOrganRule(OrganRule organRule)
  {
    this.organRuleDao.delete(organRule);
  }
  
  public void removeOrganRule(OrganRule organRule)
  {
    this.organRuleDao.remove(organRule);
  }
  
  public OrganRule loadOrganRuleByCode(String code)
  {
    return (OrganRule)this.organRuleDao.findUniqueBy("code", code);
  }
  
  public Page findOrganRuleByName(String name, int pageNo, int pageSize)
  {
    return this.organRuleDao.findByLikeNameWithPage(name, pageNo, pageSize);
  }
  
  public OrganNodeType[] getChildNodeTypesWithTreeAndParentNode(OrganTree organTree, OrganNode parentNode)
  {
    OrganTreeType treeType = organTree.getTreeType();
    OrganNodeType parentNodeType = parentNode.getOrganNodeType();
    List result = this.organRuleDao.findChildNodeTypesByParent(treeType, parentNodeType);
    return result == null ? null : (OrganNodeType[])result.toArray(new OrganNodeType[result.size()]);
  }
  
  public String[] getChildNodeTypeCodeArrayWithTreeAndParentNode(OrganTree organTree, OrganNode parentNode)
  {
    OrganTreeType treeType = organTree.getTreeType();
    OrganNodeType parentNodeType = parentNode.getOrganNodeType();
    List result = this.organRuleDao.findChildNodeTypeCodeArrayByParent(treeType, parentNodeType);
    return result == null ? null : (String[])result.toArray(new String[result.size()]);
  }
  
  public Map<Long, List<String>> getChildNodeTypeCodeMapWithTreeAndParentNodes(OrganTree tree, OrganNode[] parentNodes)
  {
    Map<Long, List<String>> result = new HashMap();
    StringBuffer qryHql = new StringBuffer();
    List args = new ArrayList();
    qryHql.append("select node.id, rule.subordinateNodeType.code from OrganNode node, OrganRule rule").append(" where node.removed=0 and rule.removed=0");
    if (tree.getTreeType() != null)
    {
      qryHql.append(" and rule.organTreeType = :treeType");
      args.add(new HqlParameter("treeType", tree.getTreeType()));
    }
    qryHql.append(" and rule.superiorNodeType = node.organNodeType");
    qryHql.append(" and node.id in (:nodes)");
    List nodeIdList = new ArrayList();
    for (OrganNode node : parentNodes)
    {
      nodeIdList.add(Long.valueOf(node.getId()));
      result.put(Long.valueOf(node.getId()), new ArrayList());
    }
    args.add(new HqlParameter("nodes", nodeIdList));
    List list = this.organRuleDao.findByHQL(qryHql.toString(), args);
    for (Object object : list)
    {
      Long nodeId = (Long)((Object[])(Object[])object)[0];
      String code = (String)((Object[])(Object[])object)[1];
      ((List)result.get(nodeId)).add(code);
    }
    return result;
  }
  
  public void removeOrganRuleById(int id)
  {
    this.organRuleDao.removeById(new Long(id));
  }
  
  public List findRulesBySupSubNodeType(OrganNodeType nodeType)
  {
    return this.organRuleDao.findRulesBySupSubNodeType(nodeType);
  }
}
