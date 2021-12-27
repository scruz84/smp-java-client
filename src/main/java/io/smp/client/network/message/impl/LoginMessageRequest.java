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
package io.smp.client.network.message.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import io.smp.client.exception.SmpException;
import io.smp.client.network.handler.HandlerUtil;
import io.smp.client.network.message.MessageWrapper;

public class LoginMessageRequest extends OutputMessage {

    private final byte[] contents;

    public LoginMessageRequest(String user, String password) throws SmpException {
        try(ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            bos.write(0); //User/Password authentication
            final byte[] userBytes = user.getBytes(StandardCharsets.UTF_8);
            bos.write(userBytes.length);
            bos.write(userBytes);
            bos.write(password.getBytes(StandardCharsets.UTF_8));
            bos.flush();
            contents = HandlerUtil.concatByteArrays(
                new byte[]{ MessageWrapper.LOGIN_REQUEST, '0', '0', '0'},
                bos.toByteArray());
        } catch (IOException e) {
            throw new SmpException("Error building login request", e);
        }
/*
        final StringBuilder request = new StringBuilder("{\"user\":\"");
        request.append(user.replaceAll("\"", "\\\""));
        request.append('"');
        request.append(", \"password\":\"");
        request.append(password.replaceAll("\"", "\\\""));
        request.append("\"}");
        this.contents = HandlerUtil.concatByteArrays(
            new byte[]{ MessageWrapper.LOGIN_REQUEST, '0', '0', '0'},
            request.toString().getBytes(StandardCharsets.UTF_8));*/
    }

    @Override
    public byte[] serialize() {
        return contents;
    }
}
