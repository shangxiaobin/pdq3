package com.geo.presto.services;

import com.geo.presto.bean.PrestoConfig;
import com.geo.presto.exception.QueryErrorException;
import com.geo.presto.bean.PrestoQueryResult;


public interface PrestoService {
	

	public PrestoQueryResult doQuery(String query, PrestoConfig config) throws QueryErrorException;

}
