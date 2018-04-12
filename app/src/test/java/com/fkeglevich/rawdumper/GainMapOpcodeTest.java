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

package com.fkeglevich.rawdumper;

import com.fkeglevich.rawdumper.dng.DngVersion;
import com.fkeglevich.rawdumper.dng.opcode.GainMapOpcode;
import com.fkeglevich.rawdumper.raw.data.RawImageSize;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Basic tests for GainMapOpcode since the get*Size methods are specially tricky
 *
 * Created by Flávio Keglevich on 01/04/18.
 */

public class GainMapOpcodeTest
{
    @Test
    public void getParameterAreaSizeIsCorrect()
    {
        GainMapOpcode gainMapOpcode = create4112x3088Opcode();
        assertEquals(595288, gainMapOpcode.getParameterAreaSize());

        gainMapOpcode = create2592x1944Opcode();
        assertEquals(237244, gainMapOpcode.getParameterAreaSize());
    }

    @Test
    public void getOpcodeSizeIsCorrect()
    {
        GainMapOpcode gainMapOpcode = create4112x3088Opcode();
        assertEquals(595308, gainMapOpcode.getOpcodeSize() + 4);

        gainMapOpcode = create2592x1944Opcode();
        assertEquals(237264, gainMapOpcode.getOpcodeSize() + 4);
    }

    @Test
    public void getDngVersionIsCorrect()
    {
        GainMapOpcode gainMapOpcode = create4112x3088Opcode();
        assertEquals(DngVersion.VERSION_1_3_0_0, gainMapOpcode.getDngVersion());

        gainMapOpcode = create2592x1944Opcode();
        assertEquals(DngVersion.VERSION_1_3_0_0, gainMapOpcode.getDngVersion());
    }

    private static GainMapOpcode create4112x3088Opcode()
    {
        RawImageSize imageSize = RawImageSize.createSimple(4112, 3088);
        return null;//new GainMapOpcode(imageSize, 193, 257);
    }

    private static GainMapOpcode create2592x1944Opcode()
    {
        RawImageSize imageSize = RawImageSize.createSimple(2592, 1944);
        return null;//new GainMapOpcode(imageSize, 122, 162);
    }
}
