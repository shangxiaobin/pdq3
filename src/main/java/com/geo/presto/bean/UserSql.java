package com.geo.presto.bean;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UserSql {
	public static SimpleDateFormat sdp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Integer id;

    private String username;

    private String sql;

    private String desc;

    private Date createTime;

    private String strCreateTime;
    public Integer getId() {
        return id;
    }

    public String getStrCreateTime() {
		return strCreateTime;
	}

	public void setStrCreateTime(String strCreateTime) {
		this.strCreateTime = strCreateTime;
	}

	public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
        try {
			this.strCreateTime = sdp.format(createTime);
		} catch (Exception e) {
			// TODO: handle exception
		}
    }
}