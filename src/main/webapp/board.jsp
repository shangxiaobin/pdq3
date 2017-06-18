<%@ page language="java" pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="<%=request.getContextPath()%>" />


<!DOCTYPE html>
<!--
This is a starter template page. Use this page to start your new project from
scratch. This page gets rid of all links and provides the needed markup only.
-->
<html ng-app="cBoard" ng-controller="cBoardCtrl">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>PDQ</title>
    <!-- Tell the browser to be responsive to screen width -->
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
    <!-- Bootstrap 3.3.6 -->
    <link rel="stylesheet" href="${ctx}/bootstrap/css/bootstrap.css">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="${ctx}/css/font-awesome.min.css">
    <!-- Ionicons -->
    <link rel="stylesheet" href="${ctx}/css/ionicons.min.css">
    <!-- Theme style -->
    <link rel="stylesheet" href="${ctx}/dist/css/AdminLTE.css">
    <!-- AdminLTE Skins. We have chosen the skin-blue for this starter
          page. However, you can choose any other skin. Make sure you
          apply the skin class to the body tag so the changes take effect.
    -->
    <link rel="stylesheet" href="${ctx}/dist/css/skins/skin-blue.min.css">

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
    <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->

    <!--<script src="http://cdn.bootcss.com/angular.js/1.5.8/angular.js"></script>-->
    <script src="${ctx}/org/cboard/Settings.js"></script>
    <script src="${ctx}/lib/angular.min.js"></script>
    <script src="${ctx}/lib/angular-ui-router.min.js"></script>
    <script src="${ctx}/lib/angular-md5.min.js"></script>
    <script src="${ctx}/lib/angular-drag-and-drop-lists.js"></script>
    <script src="${ctx}/lib/angular-sanitize.min.js"></script>
    <script src="${ctx}/lib/ui-bootstrap-tpls-2.1.3.min.js"></script>
    <script src="${ctx}/lib/angular-translate.js"></script>
    <script src="${ctx}/lib/angular-translate-loader-partial.js"></script>

    <script src="${ctx}/lib/underscore-min.js"></script>
    <script src="${ctx}/lib/numbro.min.js"></script>
    <script src="${ctx}/lib/ui-select.min.js"></script>

    <script src="${ctx}/plugins/jQuery/jquery-2.2.3.min.js"></script>
    <script src="${ctx}/plugins/jQueryUI/jquery-ui.min.js"></script>
    <!-- Bootstrap 3.3.6 -->
    <script src="${ctx}/bootstrap/js/bootstrap.min.js"></script>
    <!-- AdminLTE App -->
    <script src="${ctx}/dist/js/app.js"></script>

    <script src="${ctx}/plugins/echart/echarts.min.js"></script>
    <script src="${ctx}/plugins/echart/theme-fin1.js"></script>
    <script src="${ctx}/lib/jquery.ba-resize.js"></script>
    <!--<script src="${ctx}/plugins/echart/world.json"></script>-->
    <script src="${ctx}/plugins/echart/world.js"></script>
    <!--<script src="${ctx}/plugins/echart/china.json"></script>-->
    <script src="${ctx}/plugins/echart/china.js"></script>
	<script src="${ctx}/plugins/ace/ace.js"></script>
    <script src="${ctx}/plugins/ui-ace/ui-ace.min.js"></script>

    <script src="${ctx}/plugins/datatables/jquery.dataTables.js"></script>
    <script src="${ctx}/plugins/datatables/dataTables.bootstrap.min.js"></script>
    <link rel="stylesheet" href="${ctx}/plugins/datatables/dataTables.bootstrap.css">
    <!--<link rel="stylesheet" href="${ctx}/css/ui-select.min.css">-->
    <link rel="stylesheet" href="${ctx}/css/select.css">
    
    <script type="text/javascript" src="${ctx}/plugins/layer/layer.js"></script>
    <style>
        .modal-fit .modal-dialog {
            left: 0;
            top: 0;
            right: 0;
            bottom: 0;
            margin: auto;
            padding: 50px;
        }

        .modal-fit .modal-body {
            height: calc(100vh - 200px);
            overflow: auto;
        }

        @media (min-width: 768px) {
            .modal-fit .modal-dialog {
                width: auto;
            }
        }
    </style>
</head>
<!--
BODY TAG OPTIONS:
=================
Apply one or more of the following classes to get the
desired effect
|---------------------------------------------------------|
| SKINS         | skin-blue                               |
|               | skin-black                              |
|               | skin-purple                             |
|               | skin-yellow                             |
|               | skin-red                                |
|               | skin-green                              |
|---------------------------------------------------------|
|LAYOUT OPTIONS | fixed                                   |
|               | layout-boxed                            |
|               | layout-top-nav                          |
|               | sidebar-collapse                        |
|               | sidebar-mini                            |
|---------------------------------------------------------|
-->
<body class="hold-transition skin-blue layout-top-nav">
<div class="wrapper">

    <!-- Main Header -->
   <jsp:include page="${ctx}/layout/header.jsp"></jsp:include>
    <!-- Left side column. contains the logo and sidebar -->
    <!-- <aside class="main-sidebar" ng-include="'starter/main-sidebar.html'"></aside> -->

    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper" style="padding-top:60px;min-height: 100%">

        <!-- Main content -->
        <section ui-view class="content"></section>
        <!-- /.content -->
    </div>
    <!-- /.content-wrapper -->

    <!-- Main Footer -->
    <!-- <footer class="main-footer" ng-include="'starter/main-footer.html'"></footer> -->

