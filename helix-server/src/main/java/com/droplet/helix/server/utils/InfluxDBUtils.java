package com.droplet.helix.server.utils;

import com.droplet.helix.server.entity.dto.RuntimeData;
import com.droplet.helix.server.entity.vo.request.RuntimeDetailVO;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

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
}
