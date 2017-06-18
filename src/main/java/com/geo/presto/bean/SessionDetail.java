package com.geo.presto.bean;

public class SessionDetail {
	String queryId;
	String transactionId;
	String clientTransactionSupport;		
	String user;	
	String principal;	
	String source;
	String catalog;
	String schema;
	String timeZoneKey;	
	String locale;
	String remoteUserAddress;			
	String userAgent;	
	String startTime;	
	String systemProperties;
	String catalogProperties;
	public String getQueryId() {
		return queryId;
	}
	public void setQueryId(String queryId) {
		this.queryId = queryId;
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public String getClientTransactionSupport() {
		return clientTransactionSupport;
	}
	public void setClientTransactionSupport(String clientTransactionSupport) {
		this.clientTransactionSupport = clientTransactionSupport;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPrincipal() {
		return principal;
	}
	public void setPrincipal(String principal) {
		this.principal = principal;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getCatalog() {
		return catalog;
	}
	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}
	public String getSchema() {
		return schema;
	}
	public void setSchema(String schema) {
		this.schema = schema;
	}
	public String getTimeZoneKey() {
		return timeZoneKey;
	}
	public void setTimeZoneKey(String timeZoneKey) {
		this.timeZoneKey = timeZoneKey;
	}
	public String getLocale() {
		return locale;
	}
	public void setLocale(String locale) {
		this.locale = locale;
	}
	public String getRemoteUserAddress() {
		return remoteUserAddress;
	}
	public void setRemoteUserAddress(String remoteUserAddress) {
		this.remoteUserAddress = remoteUserAddress;
	}
	public String getUserAgent() {
		return userAgent;
	}
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getSystemProperties() {
		return systemProperties;
	}
	public void setSystemProperties(String systemProperties) {
		this.systemProperties = systemProperties;
	}
	public String getCatalogProperties() {
		return catalogProperties;
	}
	public void setCatalogProperties(String catalogProperties) {
		this.catalogProperties = catalogProperties;
	}	
	
}
