package com.wondersgroup.framework.core.dao.impl;

import com.wondersgroup.framework.core.dao.AbstractHibernateExtdDAO;
import com.wondersgroup.framework.core.bo.Page;
import com.wondersgroup.framework.core.bo.Removable;
import com.wondersgroup.framework.core.bo.Sort;
import com.wondersgroup.framework.core.bo.Sorts;
import com.wondersgroup.framework.core.bo.hibernate.HqlParameter;
import com.wondersgroup.framework.core.exception.DAOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.Type;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;
import org.springframework.orm.hibernate4.HibernateTemplate;

public abstract class AbstractHibernateExtdDAOImpl extends HibernateDaoSupport implements AbstractHibernateExtdDAO
{
  private String injectedEntity;
  protected static Log logger = LogFactory.getLog(AbstractHibernateExtdDAOImpl.class);
  
  @Resource
  public void setSessionFacotry(SessionFactory sessionFacotry) {  
      super.setSessionFactory(sessionFacotry);  
  }
  
  protected Session getSession() {  
	  return getSessionFactory().getCurrentSession();
  }
  
  private HibernateTemplate hibernateTemplate;
  public HibernateTemplate getMyHibernateTemplate() {
  	return hibernateTemplate;
  }
  	
  @Resource(name="hibernateTemplate")
  public void setMyHibernateTemplate(HibernateTemplate hibernateTemplate) {
  	this.hibernateTemplate = hibernateTemplate;
  }
  
  protected Class getEntityClass()
  {
    String entityName = null != this.injectedEntity ? this.injectedEntity : getEntityClassName();
    try
    {
      return Class.forName(entityName);
    }
    catch (ClassNotFoundException e)
    {
      throw new DAOException("Please override entity class -- " + entityName, e);
    }
  }
  
  protected String getEntityClassName()
  {
    String daoClazzName = getClass().getName();
    String entityName = daoClazzName.replaceFirst("\\.dao\\.", ".bo.").replaceFirst("\\.impl\\.", ".").replaceFirst("DAOImpl", "");
    
    return entityName;
  }
  
  public void logMetadata()
  {
    ClassMetadata meta = getSessionFactory().getClassMetadata(getEntityClass());
    String[] propertyNames = meta.getPropertyNames();
    Type[] propertyTypes = meta.getPropertyTypes();
    for (int i = 0; i < propertyNames.length; i++) {
      if ((!propertyTypes[i].isEntityType()) && (!propertyTypes[i].isCollectionType())) {
        logger.info("property name - " + propertyNames[i] + " & type - " + propertyTypes[i].getName());
      }
    }
  }
  
  protected Criteria createCriteria()
  {
    return getSession().createCriteria(getEntityClass());
  }
  
  public void save(Object object)
  {
    if (getEntityClass().isInstance(object)) {
      getHibernateTemplate().saveOrUpdate(object);
    } else {
      throw new DAOException("The DAO Class " + getClass().toString() + " is not suitable to entity " + getEntityClassName());
    }
  }
  
  public void update(Object object)
  {
    if (getEntityClass().isInstance(object)) {
      getHibernateTemplate().update(object);
    } else {
      throw new DAOException("The DAO Class " + getClass().toString() + " is not suitable to entity " + getEntityClassName());
    }
  }
  
  public Object load(Serializable id)
  {
    Object result = null;
    try
    {
      result = getSession().load(getEntityClass(), id);
    }
    catch (DataAccessException e)
    {
      throw new DAOException("Can not load " + getEntityClassName() + " with " + id.toString(), e);
    }
    return result;
  }
  
