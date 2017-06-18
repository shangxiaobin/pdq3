package com.geo.presto.dao;

import org.springframework.stereotype.Repository;

import com.geo.presto.bean.DashboardDatasource;

import java.util.List;
import java.util.Map;

/**
 * Created by yfyuan on 2016/8/15.
 */
@Repository
public interface DatasourceDao {

    String getDatasourceConfig(String userId, String name);

    List<DashboardDatasource> getDatasourceList(String userId);

    DashboardDatasource getDatasource(Long datasourceId);

    int save(DashboardDatasource dashboardDatasource);

    long countExistDatasourceName(Map<String, Object> map);

    int update(DashboardDatasource dashboardDatasource);

    int delete(Long id, String userId);
}
