package api.definition.model;

import api.Codec;
import api.codecs.*;
import api.color.HSLPalette;
import api.definition.Definition;
import api.io.InputBuffer;
import api.math.*;
import api.math.geometry.Triangle;
import api.math.texture.TextureCoordinate;
import api.math.texture.TriangleUV;
import api.util.MathUtils;
import api.util.ModelUtil;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import one.util.streamex.IntStreamEx;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;
import java.util.stream.Collectors;

import static api.math.CircularAngle.COSINE;
import static api.math.CircularAngle.SINE;

public class RSModel implements Definition {

    // TODO move somewhere else
    public static boolean POST_DECODE = true;

    @Getter
    private byte[] modelData;

    public static RSModel decode(byte[] data, int id) {
        RSModel model = new RSModel();
        model.setId(id);
        ModelType type = ModelUtil.determineModelType(data);
        Codec<InputBuffer, RSModel> codec = getCodecForType(type);
        codec.decode(new InputBuffer(data), model);
        if (POST_DECODE) {
            model.postDecode();
        }
        model.modelData = data;
        return model;
    }

    public static RSModel decode(Codec<InputBuffer, RSModel> codec, byte[] data, int id) {
        RSModel model = new RSModel();
        model.setId(id);
        codec.decode(new InputBuffer(data), model);
        if (POST_DECODE) {
            model.postDecode();
        }
        model.modelData = data;
        return model;
    }

    private static Codec<InputBuffer, RSModel> getCodecForType(ModelType type) {
        return switch (type) {
            case OLD -> new OldModelCodec();
            case NEW, REV_634 -> new Rev634ModelCodec(); // for now
            case TYPE_2 -> new Type2ModelCodec();
            case TYPE_3 -> new Type3ModelCodec();
            case CUSTOM -> new CustomOldModelCodec();
            case REV_614_OLD -> new Rev614OldModelCodec();
            case REV_614_NEW -> new Rev614NewModelCodec();
            case CUSTOM_TYPE_3 -> new CustomType3ModelCodec();
        };
    }

    public void removeTextures() {
        texturedTriangleCount = 0;
        triangleTextureIds = null;
        textureCoordinateIndices = null;
        textureTypes = null;
        texturePCoordinate = null;
        textureMCoordinate = null;
        textureNCoordinate = null;
    }

    // temp
    public ModelType determineModelType(boolean osrs) {
        boolean useNewFormat = texturedTriangleCount > 63;
        if (skeletalBones != null || osrs) {
            boolean usesTransparencyFlag = IntStreamEx.range(triangleCount).anyMatch(this::makeTriangleTransparent);
            return useNewFormat || usesTransparencyFlag ? ModelType.TYPE_3 : ModelType.TYPE_2;
        }
        return useNewFormat ? ModelType.NEW : ModelType.OLD;
    }

    // temp
    public Codec<InputBuffer, RSModel> getCodec(boolean osrs) {
        return getCodecForType(determineModelType(osrs));
    }

    public void rotateY90() {
        for (int v = 0; v < vertexCount; v++) {
            int tmp = verticesX[v];
            verticesX[v] = verticesZ[v];
            verticesZ[v] = -tmp;
        }
    }


    public void replaceTexturedTriangleWithColor(int texturedTriangleIndex, int replacementColor) {
        Set<Integer> usedIndices = IntStreamEx.range(triangleCount)
                .filter(i -> triangleTextureIds[i] != -1 && textureCoordinateIndices[i] == texturedTriangleIndex)
                .boxed()
                .toSet();
        texturePCoordinate = ArrayUtils.remove(texturePCoordinate, texturedTriangleIndex);
        textureMCoordinate = ArrayUtils.remove(textureMCoordinate, texturedTriangleIndex);
        textureNCoordinate = ArrayUtils.remove(textureNCoordinate, texturedTriangleIndex);
        for (int i = 0; i < triangleCount; i++) {
            if (triangleTextureIds[i] < 0 || textureCoordinateIndices[i] < 0) {
                continue;
            }

            boolean isUsedIndex = usedIndices.contains(i);
            if (isUsedIndex) {
                triangleInfo[i] = 0;
                triangleColors[i] = replacementColor;
                textureCoordinateIndices[i] = -1;
            } else {
                textureCoordinateIndices[i]--;
            }
        }
    }

    public RSModel copy() {
        RSModel copy = new RSModel();
        copy.modelIds = modelIds;
        copy.vertexCount = vertexCount;
        copy.triangleCount = triangleCount;
        copy.texturedTriangleCount = texturedTriangleCount;
        copy.verticesX = Arrays.copyOf(verticesX, vertexCount);
        copy.verticesY = Arrays.copyOf(verticesY, vertexCount);
        copy.verticesZ = Arrays.copyOf(verticesZ, vertexCount);
        copy.faceIndicesA = Arrays.copyOf(faceIndicesA, triangleCount);
        copy.faceIndicesB = Arrays.copyOf(faceIndicesB, triangleCount);
        copy.faceIndicesC = Arrays.copyOf(faceIndicesC, triangleCount);
        if (vertexLabels != null) {
            copy.vertexLabels = Arrays.copyOf(vertexLabels, vertexCount);
        }
        if (triangleLabels != null) {
            copy.triangleLabels = Arrays.copyOf(triangleLabels, triangleCount);
        }
        if (triangleInfo != null) {
            copy.triangleInfo = Arrays.copyOf(triangleInfo, triangleCount);
        }
        if (trianglePriorities != null) {
            copy.trianglePriorities = Arrays.copyOf(trianglePriorities, triangleCount);
        }
        copy.triangleColors = Arrays.copyOf(triangleColors, triangleCount);
        if (colorsCopy != null) {
            copy.colorsCopy = Arrays.copyOf(colorsCopy, triangleCount);
        }
        if (triangleAlpha != null) {
            copy.triangleAlpha = Arrays.copyOf(triangleAlpha, triangleCount);
        }
        if (texturedTriangleCount > 0) {
            copy.texturePCoordinate = Arrays.copyOf(texturePCoordinate, texturedTriangleCount);
            copy.textureMCoordinate = Arrays.copyOf(textureMCoordinate, texturedTriangleCount);
            copy.textureNCoordinate = Arrays.copyOf(textureNCoordinate, texturedTriangleCount);
        }
        if (triangleTextureIds != null) {
            copy.triangleTextureIds = Arrays.copyOf(triangleTextureIds, triangleCount);
        }
        if (textureCoordinateIndices != null) {
            copy.textureCoordinateIndices = Arrays.copyOf(textureCoordinateIndices, triangleCount);
        }
        if (textureTypes != null) {
            copy.textureTypes = Arrays.copyOf(textureTypes, texturedTriangleCount);
        }
        copy.modelPriority = modelPriority;
        copy.version = version;
        copy.oldModel = oldModel;
        if (skeletalBones != null) {
            copy.skeletalBones = new int[vertexCount][];
            copy.skeletalWeights = new int[vertexCount][];
            for (int vertex = 0; vertex < vertexCount; vertex++) {
                copy.skeletalBones[vertex] = Arrays.copyOf(skeletalBones[vertex], skeletalBones[vertex].length);
                copy.skeletalWeights[vertex] = Arrays.copyOf(skeletalWeights[vertex], skeletalWeights[vertex].length);
            }
        }
        copy.modelData = Arrays.copyOf(modelData, modelData.length);

        if (faceColorC != null) {
            copy.faceColorC = Arrays.copyOf(faceColorC, faceColorC.length);
        }

        // temp
        copy.applyGroups();

        // shouldn't be here for now
        /*if (POST_DECODE) {
            copy.postDecode();
        }*/

        return copy;
    }

