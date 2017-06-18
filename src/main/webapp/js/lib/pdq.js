layer.config({
	extend : '../plugins/../../layer/extend/layer.ext.js'
});
var tables={};
var pdq_tree = (function() {
	var tree = $("#tree")
			.dynatree(
					{
						imagePath : "img",
						initAjax : {
							type : "POST",
							url : "../presto/query",
							data : {
								"query" : "show catalogs"
							}
						},
						postProcess : function(data, dataType) {
							headers = data["headers"];
							results = data["results"];
							if (headers == "Catalog") {
								for (var i = 0; i < results.length; i++) {
									var catalog = results[i][0];
									var rootNode = $("#tree").dynatree(
											"getRoot");
									rootNode.addChild({
										title : catalog,
										key : catalog,
										isFolder : true,
										isLazy : true,
										catalog : catalog
									});
								}
							}
						},
						onLazyRead : function(node) {
							var param;
							if (node.data.catalog) {
								param = "show schemas from " + node.data.key;
							} else if (node.parent.data.catalog) {
								param = "show tables from "
										+ node.parent.data.catalog + "."
										+ node.data.key;
//								var q = "select * from "+node.parent.data.catalog + "."
//								+"information_schema.columns where table_schema='"
//								+node.data.key+"'";
//								$.post("../presto/query", {"query":q}, function(data){
//									showTabs.tables = data.results;
//								}, "json");
							} else if (node.parent.data.schema) {
								param = "show columns from "
										+ node.parent.parent.data.catalog + "."
										+ node.parent.data.schema + "."
										+ node.data.key;
							}
							$.ajax({
								url : "../presto/query",
								data : {
									query : param
								},
								type : "POST",
								dataType : "json"
							}).done(function(data) {
								if (data["error"]) {
									console.log(data["error"]);
									return;
								}
								headers = data["headers"];
								results = data["results"];
								if (headers == "Schema") {
									for (var i = 0; i < results.length; i++) {
										var result = results[i][0];
										node.addChild({
											title : result,
											key : result,
											isLazy : true,
											isFolder : true,
											schema : result
										});
									}
								} else if (headers == "Table") {
									for (var i = 0; i < results.length; i++) {
										var result = results[i][0];
										node.addChild({
											title : result,
											key : result,
											isLazy : true,
											isFolder : true,
											table : result
										});
									}
								} else {
									for (var i = 0; i < results.length; i++) {
										var result = results[i][0];
										node.addChild({
											title : result,
											key : result,
											isLazy : true,
											isFolder : false
										});
									}
									//$("#show-columns").empty();
									// create_table_column("#show-columns",
									// results); 停用列显示，改为右键=》表属性方式显示
								}
								node.setLazyNodeStatus(DTNodeStatus_Ok);
							}).fail(function() {
								node.data.isLazy = false;
								node.setLazyNodeStatus(DTNodeStatus_Ok);
								node.render();
							});
						},
						onExpand:function(flag, dtnode){
//							 if (flag == true &&  dtnode.parent.data.catalog) {
//									var q = "select * from "+dtnode.parent.data.catalog + "."
//									+"information_schema.columns where table_schema='"
//									+dtnode.data.key+"'";
//									$.post("../presto/query", {"query":q}, function(data){
//										showTabs.tables = data.results;
//									}, "json");
//								}
						},
						onDblClick:function(node, event){
//							var entType=node.getEventTargetType(event);
//							console.log(appendContent +" @@@@@ "+entType);
							$("#" + currIframe)[0].contentWindow.editor.replaceSelection(node.data.key+","); 
							/* if (flag == true &&  dtnode.parent.data.catalog) {
									var q = "select * from "+dtnode.parent.data.catalog + "."
									+"information_schema.columns where table_schema='"
									+dtnode.data.key+"'";
									$.post("../presto/query", {"query":q}, function(data){
										showTabs.tables = data.results;
									}, "json");
								}*/
						},
						onCreate : function(node, span) {
							if(node.data.schema){
								var catalog = node.parent.data.catalog;
								//alert(catalog+"."+node.data.schema);
								$(span).contextmenu({
										target:'#schemaMenu',
										onItem:function(context, e) {
											schema = node.data.schema;
											catalog = node.parent.data.catalog;		
											var action=$(e.target).attr("href");
										     if (action === "refresh") {
										    	var  param = "show tables from "+ catalog + "."+ schema;
													$.ajax({
														url : "../presto/query",
														data : {
															query : param
														},
														type : "POST",
														dataType : "json"
													}).done(function(data) {
														if (data["error"]) {
															console.log(data["error"]);
															return;
														}
														headers = data["headers"];
														results = data["results"];
														if (headers == "Table") {
															node.removeChildren();
															for (var i = 0; i < results.length; i++) {
																var result = results[i][0];
																node.addChild({
																	title : result,
																	key : result,
																	isLazy : true,
																	isFolder : true,
																	table : result
																});
															}
														}
														node.setLazyNodeStatus(DTNodeStatus_Ok);
													}).fail(function() {
														node.data.isLazy = false;
														node.setLazyNodeStatus(DTNodeStatus_Ok);
														node.render();
													});
											}
										}
										});
							}
							if (node.data.table) {
								$(span).contextmenu({
										target : '#tableMenu',
										 onItem : function(context, e) {
											var q = $("#" + currIframe).contents().find("#query");
											var qs = $("#" + currIframe).contents().find("#query-submit");
											table = node.data.table;
											schema = node.parent.data.schema;
											catalog = node.parent.parent.data.catalog;
											var action=$(e.target).attr("href");
											if (action === "select") {
												query = "SELECT * FROM "
														+ catalog
														+ "."
														+ schema
														+ "."
														+ table
														+ " LIMIT 100";
												set_textarea_value(
														currIframe,
														query);
												qs.click();
											} else if (action === "select_no_execute") {
												query = "SELECT * FROM "
														+ catalog
														+ "."
														+ schema
														+ "."
														+ table
														+ " LIMIT 100";
												set_textarea_value(
														currIframe,
														query);
											} else if (action === "select_where") {
												select_data(
														"SELECT * FROM",
														catalog,
														schema, table,
														true);
											} else if (action === "select_where_no_execute") {
												select_data(
														"SELECT * FROM",
														catalog,
														schema, table,
														false);
											} else if (action === "select_count_where") {
												select_data(
														"SELECT COUNT(*) FROM",
														catalog,
														schema, table,
														true);
											} else if (action === "select_count") {
												select_data(
														"SELECT COUNT(*) FROM",
														catalog,
														schema, table,
														true);
											} else if (action === "partitions") {
												query = "SHOW PARTITIONS FROM "
														+ catalog
														+ "."
														+ schema
														+ "."
														+ table;
												set_textarea_value(
														currIframe,
														query);
												qs.click();
											} else if (action === "describe") {
												query = "DESCRIBE "
														+ catalog + "."
														+ schema + "."
														+ table;
												set_textarea_value(
														currIframe,
														query);
												qs.click();
											}
										 }
										});
							}
						}
					});
	return tree;
});

