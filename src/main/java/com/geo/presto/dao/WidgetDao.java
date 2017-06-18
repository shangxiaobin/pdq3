package com.geo.presto.dao;

import org.springframework.stereotype.Repository;

import com.geo.presto.bean.DashboardWidget;

import java.util.List;
import java.util.Map;

/**
 * Created by yfyuan on 2016/8/122.
 */
@Repository
public interface WidgetDao {

    List<String> getCategoryList();

    List<DashboardWidget> getWidgetList(String userId);

    int save(DashboardWidget dashboardWidget);

    long countExistWidgetName(Map<String, Object> map);

    int update(DashboardWidget dashboardWidget);

    int delete(Long id, String userId);

    DashboardWidget getWidget(Long id);
}
