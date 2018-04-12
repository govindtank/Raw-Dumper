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

package com.fkeglevich.rawdumper.camera.async.pipeline.picture;

import android.hardware.Camera;
import android.support.annotation.NonNull;

import com.fkeglevich.rawdumper.async.operation.AsyncOperation;
import com.fkeglevich.rawdumper.camera.action.listener.PictureExceptionListener;
import com.fkeglevich.rawdumper.camera.action.listener.PictureListener;
import com.fkeglevich.rawdumper.camera.async.pipeline.recovering.ParametersSaver;
import com.fkeglevich.rawdumper.camera.extension.ICameraExtension;
import com.fkeglevich.rawdumper.util.Mutable;
import com.fkeglevich.rawdumper.util.exception.MessageException;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 02/11/17.
 */

public abstract class PicturePipelineBase implements PicturePipeline
{
    private final Mutable<ICameraExtension> cameraExtension;
    private final Object lock;
    private final boolean shouldSaveParameters;

    PicturePipelineBase(Mutable<ICameraExtension> cameraExtension, Object lock, boolean shouldSaveParameters)
    {
        this.cameraExtension = cameraExtension;
        this.lock = lock;
        this.shouldSaveParameters = shouldSaveParameters;
    }

    @Override
    public void takePicture(PictureListener pictureCallback, PictureExceptionListener exceptionCallback)
    {
        if (shouldSaveParameters)
        {
            String parameters;
            synchronized (lock)
            {
                parameters = cameraExtension.get().getCameraDevice().getParameters().flatten();
            }
            ParametersSaver.saveParametersAsync(parameters, new AsyncOperation<Void>()
            {
                @Override
                protected void execute(Void argument)
                {
                    takePictureImpl(pictureCallback, exceptionCallback);
                }
            }, new AsyncOperation<MessageException>()
            {
                @Override
                protected void execute(MessageException argument)
                {
                    exceptionCallback.onException(argument);
                }
            });
        }
        else
            takePictureImpl(pictureCallback, exceptionCallback);
    }

    private void takePictureImpl(PictureListener pictureCallback, PictureExceptionListener exceptionCallback)
    {
        synchronized (lock)
        {
            final PipelineData pipelineData = new PipelineData();
            Camera camera = cameraExtension.get().getCameraDevice();
            setupCameraBefore(camera);
            camera.takePicture(null, createRawCB(pipelineData), createJpegCB(pictureCallback, exceptionCallback, pipelineData));
        }
    }

    @NonNull
    private Camera.PictureCallback createRawCB(final PipelineData pipelineData)
    {
        return (data, camera) -> pipelineData.rawData = data;
    }

    @NonNull
    private Camera.PictureCallback createJpegCB(final PictureListener pictureCallback, final PictureExceptionListener exceptionCallback, final PipelineData pipelineData)
    {
        return (data, camera) ->
        {
            pipelineData.jpegData = data;
            synchronized (lock)
            {
                processPipeline(pipelineData, pictureCallback, exceptionCallback);
            }
        };
    }

    protected void setupCameraBefore(Camera camera)
    {
        //no op
    }

    void startPreview()
    {
        cameraExtension.get().getCameraDevice().startPreview();
    }

    protected abstract void processPipeline(PipelineData pipelineData, PictureListener pictureCallback, PictureExceptionListener exceptionCallback);
}
