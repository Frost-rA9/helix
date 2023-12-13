package com.droplet.helix.client.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConnectionConfig {

    String address;

    String token;
}
