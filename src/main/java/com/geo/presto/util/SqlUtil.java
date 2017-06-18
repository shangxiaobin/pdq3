package com.geo.presto.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class SqlUtil {
	public static Map<String, Object> pasreSessionPropsAndSql(String srcSql) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, String> props = new HashMap<String, String>();
		map.put("sessionProps", props);
		if (StringUtils.isNotEmpty(srcSql)) {
			String[] split = srcSql.split(";");
			for (String str : split) {
				if (StringUtils.isNotEmpty(str) && str.contains("set") && str.contains(" session ")) {
					String[] kv = StringUtils.substringAfterLast(str, " session ").split("=");
					if (kv != null && kv.length == 2) {
						props.put(kv[0].trim(), kv[1].trim());
					}
				} else {
					map.put("querySql", str);
					break;
				}
			}
			if (props.size() > 0 && split[0].contains("explain")) {
				String sql = (String) map.get("querySql");
				map.put("querySql", StringUtils.substringBefore(split[0], "set") + sql);
			}
		} else {
			map.put("querySql", srcSql);
		}
		return map;
	}

	public static String formatSqlCacheKey(String query) {
		query = query.replaceAll(" ", "").replaceAll("\r\n", "").replaceAll("\n", "").toUpperCase();
		return query;
	}

	public static void main(String[] args) {
		// String srcSql = "set  session.a  = a;set  session.b= b;sql";
		String srcSql = "ss";
		Map<String, Object> map = pasreSessionPropsAndSql(srcSql);
		Set<Entry<String, Object>> set = map.entrySet();
		for (Entry<String, Object> entry : set) {
			System.out.println(entry.getKey() + ":" + entry.getValue());
		}
	}
}
