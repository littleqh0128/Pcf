package com.wondersgroup.framework.organization.dao.impl;

import com.wondersgroup.framework.core.dao.impl.AbstractJdbcExtdDAOImpl;
import com.wondersgroup.framework.organization.dao.NodeUserExtdDAO;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class NodeUserExtdDAOImpl extends AbstractJdbcExtdDAOImpl implements NodeUserExtdDAO
{
  public List getUserIdByOrder(long nodeid)
  {
    List result = getJdbcTemplate().queryForList("select SECURITY_USER_ID,ORDERS from CS_USER_ORGANNODE where ORGAN_NODE_ID=" + nodeid + " order by ORDERS asc");
    
    return result;
  }
  
  public void updateNodeUserOrder(long nodeid, long userid, long order)
  {
    String hql = "update CS_USER_ORGANNODE set ORDERS=" + order + " where SECURITY_USER_ID=" + userid + " and ORGAN_NODE_ID=" + nodeid;
    
    getJdbcTemplate().execute(hql);
  }
}
