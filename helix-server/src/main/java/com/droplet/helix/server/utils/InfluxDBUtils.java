package com.droplet.helix.server.utils;

import com.alibaba.fastjson2.JSONObject;
import com.droplet.helix.server.entity.dto.RuntimeData;
import com.droplet.helix.server.entity.vo.request.RuntimeDetailVO;
import com.droplet.helix.server.entity.vo.response.RuntimeHistoryVO;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class InfluxDBUtils {

    @Value("${spring.influx.url}")
    String url;

    @Value("${spring.influx.user}")
    String user;

    @Value("${spring.influx.password}")
    String password;

    private final String BUCKET = "helix";

    private final String ORG = "lou";

    private InfluxDBClient influxDBClient;

    @PostConstruct
    public void init() {
        influxDBClient = InfluxDBClientFactory.create(url, user, password.toCharArray());
    }

    public void writeRuntimeData(int clientId, RuntimeDetailVO runtimeDetailVO) {
        RuntimeData runtimeData = new RuntimeData();
        BeanUtils.copyProperties(runtimeDetailVO, runtimeData);
        runtimeData.setTimestamp(new Date(runtimeDetailVO.getTimestamp()).toInstant());
        runtimeData.setClientId(clientId);
        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
        writeApi.writeMeasurement(BUCKET, ORG, WritePrecision.NS, runtimeData);
    }

    public RuntimeHistoryVO readRuntimeData(int clientId) {
        RuntimeHistoryVO runtimeHistoryVO = new RuntimeHistoryVO();
        String query = """
                from(bucket: "%s")
                |> range(start: %s)
                |> filter(fn: (r) => r["_measurement"] == "runtime")
                |> filter(fn: (r) => r["clientId"] == "%s")
                """;
        String format = String.format(query, BUCKET, "-1h", clientId);
        List<FluxTable> tables = influxDBClient.getQueryApi().query(format, ORG);
        int size = tables.size();
        if (size == 0) return runtimeHistoryVO;
        List<FluxRecord> records = tables.get(0).getRecords();
        for (int i = 0; i < records.size(); i++) {
            JSONObject object = new JSONObject();
            object.put("timestamp", records.get(i).getTime());
            for (int j = 0; j < size; j++) {
                FluxRecord record = tables.get(j).getRecords().get(i);
                object.put(record.getField(), record.getValue());
            }
            runtimeHistoryVO.getList().add(object);
        }
        return runtimeHistoryVO;
    }
}
