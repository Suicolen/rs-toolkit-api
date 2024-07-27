package api.codecs;

import api.Codec;
import api.definition.model.RSModel;
import api.io.InputBuffer;
import api.io.OutputBuffer;
import api.definition.model.ModelType;

import java.util.List;

public class Type2ModelCodec implements Codec<InputBuffer, RSModel> {
    @Override
    public void decode(InputBuffer firstBuffer, RSModel model) {
        model.oldModel = true;
        model.decodeType = ModelType.TYPE_2;
        boolean hasTriangleInfo = false;
        boolean hasTexture = false;
        boolean hasTextureCoords = false;
        InputBuffer secondBuffer = firstBuffer.duplicate();
        InputBuffer thirdBuffer = firstBuffer.duplicate();
        InputBuffer fourthBuffer = firstBuffer.duplicate();
        InputBuffer fifthBuffer = firstBuffer.duplicate();
        firstBuffer.setPosition(firstBuffer.getLength() - 23);
        int vertexCount = firstBuffer.g2(); // 2
        int triangleCount = firstBuffer.g2(); // 4
        int texturedTriangleCount = firstBuffer.g1(); // 5

        model.vertexCount = vertexCount;
        model.triangleCount = triangleCount;
        model.texturedTriangleCount = texturedTriangleCount;

        int triangleInfoFlag = firstBuffer.g1(); // 6
        int priorityFlag = firstBuffer.g1(); // 7
        int alphaFlag = firstBuffer.g1(); // 8
        int triangleLabelFlag = firstBuffer.g1(); // 9
        int vertexLabelFlag = firstBuffer.g1(); // 10
        int skeletalAnimationFlag = firstBuffer.g1(); // 11

        int verticesXLength = firstBuffer.g2(); // 13
        int verticesYLength = firstBuffer.g2(); // 15
        int verticesZLength = firstBuffer.g2(); // 17
        int triangleIndicesLength = firstBuffer.g2(); // 19
        int skeletalLength = firstBuffer.g2(); // 21

        int vertexFlagsOffset = 0;
        int pos = vertexFlagsOffset + vertexCount;
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

        int triangleInfosOffset = pos;
        if (triangleInfoFlag == 1) {
            pos += triangleCount;
        }

        int vertexLabelsOffset = pos;
        pos += skeletalLength;
        int triangleAlphasOffset = pos;
        if (alphaFlag == 1) {
            pos += triangleCount;
        }

        int triangleIndicesOffset = pos;
        pos += triangleIndicesLength;
        int triangleColorsOffset = pos;
        pos += triangleCount * 2;
        int textureVerticesOffset = pos;
        pos += texturedTriangleCount * 6;
        int verticesXOffset = pos;
        pos += verticesXLength;
        int verticesYOffset = pos;
        pos += verticesYLength;
        // unnecessary
        int var10000 = pos + verticesZLength;

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
            model.textureCoordinateIndices = new int[triangleCount];
            model.triangleTextureIds = new int[triangleCount];
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

        if (skeletalAnimationFlag == 1) {
            model.skeletalBones = new int[vertexCount][];
            model.skeletalWeights = new int[vertexCount][];
        }

        model.triangleColors = new int[triangleCount];
        firstBuffer.setPosition(vertexFlagsOffset);
        secondBuffer.setPosition(verticesXOffset);
        thirdBuffer.setPosition(verticesYOffset);
        fourthBuffer.setPosition(pos);
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


        if (skeletalAnimationFlag == 1) {
            for (int vertex = 0; vertex < vertexCount; vertex++) {
                int boneCount = fifthBuffer.g1();
                model.skeletalBones[vertex] = new int[boneCount];
                model.skeletalWeights[vertex] = new int[boneCount];

                for (int bone = 0; bone < boneCount; bone++) {
                    model.skeletalBones[vertex][bone] = fifthBuffer.g1();
                    model.skeletalWeights[vertex][bone] = fifthBuffer.g1();
                }
            }
        }

        firstBuffer.setPosition(triangleColorsOffset);
        secondBuffer.setPosition(triangleInfosOffset);
        thirdBuffer.setPosition(trianglePrioritiesOffset);
        fourthBuffer.setPosition(triangleAlphasOffset);
        fifthBuffer.setPosition(triangleLabelsOffset);

        for (int triangleIndex = 0; triangleIndex < triangleCount; triangleIndex++) {
            model.triangleColors[triangleIndex] = firstBuffer.g2();
            if (triangleInfoFlag == 1) {
                int flag = secondBuffer.g1();

                if ((flag & 1) == 1) {
                    model.triangleInfo[triangleIndex] = 1;
                    hasTriangleInfo = true;
                } else {
                    model.triangleInfo[triangleIndex] = 0;
                }

                if ((flag & 2) == 2) {
                    model.textureCoordinateIndices[triangleIndex] = (byte) (flag >> 2);
                    model.triangleTextureIds[triangleIndex] = model.triangleColors[triangleIndex];
                    model.triangleColors[triangleIndex] = 127;
                    if (model.triangleTextureIds[triangleIndex] != -1) {
                        hasTexture = true;
                    }
                    if (model.textureCoordinateIndices[triangleIndex] != -1) {
                        hasTextureCoords = true;
                    }
                } else {
                    model.textureCoordinateIndices[triangleIndex] = -1;
                    model.triangleTextureIds[triangleIndex] = -1;
                }
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
        }

        firstBuffer.setPosition(triangleIndicesOffset);
        secondBuffer.setPosition(triangleIndicesFlagsOffset);

        int a = 0;
        int b = 0;
        int c = 0;
        int last = 0;

        for (int triangle = 0; triangle < triangleCount; triangle++) {
            int orientation = secondBuffer.g1();
            if (orientation == 1) {
                a = firstBuffer.gSmart1or2s() + last;
                last = a;
                b = firstBuffer.gSmart1or2s() + last;
                last = b;
                c = firstBuffer.gSmart1or2s() + last;
                last = c;
                model.faceIndicesA[triangle] = a;
                model.faceIndicesB[triangle] = b;
                model.faceIndicesC[triangle] = c;
            }
            if (orientation == 2) {
                b = c;
                c = firstBuffer.gSmart1or2s() + last;
                last = c;
                model.faceIndicesA[triangle] = a;
                model.faceIndicesB[triangle] = b;
                model.faceIndicesC[triangle] = c;
            }
            if (orientation == 3) {
                a = c;
                c = firstBuffer.gSmart1or2s() + last;
                last = c;
                model.faceIndicesA[triangle] = a;
                model.faceIndicesB[triangle] = b;
                model.faceIndicesC[triangle] = c;
            }
            if (orientation == 4) {
                int tmp = a;
                a = b;
                b = tmp;
                c = firstBuffer.gSmart1or2s() + last;
                last = c;
                model.faceIndicesA[triangle] = a;
                model.faceIndicesB[triangle] = b;
                model.faceIndicesC[triangle] = c;
            }
        }

        firstBuffer.setPosition(textureVerticesOffset);

        for (int triangle = 0; triangle < texturedTriangleCount; triangle++) {
            model.textureTypes[triangle] = 0;
            model.texturePCoordinate[triangle] = (short) firstBuffer.g2();
            model.textureMCoordinate[triangle] = (short) firstBuffer.g2();
            model.textureNCoordinate[triangle] = (short) firstBuffer.g2();
        }

        // not necessary for my editor
        /*if (model.faceTexture != null) {
            boolean hasFaceTexture = false;

            for (int triangle = 0; triangle < triangleCount; triangle++) {
                textureCoordinateIndex = model.faceTexture[triangle] & 255;
                if (textureCoordinateIndex != 255) {
                    if (model.faceIndicesA[triangle] == (model.textureVertexA[textureCoordinateIndex] & 0xFFFF) && model.faceIndicesB[triangle] == (model.textureVertexB[textureCoordinateIndex] & 0xFFFF) && model.faceIndicesC[triangle] == (model.textureVertexC[textureCoordinateIndex] & 0xFFFF)) {
                        model.faceTexture[triangle] = -1;
                    } else {
                        hasFaceTexture = true;
                    }
                }
            }

            if (!hasFaceTexture) {
                model.faceTexture = null;
            }
        }*/

        if (!hasTexture) {
            model.triangleTextureIds = null;
        }

        if (!hasTextureCoords) {
            model.textureCoordinateIndices = null;
        }

        if (!hasTriangleInfo) {
            model.triangleInfo = null;
        }
    }

    @Override
    public byte[] encode(RSModel model) {
        OutputBuffer buffer = new OutputBuffer(10000);
        OutputBuffer vertexFlagsBuffer = new OutputBuffer();
        OutputBuffer triangleInfosBuffer = new OutputBuffer();
        OutputBuffer triangleIndicesFlagsBuffer = new OutputBuffer();
        OutputBuffer trianglePrioritiesBuffer = new OutputBuffer();
        OutputBuffer triangleLabelsBuffer = new OutputBuffer();
        OutputBuffer triangleAlphasBuffer = new OutputBuffer();
        OutputBuffer triangleIndicesBuffer = new OutputBuffer();
        OutputBuffer triangleColorsBuffer = new OutputBuffer();
        OutputBuffer verticesXBuffer = new OutputBuffer();
        OutputBuffer verticesYBuffer = new OutputBuffer();
        OutputBuffer verticesZBuffer = new OutputBuffer();
        OutputBuffer textureVerticesBuffer = new OutputBuffer();
        OutputBuffer vertexSkinBuffer = new OutputBuffer();
        OutputBuffer footerBuffer = new OutputBuffer();

        List<OutputBuffer> buffers = List.of(
                vertexFlagsBuffer,
                triangleIndicesFlagsBuffer,
                trianglePrioritiesBuffer,
                triangleLabelsBuffer,
                triangleInfosBuffer,
                vertexSkinBuffer,
                triangleAlphasBuffer,
                triangleIndicesBuffer,
                triangleColorsBuffer,
                textureVerticesBuffer,
                verticesXBuffer,
                verticesYBuffer,
                verticesZBuffer,
                footerBuffer
        );

        boolean hasVertexLabels = model.vertexLabels != null;
        boolean hasSkeletalBones = model.skeletalBones != null;

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
                vertexSkinBuffer.p1(label);
            }

            if (hasSkeletalBones) {
                int[][] skeletalBones = model.skeletalBones;
                int[][] skeletalScales = model.skeletalWeights;
                int boneCount = skeletalBones[vertex].length;
                vertexSkinBuffer.p1(boneCount);
                for (int boneIndex = 0; boneIndex < boneCount; boneIndex++) {
                    vertexSkinBuffer.p1(skeletalBones[vertex][boneIndex]);
                    vertexSkinBuffer.p1(skeletalScales[vertex][boneIndex]);
                }
            }
        }