var select_data = (function(select_query, catalog, schema, table, execute_flag) {
	partition_query = "SHOW PARTITIONS FROM " + catalog + "." + schema + "."
			+ table;
	var requestURL = "../presto/query";
	var requestData = {
		"query" : partition_query
	};
	var successHandler = function(data) {
		var q = $("#" + currIframe).contents().find("#query");
		var qs = $("#" + currIframe).contents().find("#query-submit");
		if (data.error||data.results=='') {
			query = select_query + " " + catalog + "." + schema + "."+ table +" LIMIT 100";
			set_textarea_value(currIframe, query);
			qs.click();
			return;
		} else {
			var partition_column = data.headers;
			if (partition_column.length == 0) {
				query = select_query + " " + catalog + "." + schema + "."
						+ table + " LIMIT 100";
				set_textarea_value(currIframe, query);

				qs.click();
				return;
			}
			var rows = data.results;
			var latest_partition = rows[rows.length - 1];
			var where = " WHERE ";
			for (var i = 0; i < partition_column.length; ++i) {
				if (typeof latest_partition[i] === "string") {
					where += partition_column[i] + "=" + "'"
							+ latest_partition[i] + "'";
				} else {
					where += partition_column[i] + "=" + latest_partition[i];
				}
				if (i != partition_column.length - 1) {
					where += " AND "
				}
			}
			query = select_query + " " + catalog + "." + schema + "." + table
					+ where + " LIMIT 100";
			set_textarea_value(currIframe, query);
			if (execute_flag) {
				qs.click();
			}
		}
	};
	$.post(requestURL, requestData, successHandler, "json");
});

var selectLine = (function(n) {
	if (n < 1)
		return false;
	$(".codelines .lineno.lineselect").removeClass("lineselect");
	$(".codelines .lineno").eq(n - 1).addClass("lineselect");
});
var close_btn="<button type='button' class='close' data-dismiss='alert' aria-label='Close'><span aria-hidden='true'>&times;</span></button>";

var handle_execute = (function(w) {
	$("#query-submit").attr("disabled", "disabled");
	$("#query-explain").attr("disabled", "disabled");
	$("#query-explain-distributed").attr("disabled", "disabled");
	$("#query-add_bookmark").attr("disabled", "disabled");
	$("#query-clear").attr("disabled", "disabled");
	$("#query-format").attr("disabled", "disabled");
	$("#tsv-download").attr("disabled", "disabled");
	$("#fullScreen").hide();
	$("#query-results").empty();
	var query = window.editor.getValue();
	var requestURL = "../presto/query";
	var requestData = {
		"query" : query
	};

	var interval_id = 0;	
	var successHandler = function(data) {
		$("#query-submit").removeAttr("disabled");
		$("#query-explain").removeAttr("disabled");
		$("#query-explain-distributed").removeAttr("disabled");
		$("#query-add_bookmark").removeAttr("disabled");
		$("#query-clear").removeAttr("disabled");
		$("#query-format").removeAttr("disabled");
		if(data.success){
			$("#success-msg").html(close_btn+" "+data.success);
			$("#success-msg").slideDown("fast");
			$("#query-results").empty();
		}else if (data.error) {
			$("#error-msg").html(close_btn+" "+data.error);
			$("#error-msg").slideDown("fast");
			$("#query-results").empty();
			selectLine(data.errorLineNumber);
		} else {
			if (data.warn) {
				$("#warn-msg").html(close_btn+" "+data.warn);
				$("#warn-msg").slideDown("fast");
			}
			$(".codelines .lineno.lineselect").removeClass("lineselect");
			push_query(query);
			$("#query-histories").empty();
			update_query_histories_area();
			$("#query-results").empty();
			var headers = data.headers;
			var rows = data.results;
			
			$("#tsv-download").removeAttr("disabled");
			
			$('.tablesorter-sticky-wrapper').remove();
			$('#query-results').remove();
			$('.wrappersorter').append("<table class='tablesorter table table-bordered' id='query-results'></table>")
			
			push_result(headers, rows);
			create_table("#query-results", headers, rows);	
			$('.wrappersorter').css("height"," 100%");
			$('.wrappersorter').css("width"," 100%");
			$('#query-results').tablesorter({
			    widthFixed : true,
			    headerTemplate : '{content} {icon}', // Add icon for various themes

				widgets: ['zebra','stickyHeaders' ],//filter,'zebra',

		    	widgetOptions: {
			    	// jQuery selector or object to attach sticky header to
				    stickyHeaders_attachTo : $('.wrappersorter')
			    }
		    }); 
			
			autoHeight();
			$("#fullScreen").show();
			
		}
		
		setTimeout(function() {
			clearInterval(interval_id);
			$("#error-msg").hide();
			$("#success-msg").hide();
			$("#warn-msg").hide();
			$("#tmp-"+w).val("");
		}, 2000);
		
		$("#running-monitor"+w).empty();
	};

	$.post(requestURL, requestData, successHandler, "json");
	setTimeout(function() {
		interval_id = setInterval(function() {
			runningQueries(w);
		}, 2000);


	}, 100);

	$("#running-monitor"+w).html(
			"<table id='running"+w+"' class='table table-striped'>" + "<thead>"
					+ "<tr>" + "<th width='60px'>操作</th>"
					+ "<th width='60px'>进度</th>"
					+ "<th width='60px'>耗时(单位s)</th>"
					+ "<th width='60px'>状态</th>"
					+ "<th width='60px'>排队中</th>"
					+ "<th width='60px'>运行中</th>"
					+ "<th width='60px'>已完成</th>" + "</tr>" + "</thead>"
					+ "<tbody>" + "<tr class='info'>" + "<td>--</td>"
					+ "<td>--</td>" + "<td>--</td>" + "<td>--</td>"
					+ "<td>--</td>" + "<td>--</td>" + "<td>--</td>"
					+ "</tr>" + "</tbody>" + "</table>");
	
	

});

