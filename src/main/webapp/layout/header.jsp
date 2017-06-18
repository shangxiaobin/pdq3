<%@ page language="java" pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="<%=request.getContextPath()%>" />

<header class="main-header" >
<nav class="navbar navbar-static-top">
    <div class="container-fluid">
    <div class="navbar-header">
      <a href="/workspace" class="navbar-brand" style="padding: 6px 15px;">
      <span style="color: #ffffff; font-weight: bold;">
      <img alt="Logo" src="${ctx}/imgs/logo1.png" width="34" height="34" /> Presto分布式查询系统 </span></a>
      <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar-collapse">
        <i class="fa fa-bars"></i>
      </button>
    </div>

    <!-- Collect the nav links, forms, and other content for toggling -->
    <div class="collapse navbar-collapse" id="navbar-collapse">
      <ul class="nav navbar-nav">
       	<li><a href="/workspace">即席查询</a></li>
        <li><a href="javascript:void(0)" id="recent_queries">最近查询</a></li>
        <li><a href="javascript:void(0)" id="my_queries">我的收藏</a></li>
        <!-- <li><a href="javascript:void(0)" id="all_queries">全部查询</a></li> -->
  <!--       <li><a href="/myboard" id="my_board">我的仪表盘</a></li> -->
        <c:if test="${sessionScope.user.username == 'admin'}">
	        <li class="dropdown">
	          <a href="#" class="dropdown-toggle" data-toggle="dropdown">系统管理 <span class="caret"></span></a>
	          <ul class="dropdown-menu" role="menu">
	            <li><a href="javascript:void(0)" id="userManager">用户管理</a></li>
				<li><a href="javascript:void(0)" id="userCatalog">数据权限管理</a></li> 
				<li role="separator" class="divider"></li>
				 <li><a href="javascript:openMonitorPage();" id="monitor">查询监控</a></li>            
	          </ul>
	        </li>
		</c:if>
<%-- 		 <c:if test="${sessionScope.user.username == 'admin'}">
	        <li class="dropdown">
	          <a href="#" class="dropdown-toggle" data-toggle="dropdown">配置<span class="caret"></span></a>
	          <ul class="dropdown-menu" role="menu">
	            <li><a href="/board#/config/datasource" id="datasource">数据源管理</a></li>
				<li><a href="/board#/config/dataset" id="dataset">数据集管理</a></li>
				<li><a href="/board#/config/widget/" id="widget">图表设计</a></li>
				<li><a href="/board#/config/board" id="board">仪表盘设计</a></li>
				<li><a href="/board#/config/category" id="category">仪表盘分类</a></li>        
	          </ul>
	        </li>
		</c:if> --%>
      </ul>
<!--       <form class="navbar-form navbar-left" role="search">
        <div class="form-group">
          <input type="text" class="form-control" id="navbar-search-input" placeholder="搜索">
        </div>
      </form> -->
      <ul class="nav navbar-nav navbar-right">
        <li class="dropdown">
          <a href="#" class="dropdown-toggle" data-toggle="dropdown"> ${sessionScope.user.username} <span class="caret"></span></a>
          <ul class="dropdown-menu" role="menu">
			<li><a href="javascript:void(0)" onclick="changePw();">修改密码</a></li>
			<li><a href="javascript:void(0)" onclick="logout();">退出</a></li>
          </ul>
        </li>
      </ul>
    </div><!-- /.navbar-collapse -->
    </div><!-- /.container-fluid -->
  </nav>
</header>  

<script type="text/javascript">
	function openMonitorPage(){
		window.open('${prestoConfig}');
	}


	var user = '${sessionScope.user.username}';
	if (user == '') {
		top.location.href = "login.jsp";
	}
	
	function logout() {

		layer.confirm('您是否确认退出登录?', {
			btn : [ '确定', '取消' ]
		}, function() {
			document.location.href = "${ctx}/user/logout";
		}, function() {
			layer.msg('取消', {
				time : 1000
			});
		});
	}
	function sendPw(){
		var oldpw = $("#oldpw").val();
		if(oldpw==null || oldpw==""){
			layer.msg("旧密码为空");
			return;
		}
		var newpw1 = $("#newpw1").val();
		if(newpw1==null || newpw1==""){
			layer.msg("新密码为空");
			return;
		}
		var newpw2 = $("#newpw2").val();
		if(newpw2==null || newpw2==""){
			layer.msg("确认密码为空");
			return;
		}
		
		if(newpw2!=newpw1){
			layer.msg("确认密码为空不一致");
			return;
		}

		var enc_oldpw=$.md5(oldpw);
		var enc_newpw2=$.md5(newpw2);

		
		var requestURL = "${ctx}/user/user";
		var requestData = {
			"type" : "changePw",
			"oldpw" : enc_oldpw,
			"newpw2" : enc_newpw2
		};
		$.post(requestURL, requestData, function(data){
			layer.msg(data.msg);
			if(data.msg =="修改成功"){
				layer.close(index);
			}
		}, "json");
	}
	var index=null;
	function canncelPw(){
		layer.close(index);
	}
	function changePw() {
		index = layer.open({
					title : '修改密码',
					type : 1,
					content : ['<div style="margin: 10px;margin-top: 20px">',
							'<div style="margin-bottom: 4px;text-align:right;" class="layui-layer-content"> 原始密码: <input id="oldpw" type="password" class="layui-layer-input" value=""> </div>',
							'<div style="margin-bottom: 4px;text-align:right;"class="layui-layer-content"> 新&nbsp;&nbsp;密&nbsp;&nbsp;码: <input id="newpw1" type="password" class="layui-layer-input" value=""> </div>',
							'<div style="margin-bottom: 4px;text-align:right;"class="layui-layer-content"> 确认密码: <input id="newpw2" type="password" class="layui-layer-input" value=""> </div>',
							'<div style="text-align:right;padding-right:0;" class="layui-layer-btn"><button type="button" href="javascript:void(0)" onclick="sendPw()" class="btn btn-primary">确定</button>&nbsp;&nbsp;',
							'<button style="margin-left: 2px" type="button" href="javascript:void(0)" onclick="canncelPw()"  class="btn btn-default">取消</button></div>',
							'</div>'
							]
							.join("")
				});

	}
	$(function() {
		$("#all_queries").on('click', function() {
			layer.open({
				title : '全部查询',
				type : 2,
				area : [ '800px', '530px' ],
				fix : false,
				maxmin : true,
				content : '${ctx}/presto/executions.html'
			});

		});
		$("#recent_queries").on('click', function() {
			layer.open({
				title : '最近查询TOP100',
				type : 2,
				area : [ '800px', '530px' ],
				fix : false,
				maxmin : true,
				content : '${ctx}/presto/recent.html'
			});

		});
		$("#my_queries").on('click', function() {
			layer.open({
				title : '我的收藏',
				type : 2,
				area : [ '800px', '530px' ],
				fix : false,
				maxmin : true,
				content : '${ctx}/sql/myqueries.html'
			});

		});
		$("#userManager").on('click', function() {
			layer.open({
				title : '用户管理',
				type : 2,
				area : [ '800px', '530px' ],
				fix : false,
				maxmin : true,
				content : '${ctx}/user/manager.html'
			});

		});
		$("#userCatalog").on('click', function() {
			layer.open({
				title : '数据权限管理',
				type : 2,
				area : [ '800px', '530px' ],
				fix : false,
				maxmin : true,
				content : '${ctx}/user/catalog.html'
			});

		});
		
	});
</script>