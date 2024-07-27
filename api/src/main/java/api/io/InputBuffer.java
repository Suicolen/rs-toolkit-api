package api.io;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import one.util.streamex.IntStreamEx;

public class InputBuffer {

    private final ByteBuf buffer;

    public InputBuffer(byte[] data) {
        buffer = Unpooled.wrappedBuffer(data);
    }

    public InputBuffer(ByteBuf buffer) {
        this.buffer = buffer;
    }

    public byte g1s() {
        return buffer.readByte();
    }

    public int g1() {
        return buffer.readUnsignedByte();
    }

    public boolean gBoolean() {
        return buffer.readBoolean();
    }

    public int g2s() {
        return buffer.readShort();
    }

    public int g2() {
        return buffer.readUnsignedShort();
    }

    public int g4() {
        return buffer.readInt();
    }

    public long g8() {
        return buffer.readLong();
    }

    public int gSmart1or2s() {
        int value = peek() & 0xFF;
        return value < 128 ? buffer.readUnsignedByte() - 64 : buffer.readUnsignedShort() - 49152;
    }

    public int gSmart1or2() {
        int value = peek() & 0xFF;
        return value < 128 ? buffer.readUnsignedByte() : buffer.readUnsignedShort() - 32768;
    }

    public int gSmart1or2Minus1() {
        int peek = peek() & 0xFF;
        return peek < 128 ? g1() - 1 : g2() - 32769;
    }


    public int gSmart2or4s() {
        if (peek() < 0) {
            return g4() & Integer.MAX_VALUE; // and off sign bit
        }
        int value = g2();
        return value == 32767 ? -1 : value;
    }

    public int peek() {
        return buffer.getByte(buffer.readerIndex());
    }

    public int getPosition() {
        return buffer.readerIndex();
    }

    public void setPosition(int position) {
        buffer.readerIndex(position);
    }

    public void decrementPosition(int amount) {
        buffer.readerIndex(buffer.readerIndex() - amount);
    }

    public void skip(int amount) {
        buffer.readerIndex(buffer.readerIndex() + amount);
    }

    public byte[] getArray() {
        if (buffer.hasArray()) {
            return buffer.array();
        }
        byte[] array = new byte[buffer.readableBytes()];
        buffer.getBytes(0, array);
        return array;
    }

    public String get10DelimitedString() {
        return IntStreamEx.generate(this::g1s).takeWhile(b -> b != 10).charsToString();
    }

    public String getStringOSRS() {
        return IntStreamEx.generate(this::g1)
                .takeWhile(b -> b != 0)
                .map(b -> {
                    if (b >= 128 && b < 160) {
                        char c = CHARACTERS[b - 128];
                        return c == 0 ? '?' : c;
                    }
                    return b;
                }).charsToString();
    }

    public byte[] get10DelimitedStringBytes() {
        return IntStreamEx.generate(this::g1).takeWhile(b -> b != 10).toByteArray();
    }

    public int g3() {
        return buffer.readUnsignedMedium();
    }

    public float gFloat() {
        return Float.intBitsToFloat(this.g4());
    }

    // TODO write method & test
    public int gSmarts() {
        int current = 0;
        int read;
        for (read = this.gSmart1or2(); read == 32767; read = this.gSmart1or2()) {
            current += 32767;
        }

        current += read;
        return current;
    }

    public byte[] getBytes(int length) {
        byte[] bytes = new byte[length];
        System.arraycopy(getArray(), getPosition(), bytes, 0, length);
        skip(length);
        return bytes;
    }

    public InputBuffer duplicate() {
        return new InputBuffer(buffer.duplicate());
    }

    public int getLength() {
        return getArray().length;
    }

    public int getCapacity() {
        return buffer.capacity();
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
