package api.io;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import one.util.streamex.IntStreamEx;

@Getter
@SuppressWarnings("UnusedReturnValue")
public class OutputBuffer {
    private final ByteBuf buffer;

    public OutputBuffer() {
        buffer = Unpooled.buffer();
    }

    public OutputBuffer(int initialCapacity) {
        buffer = Unpooled.buffer(initialCapacity);
    }

    public OutputBuffer(int initialCapacity, int maxCapacity) {
        buffer = Unpooled.buffer(initialCapacity, maxCapacity);
    }

    public OutputBuffer p1(int value) {
        buffer.writeByte(value);
        return this;
    }

    public OutputBuffer p2(int value) {
        buffer.writeShort(value);
        return this;
    }

    public OutputBuffer p4(int value) {
        buffer.writeInt(value);
        return this;
    }

    public OutputBuffer p8(long value) {
        buffer.writeLong(value);
        return this;
    }

    public OutputBuffer pFloat(float value) {
        buffer.writeFloat(value);
        return this;
    }

    public OutputBuffer pSmart1or2s(int value) {
        if (value < 64 && value >= -64) {
            buffer.writeByte(value + 64);
            return this;
        }

        if (value < 16384 && value >= -16384) {
            buffer.writeShort(value + 49152);
        } else {
            throw new IllegalArgumentException("Value out of range: " + value);
        }
        return this;
    }

    public OutputBuffer pSmart1or2(int value) {
        if (value >= 128) {
            buffer.writeShort(value + 32768);
        } else {
            buffer.writeByte(value);
        }
        return this;
    }

    public OutputBuffer pSmart2or4s(int value) {
        if (value == -1) {
            p2(32767);
        } else if (value >= 32767) {
            p4((1 << 31) | value);
        } else {
            p2(value);
        }
        return this;
    }

    public OutputBuffer pSmart1or2Minus1(int value) {
        if (value >= 128) {
            buffer.writeShort(value + 32769);
        } else {
            buffer.writeByte(value + 1);
        }
        return this;
    }

    public OutputBuffer put10DelimitedString(String str) {
        IntStreamEx.ofChars(str).append(10).forEach(buffer::writeByte);
        return this;
    }

    public OutputBuffer putStringOSRS(String str) {
        IntStreamEx.ofChars(str).map(c -> {
            int index = (int) IntStreamEx.of(CHARACTERS).indexOf(c).orElse(-1);
            if (index != -1) {
                return index + 128;
            }
            return c;
        }).append(0).forEach(buffer::writeByte);
        return this;
    }

    public OutputBuffer pBoolean(boolean value) {
        buffer.writeBoolean(value);
        return this;
    }

    public OutputBuffer p3(int value) {
        buffer.writeMedium(value);
        return this;
    }

    public int getPosition() {
        return buffer.writerIndex();
    }

    public OutputBuffer decrementPosition(int amount) {
        buffer.writerIndex(buffer.writerIndex() - amount);
        return this;
    }

    public OutputBuffer skip(int amount) {
        buffer.writerIndex(buffer.writerIndex() + amount);
        return this;
    }

    public OutputBuffer putBytes(OutputBuffer other) {
        buffer.writeBytes(other.getBuffer());
        return this;
    }

    public OutputBuffer putBytes(byte[] data) {
        buffer.writeBytes(data);
        return this;
    }

    public OutputBuffer getBytes(byte[] dst) {
        buffer.readBytes(dst);
        return this;
    }

    public byte[] toArray() {
        byte[] array = new byte[buffer.readableBytes()];
        buffer.readBytes(array);
        buffer.readerIndex(0);
        return array;
    }


    public OutputBuffer set1(int index, int value) {
        buffer.setByte(index, value);
        return this;
    }

    public OutputBuffer set2(int index, int value) {
        buffer.setShort(index, value);
        return this;
    }

    private static final char[] CHARACTERS = new char[]{
            '€', '\u0000', '‚', 'ƒ', '„', '…',
            '†', '‡', 'ˆ', '‰', 'Š', '‹',
            'Œ', '\u0000', 'Ž', '\u0000', '\u0000', '‘',
            '’', '“', '”', '•', '–', '—',
            '˜', '™', 'š', '›', 'œ', '\u0000',
            'ž', 'Ÿ'
    };

}
