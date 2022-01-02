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

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class FragmentEncoder extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        final ByteBuf m = (ByteBuf) msg;
        final ByteBuf outputMsg;
        if (m.readableBytes() > HandlerUtil.packet_size-HandlerUtil.initial_packet_length_info) {
            outputMsg = ctx.alloc().buffer(HandlerUtil.initial_packet_length_info + m.readableBytes());
        } else {
            outputMsg = ctx.alloc().buffer(HandlerUtil.packet_size);
        }
        try {
            outputMsg.writeBytes(HandlerUtil.intToBytes(m.readableBytes()+HandlerUtil.initial_packet_length_info));
            m.getBytes(0, outputMsg);
        }
        finally {
            m.release();
        }
        super.write(ctx, outputMsg, promise);
    }

}
