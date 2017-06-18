package com.geo.presto.services;

import static io.airlift.json.JsonCodec.jsonCodec;
import static java.lang.String.format;
import io.airlift.http.client.HttpClientConfig;
import io.airlift.http.client.jetty.JettyHttpClient;
import io.airlift.json.JsonCodec;
import io.airlift.units.Duration;

import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.geo.presto.bean.PrestoConfig;
import com.geo.presto.controller.PrestoController;
import com.geo.presto.exception.QueryErrorException;
import com.geo.presto.bean.PrestoQueryResult;
import com.facebook.presto.client.ClientSession;
import com.facebook.presto.client.Column;
import com.facebook.presto.client.QueryError;
import com.facebook.presto.client.QueryResults;
import com.facebook.presto.client.StatementClient;
import com.google.common.collect.Lists;


@Service("prestoService")
public class PrestoServiceImpl implements PrestoService {
	private static Logger log = LoggerFactory.getLogger(PrestoServiceImpl.class);
	private PrestoConfig prestoConfig;

	@Inject
	public PrestoServiceImpl(PrestoConfig prestoConfig) {
		this.prestoConfig = prestoConfig;
	}

	@Override
	public PrestoQueryResult doQuery(String query, PrestoConfig config) throws QueryErrorException {

		int limit = prestoConfig.getSelectLimit();

		String prestoCoordinatorServer = prestoConfig.getPrestoCoordinatorServer();
		String source = prestoConfig.getSource();
		String catalog = config.getCatalog();
		String schema = config.getSchema();
		String user = config.getUser();
		Duration duration = new Duration(10, TimeUnit.SECONDS);
		HttpClientConfig httpClientConfig = new HttpClientConfig().setConnectTimeout(duration);
		JettyHttpClient httpClient = new JettyHttpClient(httpClientConfig);
		JsonCodec<QueryResults> jsonCodec = jsonCodec(QueryResults.class);
		Map<String, String> sessionProps = new HashMap<String, String>();
		sessionProps.putAll(prestoConfig.getSessionProps());// 载入默认配置
		sessionProps.putAll(config.getSessionProps());// 载入用户设置配置
		
		log.info("sessionProps:[" + sessionProps+"],SQL:["+query+"]");
		ClientSession clientSession = new ClientSession(URI.create(prestoCoordinatorServer), user, source, catalog, schema, TimeZone.getDefault().getID(), Locale.getDefault(), sessionProps, null, false, duration);
		StatementClient client = new StatementClient(httpClient, jsonCodec, clientSession, query);

		try {
			while (client.isValid() && (client.current().getData() == null)) {
				client.advance();
			}

			if ((!client.isFailed()) && (!client.isGone()) && (!client.isClosed())) {
				QueryResults results = client.isValid() ? client.current() : client.finalResults();
				if (results.getUpdateType() != null) {
					PrestoQueryResult prestoQueryResult = new PrestoQueryResult();
					prestoQueryResult.setUpdateType(results.getUpdateType());
					return prestoQueryResult;
				} else if (results.getColumns() == null) {
					throw new QueryErrorException(new SQLException(format("Query %s has no columns\n", results.getId())));
				} else {
					PrestoQueryResult prestoQueryResult = new PrestoQueryResult();
					prestoQueryResult.setUpdateType(results.getUpdateType());
					List<String> columns = Lists.transform(results.getColumns(), Column::getName);
					prestoQueryResult.setColumns(columns);
					List<List<Object>> rowDataList = new ArrayList<List<Object>>();
					while (client.isValid()) {
						Iterable<List<Object>> data = client.current().getData();
						if (data != null) {
							data.forEach(row -> {
								List<Object> columnDataList = row.stream().collect(Collectors.toList());
								rowDataList.add(columnDataList);
							});
						}
						if (rowDataList.size() >= limit) {
							prestoQueryResult.setWarningMessage(String.format("now fetch size is %d. This is more than %d. So, fetch operation stopped.", rowDataList.size(), limit));
							break;
						}
						client.advance();
					}
					prestoQueryResult.setRecords(rowDataList);
					return prestoQueryResult;
				}
			}

			if (client.isClosed()) {
				throw new RuntimeException("Query aborted by user");
			} else if (client.isGone()) {
				throw new RuntimeException("Query is gone (server restarted?)");
			} else if (client.isFailed()) {
				throw resultsException(client.finalResults());
			}

		} catch (Exception e) {
			//throw new RuntimeException(" PrestoService doQuery interval error：", e);
			throw resultsException(client.finalResults());
		} finally {
			client.close();
			httpClient.close();
		}
		throw new RuntimeException("should not reach");

	}

	private QueryErrorException resultsException(QueryResults results) {
		QueryError error = results.getError();
		String message = format("Query failed (#%s): %s", results.getId(), error.getMessage());
		Throwable cause = (error.getFailureInfo() == null) ? null : error.getFailureInfo().toException();
		return new QueryErrorException(error, new SQLException(message, error.getSqlState(), error.getErrorCode(), cause));
	}

}
