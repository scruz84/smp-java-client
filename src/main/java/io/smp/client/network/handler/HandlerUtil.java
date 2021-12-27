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

import java.nio.ByteBuffer;

public final class HandlerUtil {

    public static final int packet_size = 32;
    public static final int initial_packet_length_info = 4;

    public static byte[] intToBytes(int data) {
        return new byte[] {
            (byte)((data >> 24) & 0xff),
            (byte)((data >> 16) & 0xff),
            (byte)((data >> 8) & 0xff),
            (byte)((data >> 0) & 0xff),
        };
    }
    
    public static int bytesToInt(ByteBuffer byteBuffer) {
        int intValue = 0;
        intValue |= getUnsignedByte(byteBuffer, 3);
        intValue |= getUnsignedByte(byteBuffer, 2) << 8;
        intValue |= getUnsignedByte(byteBuffer, 1) << 16;
        intValue |= getUnsignedByte(byteBuffer, 0) << 24;

        return intValue; //unsigned numbers
    }

    private static short getUnsignedByte(ByteBuffer byteBuffer, int pos) {
        return ((short) (byteBuffer.get(pos) & (short) 0xff));
    }

    public static byte[] concatByteArrays(byte[] array1, byte[] array2) {
        final ByteBuffer concat = ByteBuffer.allocate(array1.length + array2.length);
        concat.put(array1)
            .put(array2);
        return concat.array();
    }

}
