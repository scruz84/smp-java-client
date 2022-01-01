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

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class FragmentDecoder extends ChannelInboundHandlerAdapter {

    private ByteBuf internalBuffer;

    private int currentMessageSize = -1;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        internalBuffer = ctx.alloc().buffer(HandlerUtil.packet_size);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        final ByteBuf m = (ByteBuf) msg;
        try {
            boolean firstPackage = false;
            if (currentMessageSize < 0) {
                firstPackage = true;
                //starts a new message
                currentMessageSize = HandlerUtil.bytesToInt(m);
            }
            int maxSlice = Math.min(currentMessageSize-internalBuffer.readableBytes(), m.readableBytes());
            internalBuffer.writeBytes(m.slice(0, maxSlice));
            m.readerIndex((firstPackage? HandlerUtil.packet_size : maxSlice));
            if (internalBuffer.readableBytes() == currentMessageSize) {
                currentMessageSize = -1;
                super.channelRead(ctx, internalBuffer.slice(HandlerUtil.initial_packet_length_info,
                    internalBuffer.readableBytes() - HandlerUtil.initial_packet_length_info));
            }

        }
        finally {
            if (currentMessageSize == -1) {
                internalBuffer.resetReaderIndex();
                internalBuffer.resetWriterIndex();
                internalBuffer.clear();
            }
            m.release();
        }
    }
}
