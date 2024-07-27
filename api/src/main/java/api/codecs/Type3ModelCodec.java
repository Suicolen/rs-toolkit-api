package api.codecs;

import api.Codec;
import api.definition.model.ModelType;
import api.definition.model.RSModel;
import api.io.InputBuffer;
import api.io.OutputBuffer;

import java.util.Arrays;

public class Type3ModelCodec implements Codec<InputBuffer, RSModel> {
    @Override
    public void decode(InputBuffer firstBuffer, RSModel model) {
        model.oldModel = true;
        model.decodeType = ModelType.TYPE_3;
        InputBuffer secondBuffer = firstBuffer.duplicate();
        InputBuffer thirdBuffer = firstBuffer.duplicate();
        InputBuffer fourthBuffer = firstBuffer.duplicate();
        InputBuffer fifthBuffer = firstBuffer.duplicate();
        InputBuffer sixthBuffer = firstBuffer.duplicate();
        InputBuffer seventhBuffer = firstBuffer.duplicate();
        firstBuffer.setPosition(firstBuffer.getLength() - 26);
        int vertexCount = firstBuffer.g2();
        int triangleCount = firstBuffer.g2();
        int texturedTriangleCount = firstBuffer.g1();

        model.vertexCount = vertexCount;
        model.triangleCount = triangleCount;
        model.texturedTriangleCount = texturedTriangleCount;

        int triangleInfoFlag = firstBuffer.g1();
        int priorityFlag = firstBuffer.g1();
        int alphaFlag = firstBuffer.g1();
        int triangleLabelFlag = firstBuffer.g1();
        int textureFlag = firstBuffer.g1();
        int vertexLabelFlag = firstBuffer.g1();
        int skeletalFlag = firstBuffer.g1();

        int verticesXLength = firstBuffer.g2();
        int verticesYLength = firstBuffer.g2();
        int verticesZLength = firstBuffer.g2();
        int triangleIndicesLength = firstBuffer.g2();
        int triangleTextureCoordinateIndicesLength = firstBuffer.g2();
        int skeletalLength = firstBuffer.g2();

        // type 0 = simple
        // type 1 = cylindrical mapping
        // type 2 = cube mapping
        // type 3 = spherical mapping
        int simpleTextureCount = 0;
        int complexTextureCount = 0;
        int cubeTextureCount = 0;
        if (texturedTriangleCount > 0) {
            model.textureTypes = new int[texturedTriangleCount];
            firstBuffer.setPosition(0);
            for (int index = 0; index < texturedTriangleCount; index++) {
                int textureType = model.textureTypes[index] = firstBuffer.g1s();
                if (textureType == 0) {
                    simpleTextureCount++;
                }

                if (textureType >= 1 && textureType <= 3) {
                    complexTextureCount++;
                }

                if (textureType == 2) {
                    cubeTextureCount++;
                }
            }
        }

        int pos = texturedTriangleCount + vertexCount;
        int triangleInfoOffset = pos;
        if (triangleInfoFlag == 1) {
            pos += triangleCount;
        }

        int triangleIndicesFlagsOffset = pos;
        pos += triangleCount;
        int trianglePrioritiesOffset = pos;
        if (priorityFlag == 255) {
            pos += triangleCount;
        }

        int triangleLabelsOffset = pos;
        if (triangleLabelFlag == 1) {
            pos += triangleCount;
        }

        int vertexLabelsOffset = pos;
        pos += skeletalLength;
        int triangleAlphaOffset = pos;
        if (alphaFlag == 1) {
            pos += triangleCount;
        }

        int triangleIndicesOffset = pos;
        pos += triangleIndicesLength;
        int triangleTextureIdsOffset = pos;
        if (textureFlag == 1) {
            pos += triangleCount * 2;
        }

        int textureCoordinateIndicesOffset = pos;
        pos += triangleTextureCoordinateIndicesLength;
        int triangleColorsOffset = pos;
        pos += triangleCount * 2;
        int verticesXOffset = pos;
        pos += verticesXLength;
        int verticesYOffset = pos;
        pos += verticesYLength;
        int verticesZOffset = pos;
        pos += verticesZLength;
        int textureVerticesOffset = pos;
        pos += simpleTextureCount * 6;
        int unused1Offset = pos;
        pos += complexTextureCount * 6;
        int unused2Offset = pos;
        pos += complexTextureCount * 6;
        int unused3Offset = pos;
        pos += complexTextureCount * 2;
        int unused4Offset = pos;
        pos += complexTextureCount;
        int unused5Offset = pos;
        pos = pos + complexTextureCount * 2 + cubeTextureCount * 2;
        model.vertexCount = vertexCount;
        model.triangleCount = triangleCount;
        model.texturedTriangleCount = texturedTriangleCount;
        model.verticesX = new int[vertexCount];
        model.verticesY = new int[vertexCount];
        model.verticesZ = new int[vertexCount];
        model.faceIndicesA = new int[triangleCount];
        model.faceIndicesB = new int[triangleCount];
        model.faceIndicesC = new int[triangleCount];
        if (vertexLabelFlag == 1) {
            model.vertexLabels = new int[vertexCount];
        }

        if (triangleInfoFlag == 1) {
            model.triangleInfo = new int[triangleCount];
        }

        if (priorityFlag == 255) {
            model.trianglePriorities = new byte[triangleCount];
        } else {
            model.modelPriority = (byte) priorityFlag;
        }

        if (alphaFlag == 1) {
            model.triangleAlpha = new int[triangleCount];
        }

        if (triangleLabelFlag == 1) {
            model.triangleLabels = new int[triangleCount];
        }

        if (textureFlag == 1) {
            model.triangleTextureIds = new int[triangleCount];
        }

        if (textureFlag == 1 && texturedTriangleCount > 0) {
            model.textureCoordinateIndices = new int[triangleCount];
        }

        if (skeletalFlag == 1) {
            model.skeletalBones = new int[vertexCount][];
            model.skeletalWeights = new int[vertexCount][];
        }

        model.triangleColors = new int[triangleCount];
        if (texturedTriangleCount > 0) {
            model.texturePCoordinate = new int[texturedTriangleCount];
            model.textureMCoordinate = new int[texturedTriangleCount];
            model.textureNCoordinate = new int[texturedTriangleCount];
        }

        firstBuffer.setPosition(texturedTriangleCount);
        secondBuffer.setPosition(verticesXOffset);
        thirdBuffer.setPosition(verticesYOffset);
        fourthBuffer.setPosition(verticesZOffset);
        fifthBuffer.setPosition(vertexLabelsOffset);

        int x = 0;
        int y = 0;
        int z = 0;

        for (int vertex = 0; vertex < vertexCount; vertex++) {
            int positionFlag = firstBuffer.g1();

            int dx = 0;
            int dy = 0;
            int dz = 0;

            if ((positionFlag & 1) != 0) {
                dx = secondBuffer.gSmart1or2s();
            }

            if ((positionFlag & 2) != 0) {
                dy = thirdBuffer.gSmart1or2s();
            }

            if ((positionFlag & 4) != 0) {
                dz = fourthBuffer.gSmart1or2s();
            }

            model.verticesX[vertex] = x + dx;
            model.verticesY[vertex] = y + dy;
            model.verticesZ[vertex] = z + dz;
            x = model.verticesX[vertex];
            y = model.verticesY[vertex];
            z = model.verticesZ[vertex];
            if (vertexLabelFlag == 1) {
                model.vertexLabels[vertex] = fifthBuffer.g1();
            }
        }

        if (skeletalFlag == 1) {
            for (int vertex = 0; vertex < vertexCount; vertex++) {
                int jointCount = fifthBuffer.g1();
                model.skeletalBones[vertex] = new int[jointCount];
                model.skeletalWeights[vertex] = new int[jointCount];

                for (int joint = 0; joint < jointCount; joint++) {
                    int boneIndex = fifthBuffer.g1();
                    int scale = fifthBuffer.g1();
                    model.skeletalBones[vertex][joint] = boneIndex;
                    model.skeletalWeights[vertex][joint] = scale;
                }
            }

        }

        firstBuffer.setPosition(triangleColorsOffset);
        secondBuffer.setPosition(triangleInfoOffset);
        thirdBuffer.setPosition(trianglePrioritiesOffset);
        fourthBuffer.setPosition(triangleAlphaOffset);
        fifthBuffer.setPosition(triangleLabelsOffset);
        sixthBuffer.setPosition(triangleTextureIdsOffset);
        seventhBuffer.setPosition(textureCoordinateIndicesOffset);

        for (int triangleIndex = 0; triangleIndex < triangleCount; triangleIndex++) {
            model.triangleColors[triangleIndex] = firstBuffer.g2();
            if (triangleInfoFlag == 1) {
                model.triangleInfo[triangleIndex] = secondBuffer.g1s();
            }

            if (priorityFlag == 255) {
                model.trianglePriorities[triangleIndex] = thirdBuffer.g1s();
            }

            if (alphaFlag == 1) {
                model.triangleAlpha[triangleIndex] = fourthBuffer.g1();
            }

            if (triangleLabelFlag == 1) {
                model.triangleLabels[triangleIndex] = fifthBuffer.g1();
            }

            if (textureFlag == 1) {
                model.triangleTextureIds[triangleIndex] = (short) (sixthBuffer.g2() - 1);
            }

            if (model.textureCoordinateIndices != null && model.triangleTextureIds[triangleIndex] != -1) {
                model.textureCoordinateIndices[triangleIndex] = (byte) (seventhBuffer.g1() - 1);
            }
        }

        firstBuffer.setPosition(triangleIndicesOffset);
        secondBuffer.setPosition(triangleIndicesFlagsOffset);

        int a = 0;
        int b = 0;
        int c = 0;
        int last = 0;

        for (int triangleIndex = 0; triangleIndex < triangleCount; triangleIndex++) {
            int orientation = secondBuffer.g1();
            if (orientation == 1) {
                a = firstBuffer.gSmart1or2s() + last;
                b = firstBuffer.gSmart1or2s() + a;
                c = firstBuffer.gSmart1or2s() + b;
                last = c;
                model.faceIndicesA[triangleIndex] = a;
                model.faceIndicesB[triangleIndex] = b;
                model.faceIndicesC[triangleIndex] = c;
            }

            if (orientation == 2) {
                b = c;
                c = firstBuffer.gSmart1or2s() + last;
                last = c;
                model.faceIndicesA[triangleIndex] = a;
                model.faceIndicesB[triangleIndex] = b;
                model.faceIndicesC[triangleIndex] = c;
            }

            if (orientation == 3) {
                a = c;
                c = firstBuffer.gSmart1or2s() + last;
                last = c;
                model.faceIndicesA[triangleIndex] = a;
                model.faceIndicesB[triangleIndex] = b;
                model.faceIndicesC[triangleIndex] = c;
            }

            if (orientation == 4) {
                int tmp = a;
                a = b;
                b = tmp;
                c = firstBuffer.gSmart1or2s() + last;
                last = c;
                model.faceIndicesA[triangleIndex] = a;
                model.faceIndicesB[triangleIndex] = tmp;
                model.faceIndicesC[triangleIndex] = c;
            }
        }

        firstBuffer.setPosition(textureVerticesOffset);
        secondBuffer.setPosition(unused1Offset);
        thirdBuffer.setPosition(unused2Offset);
        fourthBuffer.setPosition(unused3Offset);
        fifthBuffer.setPosition(unused4Offset);
        sixthBuffer.setPosition(unused5Offset);

        for (int index = 0; index < texturedTriangleCount; index++) {
            int textureType = model.textureTypes[index] & 255;
            if (textureType == 0) {
                model.texturePCoordinate[index] = (short) firstBuffer.g2();
                model.textureMCoordinate[index] = (short) firstBuffer.g2();
                model.textureNCoordinate[index] = (short) firstBuffer.g2();
            }
        }

        // unused data
        /*firstBuffer.setPosition(var28);
        var55 = firstBuffer.readUnsignedByte();
        if (var55 != 0) {
            firstBuffer.readUnsignedShort();
            firstBuffer.readUnsignedShort();
            firstBuffer.readUnsignedShort();
            firstBuffer.readInt();
        }*/

    }