        boolean hasTriangleInfo = model.triangleInfo != null;
        boolean hasTriangleLabels = model.triangleLabels != null;
        boolean hasTrianglePriorities = model.trianglePriorities != null;
        boolean hasTriangleAlpha = model.triangleAlpha != null;
        boolean hasTextures = model.texturedTriangleCount > 0;
        for (int triangle = 0; triangle < model.triangleCount; triangle++) {
            boolean isTextured = hasTextures && model.textureCoordinateIndices[triangle] != -1;
            int materialId = isTextured ? model.triangleTextureIds[triangle] : model.triangleColors[triangle];
            triangleColorsBuffer.p2(materialId);

            if (hasTriangleInfo) {
                if (isTextured) {
                    int flag = 2 + (model.textureCoordinateIndices[triangle] << 2);
                    triangleInfosBuffer.p1(flag);
                } else {
                    triangleInfosBuffer.p1(model.triangleInfo[triangle]);
                }
            }

            if (hasTrianglePriorities) {
                trianglePrioritiesBuffer.p1(model.trianglePriorities[triangle]);
            }

            if (hasTriangleAlpha) {
                triangleAlphasBuffer.p1(model.triangleAlpha[triangle]);
            }

            if (hasTriangleLabels) {
                triangleLabelsBuffer.p1(model.triangleLabels[triangle]);
            }
        }

