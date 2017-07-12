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

package com.fkeglevich.rawdumper.camera.async.pipeline;

import android.hardware.Camera;

import com.fkeglevich.rawdumper.camera.async.callbacks.IIOResultCallback;
import com.fkeglevich.rawdumper.camera.async.io.IOAccess;

/**
 * Created by Flávio Keglevich on 25/06/2017.
 * TODO: Add a class header comment!
 */

public class BasicPipeline extends APicturePipeline
{
    protected IIOResultCallback ioCallback;

    public BasicPipeline(IIOResultCallback ioCallback)
    {
        this.ioCallback = ioCallback;
    }

    @Override
    void initCallbacks()
    {
        pictureCallback = new Camera.PictureCallback()
        {
            @Override
            public void onPictureTaken(byte[] data, Camera camera)
            {
                camera.startPreview();
                IOAccess.writeBytesToFile(data, "aa//sd", ioCallback);
            }
        };
    }
}
