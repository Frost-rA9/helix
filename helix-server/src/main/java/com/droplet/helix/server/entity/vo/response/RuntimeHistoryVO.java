package com.droplet.helix.server.entity.vo.response;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class RuntimeHistoryVO {

    double diskSize;

    double memorySize;

    List<JSONObject> list = new LinkedList<>();
}
