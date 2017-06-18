package com.geo.presto.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.geo.presto.bean.User;

public class SessionFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest servletRequest = (HttpServletRequest) request;
		HttpServletResponse servletResponse = (HttpServletResponse) response;
		HttpSession session = servletRequest.getSession();
		String path = servletRequest.getRequestURI();
		// 登陆页面无需过滤
		if (path.indexOf("/user/login") > -1 || path.indexOf("/lib") > -1  || path.indexOf("/bootstrap") > -1 || path.indexOf(".html") > -1 ||path.indexOf("/css") > -1 || path.indexOf("/img") > -1
				|| path.indexOf("/js") > -1 || path.indexOf("/fonts") > -1|| path.indexOf("/layer") > -1) {
			chain.doFilter(servletRequest, servletResponse);
			return;
		}
		User user = (User) session.getAttribute("user");
		if (user == null || user.getUsername().equals("")) {
			//ajax session 过期处理
			//1:判断是否是ajax请求
			if (servletRequest.getHeader("Accept") != null 
			                        && servletRequest.getHeader("Accept").contains("application/json")) {   
			    //向http头添加 状态 sessionstatus
				servletResponse.setHeader("sessionstatus","timeout");
				servletResponse.setStatus(403);
			    //向http头添加登录的url
				servletResponse.addHeader("loginPath", "login");
			    chain.doFilter(request, response);
			    return ;
			}
			
			// 跳转到登陆页面
			servletResponse.sendRedirect("/user/login");
		} else {
			// 已经登陆,继续此次请求
			chain.doFilter(request, response);
		}
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}
}