var runningQueries = (function(w) {
	var queryId=$("#tmp-"+w).val();	
	d3.json('../presto/state?state=running&winId='+w+"&queryId="+queryId, function(queries) {
		var flag=false;
		var j=0;
		$.each(queries, function(i, query) {
			if(w==query.winId){
				$("#tmp-"+w).val(query.queryId);
				var tbody = d3.select("#running"+w).select("tbody");
				tbody.html("");		
				var rows = tbody.selectAll("tr").data(queries, function(query) {
					return query.queryId;
				})
				rows.exit().remove();				
				var col1=rows.enter().append("tr").attr("class", "info")
				col1.append('button').text('终止').attr('type', 'button')
				.on('click', function(query) {
					d3.xhr("../presto/kill?queryId=" + query.queryId).send('GET');
					$(this).attr('disabled', 'disabled');
					$("#running-monitor"+w).empty();
				});
				col1.append('button').attr("style","margin-left:10px;").text("监控").attr('type', 'button')
				.on('click', function(query) {
					var qMonitorUrl=query["self"].replace('/v1/query/','/query.html?');
					full_Monitor_screen(qMonitorUrl);
				});
				rows=col1;
				var cells = rows.selectAll("td").data(function(queryInfo) {
							var splits = queryInfo.totalDrivers;
							var completedSplits = queryInfo.completedDrivers;
							var runningSplits = queryInfo.runningDrivers;
							var queuedSplits = queryInfo.queuedDrivers;
							var progress = "N/A";
							if (queryInfo.scheduled) {
								progress = d3.format("%")(
										splits == 0 ? 0 : completedSplits / splits);
							}
							return [ progress, Math.round((queryInfo.elapsedTimeMillis/1000)*100)/100, queryInfo.state,
									queuedSplits, runningSplits, completedSplits ]
						});
		
				cells.text(function(d) {
					return d;
				});
		
				cells.enter().append("td").text(function(d) {
					return d;
				});
		
				tbody.selectAll("tr").sort(function(a, b) {
					return d3.descending(a.createTime, b.createTime);
				});
				 
					return false; //break;
				}			
		});
		
	});
});

var maxWinIdx;
function full_screen(){
	$('.wrappersorter',window.parent.document).css("height"," 100%");
	 maxWinIdx=parent.layer.open({
		  title:'查询结果',
		  type: 1,
		  content: $('.wrappersorter',window.parent.document),
		  cancel:function(){
			  $('.wrappersorter',window.parent.document).css("height"," 0px");
			  $('#new-win-results',window.parent.document).remove();
			  $('.tablesorter-sticky-wrapper',window.parent.document).remove();
			  $('.wrappersorter',window.parent.document).append("<table class='tablesorter table table-bordered' id='new-win-results'></table>")
		  }
		});
	parent.layer.full(maxWinIdx);
	$('#new-win-results',window.parent.document).append($("#query-results").html());
	$('.tablesorter',window.parent.document).tablesorter({
	    widthFixed : true,
	    headerTemplate : '{content} {icon}', // Add icon for various themes

		widgets: ['zebra','stickyHeaders','filter' ],//filter,'zebra',

    	widgetOptions: {
	    	// jQuery selector or object to attach sticky header to
		    stickyHeaders_attachTo : $('.wrappersorter',window.parent.document)
	    }
    }); 
}

var handle_explain = (function() {
	explain(false);
});

var handle_explain_distributed = (function() {
	explain(true);
});

