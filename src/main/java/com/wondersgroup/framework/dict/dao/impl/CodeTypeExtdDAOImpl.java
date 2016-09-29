package com.wondersgroup.framework.dict.dao.impl;

import com.wondersgroup.framework.core.bo.Page;
import com.wondersgroup.framework.core.bo.hibernate.HqlParameter;
import com.wondersgroup.framework.core.dao.impl.AbstractHibernateExtdDAOImpl;
import com.wondersgroup.framework.dict.bo.CodeType;
import com.wondersgroup.framework.dict.dao.CodeTypeExtdDAO;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public class CodeTypeExtdDAOImpl extends AbstractHibernateExtdDAOImpl implements CodeTypeExtdDAO
{
  protected Class getEntityClass()
  {
    return CodeType.class;
  }
  
  public Collection findCodeTypeByName(String name)
  {
    return findByLike("name", name);
  }
  
  public CodeType getCodeType(String code)
  {
    String hql = "from CodeType type where type.code=? and type.removed=0";
    List res = findByHQL(hql, code);
    return (res != null) && (res.size() > 0) ? (CodeType)res.get(0) : null;
  }
  
  public Page findCodeTypeByPageAndName(Map filter, Map sort, int pageNo, int pageSize)
  {
    String nameParam = (String)filter.get("name");
    String qryHql = "select type from CodeType type where type.removed = 0";
    String countHql = "select count(type) from CodeType type where type.removed = 0";
    List args = new ArrayList();
    if (nameParam != null)
    {
      qryHql = qryHql + " and type.name like :name";
      countHql = countHql + " and type.name like :name";
      args.add(new HqlParameter("name", "%" + nameParam + "%"));
    }
    return super.findByHQLWithPage(qryHql, args, pageNo, pageSize, countHql);
  }
}