</div>
<!-- ./wrapper -->



<script src="${ctx}/org/cboard/util/CBoardEChartRender.js"></script>
<script src="${ctx}/org/cboard/util/CBoardCommonUtils.js"></script>
<script src="${ctx}/org/cboard/util/CBoardKpiRender.js"></script>
<script src="${ctx}/org/cboard/util/CBoardTableRender.js"></script>

<!-- AngularJS-->
<script src="${ctx}/org/cboard/ng-app.js"></script>
<script src="${ctx}/org/cboard/ng-config.js"></script>
<script src="${ctx}/org/cboard/controller/cboard/cBoardCtrl.js"></script>
<script src="${ctx}/org/cboard/controller/dashboard/dashboardViewCtrl.js"></script>
<script src="${ctx}/org/cboard/controller/config/widgetCtrl.js"></script>
<script src="${ctx}/org/cboard/controller/config/datasourceCtrl.js"></script>
<script src="${ctx}/org/cboard/controller/config/boardCtrl.js"></script>
<script src="${ctx}/org/cboard/controller/config/categoryCtrl.js"></script>
<script src="${ctx}/org/cboard/controller/config/datasetCtrl.js"></script>
<script src="${ctx}/org/cboard/controller/admin/userAdminCtrl.js"></script>
<script src="${ctx}/org/cboard/controller/admin/resAdminCtrl.js"></script>

<script src="${ctx}/org/cboard/service/dashboard/dashboardService.js"></script>
<script src="${ctx}/org/cboard/service/data/dataService.js"></script>
<script src="${ctx}/org/cboard/service/util/ModalUtils.js"></script>
<script src="${ctx}/org/cboard/service/updater/updateService.js"></script>
<script src="${ctx}/org/cboard/service/chart/chartFunnelService.js"></script>
<script src="${ctx}/org/cboard/service/chart/chartKpiService.js"></script>
<script src="${ctx}/org/cboard/service/chart/chartLineService.js"></script>
<script src="${ctx}/org/cboard/service/chart/chartPieService.js"></script>
<script src="${ctx}/org/cboard/service/chart/chartSankeyService.js"></script>
<script src="${ctx}/org/cboard/service/chart/chartRadarService.js"></script>
<script src="${ctx}/org/cboard/service/chart/chartService.js"></script>
<script src="${ctx}/org/cboard/service/chart/chartTableService.js"></script>
<script src="${ctx}/org/cboard/directive/dashboard/dashboardWidget.js"></script>

<script src="${ctx}/plugins/crossTable/plugin.js"></script>

<script type="text/ng-template" id="echartContent">
    <div class="col-md-{{widget.width}}">
        <div class="box box-solid">
            <div class="box-header">
                <i class="fa fa-bar-chart-o"></i>
                <h3 class="box-title">{{widget.name}}</h3>
                <div class="box-tools pull-right">
                    <button type="button" class="btn btn-box-tool" ng-click="reload(widget)"><i
                            class="fa fa-refresh"></i>
                    </button>
                    <button type="button" class="btn btn-box-tool" ng-click="config(widget)"><i
                            class="fa fa-wrench"></i>
                    </button>
                    <button type="button" class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i>
                    </button>
                    <button type="button" class="btn btn-box-tool" ng-click="modalChart(widget)"><i
                            class="fa fa-square-o"></i>
                    </button>
                </div>
            </div>
            <div class="box-body" ng-style="{height:myheight+'px'}">
            </div>
        </div>
    </div>
</script>
<script type="text/ng-template" id="kpiContent">
    <div class="col-md-{{widget.width}} kpi-body">
    </div>
</script>
<script type="text/ng-template" id="tableContent">
    <div class="col-md-{{widget.width}}">
        <div class="box box-solid">
            <div class="box-header">
                <i class="fa fa-bar-chart-o"></i>
                <h3 class="box-title">{{widget.name}}</h3>
                <div class="box-tools pull-right">
                    <button type="button" class="btn btn-box-tool" ng-click="reload(widget)"><i
                            class="fa fa-refresh"></i>
                    </button>
                    <button type="button" class="btn btn-box-tool" ng-click="config(widget)"><i
                            class="fa fa-wrench"></i>
                    </button>
                    <button type="button" class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i>
                    </button>
                    <button type="button" class="btn btn-box-tool" ng-click="modalTable(widget)"><i
                            class="fa fa-square-o"></i>
                    </button>
                </div>
            </div>
            <div class="box-body" ng-style="{height:myheight+'px'}">
            </div>
        </div>
    </div>
</script>
</body>
</html>