var explain = (function(distributed) {
	//$("#query-results").fixedHeaderTable("destroy");
	$("#query-results").empty();
	var query = "";

	if (distributed) {
		query = "explain (type distributed) " + window.editor.getValue();
	} else {
		query = "explain " + window.editor.getValue();
	}
	var requestURL = "../presto/query";
	var requestData = {
		"query" : query
	};
	var successHandler = function(data) {
		if (data.error) {
			$("#error-msg").text(data.error);
			$("#error-msg").slideDown("fast");
			$("#query-results").empty();
			selectLine(data.errorLineNumber);
		} else {
			if (data.warn) {
				$("#warn-msg").text(data.warn);
				$("#warn-msg").slideDown("fast");
			}
			$(".codelines .lineno.lineselect").removeClass("lineselect");
			$("#query-results").empty();
			var headers = data.headers;
			var rows = data.results;
			var thead = document.createElement("thead");
			var tr = document.createElement("tr");
			for (var i = 0; i < headers.length; ++i) {
				var th = document.createElement("th");
				$(th).text(headers[i]);
				$(tr).append(th);
			}
			$(thead).append(tr);
			$("#query-results").append(thead);
			var tbody = document.createElement("tbody");
			for (var i = 0; i < rows.length; ++i) {
				var tr = document.createElement("tr");
				var columns = rows[i];
				for (var j = 0; j < columns.length; ++j) {
					var pre = document.createElement("pre");
					$(pre).text(columns[j]);
					var td = document.createElement("td");
					$(td).append(pre);
					$(tr).append(td);
				}
				$(tbody).append(tr);
			}
			$("#query-results").append(tbody);
			// $("#query-results").fixedHeaderTable("destroy");
			// $("#query-results").fixedHeaderTable();
			$("#tsv-download").removeAttr("disabled");
			push_result(headers, rows);
		}
	};
	$.post(requestURL, requestData, successHandler, "json");
	
});
/*var query_add_bookmark = (function(event) {
	var q="";
	q = window.editor.getValue();
	if(q=="" || q ==null || typeof(q)=="undefined"){
		parent.layer.msg("请输入要收藏的SQL");
		return;
	}
	parent.layer.prompt({
		title : '备注说明',
		formType : 2
	}, function(remark) {
		var requestURL = "../sql/sql";
		var requestData = {
			"type" : "addSql",
			"sql" : q,
			"remark" : remark
		};
		$.post(requestURL, requestData, function(data) {
			// data type 0:成功，1:失败，2:已存在
			var msg = data.msg;
			if (msg == '0') {
				msg = "成功！"
			} else if (msg == '1') {
				msg = "失败！"
			} else if (msg == '2') {
				msg = "重复收藏！"
			}
			parent.layer.msg("收藏状态 [" + msg + "]");
		}, "json");
		$("#query-bookmarks").empty();
	});
});*/

var query_add_bookmark = (function(event) {
	var q="";
	q = window.editor.getValue();
	q=q.replace(/(^\s*)|(\s*$)/g, "");
	if(q=="" || q ==null || typeof(q)=="undefined"){
		parent.layer.msg("请输入要收藏的SQL");
		return;
	}
	//例子2
	parent.layer.prompt({
		title : '备注说明',
		formType : 2
	}, function(remark, index, elem){
		var requestURL = "../sql/sql";
		var requestData = {
			"type" : "addSql",
			"sql" : q,
			"remark" : remark
		};
		$.post(requestURL, requestData, function(data) {
			// data type 0:成功，1:失败，2:已存在
			var msg = data.msg;
			if (msg == '0') {
				msg = "成功！"
			} else if (msg == '1') {
				msg = "失败！"
			} else if (msg == '2') {
				msg = "重复收藏！"
			}
			parent.layer.msg("收藏状态 [" + msg + "]");
		}, "json");
		$("#query-bookmarks").empty();
		parent.layer.close(index);
	});
	
	
});




var query_clear = (function() {
	window.editor.setValue("");
});

var set_textarea_value = (function(currIframe, query) {
	var ta = document.getElementById(currIframe).contentWindow;
	ta.window.editor.setValue(query);
});

var query_format = (function() {
	window.editor.removeLineClass(window.editor.listSelections()[0].head.line,
			'wrap', 'CodeMirror-errorline-background');
	var query = window.editor.getValue();
	if (query == "")
		return;
	var requestURL = "../sql/format";
	var requestData = {
		"query" : query
	};
	var successHandler = function(data) {
		if (data.error) {
			$("#error-msg").text(data.error);
			$("#error-msg").slideDown("fast");
			selectLine(data.errorLineNumber);
		} else {
			var format_query = data.formattedQuery;
			window.editor.setValue(format_query);
			autoHeight();

		}
	};
	$.post(requestURL, requestData, successHandler, "json");
});

var push_result = (function(headers, rows) {
	if (!window.sessionStorage)
		return;
	window.sessionStorage.query_header = JSON.stringify(headers);
	window.sessionStorage.query_result = JSON.stringify(rows);
});

var tsv_download = (function() {
	var query_header_string = window.sessionStorage.query_header;
	var query_result_string = window.sessionStorage.query_result;
	var headers = JSON.parse(query_header_string);
	var rows = JSON.parse(query_result_string);
	var text = headers.join("\t");
	text += "\n";
	for (var i = 0; i < rows.length; ++i) {
		var columns = rows[i];
		for (var j = 0; j < columns.length; ++j) {
			if (typeof columns[j] == "object") {
				text += JSON.stringify(columns[j]);
			} else {
				text += columns[j];
			}
			if (j != columns.length - 1) {
				text += "\t";
			}
		}
		text += "\n";
	}
	var name = "result"
	var blob = new Blob([ text ], {
		type : 'text/plain'
	})
	var link = document.createElement('a')
	link.href = URL.createObjectURL(blob)
	link.download = name + '.tsv'
	link.click()
});

var csv_download = (function() {
	var query_header_string = window.sessionStorage.query_header;
	var query_result_string = window.sessionStorage.query_result;
	var headers = JSON.parse(query_header_string);
	var rows = JSON.parse(query_result_string);
	var text = headers.join(",");
	text += "\n";
	for (var i = 0; i < rows.length; ++i) {
		var columns = rows[i];
		for (var j = 0; j < columns.length; ++j) {
			if (typeof columns[j] == "object") {
				text += JSON.stringify(columns[j]);
			} else {
				text += columns[j];
			}
			if (j != columns.length - 1) {
				text += ",";
			}
		}
		text += "\n";
	}
	var name = "result"
	var blob = new Blob([ text ], {
		type : 'text/plain'
	})
	var link = document.createElement('a')
	link.href = URL.createObjectURL(blob)
	link.download = name + '.csv'
	link.click()
});
var push_query = (function(query) {
	if (!window.localStorage)
		return;
	var list = query_histories();
	list.unshift(query);
	set_query_histories(list.slice(0, 100));
});

