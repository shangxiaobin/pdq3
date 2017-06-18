package com.geo.presto.services;

import com.alibaba.fastjson.JSONObject;
import com.geo.presto.bean.DashboardDataset;
import com.geo.presto.bean.DashboardDatasource;
import com.geo.presto.dao.DatasetDao;
import com.geo.presto.dao.DatasourceDao;
import com.geo.presto.dataprovider.DataProvider;
import com.geo.presto.dataprovider.DataProviderManager;
import com.geo.presto.dto.DataProviderResult;
import com.google.common.base.Functions;
import com.google.common.collect.Maps;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * Created by yfyuan on 2016/8/15.
 */
@Repository
public class DataProviderService {

    @Value("${dataprovider.resultLimit:200000}")
    private int resultLimit;

    @Autowired
    private DatasourceDao datasourceDao;

    @Autowired
    private DatasetDao datasetDao;

    public ServiceStatus test(JSONObject dataSource, Map<String, String> query) {
        try {
            DataProvider dataProvider = DataProviderManager.getDataProvider(dataSource.getString("type"));
            dataProvider.getData(Maps.transformValues(dataSource.getJSONObject("config"), Functions.toStringFunction()), query);
            return new ServiceStatus(ServiceStatus.Status.Success, null);
        } catch (Exception e) {
            return new ServiceStatus(ServiceStatus.Status.Fail, e.getMessage());
        }
    }

    public DataProviderResult getData(Long datasourceId, Map<String, String> query, Long datasetId) {
        String[][] dataArray = null;
        int resultCount = 0;
        String msg = "1";

        if (datasetId != null) {
            Dataset dataset = getDataset(datasetId);
            datasourceId = dataset.getDatasourceId();
            query = dataset.getQuery();
        }
        DashboardDatasource datasource = datasourceDao.getDatasource(datasourceId);
        try {
            JSONObject config = JSONObject.parseObject(datasource.getConfig());
            DataProvider dataProvider = DataProviderManager.getDataProvider(datasource.getType());
            Map<String, String> parameterMap = Maps.transformValues(config, Functions.toStringFunction());
            resultCount = dataProvider.resultCount(parameterMap, query);
            if (resultCount > resultLimit) {
                msg = "Cube result count is " + resultCount + ", greater than limit " + resultLimit;
            } else {
                dataArray = dataProvider.getData(parameterMap, query);
            }
        } catch (Exception e) {
            msg =  e.getMessage();
        }
        return new DataProviderResult(dataArray, msg);
    }

    protected Dataset getDataset(Long datasetId) {
        return new Dataset(datasetDao.getDataset(datasetId));
    }

    protected class Dataset {
        private Long datasourceId;
        private Map<String, String> query;
        private Long interval;

        public Dataset(DashboardDataset dataset) {
            JSONObject data = JSONObject.parseObject(dataset.getData());
            this.query = Maps.transformValues(data.getJSONObject("query"), Functions.toStringFunction());
            this.datasourceId = data.getLong("datasource");
            this.interval = data.getLong("interval");
        }

        public Long getDatasourceId() {
            return datasourceId;
        }

        public void setDatasourceId(Long datasourceId) {
            this.datasourceId = datasourceId;
        }

        public Map<String, String> getQuery() {
            return query;
        }

        public void setQuery(Map<String, String> query) {
            this.query = query;
        }

        public Long getInterval() {
            return interval;
        }

        public void setInterval(Long interval) {
            this.interval = interval;
        }
    }
}
