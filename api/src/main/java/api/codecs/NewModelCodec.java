package api.codecs;

import api.Codec;
import api.definition.model.ModelType;
import api.definition.model.RSModel;
import api.io.InputBuffer;
import api.io.OutputBuffer;
import one.util.streamex.IntStreamEx;

import java.util.Arrays;

// this isn't pure OSRS but will work with all osrs models
public class NewModelCodec implements Codec<InputBuffer, RSModel> {
    @Override
    public void decode(InputBuffer first, RSModel model) {
        model.oldModel = false;
        model.decodeType = ModelType.NEW;
        InputBuffer second = first.duplicate();
        InputBuffer third = first.duplicate();
        InputBuffer fourth = first.duplicate();
        InputBuffer fifth = first.duplicate();
        InputBuffer sixth = first.duplicate();
        InputBuffer seventh = first.duplicate();

        first.setPosition(first.getLength() - 23);
        int vertexCount = first.g2();
        int triangleCount = first.g2();
        int texturedTriangleCount = first.g1();

        model.vertexCount = vertexCount;
        model.triangleCount = triangleCount;
        model.texturedTriangleCount = texturedTriangleCount;

        int triangleInfoFlag = first.g1();//texture flag 00 false, 01+ true
        boolean hasFaceTypes = (triangleInfoFlag & 0x1) == 1;
        boolean hasParticleEffects = (triangleInfoFlag & 0x2) == 2;
        boolean hasBillboards = (triangleInfoFlag & 0x4) == 4;
        boolean hasVersion = (triangleInfoFlag & 0x8) == 8;

        boolean hasLargeVertexLabels = (triangleInfoFlag & 0x10) == 16;
        boolean hasLargeTriangleSkins = (triangleInfoFlag & 0x20) == 32;
        boolean hasLargeBillboardGroups = (triangleInfoFlag & 0x40) == 64;

        if (hasVersion) {
            first.decrementPosition(7);
            model.version = first.g1();
            first.skip(6);
        }

        int trianglePriorityFlag = first.g1();
        int triangleAlphaFlag = first.g1();
        int triangleLabelFlag = first.g1();
        int triangleTextureIdsFlag = first.g1();
        int vertexLabelFlag = first.g1();

        int verticesXLength = first.g2();
        int verticesYLength = first.g2();
        int verticesZLength = first.g2();
        int triangleIndicesLength = first.g2();
        int triangleTextureCoordinateIndicesLength = first.g2();

        /*System.out.printf("Footer data: %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d%n", vertexCount, triangleCount, texturedTriangleCount,
                triangleInfoFlag, trianglePriorityFlag, triangleAlphaFlag, triangleLabelFlag, triangleTextureIdsFlag, vertexLabelFlag,
                verticesXLength, verticesYLength, verticesZLength, triangleIndicesLength, triangleTextureCoordinateIndicesLength);*/

        if (hasLargeVertexLabels) {
            int unknown1 = first.g2();
        }

        if (hasLargeTriangleSkins) {
            int unknown2 = first.g2();
        }

        int simpleTextureCount = 0;
        int complexTextureCount = 0;
        int cubeTextureCount = 0;
        if (texturedTriangleCount > 0) {
            model.textureTypes = new int[texturedTriangleCount];
            first.setPosition(0);
            for (int triangleIndex = 0; triangleIndex < texturedTriangleCount; triangleIndex++) {
                int type = model.textureTypes[triangleIndex] = first.g1s();
                if (type == 0) {
                    simpleTextureCount++;
                }
                if (type >= 1 && type <= 3) {
                    complexTextureCount++;
                }
                if (type == 2) {
                    cubeTextureCount++;
                }

            }
        }

        int pos = texturedTriangleCount;

        int vertexFlagsOffset = pos;
        pos += vertexCount;

        int triangleInfosOffset = pos;
        if (triangleInfoFlag == 1)
            pos += triangleCount;

        int triangleIndicesFlagsOffset = pos;
        pos += triangleCount;

        int trianglePrioritiesOffset = pos;
        if (trianglePriorityFlag == 255)
            pos += triangleCount;

        int triangleLabelsOffset = pos;
        if (triangleLabelFlag == 1)
            pos += triangleCount;

        int vertexLabelsOffset = pos;
        if (vertexLabelFlag == 1)
            pos += vertexCount;

        int triangleAlphaOffset = pos;
        if (triangleAlphaFlag == 1)
            pos += triangleCount;

        int triangleIndicesOffset = pos;
        pos += triangleIndicesLength;

        int triangleTextureIdsOffset = pos;
        if (triangleTextureIdsFlag == 1)
            pos += triangleCount * 2;

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
        pos += complexTextureCount * 2 + cubeTextureCount * 2;

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

        if (trianglePriorityFlag == 255) {
            model.trianglePriorities = new byte[triangleCount];
        } else {
            model.modelPriority = (byte) trianglePriorityFlag;
        }

        if (triangleAlphaFlag == 1) {
            model.triangleAlpha = new int[triangleCount];
        }

        if (triangleLabelFlag == 1) {
            model.triangleLabels = new int[triangleCount];
        }
        if (triangleTextureIdsFlag == 1) {
            model.triangleTextureIds = new int[triangleCount];
        }

        //System.out.println("Triangle materials flag: " + triangleTextureIdsFlag + " and " + texturedTriangleCount + " for " + data.length);

        if (triangleTextureIdsFlag == 1 && texturedTriangleCount > 0) {
            model.textureCoordinateIndices = new int[triangleCount];
        }
        model.triangleColors = new int[triangleCount];
        if (texturedTriangleCount > 0) {
            model.texturePCoordinate = new int[texturedTriangleCount];
            model.textureMCoordinate = new int[texturedTriangleCount];
            model.textureNCoordinate = new int[texturedTriangleCount];
        }

        first.setPosition(vertexFlagsOffset);
        second.setPosition(verticesXOffset);
        third.setPosition(verticesYOffset);
        fourth.setPosition(verticesZOffset);
        fifth.setPosition(vertexLabelsOffset);
        //System.out.println(vertexFlagsOffset + ", " + verticesXOffset + ", " + verticesYOffset + ", " + verticesZOffset + ", " + vertexLabelsOffset);
        int start_x = 0;
        int start_y = 0;

        int start_z = 0;
        for (int point = 0; point < vertexCount; point++) {
            int positionFlag = first.g1();
            int xOff = 0;
            if ((positionFlag & 1) != 0) {
                xOff = second.gSmart1or2s();
            }
            int yOff = 0;
            if ((positionFlag & 2) != 0) {
                yOff = third.gSmart1or2s();
            }
            int zOff = 0;
            if ((positionFlag & 4) != 0) {
                zOff = fourth.gSmart1or2s();
            }
            model.verticesX[point] = start_x + xOff;
            model.verticesY[point] = start_y + yOff;
            model.verticesZ[point] = start_z + zOff;
            start_x = model.verticesX[point];
            start_y = model.verticesY[point];
            start_z = model.verticesZ[point];
            if (model.vertexLabels != null) {
                model.vertexLabels[point] = fifth.g1();
            }

        }

        first.setPosition(triangleColorsOffset);
        second.setPosition(triangleInfosOffset);
        third.setPosition(trianglePrioritiesOffset);
        fourth.setPosition(triangleAlphaOffset);
        fifth.setPosition(triangleLabelsOffset);
        sixth.setPosition(triangleTextureIdsOffset);
        seventh.setPosition(textureCoordinateIndicesOffset);
        for (int triangleIndex = 0; triangleIndex < triangleCount; triangleIndex++) {
            model.triangleColors[triangleIndex] = first.g2();

            if (triangleInfoFlag == 1) {
                model.triangleInfo[triangleIndex] = second.g1s();
            }
            if (trianglePriorityFlag == 255) {
                model.trianglePriorities[triangleIndex] = third.g1s();
            }
            if (triangleAlphaFlag == 1) {
                model.triangleAlpha[triangleIndex] = fourth.g1();
            }
            if (triangleLabelFlag == 1) {
                model.triangleLabels[triangleIndex] = fifth.g1();
            }

            if (triangleTextureIdsFlag == 1) {
                model.triangleTextureIds[triangleIndex] = (short) (sixth.g2() - 1);
            }

            if (model.textureCoordinateIndices != null && model.triangleTextureIds[triangleIndex] != -1) {
                model.textureCoordinateIndices[triangleIndex] = (byte) (seventh.g1() - 1);
            }
        }

        first.setPosition(triangleIndicesOffset);
        second.setPosition(triangleIndicesFlagsOffset);
        int a = 0;
        int b = 0;
        int c = 0;
        int last = 0;
        for (int triangleIndex = 0; triangleIndex < triangleCount; triangleIndex++) {
            int opcode = second.g1();
            if (opcode == 1) {
                a = first.gSmart1or2s() + last;
                last = a;
                b = first.gSmart1or2s() + last;
                last = b;
                c = first.gSmart1or2s() + last;
                last = c;
                model.faceIndicesA[triangleIndex] = a;
                model.faceIndicesB[triangleIndex] = b;
                model.faceIndicesC[triangleIndex] = c;
            }
            if (opcode == 2) {
                b = c;
                c = first.gSmart1or2s() + last;
                last = c;
                model.faceIndicesA[triangleIndex] = a;
                model.faceIndicesB[triangleIndex] = b;
                model.faceIndicesC[triangleIndex] = c;
            }
            if (opcode == 3) {
                a = c;
                c = first.gSmart1or2s() + last;
                last = c;
                model.faceIndicesA[triangleIndex] = a;
                model.faceIndicesB[triangleIndex] = b;
                model.faceIndicesC[triangleIndex] = c;
            }
            if (opcode == 4) {
                int l14 = a;
                a = b;
                b = l14;
                c = first.gSmart1or2s() + last;
                last = c;
                model.faceIndicesA[triangleIndex] = a;
                model.faceIndicesB[triangleIndex] = b;
                model.faceIndicesC[triangleIndex] = c;
            }
        }

        first.setPosition(textureVerticesOffset);
        second.setPosition(unused1Offset);
        third.setPosition(unused2Offset);
        fourth.setPosition(unused3Offset);
        fifth.setPosition(unused4Offset);
        sixth.setPosition(unused5Offset);
        for (int triangleIndex = 0; triangleIndex < texturedTriangleCount; triangleIndex++) {
            int opcode = model.textureTypes[triangleIndex] & 0xff;
            if (opcode == 0) {
                model.texturePCoordinate[triangleIndex] = (short) first.g2();
                model.textureMCoordinate[triangleIndex] = (short) first.g2();
                model.textureNCoordinate[triangleIndex] = (short) first.g2();
            }
        }

        // unnecessary
        //first.pos = pos;
       /* face = first.readUnsignedByte();
        if (face != 0) {
            first.readUnsignedShort();
            first.readUnsignedShort();
            first.readUnsignedShort();
            first.getInt();
        }*/
    }