var query_histories = (function() {
	if (!window.localStorage)
		return [];
	var list = [];
	try {
		var listString = window.localStorage.query_histories;
		if (listString && listString.length > 0)
			list = JSON.parse(listString);
	} catch (e) {
		set_query_histories([]);
		list = [];
	}
	return list;
});

var set_query_histories = (function(list) {
	if (!window.localStorage)
		return;
	window.localStorage.query_histories = JSON.stringify(list);
});

var delete_query_histories = (function() {
	$("#query-histories").empty();
	if (!window.localStorage)
		return;
	window.localStorage.removeItem("query_histories");
});

var update_query_histories_area = (function() {
	var thead = document.createElement("thead");
	var tr = document.createElement("tr");
	var td = document.createElement("td");
	$(td).html("<b>SQL</b>");
	$(tr).append(td);
	var td = document.createElement("td");
	$(td).html(" ");
	$(tr).append(td);
	var td = document.createElement("td");
	$(td).html(" ");
	$(tr).append(td);
	var td = document.createElement("td");
	$(td).html(" ");
	$(tr).append(td);
	$(thead).append(tr);
	$("#query-histories").append(thead);
	
	var tbody = document.createElement("tbody");
	var query_list = query_histories();
	for (var i = 0; i < query_list.length; i++) {
		var tr = document.createElement("tr");
		var td = document.createElement("td");
		$(td).text(query_list[i]);
		$(tr).append(td);
		var copy_button = document.createElement("button");
		$(copy_button).attr("type", "button");
		$(copy_button).attr("class", "btn btn-success");
		$(copy_button).text("再查一次");
		$(copy_button).click({
			query : query_list[i]
		}, copy_query);
		var td = document.createElement("td");
		$(td).append(copy_button);
		$(tr).append(td);
		var delete_button = document.createElement("button");
		$(delete_button).attr("type", "button");
		$(delete_button).attr("class", "btn btn-info");
		$(delete_button).text("删除");
		$(delete_button).click({
			index : i
		}, delete_query);
		var td = document.createElement("td");
		$(td).append(delete_button);
		$(tr).append(td);
		var bookmark_button = document.createElement("button");
		$(bookmark_button).attr("type", "button");
		$(bookmark_button).attr("class", "btn btn-warning");
		$(bookmark_button).text("收藏SQL");
		$(bookmark_button).click({
			query : query_list[i]
		}, add_bookmark);
		var td = document.createElement("td");
		$(td).append(bookmark_button);
		$(tr).append(td);
		$(tbody).append(tr);
	}
	$("#query-histories").append(tbody);
});

var update_query_bookmarks_area = (function() {
 	var thead = document.createElement("thead");
	var tr = document.createElement("tr");
	var td = document.createElement("td");
	$(td).html("<b>已收藏的SQL</b>");
	$(tr).append(td);
	var td = document.createElement("td");
	$(td).html("<b>备注</b>");
	$(tr).append(td);
	var td = document.createElement("td");
	$(td).html("<b>收藏时间</b>");
	$(tr).append(td);
	var td = document.createElement("td");
	$(td).html(" ");
	$(tr).append(td);
	var td = document.createElement("td");
	$(td).html(" ");
	$(tr).append(td);
	$(thead).append(tr);
	$("#query-bookmarks").append(thead); 
	
	var tbody = document.createElement("tbody");
	var query_list = query_bookmarks();
	for (var i = 0; i < query_list.length; i++) {
		var tr = document.createElement("tr");
		var td = document.createElement("td");
		$(td).text(query_list[i].sql);
		$(tr).append(td);
		var td = document.createElement("td");
		$(td).text(query_list[i].desc);
		$(tr).append(td);
		var td = document.createElement("td");
		$(td).text(query_list[i].strCreateTime);
		$(tr).append(td);
		var copy_button = document.createElement("button");
		$(copy_button).attr("type", "button");
		$(copy_button).attr("class", "btn btn-success");
		$(copy_button).text("再查一次");
		$(copy_button).click({
			query : query_list[i].sql
		}, copy_query);
		var td = document.createElement("td");
		$(td).append(copy_button);
		$(tr).append(td);
		var delete_button = document.createElement("button");
		$(delete_button).attr("type", "button");
		$(delete_button).attr("class", "btn btn-info");
		$(delete_button).text("删除");
		$(delete_button).click({
			index : i,
			query : query_list[i].sql
		}, delete_bookmark);
		var td = document.createElement("td");
		$(td).append(delete_button);
		$(tr).append(td);
		$(tbody).append(tr);
	}
	$("#query-bookmarks").append(tbody);
});


//例子2


var add_bookmark = (function(event) {
	var sql=event.data.query;
	sql=sql.replace(/(^\s*)|(\s*$)/g, "");
	layer.prompt({
		title : '备注说明',
		formType : 2
		}, function(remark, index, elem){
		  var requestURL = "../sql/sql";
			var requestData = {
				"type" : "addSql",
				"sql" :sql,
				"remark" : remark
			};
			$.post(requestURL, requestData, addSqlSuccessHandler, "json");
			layer.close(index);
			$("#query-bookmarks").empty();
		  layer.close(index);
		});
	
});