        int lastA = 0;
        int lastB = 0;
        int lastC = 0;
        int pAcc = 0;

        // share edge info to save space
        for (int triangle = 0; triangle < model.triangleCount; triangle++) {
            int currentA = model.faceIndicesA[triangle];
            int currentB = model.faceIndicesB[triangle];
            int currentC = model.faceIndicesC[triangle];
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

        for (int triangle = 0; triangle < model.texturedTriangleCount; triangle++) {
            textureVerticesBuffer.p2(model.texturePCoordinate[triangle]);
            textureVerticesBuffer.p2(model.textureMCoordinate[triangle]);
            textureVerticesBuffer.p2(model.textureNCoordinate[triangle]);
        }

        footerBuffer.p2(model.vertexCount);
        footerBuffer.p2(model.triangleCount);
        footerBuffer.p1(model.texturedTriangleCount);

        footerBuffer.pBoolean(hasTriangleInfo);
        footerBuffer.p1(hasTrianglePriorities ? -1 : model.modelPriority);
        footerBuffer.pBoolean(hasTriangleAlpha);
        footerBuffer.pBoolean(hasTriangleLabels);
        footerBuffer.pBoolean(hasVertexLabels);
        footerBuffer.pBoolean(hasSkeletalBones);

        footerBuffer.p2(verticesXBuffer.getPosition());
        footerBuffer.p2(verticesYBuffer.getPosition());
        footerBuffer.p2(verticesZBuffer.getPosition());
        footerBuffer.p2(triangleIndicesBuffer.getPosition());
        footerBuffer.p2(vertexSkinBuffer.getPosition());

        buffers.forEach(buffer::putBytes);
        buffer.p1(255);
        buffer.p1(254);
        byte[] data = new byte[buffer.getPosition()];
        buffer.getBytes(data);

        return data;
    }
}
