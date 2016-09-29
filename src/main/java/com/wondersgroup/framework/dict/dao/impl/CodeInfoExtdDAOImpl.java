package com.wondersgroup.framework.dict.dao.impl;

import com.wondersgroup.framework.core.dao.impl.AbstractHibernateExtdDAOImpl;
import com.wondersgroup.framework.dict.bo.CodeInfo;
import com.wondersgroup.framework.dict.dao.CodeInfoExtdDAO;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class CodeInfoExtdDAOImpl extends AbstractHibernateExtdDAOImpl implements CodeInfoExtdDAO
{
  protected Class getEntityClass()
  {
    return CodeInfo.class;
  }
  
  public Collection findCodeInfoByName(String name)
  {
    return findByLike("name", name);
  }
  
  public Collection getChildsByParentCode(String parentCode)
  {
    String hql = "from CodeInfo info left join fetch info.codeType left join fetch info.parent where  info.parent.code=? and info.parent.removed=0 and info.removed=0";
    
    return findByHQL(hql, parentCode);
  }
  
  public CodeInfo getCodeInfoByCode(String codeInfoCode, String codeTypeCode)
  {
    String hql = "from CodeInfo info left join fetch info.codeType left join fetch info.parent where info.code=?  and info.codeType.code=? and info.removed=0";
    
    List res = getHibernateTemplate().find(hql, new String[] { codeInfoCode, codeTypeCode });
    return (res != null) && (res.size() > 0) ? (CodeInfo)res.get(0) : null;
  }
  
  public Collection getTopCodeInfo()
  {
    String hql = "from CodeInfo info left join fetch info.codeType left join fetch info.parent where  info.parent is null and info.removed=?";
    
    return findByHQL(hql, new Integer(0));
  }
  
  public Collection findCodeInfoByCodeType(String codeTypeCode, String parentCodeInfoCode)
  {
    return findCodeInfoByCodeType(codeTypeCode, parentCodeInfoCode, null, null);
  }
  
  public List findCodeInfoByCodeType(String codeTypeCode, String parentCodeInfoCode, String orderField, String orderType)
  {
    StringBuffer hqlString = new StringBuffer("from CodeInfo info ");
    List<String> values = new ArrayList();
    hqlString.append("left join fetch info.codeType ");
    hqlString.append("left join fetch info.parent ");
    hqlString.append("where info.codeType.code=? ");
    values.add(codeTypeCode);
    if (parentCodeInfoCode != null)
    {
      hqlString.append("and info.parent.code=? ");
      values.add(parentCodeInfoCode);
    }
    else
    {
      hqlString.append("and info.parent is null ");
    }
    hqlString.append("and info.removed=0 ");
    if (orderField == null) {
      orderField = "dispOrder";
    }
    if ((orderType != null) && ("desc".equalsIgnoreCase(orderType))) {
      hqlString.append("order by info." + orderField + " desc");
    } else {
      hqlString.append("order by info." + orderField + " asc");
    }
    List list = getHibernateTemplate().find(hqlString.toString(), values.toArray(new Object[values.size()]));
    return list;
  }
  
  private Collection getTopCodeInfo(String codeTypeCode)
  {
    String hql = " from CodeInfo info left join fetch info.codeType left join fetch info.parent where info.codeType.code=? and info.parent is null and info.removed=0";
    
    return findByHQL(hql, codeTypeCode);
  }
}
