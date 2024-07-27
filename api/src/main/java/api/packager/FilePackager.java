package api.packager;

import api.*;
import api.cache.Cache;
import api.definition.Definition;
import api.io.InputBuffer;
import api.io.OutputBuffer;
import com.displee.cache.index.archive.Archive;
import com.displee.cache.index.archive.file.File;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FilePackager<T extends Definition> implements Unpacker<InputBuffer, T>, Packer<T> {

    private final Cache cache;
    private final Archive archive;
    private final String fileName;

    public FilePackager(Cache cache, int index, int archive, String fileName) {
        this.cache = cache;
        this.archive = cache.getArchive(index, archive);
        this.fileName = fileName;
    }

    @Override
    public Map<Integer, T> unpack(Codec<InputBuffer, T> decoder, Factory<T> factory) {
        File dataFile = archive.file(fileName);
        InputBuffer dataBuffer = new InputBuffer(Objects.requireNonNull(dataFile, "Data file cannot be null")
                .getData());

        int count = dataBuffer.g2();
        Map<Integer, T> definitions = new HashMap<>(count);

        for (int i = 0; i < count; i++) {
            T definition = factory.construct();
            definition.setId(i);
            decoder.decode(dataBuffer, definition);
            definitions.put(i, definition);
        }

        return definitions;
    }

    @Override
    public void pack(Codec<?, T> encoder, Map<Integer, T> src) {
        OutputBuffer buffer = new OutputBuffer();
        buffer.p2(src.size());
        src.forEach((_, def) -> {
            byte[] encodedData = encoder.encode(def);
            buffer.putBytes(encodedData);
        });
        archive.add(fileName, buffer.toArray());
        cache.update();
    }

    // this is actually possible to implement, but it doesn't make much sense
    @Override
    public T unpackSingle(int id, Codec<InputBuffer, T> decoder, Factory<T> factory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void packSingle(Codec<?, T> encoder, T definition) {
        throw new UnsupportedOperationException();
    }
}
