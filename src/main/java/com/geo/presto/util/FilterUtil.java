package com.geo.presto.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.geo.presto.bean.UserCatalogKey;
import com.geo.presto.bean.PrestoConfig;
import com.geo.presto.bean.PrestoQueryResult;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.util.TablesNamesFinder;

public class FilterUtil {

	public static PrestoConfig getPrestoConfig(String username, String sql) {
		PrestoConfig pc = new PrestoConfig();
		List<String> tableFromSimpleSql = FilterUtil.getTableFromSimpleSql(sql);
		if (tableFromSimpleSql.size() > 1) {
			String[] split = tableFromSimpleSql.get(0).split("\\.");
			if (split.length > 1) {
				pc.setCatalog(split[0]);
				pc.setSchema(split[1]);
			} else {
				pc.setCatalog("hive");
				pc.setSchema("default");
			}
		} else {
			pc.setCatalog("hive");
			pc.setSchema("default");
		}
		pc.setUser(username);
		return pc;
	}

	/**
	 * 5:29:28 PM Administrator
	 * 
	 * @throws Exception
	 */
	public static void filterSqlQuery(List<UserCatalogKey> userCatalogList, String sql) throws Exception {
		List<String> tableFromSimpleSql = FilterUtil.getTableFromSimpleSql(sql);
		Map<String, List<String>> catalogMap = FilterUtil.getCatalog(userCatalogList);
		if(catalogMap.containsKey("all")){
			return;
		}
		for (String str : tableFromSimpleSql) {
			str = str.toLowerCase();
			if (str.contains("system")||str.contains("information_schema")) {
				continue;
			}
			String[] split = str.split("\\.");

			if (split.length > 0 && !catalogMap.containsKey("all") && !catalogMap.containsKey(split[0])) {
				throw new Exception("没有访问 " + split[0] + " 的权限");
			} else if (split.length > 2 && !catalogMap.get(split[0]).contains("all")
					&& !catalogMap.get(split[0]).contains(split[1])) {
				throw new Exception("没有访问 " + split[0] + "." + split[1] + " 的权限");
			}
		}
	}

	public static PrestoQueryResult filterQueryResult(PrestoQueryResult prestoQueryResult,
			List<UserCatalogKey> userCatalogList, String sql) {
		if (userCatalogList == null) {
			return prestoQueryResult;
		}
		if (prestoQueryResult.getColumns().size() == 1) {
			if (prestoQueryResult.getColumns().get(0).equalsIgnoreCase("Catalog")) {// show
																					// Catalog
				Set<String> catalog = FilterUtil.getCatalog(userCatalogList).keySet();
				if (catalog.contains("all")) {
					return prestoQueryResult;
				}
				List<List<Object>> list = prestoQueryResult.getRecords();
				Iterator<List<Object>> iterator = list.iterator();
				while (iterator.hasNext()) {
					if (!catalog.contains(iterator.next().get(0))) {
						iterator.remove();
					}
				}
			} else if (prestoQueryResult.getColumns().get(0).equalsIgnoreCase("Schema")) {// show
				Map<String, List<String>> catalog = FilterUtil.getCatalog(userCatalogList);						// 库
				if(catalog.containsKey("all")){
					return prestoQueryResult;
				}
				List<String> table = FilterUtil.getTableFromSimpleSql(sql);
				List<String> schema = catalog.get(table.get(0));
				if (schema.contains("all")) {
					return prestoQueryResult;
				}
				List<List<Object>> list = prestoQueryResult.getRecords();
				Iterator<List<Object>> iterator = list.iterator();
				while (iterator.hasNext()) {
					if (!schema.contains(iterator.next().get(0))) {
						iterator.remove();
					}
				}
			}
		} else if (prestoQueryResult.getColumns().size() == 3
				&& prestoQueryResult.getColumns().get(1).equalsIgnoreCase("Schema")) {// 搜表
			Map<String, List<String>> catalogMap = FilterUtil.getCatalog(userCatalogList);
			if(catalogMap.containsKey("all")){
				return prestoQueryResult;
			}
			List<List<Object>> records = prestoQueryResult.getRecords();
			Iterator<List<Object>> iterator = records.iterator();
			while (iterator.hasNext()) {
				List<Object> next = iterator.next();
				String catalog = next.get(0).toString();
				String schema = next.get(1).toString();
				if (!catalogMap.containsKey("all") && !catalogMap.containsKey(catalog)) {
					iterator.remove();
				} else if (!catalogMap.get(catalog).contains("all") && !catalogMap.get(catalog).contains(schema)) {
					iterator.remove();
				}
			}
		}
		return prestoQueryResult;
	}

	public static Map<String, List<String>> getCatalog(List<UserCatalogKey> userCatalogList) {
		Map<String, List<String>> result = new HashMap<String, List<String>>();
		if (userCatalogList != null && userCatalogList.size() > 0) {
			for (UserCatalogKey userCatalogKey : userCatalogList) {
				if (result.containsKey(userCatalogKey.getCatalog())) {
					result.get(userCatalogKey.getCatalog()).add(userCatalogKey.getSchema());
				} else {
					List<String> list = new LinkedList<String>();
					list.add(userCatalogKey.getSchema());
					result.put(userCatalogKey.getCatalog(), list);
				}
			}
		}
		return result;
	}

	public static void main(String[] args) throws JSQLParserException {
		List<String> tableFromSql = getTableFromSimpleSql(
				"select  count(*) from  fsdf.fsdf sdfsd fsdfs sdfsdfs fsd(*) from aa.bb fff from sdfsd.fsdfsdfsdfs ");
		System.out.println(tableFromSql);

		String sql = "select b.type,mobbank,c.type,count(distinct a.mobile) from fsdf.hq_zx_data_app_0217 a join hq_zx_usertype_0216 b on a.mobile=b.mobile join hq_tag_app_0216 c on a.tag=c.tag group by b.type,mobbank,c.type";
		Statement statement = CCJSqlParserUtil.parse(sql);
		Select selectStatement = (Select) statement;
		TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
		List<String> tableList = tablesNamesFinder.getTableList(selectStatement);
		System.out.println(tableList);
	}

	public static List<String> getTableFromSimpleSql(String sql) {
		LinkedHashSet<String> set = new LinkedHashSet<String>();
		String regex = "([\\w| |\\(|\\)|\\*]+) from +([\\w|\\.]+) *";

		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(sql);
		while (matcher.find()) {
			set.add(matcher.group(2).toLowerCase());
		}
		ArrayList<String> a = new ArrayList<>(set);
		List<String> tableList = new ArrayList<>(set);
		try {//sqlParser解析无异常则替代正则解析
			Statement statement = CCJSqlParserUtil.parse(sql);
			Select selectStatement = (Select) statement;
			TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
			tableList = tablesNamesFinder.getTableList(selectStatement);
		} catch (Exception e) {
			//tableList = new ArrayList<String>();
		}
		return tableList;
	}

	public static List<String> getSchema(List<UserCatalogKey> userCatalogList) {
		List<String> result = new ArrayList<String>();
		if (userCatalogList != null && userCatalogList.size() > 0) {
			for (UserCatalogKey userCatalogKey : userCatalogList) {
				result.add(userCatalogKey.getSchema());
			}
		}
		return result;
	}
}
