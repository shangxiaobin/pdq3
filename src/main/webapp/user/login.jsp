<%@ page language="java" pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.servletContext.contextPath}" />

<html lang="en"
	class="app js no-touch no-android chrome no-firefox no-iemobile no-ie no-ie10 no-ie11 no-ios no-ios7 ipad">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge;chrome=1">
<link href="favicon.ico" type="image/x-icon" rel=icon>
<link href="favicon.ico" type="image/x-icon" rel="shortcut icon">
<meta name="renderer" content="webkit">
<title>登录-Presto分布式查询系统 </title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet" href="${ctx}/bootstrap/css/bootstrap.min.css"	type="text/css">
<link rel="stylesheet" href="${ctx}/css/main.css" type="text/css">
<style type="text/css">
body{
	background-image: url('${ctx}/imgs/bg.jpg');
	margin-top:0px;
	background-repeat: no;
	background-position: center;
}
</style>
<script type="text/javascript" src="${ctx}/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript" src="${ctx}/js/lib/jquery.js"></script>
<script type="text/javascript" src="${ctx}/js/lib/jquery.md5.js"></script>
<script type="text/javascript" src="${ctx}/plugins/layer/layer.js"></script>



</head>
<body >
	<div class="container-full">
		<div class="login">
			<div class="alert alert-danger" id="error-msg" style="display: none;"></div>
			<div class="well" style="background-color: rgba(0, 0, 0, 0.3) !important; background: #000; filter: alpha(opacity = 50); *background: #000; *filter: alpha(opacity = 50); /*黑色透明背景结束*/ color: #FFF; bottom: 0px; right: 0px;">
				<div class="control-group">
					<h3 align="center" style="margin-top:5px;margin-bottom: 15px; font-weight: bold; color: white;">
						<img src="${ctx}/imgs/logo1.png" alt="Logo"style="width: 50px; height: 50px;"> Presto分布式查询系统
					</h3>
				</div>
				<form id="loginform" name="loginform" class="form-vertical"	action="${ctx}/user/loginCheck" method="post">					
				      <div class="form-group has-warning has-feedback">
				        <input class="form-control" placeholder="用户名" aria-describedby="inputSuccess2Status" type="text" placeholder="用户名" id="username" name="username"
							value="" >
				        <span class="glyphicon glyphicon-user form-control-feedback" aria-hidden="true"></span> 
				      </div>
				      <div class="form-group has-warning has-feedback">
				        <input class="form-control" type="password" placeholder="密码" id="password" name="password" aria-describedby="inputWarning2Status" >
				        <span class="glyphicon glyphicon-lock form-control-feedback" aria-hidden="true"></span>
				      </div>
				      <div style="width: 100%;height: 30px;text-align: right;">
				     	<button type="submit" class="btn btn-primary " id="login-submit" onclick="javascript:checkUserForm()" >登&nbsp;&nbsp;录</button> 
					</div>
				</form>
			</div>
			<!-- /.well -->
		</div>
		<!-- /.login -->
	</div>

	<script type="text/javascript">
		if ("${error}" != "") {
			layer.alert('${error}',{icon:5,skin: 'layer-ext-moon'});
			$("#username").val("");
			$("#password").val("");
		};
		function checkUserForm() {
			 //将明文密码进行md5加密之后再传输
			encryptPasswd=$.md5($("#password").val());
		    $("#password").val(encryptPasswd);
			document.loginform.submit();
		}
	</script>
</body>
</html>