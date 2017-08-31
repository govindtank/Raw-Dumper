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

package com.fkeglevich.rawdumper.ui.activity;

import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.fkeglevich.rawdumper.controller.activity.ActivityReference;
import com.fkeglevich.rawdumper.controller.activity.EventListener;
import com.fkeglevich.rawdumper.controller.activity.event.InteractiveEvent;
import com.fkeglevich.rawdumper.controller.activity.module.ActivityModule;

/**
 * Created by Flávio Keglevich on 29/08/2017.
 * TODO: Add a class header comment!
 */

public class FullscreenModule extends ActivityModule
{
    public FullscreenModule(ActivityReference activityReference)
    {
        super(activityReference);
        new EventListener<InteractiveEvent>(InteractiveEvent.ON_WINDOWS_FOCUS_CHANGED, activityReference.getInteractiveEvents())
        {
            @Override
            protected void onEvent(Object optionalData)
            {
                boolean hasFocus = (Boolean) optionalData;
                if (hasFocus)
                    goToFullscreenMode();
            }
        };
    }

    /**
     * Hides the action bar and the system UI.
     */
    private void goToFullscreenMode()
    {
        AppCompatActivity activityLocal = getActivityReference().weaklyGet();
        if (activityLocal != null)
        {
            // Hide UI first
            ActionBar actionBar = activityLocal.getSupportActionBar();
            if (actionBar != null)
                actionBar.hide();

            View decorView = activityLocal.getWindow().getDecorView();
            if (decorView != null)
            {
                int systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                    systemUiVisibility = systemUiVisibility | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

                decorView.setSystemUiVisibility(systemUiVisibility);
            }
        }
    }
}