    public List<Vector3i> computePMNFromUVNew(int triangleIndex, TriangleUV triangleUV) {
        int faceA = faceIndicesA[triangleIndex];
        int faceB = faceIndicesB[triangleIndex];
        int faceC = faceIndicesC[triangleIndex];
        Point3D a = new Point3D(verticesX[faceA], verticesY[faceA], verticesZ[faceA]);
        Point3D b = new Point3D(verticesX[faceB], verticesY[faceB], verticesZ[faceB]);
        Point3D c = new Point3D(verticesX[faceC], verticesY[faceC], verticesZ[faceC]);

        Point2D uv0 = new Point2D(triangleUV.u1(), triangleUV.v1());
        Point2D uv1 = new Point2D(triangleUV.u2(), triangleUV.v2());
        Point2D uv2 = new Point2D(triangleUV.u3(), triangleUV.v3());

        Point3D e1 = b.subtract(a);
        Point3D e2 = c.subtract(a);

        Point2D t1 = uv1.subtract(uv0);
        Point2D t2 = uv2.subtract(uv0);

        double denom = t1.getX() * t2.getY() - t2.getX() * t1.getY();
        double detRcp = 1.0 / denom;
        Point3D f1 = linearCombination(t2.getY() * detRcp, e1, -t1.getY() * detRcp, e2);
        Point3D f2 = linearCombination(-t2.getX() * detRcp, e1, t1.getX() * detRcp, e2);
        Point3D f3 = linearCombination(uv0.getX(), f1, uv0.getY(), f2);
        Point3D p = a.subtract(f3);
        Point3D m = p.add(f1);
        Point3D n = p.add(f2);

        // TODO: try flooring and rounding and then convert that back to UV, find which one is closer to the original and pick that
        // TODO: might produce slightly better results
        return List.of(Vector3i.fromPoint3D(p), Vector3i.fromPoint3D(m), Vector3i.fromPoint3D(n));
    }

    private Point3D linearCombination(double s1, Point3D v1, double s2, Point3D v2) {
        return new Point3D(s1 * v1.getX() + s2 * v2.getX(), s1 * v1.getY() + s2 * v2.getY(), s1 * v1.getZ() + s2 * v2.getZ());
    }

    public void updateColor(int overrideHue, int overrideSaturation, int overrideLuminance, int overrideAmount) {
        triangleColors = Arrays.copyOf(colorsCopy, colorsCopy.length);
        for (int i = 0; i < triangleCount; i++) {
            int color = triangleColors[i];
            int hue = color >> 10 & 63;
            int saturation = color >> 7 & 7;
            int lightness = color & 127;
            int amount = overrideAmount & 255;
            if (overrideHue != -1) {
                hue += amount * (overrideHue - hue) >> 7;
            }

            if (overrideSaturation != -1) {
                saturation += amount * (overrideSaturation - saturation) >> 7;
            }

            if (overrideLuminance != -1) {
                lightness += amount * (overrideLuminance - lightness) >> 7;
            }

            int updatedColor = (hue << 10 | saturation << 7 | lightness) & 65535;
            triangleColors[i] = updatedColor;
        }
    }

    /**
     * @param hueShift        the amount to shift hue by
     * @param saturationShift the amount to shift saturation by
     * @param lightnessShift  the amount to shift lightness by
     * @param clampColor      whether to clamp the hue/saturation/lightness or to wrap it
     */
    public void shiftColor(int hueShift, int saturationShift, int lightnessShift, boolean clampColor) {
        for (int i = 0; i < triangleCount; i++) {
            if (isTextured(i)) {
                continue;
            }
            int hsl = triangleColors[i];
            int currentHue = HSLPalette.getHue(hsl);
            int currentSaturation = HSLPalette.getSaturation(hsl);
            int currentLightness = HSLPalette.getLightness(hsl);
            int newHue = currentHue + hueShift;
            int newSaturation = currentSaturation + saturationShift;
            int newLightness = currentLightness + lightnessShift;
            if (clampColor) {
                newHue = MathUtils.clamp(newHue, 0, 63);
                newSaturation = MathUtils.clamp(newSaturation, 0, 7);
                newLightness = MathUtils.clamp(newLightness, 0, 127);
            } else {
                newHue &= 0x3F;
                newSaturation &= 0x7;
                newLightness &= 0x7F;
            }
            int newHsl = HSLPalette.getHSL(newHue, newSaturation, newLightness);
            triangleColors[i] = newHsl;
        }
    }

    public void scale(int x, int y, int z) {
        for (int v = 0; v < vertexCount; v++) {
            verticesX[v] = (verticesX[v] * x) >> 7;
            verticesY[v] = (verticesY[v] * z) >> 7;
            verticesZ[v] = (verticesZ[v] * y) >> 7;
        }
    }

    public void upscale(double factor) {
        for (int v = 0; v < vertexCount; v++) {
            verticesX[v] = (int) Math.round(verticesX[v] * factor);
            verticesY[v] = (int) Math.round(verticesY[v] * factor);
            verticesZ[v] = (int) Math.round(verticesZ[v] * factor);
        }
    }

    public void downscale(double factor) {
        for (int v = 0; v < vertexCount; v++) {
            verticesX[v] = (int) Math.round(verticesX[v] / factor);
            verticesY[v] = (int) Math.round(verticesY[v] / factor);
            verticesZ[v] = (int) Math.round(verticesZ[v] / factor);
        }
    }

    public void recolor(int[] originalColors, int[] targetColors) {
        if (originalColors == null || targetColors == null) {
            return;
        }

        for (int i = 0; i < originalColors.length; i++) {
            int original = originalColors[i];
            int target = targetColors[i];
            for (int j = 0; j < triangleCount; j++) {
                if (triangleColors[j] == original) {
                    triangleColors[j] = target;
                }
            }
        }
    }

    public void recolor(int original, int target) {
        for (int j = 0; j < triangleCount; j++) {
            if (triangleColors[j] == original) {
                triangleColors[j] = target;
            }
        }
    }

    public void retextureNewFormat(int original, int target) {
        for (int j = 0; j < triangleCount; j++) {
            if (triangleTextureIds[j] == original) {
                triangleTextureIds[j] = target;
            }
        }
    }

    public Point3D[] getFaceAsPoint3DArray(int face) {
        int faceA = faceIndicesA[face];
        int faceB = faceIndicesB[face];
        int faceC = faceIndicesC[face];
        Point3D a = new Point3D(verticesX[faceA], verticesY[faceA], verticesZ[faceA]);
        Point3D b = new Point3D(verticesX[faceB], verticesY[faceB], verticesZ[faceB]);
        Point3D c = new Point3D(verticesX[faceC], verticesY[faceC], verticesZ[faceC]);
        return new Point3D[]{a, b, c};
    }

    public Triangle getTriangle(int face) {
        int faceA = faceIndicesA[face];
        int faceB = faceIndicesB[face];
        int faceC = faceIndicesC[face];
        Point3D a = new Point3D(verticesX[faceA], verticesY[faceA], verticesZ[faceA]);
        Point3D b = new Point3D(verticesX[faceB], verticesY[faceB], verticesZ[faceB]);
        Point3D c = new Point3D(verticesX[faceC], verticesY[faceC], verticesZ[faceC]);
        return new Triangle(a, b, c);
    }

    public Triangle getTriangle(int faceA, int faceB, int faceC) {
        Point3D a = new Point3D(verticesX[faceA], verticesY[faceA], verticesZ[faceA]);
        Point3D b = new Point3D(verticesX[faceB], verticesY[faceB], verticesZ[faceB]);
        Point3D c = new Point3D(verticesX[faceC], verticesY[faceC], verticesZ[faceC]);
        return new Triangle(a, b, c);
    }

    public Vector3i getVertex(int vertex) {
        return new Vector3i(verticesX[vertex], verticesY[vertex], verticesZ[vertex]);
    }

    public Vector3i computeLabelAverage(Set<Integer> labels) {
        int displacedVertexCount = 0;
        int originX = 0;
        int originY = 0;
        int originZ = 0;
        for (int label : labels) {
            int[] vertexGroups = groupedVertexLabels != null && label < groupedVertexLabels.length ? groupedVertexLabels[label] : null;
            if (vertexGroups == null) {
                continue;
            }
            for (int vertex : vertexGroups) {
                originX += verticesX[vertex];
                originY += verticesY[vertex];
                originZ += verticesZ[vertex];
                displacedVertexCount++;
            }
        }

        if (displacedVertexCount > 0) {
            originX = originX / displacedVertexCount;
            originY = originY / displacedVertexCount;
            originZ = originZ / displacedVertexCount;
        }

        return new Vector3i(originX, originY, originZ);
    }

    public int[] getVertexGroup(int label) {
        return groupedVertexLabels != null && label < groupedVertexLabels.length ? groupedVertexLabels[label] : null;
    }

