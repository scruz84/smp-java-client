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
package io.smp.client.network.selection;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.smp.client.network.Channel;
import io.smp.client.network.handler.ChannelReadException;

public final class ChannelSelector {

    private static final Logger logger = Logger.getLogger(ChannelSelector.class.getCanonicalName());

    private static long counter = 0;
    private static final ExecutorService channelSelectorExecutor = Executors.newFixedThreadPool(2, r -> {
        final Thread th = new Thread(r, "smp ("+(counter++)+')');
        th.setDaemon(true);
        return th;
    });
    private static final AtomicBoolean started = new AtomicBoolean(false);
    private static final Selector selector;

    static {
        Selector s = null;
        try {
            s = Selector.open();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "* ERROR creating selector", e);
        }
        selector = s;
    }

    public static void registerSelector(Channel channel, SocketChannel socketChannel)
        throws IOException {
        final SelectionKey selectionKey = socketChannel.register(selector, SelectionKey.OP_READ);
        selectionKey.attach(channel);
        if (started.compareAndSet(false, true)) {
            channelSelectorExecutor.submit(new SelectionTask());
        }
    }

    private static class SelectionTask implements Runnable {

        @Override
        public void run() {
            while(true) {
                try {
                    if (selector.select()>0) {
                        final Iterator<SelectionKey> selectionKeyIterator = selector.selectedKeys().iterator();
                        while(selectionKeyIterator.hasNext()) {
                            final SelectionKey selectionKey = selectionKeyIterator.next();
                            selectionKeyIterator.remove();
                            //READ
                            if (selectionKey.isReadable()) {
                                doRead(selectionKey);
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "error processing selector: "+ e.getMessage(), e);
                }
            }
        }

        private static void doRead(SelectionKey selectionKey) throws ChannelReadException {
            final SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            final Channel channel = (Channel) selectionKey.attachment();
            if (channel!=null) {
                channel.dispatchRead();
            }
            else {
                logger.log(Level.WARNING, "not found channel for " + socketChannel);
            }
        }
    }
}