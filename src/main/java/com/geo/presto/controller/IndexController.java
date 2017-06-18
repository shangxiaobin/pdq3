
package com.geo.presto.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.geo.presto.bean.PrestoConfig;
import com.geo.presto.bean.User;

@Controller
@RequestMapping("/")
public class IndexController {
	@Autowired
	private PrestoConfig prestoConfig;
	
	
	@RequestMapping("")
	public String index(HttpServletRequest request, HttpServletResponse response) throws IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user == null) {
			return "redirect:/user/login";
		} else {
			return "workspace";
		}
	}
	
	@RequestMapping("index")
	public String index1(HttpServletRequest request, HttpServletResponse response) throws IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user == null) {
			return "redirect:/user/login";
		} else {
			return "redirect:/workspace";
		}
	}
		
	@RequestMapping("workspace")
	public String wokspace(HttpServletRequest request, HttpServletResponse response) throws IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user == null) {
			return "redirect:/user/login";
		} else {
			request.setAttribute("prestoConfig", prestoConfig.getPrestoCoordinatorServer());	
			return "workspace";
		}
	}
	
	@RequestMapping("board")
	public String board(HttpServletRequest request, HttpServletResponse response) throws IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user == null) {
			return "redirect:/user/login";
		} else if(user!=null&&!user.getUsername().equals("admin")) {
			return "redirect:/workspace";
		}else{
			return "board";
			
		}
	}
	
	@RequestMapping("datasetBoard")
	public String datasetBoard(HttpServletRequest request, HttpServletResponse response) throws IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user == null) {
			return "redirect:/user/login";
		} else if(user!=null&&!user.getUsername().equals("admin")) {
			return "redirect:/workspace";
		}else{
			return "datasetBoard";
			
		}
	}
	@RequestMapping("myboard")
	public String myboard(HttpServletRequest request, HttpServletResponse response) throws IOException {
		User user = (User) request.getSession().getAttribute("user");
		if (user == null) {
			return "redirect:/user/login";
		} else{
			return "myboard";
			
		}
	}
	
	
	

}
