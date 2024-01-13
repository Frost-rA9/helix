package com.droplet.helix.client.entity;

import com.alibaba.fastjson2.JSONObject;

public record Response(long id, int code, Object data, String message) {

    public boolean success() {
        return code == 200;
    }

    public JSONObject asJson() {
        return JSONObject.from(data);
    }

    public String asString() {
        return data.toString();
    }

    public static Response errorResponse(Exception exception) {
        return new Response(0, 500, null, exception.getMessage());
    }
}
