package com.geo.presto.dao;

import org.springframework.stereotype.Repository;

import com.geo.presto.bean.DashboardDataset;

import java.util.List;
import java.util.Map;

/**
 * Created by yfyuan on 2016/10/11.
 */
@Repository
public interface DatasetDao {

    List<String> getCategoryList();

    List<DashboardDataset> getDatasetList(String userId);

    int save(DashboardDataset dataset);

    long countExistDatasetName(Map<String, Object> map);

    int update(DashboardDataset dataset);

    int delete(Long id, String userId);

    DashboardDataset getDataset(Long id);

}
