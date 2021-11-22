package cloud.timo.TimoCloud.api.events.proxy;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.events.PropertyChangeEvent;
import cloud.timo.TimoCloud.api.objects.ProxyObject;

public abstract class ProxyPropertyChangeEvent<T> extends PropertyChangeEvent<ProxyObject, T> {

    public ProxyPropertyChangeEvent(ProxyObject instance, T oldValue, T newValue) {
        super(instance, oldValue, newValue);
    }

    public ProxyPropertyChangeEvent() {
    }

    @Override
    public ProxyObject getInstance() {
        return TimoCloudAPI.getUniversalAPI().getProxy(getInstanceId());
    }

    public ProxyObject getProxy() {
        return getInstance();
    }

}

