package com.geo.presto.controller;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Created by yfyuan on 2016/7/25.
 * Modified by Chris Yuan 
 */
@RestController
@RequestMapping("/commons")
public class CommonsController {
	private com.geo.presto.dto.User dtoUser=new com.geo.presto.dto.User();
	// TODO：合并dto.User 和 bean.User 
    @RequestMapping(value = "/getUserDetail")
    public com.geo.presto.dto.User getUserDetail(HttpServletRequest request, HttpServletResponse response) {
    	com.geo.presto.bean.User user = (com.geo.presto.bean.User) request.getSession().getAttribute("user");
    	dtoUser.setUserId(user.getUsername());
    	dtoUser.setUsername(user.getUsername());
        return dtoUser;
    }
}