    public void postDecode() {
        if (version >= 13) {
            for (int i = 0; i < vertexCount; i++) {
                verticesX[i] = verticesX[i] / 4;
                verticesY[i] = verticesY[i] / 4;
                verticesZ[i] = verticesZ[i] / 4;
            }
        }

        //System.out.println("Has triangle labels: " + (triangleLabels != null) + " | " + Arrays.toString(triangleLabels));

        applyGroups();

        int count = vertexCount;
        vertexPositionsCopyX = Arrays.copyOf(verticesX, count);
        vertexPositionsCopyY = Arrays.copyOf(verticesY, count);
        vertexPositionsCopyZ = Arrays.copyOf(verticesZ, count);
        if (triangleColors != null) {
            colorsCopy = Arrays.copyOf(triangleColors, triangleColors.length);
        }

        if (particleVertices == null) {
            particleVertices = new int[count];
        }

        if (triangleInfo == null) {
            triangleInfo = new int[triangleCount];
        }

        convertTexturesToNewFormat();

        // debug
        if (decodeType == ModelType.REV_634) {
            boolean shouldRemove = IntStreamEx.range(texturedTriangleCount)
                    .anyMatch(i -> texturePCoordinate[i] < 0 || texturePCoordinate[i] >= 32767 || textureMCoordinate[i] < 0 || textureMCoordinate[i] >= 32767 || textureNCoordinate[i] < 0 || textureNCoordinate[i] >= 32767);
            if (shouldRemove) {
                removeTextures();
                System.out.println("Removed textures from the model as they could not be loaded (index < 0 or >= 32767)");
                System.out.println("If you get this message please message me on discord");
            }

        }

    }

    public boolean oldModel = true;

    public int version;

    public int[] vertexPositionsCopyX;
    public int[] vertexPositionsCopyY;
    public int[] vertexPositionsCopyZ;
    // temp
    public int[] colorsCopy;
    public ModelType decodeType = ModelType.OLD;

    public int[] particleVertices;

    public int vertexCount;
    public int triangleCount;
    public int[] verticesX;
    public int[] verticesY;
    public int[] verticesZ;
    public int[] faceIndicesA;
    public int[] faceIndicesB;
    public int[] faceIndicesC;
    public int[] triangleInfo;
    public byte[] trianglePriorities;

    public int[] triangleAlpha;
    public int[] triangleColors;
    public byte modelPriority = 0;
    public int texturedTriangleCount;
    public int[] texturePCoordinate;
    public int[] textureMCoordinate;
    public int[] textureNCoordinate;

    public int[] triangleTextureIds;
    public int[] textureCoordinateIndices;
    public int[] textureTypes;

    public int[] vertexLabels;
    public int[] triangleLabels;
    public int[][] groupedVertexLabels;
    public int[][] groupedTriangleLabels;

    public int[][] skeletalBones;
    public int[][] skeletalWeights;

    public boolean isTextured(int index) {
        return triangleTextureIds != null && triangleTextureIds[index] != -1;
    }

    // those are the conditions in the client for setting the 3rd vertex color (faceColorZ) to -2
    public boolean makeTriangleTransparent(int index) {
        int textureId = triangleTextureIds == null ? -1 : triangleTextureIds[index];
        int info = triangleInfo == null ? 0 : triangleInfo[index];
        if (textureId != -1 && info != 0 && info != 1) {
            return true;
        }
        return textureId == -1 && info != 0 && info != 1 && info != 3;
    }

    public void applyGroups() {
        if (vertexLabels != null) {
            int[] labelVertexCount = new int[256];

            int count = 0;
            for (int v = 0; v < vertexCount; v++) {
                int label = vertexLabels[v];
                labelVertexCount[label]++;
                if (label > count) {
                    count = label;
                }
            }

            groupedVertexLabels = new int[count + 1][];

            for (int label = 0; label <= count; label++) {
                groupedVertexLabels[label] = new int[labelVertexCount[label]];
                labelVertexCount[label] = 0;
            }

            for (int v = 0; v < vertexCount; v++) {
                int label = vertexLabels[v];
                groupedVertexLabels[label][labelVertexCount[label]++] = v;
            }

        }

        if (triangleLabels != null) {
            int[] labelTriangleCount = new int[256];

            int count = 0;
            for (int f = 0; f < triangleCount; f++) {
                int label = triangleLabels[f];
                labelTriangleCount[label]++;
                if (label > count) {
                    count = label;
                }
            }

            groupedTriangleLabels = new int[count + 1][];
            for (int label = 0; label <= count; label++) {
                groupedTriangleLabels[label] = new int[labelTriangleCount[label]];
                labelTriangleCount[label] = 0;
            }

            for (int face = 0; face < triangleCount; face++) {
                int label = triangleLabels[face];
                groupedTriangleLabels[label][labelTriangleCount[label]++] = face;
            }

        }
    }

    public void convertTexturesToOldFormat() {
        if (triangleTextureIds == null || textureCoordinateIndices == null) {
            return;
        }

        if (triangleInfo == null) {
            triangleInfo = new int[triangleCount];
        }

        for (int i = 0; i < triangleCount; i++) {
            if (triangleTextureIds[i] == -1 || textureCoordinateIndices[i] == -1) {
                continue;
            }
            int mask = 2 + (textureCoordinateIndices[i] << 2);
            triangleInfo[i] = mask;
            triangleColors[i] = triangleTextureIds[i];
        }

        triangleTextureIds = null;
        textureCoordinateIndices = null;
    }

    public void convertTexturesToNewFormat() {
        // must already be in new format
        if (triangleTextureIds != null) {
            return;
        }

        // not textured
        if (texturedTriangleCount == 0) {
            return;
        }

        triangleTextureIds = new int[triangleCount];
        textureCoordinateIndices = new int[triangleCount];
        textureTypes = new int[texturedTriangleCount];

        for (int i = 0; i < triangleCount; i++) {
            if (triangleInfo[i] >= 2) { // textured
                int textureIndex = triangleInfo[i] >> 2;
                textureCoordinateIndices[i] = textureIndex;
                triangleTextureIds[i] = triangleColors[i];
                triangleInfo[i] = 0;
                triangleColors[i] = 127;
            } else {
                textureCoordinateIndices[i] = -1;
                triangleTextureIds[i] = -1;
            }
        }

    }

    public void makeDropModel(int rotation) {
        // rotate
        // translate
    }

    public void cacheVertexPositions() {
        int count = vertexCount;
        vertexPositionsCopyX = Arrays.copyOf(verticesX, count);
        vertexPositionsCopyY = Arrays.copyOf(verticesY, count);
        vertexPositionsCopyZ = Arrays.copyOf(verticesZ, count);
    }

    public void setOrigin() {
        int count = vertexCount;
        if (vertexPositionsCopyX == null) {
            vertexPositionsCopyX = Arrays.copyOf(verticesX, count);
            vertexPositionsCopyY = Arrays.copyOf(verticesY, count);
            vertexPositionsCopyZ = Arrays.copyOf(verticesZ, count);
        }
        System.arraycopy(vertexPositionsCopyX, 0, verticesX, 0, count);
        System.arraycopy(vertexPositionsCopyY, 0, verticesY, 0, count);
        System.arraycopy(vertexPositionsCopyZ, 0, verticesZ, 0, count);
    }

    public void rotateRoll(int angle, boolean reset) {
        if (reset) {
            setOrigin();
        }
        int sin = SINE[angle % 2048];
        int cos = COSINE[angle % 2048];
        for (int i = 0; i < vertexCount; i++) {
            int x = verticesX[i];
            int y = verticesY[i];

            int newX = y * sin + x * cos >> 16;
            int newY = y * cos - x * sin >> 16;

            verticesX[i] = newX;
            verticesY[i] = newY;
        }
    }

    public void rotatePitch(int angle, boolean reset) {
        if (reset) {
            setOrigin();
        }
        int sin = SINE[angle % 2048];
        int cos = COSINE[angle % 2048];
        for (int i = 0; i < vertexCount; i++) {
            int z = verticesZ[i];
            int y = verticesY[i];

            int newZ = y * sin + z * cos >> 16;
            int newY = y * cos - z * sin >> 16;

            verticesZ[i] = newZ;
            verticesY[i] = newY;
        }
    }


    public void rotateYaw(int angle, boolean setOrigin) {
        if (setOrigin) {
            setOrigin();
        }
        int sin = SINE[angle % 2048];
        int cos = COSINE[angle % 2048];
        for (int i = 0; i < vertexCount; i++) {
            int x = verticesX[i];
            int z = verticesZ[i];

            int newX = z * sin + x * cos >> 16;
            int newZ = z * cos - x * sin >> 16;

            verticesX[i] = newX;
            verticesZ[i] = newZ;
        }
    }

