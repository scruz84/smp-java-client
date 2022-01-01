/**
 *    SimpleMessagePassing.
 *    Copyright (C) 2021  Sergio Cruz
 *
 *    This program is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU General Public License
 *    as published by the Free Software Foundation; either version 2
 *    of the License, or (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package io.smp.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.smp.client.exception.SmpException;
import io.smp.client.network.handler.DispatchMessageHandler;
import io.smp.client.network.handler.FragmentDecoder;
import io.smp.client.network.handler.FragmentEncoder;
import io.smp.client.network.handler.HandlerUtil;
import io.smp.client.network.handler.MessageDecoder;
import io.smp.client.network.handler.MessageEncoder;
import io.smp.client.network.message.impl.LoginMessageRequest;

public final class ClientBuilder {

    private static AtomicLong counter = new AtomicLong(0);
    private static final ExecutorService eventLoopExecutor = Executors.newFixedThreadPool(2, r -> {
        final Thread th = new Thread(r, "smp ("+counter.getAndIncrement()+')');
        th.setDaemon(true);
        return th;
    });

    private String host;
    private int port = 1984;
    private String user;
    private String password;
    private Client.OnMessageListener onMessageListener;

    public String getHost() {
        return host;
    }

    public ClientBuilder setHost(String host) {
        this.host = host;
        return this;
    }

    public String getUser() {
        return user;
    }

    public ClientBuilder setUser(String user) {
        this.user = user;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public ClientBuilder setPassword(String password) {
        this.password = password;
        return this;
    }

    public int getPort() {
        return port;
    }

    public ClientBuilder setPort(int port) {
        this.port = port;
        return this;
    }

    public Client.OnMessageListener getOnMessageListener() {
        return onMessageListener;
    }

    public ClientBuilder setOnMessageListener(Client.OnMessageListener onMessageListener) {
        this.onMessageListener = onMessageListener;
        return this;
    }

    public Client build() throws SmpException {
        try {
            final EventLoopGroup workerGroup = new NioEventLoopGroup(2, eventLoopExecutor);
            final Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);

            final DispatchMessageHandler dispatchMessageHandler = new DispatchMessageHandler();
            bootstrap.handler(new ChannelInitializer<io.netty.channel.socket.SocketChannel>() {
                @Override
                public void initChannel(io.netty.channel.socket.SocketChannel ch)
                    throws Exception {
                    ch.pipeline().addLast(
                        new FixedLengthFrameDecoder(HandlerUtil.packet_size),
                        new FragmentDecoder(),
                        new MessageDecoder(),
                        new FragmentEncoder(),
                        new MessageEncoder(),
                        dispatchMessageHandler);
                }
            });
            final ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            final NettyClientImpl nettyClient = new NettyClientImpl(channelFuture, onMessageListener);
            dispatchMessageHandler.subscribe(nettyClient);
            final LoginMessageRequest login = new LoginMessageRequest(user, password);
            channelFuture.channel().writeAndFlush(login);
            return nettyClient;
        }
        catch (SmpException e) {
            throw e;
        }
        catch (Exception e) {
            throw new SmpException("Error opening connection with " + host+':'+port, e);
        }
    }


}
