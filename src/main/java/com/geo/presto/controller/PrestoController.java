package com.geo.presto.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.facebook.presto.client.ErrorLocation;
import com.facebook.presto.client.QueryError;
import com.geo.presto.Constant;
import com.geo.presto.bean.Dynatree;
import com.geo.presto.bean.PrestoConfig;
import com.geo.presto.bean.PrestoQueryResult;
import com.geo.presto.bean.QueryDetail;
import com.geo.presto.bean.User;
import com.geo.presto.bean.UserCatalogKey;
import com.geo.presto.cache.CacheManager;
import com.geo.presto.dto.DataProviderResult;
import com.geo.presto.exception.QueryErrorException;
import com.geo.presto.services.PrestoService;
import com.geo.presto.util.FilterUtil;
import com.geo.presto.util.JsonUtil;
import com.geo.presto.util.SqlUtil;

@Controller
@RequestMapping("/presto/")
public class PrestoController {
	private static Logger log = LoggerFactory.getLogger(PrestoController.class);

	@Autowired
	private PrestoConfig prestoConfig;
	@Autowired
	private CacheManager<DataProviderResult> cacheManager;
	@Autowired
	private PrestoService prestoService;

	@RequestMapping(value = "query")
	public @ResponseBody Map<String, Object> query(HttpServletRequest request, HttpServletResponse response) {
		String sql = request.getParameter("query").replace("\n", " ");
		User user = (User) request.getSession().getAttribute("user");
		@SuppressWarnings("unchecked")
		List<UserCatalogKey> userCatalogList = (List<UserCatalogKey>) request.getSession().getAttribute(Constant.CATALOG_KEY);
		return queryResult(user, userCatalogList, sql);
	}

	private HashMap<String, Object> queryResult(User user, List<UserCatalogKey> userCatalogList, String sql) {
		HashMap<String, Object> retVal = new HashMap<String, Object>();
		try {
			Map<String, Object> map = SqlUtil.pasreSessionPropsAndSql(sql);
			sql = (String) map.get("querySql");
			FilterUtil.filterSqlQuery(userCatalogList, sql);
			Optional<String> queryOptional = Optional.ofNullable(sql);
			queryOptional.ifPresent(query -> {
				try {
					PrestoConfig config = FilterUtil.getPrestoConfig(user.getUsername(), (String) map.get("querySql"));
					config.setSessionProps((Map<String, String>) map.get("sessionProps"));
					PrestoQueryResult prestoQueryResult = prestoService.doQuery(query, config);
					if (prestoQueryResult.getUpdateType() == null) {

						prestoQueryResult = FilterUtil.filterQueryResult(prestoQueryResult, userCatalogList, (String) map.get("querySql"));
						if (query.contains("information_schema.columns where table_schema='")) {
							retVal.put("results", JsonUtil.FormatResults(prestoQueryResult.getRecords()));

						} else {
							retVal.put("headers", prestoQueryResult.getColumns());
							retVal.put("results", prestoQueryResult.getRecords());

							// 将查询出的数据添加到缓存中
					Map<String, Object> temp = new HashMap<>();
					temp.put("sql", query);
					String keys = "1_" + SqlUtil.formatSqlCacheKey(temp.toString());
					if (cacheManager.get(keys) == null && prestoQueryResult.getColumns() != null && !prestoQueryResult.getColumns().get(0).equals("Catalog") && !prestoQueryResult.getColumns().get(0).equals("Schema") && !prestoQueryResult.getColumns().get(0).equals("Table") && !prestoQueryResult.getColumns().get(0).equals("Column")) {
						List<String[]> list = new LinkedList<>();
						int columnCount = prestoQueryResult.getColumns().size();
						String[] columns = new String[columnCount];
						for (int i = 0; i < columnCount; i++) {
							columns[i] = prestoQueryResult.getColumns().get(i);
						}
						list.add(columns);

						for (int i = 0; i < prestoQueryResult.getRecords().size(); i++) {
							columns = new String[columnCount];
							for (int j = 0; j < prestoQueryResult.getRecords().get(i).size(); j++) {
								if(prestoQueryResult.getRecords().get(i).get(j)!=null){
									columns[j] = prestoQueryResult.getRecords().get(i).get(j).toString();
								}else{
									columns[j]=null;
								}
								
							}
							list.add(columns);
						}

						String[][] data = list.toArray(new String[][] {});
						DataProviderResult cacheResult = new DataProviderResult(data, "1", prestoQueryResult.getRecords().size());
						long expire = 12 * 60 * 60 * 1000;
						cacheManager.put(keys, cacheResult, expire);
					}
				}
				Optional<String> warningMessageOptinal = Optional.ofNullable(prestoQueryResult.getWarningMessage());
				warningMessageOptinal.ifPresent(warningMessage -> {
					retVal.put("warn", warningMessage);
				});
			} else {
				retVal.put("success", prestoQueryResult.getUpdateType() + " SUCCESSFULLY !");
			}
		} catch (QueryErrorException e) {
			log.error(" " + e.getMessage());
			Optional<QueryError> queryErrorOptional = Optional.ofNullable(e.getQueryError());
			queryErrorOptional.ifPresent(queryError -> {
				Optional<ErrorLocation> errorLocationOptional = Optional.ofNullable(queryError.getErrorLocation());
				errorLocationOptional.ifPresent(errorLocation -> {
					int errorLineNumber = errorLocation.getLineNumber();
					retVal.put("errorLineNumber", errorLineNumber);
				});
			});
			retVal.put("error", e.getCause().getMessage());

		}
	})		;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			retVal.put("error", e.getMessage());
			return retVal;
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			retVal.put("error", e.getMessage());
			return retVal;
		}
		return retVal;
	}

	
	@RequestMapping(value = "state")
	public @ResponseBody Object state(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String prestoCoordinatorServer = prestoConfig.getPrestoCoordinatorServer();
		String results = Request.Get(prestoCoordinatorServer + "/v1/query").execute().returnContent().asString();

		User user = (User) request.getSession().getAttribute("user");
		if(user == null){
			response.sendRedirect("/user/login");
		}
		Object ret = filterQueryByState(results, user.getUsername(),request);
		request.setCharacterEncoding("UTF-8");
		response.setHeader("content-type", "text/html;charset=UTF-8");
		return ret;
		
	}

