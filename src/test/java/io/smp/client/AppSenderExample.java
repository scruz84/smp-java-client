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

public class AppSenderExample {

    public static void main(String args[]) throws Exception {

        Client client = new ClientBuilder()
            .setHost("localhost")
            .setPort(1985)
            .setUser("sergio")
            .setPassword("secret")
            .setTls(true)
            .setServerCertificate("./config/tls/server.pem")
            .build();

        client.subscribeTopic("my-topic");

        client.sendMessage("my-topic", "first message");
        client.sendMessage("my-topic", "second message");

        //Do some stuffs...
    }
}
