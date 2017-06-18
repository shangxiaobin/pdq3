package com.geo.presto.bean;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.geo.presto.util.PropertiesUtils;

@Service("prestoConfig")
public class PrestoConfig {

	private String webResourceDir;

	private String prestoCoordinatorServer;

	private String catalog;

	private String schema;

	private String user;

	private String source;

	private int selectLimit;
	
	Map<String,String> sessionProps = null;

	
	public Map<String, String> getSessionProps() {
		return sessionProps;
	}

	public void setSessionProps(Map<String, String> sessionProps) {
		this.sessionProps = sessionProps;
	}

	public PrestoConfig(){
		Properties properties = PropertiesUtils.getProperties();
		this.webResourceDir = properties.getProperty("web.resource.dir", "web");
		this.prestoCoordinatorServer = properties.getProperty("presto.coordinator.server");
		this.catalog = properties.getProperty("catalog");
		this.schema = properties.getProperty("schema");
		this.source = properties.getProperty("source");
		this.selectLimit = Integer.parseInt(properties.getProperty("select.limit"));
		String hashPartitionCount = properties.getProperty("hash.partition.count");
		String taskAggregationConcurrency = properties.getProperty("task.aggregation.concurrency");
		Map<String,String> sessionProps=new HashMap<String, String>();
		if(StringUtils.isNotEmpty(hashPartitionCount)){
			sessionProps.put("hash_partition_count", hashPartitionCount);
		}
		if(StringUtils.isNotEmpty(taskAggregationConcurrency)){
			sessionProps.put("task_concurrency", taskAggregationConcurrency);
		}
		setSessionProps(sessionProps);
		
	}

	

	public void setWebResourceDir(String webResourceDir) {
		this.webResourceDir = webResourceDir;
	}

	public void setPrestoCoordinatorServer(String prestoCoordinatorServer) {
		this.prestoCoordinatorServer = prestoCoordinatorServer;
	}

	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public void setSelectLimit(int selectLimit) {
		this.selectLimit = selectLimit;
	}

	public String getWebResourceDir() {
		return webResourceDir;
	}

	public String getPrestoCoordinatorServer() {
		return prestoCoordinatorServer;
	}

	public String getCatalog() {
		return catalog;
	}

	public String getSchema() {
		return schema;
	}

	public String getUser() {
		return user;
	}

	public String getSource() {
		return source;
	}

	public int getSelectLimit() {
		return selectLimit;
	}
 
}
