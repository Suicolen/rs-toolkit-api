package api.codecs;

import api.Codec;
import api.definition.model.ModelType;
import api.definition.model.RSModel;
import api.io.InputBuffer;
import api.io.OutputBuffer;

import java.util.Arrays;

public class OldModelCodec implements Codec<InputBuffer, RSModel> {
    @Override
    public void decode(InputBuffer first, RSModel model) {
        model.oldModel = true;
        model.decodeType = ModelType.OLD;
        InputBuffer second = first.duplicate();
        InputBuffer third = first.duplicate();
        InputBuffer fourth = first.duplicate();
        InputBuffer fifth = first.duplicate();
        first.setPosition(first.getLength() - 18);
        int vertexCount = first.g2();
        int triangleCount = first.g2();
        int texturedTriangleCount = first.g1();

        model.vertexCount = vertexCount;
        model.triangleCount = triangleCount;
        model.texturedTriangleCount = texturedTriangleCount;


        int triangleInfoFlag = first.g1();
        int trianglePriorityFlag = first.g1();
        int triangleAlphaFlag = first.g1();
        int triangleLabelFlag = first.g1();
        int vertexLabelFlag = first.g1();

        int verticesXLength = first.g2();
        int verticesYLength = first.g2();
        int verticesZLength = first.g2();
        int triangleIndicesLength = first.g2();

        int pos = 0;

        int vertexFlagOffset = pos;
        pos += vertexCount;

        int triangleIndicesFlagsOffset = pos;
        pos += triangleCount;

        int trianglePriorityOffset = pos;
        if (trianglePriorityFlag == 255) {
            pos += triangleCount;
        }

        int triangleLabelOffset = pos;
        if (triangleLabelFlag == 1) {
            pos += triangleCount;
        }

        int triangleInfoOffset = pos;
        if (triangleInfoFlag == 1) {
            pos += triangleCount;
        }

        int vertexLabelOffset = pos;
        if (vertexLabelFlag == 1) {
            pos += vertexCount;
        }

        int triangleAlphaOffset = pos;
        if (triangleAlphaFlag == 1) {
            pos += triangleCount;
        }

        int triangleIndicesOffset = pos;
        pos += triangleIndicesLength;

        int triangleColorOffset = pos;
        pos += triangleCount * 2;

        int textureVerticesOffset = pos;
        pos += texturedTriangleCount * 6;

        int verticesXOffset = pos;
        pos += verticesXLength;

        int verticesYOffset = pos;
        pos += verticesYLength;

        int verticesZOffset = pos;

        model.verticesX = new int[vertexCount];
        model.verticesY = new int[vertexCount];
        model.verticesZ = new int[vertexCount];
        model.faceIndicesA = new int[triangleCount];
        model.faceIndicesB = new int[triangleCount];
        model.faceIndicesC = new int[triangleCount];
        if (texturedTriangleCount > 0) {
            model.textureTypes = new int[texturedTriangleCount];
            model.texturePCoordinate = new int[texturedTriangleCount];
            model.textureMCoordinate = new int[texturedTriangleCount];
            model.textureNCoordinate = new int[texturedTriangleCount];
        }

        if (vertexLabelFlag == 1) {
            model.vertexLabels = new int[vertexCount];
        }


        if (triangleInfoFlag == 1) {
            model.triangleInfo = new int[triangleCount];
            //model.faceTexture = new short[triangleCount];
            //model.triangleMaterial = new int[triangleCount];
            //model.faceTextureMasks = new byte[triangleCount];
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

        model.triangleColors = new int[triangleCount];
        first.setPosition(vertexFlagOffset);
        second.setPosition(verticesXOffset);
        third.setPosition(verticesYOffset);
        fourth.setPosition(verticesZOffset);
        fifth.setPosition(vertexLabelOffset);
        int baseX = 0;
        int baseY = 0;
        int baseZ = 0;

        for (int point = 0; point < vertexCount; point++) {
            int flag = first.g1();

            int x = 0;
            if ((flag & 0x1) != 0) {
                x = second.gSmart1or2s();
            }

            int y = 0;
            if ((flag & 0x2) != 0) {
                y = third.gSmart1or2s();
            }
            int z = 0;
            if ((flag & 0x4) != 0) {
                z = fourth.gSmart1or2s();
            }

            model.verticesX[point] = baseX + x;
            model.verticesY[point] = baseY + y;
            model.verticesZ[point] = baseZ + z;

            baseX = model.verticesX[point];
            baseY = model.verticesY[point];
            baseZ = model.verticesZ[point];
            if (vertexLabelFlag == 1) {
                model.vertexLabels[point] = fifth.g1();
            }
        }

        first.setPosition(triangleColorOffset);
        second.setPosition(triangleInfoOffset);
        third.setPosition(trianglePriorityOffset);
        fourth.setPosition(triangleAlphaOffset);
        fifth.setPosition(triangleLabelOffset);

        for (int face = 0; face < triangleCount; face++) {
            int color = first.g2();
            model.triangleColors[face] = color;

            if (triangleInfoFlag == 1) {
                model.triangleInfo[face] = second.g1();
            }
            if (trianglePriorityFlag == 255) {
                model.trianglePriorities[face] = third.g1s();
            }

            if (triangleAlphaFlag == 1) {
                model.triangleAlpha[face] = fourth.g1();

            }
            if (triangleLabelFlag == 1) {
                model.triangleLabels[face] = fifth.g1();
            }

        }
        first.setPosition(triangleIndicesOffset);
        second.setPosition(triangleIndicesFlagsOffset);
        int a = 0;
        int b = 0;
        int c = 0;
        int offset = 0;
        int coordinate;

        for (int face = 0; face < triangleCount; face++) {
            int opcode = second.g1();


            if (opcode == 1) {
                a = (first.gSmart1or2s() + offset);
                offset = a;
                b = (first.gSmart1or2s() + offset);
                offset = b;
                c = (first.gSmart1or2s() + offset);
                offset = c;
                model.faceIndicesA[face] = a;
                model.faceIndicesB[face] = b;
                model.faceIndicesC[face] = c;

            }
            if (opcode == 2) {
                b = c;
                c = (first.gSmart1or2s() + offset);
                offset = c;
                model.faceIndicesA[face] = a;
                model.faceIndicesB[face] = b;
                model.faceIndicesC[face] = c;
            }
            if (opcode == 3) {
                a = c;
                c = (first.gSmart1or2s() + offset);
                offset = c;
                model.faceIndicesA[face] = a;
                model.faceIndicesB[face] = b;
                model.faceIndicesC[face] = c;
            }
            if (opcode == 4) {
                coordinate = a;
                a = b;
                b = coordinate;
                c = (first.gSmart1or2s() + offset);
                offset = c;
                model.faceIndicesA[face] = a;
                model.faceIndicesB[face] = b;
                model.faceIndicesC[face] = c;
            }

        }
        first.setPosition(textureVerticesOffset);

        for (int face = 0; face < texturedTriangleCount; face++) {
            model.textureTypes[face] = 0;
            model.texturePCoordinate[face] = (short) first.g2();
            model.textureMCoordinate[face] = (short) first.g2();
            model.textureNCoordinate[face] = (short) first.g2();
        }

    }

    @Override
    public byte[] encode(RSModel model) {
        OutputBuffer masterBuffer = new OutputBuffer(10000);
        OutputBuffer vertexFlagsBuffer = new OutputBuffer();
        OutputBuffer triangleInfoBuffer = new OutputBuffer();
        OutputBuffer faceIndicesFlagsBuffer = new OutputBuffer();
        OutputBuffer trianglePrioritiesBuffer = new OutputBuffer();
        OutputBuffer triangleLabelBuffer = new OutputBuffer();
        OutputBuffer vertexLabelBuffer = new OutputBuffer();
        OutputBuffer faceAlphasBuffer = new OutputBuffer();
        OutputBuffer triangleIndicesBuffer = new OutputBuffer();
        OutputBuffer triangleColorsBuffer = new OutputBuffer();
        OutputBuffer verticesXBuffer = new OutputBuffer();
        OutputBuffer verticesYBuffer = new OutputBuffer();
        OutputBuffer verticesZBuffer = new OutputBuffer();
        OutputBuffer texturesBuffer = new OutputBuffer();
        OutputBuffer footerBuffer = new OutputBuffer();

        OutputBuffer[] buffers = {vertexFlagsBuffer,
                faceIndicesFlagsBuffer, trianglePrioritiesBuffer,
                triangleLabelBuffer, triangleInfoBuffer, vertexLabelBuffer, faceAlphasBuffer,
                triangleIndicesBuffer,
                triangleColorsBuffer,
                texturesBuffer, verticesXBuffer,
                verticesYBuffer, verticesZBuffer,
                footerBuffer
        };

        boolean hasVertexLabels = model.vertexLabels != null;

        int baseX = 0;
        int baseY = 0;
        int baseZ = 0;

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
                vertexLabelBuffer.p1(label);
            }
        }


