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

package com.fkeglevich.rawdumper.raw.gain;

import junit.framework.Assert;

public class GainMap
{
    public final int numColumns;
    public final int numRows;

    public final float[] red;
    public final float[] greenRed;
    public final float[] greenBlue;
    public final float[] blue;

    public GainMap(int numColumns, int numRows)
    {
        this.numColumns = numColumns;
        this.numRows = numRows;

        this.red       = new float[numRows * numColumns];
        this.greenRed  = new float[numRows * numColumns];
        this.greenBlue = new float[numRows * numColumns];
        this.blue      = new float[numRows * numColumns];
    }

    public void addAndMultiply(GainMap map, float scalar)
    {
        Assert.assertEquals(numColumns, map.numColumns);
        Assert.assertEquals(numRows,    map.numRows);

        int size = numRows * numColumns;
        for (int i = 0; i < size; i++)
        {
            red[i]       += map.red[i]       * scalar;
            greenRed[i]  += map.greenRed[i]  * scalar;
            greenBlue[i] += map.greenBlue[i] * scalar;
            blue[i]      += map.blue[i]      * scalar;
        }
    }
}
