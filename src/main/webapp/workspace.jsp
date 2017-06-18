<%@ page language="java" pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="<%=request.getContextPath()%>" />

<!DOCTYPE html>
<html lang="en" >
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>PDQ</title>
    <!-- Tell the browser to be responsive to screen width -->
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
    <link href="${ctx}/css/theme.default.css" rel="stylesheet">
    <!-- Bootstrap 3.3.6 -->
   <%--  <link rel="stylesheet" href="${ctx}/bootstrap/css/bootstrap.css"> --%>
   <link href="${ctx}/css/bootstrap.min.css" rel="stylesheet"> 
    <!-- Font Awesome -->
    <link rel="stylesheet" href="${ctx}/css/font-awesome.min.css">
    <!-- Ionicons -->
    <link rel="stylesheet" href="${ctx}/css/ionicons.min.css">
    <!-- Theme style -->
    <link rel="stylesheet" href="${ctx}/dist/css/AdminLTE.css">

    <link rel="stylesheet" href="${ctx}/dist/css/skins/skin-blue.min.css">

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
    <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->






<link href="${ctx}/css/ui.dynatree.css" rel="stylesheet">
<link type="text/css" href="${ctx}/css/custom-theme/jquery-ui-1.10.0.custom.css" rel="stylesheet">
<link href="${ctx}/css/defaultTheme.css" rel="stylesheet">



<link href="${ctx}/css/main.css" rel="stylesheet">
<script type="text/javascript" src="${ctx}/js/lib/jquery.js"></script>
<script src="${ctx}/js/lib/jquery.md5.js"></script>
<script src="${ctx}/js/lib/jquery-ui-1.10.0.custom.min.js" type="text/javascript"></script>
<script src="${ctx}/js/lib/bootstrap.min.js"></script>
<script src="${ctx}/js/lib/jquery.dynatree.min.js"></script>
<script src="${ctx}/bootstrap/js/bootstrap-contextmenu.js"></script>
<script src="${ctx}/js/lib/jquery.fixedheadertable.min.js"></script>
<script src="${ctx}/js/lib/d3.min.js"></script>
<script src="${ctx}/js/lib/jquery-linedtextarea.js"></script>
<script src="${ctx}/js/lib/jquery.json-2.4.js"></script>
<script type="text/javascript" src="${ctx}/plugins/layer/layer.js"></script>
<script src="${ctx}/js/lib/jquery.tablesorter.js" ></script>
<script src="${ctx}/js/lib/jquery.tablesorter.widgets.js"></script>



<style type="text/css">
.wrappersorter{
	position: relative;
	padding: 0 5px;
	overflow-y: auto;
}  
</style>
</head>
<body class="hold-transition skin-blue layout-top-nav">
	<jsp:include page="${ctx}/layout/header.jsp"></jsp:include>

	<div class="container-fluid" style="padding-top:60px;">
		<div class="row">
			<div class="col-xs-3">
				<div id="tableMenu">
				<ul class="dropdown-menu" style="width: 200px;">
					<li><a tabindex="-1" href="select">查询100条</a></li>
					<!-- <li><a href="#select_no_execute">查询100条</a></li> -->
					<!-- <li><a href="#select_where">SELECT ... WHERE LATESTPARTITION LIMIT 100</a></li> -->
					<li><a tabindex="-1" href="select_where">查询最新分区中100条</a></li>
					<!-- <li><a href="#select_count_where">SELECT COUNT(*) WHERE LATEST PARTITION LIMIT 100</a></li> -->
					<li><a tabindex="-1" href="select_count_where">最新分区COUNT</a></li>
					<li><a tabindex="-1" href="partitions">查看分区</a></li>
					<li><a tabindex="-1" href="describe">表属性</a></li>
				</ul>
				</div>
				<div id="schemaMenu">
				<ul class="dropdown-menu" style="width: 100px;">
					<li><a tabindex="-1" href="refresh">刷新</a></li>
				</ul>
				</div>
				<h4>数据仓库结构</h4>

				<div class="row" style="margin-bottom: 2px;">
					<div class="col-lg-12">
						<div class="input-group">
							<input type="text" class="form-control" id="table_name"
								placeholder="输入表名(多个表名用逗号分隔)" onkeydown='if(event.keyCode==13){table_search();}'> 
								<span class="input-group-btn">
								<button class="btn btn-default" onclick="table_search()"
									type="button">搜索</button>
							</span>
						</div>
						<!-- /input-group -->
					</div>
					<!-- /.col-lg-6 -->
				</div>
				<!-- /.row -->

				<div id="tree"></div>
				<div id="searchTree"></div>
			</div>
			<div class="col-xs-9">
				<script type="text/javascript">
					var currIframe = 'winId0';
					$(function() {
						var tabCounter = 1;
						var loadfinish = true;
						var tabs = $("#tabs").tabs({
							activate : function(event, ui) {
								currIframe = ui.newPanel[0].children[0].id;
							}
						});
						tabs.delegate("span.ui-icon-close", "click",
								function() {
									var panelId = $(this).closest("li")
											.remove().attr("aria-controls");
									$("#" + panelId).remove();
									tabs.tabs("refresh");
								});
						function addTab() {
							if (loadfinish) {
								var li = '<li ui-state-default ui-corner-top" role="tab" tabindex="-1" aria-controls="query-query-tab" aria-labelledby="ui-id-1" aria-selected="false" aria-expanded="false"><a href="#div_winId' + tabCounter + '" class="ui-tabs-anchor" role="presentation" tabindex="-1" id="ui-id-1">查询窗口'
										+ (tabCounter + 1)
										+ '</a><span class="ui-icon ui-icon-close" role="presentation">关闭</span></li>';
								$("#ll").before(li);
								var targetUrl="${ctx}/main/querypage.html?w=winId"+tabCounter;
								var tabContentHtml = '<iframe id="winId' + tabCounter + '" src="'+targetUrl+'" class="col-xs-12" style="padding: 1px; border-top: 0px;" width="500px"height="900px;"scrolling="no"></iframe>';
								tabs.append("<div id='div_winId" + tabCounter + "' style='padding: 0px;margin-top: -1px'>"+ tabContentHtml + "</div>");
								loadfinish = false;
							}
							$('#winId' + tabCounter).load(function() {
								tabs.tabs("refresh");
								tabCounter++;
								loadfinish = true;
							});

						}
						;

						$("#plus").click(function() {
							addTab();
						});

					});
				</script>

				<h4></h4>
				<div id="tabs" style="border: 0px;">
					<ul>
						<li class="temp"><a href="#div_winId0">查询窗口1</a></li>
						<li id="ll"><a id="plus"><span
								class="glyphicon glyphicon-plus" style="margin-top: 6px;"
								aria-hidden="true"></span></a></li>
					</ul>
					<div id='div_winId0' style="padding: 0px; margin-top: -1px;">
						<iframe id="winId0" src="${ctx}/main/querypage.html?w=winId0" class="col-xs-12"
							style="padding: 1px; border-top: 0px; border: 0;" width="100%"
							height="100%" scrolling="no"></iframe>
					</div>
				</div>
			</div>
		</div>
		<div class="wrappersorter">
				<table class="tablesorter table table-bordered" id="new-win-results"></table>
		</div>		
		<script type="text/javascript" src="${ctx}/js/lib/pdq.js" charset="utf-8"></script>
		<script>
			var tree = pdq_tree();
		</script>
	</div>
</body>
</html>