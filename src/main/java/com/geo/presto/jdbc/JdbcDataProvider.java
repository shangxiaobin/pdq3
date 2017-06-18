package com.geo.presto.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.geo.presto.dataprovider.DataProvider;
import com.geo.presto.dataprovider.annotation.DatasourceParameter;
import com.geo.presto.dataprovider.annotation.ProviderName;
import com.geo.presto.dataprovider.annotation.QueryParameter;

/**
 * Created by yfyuan on 2016/8/17.
 */
@ProviderName(name = "jdbc")
public class JdbcDataProvider extends DataProvider {
	private static final Logger LOG = LoggerFactory
			.getLogger(JdbcDataProvider.class);

	@DatasourceParameter(label = "Driver (eg: com.mysql.jdbc.Driver)", type = DatasourceParameter.Type.Input, order = 1)
	private String DRIVER = "driver";

	@DatasourceParameter(label = "JDBC Url (eg: jdbc:mysql://hostname:port/db)", type = DatasourceParameter.Type.Input, order = 2)
	private String JDBC_URL = "jdbcurl";

	@DatasourceParameter(label = "User Name", type = DatasourceParameter.Type.Input, order = 3)
	private String USERNAME = "username";

	@DatasourceParameter(label = "Password", type = DatasourceParameter.Type.Password, order = 4)
	private String PASSWORD = "password";

	@QueryParameter(label = "SQL Text", type = QueryParameter.Type.TextArea, order = 1)
	private String SQL = "sql";

	public String[][] getData(Map<String, String> dataSource,
			Map<String, String> query) throws Exception {
		LOG.debug("Execute JdbcDataProvider.getData() Start!");
		
		Connection con = getConnection(dataSource);
		String sql = query.get(SQL);
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<String[]> list = null;
		LOG.info("SQL:"+sql);

		try {
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
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
		LOG.debug("Execute JdbcDataProvider.getData() End!");
		return list.toArray(new String[][] {});
	}

	@Override
	public int resultCount(Map<String, String> dataSource,
			Map<String, String> query) throws Exception {
		LOG.debug("Execute JdbcDataProvider.resultCount() Start!");
		
		Connection con = getConnection(dataSource);
		StringBuffer cubeSqlBuffer = new StringBuffer();
		String querySql = query.get(SQL).replace(";", "");
		cubeSqlBuffer.append("SELECT count(*) AS cnt FROM ( ").append(querySql)
				.append(" ) AS cube_query__");

		PreparedStatement ps = null;
		ResultSet rs = null;
		int count = 0;
		LOG.info("SQL:"+cubeSqlBuffer.toString());

		try {
			ps = con.prepareStatement(cubeSqlBuffer.toString());
			rs = ps.executeQuery();
			rs.next();
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
		LOG.debug("Execute JdbcDataProvider.resultCount() End!");
		return count;
	}

	private Connection getConnection(Map<String, String> dataSource)
			throws Exception {
		LOG.debug("Execute JdbcDataProvider.getConnection() Start!");
		
		//get paramter
		String driver = dataSource.get(DRIVER);
		String jdbcurl = dataSource.get(JDBC_URL);
		String username = dataSource.get(USERNAME);
		String password = dataSource.get(PASSWORD);
		LOG.debug("Get 'Rlational Database' JDBC Connection Paramter: driver='" + driver
				+ "',jdbcUrl='" + jdbcurl + "',userName='" + username
				+ "',passWord='" + password + "'");

		Properties props;
		Connection connection = null;
		try {
			Class.forName(driver);
			props = new Properties();
			props.setProperty("user", username);
			props.setProperty("password", password);
			connection = DriverManager.getConnection(jdbcurl, props);
		} catch (Exception e) {
			LOG.error("ERROR:" + e.getMessage());
			throw new Exception("ERROR:" + e.getMessage(), e);
		}
		LOG.debug("Execute JdbcDataProvider.getConnection() End!");
		return connection;
	}

}