    public void translate(int x, int y, int z) {
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            verticesX[vertex] += x;
            verticesY[vertex] += y;
            verticesZ[vertex] += z;
        }
    }

    public void rotate(int pitch, int yaw, int roll) {
        setOrigin();
        rotateRoll(roll, false);
        rotatePitch(pitch, false);
        rotateYaw(yaw, false);
    }

    public void computeUVCoordinates() {
        if (textureUCoordinates != null && texturedTriangleCount == 0) {
            return;
        }

        this.textureUCoordinates = new float[triangleCount][];
        this.textureVCoordinates = new float[triangleCount][];

        float[] defaultU = new float[3];
        float[] defaultV = new float[3];

        defaultU[0] = 0.0F;
        defaultV[0] = 1.0F;

        defaultU[1] = 1.0F;
        defaultV[1] = 1.0F;

        defaultU[2] = 0.0F;
        defaultV[2] = 0.0F;

        for (int i = 0; i < triangleCount; i++) {
            textureUCoordinates[i] = defaultU;
            textureVCoordinates[i] = defaultV;
        }

        if (texturedTriangleCount == 0) {
            return;
        }

        for (int i = 0; i < triangleCount; i++) {
            int coordinate;
            if (textureCoordinateIndices == null || textureCoordinateIndices[i] == -1) {
                coordinate = -1;
            } else {
                coordinate = textureCoordinateIndices[i];
            }

            int textureIdx;
            if (triangleTextureIds == null || triangleTextureIds[i] == -1) {
                textureIdx = -1;
            } else {
                textureIdx = triangleTextureIds[i];
            }

            if (textureIdx != -1) {
                float[] u = new float[3];
                float[] v = new float[3];

                if (coordinate == -1) {
                    u[0] = 0.0F;
                    v[0] = 1.0F;

                    u[1] = 1.0F;
                    v[1] = 1.0F;

                    u[2] = 0.0F;
                    v[2] = 0.0F;
                } else {
                    coordinate &= 0xFF;


                    int faceA = faceIndicesA[i];
                    int faceB = faceIndicesB[i];
                    int faceC = faceIndicesC[i];

                    Point3D a = new Point3D(verticesX[faceA], verticesY[faceA], verticesZ[faceA]);
                    Point3D b = new Point3D(verticesX[faceB], verticesY[faceB], verticesZ[faceB]);
                    Point3D c = new Point3D(verticesX[faceC], verticesY[faceC], verticesZ[faceC]);

                    Point3D p = new Point3D(verticesX[texturePCoordinate[coordinate]], verticesY[texturePCoordinate[coordinate]], verticesZ[texturePCoordinate[coordinate]]);
                    Point3D m = new Point3D(verticesX[textureMCoordinate[coordinate]], verticesY[textureMCoordinate[coordinate]], verticesZ[textureMCoordinate[coordinate]]);
                    Point3D n = new Point3D(verticesX[textureNCoordinate[coordinate]], verticesY[textureNCoordinate[coordinate]], verticesZ[textureNCoordinate[coordinate]]);

                    Point3D s = m.subtract(p);
                    Point3D t = n.subtract(p);

                    Point3D pA = a.subtract(p);
                    Point3D pB = b.subtract(p);
                    Point3D pC = c.subtract(p);

                    Point3D pmnNormal = s.crossProduct(t); // normal to the plane PMN

                    Point3D uCoordinate = t.crossProduct(pmnNormal);
                    double mU = 1.0F / uCoordinate.dotProduct(s);

                    double uA = uCoordinate.dotProduct(pA) * mU;
                    double uB = uCoordinate.dotProduct(pB) * mU;
                    double uC = uCoordinate.dotProduct(pC) * mU;

                    Point3D vCoordinate = s.crossProduct(pmnNormal);
                    double mV = 1.0 / vCoordinate.dotProduct(t);
                    double vA = vCoordinate.dotProduct(pA) * mV;
                    double vB = vCoordinate.dotProduct(pB) * mV;
                    double vC = vCoordinate.dotProduct(pC) * mV;

                    u[0] = (float) uA;
                    u[1] = (float) uB;
                    u[2] = (float) uC;

                    v[0] = (float) vA;
                    v[1] = (float) vB;
                    v[2] = (float) vC;

                }
                this.textureUCoordinates[i] = u;
                this.textureVCoordinates[i] = v;
            }
        }
    }

    /**
     * @param face the face that is textured
     * @param p    the origin of the texture in 3D space
     * @param m    the horizontal end of the texture in 3D space, i.e. uv at (1, 0)
     * @param n    the vertical end of the texture in 3D space, i.e. uv at (0, 1)
     * @return the per-vertex UV coordinates
     */
    public TriangleUV computeUVCoordinatesForFace(int face, Point3D p, Point3D m, Point3D n) {
        int faceA = faceIndicesA[face];
        int faceB = faceIndicesB[face];
        int faceC = faceIndicesC[face];

        Point3D a = new Point3D(verticesX[faceA], verticesY[faceA], verticesZ[faceA]);
        Point3D b = new Point3D(verticesX[faceB], verticesY[faceB], verticesZ[faceB]);
        Point3D c = new Point3D(verticesX[faceC], verticesY[faceC], verticesZ[faceC]);

        // the horizontal and vertical axis of the texture plane
        Point3D s = m.subtract(p);
        Point3D t = n.subtract(p);
        // convert vertices to the local coordinate system relative to the origin p
        Point3D pA = a.subtract(p);
        Point3D pB = b.subtract(p);
        Point3D pC = c.subtract(p);

        // normal to the plane PMN
        // explanation:
        // the surface normal for a triangle can be calculated by taking the vector cross product of two edges of that triangle
        // if a triangle was defined by 3 vertices v1, v2, v3 then the normal of the triangle would be calculated the following way:
        // u = v2 - v1
        // v = v3 - v1
        // N = u x v
        Point3D normalPMN = s.crossProduct(t);

        // direction perpendicular to both the normal of the plane and t - it can be considered as the "u" coordinate axis direction in the plane.
        Point3D uCoordinate = t.crossProduct(normalPMN);

        // used to normalize the u coordinate(?)
        double mU = 1.0F / uCoordinate.dotProduct(s);

        // project pA onto uCoordinate
        // this computes the distance from the origin(P) to the projection of vertex A onto the "u" axis direction in the plane
        double uA = uCoordinate.dotProduct(pA) * mU;
        // same as above but for vertices B and C
        double uB = uCoordinate.dotProduct(pB) * mU;
        double uC = uCoordinate.dotProduct(pC) * mU;

        // same as uCoordinate except it points in the "v" coordinate axis direction within the plane
        Point3D vCoordinate = s.crossProduct(normalPMN);
        // used to normalize the v coordinate(?)
        double mV = 1.0 / vCoordinate.dotProduct(t);
        // same as above but for the v coordinate instead
        double vA = vCoordinate.dotProduct(pA) * mV;
        double vB = vCoordinate.dotProduct(pB) * mV;
        double vC = vCoordinate.dotProduct(pC) * mV;

        // you may also wonder why both s and t is necessary for both u and v
        // because it certainly makes more sense that s would be needed to compute u and t to compute v
        // however both are needed because they're not necessarily axis aligned

        return new TriangleUV((float) uA, (float) uB, (float) uC, (float) vA, (float) vB, (float) vC);
    }

    public TriangleUV computeUVCoordinatesForFace(int face, Vector3i pCoordinate, Vector3i mCoordinate, Vector3i nCoordinate) {
        Point3D p = pCoordinate.toPoint3D();
        Point3D m = mCoordinate.toPoint3D();
        Point3D n = nCoordinate.toPoint3D();
        int faceA = faceIndicesA[face];
        int faceB = faceIndicesB[face];
        int faceC = faceIndicesC[face];

        Point3D a = new Point3D(verticesX[faceA], verticesY[faceA], verticesZ[faceA]);
        Point3D b = new Point3D(verticesX[faceB], verticesY[faceB], verticesZ[faceB]);
        Point3D c = new Point3D(verticesX[faceC], verticesY[faceC], verticesZ[faceC]);

        Point3D pM = m.subtract(p);
        Point3D pN = n.subtract(p);
        Point3D pA = a.subtract(p);
        Point3D pB = b.subtract(p);
        Point3D pC = c.subtract(p);

        Point3D normalPMN = pM.crossProduct(pN); // normal to the plane PMN

        Point3D uCoordinate = pN.crossProduct(normalPMN);
        double mU = 1.0F / uCoordinate.dotProduct(pM);

        double uA = uCoordinate.dotProduct(pA) * mU;
        double uB = uCoordinate.dotProduct(pB) * mU;
        double uC = uCoordinate.dotProduct(pC) * mU;

        Point3D vCoordinate = pM.crossProduct(normalPMN);
        double mV = 1.0 / vCoordinate.dotProduct(pN);
        double vA = vCoordinate.dotProduct(pA) * mV;
        double vB = vCoordinate.dotProduct(pB) * mV;
        double vC = vCoordinate.dotProduct(pC) * mV;
        return new TriangleUV((float) uA, (float) uB, (float) uC, (float) vA, (float) vB, (float) vC);
    }

    public TriangleUV computeUVCoordinatesForFace(Triangle face, Point3D pCoordinate, Point3D mCoordinate, Point3D nCoordinate) {
        Point3D p = pCoordinate;
        Point3D m = mCoordinate;
        Point3D n = nCoordinate;

        Point3D a = face.v1();
        Point3D b = face.v2();
        Point3D c = face.v3();

        Point3D pM = m.subtract(p);
        Point3D pN = n.subtract(p);
        Point3D pA = a.subtract(p);
        Point3D pB = b.subtract(p);
        Point3D pC = c.subtract(p);

        Point3D normalPMN = pM.crossProduct(pN); // normal to the plane PMN

        Point3D uCoordinate = pN.crossProduct(normalPMN);
        double mU = 1.0F / uCoordinate.dotProduct(pM);

        double uA = uCoordinate.dotProduct(pA) * mU;
        double uB = uCoordinate.dotProduct(pB) * mU;
        double uC = uCoordinate.dotProduct(pC) * mU;

        Point3D vCoordinate = pM.crossProduct(normalPMN);
        double mV = 1.0 / vCoordinate.dotProduct(pN);
        double vA = vCoordinate.dotProduct(pA) * mV;
        double vB = vCoordinate.dotProduct(pB) * mV;
        double vC = vCoordinate.dotProduct(pC) * mV;
        return new TriangleUV((float) uA, (float) uB, (float) uC, (float) vA, (float) vB, (float) vC);
    }

    public Map<TextureCoordinate, Vector3i> generateIndicesForTexCoords(List<TextureCoordinate> texCoords) {
        Map<TextureCoordinate, Vector3i> texCoordToIndex = new HashMap<>();
        // TODO: take a look at this one
        texCoords.forEach(texCoord -> {
            Vector3i p = Vector3i.fromPoint3D(texCoord.getP());
            Vector3i m = Vector3i.fromPoint3D(texCoord.getM());
            Vector3i n = Vector3i.fromPoint3D(texCoord.getN());
            int pIndex = findVertexIndex(p);
            int mIndex = findVertexIndex(m);
            int nIndex = findVertexIndex(n);
            if (pIndex == -1 || mIndex == -1 || nIndex == -1) {
                Vector3i index = addTextureMapTriangle(p, m, n);
                texCoordToIndex.put(texCoord, index);
                System.out.println("had to generate new texture coordinate");
            } else {
                texCoordToIndex.put(texCoord, new Vector3i(pIndex, mIndex, nIndex));
            }

        });
        return texCoordToIndex;
    }

    private int findVertexIndex(Vector3i point) {
        for (int i = 0; i < vertexCount; i++) {
            if (verticesX[i] == point.x() && verticesY[i] == point.y() && verticesZ[i] == point.z()) {
                return i;
            }
        }
        return -1;
    }

    public int findTextureCoordinateIndex(TextureCoordinate coordinate) {
        for (int i = 0; i < texturedTriangleCount; i++) {
            int pIndex = texturePCoordinate[i];
            int mIndex = textureMCoordinate[i];
            int nIndex = textureNCoordinate[i];
            Point3D p = toVector3i(pIndex).toPoint3D();
            Point3D m = toVector3i(mIndex).toPoint3D();
            Point3D n = toVector3i(nIndex).toPoint3D();
            TextureCoordinate other = TextureCoordinate.fromPMN(p, m, n);
            if (coordinate.isSame(other)) {
                return i;
            }
        }
        return -1;
    }

    private Vector3i toVector3i(int index) {
        return new Vector3i(verticesX[index], verticesY[index], verticesZ[index]);
    }

    public TriangleUV computeUVCoordinatesForFace(int face, int aTex, int bTex, int cTex) {
        int faceA = faceIndicesA[face];
        int faceB = faceIndicesB[face];
        int faceC = faceIndicesC[face];

        Point3D a = new Point3D(verticesX[faceA], verticesY[faceA], verticesZ[faceA]);
        Point3D b = new Point3D(verticesX[faceB], verticesY[faceB], verticesZ[faceB]);
        Point3D c = new Point3D(verticesX[faceC], verticesY[faceC], verticesZ[faceC]);

        Point3D p = new Point3D(verticesX[aTex], verticesY[aTex], verticesZ[aTex]);
        Point3D m = new Point3D(verticesX[bTex], verticesY[bTex], verticesZ[bTex]);
        Point3D n = new Point3D(verticesX[cTex], verticesY[cTex], verticesZ[cTex]);


        Point3D pM = m.subtract(p);
        Point3D pN = n.subtract(p);
        Point3D pA = a.subtract(p);
        Point3D pB = b.subtract(p);
        Point3D pC = c.subtract(p);

        Point3D pMxPn = pM.crossProduct(pN); // normal to the plane PMN

        Point3D uCoordinate = pN.crossProduct(pMxPn);
        double mU = 1.0F / uCoordinate.dotProduct(pM);

        double uA = uCoordinate.dotProduct(pA) * mU;
        double uB = uCoordinate.dotProduct(pB) * mU;
        double uC = uCoordinate.dotProduct(pC) * mU;

        Point3D vCoordinate = pM.crossProduct(pMxPn);
        double mV = 1.0 / vCoordinate.dotProduct(pN);
        double vA = vCoordinate.dotProduct(pA) * mV;
        double vB = vCoordinate.dotProduct(pB) * mV;
        double vC = vCoordinate.dotProduct(pC) * mV;
        return new TriangleUV((float) uA, (float) uB, (float) uC, (float) vA, (float) vB, (float) vC);
    }

    public void computeNormals() {
        if (this.vertexNormals != null) {
            return;
        }

        this.vertexNormals = new VertexNormal[this.vertexCount];

        int index;
        for (index = 0; index < this.vertexCount; index++) {
            this.vertexNormals[index] = new VertexNormal();
        }

        for (index = 0; index < this.triangleCount; index++) {
            int vertexA = this.faceIndicesA[index];
            int vertexB = this.faceIndicesB[index];
            int vertexC = this.faceIndicesC[index];

            int xA = this.verticesX[vertexB] - this.verticesX[vertexA];
            int yA = this.verticesY[vertexB] - this.verticesY[vertexA];
            int zA = this.verticesZ[vertexB] - this.verticesZ[vertexA];

            int xB = this.verticesX[vertexC] - this.verticesX[vertexA];
            int yB = this.verticesY[vertexC] - this.verticesY[vertexA];
            int zB = this.verticesZ[vertexC] - this.verticesZ[vertexA];

            // Compute cross product
            int nx = yA * zB - yB * zA;
            int ny = zA * xB - zB * xA;
            int nz = xA * yB - xB * yA;

            while (nx > 8192 || ny > 8192 || nz > 8192 || nx < -8192 || ny < -8192 || nz < -8192) {
                nx >>= 1;
                ny >>= 1;
                nz >>= 1;
            }

            int length = (int) Math.sqrt(nx * nx + ny * ny + nz * nz);
            if (length <= 0) {
                length = 1;
            }

            nx = nx * 256 / length;
            ny = ny * 256 / length;
            nz = nz * 256 / length;

            int triangleInfo;
            if (this.triangleInfo == null) {
                triangleInfo = 0;
            } else {
                triangleInfo = this.triangleInfo[index];
            }

            if (triangleInfo == 0) {
                VertexNormal vertexNormal = this.vertexNormals[vertexA];
                vertexNormal.x += nx;
                vertexNormal.y += ny;
                vertexNormal.z += nz;
                vertexNormal.magnitude++;

                vertexNormal = this.vertexNormals[vertexB];
                vertexNormal.x += nx;
                vertexNormal.y += ny;
                vertexNormal.z += nz;
                vertexNormal.magnitude++;

                vertexNormal = this.vertexNormals[vertexC];
                vertexNormal.x += nx;
                vertexNormal.y += ny;
                vertexNormal.z += nz;
                vertexNormal.magnitude++;
            }

            if (this.faceNormals == null) {
                this.faceNormals = new FaceNormal[this.triangleCount];
            }

            FaceNormal faceNormal = this.faceNormals[index] = new FaceNormal();
            faceNormal.x = nx;
            faceNormal.y = ny;
            faceNormal.z = nz;
        }
    }

    public void debugSkeletalBoneStuff() {
        int[][] bones = skeletalBones;
        if (skeletalBones == null) {
            return;
        }

        // vertex -> bone indices affecting it
        Map<Integer, List<Integer>> labels = new HashMap<>();
        for (int vertex = 0; vertex < bones.length; vertex++) {
            for (int bone = 0; bone < bones[vertex].length; bone++) {
                int boneIndex = bones[vertex][bone];
                labels.computeIfAbsent(vertex, _ -> new ArrayList<>()).add(boneIndex);
            }
        }

        List<Integer> boneIndices = IntStreamEx.range(vertexCount)
                .flatMap(i -> IntStreamEx.of(bones[i]))
                .boxed()
                .distinct()
                .toList();

        System.out.println(boneIndices);

    }

    public RSModel merge(List<RSModel> models) {
        boolean copyInfo = false;
        boolean copyPriority = false;
        boolean copyAlpha = false;
        boolean copyLabels = false;
        boolean mergeSkeletalData = false;

        vertexCount = 0;
        triangleCount = 0;
        texturedTriangleCount = 0;
        modelPriority = -1;

        for (RSModel model : models) {
            if (model == null) {
                continue;
            }

            vertexCount += model.vertexCount;
            triangleCount += model.triangleCount;
            texturedTriangleCount += model.texturedTriangleCount;

            copyInfo |= model.triangleInfo != null;

            if (model.trianglePriorities != null) {
                copyPriority = true;
            } else {
                if (modelPriority == -1) {
                    modelPriority = model.modelPriority;
                }
                if (modelPriority != model.modelPriority) {
                    copyPriority = true;
                }
            }

            copyAlpha |= model.triangleAlpha != null;
            copyLabels |= model.triangleLabels != null;
            mergeSkeletalData |= model.skeletalBones != null;
        }

        verticesX = new int[vertexCount];
        verticesY = new int[vertexCount];
        verticesZ = new int[vertexCount];
        vertexLabels = new int[vertexCount];
        faceIndicesA = new int[triangleCount];
        faceIndicesB = new int[triangleCount];
        faceIndicesC = new int[triangleCount];
        texturePCoordinate = new int[texturedTriangleCount];
        textureMCoordinate = new int[texturedTriangleCount];
        textureNCoordinate = new int[texturedTriangleCount];

        if (copyInfo) {
            triangleInfo = new int[triangleCount];
        }

        if (copyPriority) {
            trianglePriorities = new byte[triangleCount];
        }

        if (copyAlpha) {
            triangleAlpha = new int[triangleCount];
        }

        if (copyLabels) {
            triangleLabels = new int[triangleCount];
        }

        if (mergeSkeletalData) {
            this.skeletalBones = new int[this.vertexCount][];
            this.skeletalWeights = new int[this.vertexCount][];
        }

        triangleColors = new int[triangleCount];
        vertexCount = 0;
        triangleCount = 0;
        texturedTriangleCount = 0;

        int ttriangleCount = 0;

        for (RSModel model : models) {
            if (model == null) {
                continue;
            }

            for (int face = 0; face < model.triangleCount; face++) {
                if (copyInfo) {
                    if (model.triangleInfo == null) {
                        triangleInfo[triangleCount] = 0;
                    } else {
                        int info = model.triangleInfo[face];

                        if ((info & 2) == 2) {
                            info += ttriangleCount << 2;
                        }

                        triangleInfo[triangleCount] = info;
                    }
                }

                if (copyPriority) {
                    if (model.trianglePriorities == null) {
                        trianglePriorities[triangleCount] = model.modelPriority;
                    } else {
                        trianglePriorities[triangleCount] = model.trianglePriorities[face];
                    }
                }

                if (copyAlpha) {
                    if (model.triangleAlpha == null) {
                        triangleAlpha[triangleCount] = 0;
                    } else {
                        triangleAlpha[triangleCount] = model.triangleAlpha[face];
                    }
                }

                if (copyLabels && (model.triangleLabels != null)) {
                    triangleLabels[triangleCount] = model.triangleLabels[face];
                }

                triangleColors[triangleCount] = model.triangleColors[face];
                faceIndicesA[triangleCount] = addVertex(model, model.faceIndicesA[face]);
                faceIndicesB[triangleCount] = addVertex(model, model.faceIndicesB[face]);
                faceIndicesC[triangleCount] = addVertex(model, model.faceIndicesC[face]);
                triangleCount++;
            }

            // delete if errors for high rev models
            for (int tFace = 0; tFace < model.texturedTriangleCount; tFace++) {
                texturePCoordinate[texturedTriangleCount] = (short) addVertex(model, model.texturePCoordinate[tFace]);
                textureMCoordinate[texturedTriangleCount] = (short) addVertex(model, model.textureMCoordinate[tFace]);
                textureNCoordinate[texturedTriangleCount] = (short) addVertex(model, model.textureNCoordinate[tFace]);
                texturedTriangleCount++;
            }

            ttriangleCount += model.texturedTriangleCount;
        }

        // added code
        verticesX = Arrays.copyOf(verticesX, vertexCount);
        verticesY = Arrays.copyOf(verticesY, vertexCount);
        verticesZ = Arrays.copyOf(verticesZ, vertexCount);
        if (vertexLabels != null) {
            vertexLabels = Arrays.copyOf(vertexLabels, vertexCount);
        }
        faceIndicesA = Arrays.copyOf(faceIndicesA, triangleCount);
        faceIndicesB = Arrays.copyOf(faceIndicesB, triangleCount);
        faceIndicesC = Arrays.copyOf(faceIndicesC, triangleCount);
        if (texturePCoordinate != null) {
            texturePCoordinate = Arrays.copyOf(texturePCoordinate, texturedTriangleCount);
            textureMCoordinate = Arrays.copyOf(textureMCoordinate, texturedTriangleCount);
            textureNCoordinate = Arrays.copyOf(textureNCoordinate, texturedTriangleCount);
        }

        List<Integer> ids = models.stream()
                .map(RSModel::getModelIds)
                .flatMap(List::stream)
                .collect(Collectors.toCollection(ArrayList::new));
        if (modelIds != null) {
            modelIds.addAll(ids);
        } else {
            modelIds = ids;
        }
        return this;
    }

    /**
     * Adds <code>vertexId</code> from <code>src</code> to <code>this</code>. Reuses vertex if one already exists at the
     * same location.
     *
     * @param src      the source model.
     * @param vertexId the vertex id to add from the <code>src</code>.
     * @return the vertex id of the added vertex.
     */
    public int addVertex(RSModel src, int vertexId) {
        int x = src.verticesX[vertexId];
        int y = src.verticesY[vertexId];
        int z = src.verticesZ[vertexId];

        int identical = -1;
        for (int v = 0; v < vertexCount; v++) {
            if ((x == verticesX[v]) && (y == verticesY[v]) && (z == verticesZ[v])) {
                identical = v;
                break;
            }
        }

        // append new one if no matches were found
        if (identical == -1) {
            verticesX[vertexCount] = x;
            verticesY[vertexCount] = y;
            verticesZ[vertexCount] = z;
            if (src.vertexLabels != null) {
                vertexLabels[vertexCount] = src.vertexLabels[vertexId];
            }

            if (src.skeletalBones != null) {
                this.skeletalBones[this.vertexCount] = src.skeletalBones[vertexId];
                this.skeletalWeights[this.vertexCount] = src.skeletalWeights[vertexId];
            }

            identical = vertexCount++;

            //System.out.println("Current: " + vertexLabels.length + " vs " + vertexCount);
        }

        return identical;
    }

    public VertexNormal[] vertexNormals;
    public FaceNormal[] faceNormals;

    public float[][] textureUCoordinates;
    public float[][] textureVCoordinates;

    @Getter
    @Setter // temp
    private List<Integer> modelIds;

    public void calculateBoundsCylinder() {
        minY = 0;
        radius = 0;
        maxY = 0;
        for (int i = 0; i < vertexCount; i++) {
            int x = verticesX[i];
            int y = verticesY[i];
            int z = verticesZ[i];
            if (-y > minY) {
                minY = -y;
            }
            if (y > maxY) {
                maxY = y;
            }
            int radiusSqr = (x * x) + (z * z);
            if (radiusSqr > radius) {
                radius = radiusSqr;
            }
        }
        radius = (int) (Math.sqrt(radius) + 0.99);
        minDepth = (int) (Math.sqrt((radius * radius) + (minY * minY)) + 0.99);
        maxDepth = minDepth + (int) (Math.sqrt((radius * radius) + (maxY * maxY)) + 0.99);
    }

    /**
     * Calculates {@link #minY}, {@link #maxY}, {@link #minDepth} and {@link #maxDepth}.
     */
    public void calculateBoundsY() {
        minY = 0;
        maxY = 0;
        for (int i = 0; i < vertexCount; i++) {
            int y = verticesY[i];
            if (-y > minY) {
                minY = -y;
            }
            if (y > maxY) {
                maxY = y;
            }
        }
        minDepth = (int) (Math.sqrt((radius * radius) + (minY * minY)) + 0.99);
        maxDepth = minDepth + (int) (Math.sqrt((radius * radius) + (maxY * maxY)) + 0.99);
    }

    public int minX;
    public int maxX;
    public int maxY;
    public int minZ;
    public int maxZ;
    /**
     * The radius of this model on the XZ plane.
     *
     * @see #calculateBoundsAABB()
     */
    public int radius;
    public int minDepth;
    public int maxDepth;
    public int minY = 1000;

    /**
     * Calculates this models axis-aligned bounding box (AABB) and stores it.
     */
    public void calculateBoundsAABB() {
        minY = 0;
        radius = 0;
        maxY = 0;
        minX = 999999;
        maxX = -999999;
        maxZ = -99999;
        minZ = 99999;
        for (int j = 0; j < vertexCount; j++) {
            int x = verticesX[j];
            int y = verticesY[j];
            int z = verticesZ[j];
            if (x < minX) {
                minX = x;
            }
            if (x > maxX) {
                maxX = x;
            }
            if (z < minZ) {
                minZ = z;
            }
            if (z > maxZ) {
                maxZ = z;
            }
            /*
             *             if (-y > minY) {
             *                 minY = -y;
             *             }
             */
            if (y < minY) {
                minY = y;
            }
            if (y > maxY) {
                maxY = y;
            }
            int radiusSqr = (x * x) + (z * z);
            if (radiusSqr > radius) {
                radius = radiusSqr;
            }
        }
        radius = (int) Math.sqrt(radius);
        minDepth = (int) Math.sqrt((radius * radius) + (minY * minY));
        maxDepth = minDepth + (int) Math.sqrt((radius * radius) + (maxY * maxY));
    }

    /**
     * Recalculates the normals and optionally applies the lighting immediately after. This method <i>can</i> be
     * destructive if <code>applyLighting</code> is set to <code>true</code>.
     *
     * @param lightAmbient     the ambient light.
     * @param lightAttenuation the light attenuation.
     * @param lightSrcX        the light source x.
     * @param lightSrcY        the light source y.
     * @param lightSrcZ        the light source z.
     * @param applyLighting    <code>true</code> to invoke {@link #applyLighting(int, int, int, int, int)} after normals are
     *                         calculated.
     * @see #applyLighting(int, int, int, int, int)
     */
    public void calculateNormals(int lightAmbient, int lightAttenuation, int lightSrcX, int lightSrcY, int lightSrcZ, boolean applyLighting) {
        if (faceColorA != null) {
            return;
        }
        int lightMagnitude = (int) Math.sqrt((lightSrcX * lightSrcX) + (lightSrcY * lightSrcY) + (lightSrcZ * lightSrcZ));
        int attenuation = (lightAttenuation * lightMagnitude) >> 8;

        faceColorA = new int[triangleCount];
        faceColorB = new int[triangleCount];
        faceColorC = new int[triangleCount];

        if (vertexNormal == null) {
            vertexNormal = new VertexNormal[vertexCount];
            for (int v = 0; v < vertexCount; v++) {
                vertexNormal[v] = new VertexNormal();
            }
        }

        for (int f = 0; f < triangleCount; f++) {
            int a = faceIndicesA[f];
            int b = faceIndicesB[f];
            int c = faceIndicesC[f];

            int dxAB = verticesX[b] - verticesX[a];
            int dyAB = verticesY[b] - verticesY[a];
            int dzAB = verticesZ[b] - verticesZ[a];

            int dxAC = verticesX[c] - verticesX[a];
            int dyAC = verticesY[c] - verticesY[a];
            int dzAC = verticesZ[c] - verticesZ[a];

            int nx = (dyAB * dzAC) - (dyAC * dzAB);
            int ny = (dzAB * dxAC) - (dzAC * dxAB);
            int nz = (dxAB * dyAC) - (dxAC * dyAB);

            while ((nx > 8192) || (ny > 8192) || (nz > 8192) || (nx < -8192) || (ny < -8192) || (nz < -8192)) {
                nx >>= 1;
                ny >>= 1;
                nz >>= 1;
            }

            int length = (int) Math.sqrt((nx * nx) + (ny * ny) + (nz * nz));

            if (length <= 0) {
                length = 1;
            }

            // normalize
            nx = (nx * 256) / length;
            ny = (ny * 256) / length;
            nz = (nz * 256) / length;

            if ((triangleInfo == null) || ((triangleInfo[f] & 1) == 0)) {
                VertexNormal n = vertexNormal[a];
                n.x += nx;
                n.y += ny;
                n.z += nz;
                n.magnitude++;
                n = vertexNormal[b];
                n.x += nx;
                n.y += ny;
                n.z += nz;
                n.magnitude++;
                n = vertexNormal[c];
                n.x += nx;
                n.y += ny;
                n.z += nz;
                n.magnitude++;
            } else {
                int lightness = lightAmbient + (((lightSrcX * nx) + (lightSrcY * ny) + (lightSrcZ * nz)) / (attenuation + (attenuation / 2)));
                faceColorA[f] = mulColorLightness(triangleColors[f], lightness, triangleInfo[f]);
            }
        }

        if (applyLighting) {
            applyLighting(lightAmbient, attenuation, lightSrcX, lightSrcY, lightSrcZ);
        } else {
            vertexNormalOriginal = new VertexNormal[vertexCount];

            for (int v = 0; v < vertexCount; v++) {
                VertexNormal normal = vertexNormal[v];
                VertexNormal copy = vertexNormalOriginal[v] = new VertexNormal();
                copy.x = normal.x;
                copy.y = normal.y;
                copy.z = normal.z;
                copy.magnitude = normal.magnitude;
            }
        }

        if (applyLighting) {
            calculateBoundsCylinder();
        } else {
            calculateBoundsAABB();
        }
    }

    /**
     * Utility function. Multiplies the input HSL lightness component by the provided <code>scalar</code>.
     *
     * @param hsl      the color value.
     * @param scalar   the scalar. [0...127]
     * @param faceInfo Provided face info to determine the type of color to return. Textured triangles (type 2) only have
     *                 a lightness component.
     * @return the color.
     * @see HSLPalette
     */
    public static int mulColorLightness(int hsl, int scalar, int faceInfo) {
        if ((faceInfo & 2) == 2) {
            if (scalar < 0) {
                scalar = 0;
            } else if (scalar > 127) {
                scalar = 127;
            }
            scalar = 127 - scalar;
            return scalar;
        }
        scalar = (scalar * (hsl & 0x7f)) >> 7;
        if (scalar < 2) {
            scalar = 2;
        } else if (scalar > 126) {
            scalar = 126;
        }
        return (hsl & 0xff80) + scalar;
    }

    /**
     * Calculates the lightness values for all faces using the provided lighting parameters. This method is destructive
     * and nullifies normals and labels, which means it is meant to be applied once to a model.
     *
     * @param lightAmbient     the ambient lighting value. [0...127]
     * @param lightAttenuation the light attenuation.
     * @param lightSrcX        the light source x.
     * @param lightSrcY        the light source y.
     * @param lightSrcZ        the light source z.
     */
    public void applyLighting(int lightAmbient, int lightAttenuation, int lightSrcX, int lightSrcY, int lightSrcZ) {
        for (int f = 0; f < triangleCount; f++) {
            int a = faceIndicesA[f];
            int b = faceIndicesB[f];
            int c = faceIndicesC[f];

            if (triangleInfo == null) {
                int color = triangleColors[f];

                VertexNormal n = vertexNormal[a];
                int lightness = lightAmbient + (((lightSrcX * n.x) + (lightSrcY * n.y) + (lightSrcZ * n.z)) / (lightAttenuation * n.magnitude));
                faceColorA[f] = mulColorLightness(color, lightness, 0);

                n = vertexNormal[b];
                lightness = lightAmbient + (((lightSrcX * n.x) + (lightSrcY * n.y) + (lightSrcZ * n.z)) / (lightAttenuation * n.magnitude));
                faceColorB[f] = mulColorLightness(color, lightness, 0);

                n = vertexNormal[c];
                lightness = lightAmbient + (((lightSrcX * n.x) + (lightSrcY * n.y) + (lightSrcZ * n.z)) / (lightAttenuation * n.magnitude));
                faceColorC[f] = mulColorLightness(color, lightness, 0);
            } else if ((triangleInfo[f] & 1) == 0) {
                int color = triangleColors[f];
                int info = triangleInfo[f];

                VertexNormal n = vertexNormal[a];
                int lightness = lightAmbient + (((lightSrcX * n.x) + (lightSrcY * n.y) + (lightSrcZ * n.z)) / (lightAttenuation * n.magnitude));
                faceColorA[f] = mulColorLightness(color, lightness, info);

                n = vertexNormal[b];
                lightness = lightAmbient + (((lightSrcX * n.x) + (lightSrcY * n.y) + (lightSrcZ * n.z)) / (lightAttenuation * n.magnitude));
                faceColorB[f] = mulColorLightness(color, lightness, info);

                n = vertexNormal[c];
                lightness = lightAmbient + (((lightSrcX * n.x) + (lightSrcY * n.y) + (lightSrcZ * n.z)) / (lightAttenuation * n.magnitude));
                faceColorC[f] = mulColorLightness(color, lightness, info);
            }
        }

        vertexNormal = null;
        vertexNormalOriginal = null;
        vertexLabels = null;
        triangleLabels = null;

        if (triangleInfo != null) {
            for (int f = 0; f < triangleCount; f++) {
                if ((triangleInfo[f] & 2) == 2) {
                    return;
                }
            }
        }

        triangleColors = null;
    }

    public int[] faceColorA;
    public int[] faceColorB;
    public int[] faceColorC;

    public VertexNormal[] vertexNormal;
    public VertexNormal[] vertexNormalOriginal;

    public Vector3i addTextureMapTriangle(Vector3i p, Vector3i m, Vector3i n) {
        int newVertexCount = vertexCount + 3;
        verticesX = Arrays.copyOf(verticesX, newVertexCount);
        verticesY = Arrays.copyOf(verticesY, newVertexCount);
        verticesZ = Arrays.copyOf(verticesZ, newVertexCount);

        int newFaceA = newVertexCount - 3;
        int newFaceB = newVertexCount - 2;
        int newFaceC = newVertexCount - 1;

        verticesX[newFaceA] = p.x();
        verticesY[newFaceA] = p.y();
        verticesZ[newFaceA] = p.z();

        verticesX[newFaceB] = m.x();
        verticesY[newFaceB] = m.y();
        verticesZ[newFaceB] = m.z();

        verticesX[newFaceC] = n.x();
        verticesY[newFaceC] = n.y();
        verticesZ[newFaceC] = n.z();

        vertexCount = newVertexCount;

        if (texturedTriangleCount == 0) {
            texturePCoordinate = new int[1];
            textureMCoordinate = new int[1];
            textureNCoordinate = new int[1];
        }
        int newTexturedTriangleCount = texturedTriangleCount + 1;
        texturePCoordinate = Arrays.copyOf(texturePCoordinate, newTexturedTriangleCount);
        textureMCoordinate = Arrays.copyOf(textureMCoordinate, newTexturedTriangleCount);
        textureNCoordinate = Arrays.copyOf(textureNCoordinate, newTexturedTriangleCount);

        texturePCoordinate[texturedTriangleCount] = (short) newFaceA;
        textureMCoordinate[texturedTriangleCount] = (short) newFaceB;
        textureNCoordinate[texturedTriangleCount] = (short) newFaceC;

        texturedTriangleCount = newTexturedTriangleCount;

        if (vertexLabels != null) {
            vertexLabels = Arrays.copyOf(vertexLabels, vertexCount);
        }

        return new Vector3i(newFaceA, newFaceB, newFaceC);
    }

    @Override
    public int getId() {
        return modelIds.getFirst();
    }

    @Override
    public void setId(int id) {
        modelIds = new ArrayList<>();
        modelIds.add(id);
    }

    @AllArgsConstructor
    @ToString
    private static class VertexIndices {
        public int a;
        public int b;
        public int c;
    }

    public void removeTriangle(int triangleIndex) {

        System.out.println("Removing triangle at index " + triangleIndex);

        List<Vector3i> vertices = IntStreamEx.range(vertexCount)
                .mapToObj(index -> new Vector3i(verticesX[index], verticesY[index], verticesZ[index]))
                .toMutableList();

        List<VertexIndices> triangles = IntStreamEx.range(triangleCount)
                .mapToObj(index -> new VertexIndices(faceIndicesA[index], faceIndicesB[index], faceIndicesC[index]))
                .toMutableList();

        System.out.println("Vertices: " + vertices);
        System.out.println("Indices: " + triangles);

        removeTriangle(vertices, triangles, triangleIndex);

        System.out.println("-".repeat(100));
        System.out.println("Vertices: " + vertices);
        System.out.println("Indices: " + triangles);

        int vertexCount = vertices.size();
        this.vertexCount = vertexCount;
        int[] x = new int[vertexCount];
        int[] y = new int[vertexCount];
        int[] z = new int[vertexCount];
        for (int i = 0; i < vertexCount; i++) {
            Vector3i vertex = vertices.get(i);
            x[i] = vertex.x();
            y[i] = vertex.y();
            z[i] = vertex.z();
        }
        verticesX = x;
        verticesY = y;
        verticesZ = z;

        int indexCount = triangles.size();
        this.triangleCount = indexCount;
        int[] indicesA = new int[indexCount];
        int[] indicesB = new int[indexCount];
        int[] indicesC = new int[indexCount];
        for (int i = 0; i < indexCount; i++) {
            VertexIndices tri = triangles.get(i);
            indicesA[i] = tri.a;
            indicesB[i] = tri.b;
            indicesC[i] = tri.c;
        }
        faceIndicesA = indicesA;
        faceIndicesB = indicesB;
        faceIndicesC = indicesC;

        triangleColors = new int[triangleCount];
        Arrays.fill(triangleColors, 1000);
        //ArrayUtils.remove(triangleColors, triangleIndex);
        //ArrayUtils.remove(triangleInfo, triangleIndex);
        //ArrayUtils.remove(triangleAlpha, triangleIndex);
    }


    public void removeTriangle(List<Vector3i> vertices, List<VertexIndices> triangles, int index) {
        VertexIndices triangle = triangles.get(index);

        Set<Vector3i> unusedVertices = new HashSet<>();

        unusedVertices.add(vertices.get(triangle.a));
        unusedVertices.add(vertices.get(triangle.b));
        unusedVertices.add(vertices.get(triangle.c));

        for (VertexIndices other : triangles) {
            if (other == triangle) {
                continue;
            }

            if (other.a == triangle.a || other.b == triangle.a || other.c == triangle.a) {
                unusedVertices.remove(vertices.get(triangle.a));
            }

            if (other.a == triangle.b || other.b == triangle.b || other.c == triangle.b) {
                unusedVertices.remove(vertices.get(triangle.b));
            }

            if (other.a == triangle.c || other.b == triangle.c || other.c == triangle.c) {
                unusedVertices.remove(vertices.get(triangle.c));
            }
        }


        // Remove the triangle from the list
        triangles.remove(index);

        // Remove the unused vertices from the list and update the indices
        for (Vector3i v : unusedVertices) {
            int indexToRemove = vertices.indexOf(v);
            for (VertexIndices t : triangles) {
                if (t.a > indexToRemove) {
                    t.a--;
                }
                if (t.b > indexToRemove) {
                    t.b--;
                }
                if (t.c > indexToRemove) {
                    t.c--;
                }
            }
            vertices.remove(v);
        }
    }

    // RS3

    public int highestUsedVertex;
    public int uvCoordsCount;

    public int[] texturesScaleX;
    public int[] texturesScaleY;
    public int[] texturesScaleZ;
    public int[] texturesSpeed;

    public byte[] texturesRotation;
    public byte[] texturesDirection;

    public int[] texturesUTrans;
    public int[] texturesVTrans;
    public int[] uvMappingVertexOffset;

    public byte[] uvFaceIndexA;
    public byte[] uvFaceIndexB;
    public byte[] uvFaceIndexC;

    public float[] uvFaceUCoord;
    public float[] uvFaceVCoord;

    public ModelEmitter[] emitters;
    public ModelBillboard[] billboards;
    public ModelEffector[] effectors;


}

