package com.wondersgroup.framework.organization.dao;

import java.util.List;

public abstract interface NodeUserExtdDAO
{
  public static final String SECURITY_SUER_ID = "security_user_id";
  public static final String ORDERS = "orders";
  
  public abstract List getUserIdByOrder(long paramLong);
  
  public abstract void updateNodeUserOrder(long paramLong1, long paramLong2, long paramLong3);
}
