package com.wondersgroup.framework.organization.dao.impl;

import com.wondersgroup.framework.core.bo.Page;
import com.wondersgroup.framework.core.bo.hibernate.HqlParameter;
import com.wondersgroup.framework.core.dao.impl.AbstractHibernateExtdDAOImpl;
import com.wondersgroup.framework.organization.bo.OrganNodeType;
import com.wondersgroup.framework.organization.bo.OrganRule;
import com.wondersgroup.framework.organization.bo.OrganTreeType;
import com.wondersgroup.framework.organization.dao.OrganRuleExtdDao;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

@Repository
public class OrganRuleExtdDaoImpl extends AbstractHibernateExtdDAOImpl implements OrganRuleExtdDao
{
  protected Class getEntityClass()
  {
    return OrganRule.class;
  }
  
  public OrganRule loadByCode(String code)
  {
    String hql = "from OrganRule organRule where organRule.code=? and organRule.removed=0";
    List res = findByHQL(hql, code);
    return (res != null) && (res.size() > 0) ? (OrganRule)res.get(0) : null;
  }
  
  public Page findByLikeNameWithPage(String name, int pageNo, int pageSize)
  {
    StringBuffer qryHql = new StringBuffer();
    
    qryHql.append("from OrganRule organRule where organRule.removed=0 ");
    if ((name == null) || (name.equalsIgnoreCase(""))) {
      name = "";
    }
    List args = new ArrayList();
    qryHql.append(" and name like :name ");
    args.add(new HqlParameter("name", "%" + name + "%"));
    
    return findByHQLWithPage(qryHql.toString(), args, pageNo, pageSize, null);
  }
  
  public List findChildNodeTypesByParent(OrganTreeType treeType, OrganNodeType parentNodeType)
  {
    StringBuffer qryHql = new StringBuffer();
    if (getSession().contains(parentNodeType)) {
      getSession().evict(parentNodeType);
    }
    qryHql.append("select rule.subordinateNodeType from OrganRule rule where rule.removed=0 ");
    if (treeType != null) {
      qryHql.append(" and rule.organTreeType.id = " + treeType.getId() + " ");
    }
    if (parentNodeType != null) {
      qryHql.append(" and rule.superiorNodeType.id = " + parentNodeType.getId() + " ");
    }
    Query query = getSession().createQuery(qryHql.toString());
    return query.list();
  }
  
  public List findChildNodeTypeCodeArrayByParent(OrganTreeType treeType, OrganNodeType parentNodeType)
  {
    StringBuffer qryHql = new StringBuffer();
    qryHql.append("select rule.subordinateNodeType.code from OrganRule rule where rule.removed=0 ");
    if (treeType != null) {
      qryHql.append(" and rule.organTreeType.id = " + treeType.getId() + " ");
    }
    if (parentNodeType != null) {
      qryHql.append(" and rule.superiorNodeType.id = " + parentNodeType.getId() + " ");
    }
    Query query = getSession().createQuery(qryHql.toString());
    return query.list();
  }
  
  public Page findRulesByTreeTypeWithPage(OrganTreeType treeType, int pageNo, int pageSize)
  {
    StringBuffer qryHql = new StringBuffer();
    
    qryHql.append("select rule.superiorNodeType.name,rule.subordinateNodeType.name,rule from OrganRule rule where rule.removed=0 ");
    if (treeType != null) {
      qryHql.append(" and rule.organTreeType.id = " + treeType.getId() + " ");
    }
    return findByHQLWithPage(qryHql.toString(), null, pageNo, pageSize, null);
  }
  
  public List findRulesBySupSubNodeType(OrganNodeType nodeType)
  {
    StringBuffer qryHql = new StringBuffer();
    qryHql.append("select rule from OrganRule rule where (rule.superiorNodeType = :nodeType or rule.subordinateNodeType = :nodeType)").append(" and rule.removed = 0");
    
    Query query = getSession().createQuery(qryHql.toString());
    query.setParameter("nodeType", nodeType);
    return query.list();
  }
}
