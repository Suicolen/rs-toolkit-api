package api.util;

import api.definition.model.ModelType;
import api.definition.model.RSModel;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;

@UtilityClass
public class ModelUtil {
    public ModelType determineModelType(byte[] data) {
        if (data[data.length - 1] == -4 && data[data.length - 2] == -1) {
            return ModelType.CUSTOM_TYPE_3;
        } else if (data[data.length - 1] == -1 && data[data.length - 2] == -1 && data[data.length - 3] == -1) {
            return ModelType.CUSTOM;
        } else if (data[data.length - 1] == -3 && data[data.length - 2] == -1) {
            return ModelType.TYPE_3;
        } else if (data[data.length - 1] == -2 && data[data.length - 2] == -1) {
            return ModelType.TYPE_2;
        } else if (data[data.length - 1] == -1 && data[data.length - 2] == -1) {
            return ModelType.NEW;
        } else {
            return ModelType.OLD;
        }
    }

    // perhaps move this to somewhere else, shouldn't really be part of the api imo
    @SneakyThrows(IOException.class)
    public RSModel loadRawModel(String location) {
        byte[] data = FileUtils.readAllBytes(Path.of(location));
        if (CompressionUtils.isGZipped(new ByteArrayInputStream(data))) {
            data = CompressionUtils.degzip(ByteBuffer.wrap(data));
        }
        return RSModel.decode(data, -1).copy();
    }

    public boolean usesSkeletalAnimations(RSModel model) {
        return model.skeletalBones != null;
    }
}
