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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import io.smp.client.exception.SmpException;
import io.smp.client.network.Channel;
import io.smp.client.network.ChannelImpl;
import io.smp.client.network.handler.ChannelWriteException;
import io.smp.client.network.handler.DispatchMessageReadHandler;
import io.smp.client.network.handler.MessageWriter;
import io.smp.client.network.message.impl.LoginMessageRequest;
import io.smp.client.network.selection.ChannelSelector;

public final class ClientBuilder {

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
            final DispatchMessageReadHandler dispatchMessageReadHandler = new DispatchMessageReadHandler();
            final SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress(host, port));
            final Channel channel = new ChannelImpl(socketChannel,
                new MessageWriter(null),
                dispatchMessageReadHandler);

            socketChannel.configureBlocking(false);
            ChannelSelector.registerSelector(channel, socketChannel);

            final LoginMessageRequest login = new LoginMessageRequest(user, password);
            final ClientImpl client = new ClientImpl(channel, onMessageListener);
            dispatchMessageReadHandler.subscribe(client);
            channel.dispatchWrite(login);
            return client;
        }
        catch (SmpException e) {
            throw e;
        }
        catch (IOException | ChannelWriteException e) {
            throw new SmpException("Error opening connection with " + host+':'+port, e);
        }
    }
}