var addSqlSuccessHandler = (function(data) {
	// data type 0:成功，1:失败，2:已存在
	var msg = data.msg;
	
	if (msg == '0') {	
		msg = "成功！"
	} else if (msg == '1') {
		msg = "失败！"
	} else if (msg == '2') {
		msg = "重复收藏！"
	}
	layer.msg("收藏状态 [" + msg + "]");
});
var query_bookmarks = (function() {

	var list = [];
	try {
		var requestURL = "sql";
		var requestData = {
			"type" : "getSql"
		};
		$.ajaxSetup({
			async : false
		});
		$.get(requestURL, requestData, function(data) {
			list = data;
		}, "json");
		$.ajaxSetup({
			async : true
		});
	} catch (e) {
		set_query_bookmarks([]);
		list = [];
	}
	return list;
});

var set_query_bookmarks = (function(list) {
	if (!window.localStorage)
		return;
	window.localStorage.query_bookmarks = JSON.stringify(list);
});

var delete_bookmark = (function(event) {
	var index = layer.confirm('确认删除?', function(index){
		var requestURL = "sql";
		var requestData = {
			"type" : "deleteSql",
			"sql" : event.data.query
		};
		$.ajaxSetup({
			async : false
		});
		$.post(requestURL, requestData, function(data) {
			// data type 0:成功，1:失败，2:已存在
			var msg = data.msg;
			if (msg == '0') {
				msg = "成功！"
			} else if (msg == '1') {
				msg = "失败！"
			} else if (msg == '2') {
				msg = "重复收藏！"
			}
			layer.msg("收藏状态 [" + msg + "]");

		}, "json");
		$.ajaxSetup({
			async : true
		});
		$("#query-bookmarks").empty();
		update_query_bookmarks_area();
	    
	    layer.close(index);
	});   
	
});

var delete_query_bookmarks = (function() {
	$("#query-bookmarks").empty();
	if (!window.localStorage)
		return;
	window.localStorage.removeItem("query_bookmarks");
});
// -----------------user manager-------------------
var update_query_userManager_area = (function() {
	var thead = document.createElement("thead");
	var tr = document.createElement("tr");
	var td = document.createElement("td");
	$(td).html("<b>用户名</b>");
	$(tr).append(td);
	var td = document.createElement("td");
	$(td).html("<b>操作</b>");
	$(tr).append(td);
	
	$(thead).append(tr);
	$("#query-userManager").append(thead);
	
	var tbody = document.createElement("tbody");
	var query_list = query_user();
	for (var i = 0; i < query_list.length; i++) {
		var tr = document.createElement("tr");
		// var copy_button = document.createElement("button");
		// $(copy_button).attr("type", "button");
		// $(copy_button).attr("class", "btn btn-success");
		// $(copy_button).text("再查一次");
		// $(copy_button).click({
		// query : query_list[i].sql
		// }, copy_query);
		// var td = document.createElement("td");
		// $(td).append(copy_button);
		// $(tr).append(td);
		var td = document.createElement("td");
		$(td).text(query_list[i].username);
		$(tr).append(td);
		
		var delete_button = document.createElement("button");
		$(delete_button).attr("type", "button");
		$(delete_button).attr("class", "btn btn-info");
		$(delete_button).text("删除");
		$(delete_button).click({
			index : i,
			username : query_list[i].username
		}, delete_user);
		var td = document.createElement("td");
		$(td).append(delete_button);
		$(tr).append(td);
		$(tbody).append(tr);
	}
	$("#query-userManager").append(tbody);
});
var query_user = (function() {
	var list = [];
	try {
		var requestURL = "user";
		var requestData = {
			"type" : "getUser"
		};
		$.ajaxSetup({
			async : false
		});
		$.post(requestURL, requestData, function(data) {
			list = data;
		}, "json");
		$.ajaxSetup({
			async : true
		});
	} catch (e) {
		set_query_user([]);
		list = [];
	}
	return list;
});
var delete_user = (function(event) {
	var index = layer.confirm('确认删除?', function(index){
		var requestURL = "user";
		var requestData = {
			"type" : "deleteUser",
			"username" : event.data.username
		};
		$.ajaxSetup({
			async : false
		});
		$.post(requestURL, requestData, function(data) {
			layer.msg(data.msg);
		}, "json");
		$.ajaxSetup({
			async : true
		});
		$("#query-userManager").empty();
		update_query_userManager_area();
	    
	    layer.close(index);
	});   
	
	

});
// -----------------end user manager-------------------
// -----------------userCatalog manager-------------------
var update_query_userCatalog_area = (function() {
	var thead = document.createElement("thead");
	var tr = document.createElement("tr");
	var td = document.createElement("td");
	$(td).html("<b>用户名</b>");
	$(tr).append(td);
	var td = document.createElement("td");
	$(td).html("<b>Catalog</b>");
	$(tr).append(td);
	var td = document.createElement("td");
	$(td).html("<b>Schema</b>");
	$(tr).append(td);
	var td = document.createElement("td");
	$(td).html("<b>操作</b>");
	$(tr).append(td);
	
	$(thead).append(tr);
	$("#query-userCatalog").append(thead);
	
	
	var tbody = document.createElement("tbody");
	var query_list = query_userCatalog();
	for (var i = 0; i < query_list.length; i++) {
		var tr = document.createElement("tr");
		// var copy_button = document.createElement("button");
		// $(copy_button).attr("type", "button");
		// $(copy_button).attr("class", "btn btn-success");
		// $(copy_button).text("再查一次");
		// $(copy_button).click({
		// query : query_list[i].sql
		// }, copy_query);
		// var td = document.createElement("td");
		// $(td).append(copy_button);
		// $(tr).append(td);
		var td = document.createElement("td");
		$(td).text(query_list[i].username);
		$(tr).append(td);
		var td = document.createElement("td");
		$(td).text(query_list[i].catalog);
		$(tr).append(td);
		var td = document.createElement("td");
		$(td).text(query_list[i].schema);
		$(tr).append(td);
		var delete_button = document.createElement("button");
		$(delete_button).attr("type", "button");
		$(delete_button).attr("class", "btn btn-info");
		$(delete_button).text("删除");
		$(delete_button).click({
			index : i,
			username : query_list[i].username,
			catalog : query_list[i].catalog,
			schema : query_list[i].schema
		}, delete_userCatalog);
		var td = document.createElement("td");
		$(td).append(delete_button);
		$(tr).append(td);
		$(tbody).append(tr);
	}
	$("#query-userCatalog").append(tbody);
});
var query_userCatalog = (function() {
	var list = [];
	try {
		var requestURL = "userCatalog";
		var requestData = {
			"type" : "getUserCatalog"
		};
		$.ajaxSetup({
			async : false
		});
		$.post(requestURL, requestData, function(data) {
			list = data;
		}, "json");
		$.ajaxSetup({
			async : true
		});
	} catch (e) {
		set_query_user([]);
		list = [];
	}
	return list;
});
var delete_userCatalog = (function(event) {
	var index = layer.confirm('确认删除?', function(index){
		var requestURL = "userCatalog";
		var requestData = {
			"type" : "deleteUserCatalog",
			"username" : event.data.username,
			"catalog" : event.data.catalog,
			"schema" : event.data.schema
		};
		$.ajaxSetup({
			async : false
		});
		$.post(requestURL, requestData, function(data) {
			layer.msg(data.msg);
		}, "json");
		$.ajaxSetup({
			async : true
		});
		$("#query-userCatalog").empty();
		update_query_userCatalog_area();
	    
	    layer.close(index);
	});   
});
// -----------------end userCatalog manager-------------------
var copy_query = (function(event) {
	var frameId = parent.currIframe;
	var qs = $("#" + frameId).contents().find("#query-submit");
	var queryStr = event.data.query;
	var ta = parent.document.getElementById(frameId).contentWindow;
	ta.window.editor.setValue(queryStr);
	var index = parent.layer.getFrameIndex(window.name);
	parent.layer.close(index);
	qs.click();
});

