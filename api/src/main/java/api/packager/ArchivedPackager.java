package api.packager;

import api.*;
import api.cache.Cache;
import api.definition.Definition;
import api.io.InputBuffer;
import api.util.ConfigTypeOSRS;
import api.util.IndexTypeOSRS;
import com.displee.cache.index.Index;
import com.displee.cache.index.archive.Archive;
import com.displee.cache.index.archive.file.File;
import one.util.streamex.EntryStream;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ArchivedPackager<T extends Definition> implements Unpacker<InputBuffer, T>, Packer<T> {

    private final Cache cache;
    private final Index index;
    private final Archive archive;

    public ArchivedPackager(Cache cache, int index, int archive) {
        this.cache = cache;
        this.index = cache.getIndex(index);
        this.archive = cache.getArchive(index, archive);
    }

    @Override
    public Map<Integer, T> unpack(Codec<InputBuffer, T> decoder, Factory<T> factory) {
        int count = this.archive.fileIds().length;
        Map<Integer, T> definitions = new HashMap<>(count);
        for (File file : this.archive.getFiles().values()) {
            if (file == null) {
                continue;
            }

            byte[] data = file.getData();

            if (data == null || data.length == 0) {
                continue;
            }

            // put this elsewhere later
            if (cache.isOSRS() && index.getId() == IndexTypeOSRS.CONFIGS.getId() && archive.getId() == ConfigTypeOSRS.ENUM.getId() && data.length == 1 && data[0] == 0) {
                continue;
            }

            T definition = factory.construct();
            definition.setId(file.getId());
            try {
                decoder.decode(new InputBuffer(data), definition);
                definitions.put(definition.getId(), definition);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println("Failed to decode " + file.getId() + " | " + index.getId() + ", " + archive.getId());
            }
        }

        return definitions;
    }

    @Override
    public void pack(Codec<?, T> encoder, Map<Integer, T> src) {
        EntryStream.of(src).filterKeys(Objects::nonNull).forKeyValue((id, def) -> {
            byte[] data = encoder.encode(def);
            archive.add(id, data);
        });
        cache.update();
    }

    @Override
    public T unpackSingle(int id, Codec<InputBuffer, T> decoder, Factory<T> factory) {
        File file = archive.file(id);
        if (file == null) {
            return null;
        }

        byte[] data = file.getData();

        if (data == null || data.length == 0) {
            return null;
        }

        T definition = factory.construct();
        definition.setId(file.getId());
        decoder.decode(new InputBuffer(data), definition);
        return definition;
    }

    @Override
    public void packSingle(Codec<?, T> encoder, T definition) {
        byte[] data = encoder.encode(definition);
        archive.add(definition.getId(), data);
        cache.update();
    }
}
