package com.geo.presto.controller;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.facebook.presto.sql.SqlFormatter;
import com.facebook.presto.sql.parser.ParsingException;
import com.facebook.presto.sql.parser.SqlParser;
import com.facebook.presto.sql.tree.Statement;
import com.geo.presto.Constant;
import com.geo.presto.bean.User;
import com.geo.presto.bean.UserSql;
import com.geo.presto.bean.UserSqlExample;
import com.geo.presto.dao.UserSqlMapper;
import com.geo.presto.util.JsonUtil;
import com.geo.presto.util.SqlUtil;

@Controller
@RequestMapping("/sql/")
public class SqlController {

	private static Logger log = LoggerFactory.getLogger(SqlController.class);
	@Resource
	UserSqlMapper userSqlMapper;

	@RequestMapping(value = "sql", method = RequestMethod.GET)
	public @ResponseBody Object getSql(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		log.info("GETSQL!");
		User user = (User) request.getSession().getAttribute("user");
		if (user == null) {
			log.info("user is NULL!");
			return "请登录！";
		}
		request.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");
		List<UserSql> attribute = (List<UserSql>) request.getSession().getAttribute(Constant.COLLECT_SQL);
		if (attribute == null) {
			UserSqlExample example = new UserSqlExample();
			example.setOrderByClause("create_time_ desc");
			example.createCriteria().andUsernameEqualTo(user.getUsername());
			List<UserSql> selectByExample = userSqlMapper.selectByExample(example);
			request.getSession().setAttribute(Constant.COLLECT_SQL, selectByExample);
		}
		return attribute;
	}

	@RequestMapping(value = "sql", method = RequestMethod.POST)
	public @ResponseBody Object addSql(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		log.info("ADDSQL!");
		User user = (User) request.getSession().getAttribute("user");
		if (user == null) {
			return "请登录！";
		}
		String type = request.getParameter("type");
		Map<String, Object> resMap = new HashMap<String, Object>();
		if ("addSql".equals(type)) {
			request.setCharacterEncoding("UTF-8");
			response.setContentType("application/json");
			UserSqlExample example = new UserSqlExample();
			example.setOrderByClause("create_time_ desc");
			example.createCriteria().andUsernameEqualTo(user.getUsername());
			List<UserSql> attribute = userSqlMapper.selectByExample(example);
			String sql = request.getParameter("sql");
			String remark = request.getParameter("remark");
			String msg = null;
			if (sql != null && remark != null) {
				// remark = new String(remark.getBytes("ISO-8859-1"),"UTF-8");
			}
			for (UserSql userSql : attribute) {// 存在的sql收藏不重复添加
				if (userSql.getSql().equalsIgnoreCase(sql)) {
					log.info(user.getUsername() + " SQL:" + sql + "\t already exists!");
					msg = "2";
					resMap.put("msg", msg);
					return resMap;
				}
			}
			// 插入数据
			UserSql record = new UserSql();
			record.setUsername(user.getUsername());
			record.setSql(request.getParameter("sql"));
			record.setDesc(remark);
			record.setCreateTime(Calendar.getInstance().getTime());
			int flag = userSqlMapper.insertSelective(record);
			// 更新sql缓存
			example.setOrderByClause("create_time_ desc");
			attribute = userSqlMapper.selectByExample(example);
			request.getSession().setAttribute(Constant.COLLECT_SQL, attribute);
			log.info(user.getUsername() + " ADDSQL SUCCESS!\t sql:" + sql);
			msg = flag != -1 ? "0" : "1";
			resMap.put("msg", msg);
			return resMap;
		} else {
			if (user == null) {
				return "请登录！";
			}
			log.info("DELETESQL!");
			request.setCharacterEncoding("UTF-8");
			response.setContentType("application/json");
			String msg = null;
			// 删除sql
			String sql = request.getParameter("sql");
			UserSqlExample example = new UserSqlExample();
			example.createCriteria().andUsernameEqualTo(user.getUsername()).andSqlEqualTo(sql);
			int flag = userSqlMapper.deleteByExample(example);
			if (flag != -1) {// 更新sql缓存
				example.clear();
				example.setOrderByClause("create_time_ desc");
				example.createCriteria().andUsernameEqualTo(user.getUsername());
				List<UserSql> selectByExample = userSqlMapper.selectByExample(example);
				request.getSession().setAttribute(Constant.COLLECT_SQL, selectByExample);
			}
			log.info("DELETESQL SUCCESS!\t sql:" + sql);
			msg = flag != -1 ? "0" : "1";
			resMap.put("msg", msg);
			return resMap;
		}
	}

	@RequestMapping(value = "sql/{id:\\d+}", method = RequestMethod.DELETE)
	public @ResponseBody Object deleteSql(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		Map<String, Object> resMap = new HashMap<String, Object>();
		User user = (User) request.getSession().getAttribute("user");
		if (user == null) {
			return "请登录！";
		}
		log.info("DELETESQL!");
		request.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");
		String msg = null;
		// 删除sql
		String sql = request.getParameter("sql");
		UserSqlExample example = new UserSqlExample();
		example.createCriteria().andUsernameEqualTo(user.getUsername()).andSqlEqualTo(sql);
		int flag = userSqlMapper.deleteByExample(example);
		if (flag != -1) {// 更新sql缓存
			example.clear();
			example.setOrderByClause("create_time_ desc");
			example.createCriteria().andUsernameEqualTo(user.getUsername());
			List<UserSql> selectByExample = userSqlMapper.selectByExample(example);
			request.getSession().setAttribute(Constant.COLLECT_SQL, selectByExample);
		}
		log.info("DELETESQL SUCCESS!\t sql:" + sql);
		msg = flag != -1 ? "0" : "1";
		resMap.put("msg", msg);
		return resMap;
	}

	@RequestMapping(value = "format", method = RequestMethod.POST)
	public @ResponseBody Object format(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		HashMap<String, Object> retVal = new HashMap<String, Object>();

		try {
			Optional<String> queryOptional = Optional.ofNullable(request.getParameter("query"));
			queryOptional.ifPresent(query -> {
				try {
					SqlParser sqlParser = new SqlParser();
					Map<String, Object> propsAndSql = SqlUtil.pasreSessionPropsAndSql(query);
					Statement statement = sqlParser.createStatement((String) propsAndSql.get("querySql"));
					String formattedQuery = SqlFormatter.formatSql(statement);
					formattedQuery = formattedQuery.replaceAll("\\\"", "");
					log.info("formattedSQL:\n" + formattedQuery);
					String prex = "";
					if (((Map) propsAndSql.get("sessionProps")).size() > 0) {
						prex = StringUtils.substringBefore(query, (String) propsAndSql.get("querySql")) + "\n";
					}
					retVal.put("formattedQuery", prex + formattedQuery);
				} catch (ParsingException e) {
					retVal.put("errorLineNumber", e.getLineNumber());
					log.error(e.getMessage(), e);
					retVal.put("error", e.getMessage());
				}
			});
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			retVal.put("error", e.getMessage());
		}

		return retVal;

	}
}
