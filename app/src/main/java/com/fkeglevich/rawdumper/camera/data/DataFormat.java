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

package com.fkeglevich.rawdumper.camera.data;

import com.fkeglevich.rawdumper.camera.extension.IntelParameters;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 31/10/17.
 */

public enum DataFormat implements ParameterValue
{
    JPEG(IntelParameters.RAW_DATA_FORMAT_NONE),
    YUV(IntelParameters.RAW_DATA_FORMAT_NONE),
    RAW(IntelParameters.RAW_DATA_FORMAT_BAYER);

    private final String parameterValue;

    DataFormat(String parameterValue)
    {
        this.parameterValue = parameterValue;
    }

    @Override
    public String getParameterValue()
    {
        return parameterValue;
    }
}
