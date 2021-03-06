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

package com.fkeglevich.rawdumper.camera.async.function;

import android.util.Log;

import com.fkeglevich.rawdumper.async.function.ThrowingAsyncFunction;
import com.fkeglevich.rawdumper.camera.async.CameraContext;
import com.fkeglevich.rawdumper.camera.async.TurboCamera;
import com.fkeglevich.rawdumper.camera.async.direct.LowLevelCamera;
import com.fkeglevich.rawdumper.camera.async.direct.LowLevelCameraImpl;
import com.fkeglevich.rawdumper.camera.async.impl.TurboCameraImpl;
import com.fkeglevich.rawdumper.camera.exception.CameraOpenException;
import com.fkeglevich.rawdumper.camera.exception.CameraPatchRequiredException;
import com.fkeglevich.rawdumper.camera.exception.RawIsUnavailableException;
import com.fkeglevich.rawdumper.camera.extension.ICameraExtension;
import com.fkeglevich.rawdumper.camera.extension.IntelCameraExtensionLoader;
import com.fkeglevich.rawdumper.camera.service.CameraServiceManager;
import com.fkeglevich.rawdumper.util.exception.MessageException;

import java.io.IOException;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 08/10/17.
 */

public class CameraOpenFunction extends ThrowingAsyncFunction<CameraContext, TurboCamera, MessageException>
{
    private static final String TAG = "CameraOpenFunction";

    @Override
    protected TurboCamera call(CameraContext context) throws MessageException
    {
        boolean rawIsUnavailable = context.getSensorInfo().getRawImageSizes().length == 0;
        boolean cameraCanBePatched = context.getCameraInfo().isCanBePatched();
        if (rawIsUnavailable)
            throw cameraCanBePatched ? new CameraPatchRequiredException() : new RawIsUnavailableException();

        try
        {
            CameraServiceManager.getInstance().enableFeatures(context);
            ICameraExtension cameraExtension = IntelCameraExtensionLoader.extendedOpenCamera(context);
            LowLevelCamera llCamera = new LowLevelCameraImpl(context, cameraExtension);
            return new TurboCameraImpl(llCamera);
        }
        catch (IOException | RuntimeException e)
        {
            Log.e(TAG, "Exception (" + e.getClass().getSimpleName() + "): " + e.getMessage());
            throw new CameraOpenException();
        }
    }
}
