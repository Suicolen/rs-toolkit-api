package api.packager;

import api.Codec;
import api.Factory;
import api.Packer;
import api.Unpacker;
import api.definition.Definition;
import api.io.InputBuffer;
import api.io.OutputBuffer;
import api.util.FileUtils;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class RawIndexedFilePackager<T extends Definition> implements Unpacker<InputBuffer, T>, Packer<T> {

    private final Path dataFilePath;
    private final Path indexFilePath;
    private final int posOffset;

    public RawIndexedFilePackager(String dataFilePath, String indexFilePath, int posOffset) {
        this.dataFilePath = Path.of(dataFilePath);
        this.indexFilePath = Path.of(indexFilePath);
        this.posOffset = posOffset;
    }

    public RawIndexedFilePackager(String dataFilePath, String indexFilePath) {
        this(dataFilePath, indexFilePath, 0);
    }

    @Override
    public Map<Integer, T> unpack(Codec<InputBuffer, T> decoder, Factory<T> factory) {
        InputBuffer dataBuffer = new InputBuffer(FileUtils.readAllBytes(dataFilePath));
        InputBuffer indexBuffer = new InputBuffer(FileUtils.readAllBytes(indexFilePath));
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
        FileUtils.write(dataFilePath, data);
        FileUtils.write(indexFilePath, indices);
    }


    // same as IndexedFilePackager, it's possible to implement but doesn't make much sense
    @Override
    public T unpackSingle(int id, Codec<InputBuffer, T> decoder, Factory<T> factory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void packSingle(Codec<?, T> encoder, T definition) {
        throw new UnsupportedOperationException();
    }
}
