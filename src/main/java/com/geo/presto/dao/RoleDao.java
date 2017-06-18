package com.geo.presto.dao;

import org.springframework.stereotype.Repository;

import com.geo.presto.bean.DashboardRole;

import java.util.List;

/**
 * Created by yfyuan on 2016/12/6.
 */
@Repository
public interface RoleDao {
    int save(DashboardRole role);

    List<DashboardRole> getRoleList();

    int update(DashboardRole role);
}