    @Override
    public byte[] encode(RSModel model) {
        OutputBuffer master = new OutputBuffer(16 * 1024);
        OutputBuffer textureMapBuffer = new OutputBuffer(0);
        OutputBuffer vertexFlagsBuffer = new OutputBuffer(0);
        OutputBuffer triangleInfosBuffer = new OutputBuffer(0);
        OutputBuffer triangleIndicesFlagsBuffer = new OutputBuffer(0);
        OutputBuffer trianglePrioritiesBuffer = new OutputBuffer(0);
        OutputBuffer triangleLabelsBuffer = new OutputBuffer(0);
        OutputBuffer vertexLabelsBuffer = new OutputBuffer(0);
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
                textureMapBuffer,
                vertexFlagsBuffer,
                triangleInfosBuffer,
                triangleIndicesFlagsBuffer,
                trianglePrioritiesBuffer,
                triangleLabelsBuffer,
                vertexLabelsBuffer,
                triangleAlphasBuffer,
                triangleIndicesBuffer,
                triangleMaterialsBuffer,
                faceTexturesBuffer,
                triangleColorsBuffer,
                verticesXBuffer,
                verticesYBuffer,
                verticesZBuffer,
                textureVerticesBuffer,
                footerBuffer
        };
        if (model.texturedTriangleCount > 0) {
            if (model.textureTypes == null) {
                model.textureTypes = new int[model.texturedTriangleCount];
                Arrays.fill(model.textureTypes, (short) 0);
            }
            for (int face = 0; face < model.texturedTriangleCount; face++) {
                textureMapBuffer.p1(model.textureTypes == null ? 0 : model.textureTypes[face]);
            }
        }


