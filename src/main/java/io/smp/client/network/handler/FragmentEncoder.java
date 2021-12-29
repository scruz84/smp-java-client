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
        if (m.readableBytes() > HandlerUtil.packet_size-4) {
            outputMsg = ctx.alloc().buffer(4 + m.readableBytes());
        } else {
            outputMsg = ctx.alloc().buffer(HandlerUtil.packet_size);
        }
        try {
            outputMsg.writeBytes(HandlerUtil.intToBytes(m.readableBytes()+4));
            m.getBytes(0, outputMsg);
            //TODO: mirar si el mensaje es menor de packet_size, el resto está con ceros y además cuentan!!
        }
        finally {
            m.release();
        }
        super.write(ctx, outputMsg, promise);
    }

    /*
    @Override
    protected byte[] doWrite(byte[] message, Channel channel) throws Exception {
        final ByteBuffer[] byteBuffers;
        final ByteBuffer byteBuffer = ByteBuffer.allocate(HandlerUtil.packet_size);
        //writes first block
        byteBuffer.put(HandlerUtil.intToBytes(message.length+4));
        byteBuffer.position(4);
        byteBuffer.put(message, 0, Math.min(message.length, HandlerUtil.packet_size - 4));
        byteBuffer.position(0);

        if (message.length > HandlerUtil.packet_size-4) {
            byteBuffers = new ByteBuffer[2];
            byteBuffers[0] = byteBuffer;
            final ByteBuffer newByteBuffer = ByteBuffer.allocate(message.length+4-HandlerUtil.packet_size);
            byteBuffers[1] = newByteBuffer;
            newByteBuffer.put(message, HandlerUtil.packet_size-4, newByteBuffer.capacity());
            newByteBuffer.position(0);
        } else {
            byteBuffers = new ByteBuffer[]{byteBuffer};
        }

        channel.write(byteBuffers);

        return message;
    }
    */

}
