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

package com.fkeglevich.rawdumper.raw.gain;

import com.fkeglevich.rawdumper.debug.PerfInfo;
import com.fkeglevich.rawdumper.raw.mkn.illuminant.MknIlluminant;

import junit.framework.Assert;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

/**
 * Created by flavio on 24/03/18.
 */

public class GainMapParser
{
    public Map<MknIlluminant, GainMap> parse(Reader reader) throws IOException
    {
        Map<MknIlluminant, GainMap> result = new EnumMap<>(MknIlluminant.class);

        try(BufferedReader br = new BufferedReader(reader))
        {
            int width  = getIntVariable(br.readLine());
            int height = getIntVariable(br.readLine());

            float[] floats;
            do
            {
                //PerfInfo.start(getClass().getName());
                floats = selectIlluminant(width, height, br.readLine(), result);
                //PerfInfo.end(getClass().getName());

                if (floats != null)
                {
                    for (int i = 0; i < height; i++)
                        readLine(br.readLine(), floats, i * width);
                }
            }
            while (floats != null);
        }

        return result;
    }

    private int getIntVariable(String line)
    {
        String[] split = line.split("=");
        Assert.assertEquals(2, split.length);
        return Integer.parseInt(split[1].trim());
    }

    private void readLine(String line, float[] out, int offset)
    {
        String[] split = line.split(" ");
        for (int i = 0; i < split.length; i++)
            out[offset + i] = 1.0f;//Float.parseFloat(split[i]);
    }

    private float[] selectIlluminant(int width, int height, String line, Map<MknIlluminant, GainMap> illuminantMap)
    {
        if (line == null) return null;

        String[] split = line.split("_");

        for (MknIlluminant illuminant : MknIlluminant.values())
            if (split[0].equals(illuminant.getToken()))
            {
                if (!illuminantMap.containsKey(illuminant))
                    illuminantMap.put(illuminant, new GainMap(width, height));

                GainMap gainMap = illuminantMap.get(illuminant);
                return selectColorPlane(gainMap, split[1]);
            }

        return null;
    }

    private float[] selectColorPlane(GainMap map, String planeToken)
    {
        switch (planeToken)
        {
            case "R": return map.red;
            case "B": return map.blue;
            case "Gr": return map.greenRed;
            case "Gb": return  map.greenBlue;
            default: throw new RuntimeException("Invalid color plane token: " + planeToken);
        }
    }
}
