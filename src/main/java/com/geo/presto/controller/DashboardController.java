package com.geo.presto.controller;

import com.alibaba.fastjson.JSONObject;
import com.geo.presto.bean.*;
import com.geo.presto.bean.User;
import com.geo.presto.dao.*;
import com.geo.presto.dataprovider.DataProviderManager;
import com.geo.presto.dataprovider.DataProviderViewManager;
import com.geo.presto.dto.*;
import com.geo.presto.services.*;
import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by yfyuan on 2016/8/9.
 */
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private BoardDao boardDao;

    @Autowired
    private DatasourceDao datasourceDao;

    @Autowired
    private DataProviderService dataProviderService;

    @Autowired
    private CachedDataProviderService cachedDataProviderService;

    @Autowired
    private DatasourceService datasourceService;

    @Autowired
    private WidgetService widgetService;

    @Autowired
    private WidgetDao widgetDao;

    @Autowired
    private BoardService boardService;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private CategoryService categoryService;


    @Autowired
    private DatasetDao datasetDao;

    @Autowired
    private DatasetService datasetService;

    @RequestMapping(value = "/test")
    public ServiceStatus test(@RequestParam(name = "datasource", required = false) String datasource, @RequestParam(name = "query", required = false) String query) {
        JSONObject queryO = JSONObject.parseObject(query);
        JSONObject datasourceO = JSONObject.parseObject(datasource);
        return dataProviderService.test(datasourceO, Maps.transformValues(queryO, Functions.toStringFunction()));
    }

    @RequestMapping(value = "/getData")
    public DataProviderResult getData(@RequestParam(name = "datasourceId", required = false) Long datasourceId, @RequestParam(name = "query", required = false) String query, @RequestParam(name = "datasetId", required = false) Long datasetId) {
        Map<String, String> strParams = null;
        if (query != null) {
            JSONObject queryO = JSONObject.parseObject(query);
            strParams = Maps.transformValues(queryO, Functions.toStringFunction());
        }
        DataProviderResult result = dataProviderService.getData(datasourceId, strParams, datasetId);
        return result;
    }

    @RequestMapping(value = "/getCachedData")
    public DataProviderResult getCachedData(@RequestParam(name = "datasourceId", required = false) Long datasourceId, @RequestParam(name = "query", required = false) String query, @RequestParam(name = "datasetId", required = false) Long datasetId, @RequestParam(name = "reload", required = false, defaultValue = "false") Boolean reload) {
        Map<String, String> strParams = null;
        if (query != null) {
            JSONObject queryO = JSONObject.parseObject(query);
            strParams = Maps.transformValues(queryO, Functions.toStringFunction());
        }
        DataProviderResult result = cachedDataProviderService.getData(datasourceId, strParams, datasetId, reload);
        return result;
    }

    @RequestMapping(value = "/getDatasourceList")
    public List<ViewDashboardDatasource> getDatasourceList(HttpServletRequest request, HttpServletResponse response) {

    	User user = (User) request.getSession().getAttribute("user");

        List<DashboardDatasource> list = datasourceDao.getDatasourceList(user.getUsername());
        return Lists.transform(list, ViewDashboardDatasource.TO);
    }

    @RequestMapping(value = "/getProviderList")
    public Set<String> getProviderList() {
        return DataProviderManager.getProviderList();
    }

    @RequestMapping(value = "/getConfigView")
    public String getConfigView(@RequestParam(name = "type") String type) {
        return DataProviderViewManager.getQueryView(type);
    }

    @RequestMapping(value = "/getDatasourceView")
    public String getDatasourceView(@RequestParam(name = "type") String type) {
        return DataProviderViewManager.getDatasourceView(type);
    }

    @RequestMapping(value = "/saveNewDatasource")
    public ServiceStatus saveNewDatasource(@RequestParam(name = "json") String json,HttpServletRequest request, HttpServletResponse response) {

    	User user = (User) request.getSession().getAttribute("user");
        return datasourceService.save(user.getUsername(), json);
    }

    @RequestMapping(value = "/updateDatasource")
    public ServiceStatus updateDatasource(@RequestParam(name = "json") String json,HttpServletRequest request, HttpServletResponse response) {

    	User user = (User) request.getSession().getAttribute("user");
        return datasourceService.update(user.getUsername(), json);
    }

    @RequestMapping(value = "/deleteDatasource")
    public ServiceStatus deleteDatasource(@RequestParam(name = "id") Long id,HttpServletRequest request, HttpServletResponse response) {

    	User user = (User) request.getSession().getAttribute("user");
        return datasourceService.delete(user.getUsername(), id);
    }

    @RequestMapping(value = "/saveNewWidget")
    public ServiceStatus saveNewWidget(@RequestParam(name = "json") String json,HttpServletRequest request, HttpServletResponse response) {
    	User user = (User) request.getSession().getAttribute("user");
        return widgetService.save(user.getUsername(), json);
    }

    @RequestMapping(value = "/getWidgetList")
    public List<ViewDashboardWidget> getWidgetList(HttpServletRequest request, HttpServletResponse response) {
    	User user = (User) request.getSession().getAttribute("user");
        List<DashboardWidget> list = widgetDao.getWidgetList(user.getUsername());
        return Lists.transform(list, ViewDashboardWidget.TO);
    }

    @RequestMapping(value = "/updateWidget")
    public ServiceStatus updateWidget(@RequestParam(name = "json") String json,HttpServletRequest request, HttpServletResponse response) {
    	
    	User user = (User) request.getSession().getAttribute("user");
        return widgetService.update(user.getUsername(), json);
    }

    @RequestMapping(value = "/deleteWidget")
    public ServiceStatus deleteWidget(@RequestParam(name = "id") Long id,HttpServletRequest request, HttpServletResponse response) {

    	User user = (User) request.getSession().getAttribute("user");
        return widgetService.delete(user.getUsername(), id);
    }

    @RequestMapping(value = "/getBoardList")
    public List<ViewDashboardBoard> getBoardList(HttpServletRequest request, HttpServletResponse response) {

    	User user = (User) request.getSession().getAttribute("user");
        List<DashboardBoard> list = boardDao.getBoardList(user.getUsername());
        return Lists.transform(list, ViewDashboardBoard.TO);
    }

    @RequestMapping(value = "/saveNewBoard")
    public ServiceStatus saveNewBoard(@RequestParam(name = "json") String json,HttpServletRequest request, HttpServletResponse response) {

    	User user = (User) request.getSession().getAttribute("user");
        return boardService.save(user.getUsername(), json);
    }

    @RequestMapping(value = "/updateBoard")
    public ServiceStatus updateBoard(@RequestParam(name = "json") String json,HttpServletRequest request, HttpServletResponse response) {

    	User user = (User) request.getSession().getAttribute("user");
        return boardService.update(user.getUsername(), json);
    }

    @RequestMapping(value = "/deleteBoard")
    public String deleteBoard(@RequestParam(name = "id") Long id,HttpServletRequest request, HttpServletResponse response) {

    	User user = (User) request.getSession().getAttribute("user");
        return boardService.delete(user.getUsername(), id);
    }

    @RequestMapping(value = "/getBoardData")
    public ViewDashboardBoard getBoardData(@RequestParam(name = "id") Long id) {
        return boardService.getBoardData(id);
    }

    @RequestMapping(value = "/saveNewCategory")
    public ServiceStatus saveNewCategory(@RequestParam(name = "json") String json,HttpServletRequest request, HttpServletResponse response) {

    	User user = (User) request.getSession().getAttribute("user");
        return categoryService.save(user.getUsername(), json);
    }

    @RequestMapping(value = "/getCategoryList")
    public List<DashboardCategory> getCategoryList() {
        List<DashboardCategory> list = categoryDao.getCategoryList();
        return list;
    }

    @RequestMapping(value = "/updateCategory")
    public ServiceStatus updateCategory(@RequestParam(name = "json") String json,HttpServletRequest request, HttpServletResponse response) {

    	User user = (User) request.getSession().getAttribute("user");
        return categoryService.update(user.getUsername(), json);
    }

    @RequestMapping(value = "/deleteCategory")
    public String deleteCategory(@RequestParam(name = "id") Long id) {
        return categoryService.delete(id);
    }

    @RequestMapping(value = "/getWidgetCategoryList")
    public List<String> getWidgetCategoryList() {
        return widgetDao.getCategoryList();
    }

    @RequestMapping(value = "/saveNewDataset")
    public ServiceStatus saveNewDataset(@RequestParam(name = "json") String json,HttpServletRequest request, HttpServletResponse response) {

    	User user = (User) request.getSession().getAttribute("user");
        return datasetService.save(user.getUsername(), json);
    }

    @RequestMapping(value = "/getDatasetList")
    public List<ViewDashboardDataset> getDatasetList(HttpServletRequest request, HttpServletResponse response) {

    	User user = (User) request.getSession().getAttribute("user");
        List<DashboardDataset> list = datasetDao.getDatasetList(user.getUsername());
        return Lists.transform(list, ViewDashboardDataset.TO);
    }

    @RequestMapping(value = "/updateDataset")
    public ServiceStatus updateDataset(@RequestParam(name = "json") String json,HttpServletRequest request, HttpServletResponse response) {

    	User user = (User) request.getSession().getAttribute("user");
        return datasetService.update(user.getUsername(), json);
    }

    @RequestMapping(value = "/deleteDataset")
    public ServiceStatus deleteDataset(@RequestParam(name = "id") Long id,HttpServletRequest request, HttpServletResponse response) {

    	User user = (User) request.getSession().getAttribute("user");
        return datasetService.delete(user.getUsername(), id);
    }

    @RequestMapping(value = "/getDatasetCategoryList")
    public List<String> getDatasetCategoryList() {
        return datasetDao.getCategoryList();
    }


}
