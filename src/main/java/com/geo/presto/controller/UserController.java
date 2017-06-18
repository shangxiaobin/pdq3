package com.geo.presto.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.geo.presto.Constant;
import com.geo.presto.bean.User;
import com.geo.presto.bean.UserCatalogExample;
import com.geo.presto.bean.UserCatalogKey;
import com.geo.presto.bean.UserExample;
import com.geo.presto.bean.UserSql;
import com.geo.presto.bean.UserSqlExample;
import com.geo.presto.bean.PrestoConfig;
import com.geo.presto.dao.UserCatalogMapper;
import com.geo.presto.dao.UserSqlMapper;
import com.geo.presto.services.UserService;
import com.geo.presto.util.PropertiesUtils;

@Controller
@RequestMapping("/user/")
public class UserController {
	private static Logger log = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserService userService;
	@Resource
	UserSqlMapper userSqlMapper;
	@Resource
	UserCatalogMapper userCatalogMapper;

	@RequestMapping("loginCheck")
	public String loginCheck(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		User user = userService.selectByPrimaryKey(username);
		if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password) && user != null && username.equals(user.getUsername()) && password.equals(user.getPassword())) {
			try {
				user.setPassword("");
				request.getSession().setAttribute("user", user);
				// request.getSession().setMaxInactiveInterval(5);
				UserSqlExample example = new UserSqlExample();
				example.setOrderByClause("create_time_ desc");
				example.createCriteria().andUsernameEqualTo(username);
				List<UserSql> collectSqlList = userSqlMapper.selectByExample(example);
				request.getSession().setAttribute(Constant.COLLECT_SQL, collectSqlList);

				UserCatalogExample uc_example = new UserCatalogExample();
				uc_example.createCriteria().andUsernameEqualTo(username);
				List<UserCatalogKey> catalogList = userCatalogMapper.selectByExample(uc_example);
				String uck = "";
				for (UserCatalogKey userCatalogKey : catalogList) {
					uck += userCatalogKey.getCatalog() + "." + userCatalogKey.getSchema() + ",";
				}
				request.getSession().setAttribute(Constant.CATALOG_KEY, catalogList);
				log.info("LoginInfo:[" + user.getUsername() + "][" + uck + "]");
				// rd.forward(request, response);
				return "redirect:/workspace";
			} catch (Exception e) {
				log.error(e.getMessage());
				e.printStackTrace();
			}
		} else {
			request.setCharacterEncoding("UTF-8");
			request.setAttribute("error", "登录失败!");
			// rd = request.getRequestDispatcher("login.jsp");
			response.setHeader("content-type", "text/html;charset=UTF-8");
			try {
				// rd.forward(request, response);
				return "user/login";
			} catch (Exception e) {
				log.error(e.getMessage());
				e.printStackTrace();
			}
		}

		return null;
	}

	@RequestMapping("login")
	public String login(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		return "user/login";
	}

	@RequestMapping("logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		User user = (User) request.getSession().getAttribute("user");
		if (user != null) {
			request.getSession().removeAttribute("user");
			request.getSession().removeAttribute(Constant.CATALOG_KEY);
			request.getSession().removeAttribute(Constant.COLLECT_SQL);
			return "user/login";
		}
		return null;
	}

	@RequestMapping("user")
	@ResponseBody
	public Object user(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Object object = null;
		String type = request.getParameter("type");

		User user = (User) request.getSession().getAttribute("user");
		if (user == null) {
			response.sendRedirect("login");
		}
		String msg = null;
		Map<String, Object> resMap = new HashMap<String, Object>();

		if (StringUtils.isEmpty(type)) {
			msg = "操作失败";
			resMap.put("msg", msg);
			object = resMap;
			return object;
		} else if (type.equals("changePw")) {
			String oldpw = request.getParameter("oldpw");
			String newpw = request.getParameter("newpw2");
			User user2 = userService.selectByPrimaryKey(user.getUsername());
			if (user2.getPassword().equals(oldpw)) {
				user2.setPassword(newpw);
				int r = userService.updateByPrimaryKeySelective(user2);
				if (r != -1) {
					msg = "修改成功";
					resMap.put("msg", msg);
					object = resMap;
					return object;
				}
			} else {
				msg = "旧密码错误";
				resMap.put("msg", msg);
				object = resMap;
				return object;
			}
		} else if (type.equals("addUser")) {
			if (user.getUsername().equals("admin")) {
				User record = new User();
				record.setUsername(request.getParameter("username"));
				record.setPassword(request.getParameter("password"));
				User user2 = userService.selectByPrimaryKey(request.getParameter("username"));
				if (user2 != null) {
					msg = "用户名存在";
					resMap.put("msg", msg);
					object = resMap;
					return object;
				}
				int r = userService.insertSelective(record);
				if (r != -1) {
					msg = "操作成功";
				} else {
					msg = "操作失败";
				}
			} else {
				msg = "无权限";
			}
			resMap.put("msg", msg);
			object = resMap;
			return object;
		} else if (type.equals("getUser")) {
			if (user.getUsername().equals("admin")) {
				UserExample example = new UserExample();
				example.createCriteria().andUsernameIsNotNull().andUsernameNotEqualTo("admin");
				List<User> userList = userService.selectByExample(example);
				for (User user2 : userList) {
					user2.setPassword("");
				}
				object = userList;
				return object;
			} else {
				msg = "无权限";
				resMap.put("msg", msg);
				object = resMap;
				return object;
			}
		} else if (type.equals("deleteUser")) {
			if (user.getUsername().equals("admin")) {
				int r = userService.deleteByPrimaryKey(request.getParameter("username"));
				if (r != -1) {
					msg = "操作成功";
				} else {
					msg = "操作失败";
				}
			} else {
				msg = "无权限";
			}
			resMap.put("msg", msg);
			object = resMap;
			return object;
		} else if (type.equals("getAllUserName")) {
			if (user.getUsername().equals("admin")) {
				UserExample example = new UserExample();
				example.createCriteria().andUsernameIsNotNull();
				List<User> userList = userService.selectByExample(example);
				for (User user2 : userList) {
					user2.setPassword(null);
				}
				object = userList;
				return object;
			} else {
				msg = "无权限";
				resMap.put("msg", msg);
				object = resMap;
				return object;
			}
		}

		return object;

	}

	@RequestMapping("userCatalog")
	@ResponseBody
	public Object userCatalog(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		Object object = null;
		User user = (User) request.getSession().getAttribute("user");
		if (user == null) {
			response.sendRedirect("login");
		}

		String type = request.getParameter("type");
		String msg = null;
		Map<String, Object> resMap = new HashMap<String, Object>();
		if (StringUtils.isEmpty(type)) {
			msg = "操作失败";
			resMap.put("msg", msg);
			object = resMap;
			return object;
		} else if (type.equals("addUserCatalog")) {
			if (user.getUsername().equals("admin")) {
				User user2 = userService.selectByPrimaryKey(request.getParameter("username"));
				if (user2 == null) {
					msg = "不存在此用户,操作无效";
					resMap.put("msg", msg);
					object = resMap;
					return object;
				}
				UserCatalogExample example = new UserCatalogExample();
				example.createCriteria().andCatalogEqualTo(request.getParameter("catalog")).andUsernameEqualTo(request.getParameter("username")).andSchemaEqualTo(request.getParameter("schema"));
				List<UserCatalogKey> list = userCatalogMapper.selectByExample(example);
				if (list != null && list.size() > 0) {
					msg = "重复添加,操作无效";
					resMap.put("msg", msg);
					object = resMap;
					return object;
				}
				UserCatalogKey userCatalog = new UserCatalogKey();
				userCatalog.setUsername(request.getParameter("username"));
				userCatalog.setCatalog(request.getParameter("catalog"));
				userCatalog.setSchema(request.getParameter("schema"));
				int r = userCatalogMapper.insertSelective(userCatalog);
				if (r != -1) {
					msg = "操作成功";
				} else {
					msg = "操作失败";
				}
			} else {
				msg = "无权限";
			}
			resMap.put("msg", msg);
			object = resMap;
			return object;
		} else if (type.equals("getUserCatalog")) {
			if (user.getUsername().equals("admin")) {
				UserCatalogExample example = new UserCatalogExample();
				example.createCriteria().andUsernameIsNotNull();
				List<UserCatalogKey> list = userCatalogMapper.selectByExample(example);
				object = list;
				return object;
			} else {
				msg = "无权限";
				resMap.put("msg", msg);
				object = resMap;
				return object;
			}
		} else if (type.equals("deleteUserCatalog")) {
			if (user.getUsername().equals("admin")) {
				UserCatalogExample example = new UserCatalogExample();
				example.createCriteria().andUsernameEqualTo(request.getParameter("username")).andCatalogEqualTo(request.getParameter("catalog")).andSchemaEqualTo(request.getParameter("schema"));
				int r = userCatalogMapper.deleteByExample(example);
				if (r != -1) {
					msg = "操作成功";
				} else {
					msg = "操作失败";
				}
			} else {
				msg = "无权限";
			}
			resMap.put("msg", msg);
			object = resMap;
			return object;
		} else if (type.equals("editUserCatalog")) {

		} else if (type.equals("getColumns")) {
			RequestDispatcher dispatcher = request.getRequestDispatcher("/presto/query");
			dispatcher.forward(request, response);
		}
		return object;

	}
	
	



}
