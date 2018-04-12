/*
 * Copyright 2018, Flávio Keglevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fkeglevich.rawdumper.dng.opcode;

import com.fkeglevich.rawdumper.dng.DngVersion;
import com.fkeglevich.rawdumper.raw.data.RawImageSize;
import com.fkeglevich.rawdumper.raw.gain.GainMap;

import java.nio.ByteBuffer;

import static com.fkeglevich.rawdumper.util.DataSize.DOUBLE_SIZE;
import static com.fkeglevich.rawdumper.util.DataSize.FLOAT_SIZE;
import static com.fkeglevich.rawdumper.util.DataSize.INT_SIZE;

/**
 * Represents a GainMap DNG Opcode. It is used to perform lens shading corrections
 *
 * Created by Flávio Keglevich on 01/04/18.
 */

public class GainMapOpcode extends Opcode
{
    private static final int OPCODE_ID = 9;
    private static final int STATIC_PARAMETER_AREA_SIZE = INT_SIZE * 11 + DOUBLE_SIZE * 4;

    private final GainMap gainMap;

    private int top;
    private int left;
    private int bottom;
    private int right;

    private int plane;
    private int planes;

    private int rowPitch;
    private int colPitch;

    private int mapPointsV;
    private int mapPointsH;

    private double mapSpacingV;
    private double mapSpacingH;

    private double mapOriginH;
    private double mapOriginV;

    private int mapPlanes;

    public GainMapOpcode(RawImageSize rawImageSize, GainMap gainMap)
    {
        super(OPCODE_ID, DngVersion.VERSION_1_3_0_0, OPCODE_DEFAULT_FLAGS);

        this.gainMap = gainMap;

        top     = 0;
        left    = 0;
        bottom  = rawImageSize.getPaddedHeight();
        right   = rawImageSize.getPaddedWidth();

        plane   = 0;
        planes  = 3;

        rowPitch = 1;
        colPitch = 1;

        mapPointsV = gainMap.numRows;
        mapPointsH = gainMap.numColumns;

        mapSpacingV = 1.0 / (mapPointsV - 1);
        mapSpacingH = 1.0 / (mapPointsH - 1);

        mapOriginH  = 0;
        mapOriginV  = 0;

        mapPlanes   = 3;
    }

    @Override
    public int getParameterAreaSize()
    {
        return STATIC_PARAMETER_AREA_SIZE + mapPointsV * mapPointsH * mapPlanes * FLOAT_SIZE;
    }

    @Override
    public void writeOpcodeData(ByteBuffer buffer)
    {
        buffer.putInt(top)
                .putInt(left)
                .putInt(bottom)
                .putInt(right)
                .putInt(plane)
                .putInt(planes)
                .putInt(rowPitch)
                .putInt(colPitch)
                .putInt(mapPointsV)
                .putInt(mapPointsH)
                .putDouble(mapSpacingV)
                .putDouble(mapSpacingH)
                .putDouble(mapOriginV)
                .putDouble(mapOriginH)
                .putInt(mapPlanes);

        int i = 0;
        for (int y = 0; y < mapPointsV; y++)
            for (int x = 0; x < mapPointsH; x ++)
            {
                buffer.putFloat(gainMap.red[i]);
                buffer.putFloat((gainMap.greenRed[i] + gainMap.greenBlue[i]) / 2f);
                buffer.putFloat(gainMap.blue[i]);
                i++;
            }
    }
}