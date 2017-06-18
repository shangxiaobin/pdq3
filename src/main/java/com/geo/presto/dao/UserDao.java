package com.geo.presto.dao;

import org.springframework.stereotype.Repository;

import com.geo.presto.bean.DashboardUser;
import com.geo.presto.bean.DashboardUserRole;

import java.util.List;

/**
 * Created by yfyuan on 2016/12/2.
 */
@Repository
public interface UserDao {
    int save(DashboardUser user);

    List<DashboardUser> getUserList();

    int update(DashboardUser user);

    int saveUserRole(List<DashboardUserRole> list);

    int deleteUserRole(String userId);

    List<DashboardUserRole> getUserRoleList();
}
