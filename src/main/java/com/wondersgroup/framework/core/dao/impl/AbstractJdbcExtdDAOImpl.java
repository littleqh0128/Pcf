package com.wondersgroup.framework.core.dao.impl;

import com.wondersgroup.framework.core.dao.AbstractJdbcExtdDAO;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

@Repository
public abstract class AbstractJdbcExtdDAOImpl extends JdbcDaoSupport implements AbstractJdbcExtdDAO
{
  protected static Log logger = LogFactory.getLog(AbstractJdbcExtdDAOImpl.class);
  
  @Autowired  
  @Qualifier("dataSource")  
  public void setSource(DataSource dataSource)  
  {  
      super.setDataSource(dataSource);  
  }  
}
