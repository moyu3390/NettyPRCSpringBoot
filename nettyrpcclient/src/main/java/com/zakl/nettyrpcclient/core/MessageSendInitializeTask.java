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
package com.zakl.nettyrpcclient.core;

import com.zakl.nettyrpc.common.config.RpcSystemConfig;
import com.zakl.nettyrpcclient.handler.MessageSendHandler;
import com.zakl.nettyrpc.common.serialize.RpcSerializeProtocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @author tangjie<https: / / github.com / tang-jie>
 * @filename:MessageSendInitializeTask.java
 * @description:MessageSendInitializeTask功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2016/10/7
 */
public class MessageSendInitializeTask implements Callable<Boolean> {

    private EventLoopGroup eventLoopGroup;
    private InetSocketAddress serverAddress;
    private RpcSerializeProtocol protocol;
    //允许重连,重连次数为5
    private int reconnectCnt = 5;

    public MessageSendInitializeTask(EventLoopGroup eventLoopGroup, InetSocketAddress serverAddress, RpcSerializeProtocol protocol) {
        this.eventLoopGroup = eventLoopGroup;
        this.serverAddress = serverAddress;
        this.protocol = protocol;
    }

    @Override
    public Boolean call() {
        Bootstrap b = new Bootstrap();
        b.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .remoteAddress(serverAddress);
        b.handler(new MessageSendChannelInitializer().buildRpcSerializeProtocol(protocol));

        ChannelFuture channelFuture = b.connect();
        channelFuture.addListener((ChannelFutureListener) channelFuture1 -> {
            if (channelFuture1.isSuccess()) {
                MessageSendHandler handler = channelFuture1.channel().pipeline().get(MessageSendHandler.class);
                RpcServerLoader.getInstance().setMessageSendHandler(handler);
            } else if (reconnectCnt > 0) {
                reconnectCnt--;
                //定时重连
                eventLoopGroup.schedule(() -> {
                    System.out.println("NettyRPC server is down,start to reconnecting to: " + (5 - reconnectCnt) + " times" + serverAddress.getAddress().getHostAddress() + ':' + serverAddress.getPort());
                    call();
                }, RpcSystemConfig.SYSTEM_PROPERTY_CLIENT_RECONNECT_DELAY, TimeUnit.SECONDS);
            }
        });
        return Boolean.TRUE;
    }
}