        boolean hasTriangleInfo = model.triangleInfo != null;
        boolean hasTrianglePriorities = model.trianglePriorities != null;
        boolean hasTriangleAlpha = model.triangleAlpha != null;
        boolean hasTriangleSkins = model.triangleLabels != null;

        for (int face = 0; face < model.triangleCount; face++) {
            triangleColorsBuffer.p2(model.triangleColors[face]);

            if (hasTriangleInfo) {
                triangleInfoBuffer.p1(model.triangleInfo[face]);
            }

            if (hasTrianglePriorities) {
                trianglePrioritiesBuffer.p1(model.trianglePriorities[face]);
            }
            if (hasTriangleAlpha) {
                faceAlphasBuffer.p1(model.triangleAlpha[face]);
            }

            if (hasTriangleSkins) {
                int weight = model.triangleLabels[face];
                triangleLabelBuffer.p1(weight);
            }
        }

        int lastA = 0;
        int lastB = 0;
        int lastC = 0;
        int pAcc = 0;

        // share edge info to save space
        for (int face = 0; face < model.triangleCount; face++) {
            int currentA = model.faceIndicesA[face];
            int currentB = model.faceIndicesB[face];
            int currentC = model.faceIndicesC[face];
            if (currentA == lastB && currentB == lastA && currentC != lastC) {
                faceIndicesFlagsBuffer.p1(4);
                triangleIndicesBuffer.pSmart1or2s(currentC - pAcc);
                int back = lastA;
                lastA = lastB;
                lastB = back;
                pAcc = lastC = currentC;
            } else if (currentA == lastC && currentB == lastB && currentC != lastC) {
                faceIndicesFlagsBuffer.p1(3);
                triangleIndicesBuffer.pSmart1or2s(currentC - pAcc);
                lastA = lastC;
                pAcc = lastC = currentC;
            } else if (currentA == lastA && currentB == lastC && currentC != lastC) {
                faceIndicesFlagsBuffer.p1(2);
                triangleIndicesBuffer.pSmart1or2s(currentC - pAcc);
                lastB = lastC;
                pAcc = lastC = currentC;
            } else {
                faceIndicesFlagsBuffer.p1(1);
                triangleIndicesBuffer.pSmart1or2s(currentA - pAcc);
                triangleIndicesBuffer.pSmart1or2s(currentB - currentA);
                triangleIndicesBuffer.pSmart1or2s(currentC - currentB);
                lastA = currentA;
                lastB = currentB;
                pAcc = lastC = currentC;
            }
        }

        for (int face = 0; face < model.texturedTriangleCount; face++) {
            texturesBuffer.p2(model.texturePCoordinate[face]);
            texturesBuffer.p2(model.textureMCoordinate[face]);
            texturesBuffer.p2(model.textureNCoordinate[face]);
        }

        footerBuffer.p2(model.vertexCount);
        footerBuffer.p2(model.triangleCount);
        footerBuffer.p1(model.texturedTriangleCount);

        footerBuffer.p1(hasTriangleInfo ? 1 : 0);
        footerBuffer.p1(hasTrianglePriorities ? -1 : model.modelPriority);
        footerBuffer.pBoolean(hasTriangleAlpha);
        footerBuffer.pBoolean(hasTriangleSkins);
        footerBuffer.pBoolean(hasVertexLabels);

        footerBuffer.p2(verticesXBuffer.getPosition());
        footerBuffer.p2(verticesYBuffer.getPosition());
        footerBuffer.p2(verticesZBuffer.getPosition());
        footerBuffer.p2(triangleIndicesBuffer.getPosition());

        Arrays.stream(buffers).forEach(masterBuffer::putBytes);
        byte[] data = new byte[masterBuffer.getPosition()];
        masterBuffer.getBytes(data);
        return data;
    }
}
