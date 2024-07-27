package api.packager;

import api.*;
import api.cache.Cache;
import api.definition.Definition;
import api.io.InputBuffer;
import com.displee.cache.index.Index;
import com.displee.cache.index.archive.Archive;
import com.displee.cache.index.archive.file.File;
import one.util.streamex.EntryStream;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class IndexedPackager<T extends Definition> implements Unpacker<InputBuffer, T>, Packer<T> {

    private final Cache cache;
    private final Index index;

    public IndexedPackager(Cache cache, int index) {
        this.cache = cache;
        this.index = cache.getIndex(index);
    }

    @Override
    public Map<Integer, T> unpack(Codec<InputBuffer, T> decoder, Factory<T> factory) {
        int count = this.index.archiveIds().length;
        Map<Integer, T> definitions = new HashMap<>(count);
        for (int i = 0; i < count; i++) {
            Archive archive = index.archive(i);
            if (archive == null) {
                continue;
            }
            File file = archive.file(0);
            if (file == null || file.getData() == null || file.getData().length == 0) {
                continue;
            }
            byte[] data = file.getData();
            T definition = factory.construct();
            definition.setId(file.getId());
            decoder.decode(new InputBuffer(data), definition);
            definitions.put(definition.getId(), definition);
        }

        return definitions;
    }

    @Override
    public void pack(Codec<?, T> encoder, Map<Integer, T> src) {
        EntryStream.of(src).filterKeys(Objects::nonNull).forKeyValue((id, def) -> {
            Archive archive = index.add(id, -1, null, false);
            byte[] encodedData = encoder.encode(def);
            archive.add(0, encodedData);
        });
        cache.update();
    }

    @Override
    public T unpackSingle(int id, Codec<InputBuffer, T> decoder, Factory<T> factory) {
        Archive archive = index.archive(id);
        if (archive == null) {
            return null;
        }
        File file = archive.file(0);
        if (file == null || file.getData() == null || file.getData().length == 0) {
            return null;
        }
        byte[] data = file.getData();
        T definition = factory.construct();
        definition.setId(id);
        decoder.decode(new InputBuffer(data), definition);
        return definition;
    }

    @Override
    public void packSingle(Codec<?, T> encoder, T definition) {
        Archive archive = index.add(definition.getId(), -1, null, false);
        byte[] encodedData = encoder.encode(definition);
        archive.add(0, encodedData);
        cache.update();
    }
}
