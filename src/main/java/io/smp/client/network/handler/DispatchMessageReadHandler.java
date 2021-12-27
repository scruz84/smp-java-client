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
package io.smp.client.network.handler;

import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

import io.smp.client.network.Channel;
import io.smp.client.network.message.Message;
import io.smp.client.network.message.MessageWrapper;

public class DispatchMessageReadHandler extends SubmissionPublisher<Message> implements ChannelReadHandler, Flow.Processor<byte[], Message>  {

    @Override
    public void read(byte[] message, Channel channel) {
        onNext(message);
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        subscription.request(1);
    }

    @Override
    public void onNext(byte[] message) {
        submit(MessageWrapper.buildMessage(message));
    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onComplete() {
        close();
    }
}
