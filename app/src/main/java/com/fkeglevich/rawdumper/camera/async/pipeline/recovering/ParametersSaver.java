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

package com.fkeglevich.rawdumper.camera.async.pipeline.recovering;

import com.fkeglevich.rawdumper.async.operation.AsyncOperation;
import com.fkeglevich.rawdumper.io.Directories;
import com.fkeglevich.rawdumper.io.async.IOThread;
import com.fkeglevich.rawdumper.io.async.IOUtil;
import com.fkeglevich.rawdumper.util.Nullable;
import com.fkeglevich.rawdumper.util.exception.MessageException;

import java.io.File;
import java.io.IOException;

/**
 * Created by flavio on 25/03/18.
 */

public class ParametersSaver
{
    private static final String PARAMETERS_FILENAME = "parameters";

    private static String getOutputFile()
    {
        return new File(Directories.getPartialPicturesDirectory(), PARAMETERS_FILENAME).getAbsolutePath();
    }

    public static void saveParametersAsync(String flattened, AsyncOperation<Void> cb, AsyncOperation<MessageException> exceptionCb)
    {
        IOThread.getIOAccess().saveStringAsync(flattened, getOutputFile(), cb, exceptionCb);
    }

    public static Nullable<String> restoreParameters()
    {
        try
        {
            String result = new String(IOUtil.readBytes(getOutputFile()));
            return Nullable.of(result);
        }
        catch (IOException e)
        {
            return Nullable.of(null);
        }
    }
}
