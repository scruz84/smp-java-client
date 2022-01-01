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
package io.smp.client.network.message;

import java.util.Arrays;

import io.netty.buffer.ByteBuf;
import io.smp.client.network.handler.HandlerUtil;
import io.smp.client.network.message.impl.LoginMessageResponse;
import io.smp.client.network.message.impl.TopicMessage;

public final class MessageWrapper {

    public static final byte LOGIN_REQUEST = 'a';
    public static final byte LOGIN_RESPONSE = 'b';
    public static final byte TOPIC_SUBSCRIPTION_REQUEST = 'c';
    public static final byte SEND_MESSAGE = 'd';
    public static final byte TOPIC_MESSAGE = 'e';
    public static final byte TOPIC_UN_SUBSCRIPTION_REQUEST = 'f';

    /**
     * contents:    X _ _ _ ZZZZ
     *  X: message type
     *  _: reserved
     *  _: reserved
     *  _: reserved
     *  Z: message contents
     */
    public static Message buildMessage(byte[] contents) {
        final byte messageType = contents[0];
        final byte[] messageContents = Arrays.copyOfRange(contents, HandlerUtil.initial_packet_length_info, contents.length);
        Message message = null;

        switch (messageType) {
            //login
            case LOGIN_RESPONSE:
                message = new LoginMessageResponse(messageContents);
                break;
            //message received
            case TOPIC_MESSAGE:
                message = new TopicMessage();
                message.deSerialize(messageContents);
                break;
        }
        return message;
    }

    public static Message buildMessage(ByteBuf contents) {
        final byte messageType = contents.getByte(0);
        final byte[] messageContents = new byte[contents.readableBytes()-HandlerUtil.initial_packet_length_info];
        contents.getBytes(HandlerUtil.initial_packet_length_info, messageContents);
        Message message = null;
        switch (messageType) {
            //login
            case LOGIN_RESPONSE:
                message = new LoginMessageResponse(messageContents);
                break;
            //message received
            case TOPIC_MESSAGE:
                message = new TopicMessage();
                message.deSerialize(messageContents);
                break;
        }
        return message;
    }
}
