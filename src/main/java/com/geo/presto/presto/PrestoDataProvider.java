package com.geo.presto.presto;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.facebook.presto.jdbc.PrestoConnection;
import com.facebook.presto.jdbc.PrestoStatement;
import com.geo.presto.Constant;
import com.geo.presto.dataprovider.DataProvider;
import com.geo.presto.dataprovider.annotation.DatasourceParameter;
import com.geo.presto.dataprovider.annotation.ProviderName;
import com.geo.presto.dataprovider.annotation.QueryParameter;
import com.geo.presto.util.PropertiesUtils;


@ProviderName(name = "presto")
public class PrestoDataProvider extends DataProvider {
	private static final Logger LOG = LoggerFactory
			.getLogger(PrestoDataProvider.class);

	@DatasourceParameter(label = "Driver (eg: com.facebook.presto.jdbc.PrestoDriver)", type = DatasourceParameter.Type.Input, order = 1)
	private String DRIVER = "driver";

	@DatasourceParameter(label = "JDBC Url (eg: jdbc:presto://host:port/catalog)", type = DatasourceParameter.Type.Input, order = 2)
	private String JDBC_URL = "jdbcurl";

	@DatasourceParameter(label = "User Name", type = DatasourceParameter.Type.Input, order = 3)
	private String USERNAME = "username";

	@DatasourceParameter(label = "Password", type = DatasourceParameter.Type.Password, order = 4)
	private String PASSWORD = "password";

	@QueryParameter(label = "Presto SQL Text", type = QueryParameter.Type.TextArea, order = 1)
	private String SQL = "sql";

	@Override
	public String[][] getData(Map<String, String> dataSource,
			Map<String, String> query) throws Exception {
		LOG.debug("Execute PrestoDataProvider.getData() Start!");

		PrestoConnection con = getConnection(dataSource);
		String sql = query.get(SQL);
		PrestoStatement ps = null;
		ResultSet rs = null;
		List<String[]> list = null;
		LOG.info("Presto SQL:" + sql);

		try {
			ps = (PrestoStatement) con.createStatement();
			rs = ps.executeQuery(sql);
			ResultSetMetaData metaData = rs.getMetaData();
			int columnCount = metaData.getColumnCount();
			list = new LinkedList<>();
			String[] row = new String[columnCount];
			for (int i = 0; i < columnCount; i++) {
				row[i] = metaData.getColumnName(i + 1);
			}
			list.add(row);
			while (rs.next()) {
				row = new String[columnCount];
				for (int j = 0; j < columnCount; j++) {
					row[j] = rs.getString(j + 1);
				}
				list.add(row);
			}
		} catch (Exception e) {
			LOG.error("ERROR:" + e.getMessage());
			throw new Exception("ERROR:" + e.getMessage(), e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					LOG.error("ERROR:" + e.getMessage());
					throw new Exception("ERROR:" + e.getMessage(), e);
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (Exception e) {
					LOG.error("ERROR:" + e.getMessage());
					throw new Exception("ERROR:" + e.getMessage(), e);
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					LOG.error("ERROR:" + e.getMessage());
					throw new Exception("ERROR:" + e.getMessage(), e);
				}
			}
		}
		LOG.debug("Execute PrestoDataProvider.getData() End!");
		return list.toArray(new String[][] {});
	}

	@Override
	public int resultCount(Map<String, String> dataSource,
			Map<String, String> query) throws Exception {
		LOG.debug("Execute PrestoDataProvider.resultCount() Start!");
		int count = 0;
		String querySql = query.get(SQL).replace(";", "");
		String skipList = PropertiesUtils
				.findPropertiesKey(Constant.DATAPROVIDER_SKIPCOUNT);
		if (isSkipCount(querySql, skipList)) {
			LOG.info(Constant.DATAPROVIDER_SKIPCOUNT + "=" + skipList
					+ ",the resultCount is skip set count="
					+ Constant.DATAPROVIDER_RESULTLIMIT);
			count = Integer.valueOf(PropertiesUtils
					.findPropertiesKey(Constant.DATAPROVIDER_RESULTLIMIT));
			return count;
		}

		StringBuffer cubeSqlBuffer = new StringBuffer();
		PrestoConnection con = getConnection(dataSource);
		cubeSqlBuffer.append("SELECT count(*) AS cnt FROM ( ").append(querySql)
				.append(" ) AS cube_query__");

		PrestoStatement ps = null;
		ResultSet rs = null;

		LOG.info("Presto SQL:" + cubeSqlBuffer.toString());

		try {
			ps = (PrestoStatement) con.createStatement();
			rs = ps.executeQuery(cubeSqlBuffer.toString());
			if (rs.next())
				count = rs.getInt("cnt");
		} catch (Exception e) {
			LOG.error("ERROR:" + e.getMessage());
			throw new Exception("ERROR:" + e.getMessage(), e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					LOG.error("ERROR:" + e.getMessage());
					throw new Exception("ERROR:" + e.getMessage(), e);
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (Exception e) {
					LOG.error("ERROR:" + e.getMessage());
					throw new Exception("ERROR:" + e.getMessage(), e);
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					LOG.error("ERROR:" + e.getMessage());
					throw new Exception("ERROR:" + e.getMessage(), e);
				}
			}
		}
		LOG.debug("Execute PrestoDataProvider.resultCount() End!");
		return count;
	}

	private boolean isSkipCount(String querySql, String skipList) {
		boolean isMatch = false;
		String sql = querySql.toLowerCase();
		String[] excludeList = skipList
				.split(Constant.DATAPROVIDER_SKIPCOUNT_SPLIT);
		List<String> regexList = new ArrayList<String>();
		for (String exclude : excludeList) {
			regexList.add("(select.*from.*" + exclude + "\\..*)");
		}

		for (String regex : regexList) {
			if (sql.matches(regex))
				isMatch = true;
		}
		return isMatch;
	}

	private PrestoConnection getConnection(Map<String, String> dataSource)
			throws Exception {
		LOG.debug("Execute PrestoDataProvider.getConnection() Start!");

		// 获取配置参数
		String driver = dataSource.get(DRIVER);
		String jdbcurl = dataSource.get(JDBC_URL);
		String username = dataSource.get(USERNAME);
		String password = dataSource.get(PASSWORD);
		LOG.debug("Get 'Presto' JDBC Connection Paramter: driver='" + driver
				+ "',jdbcUrl='" + jdbcurl + "',userName='" + username
				+ "',passWord='" + password + "'");
		//
		PrestoConnection connection = null;
		try {
			Class.forName(driver);
			connection = (PrestoConnection) DriverManager.getConnection(
					jdbcurl, username, password);
		} catch (Exception e) {
			LOG.error("ERROR:" + e.getMessage());
			throw new Exception("ERROR:" + e.getMessage(), e);
		}
		LOG.debug("Execute PrestoDataProvider.getConnection() End!");
		return connection;
	}
}
