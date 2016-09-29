package com.wondersgroup.framework.core5.dao.impl;

import com.wondersgroup.framework.core.bo.Page;
import com.wondersgroup.framework.core.bo.Removable;
import com.wondersgroup.framework.core.bo.Sort;
import com.wondersgroup.framework.core.bo.Sorts;
import com.wondersgroup.framework.core.bo.hibernate.HqlParameter;
import com.wondersgroup.framework.core.exception.DAOException;
import com.wondersgroup.framework.core5.dao.AbstractHibernateDao;
import com.wondersgroup.framework.core5.dao.AbstractHibernateExtdDao;
import com.wondersgroup.framework.core5.dao.support.Filters;
import com.wondersgroup.framework.core5.dao.support.HqlUtils;
import com.wondersgroup.framework.core5.dao.support.QueryCallBack;
import com.wondersgroup.framework.core5.dao.support.RowConverter;
import com.wondersgroup.framework.core5.dao.support.RowConverterUtils;
import com.wondersgroup.framework.core5.model.RemovablePeer;
import com.wondersgroup.framework.core5.storeprocedure.BeanStoreProcedure;
import com.wondersgroup.framework.core5.storeprocedure.SpParameters;
import com.wondersgroup.framework.core5.storeprocedure.StoreProcedure;
import com.wondersgroup.framework.core5.storeprocedure.bean.SpSupportBean;
import com.wondersgroup.framework.core5.util.GenericsExtUtils;
import com.wondersgroup.framework.core5.util.GenericsUtils;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.dialect.Dialect;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.internal.SessionImpl;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;
import org.springframework.orm.hibernate4.HibernateCallback;

public abstract class AbstractHibernateExtdDaoImpl<T>extends HibernateDaoSupport implements AbstractHibernateExtdDao<T>
{
  protected Class<T> entityClass;
  private RowConverter rowConverter;
  
  public AbstractHibernateExtdDaoImpl()
  {
    this.entityClass = (Class<T>) GenericsExtUtils.getSuperClassGenericType(getClass(), AbstractHibernateExtdDaoImpl.class, 0);
  }
  
  public AbstractHibernateExtdDaoImpl(Class<T> entityClass)
  {
    this.entityClass = entityClass;
  }
  
  @Resource
  public void setSessionFacotry(SessionFactory sessionFacotry) {  
      super.setSessionFactory(sessionFacotry);  
  }
  
  protected Session getSession() {  
	  return getSessionFactory().getCurrentSession();
  }
  
  public void setRowConverter(RowConverter rowConverter)
  {
    this.rowConverter = rowConverter;
  }
  
  protected Criteria createCriteria()
  {
    return getSession().createCriteria(getEntityClass());
  }
  
  public int countByCriteria(final DetachedCriteria detachedCriteria)
  {
    Integer count = (Integer)getHibernateTemplate().execute(new HibernateCallback()
    {
      public Object doInHibernate(Session session)
      {
        Criteria criteria = detachedCriteria.getExecutableCriteria(session);
        return criteria.setProjection(Projections.rowCount()).uniqueResult();
      }
    });
    
    return count.intValue();
  }
  
  public int countByHQL(String hql)
  {
    Query query = getSession().createQuery(hql);
    return ((Long)query.uniqueResult()).intValue();
  }
  
  public int countByHQL(String hql, List<HqlParameter> args)
  {
    try
    {
      Query query = getSession().createQuery(hql);
      HqlUtils.setQueryParameters(query, args);
      return ((Long)query.uniqueResult()).intValue();
    }
    catch (HibernateException ex)
    {
      throw new DAOException(ex);
    }
  }
  
  public void delete(T entity)
  {
    getHibernateTemplate().delete(entity);
  }
  
  public void deleteById(Serializable id)
  {
    delete(load(id));
  }
  
  public List<T> findAll()
  {
    Criteria criteria = createCriteria();
    filterRemovable(criteria);
    return criteria.list();
  }
  
  public Page findAllWithPage(int pageNo, int pageSize)
  {
    Criteria criteria = createCriteria();
    criteria.setProjection(null);
    criteria.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
    return findByCriteriaWithPage(criteria, pageNo, pageSize, true);
  }
  
