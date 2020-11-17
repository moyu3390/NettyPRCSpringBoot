/**
 * Copyright (C) 2016 Newland Group Holding Limited
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zakl.nettyrpcclient.config;

import com.google.common.eventbus.EventBus;
import com.zakl.nettyrpcclient.core.MessageSendExecutor;
import com.zakl.nettyrpcclient.event.ClientStopEvent;
import com.zakl.nettyrpcclient.event.ClientStopEventListener;
import com.zakl.nettyrpcclient.serialize.RpcSerializeProtocol;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;

import javax.annotation.PostConstruct;

/**
 * @author tangjie<https: / / github.com / tang-jie>
 * @filename:NettyRpcReference.java
 * @description:NettyRpcReference功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2016/10/7
 */

public class NettyRpcReference implements FactoryBean, DisposableBean {


    private String remoteInterfaceName;
    private String ipAddr;
    private int port;
    private RpcSerializeProtocol protocol;
    private EventBus eventBus = new EventBus();


    @Override
    public void destroy() {
        eventBus.post(new ClientStopEvent(0));
    }


    @PostConstruct
    public void init() {
        MessageSendExecutor.getInstance().setRpcServerLoader(ipAddr, port, protocol);
        ClientStopEventListener listener = new ClientStopEventListener();
        eventBus.register(listener);
    }


    @Override
    public Object getObject() {
        return MessageSendExecutor.getInstance().execute(getObjectType());
    }

    @Override
    public Class<?> getObjectType() {
        if (remoteInterfaceName == null) {
            //该bean参数尚未注入,不适合提前加载。
            return null;
        }
        try {
            return this.getClass().getClassLoader().loadClass(remoteInterfaceName);
        } catch (ClassNotFoundException e) {
            System.err.println("spring analyze fail!");
        }
        return null;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public String getRemoteInterfaceName() {
        return remoteInterfaceName;
    }

    public void setRemoteInterfaceName(String remoteInterfaceName) {
        this.remoteInterfaceName = remoteInterfaceName;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public RpcSerializeProtocol getProtocol() {
        return protocol;
    }

    public void setProtocol(RpcSerializeProtocol protocol) {
        this.protocol = protocol;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}