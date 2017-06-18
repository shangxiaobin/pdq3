package com.geo.presto.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class JsonUtil {

	public static void writeJSON(HttpServletResponse resp, Object obj) {

		try {
			resp.setContentType("application/json");
			ObjectMapper mapper = new ObjectMapper();
			OutputStream stream = resp.getOutputStream();
			mapper.writeValue(stream, obj);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static Map<String, Set<String>> FormatResults(List<List<Object>> results) {
		Map<String, Set<String>> map = new HashMap<String, Set<String>>();
		if (results == null && results.size() < 1) {
			return map;
		}
		for (Iterator iterator = results.iterator(); iterator.hasNext();) {
			List<Object> list = (List<Object>) iterator.next();
			if (list.size() > 3) {
				String c = (String) list.get(3);
				String table = (String) list.get(2);
				if (map.containsKey(table)) {
					map.get(table).add(c);
				} else {
					Set<String> set = new HashSet<String>();
					set.add(c);
					map.put(table, set);
				}
			}
		}
		return map;
	}

	public static void main(String[] args) {
		Map<String, Set<String>> map = new HashMap<String, Set<String>>();
		Set<String> set = new HashSet<String>();
		set.add("a");
		set.add("b");
		set.add("c");
		map.put("key", set);
		String jsonString = JSON.toJSONString(map, SerializerFeature.WriteMapNullValue);
		System.out.println(jsonString);
	}
}
