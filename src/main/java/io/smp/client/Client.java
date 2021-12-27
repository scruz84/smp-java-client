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

import io.smp.client.exception.SmpException;

public interface Client {

    /**
     * Subscribe to the messages sent to a topic.
     *
     * @param topic the topic name
     * @throws SmpException in case of error
     */
    void subscribeTopic(String topic) throws SmpException;

    /**
     * Unsubscribe to the messages sent to a topic.
     *
     * @param topic the topic name
     * @throws SmpException in case of error
     */
    void unSubscribeTopic(String topic) throws SmpException;

    /**
     * Sends a message to a topic.
     *
     * @param topic the topic name
     * @param message the message to send
     * @throws SmpException in case of error
     */
    void sendMessage(String topic, byte[] message) throws SmpException;

    /**
     * Sends a message to a topic.
     *
     * @param topic the topic name
     * @param message the message to send
     * @throws SmpException in case of error
     */
    void sendMessage(String topic, String message) throws SmpException;

    /**
     * Interface for handling the received messages
     */
    interface OnMessageListener {

        /**
         * Invoked when a message is received.
         *
         * @param topic the topic name.
         * @param message the message.
         */
        void onMessage(String topic, byte[] message);

        /**
         * Invoked when an exception is received.
         *
         * @param exception the exception
         */
        void onError(Throwable exception);
    }
}
