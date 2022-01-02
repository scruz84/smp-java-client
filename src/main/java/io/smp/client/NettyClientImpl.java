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

import java.nio.charset.StandardCharsets;
import java.util.concurrent.Flow;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.netty.channel.ChannelFuture;
import io.smp.client.exception.SmpException;
import io.smp.client.network.message.Message;
import io.smp.client.network.message.impl.InputMessage;
import io.smp.client.network.message.impl.SendMessage;
import io.smp.client.network.message.impl.SubscribeTopic;
import io.smp.client.network.message.impl.TopicMessage;

final class NettyClientImpl implements Client, Flow.Subscriber<Message> {

    private static final Logger logger = Logger.getLogger(NettyClientImpl.class.getCanonicalName());

    private final ChannelFuture channel;
    private final OnMessageListener onMessageListener;

    NettyClientImpl(ChannelFuture channel, OnMessageListener onMessageListener) {
        this.channel = channel;
        this.onMessageListener = onMessageListener;
    }

    @Override
    public void subscribeTopic(String topic) throws SmpException {
        try {
            channel.channel().writeAndFlush(new SubscribeTopic(topic, true));
        } catch (Exception e) {
            throw new SmpException("Error subscribing to topic '"+ topic+"': " + e.getMessage(), e);
        }
    }

    @Override
    public void unSubscribeTopic(String topic) throws SmpException {
        try {
            channel.channel().writeAndFlush(new SubscribeTopic(topic, false));
        } catch (Exception e) {
            throw new SmpException("Error un-subscribing to topic '"+ topic+"': " + e.getMessage(), e);
        }
    }

    @Override
    public void sendMessage(String topic, byte[] message) throws SmpException {
        if (topic.length()>255) {
            throw new SmpException("Topic name size must be lower or equal to 256 characters");
        }
        try {
            channel.channel().writeAndFlush(new SendMessage(topic, message));
        } catch (Exception e) {
            throw new SmpException("Error subscribing to topic '"+ topic+"': " + e.getMessage(), e);
        }
    }

    @Override
    public void sendMessage(String topic, String message) throws SmpException {
        sendMessage(topic, message.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        subscription.request(Long.MAX_VALUE);
    }

    @Override
    public void onNext(Message message) {
        if (message instanceof InputMessage && ((InputMessage) message).getException()!=null) {
            onError(((InputMessage) message).getException());
        }
        else if (message instanceof TopicMessage) {
            if (onMessageListener!=null) {
                onMessageListener.onMessage(((TopicMessage) message).getTopic(), ((TopicMessage) message).getContent());
            }
        }
    }

    @Override
    public void onError(Throwable t) {
        logger.log(Level.SEVERE, "received error", t);
        if (onMessageListener!=null) {
            onMessageListener.onError(t);
        }
    }

    @Override
    public void onComplete() {

    }
}
