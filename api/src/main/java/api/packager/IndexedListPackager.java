package api.packager;

import api.Codec;
import api.Factory;
import api.Packer;
import api.Unpacker;
import api.cache.Cache;
import api.definition.Definition;
import api.io.InputBuffer;
import com.displee.cache.index.Index;
import com.displee.cache.index.archive.Archive;
import com.displee.cache.index.archive.file.File;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// perhaps there could a better name for this
// this is similar to an IndexedPackager except it outputs elements of type List<T> instead of T
public class IndexedListPackager<T extends Definition> implements Unpacker<InputBuffer, List<T>>, Packer<List<T>> {

    private final Cache cache;
    private final Index index;

    public IndexedListPackager(Cache cache, int index) {
        this.cache = cache;
        this.index = cache.getIndex(index);
    }

    @Override
    public Map<Integer, List<T>> unpack(Codec<InputBuffer, List<T>> decoder, Factory<List<T>> factory) {
        int groupCount = index.archiveIds()[index.archiveIds().length - 1] + 1;
        Map<Integer, List<T>> definitions = new HashMap<>();
        for (int i = 0; i < groupCount; i++) {
            Archive archive = index.archive(i);
            if (archive == null) {
                continue;
            }
            File file = archive.file(0);
            if (file == null) {
                continue;
            }
            byte[] data = file.getData();
            List<T> list = factory.construct();
            int id = archive.getId();
            InputBuffer buffer = new InputBuffer(data);
            try {
                decoder.decode(buffer, list);
                definitions.put(id, list);
            } catch (Exception _) {
                System.out.println("Failed to load " + index.getId() + ", " + archive.getId() + " | Data length: " + data.length);
            }
            list.forEach(definition -> {
                definition.setId(id);
            });
        }
        return definitions;
    }

    @Override
    public void pack(Codec<?, List<T>> encoder, Map<Integer, List<T>> src) {
        src.forEach((groupId, definitions) -> {
            byte[] encodedData = encoder.encode(definitions);
            index.add(groupId).add(0, encodedData);
        });
        cache.update();
    }

    @Override
    public List<T> unpackSingle(int id, Codec<InputBuffer, List<T>> decoder, Factory<List<T>> factory) {
        Archive archive = index.archive(id);
        if (archive == null) {
            return null;
        }
        File file = archive.file(0);
        if (file == null) {
            return null;
        }
        byte[] data = file.getData();
        List<T> list = factory.construct();
        list.forEach(definition -> {
            definition.setId(id);
        });
        InputBuffer buffer = new InputBuffer(data);
        decoder.decode(buffer, list);
        return list;
    }

    @Override
    public void packSingle(Codec<?, List<T>> encoder, List<T> list) {
        byte[] encodedData = encoder.encode(list);
        int id = list.getFirst().getId();
        index.add(id).add(0, encodedData);
        cache.update();
    }
}
