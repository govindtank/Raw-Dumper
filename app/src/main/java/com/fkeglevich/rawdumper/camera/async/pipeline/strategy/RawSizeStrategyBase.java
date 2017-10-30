/*
 * Copyright 2017, Flávio Keglevich
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

package com.fkeglevich.rawdumper.camera.async.pipeline.strategy;

import com.fkeglevich.rawdumper.camera.data.CaptureSize;
import com.fkeglevich.rawdumper.raw.data.RawImageSize;
import com.fkeglevich.rawdumper.raw.info.SensorInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 30/10/17.
 */

public abstract class RawSizeStrategyBase implements PictureSizeStrategy
{
    private final List<CaptureSize> rawSizes;

    public RawSizeStrategyBase(SensorInfo sensorInfo)
    {
        rawSizes = initRawSizes(sensorInfo);
    }

    private List<CaptureSize> initRawSizes(SensorInfo sensorInfo)
    {
        List<CaptureSize> result = new ArrayList<>();
        for (RawImageSize rawImageSize : getSizeListFromSensor(sensorInfo))
            result.add(new CaptureSize(rawImageSize.getPaddedWidth(), rawImageSize.getPaddedHeight()));

        return Collections.unmodifiableList(result);
    }

    protected abstract RawImageSize[] getSizeListFromSensor(SensorInfo sensorInfo);

    @Override
    public List<CaptureSize> getValidPictureSizes()
    {
        return rawSizes;
    }
}