	private Object filterQueryByState(String queries, String user,HttpServletRequest request) {
		String state = request.getParameter("state");
		String winId = request.getParameter("winId")==null?"":request.getParameter("winId");
		String qkey = request.getParameter("queryId")==null?"":request.getParameter("queryId");
		HttpSession session=request.getSession();
		
		HashMap<String, Object> retVal = new HashMap<String, Object>();
		List<QueryDetail> filteredQueries = new ArrayList<QueryDetail>();
		List<QueryDetail> queriesLisit = JSON.parseObject(queries, new TypeReference<List<QueryDetail>>() {
		});
		String sessionQueryId="";		
		for (QueryDetail queryDetail : queriesLisit) {
			if (queryDetail.getState().equals(state.toUpperCase()) && user.equals(queryDetail.getSession().getUser())) {
				String queryId=queryDetail.getQueryId();
				sessionQueryId=(String) session.getServletContext().getAttribute(qkey);
				//第一次调用， 如果session里没有当前窗口发起查询的queryid，将当前查询id放入session
				if(sessionQueryId==null){
					String olderQueryId=(String) session.getServletContext().getAttribute(queryId);
					if(olderQueryId!=null)
						continue;//由客户端轮询检查 跳过已存在session中的queryId
					session.getServletContext().setAttribute(queryId, queryId);
					queryDetail.setWinId(winId);
					filteredQueries.add(queryDetail);
					break;
				}
				//第二次调用，如果遍历出来的queryid跟session里的queryid相同，拿到最新状态，直接返回。
				else if(queryId.equals(sessionQueryId)){
					queryDetail.setWinId(winId);
					filteredQueries.add(queryDetail);
					break;
				}
			}
		}
		if (filteredQueries.size() == 0) {
			// 没查到数据清除当前窗口的session
			log.info("clean current query session.");
			session.getServletContext().removeAttribute(qkey);
			retVal.put("error", "无数据");
			return retVal;
		} else {
			log.info("filteredQueries\t" + filteredQueries);
			log.info("WinId:{"+winId+"},QueryId:{"+qkey+"},Return:{"+filteredQueries.toString()+"}");
			return filteredQueries;
		}

	}

