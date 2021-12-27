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

public class AppReceiverExample {

    public static void main(String args[]) throws Exception {

        Client client = new ClientBuilder()
            .setHost("localhost")
            .setPort(1984)
            .setUser("sergio")
            .setPassword("secret")
            .setOnMessageListener(new MyMessageListener())
            .build();

        client.subscribeTopic("my-topic");
        client.unSubscribeTopic("my-topic");

        //Do some stuffs...
    }

    private static class MyMessageListener implements Client.OnMessageListener {

        @Override
        public void onMessage(String topic, byte[] message) {
            System.out.println("Received from topic " + topic + ", content "+new String(message));
        }

        @Override
        public void onError(Throwable t) {
            t.printStackTrace();
        }
    }
}
