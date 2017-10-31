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

package com.fkeglevich.rawdumper.camera.feature;

import com.fkeglevich.rawdumper.camera.data.CaptureSize;
import com.fkeglevich.rawdumper.camera.data.PictureMode;
import com.fkeglevich.rawdumper.camera.mode.ModeList;
import com.fkeglevich.rawdumper.camera.parameter.ParameterCollection;
import com.fkeglevich.rawdumper.raw.info.ExtraCameraInfo;

import java.util.List;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 30/10/17.
 */

public class VirtualFeatureRecyclerFactory extends FeatureRecyclerFactoryBase
{
    private final ParameterCollection virtualParameterCollection = ParameterCollection.createVirtualParameterCollection();
    private final ModeList modeList;
    private final ParameterCollection parameterCollection;

    public VirtualFeatureRecyclerFactory(ModeList modeList, ParameterCollection parameterCollection)
    {
        this.modeList = modeList;
        this.parameterCollection = parameterCollection;
    }

    public PictureModeFeature createPictureModeFeature()
    {
        PictureModeFeature result = new PictureModeFeature(virtualParameterCollection, modeList);
        registerFeature(result);
        return result;
    }

    public PictureFormatFeature createPictureFormatFeature(PictureModeFeature pictureModeFeature)
    {
        PictureFormatFeature result = new PictureFormatFeature(virtualParameterCollection, pictureModeFeature);
        registerFeature(result);
        return result;
    }

    public PictureSizeFeature createPictureSizeFeature(PictureFormatFeature pictureFormatFeature)
    {
        PictureSizeFeature result = new PictureSizeFeature(parameterCollection, pictureFormatFeature);
        registerFeature(result);
        return result;
    }
}