        boolean hasVertexSkins = model.vertexLabels != null;
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
            if (hasVertexSkins) {
                int weight = model.vertexLabels[vertex];
                vertexLabelsBuffer.p1(weight);
            }
        }

        boolean hasTriangleInfo = model.triangleInfo != null;
        boolean hasTrianglePriorities = model.trianglePriorities != null;
        boolean hasFaceAlpha = model.triangleAlpha != null;
        boolean hasTriangleLabels = model.triangleLabels != null;
        boolean hasFaceTextures = model.triangleTextureIds != null && IntStreamEx.of(model.triangleTextureIds)
                .anyMatch(x -> x != -1);
        for (int face = 0; face < model.triangleCount; face++) {
            triangleColorsBuffer.p2(model.triangleColors[face]);
            if (hasTriangleInfo) {
                triangleInfosBuffer.p1(model.triangleInfo[face]);
            }
            if (hasTrianglePriorities) {
                trianglePrioritiesBuffer.p1(model.trianglePriorities[face]);
            }
            if (hasFaceAlpha) {
                triangleAlphasBuffer.p1(model.triangleAlpha[face]);
            }
            if (hasTriangleLabels) {
                int label = model.triangleLabels[face];
                triangleLabelsBuffer.p1(label);
            }
            if (hasFaceTextures) {
                triangleMaterialsBuffer.p2(model.triangleTextureIds[face] + 1);
            }
            if (model.textureCoordinateIndices != null) {
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
        footerBuffer.pBoolean(hasFaceAlpha);
        footerBuffer.pBoolean(hasTriangleLabels);
        footerBuffer.pBoolean(hasFaceTextures);
        footerBuffer.pBoolean(hasVertexSkins);
        footerBuffer.p2(verticesXBuffer.getPosition());
        footerBuffer.p2(verticesYBuffer.getPosition());
        footerBuffer.p2(verticesZBuffer.getPosition());
        footerBuffer.p2(triangleIndicesBuffer.getPosition());
        footerBuffer.p2(faceTexturesBuffer.getPosition());
        Arrays.stream(buffers).forEach(master::putBytes);
        master.p1(255);
        master.p1(255);
        byte[] data = new byte[master.getPosition()];
        master.getBytes(data);
        return data;
    }

    private void encodeMapping(RSModel model, OutputBuffer simple) {
        for (int face = 0; face < model.texturedTriangleCount; face++) {
            int type = model.textureTypes[face] & 0xff;
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
