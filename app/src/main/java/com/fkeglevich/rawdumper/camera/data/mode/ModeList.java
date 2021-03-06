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

package com.fkeglevich.rawdumper.camera.data.mode;

import com.fkeglevich.rawdumper.camera.data.DataContainer;
import com.fkeglevich.rawdumper.camera.parameter.ParameterCollection;
import com.fkeglevich.rawdumper.raw.info.ExtraCameraInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 30/10/17.
 */

public class ModeList implements DataContainer<Mode>
{
    private final List<Mode> modeList = new ArrayList<>();

    public ModeList(ParameterCollection parameterCollection, ExtraCameraInfo cameraInfo)
    {
        addModeIfAvailable(new NormalMode(parameterCollection, cameraInfo));
        addModeIfAvailable(new LowLightMode(cameraInfo));
    }

    private void addModeIfAvailable(Mode mode)
    {
        if (mode.isAvailable())
            modeList.add(mode);
    }

    @Override
    public List<Mode> getAvailableValues()
    {
        return modeList;
    }
}
