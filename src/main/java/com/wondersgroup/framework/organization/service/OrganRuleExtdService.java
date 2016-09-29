package com.wondersgroup.framework.organization.service;

import com.wondersgroup.framework.core.bo.Page;
import com.wondersgroup.framework.organization.bo.OrganNode;
import com.wondersgroup.framework.organization.bo.OrganNodeType;
import com.wondersgroup.framework.organization.bo.OrganRule;
import com.wondersgroup.framework.organization.bo.OrganTree;
import java.util.List;
import java.util.Map;

public abstract interface OrganRuleExtdService
{
  public abstract boolean addOrganRule(OrganRule paramOrganRule);
  
  public abstract void updateOrganRule(OrganRule paramOrganRule);
  
  public abstract void deleteOrganRule(OrganRule paramOrganRule);
  
  public abstract void removeOrganRule(OrganRule paramOrganRule);
  
  public abstract OrganRule loadOrganRuleByCode(String paramString);
  
  public abstract Page findOrganRuleByName(String paramString, int paramInt1, int paramInt2);
  
  public abstract OrganNodeType[] getChildNodeTypesWithTreeAndParentNode(OrganTree paramOrganTree, OrganNode paramOrganNode);
  
  public abstract String[] getChildNodeTypeCodeArrayWithTreeAndParentNode(OrganTree paramOrganTree, OrganNode paramOrganNode);
  
  public abstract Map<Long, List<String>> getChildNodeTypeCodeMapWithTreeAndParentNodes(OrganTree paramOrganTree, OrganNode[] paramArrayOfOrganNode);
  
  public abstract void removeOrganRuleById(int paramInt);
  
  public abstract List findRulesBySupSubNodeType(OrganNodeType paramOrganNodeType);
}