  public List<T> findBy(Map<String, Object> filter)
  {
    return findBy(filter, false);
  }
  
  public List<T> findBy(Map<String, Object> filter, boolean filterRemoved)
  {
    Criteria criteria = createCriteria();
    for (Iterator<String> i = filter.keySet().iterator(); i.hasNext();)
    {
      String name = (String)i.next();
      if (filterProperty(name)) {
        criteria.add(Restrictions.eq(name, filter.get(name)));
      } else {
        throw new DAOException("Could not resolve property:" + name);
      }
    }
    if (filterRemoved) {
      filterRemovable(criteria);
    }
    return criteria.list();
  }
  
  public List<T> findBy(Map<String, Object> filters, Sorts sorts, Boolean filterRemoved)
  {
    Criteria criteria = createCriteria();
    for (Iterator<String> i = filters.keySet().iterator(); i.hasNext();)
    {
      String name = (String)i.next();
      if (filterProperty(name)) {
        criteria.add(Restrictions.eq(name, filters.get(name)));
      } else {
        throw new DAOException("Could not resolve property:" + name);
      }
    }
    if (filterRemoved.booleanValue()) {
      filterRemovable(criteria);
    }
    orderBy(criteria, sorts);
    return criteria.list();
  }
  
  public List<T> findBy(String name, Object value)
  {
    Criteria criteria = createCriteria();
    if (filterProperty(name)) {
      criteria.add(Restrictions.eq(name, value));
    } else {
      throw new DAOException("Could not resolve property:" + name);
    }
    filterRemovable(criteria);
    return criteria.list();
  }
  
  public List<T> findByCriteria(final DetachedCriteria detachedCriteria)
  {
	List result = (List)getHibernateTemplate().execute(new HibernateCallback()
    {
      public Object doInHibernate(Session session)
      {
        Criteria criteria = detachedCriteria.getExecutableCriteria(session);
        return criteria.list();
      }
    });
	
	return result;
  }
  
  public List<T> findByHQL(String query, Object object)
  {
    return (List<T>) getHibernateTemplate().find(query, object);
  }
  
  public List<T> findByHQL(String qryHql, List<HqlParameter> args)
  {
    Query query = getSession().createQuery(qryHql);
    HqlUtils.setQueryParameters(query, args);
    return query.list();
  }
  
  public Page findByHQLWithPage(String qryHql, int pageNo, int pageSize, String countHql)
  {
    return findByHQLWithPage(qryHql, null, pageNo, pageSize, countHql);
  }
  
  public Page findByHQLWithPage(String qryHql, List<HqlParameter> args, int pageNo, int pageSize, String countHql)
  {
    return findByHQLWithPage(qryHql, args, pageNo, pageSize, countHql, false);
  }
  
  public Page findByHQLWithPage(String qryHql, List<HqlParameter> args, int pageNo, int pageSize, String countHql, boolean filterRemoved)
  {
    if (qryHql == null) {
      throw new IllegalArgumentException("NULL is not a valid string");
    }
    if ((countHql == null) || (countHql.length() <= 0))
    {
      countHql = HqlUtils.getCountHql(qryHql);
      if (filterRemoved) {
        countHql = HqlUtils.filterRemovable(countHql);
      }
    }
    int totalCount = countByHQL(countHql, args);
    if (totalCount < 1) {
      return Page.EMPTY_PAGE;
    }
    Query query = getSession().createQuery(qryHql);
    HqlUtils.setQueryParameters(query, args);
    if (pageNo < 1) {
      pageNo = 1;
    }
    int startIndex = Page.getStartOfAnyPage(pageNo, pageSize);
    if (filterRemoved) {
      qryHql = HqlUtils.filterRemovable(qryHql);
    }
    List list = query.setFirstResult(startIndex - 1).setMaxResults(pageSize).list();
    int avaCount = list == null ? 0 : list.size();
    Page page = new Page(startIndex, avaCount, totalCount, pageSize, list);
    if (this.rowConverter != null) {
      page.setResult(convert(page.getResult()));
    }
    return page;
  }
  
