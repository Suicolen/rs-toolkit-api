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

public class IndexedFilePackager<T extends Definition> implements Unpacker<InputBuffer, T>, Packer<T> {

    private final Cache cache;

    private final Archive archive;
    private final String dataFileName;
    private final String indexFileName;

    private final int posOffset;

    public IndexedFilePackager(Cache cache, int index, int archive, String dataFileName, String indexFileName, int posOffset) {
        this.cache = cache;
        this.archive = cache.getArchive(index, archive);
        this.dataFileName = dataFileName;
        this.indexFileName = indexFileName;
        this.posOffset = posOffset;
    }

    public IndexedFilePackager(Cache cache, int index, int archive, String dataFileName, String indexFileName) {
        this(cache, index, archive, dataFileName, indexFileName, 0);
    }

    @Override
    public Map<Integer, T> unpack(Codec<InputBuffer, T> decoder, Factory<T> factory) {
        File dataFile = Objects.requireNonNull(archive.file(dataFileName));
        File indexFile = Objects.requireNonNull(archive.file(indexFileName));
        InputBuffer dataBuffer = new InputBuffer(dataFile.getData());
        InputBuffer indexBuffer = new InputBuffer(indexFile.getData());
        int pos = posOffset;
        int totalDefinitions = indexBuffer.g2();
        Map<Integer, T> definitions = new HashMap<>(totalDefinitions);
        int[] offsets = new int[totalDefinitions];
        for (int id = 0; id < totalDefinitions; id++) {
            offsets[id] = pos;
            pos += indexBuffer.g2();
        }

        for (int id = 0; id < totalDefinitions; id++) {
            T definition = factory.construct();
            definition.setId(id);
            int offset = offsets[id];
            dataBuffer.setPosition(offset);
            decoder.decode(dataBuffer, definition);
            definitions.put(id, definition);
        }

        return definitions;
    }


    @Override
    public void pack(Codec<?, T> encoder, Map<Integer, T> src) {
        OutputBuffer dataBuffer = new OutputBuffer();
        OutputBuffer indexBuffer = new OutputBuffer();
        indexBuffer.p2(src.size());
        int previousPos = posOffset;
        for (var entry : src.entrySet()) {
            T value = entry.getValue();
            byte[] encodedData = encoder.encode(value);
            dataBuffer.putBytes(encodedData);
            indexBuffer.p2(dataBuffer.getPosition() - previousPos);
            previousPos = dataBuffer.getPosition();
        }
        byte[] data = dataBuffer.toArray();
        byte[] indices = indexBuffer.toArray();
        archive.add(dataFileName, data);
        archive.add(indexFileName, indices);
        cache.update();
    }

    // this is actually possible to implement, but it doesn't make much sense
    @Override
    public T unpackSingle(int id, Codec<InputBuffer, T> decoder, Factory<T> factory) {
        throw new UnsupportedOperationException();
    }

    // same as above
    @Override
    public void packSingle(Codec<?, T> encoder, T definition) {
        throw new UnsupportedOperationException();
    }

}
