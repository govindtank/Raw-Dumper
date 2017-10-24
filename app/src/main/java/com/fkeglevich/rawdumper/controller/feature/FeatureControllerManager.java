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

package com.fkeglevich.rawdumper.controller.feature;

import com.fkeglevich.rawdumper.activity.ActivityReference;
import com.fkeglevich.rawdumper.camera.async.TurboCamera;
import com.fkeglevich.rawdumper.util.Nothing;
import com.fkeglevich.rawdumper.util.event.EventDispatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 23/10/17.
 */

public class FeatureControllerManager
{
    private final FeatureControllerFactory controllerFactory;
    private final List<DisplayableFeatureController> controllersList;

    public FeatureControllerManager()
    {
        controllerFactory = new FeatureControllerFactory();
        controllersList = new ArrayList<>();
    }

    public void createControllers(ActivityReference reference)
    {
        controllersList.add(controllerFactory.createISOController(reference));
        controllersList.add(controllerFactory.createSSController(reference));
        controllersList.add(controllerFactory.createEVController(reference));
    }

    public void setupControllers(TurboCamera camera, EventDispatcher<Nothing> onCameraClose)
    {
        for (DisplayableFeatureController featureController : controllersList)
            featureController.setupFeature(camera, onCameraClose);
    }
}
