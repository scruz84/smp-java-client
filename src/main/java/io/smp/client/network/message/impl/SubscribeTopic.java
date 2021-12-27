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
package io.smp.client.network.message.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import io.smp.client.network.message.MessageWrapper;
import io.smp.client.network.message.SerializeException;

public class SubscribeTopic extends OutputMessage {

    private final String topic;
    private final boolean subscribe;

    public SubscribeTopic(String topic, boolean subscribe) {
        this.topic = topic;
        this.subscribe = subscribe;
    }

    @Override
    public byte[] serialize() throws SerializeException {
        try(ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            os.write(subscribe?
                    MessageWrapper.TOPIC_SUBSCRIPTION_REQUEST
                    : MessageWrapper.TOPIC_UN_SUBSCRIPTION_REQUEST);
            os.write(0);
            os.write(0);
            os.write(0);
            os.write(topic.getBytes(StandardCharsets.UTF_8));
            return os.toByteArray();
        } catch (IOException e) {
            throw new SerializeException("error serializing message", e);
        }
    }
}