var delete_query = (function(event) {
	var index = layer.confirm('确认删除?', function(index){
		if (!window.localStorage)
			return;
		var query_list = query_histories();
		query_list.splice(event.data.index, 1);
		set_query_histories(query_list);
		$("#query-histories").empty();
		update_query_histories_area();
	    layer.close(index);
	});   
});

var create_table_column = (function(table_id, rows) {
	var thead = "<thead><tr><th>列名</th><th>类型</th><th>分区</th><th>注释</th></tr></thead>";
	$(table_id).append(thead);
	var tbody = document.createElement("tbody");
	for (var i = 0; i < rows.length; ++i) {
		var tr = document.createElement("tr");
		var columns = rows[i];
		for (var j = 0; j < columns.length; ++j) {
			if (j == 2)
				continue;
			var td = document.createElement("td");
			if (typeof columns[j] == "object") {
				$(td).text(JSON.stringify(columns[j]));
			} else {
				$(td).text(columns[j]);
			}
			$(tr).append(td);
		}
		$(tbody).append(tr);
	}
	$(table_id).append(tbody);
	/*
	 * $(table_id).fixedHeaderTable("destroy"); $(table_id).fixedHeaderTable();
	 */

});
var create_table = (function(table_id, headers, rows) {
	var thead = document.createElement("thead");
	var tr = document.createElement("tr");
	for (var i = 0; i < headers.length; ++i) {
		var th = document.createElement("th");
		$(th).text(headers[i]);

		$(tr).append(th);
	}
	$(thead).append(tr);
	$(table_id).append(thead);
	var tbody = document.createElement("tbody");
	for (var i = 0; i < rows.length; ++i) {
		var tr = document.createElement("tr");
		var columns = rows[i];
		for (var j = 0; j < columns.length; ++j) {
			var td = document.createElement("td");
			if (typeof columns[j] == "object") {
				$(td).text(JSON.stringify(columns[j]));
			} else {
				$(td).text(columns[j]);
			}
			$(tr).append(td);
		}
		$(tbody).append(tr);
	}
	$(table_id).append(tbody);
	/*
	 * $(table_id).fixedHeaderTable("destroy"); $(table_id).fixedHeaderTable();
	 */

});


var doneQueries = (function(state) {
	d3.json('../presto/state?state=' + state, function(queries) {

		var tbody = d3.select("#done-" + state).select("tbody");

		var rows = tbody.selectAll("tr").data(queries, function(query) {
			return query.queryId;
		});

		rows.enter().append("tr").attr("class", function(query) {
			switch (query.state) {
			case "FINISHED":
				return "success";
			case "FAILED":
				return "danger";
			case "CANCELED":
				return "warning";
			default:
				return "info";
			}
		});

		rows.exit().remove();

		rows.selectAll("td").data(
				function(queryInfo) {
					var splits = queryInfo.totalDrivers;
					var completedSplits = queryInfo.completedDrivers;

					var query = queryInfo.query;
					if (query.length > 200) {
						query = query.substring(0, 200) + "...";
					}

					return [
							queryInfo.queryId,
							queryInfo.elapsedTime,
							query,
							queryInfo.session.user,
							queryInfo.state,
							shortErrorType(queryInfo.errorType),
							completedSplits,
							splits,
							d3.format("%")(
									splits == 0 ? 0 : completedSplits / splits) ]
				}).enter().append("td").text(function(d) {
			return d;
		});

		tbody.selectAll("tr").sort(function(a, b) {
			return d3.descending(a.endTime, b.endTime);
		});
	});
});