    @Override
    public byte[] encode(RSModel model) {
        OutputBuffer buffer = new OutputBuffer(10000);
        OutputBuffer textureMapBuffer = new OutputBuffer(0);
        OutputBuffer vertexFlagsBuffer = new OutputBuffer(0);
        OutputBuffer triangleInfoBuffer = new OutputBuffer(0);
        OutputBuffer triangleIndicesFlagsBuffer = new OutputBuffer(0);
        OutputBuffer trianglePrioritiesBuffer = new OutputBuffer(0);
        OutputBuffer triangleLabelsBuffer = new OutputBuffer(0);
        OutputBuffer vertexSkinBuffer = new OutputBuffer(0);
        OutputBuffer triangleAlphasBuffer = new OutputBuffer(0);
        OutputBuffer triangleIndicesBuffer = new OutputBuffer(0);
        OutputBuffer triangleMaterialsBuffer = new OutputBuffer(0);
        OutputBuffer faceTexturesBuffer = new OutputBuffer(0);
        OutputBuffer triangleColorsBuffer = new OutputBuffer(0);
        OutputBuffer verticesXBuffer = new OutputBuffer(0);
        OutputBuffer verticesYBuffer = new OutputBuffer(0);
        OutputBuffer verticesZBuffer = new OutputBuffer(0);
        OutputBuffer textureVerticesBuffer = new OutputBuffer(0);
        OutputBuffer footerBuffer = new OutputBuffer(0);

        OutputBuffer[] buffers = {
                textureMapBuffer, vertexFlagsBuffer, triangleInfoBuffer, triangleIndicesFlagsBuffer, trianglePrioritiesBuffer,
                triangleLabelsBuffer, vertexSkinBuffer, triangleAlphasBuffer, triangleIndicesBuffer,
                triangleMaterialsBuffer, faceTexturesBuffer, triangleColorsBuffer,
                verticesXBuffer, verticesYBuffer, verticesZBuffer, textureVerticesBuffer, footerBuffer
        };
        if (model.texturedTriangleCount > 0) {
            // because osrs doesn't support anything other than "simple" textures
            if (model.textureTypes != null) {
                Arrays.fill(model.textureTypes, (short) 0);
            }
            for (int face = 0; face < model.texturedTriangleCount; face++) {
                textureMapBuffer.p1(model.textureTypes == null ? 0 : model.textureTypes[face]);
            }
        }


        boolean hasVertexLabels = model.vertexLabels != null;
        boolean hasSkeletalBones = model.skeletalBones != null;
        int baseX = 0, baseY = 0, baseZ = 0;
        for (int vertex = 0; vertex < model.vertexCount; vertex++) {
            int x = model.verticesX[vertex];
            int y = model.verticesY[vertex];
            int z = model.verticesZ[vertex];
            int xOffset = x - baseX;
            int yOffset = y - baseY;
            int zOffset = z - baseZ;
            int flag = 0;
            if (xOffset != 0) {
                verticesXBuffer.pSmart1or2s(xOffset);
                flag |= 0x1;
            }
            if (yOffset != 0) {
                verticesYBuffer.pSmart1or2s(yOffset);
                flag |= 0x2;
            }
            if (zOffset != 0) {
                verticesZBuffer.pSmart1or2s(zOffset);
                flag |= 0x4;
            }
            vertexFlagsBuffer.p1(flag);
            model.verticesX[vertex] = baseX + xOffset;
            model.verticesY[vertex] = baseY + yOffset;
            model.verticesZ[vertex] = baseZ + zOffset;
            baseX = model.verticesX[vertex];
            baseY = model.verticesY[vertex];
            baseZ = model.verticesZ[vertex];
            if (hasVertexLabels) {
                int label = model.vertexLabels[vertex];
                vertexSkinBuffer.p1(label);
            }

        }

        if (hasSkeletalBones) {
            int[][] skeletalBones = model.skeletalBones;
            int[][] skeletalScales = model.skeletalWeights;
            for (int vertex = 0; vertex < model.vertexCount; vertex++) {
                int boneCount = skeletalBones[vertex].length;
                vertexSkinBuffer.p1(boneCount);
                for (int boneIndex = 0; boneIndex < boneCount; boneIndex++) {
                    vertexSkinBuffer.p1(skeletalBones[vertex][boneIndex]);
                    vertexSkinBuffer.p1(skeletalScales[vertex][boneIndex]);
                }
            }
        }

        boolean hasTriangleInfo = model.triangleInfo != null;
        boolean hasTrianglePriorities = model.trianglePriorities != null;
        boolean hasTriangleAlpha = model.triangleAlpha != null;
        boolean hasTriangleLabels = model.triangleLabels != null;
        boolean hasTriangleMaterial = model.triangleTextureIds != null;
        boolean hasTextureCoordinateIndices = model.textureCoordinateIndices != null;
        for (int face = 0; face < model.triangleCount; face++) {
            triangleColorsBuffer.p2(model.triangleColors[face]);
            if (hasTriangleInfo) {
                triangleInfoBuffer.p1(model.triangleInfo[face]);
            }
            if (hasTrianglePriorities) {
                trianglePrioritiesBuffer.p1(model.trianglePriorities[face]);
            }
            if (hasTriangleAlpha) {
                triangleAlphasBuffer.p1(model.triangleAlpha[face]);
            }
            if (hasTriangleLabels) {
                int label = model.triangleLabels[face];
                triangleLabelsBuffer.p1(label);
            }
            if (hasTriangleMaterial) {
                triangleMaterialsBuffer.p2(model.triangleTextureIds[face] + 1);
            }
            if (hasTextureCoordinateIndices) {
                if (model.triangleTextureIds[face] != -1) {
                    faceTexturesBuffer.p1(model.textureCoordinateIndices[face] + 1);
                }
            }
        }
        encodeIndices(model, triangleIndicesBuffer, triangleIndicesFlagsBuffer);
        encodeMapping(model, textureVerticesBuffer);
        footerBuffer.p2(model.vertexCount);
        footerBuffer.p2(model.triangleCount);
        footerBuffer.p1(model.texturedTriangleCount);
        int flags = 0;
        if (hasTriangleInfo) {
            flags |= 0x1;
        }

        footerBuffer.p1(flags);
        footerBuffer.p1(hasTrianglePriorities ? -1 : model.modelPriority);
        footerBuffer.pBoolean(hasTriangleAlpha);
        footerBuffer.pBoolean(hasTriangleLabels);
        footerBuffer.pBoolean(hasTriangleMaterial);
        footerBuffer.pBoolean(hasVertexLabels);
        footerBuffer.pBoolean(hasSkeletalBones);
        footerBuffer.p2(verticesXBuffer.getPosition());
        footerBuffer.p2(verticesYBuffer.getPosition());
        footerBuffer.p2(verticesZBuffer.getPosition());
        footerBuffer.p2(triangleIndicesBuffer.getPosition());
        footerBuffer.p2(faceTexturesBuffer.getPosition());
        footerBuffer.p2(vertexSkinBuffer.getPosition());
        Arrays.stream(buffers).forEach(buffer::putBytes);

        buffer.p1(255);
        buffer.p1(253);
        byte[] data = new byte[buffer.getPosition()];
        buffer.getBytes(data);
        return data;
    }