	@RequestMapping(value = "kill")
	public void kill(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Optional<String> queryIdOptinal = Optional.ofNullable(request.getParameter("queryId"));
		queryIdOptinal.ifPresent(queryId -> {
			String prestoCoordinatorServer = prestoConfig.getPrestoCoordinatorServer();
			try {
				Request.Delete(prestoCoordinatorServer + "/v1/query/" + queryId).execute();
			} catch (IOException e) {
				log.info("[presto-kill]" + e.getCause().getMessage().toString());
				throw new RuntimeException(e.getCause().getMessage());
			}
		});
	}

	@RequestMapping(value = "search", method = RequestMethod.GET)
	public @ResponseBody Object search(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String content = request.getParameter("content").trim();
		StringBuilder sb = new StringBuilder();
		String[] conArr = content.split(",");
		for (String con : conArr) {
			sb.append(" table_name LIKE '%").append(con).append("%'").append(" OR");
		}
		sb.delete(sb.length() - 2, sb.length());

		String sql = "SELECT table_cat AS catalog, table_schem AS schema, table_name AS table_name FROM system.jdbc.tables WHERE table_type='TABLE' and (" + sb.toString() + " )";
		log.info("SEARCH SQL:" + sql);
		User user = (User) request.getSession().getAttribute("user");
		@SuppressWarnings("unchecked")
		List<UserCatalogKey> userCatalogList = (List<UserCatalogKey>) request.getSession().getAttribute(Constant.CATALOG_KEY);
		HashMap<String, Object> result = queryResult(user, userCatalogList, sql);
		List<Dynatree> resultDynatree = new ArrayList<Dynatree>();

		HashMap<String, Dynatree> treeMap = new HashMap<String, Dynatree>();
		List<List<String>> list = (List<List<String>>) result.get("results");
		for (List<String> arr : list) {

			Dynatree catalog = new Dynatree();
			catalog.setKey(arr.get(0));
			catalog.setTitle(arr.get(0));

			Dynatree schema = new Dynatree();
			schema.setKey(arr.get(1));
			schema.setTitle(arr.get(1));

			Dynatree table = new Dynatree();
			table.setIsLazy(true);
			table.setExpand(false);
			table.setKey(arr.get(2));
			table.setTitle(arr.get(2));

			if (treeMap.get(arr.get(0)) == null) {// 新节点
				List<Dynatree> schemaList = new ArrayList<Dynatree>();
				schemaList.add(table);
				schema.setChildren(schemaList);

				List<Dynatree> catalogList = new ArrayList<Dynatree>();
				catalogList.add(schema);
				catalog.setChildren(catalogList);

				treeMap.put(arr.get(0), catalog);
			} else {// 第一级节点已存在
				catalog = treeMap.get(arr.get(0));

				if (catalog.getChildren().contains(schema)) {// 第二级节点已存在
					for (int j = 0; j < catalog.getChildren().size(); j++) {
						Dynatree t = catalog.getChildren().get(j);
						if (t.equals(schema)) {
							t.getChildren().add(table);
						}
					}
				} else {
					List<Dynatree> schemaList = new ArrayList<Dynatree>();
					schemaList.add(table);
					schema.setChildren(schemaList);
					List<Dynatree> catalogList = catalog.getChildren();
					if (catalogList == null) {
						catalogList = new ArrayList<Dynatree>();
					}
					catalogList.add(schema);
					catalog.setChildren(catalogList);
				}
			}
			treeMap.put(arr.get(0), catalog);
		}
		Iterator it = treeMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			resultDynatree.add((Dynatree) entry.getValue());
		}
		return resultDynatree;
	}
	//
}
