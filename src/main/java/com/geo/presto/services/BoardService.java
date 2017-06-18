package com.geo.presto.services;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.geo.presto.bean.DashboardBoard;
import com.geo.presto.bean.DashboardWidget;
import com.geo.presto.dao.BoardDao;
import com.geo.presto.dao.WidgetDao;
import com.geo.presto.dto.DataProviderResult;
import com.geo.presto.dto.ViewDashboardBoard;
import com.geo.presto.dto.ViewDashboardWidget;
import com.google.common.base.Functions;
import com.google.common.collect.Maps;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yfyuan on 2016/8/23.
 */
@Repository
public class BoardService {

    @Autowired
    private BoardDao boardDao;

    @Autowired
    private WidgetDao widgetDao;

    @Autowired
    private CachedDataProviderService dataProviderService;

    public ViewDashboardBoard getBoardData(Long id) {
        DashboardBoard board = boardDao.getBoard(id);
        JSONObject layout = JSONObject.parseObject(board.getLayout());
        JSONArray rows = layout.getJSONArray("rows");
        for (Object row : rows) {
            JSONObject o = (JSONObject) row;
            if ("param".equals(o.getString("type"))) {
                continue;
            }
            JSONArray widgets = o.getJSONArray("widgets");
            for (Object w : widgets) {
                JSONObject ww = (JSONObject) w;
                Long widgetId = ww.getLong("widgetId");
                DashboardWidget widget = widgetDao.getWidget(widgetId);
                JSONObject dataJson = JSONObject.parseObject(widget.getData());
                //DataProviderResult data = dataProviderService.getData(dataJson.getLong("datasource"), Maps.transformValues(dataJson.getJSONObject("query"), Functions.toStringFunction()));
                JSONObject widgetJson = (JSONObject) JSONObject.toJSON(new ViewDashboardWidget(widget));
                //widgetJson.put("queryData", data.getData());
                ww.put("widget", widgetJson);
            }
        }
        ViewDashboardBoard view = new ViewDashboardBoard(board);
        view.setLayout(layout);
        return view;
    }

    public ServiceStatus save(String userId, String json) {
        JSONObject jsonObject = JSONObject.parseObject(json);
        DashboardBoard board = new DashboardBoard();
        board.setUserId(userId);
        board.setName(jsonObject.getString("name"));
        board.setCategoryId(jsonObject.getLong("categoryId"));
        board.setLayout(jsonObject.getString("layout"));

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("user_id", board.getUserId());
        paramMap.put("board_name", board.getName());
        if (boardDao.countExistBoardName(paramMap) <= 0) {
            boardDao.save(board);
            return new ServiceStatus(ServiceStatus.Status.Success, "success");
        } else {
            return new ServiceStatus(ServiceStatus.Status.Fail, "名称已存在");
        }
    }

    public ServiceStatus update(String userId, String json) {
        JSONObject jsonObject = JSONObject.parseObject(json);
        DashboardBoard board = new DashboardBoard();
        board.setUserId(userId);
        board.setName(jsonObject.getString("name"));
        board.setCategoryId(jsonObject.getLong("categoryId"));
        board.setLayout(jsonObject.getString("layout"));
        board.setId(jsonObject.getLong("id"));

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("board_id", board.getId());
        paramMap.put("user_id", board.getUserId());
        paramMap.put("board_name", board.getName());
        if (boardDao.countExistBoardName(paramMap) <= 0) {
            boardDao.update(board);
            return new ServiceStatus(ServiceStatus.Status.Success, "success");
        } else {
            return new ServiceStatus(ServiceStatus.Status.Fail, "名称已存在");
        }
    }

    public String delete(String userId, Long id) {
        boardDao.delete(id, userId);
        return "1";
    }
}
