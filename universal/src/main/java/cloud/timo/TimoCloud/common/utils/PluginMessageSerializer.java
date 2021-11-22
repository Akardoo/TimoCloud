package cloud.timo.TimoCloud.common.utils;

import cloud.timo.TimoCloud.api.messages.objects.AddressedPluginMessage;
import cloud.timo.TimoCloud.common.json.JsonConverter;

import java.util.Map;

public class PluginMessageSerializer {

    public static AddressedPluginMessage deserialize(Map map) {
        return JsonConverter.convertMapIfNecessary(map, AddressedPluginMessage.class);
    }

}
