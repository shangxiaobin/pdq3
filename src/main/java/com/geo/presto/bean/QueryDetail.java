package com.geo.presto.bean;

public class QueryDetail {
	String queryId;
	String state;
	String scheduled;
	String fullyBlocked;
	String blockedReasons;
	String self;	
	String query;
	String elapsedTimeMillis;

	String endTime;
	String createTime;
	String runningDrivers;
	String queuedDrivers;
	String completedDrivers;
	String totalDrivers;
	String winId;


	SessionDetail session;
	
	public String getElapsedTimeMillis() {
		return elapsedTimeMillis;
	}
	public void setElapsedTimeMillis(String elapsedTimeMillis) {
		this.elapsedTimeMillis = elapsedTimeMillis;
	}
	public String getQueryId() {
		return queryId;
	}
	public void setQueryId(String queryId) {
		this.queryId = queryId;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getScheduled() {
		return scheduled;
	}
	public void setScheduled(String scheduled) {
		this.scheduled = scheduled;
	}
	public String getFullyBlocked() {
		return fullyBlocked;
	}
	public void setFullyBlocked(String fullyBlocked) {
		this.fullyBlocked = fullyBlocked;
	}
	public String getBlockedReasons() {
		return blockedReasons;
	}
	public void setBlockedReasons(String blockedReasons) {
		this.blockedReasons = blockedReasons;
	}
	public String getSelf() {
		return self;
	}
	public void setSelf(String self) {
		this.self = self;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}

	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getRunningDrivers() {
		return runningDrivers;
	}
	public void setRunningDrivers(String runningDrivers) {
		this.runningDrivers = runningDrivers;
	}
	public String getQueuedDrivers() {
		return queuedDrivers;
	}
	public void setQueuedDrivers(String queuedDrivers) {
		this.queuedDrivers = queuedDrivers;
	}
	public String getCompletedDrivers() {
		return completedDrivers;
	}
	public void setCompletedDrivers(String completedDrivers) {
		this.completedDrivers = completedDrivers;
	}
	public String getTotalDrivers() {
		return totalDrivers;
	}
	public void setTotalDrivers(String totalDrivers) {
		this.totalDrivers = totalDrivers;
	}
	public SessionDetail getSession() {
		return session;
	}
	public void setSession(SessionDetail session) {
		this.session = session;
	}
	
	public String getWinId() {
		return winId;
	}
	public void setWinId(String winId) {
		this.winId = winId;
	}

	
	@Override
	public String toString() {
		return new StringBuilder("[")
		.append("queryId=").append(queryId).append(",")
		.append("winId=").append(winId).append(",")
		.append("state=").append(state).append(",")
		.append("scheduled=").append(scheduled).append(",")
		.append("fullyBlocked=").append(fullyBlocked).append(",")
		.append("blockedReasons=").append(blockedReasons).append(",")
		.append("endTime=").append(endTime).append(",")
		.append("createTime=").append(createTime).append(",")
		.append("runningDrivers=").append(runningDrivers).append(",")
		.append("queuedDrivers=").append(queuedDrivers).append(",")
		.append("totalDrivers=").append(totalDrivers).append(",")
		.append("]")
		.toString();
	}
}
