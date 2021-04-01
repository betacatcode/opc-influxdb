package com.xinda.xiaoxing.config.opc;

import com.xinda.xiaoxing.util.OPCUtil;
import org.openscada.opc.lib.da.Item;
import org.openscada.opc.lib.da.Server;
import org.openscada.opc.lib.da.ServerConnectionStateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import java.util.Map;


public class KepConnectionListener implements ServerConnectionStateListener {

    Logger logger = LoggerFactory.getLogger(KepConnectionListener.class);

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    Map<String, Item> kepIdItemMap;
    @Value("${opc.kep.prefix}")
    String kepPrefix;
    private boolean isConnected;

    @Override
    public void connectionStateChanged(boolean b) {
        if (b) {
            isConnected = true;
            logger.info("=========================已连接！");
            //初始化设备
            logger.info("=========================初始化设备");
            Server kepServer = (Server)applicationContext.getBean("kepServer");
            kepIdItemMap.putAll(OPCUtil.initItem(kepServer,kepPrefix));
        } else {
            isConnected = false;
            logger.info("=========================连接断开!");
        }
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }
}
