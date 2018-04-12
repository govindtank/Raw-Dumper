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

package com.fkeglevich.rawdumper.raw.mkn.illuminant;

import com.fkeglevich.rawdumper.raw.color.ColorTemperature;

import junit.framework.Assert;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import static com.fkeglevich.rawdumper.util.MathUtil.clamp;

/**
 * Created by flavio on 02/04/18.
 */

public class IlluminantEstimation
{
    private final Map<MknIlluminant, Double> intensityMap = new EnumMap<>(MknIlluminant.class);
    private final Map<MknIlluminant, ColorTemperature> temperatureMap;

    public IlluminantEstimation(Map<MknIlluminant, ColorTemperature> temperatureMap)
    {
        this.temperatureMap = temperatureMap;
    }

    public void setIlluminantIntensity(MknIlluminant illuminant, double intensity)
    {
        intensityMap.put(illuminant, clamp(intensity, 0, 1));
    }

    public ColorTemperature getColorTemperature()
    {
        isValid();

        double temperature = 0, tint = 0;
        Set<MknIlluminant> illuminantSet = intensityMap.keySet();
        for (MknIlluminant illuminant : illuminantSet)
        {
            temperature += temperatureMap.get(illuminant).getTemperature() * intensityMap.get(illuminant);
            tint += temperatureMap.get(illuminant).getTint() * intensityMap.get(illuminant);
        }
        return new ColorTemperature(temperature, tint);
    }

    /*public RGBGainMap getGainMap()
    {
        throw new RuntimeException("Stub!");
    }*/

    private void isValid()
    {
        Assert.assertTrue(intensityMap.keySet().containsAll(temperatureMap.keySet()));
    }
}
