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

package com.fkeglevich.rawdumper.raw.data;

import android.util.Log;

import com.fkeglevich.rawdumper.raw.data.buffer.ArrayRawImageData;
import com.fkeglevich.rawdumper.raw.data.buffer.RawImageData;
import com.fkeglevich.rawdumper.raw.info.SensorInfo;

import junit.framework.Assert;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by flavio on 25/03/18.
 */

public class BayerBitmap
{
    private static final String EVEN_NUMBER_REQUIREMENT_MSG = "Each dimension needs to be an even number!";
    private static final String DIMENSIONS_OUT_OF_BOUNDS_MSG = "The dimensions are out of bounds!";

    private final BayerPattern pattern;
    private final RawImageData rawImageData;

    public BayerBitmap(BayerPattern pattern, RawImageData rawImageData)
    {
        this.pattern = pattern;
        this.rawImageData = rawImageData;
    }

    public BayerPattern getPattern()
    {
        return pattern;
    }

    public RawImageData getRawImageData()
    {
        return rawImageData;
    }

    public RawImageSize getRawImageSize()
    {
        return rawImageData.getSize();
    }

    public int getWidth()
    {
        return rawImageData.getSize().getPaddedWidth();
    }

    public int getHeight()
    {
        return rawImageData.getSize().getPaddedHeight();
    }

    public BayerBitmap crop(int x, int y, int width, int height) throws IOException
    {
        Assert.assertTrue(EVEN_NUMBER_REQUIREMENT_MSG, x      % 2 == 0);
        Assert.assertTrue(EVEN_NUMBER_REQUIREMENT_MSG, y      % 2 == 0);
        Assert.assertTrue(EVEN_NUMBER_REQUIREMENT_MSG, width  % 2 == 0);
        Assert.assertTrue(EVEN_NUMBER_REQUIREMENT_MSG, height % 2 == 0);

        Assert.assertFalse(DIMENSIONS_OUT_OF_BOUNDS_MSG, x < 0 || (x + width) > getWidth());
        Assert.assertFalse(DIMENSIONS_OUT_OF_BOUNDS_MSG, y < 0 || (y + height) > getHeight());

        RawImageSize croppedSize = RawImageSize.createSimple(width, height);

        Log.i("ASD", croppedSize.toString());


        byte[] rowBuffer = getRawImageSize().buildValidRowBuffer();
        byte[] croppedBytes = new byte[croppedSize.getBufferLength()];

        int croppedRow = 0;
        for (int row = y; row < (height + y); row++, croppedRow++)
        {
            rawImageData.copyValidRowToBuffer(row, rowBuffer);
            System.arraycopy(rowBuffer, x*2, croppedBytes, croppedRow * croppedSize.getBytesPerLine(), croppedSize.getBytesPerLine());
        }

        //src.length=7142544 srcPos=7142544 dst.length=6168 dstPos=0 length=6168
        ArrayRawImageData croppedData = new ArrayRawImageData(croppedSize, croppedBytes);
        return new BayerBitmap(pattern, croppedData);
    }

    public double[] calculateMean() throws IOException
    {
        byte[] buffer = new byte[rawImageData.getSize().getBufferLength()];
        rawImageData.copyAllImageDataToBuffer(buffer);

        short[] shortBuffer = new short[buffer.length/2];
        ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shortBuffer);

        int w = getRawImageSize().getPaddedWidth();
        int h = getRawImageSize().getPaddedHeight();
        int bpl = getRawImageSize().getBytesPerLine() / 2;

        double[] mean = new double[4];

        for (int y = 0; y < h; y += 2)
        {
            for (int x = 0; x < w; x += 2)
            {
                mean[0] += shortBuffer[(y * bpl) + x];
                mean[1] += shortBuffer[(y * bpl) + x + 1];
                mean[2] += shortBuffer[((y + 1) * bpl) + x];
                mean[3] += shortBuffer[((y + 1) * bpl) + x + 1];
            }
        }

        mean[0] /= (shortBuffer.length/4);
        mean[1] /= (shortBuffer.length/4);
        mean[2] /= (shortBuffer.length/4);
        mean[3] /= (shortBuffer.length/4);

        return mean;
    }

    public double[] getRGBMean(double[] mean, SensorInfo sensorInfo)
    {
        double[] linMean = new double[4];
        float[] blackLevel = sensorInfo.getBlackLevel();
        double whiteLevel = sensorInfo.getWhiteLevel();

        linMean[0] = (mean[0] - blackLevel[0]) / (whiteLevel - blackLevel[0]);
        linMean[1] = (mean[1] - blackLevel[1]) / (whiteLevel - blackLevel[1]);
        linMean[2] = (mean[2] - blackLevel[2]) / (whiteLevel - blackLevel[2]);
        linMean[3] = (mean[3] - blackLevel[3]) / (whiteLevel - blackLevel[3]);

        double red   = linMean[pattern.getR()];
        double green = (linMean[pattern.getGr()] + linMean[pattern.getGb()]) / 2;
        double blue  = linMean[pattern.getB()];

        return new double[] { red, green, blue };
    }

    /*
    wb_multipliers = (meta_info.AsShotNeutral).ˆ-1;
    wb_multipliers = wb_multipliers/wb_multipliers(2);
    mask = wbmask(size(lin_bayer,1),size(lin_bayer,2),wb_multipliers,’rggb’);
    balanced_bayer = lin_bayer .* mask;

    jpegAtRawSpaceRGBs = mask .* rawRGBs;

    mask = jpegAtRawSpaceRGBs ./ rawRGBs;

    AsShotNeutral = 1 ./ mask


    AsShotNeutral = rawRGBs / jpegAtRawSpaceRGBs
     */

    //public native void linearizeAndCalculateMean(byte[] rawData, double[] meanOut, short blackLevel, short whiteLevel);
}