var renderDoneQueries = (function(queries, state) {
});

var shortErrorType = (function(errorType) {
	switch (errorType) {
	case "USER_ERROR":
		return "USER";
	case "INTERNAL_ERROR":
		return "INTERNAL";
	case "INSUFFICIENT_RESOURCES":
		return "RESOURCES";
	}
	return errorType;
});

var table_search_old = (function() {
	query = "SELECT table_cat AS catalog, table_schem AS schema, table_name AS table_name FROM system.jdbc.tables WHERE table_type='TABLE' and table_name LIKE '%"
			+ $("#table_name").val() + "%'";
	var q = $("#" + currIframe).contents().find("#query");
	var qs = $("#" + currIframe).contents().find("#query-submit");
	set_textarea_value(currIframe, query);
	qs.click();
});


//ajax完成时回调函数
$(document).ajaxComplete(function(event, xhr, settings) {
    //从http头信息取出 在filter定义的sessionstatus，判断是否是 timeout
    if(xhr.getResponseHeader("sessionstatus")=="timeout"){ 
        //从http头信息取出登录的url ＝ loginPath
        if(xhr.getResponseHeader("loginPath")){
        	//打会到登录页面
        	 layer.confirm("会话过期，请重新登陆!" ,function(index){
        		parent.window.location.replace(xhr.getResponseHeader("loginPath"));  
        	    layer.close(index);
        	 });
        }
    }  
}); 

var t_tree=(function(){
	
	$("#searchTree").dynatree(
			{
				imagePath : "img",
				initAjax : {
					type : "GET",
					url : "../presto/search?content="+$("#table_name").val()
				},
				getProcess : function(data) {
							$("#searchTree").dynatree({
								children:data
							});
				},
				onLazyRead : function(node) {
					var param = "show columns from "
								+ node.parent.parent.data.key + "."
								+ node.parent.data.key + "."
								+ node.data.key;
					$.ajax({
						url : "../presto/query",
						data : {
							query : param
						},
						type : "POST",
						dataType : "json"
					}).done(function(data) {
						if (data["error"]) {
							console.log(data["error"]);
							return;
						}
						results = data["results"];
							for (var i = 0; i < results.length; i++) {
								var result = results[i][0];
								node.addChild({
									title : result,
									key : result,
									isLazy : false,
									isFolder : false
								});
							}
						node.setLazyNodeStatus(DTNodeStatus_Ok);
					}).fail(function() {
						node.data.isLazy = false;
						node.setLazyNodeStatus(DTNodeStatus_Ok);
						node.render();
					});
				},
				onDblClick:function(node, event){
					$("#" + currIframe)[0].contentWindow.editor.replaceSelection(" "+node.data.key); 
				},
				onCreate : function(node, span) {
					if (node.data.isLazy) {
						$(span).contextmenu({
										target:'#tableMenu',
										onItem:function(context, e) {
											var q = $("#" + currIframe).contents().find("#query");
											var qs = $("#" + currIframe).contents().find("#query-submit");
											table = node.data.key;
											schema = node.parent.data.key;
											catalog = node.parent.parent.data.key;
											var action=$(e.target).attr("href");
											if (action === "select") {
												query = "SELECT * FROM "
														+ catalog
														+ "."
														+ schema
														+ "."
														+ table
														+ " LIMIT 100";
												set_textarea_value(
														currIframe,
														query);
												qs.click();
											} else if (action === "select_no_execute") {
												query = "SELECT * FROM "
														+ catalog
														+ "."
														+ schema
														+ "."
														+ table
														+ " LIMIT 100";
												set_textarea_value(
														currIframe,
														query);
											} else if (action === "select_where") {
												select_data(
														"SELECT * FROM",
														catalog,
														schema, table,
														true);
											} else if (action === "select_where_no_execute") {
												select_data(
														"SELECT * FROM",
														catalog,
														schema, table,
														false);
											} else if (action === "select_count_where") {
												select_data(
														"SELECT COUNT(*) FROM",
														catalog,
														schema, table,
														true);
											} else if (action === "select_count") {
												select_data(
														"SELECT COUNT(*) FROM",
														catalog,
														schema, table,
														true);
											} else if (action === "partitions") {
												query = "SHOW PARTITIONS FROM "
														+ catalog
														+ "."
														+ schema
														+ "."
														+ table;
												set_textarea_value(
														currIframe,
														query);
												qs.click();
											} else if (action === "describe") {
												query = "DESCRIBE "
														+ catalog + "."
														+ schema + "."
														+ table;
												set_textarea_value(
														currIframe,
														query);
												qs.click();
											}
										}
										});
					}
				}
			});
	
});




function table_search(){
	if($("#table_name").val()==""){
		$("#tree").show();
		$("#searchTree").hide();
	}else{
		$("#table_name").val($("#table_name").val().replace(/\;|\%/g, ''));
		$("#tree").hide();
		$("#searchTree").show();
		t_tree();
		$("#searchTree").dynatree("option","initAjax",{url:"../presto/search?content="+$("#table_name").val()});
		$("#searchTree").dynatree("getTree").reload();
	}

}


function clear_search(){
	$("#table_name").val("");
	table_search();
}



function full_Monitor_screen(url){
	parent.layer.open({
		  type: 2,
		  shade: false,
		  area: ['100%', '100%'], //宽高
		  content:url
		});
}

function save_as_dataset(){
	var query = window.editor.getValue();
	console.log(query);
	var url="datasetBoard#/config/datasetalert/"+query;
	parent.layer.open({
		  type: 2,
		  shade: false,
		  area: ['100%', '100%'], //宽高
		  content:url
		});
}
