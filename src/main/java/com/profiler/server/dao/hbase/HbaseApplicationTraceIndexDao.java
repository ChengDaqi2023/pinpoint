package com.profiler.server.dao.hbase;

import static com.profiler.common.hbase.HBaseTables.APPLICATION_TRACE_INDEX;
import static com.profiler.common.hbase.HBaseTables.APPLICATION_TRACE_INDEX_CF_TRACE;

import com.profiler.server.util.AcceptedTime;
import org.apache.hadoop.hbase.client.Put;
import org.springframework.beans.factory.annotation.Autowired;

import com.profiler.common.dto.thrift.Span;
import com.profiler.common.hbase.HbaseOperations2;
import com.profiler.common.util.BytesUtils;
import com.profiler.common.util.SpanUtils;
import com.profiler.server.dao.ApplicationTraceIndexDao;

/**
 * find traceids by application name
 * 
 * @author netspider
 */
public class HbaseApplicationTraceIndexDao implements ApplicationTraceIndexDao {

	@Autowired
	private HbaseOperations2 hbaseTemplate;

	@Override
	public void insert(String applicationName, final Span span) {
		int elapsedTime = span.getElapsed();
		byte[] value = new byte[8];
		BytesUtils.writeInt(elapsedTime, value, 0);
		BytesUtils.writeInt(span.getErr(), value, 4);

        long acceptedTime = AcceptedTime.getAcceptedTime();
        Put put = new Put(SpanUtils.getApplicationTraceIndexRowKey(applicationName, acceptedTime));
		put.add(APPLICATION_TRACE_INDEX_CF_TRACE, SpanUtils.getTraceId(span), acceptedTime, value);

		hbaseTemplate.put(APPLICATION_TRACE_INDEX, put);
	}
}
