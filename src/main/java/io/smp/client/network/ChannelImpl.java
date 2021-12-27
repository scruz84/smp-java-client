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

package io.smp.client.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import io.smp.client.network.handler.ChannelReadException;
import io.smp.client.network.handler.ChannelReadHandler;
import io.smp.client.network.handler.ChannelWriteException;
import io.smp.client.network.handler.ChannelWriteHandler;
import io.smp.client.network.handler.HandlerUtil;
import io.smp.client.network.message.Message;

public class ChannelImpl implements Channel {

    private static final Logger logger = Logger.getLogger(ChannelImpl.class.getCanonicalName());

    private final SocketChannel socketChannel;
    private final ChannelWriteHandler writerHandlers;
    private final ChannelReadHandler readersHandlers;
    private final Lock socketWriteLock = new ReentrantLock();
    private final Lock sockerReadLock = new ReentrantLock();

    public ChannelImpl(SocketChannel socketChannel, ChannelWriteHandler writerHandlers,
        ChannelReadHandler readersHandlers) {
        this.socketChannel = socketChannel;
        this.writerHandlers = writerHandlers;
        this.readersHandlers = readersHandlers;
    }

    @Override
    public void dispatchWrite(Message message) throws ChannelWriteException {
        try {
            writerHandlers.write(message.serialize(), this);
        } catch (Exception e) {
            throw new ChannelWriteException("Error writing message: " + e.getMessage(), e);
        }
    }

    @Override
    public void write(ByteBuffer... byteBuffers) throws ChannelWriteException {
        socketWriteLock.lock();
        try {
            if (byteBuffers[0].limit()<HandlerUtil.packet_size) {
                logger.severe("##Unkown sent package: " + Arrays.toString(byteBuffers[0].array()));
            }
            for (ByteBuffer bf : byteBuffers) {
                long writed = 0;
                while(writed < bf.capacity()) {
                    writed += socketChannel.write(bf);
                }
            }
        } catch (IOException e) {
            throw new ChannelWriteException(e.getMessage(), e);
        }
        finally {
            socketWriteLock.unlock();
        }
    }

    @Override
    public boolean dispatchRead() throws ChannelReadException {
        final int messageSize;
        sockerReadLock.lock();
        try {
            final byte[] message;
            final ByteBuffer initialByteBuffer = ByteBuffer.allocate(HandlerUtil.packet_size);

            int nBytes = 0;
            while(nBytes<HandlerUtil.packet_size) {
                nBytes += socketChannel.read(initialByteBuffer);
            }

            messageSize = HandlerUtil.bytesToInt(initialByteBuffer);
            if (messageSize>0) {
                if (messageSize > HandlerUtil.packet_size) {
                    final ByteBuffer byteBuffer = ByteBuffer.allocate(messageSize - HandlerUtil.packet_size);
                    nBytes=0;
                    while(nBytes<byteBuffer.capacity()) {
                        nBytes += socketChannel.read(byteBuffer);
                    }
                    try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                        os.write(Arrays.copyOfRange(initialByteBuffer.array(), HandlerUtil.initial_packet_length_info,
                            initialByteBuffer.limit()));
                        os.write(byteBuffer.array());
                        message = os.toByteArray();
                    }
                } else {
                    message = Arrays.copyOfRange(initialByteBuffer.array(), HandlerUtil.initial_packet_length_info,
                        messageSize);
                }

                readersHandlers.read(message, this);
            }
        } catch (Exception e) {
            throw new ChannelReadException(e.getMessage(), e);
        } finally {
            sockerReadLock.unlock();
        }
        return messageSize>0;
    }


}
