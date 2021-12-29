/*
 * SimpleMessagePassing.
 * Copyright (C) 2021  Sergio Cruz
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package io.smp.client.network.handler;

import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.smp.client.Client;
import io.smp.client.network.message.Message;
import io.smp.client.network.message.impl.ErrorMessage;

public class DispatchMessageHandler extends ChannelInboundHandlerAdapter {

    private final SubmissionPublisher<Message> publisher = new SubmissionPublisher<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        final Message message = (Message) msg;
        publisher.submit(message);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        publisher.submit(new ErrorMessage(cause));
    }

    public void subscribe(Client nettyClient) {
        publisher.subscribe((Flow.Subscriber<? super Message>) nettyClient);
    }
}