    private void encodeMapping(RSModel model, OutputBuffer simple) {
        for (int face = 0; face < model.texturedTriangleCount; face++) {
            int type = model.textureTypes[face] & 0xff;
            // always true but keeping it that way, so it's consistent with the decoder
            if (type == 0) {
                simple.p2(model.texturePCoordinate[face]);
                simple.p2(model.textureMCoordinate[face]);
                simple.p2(model.textureNCoordinate[face]);
            }
        }
    }

    private void encodeIndices(RSModel model, OutputBuffer triangleIndicesBuffer, OutputBuffer triangleIndicesFlagsBuffer) {
        int lastA = 0;
        int lastB = 0;
        int lastC = 0;
        int pAcc = 0;
        for (int face = 0; face < model.triangleCount; face++) {
            int currentA = model.faceIndicesA[face];
            int currentB = model.faceIndicesB[face];
            int currentC = model.faceIndicesC[face];
            if (currentA == lastB && currentB == lastA && currentC != lastC) {
                triangleIndicesFlagsBuffer.p1(4);
                triangleIndicesBuffer.pSmart1or2s(currentC - pAcc);
                int back = lastA;
                lastA = lastB;
                lastB = back;
                pAcc = lastC = currentC;
            } else if (currentA == lastC && currentB == lastB && currentC != lastC) {
                triangleIndicesFlagsBuffer.p1(3);
                triangleIndicesBuffer.pSmart1or2s(currentC - pAcc);
                lastA = lastC;
                pAcc = lastC = currentC;
            } else if (currentA == lastA && currentB == lastC && currentC != lastC) {
                triangleIndicesFlagsBuffer.p1(2);
                triangleIndicesBuffer.pSmart1or2s(currentC - pAcc);
                lastB = lastC;
                pAcc = lastC = currentC;
            } else {
                triangleIndicesFlagsBuffer.p1(1);
                triangleIndicesBuffer.pSmart1or2s(currentA - pAcc);
                triangleIndicesBuffer.pSmart1or2s(currentB - currentA);
                triangleIndicesBuffer.pSmart1or2s(currentC - currentB);
                lastA = currentA;
                lastB = currentB;
                pAcc = lastC = currentC;
            }
        }
    }

}
