package com.xinda.xiaoxing.config.opc;

import org.openscada.opc.lib.common.ConnectionInformation;
import org.openscada.opc.lib.da.Item;
import org.openscada.opc.lib.da.Server;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

@Configuration
public class OPCConfig {
    @Bean
    @ConfigurationProperties("opc.kep")
    public ConnectionInformation connectionInformation(){
        return new ConnectionInformation();
    }

    @Bean
    public Map<String, Item> kepIdItemMap(){
        return new HashMap<>();
    }

    @Bean
    public KepConnectionListener kepConnectionListener(){
        return new KepConnectionListener();
    }

    @Bean
    public Server kepServer(ConnectionInformation connectionInformation, KepConnectionListener kepConnectionListener){
        Server server= new Server(connectionInformation, Executors.newSingleThreadScheduledExecutor()); // 启动服务
        server.addStateListener(kepConnectionListener);
        return server;
    }

}
