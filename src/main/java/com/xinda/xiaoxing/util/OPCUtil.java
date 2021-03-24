package com.xinda.xiaoxing.util;

import com.xinda.xiaoxing.entity.pojo.Tag;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIVariant;
import org.openscada.opc.lib.da.Group;
import org.openscada.opc.lib.da.Item;
import org.openscada.opc.lib.da.Server;
import org.openscada.opc.lib.da.browser.FlatBrowser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class OPCUtil {

    static Logger logger=LoggerFactory.getLogger(OPCUtil.class);


    /**
     * 根据连接的服务器和设备前缀获取设备
     *
     * @param server 服务器
     * @param prefix 设备前缀
     * @return id与设备的Map
     */
    public static Map<String, Item> initItem(Server server, String prefix) {

        Map<String, Item> idItemMap = null;
        try {
            FlatBrowser flatBrowser = server.getFlatBrowser();
            Collection<String> browse = flatBrowser.browse();
            String[] itemIds = browse.stream().filter(item -> item.startsWith(prefix)).toArray(String[]::new);

            Group group = server.addGroup();
            idItemMap = group.addItems(itemIds);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return idItemMap;
    }

    /**
     * 设备数据同步
     * @param server 服务器
     * @param idItemMap id对应设备的map
     */
    public static List<Tag> sync(Server server, Map<String, Item> idItemMap) {

        List<Tag> result=new ArrayList<>();
        for (String itemId : idItemMap.keySet()) {
            Item item = idItemMap.get(itemId);
            JIVariant itemValue;
            try {
                itemValue = item.read(false).getValue();
            } catch (Exception e) {//连接错误
                server.disconnect();
                e.printStackTrace();
                return result;
            }

            try {
                int type = itemValue.getType();
                String id = item.getId();

                if(type==JIVariant.VT_R4){
                    Tag tag=new Tag();
                    tag.setItem(id);
                    tag.setValue(itemValue.getObjectAsFloat());
                    result.add(tag);
//                    logger.info(tag.getValue()+"");
                }
            } catch (JIException e) {
                e.printStackTrace();
            }

        }
        return result;
    }
}