  public Object loadWithLazy(Serializable id, String[] propertyNames)
  {
    Object object = load(id);
    if (object == null) {
      throw new DAOException("Can not loadWithLazy " + getEntityClassName() + " with " + id.toString() + ": this object does not exist");
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
  
  public List findAll()
  {
    Criteria criteria = getSession().createCriteria(getEntityClass());
    filterRemovable(criteria);
    return criteria.list();
  }
  
  public List findAll(Sorts sorts)
  {
    Criteria criteria = createCriteria();
    filterRemovable(criteria);
    orderBy(criteria, sorts);
    return criteria.list();
  }
  
  public List findByHQL(String query, Object object)
  {
    return getHibernateTemplate().find(query, object);
  }
  
  public List findByHQL(String qryHql, List args)
  {
    Query query = getSession().createQuery(qryHql);
    setQueryParameters(query, args);
    return query.list();
  }
  
  public Object findUniqueBy(String name, Object value)
  {
    Criteria criteria = getSession().createCriteria(getEntityClass());
    criteria.add(Restrictions.eq(name, value));
    filterRemovable(criteria);
    return criteria.uniqueResult();
  }
  
  public List findBy(String name, Object value)
  {
    Criteria criteria = getSession().createCriteria(getEntityClass());
    if (filterProperty(name)) {
      criteria.add(Restrictions.eq(name, value));
    } else {
      throw new DAOException("Could not resolve property:" + name);
    }
    filterRemovable(criteria);
    return criteria.list();
  }
  
  public List findBy(String name, Object value, Sorts sorts)
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
  
  public List findBy(Map filter)
  {
    return findBy(filter, false);
  }
  
  public List findBy(Map filter, boolean filterRemoved)
  {
    Criteria criteria = getSession().createCriteria(getEntityClass());
    for (Iterator i = filter.keySet().iterator(); i.hasNext();)
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
  
  public List findBy(Map filters, Sorts sorts, Boolean filterRemoved)
  {
    Criteria criteria = getSession().createCriteria(getEntityClass());
    for (Iterator i = filters.keySet().iterator(); i.hasNext();)
    {
      String name = (String)i.next();
      if (filterProperty(name)) {
        criteria.add(Restrictions.eq(name, filters.get(name)));
      } else {
        throw new DAOException("Could not resolve property:" + name);
      }
    }
    if ((filterRemoved != null) && (filterRemoved.booleanValue() == true)) {
      filterRemovable(criteria);
    }
    orderBy(criteria, sorts);
    return criteria.list();
  }
  
  public List findByLike(String name, String value)
  {
    Criteria criteria = getSession().createCriteria(getEntityClass());
    if (filterProperty(name)) {
      criteria.add(Restrictions.like(name, value, MatchMode.ANYWHERE));
    } else {
      throw new DAOException("Could not resolve property:" + name);
    }
    filterRemovable(criteria);
    return criteria.list();
  }
  
  public List findByLike(Map filter)
  {
    Criteria criteria = getSession().createCriteria(getEntityClass());
    for (Iterator i = filter.keySet().iterator(); i.hasNext();)
    {
      String name = (String)i.next();
      if (filterProperty(name)) {
        criteria.add(Restrictions.like(name, (String)filter.get(name), MatchMode.ANYWHERE));
      } else {
        throw new DAOException("Could not resolve property:" + name);
      }
    }
    return criteria.list();
  }
  
  public List findByCriteria(final DetachedCriteria criteria)
  {
	  return (List) (criteria.getExecutableCriteria(getSession()).list());
	  
//	List result = (List) getMyHibernateTemplate().execute(new HibernateCallback()
//    {
//      public Object doInHibernate(Session session)
//      {
//        Criteria criteria = dc.getExecutableCriteria(session);
//        return criteria.list();
//      }
//   });
	
//	return result;
  }
  
  public int countByCriteria(final DetachedCriteria criteria)
  {
	  return Integer.valueOf(criteria.getExecutableCriteria(getSession()).uniqueResult().toString());
	  
//    Integer count = (Integer)getMyHibernateTemplate().execute(new HibernateCallback()
//    {
//      public Object doInHibernate(Session session)
//      {
//        Criteria criteria = detachedCriteria.getExecutableCriteria(session);
//        return criteria.setProjection(Projections.rowCount()).uniqueResult();
//      }
//    });
    
    //return count.intValue();
  }
  
  public void remove(Removable object)
  {
    if (getEntityClass().isInstance(object))
    {
      object.setRemoved(1);
      getHibernateTemplate().update(object);
    }
    else
    {
      throw new DAOException("The DAO Class " + getClass().toString() + " is not suitable to entity " + getEntityClassName());
    }
  }
  
  public void removeById(Serializable id)
  {
    Object object = load(id);
    if (Removable.class.isInstance(object)) {
      remove((Removable)load(id));
    } else {
      throw new DAOException("The object " + getEntityClassName() + " with id " + id + " is not removable.");
    }
  }
  
  public void delete(Object object)
  {
    if (getEntityClass().isInstance(object)) {
      getHibernateTemplate().delete(object);
    } else {
      throw new DAOException("The DAO Class " + getClass().toString() + " is not suitable to entity " + getEntityClassName());
    }
  }
  
  public void deleteById(Serializable id)
  {
    delete(load(id));
  }
  
  public int countByHQL(String hql)
  {
    Query query = getSession().createQuery(hql);
    return ((Number)query.uniqueResult()).intValue();
  }
  
  public int countByHQL(String hql, List args)
  {
    try
    {
      Query query = getSession().createQuery(hql);
      setQueryParameters(query, args);
      return ((Number)query.uniqueResult()).intValue();
    }
    catch (HibernateException ex)
    {
      throw new DAOException(ex);
    }
  }
  
  public Page findByHQLWithPage(String qryHql, int pageNo, int pageSize, String countHql)
  {
    return findByHQLWithPage(qryHql, null, pageNo, pageSize, countHql);
  }
  
  public Page findByHQLWithPage(String qryHql, List args, int pageNo, int pageSize, String countHql)
  {
    return findByHQLWithPage(qryHql, args, pageNo, pageSize, countHql, false);
  }
  
  public Page findByHQLWithPage(String qryHql, List args, int pageNo, int pageSize, String countHql, boolean filterRemoved)
  {
    if (qryHql == null) {
      throw new IllegalArgumentException("NULL is not a valid string");
    }
    if (countHql == null)
    {
      countHql = "select count(*) " + qryHql.substring(qryHql.indexOf("from "));
      if (filterRemoved) {
        countHql = filterRemovable(countHql);
      }
    }
    int totalCount = countByHQL(countHql, args);
    if (totalCount < 1) {
      return Page.EMPTY_PAGE;
    }
    if (filterRemoved) {
      qryHql = filterRemovable(qryHql);
    }
    Query query = getSession().createQuery(qryHql);
    setQueryParameters(query, args);
    if (pageNo < 1) {
      pageNo = 1;
    }
    int startIndex = Page.getStartOfAnyPage(pageNo, pageSize);
    List list = query.setFirstResult(startIndex - 1).setMaxResults(pageSize).list();
    int avaCount = list == null ? 0 : list.size();
    return new Page(startIndex, avaCount, totalCount, pageSize, list);
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
    
    return new Page(startIndex, avaCount, totalCount, pageSize, list);
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
    
    return new Page(startIndex, avaCount, totalCount, pageSize, list);
  }
  
  public Page findAllWithPage(int pageNo, int pageSize)
  {
    Criteria criteria = getSession().createCriteria(getEntityClass());
    criteria.setProjection(null);
    criteria.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
    return findByCriteriaWithPage(criteria, pageNo, pageSize, true);
  }
  
  public Page findByWithPage(Map filter, Map sortMap, int pageNo, int pageSize)
  {
    return findByWithPage(filter, sortMap, pageNo, pageSize, false);
  }
  
  public Page findByWithPage(Map filter, Map sortMap, int pageNo, int pageSize, boolean filterRemoved)
  {
    Criteria criteria = getSession().createCriteria(getEntityClass());
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
    return new Page(startIndex, avaCount, totalCount, pageSize, list);
  }
  
  public Page findByCriteriaWithPage(DetachedCriteria detachedCriteria, int pageNo, int pageSize)
  {
    return findByCriteriaWithPage(detachedCriteria, pageNo, pageSize, false);
  }
  
  public Page findByCriteriaWithPage(DetachedCriteria detachedCriteria, int pageNo, int pageSize, boolean filterRemoved)
  {
    if (pageNo < 1) {
      pageNo = 1;
    }
    int startIndex = Page.getStartOfAnyPage(pageNo, pageSize);
    
    Criteria criteria = detachedCriteria.getExecutableCriteria(getSessionFactory().getCurrentSession());
    
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
  
  public String getInjectedEntity()
  {
    return this.injectedEntity;
  }
  
  public void setInjectedEntity(String injectedEntity)
  {
    this.injectedEntity = injectedEntity;
  }
  
  protected void filterCriteria(Criteria criteria, Map filter)
  {
    if (filter == null) {
      return;
    }
    for (Iterator i = filter.keySet().iterator(); i.hasNext();)
    {
      String name = (String)i.next();
      if (filterProperty(name)) {
        criteria.add(Restrictions.eq(name, filter.get(name)));
      } else {
        throw new DAOException("Could not resolve property:" + name);
      }
    }
  }
  
  protected void sortCriteria(Criteria criteria, Map sortMap)
  {
    Iterator i;
    if ((sortMap != null) && (!sortMap.isEmpty())) {
      for (i = sortMap.keySet().iterator(); i.hasNext();)
      {
        String fieldName = i.next().toString();
        String orderType = sortMap.get(fieldName).toString();
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
  
  private void setQueryParameters(Query query, List args)
  {
    if (args != null) {
      for (int i = 0; i < args.size(); i++)
      {
        HqlParameter arg = (HqlParameter)args.get(i);
        String argName = arg.getName();
        Object argValue = arg.getValue();
        Type argType = arg.getType();
        if (argName == null)
        {
          if (argType == null) {
            query.setParameter(i, argValue);
          } else {
            query.setParameter(i, argValue, argType);
          }
        }
        else if (argType == null)
        {
          if (Collection.class.isInstance(argValue)) {
            query.setParameterList(argName, (Collection)argValue);
          } else {
            query.setParameter(argName, argValue);
          }
        }
        else if (Collection.class.isInstance(argValue)) {
          query.setParameterList(argName, (Collection)argValue, argType);
        } else {
          query.setParameter(argName, argValue, argType);
        }
      }
    }
  }
  
  private boolean isRemovableBO()
  {
    boolean result = false;
    try
    {
      result = Removable.class.isInstance(getEntityClass().newInstance());
    }
    catch (InstantiationException e)
    {
      throw new DAOException("instantiate Entity fail: " + getEntityClass().getName(), e);
    }
    catch (IllegalAccessException e)
    {
      throw new DAOException("access Entity fail: " + getEntityClass().getName(), e);
    }
    return result;
  }
  
  private void filterRemovable(Criteria criteria)
  {
    if (isRemovableBO()) {
      criteria.add(Restrictions.eq("removed", new Integer(0)));
    }
  }
  
  private String filterRemovable(String hql)
  {
    String newHql = hql;
    if ((isRemovableBO()) && (newHql.indexOf(" removed") == -1))
    {
      String trimHql = newHql.trim();
      if ((trimHql.endsWith("and")) || (trimHql.endsWith("where"))) {
        newHql = newHql.concat(" removed = 0");
      } else if (trimHql.indexOf("where") > -1) {
        newHql = newHql.concat(" and removed = 0");
      } else {
        newHql = newHql.concat(" where removed = 0");
      }
    }
    return newHql;
  }
  
  private boolean filterProperty(String keyValue)
  {
    String[] propertyParts = keyValue.split("\\.");
    try
    {
      Class clazz = getEntityClass();
      Field field = null;
      while (field == null) {
        try
        {
          field = clazz.getDeclaredField(propertyParts[0]);
        }
        catch (NoSuchFieldException e)
        {
          clazz = clazz.getSuperclass();
          if (clazz.equals(Object.class)) {
            throw e;
          }
        }
      }
      for (int i = 1; i < propertyParts.length; i++) {
        field = field.getType().getDeclaredField(propertyParts[i]);
      }
    }
    catch (Exception e)
    {
      logger.error("Could not resolve property:" + keyValue, e);
      return false;
    }
    return true;
  }
  
  public void clear()
  {
    getHibernateTemplate().clear();
  }
  
  public void flush()
  {
    getHibernateTemplate().flush();
  }
  
  public void bulkUpdate(String queryString)
  {
    getHibernateTemplate().bulkUpdate(queryString);
  }
  
  public void bulkUpdate(String hql, Object param)
  {
    getHibernateTemplate().bulkUpdate(hql, param);
  }
  
  public void bulkUpdate(String hql, Object[] params)
  {
    getHibernateTemplate().bulkUpdate(hql, params);
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