  public List<T> findByLike(Map<String, String> filter)
  {
    Criteria criteria = createCriteria();
    for (Iterator<String> iter = filter.keySet().iterator(); iter.hasNext();)
    {
      String name = (String)iter.next();
      if (filterProperty(name)) {
        criteria.add(Restrictions.like(name, (String)filter.get(name), MatchMode.ANYWHERE));
      } else {
        throw new DAOException("Could not resolve property:" + name);
      }
    }
    return criteria.list();
  }
  
  public List<T> findByLike(String name, String value)
  {
    Criteria criteria = createCriteria();
    if (filterProperty(name)) {
      criteria.add(Restrictions.like(name, value, MatchMode.ANYWHERE));
    } else {
      throw new DAOException("Could not resolve property:" + name);
    }
    filterRemovable(criteria);
    return criteria.list();
  }
  
  public Page findByWithPage(Map<String, Object> filter, Map<String, String> sortMap, int pageNo, int pageSize)
  {
    return findByWithPage(filter, sortMap, pageNo, pageSize, false);
  }
  
  public Page findByWithPage(Map<String, Object> filter, Map<String, String> sortMap, int pageNo, int pageSize, boolean filterRemoved)
  {
    Criteria criteria = createCriteria();
    
    filterCriteria(criteria, filter);
    
    criteria.setProjection(null);
    criteria.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
    if (pageNo < 1) {
      pageNo = 1;
    }
    int startIndex = Page.getStartOfAnyPage(pageNo, pageSize);
    int totalCount = ((Integer)criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
    criteria.setProjection(null);
    
    sortCriteria(criteria, sortMap);
    if (filterRemoved) {
      filterRemovable(criteria);
    }
    List list = criteria.setFirstResult(startIndex - 1).setMaxResults(pageSize).list();
    int avaCount = list == null ? 0 : list.size();
    Page page = new Page(startIndex, avaCount, totalCount, pageSize, list);
    if (this.rowConverter != null) {
      page.setResult(convert(page.getResult()));
    }
    return page;
  }
  
  public T findUniqueBy(String name, Object value)
  {
    Criteria criteria = createCriteria();
    criteria.add(Restrictions.eq(name, value));
    filterRemovable(criteria);
    return (T)criteria.uniqueResult();
  }
  
  public T load(Serializable id)
  {
    T result = null;
    try
    {
      result = getHibernateTemplate().get(getEntityClass(), id);
    }
    catch (DataAccessException ex)
    {
      throw new DAOException("Can not load " + getEntityClassName() + " with " + id.toString(), ex);
    }
    return result;
  }
  
  public T loadWithLazy(Serializable id, String... propertyNames)
  {
    T object = load(id);
    if (object == null) {
      throw new DAOException("The object " + getEntityClassName() + " with id:[" + id + "] is not existed.");
    }
    BeanWrapper beanWrapper = new BeanWrapperImpl(object);
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
        throw new DAOException("Can not lazyload " + getEntityClassName() + " with " + id.toString(), e);
      }
    }
    return object;
  }
  
  public void remove(RemovablePeer entity)
  {
    Removable entity2 = entity;
    remove(entity2);
  }
  
  public void remove(Removable entity)
  {
    if (getEntityClass().isInstance(entity))
    {
      entity.setRemoved(1);
      getHibernateTemplate().update(entity);
    }
    else
    {
      throw new DAOException("The DAO Class " + getClass().toString() + " is not suitable to entity " + getEntityClassName());
    }
  }
  
  public void removeById(Serializable id)
  {
    T object = load(id);
    if (Removable.class.isInstance(object)) {
      remove((Removable)load(id));
    } else {
      throw new DAOException("The object " + getEntityClassName() + " with id " + id + " is not removable.");
    }
  }
  
  public void save(T entity)
  {
    getHibernateTemplate().saveOrUpdate(entity);
  }
  
  public void update(T entity)
  {
    getHibernateTemplate().update(entity);
  }
  
  public void bulkUpdate(String queryString, Object... values)
  {
    getHibernateTemplate().bulkUpdate(queryString, values);
  }
  
  private boolean filterProperty(String name)
  {
    try
    {
      BeanWrapper bw = new BeanWrapperImpl(getEntityClass().newInstance());
      bw.getPropertyValue(name);
    }
    catch (Exception e)
    {
      this.logger.error("Could not resolve property:" + name, e);
      return false;
    }
    return true;
  }
  
  private void filterRemovable(Criteria criteria)
  {
    try
    {
      boolean isRemovable = Removable.class.isInstance(getEntityClass().newInstance());
      if (isRemovable) {
        criteria.add(Restrictions.eq("removed", new Integer(0)));
      }
    }
    catch (Exception e)
    {
      this.logger.error(getEntityClass().getSimpleName() + " cannot instantiate");
    }
  }
  
  public Page findByCriteriaWithPage(Criteria criteria, int pageNo, int pageSize)
  {
    if (pageNo < 1) {
      pageNo = 1;
    }
    int startIndex = Page.getStartOfAnyPage(pageNo, pageSize);
    int totalCount = ((Integer)criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
    criteria.setProjection(null);
    
    List list = criteria.setFirstResult(startIndex - 1).setMaxResults(pageSize).list();
    int avaCount = list == null ? 0 : list.size();
    
    Page page = new Page(startIndex, avaCount, totalCount, pageSize, list);
    if (this.rowConverter != null) {
      page.setResult(convert(page.getResult()));
    }
    return page;
  }
  
  public Page findByCriteriaWithPage(Criteria criteria, int pageNo, int pageSize, boolean filterRemoved)
  {
    if (pageNo < 1) {
      pageNo = 1;
    }
    int startIndex = Page.getStartOfAnyPage(pageNo, pageSize);
    int totalCount = ((Integer)criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
    criteria.setProjection(null);
    
    filterRemovable(criteria);
    
    List list = criteria.setFirstResult(startIndex - 1).setMaxResults(pageSize).list();
    int avaCount = list == null ? 0 : list.size();
    
    Page page = new Page(startIndex, avaCount, totalCount, pageSize, list);
    if (this.rowConverter != null) {
      page.setResult(convert(page.getResult()));
    }
    return page;
  }
  
  protected void filterCriteria(Criteria criteria, Map<String, Object> filter)
  {
    if (filter == null) {
      return;
    }
    for (Iterator<String> i = filter.keySet().iterator(); i.hasNext();)
    {
      String name = (String)i.next();
      if (filterProperty(name)) {
        criteria.add(Restrictions.eq(name, filter.get(name)));
      } else {
        throw new DAOException("Could not resolve property:" + name);
      }
    }
  }
  
  protected Class<T> getEntityClass()
  {
    return this.entityClass;
  }
  
  protected String getEntityClassName()
  {
    return this.entityClass.getName();
  }
  
  protected void sortCriteria(Criteria criteria, Map<String, String> sortMap)
  {
    Iterator<String> iter;
    if ((sortMap != null) && (!sortMap.isEmpty())) {
      for (iter = sortMap.keySet().iterator(); iter.hasNext();)
      {
        String fieldName = (String)iter.next();
        String orderType = (String)sortMap.get(fieldName);
        if (fieldName.indexOf('.') != -1)
        {
          String alias = StringUtils.substringBefore(fieldName, ".");
          criteria.createAlias(alias, alias);
        }
        if ("asc".equalsIgnoreCase(orderType)) {
          criteria.addOrder(Order.asc(fieldName));
        } else {
          criteria.addOrder(Order.desc(fieldName));
        }
      }
    }
  }
  
  protected void sortCriteria(Criteria criteria, Sorts sorts)
  {
    if (sorts != null) {
      for (Sort sort : sorts) {
        criteria.addOrder(sort.isAsc() ? Order.asc(sort.getName()) : Order.desc(sort.getName()));
      }
    }
  }
  
  protected List<Object> convert(List<Object> source)
  {
    return RowConverterUtils.convert(source, this.rowConverter);
  }
  
  public Object getSequenceNextValue(String sequenceName)
  {
    Dialect dialect = ((SessionImpl)getSession()).getFactory().getDialect();
    String queryString = dialect.getSequenceNextValString(sequenceName);
    SQLQuery query = getSession().createSQLQuery(queryString);
    return query.uniqueResult();
  }
  
//  public <T> T executeStoreFunction(String name, Class<T> resultType)
//  {
//    return (T)executeStoreProcedure(name, new SpParameters().setReturnParameter(com.wondersgroup.framework.core5.storeprocedure.SpParameter.createReturnParameter(resultType)), new Object[0])[0];
//  }
  
//  public void executeStoreProcedure(String name)
//  {
//    executeStoreProcedure(name, (SpParameters)null, new Object[0]);
//  }
  
//  public Object[] executeStoreProcedure(final String name, final SpParameters parameters, final Object... inValues)
//  {
//    (Object[])getHibernateTemplate().execute(new HibernateCallback()
//    {
//      public Object doInHibernate(Session session)
//        throws HibernateException, SQLException
//      {
//        session.flush();
//        return new StoreProcedure(name, parameters).execute(session.connection(), inValues);
//      }
//    });
//  }
//  
//  public void executeStoreProcedure(final String name, final SpSupportBean parameterBean)
//  {
//    getHibernateTemplate().execute(new HibernateCallback()
//    {
//      public Object doInHibernate(Session session)
//        throws HibernateException, SQLException
//      {
//        session.flush();
//        BeanStoreProcedure.execute(session.connection(), name, parameterBean);
//        return null;
//      }
//    });
//  }
  
  public List<T> findAll(Sorts sorts)
  {
    Criteria criteria = createCriteria();
    filterRemovable(criteria);
    orderBy(criteria, sorts);
    return criteria.list();
  }
  
  public List<T> findBy(String name, Object value, Sorts sorts)
  {
    Criteria criteria = getSession().createCriteria(getEntityClass());
    if (filterProperty(name)) {
      criteria.add(Restrictions.eq(name, value));
    } else {
      throw new DAOException("Could not resolve property:" + name);
    }
    filterRemovable(criteria);
    orderBy(criteria, sorts);
    return criteria.list();
  }
  
  public <F extends Filters> Page query(Page page, F filters, Sorts sorts, QueryCallBack<F> queryCallBack)
  {
    StringBuffer queryHql = new StringBuffer();
    StringBuffer countHql = new StringBuffer();
    List<HqlParameter> args = new ArrayList();
    if (filters == null) {
      try
      {
        filters = (F)GenericsExtUtils.getSuperClassGenricType(queryCallBack.getClass(), 0).newInstance();
      }
      catch (Exception ex)
      {
        throw new DAOException(ex);
      }
    }
    queryCallBack.populateQuery(queryHql, countHql, args, filters, sorts);
    page = findByHQLWithPage(queryHql.toString(), args, page.getCurrentPageNo(), page.getPageSize(), countHql.toString());
    
    return page;
  }
  
  public Page findByCriteriaWithPage(DetachedCriteria detachedCriteria, int pageNo, int pageSize, boolean filterRemoved)
  {
    if (pageNo < 1) {
      pageNo = 1;
    }
    int startIndex = Page.getStartOfAnyPage(pageNo, pageSize);
    
    Criteria criteria = detachedCriteria.getExecutableCriteria(getSession());
    CriteriaImpl criteriaImpl = (CriteriaImpl)criteria;
    
    Projection projection = criteriaImpl.getProjection();
    int totalCount = ((Integer)criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
    criteria.setProjection(projection);
    if (projection == null) {
      criteria.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
    }
    if (filterRemoved) {
      filterRemovable(criteria);
    }
    List list = criteria.setFirstResult(startIndex - 1).setMaxResults(pageSize).list();
    int avaCount = list == null ? 0 : list.size();
    
    return new Page(startIndex, avaCount, totalCount, pageSize, list);
  }
  
  public Page findByCriteriaWithPage(DetachedCriteria detachedCriteria, int pageNo, int pageSize)
  {
    return findByCriteriaWithPage(detachedCriteria, pageNo, pageSize, false);
  }
  
  public void clear()
  {
    getHibernateTemplate().clear();
  }
  
  public void flush()
  {
    getHibernateTemplate().flush();
  }
  
  protected void orderBy(Criteria criteria, Sorts sorts)
  {
    Iterator itr;
    if (sorts != null) {
      for (itr = sorts.iterator(); itr.hasNext();)
      {
        Sort sort = (Sort)itr.next();
        criteria.addOrder(sort.isAsc() ? Order.asc(sort.getName()) : Order.desc(sort.getName()));
      }
    }
  }
}
