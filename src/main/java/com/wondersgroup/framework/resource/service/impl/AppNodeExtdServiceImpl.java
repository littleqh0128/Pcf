package com.wondersgroup.framework.resource.service.impl;

import com.wondersgroup.framework.core.bo.Page;
import com.wondersgroup.framework.resource.bo.AppNode;
import com.wondersgroup.framework.resource.dao.AppNodeExtdDAO;
import com.wondersgroup.framework.resource.exception.ResourceException;
import com.wondersgroup.framework.resource.service.AppNodeExtdService;
import com.wondersgroup.framework.security.bo.SecurityUser;
import com.wondersgroup.framework.security.exception.SecurityException;
import com.wondersgroup.framework.security.service.UserExtdService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("AppNodeExtService")
@Transactional
public class AppNodeExtdServiceImpl implements AppNodeExtdService
{
  @Autowired
  private AppNodeExtdDAO appNodeDAO;
  @Autowired
  private UserExtdService userService;
  
  public AppNode loadById(long id)
  {
    return (AppNode)this.appNodeDAO.load(new Long(id));
  }
  
  public AppNode findNodeByCode(String code)
  {
    return (AppNode)this.appNodeDAO.findUniqueBy("code", code);
  }
  
  public void addNewAppNode(AppNode node)
  {
    String code = node.getCode();
    if (findNodeByCode(code) != null) {
      throw new ResourceException("AppNode code alreay existed");
    }
    this.appNodeDAO.save(node);
  }
  
  public void removeAppNode(AppNode node)
  {
    if (null == node) {
      throw new ResourceException("AppNode Instance not existed");
    }
    AppNode[] childnodes = findNodesWithCentralNode(node);
    if (childnodes != null) {
      for (int i = 0; i < childnodes.length; i++) {
        this.appNodeDAO.remove(childnodes[0]);
      }
    }
    this.appNodeDAO.remove(node);
  }
  
  public void updateAppNode(AppNode node)
  {
    if (null == node) {
      throw new ResourceException("AppNode Instance not existed");
    }
    this.appNodeDAO.update(node);
  }
  
  public AppNode[] findAllNodes()
  {
    List result = this.appNodeDAO.findAll();
    return (AppNode[])result.toArray(new AppNode[result.size()]);
  }
  
  public Page findByWithPage(Map filter, Map sortMap, int pageNo, int pageSize)
  {
    return this.appNodeDAO.findByLikeNameWithPage(filter, sortMap, pageNo, pageSize);
  }
  
  public AppNode[] findAllCentralNodes()
  {
    List result = this.appNodeDAO.findAllCentralNodes();
    return (AppNode[])result.toArray(new AppNode[result.size()]);
  }
  
  public AppNode[] findNodesWithCentralNode(AppNode centralNode)
  {
    if (null == centralNode) {
      throw new ResourceException("AppNode Instance not existed");
    }
    List result = this.appNodeDAO.findNodesWithCentralNode(centralNode);
    return (AppNode[])result.toArray(new AppNode[result.size()]);
  }
  
  public SecurityUser[] findUsersByNode(AppNode node)
  {
    if (null == node) {
      throw new ResourceException("AppNode Instance not existed");
    }
    node = (AppNode)this.appNodeDAO.loadWithLazy(new Long(node.getId()), new String[] { "users" });
    System.out.println(node.getUsers());
    List result = new ArrayList(node.getUsers());
    return (SecurityUser[])result.toArray(new SecurityUser[result.size()]);
  }
  
  public void addUserToNode(SecurityUser user, AppNode appNode)
  {
    if (null == appNode) {
      throw new ResourceException("AppNode Instance not existed");
    }
    appNode = (AppNode)this.appNodeDAO.loadWithLazy(new Long(appNode.getId()), new String[] { "users" });
    appNode.getUsers().add(user);
    this.appNodeDAO.update(appNode);
  }
  
  public void removeUserFromNode(SecurityUser user, AppNode appNode)
  {
    if (null == appNode) {
      throw new ResourceException("AppNode Instance not existed");
    }
    appNode = (AppNode)this.appNodeDAO.loadWithLazy(new Long(appNode.getId()), new String[] { "users" });
    user = this.userService.loadUserById(user.getId());
    if (appNode.getUsers().contains(user)) {
      appNode.getUsers().remove(user);
    }
    this.appNodeDAO.update(appNode);
  }
  
  public AppNode getAppNodeByEntryPath(String appEntryPath)
  {
    List result = this.appNodeDAO.getAppNodeByEntryPath(appEntryPath);
    if (result.size() == 0) {
      throw new ResourceException("AppNode Instance not existed");
    }
    return (AppNode)result.get(0);
  }
  
  public Page getAllUsersNotInAppNodeByPage(AppNode appNode, int pageNo, int pageSize, Map filter)
  {
    if (appNode == null) {
      throw new SecurityException("AppNode Instance not existed");
    }
    return this.appNodeDAO.getAllUsersNotInAppNodeByPage(appNode, pageNo, pageSize, filter);
  }
  
  public Page getAllUsersInAppNodeByPage(AppNode appNode, int pageNo, int pageSize)
  {
    if (appNode == null) {
      throw new SecurityException("AppNode Instance not existed");
    }
    return this.appNodeDAO.getAllUsersInAppNodeByPage(appNode, pageNo, pageSize);
  }
  
  public List<SecurityUser> findUsersNotInAppNode(AppNode appNode, Map filter)
  {
    if (appNode == null) {
      throw new SecurityException("AppNode Instance not existed");
    }
    return this.appNodeDAO.findUsersNotInAppNode(appNode, filter);
  }
  
  public boolean isUserInAppNode(SecurityUser user, AppNode appNode)
  {
    if (appNode == null) {
      throw new SecurityException("AppNode Instance not existed");
    }
    return this.appNodeDAO.isUserInAppNode(user, appNode);
  }
}
