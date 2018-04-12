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

package com.fkeglevich.rawdumper.dng;

import android.support.annotation.Nullable;
import android.util.Log;

import com.fkeglevich.rawdumper.debug.PerfInfo;
import com.fkeglevich.rawdumper.dng.opcode.GainMapOpcode;
import com.fkeglevich.rawdumper.dng.opcode.OpcodeListWriter;
import com.fkeglevich.rawdumper.raw.capture.CaptureInfo;
import com.fkeglevich.rawdumper.raw.data.BayerBitmap;
import com.fkeglevich.rawdumper.raw.data.RawImageSize;
import com.fkeglevich.rawdumper.raw.data.buffer.RawImageData;
import com.fkeglevich.rawdumper.raw.gain.GainMapParser;
import com.fkeglevich.rawdumper.tiff.TiffTag;
import com.fkeglevich.rawdumper.tiff.TiffWriter;
import com.fkeglevich.rawdumper.util.MathUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

/**
 * Simple class for generating DNG files.
 * Created by Flávio Keglevich on 16/04/2017.
 */

public class DngWriter
{
    private TiffWriter tiffWriter;

    @Nullable
    public static DngWriter open(String dngFile)
    {
        TiffWriter tiffWriter = TiffWriter.open(dngFile);
        if (tiffWriter == null) return null;

        return new DngWriter(tiffWriter);
    }

    private DngWriter(TiffWriter tiffWriter)
    {
        this.tiffWriter = tiffWriter;
    }

    public void write(CaptureInfo captureInfo, ADngImageWriter writer, RawImageData imageData) throws IOException
    {
        try
        {
            /*int x = 1028;
            int y = 772;
            int w = 3084;
            int h = 2316;*/

            /*
            int x = 1644;
            int w = 822;

            int y = 1234;
            int h = 618;

            PerfInfo.start("BayerBitmap.crop");
            BayerBitmap bayerBitmap = new BayerBitmap(captureInfo.camera.getSensor().getBayerPattern(), imageData).crop(x, y, w, h);
            PerfInfo.end("BayerBitmap.crop");

            PerfInfo.start("BayerBitmap.calculateMean");
            double[] mean = bayerBitmap.calculateMean();
            Log.i("ASD", "Mean: " + mean[0] + ", " + mean[1] + ", " + mean[2] + ", " + mean[3]);
            PerfInfo.end("BayerBitmap.calculateMean");

            double[] rgb = bayerBitmap.getRGBMean(mean, captureInfo.camera.getSensor());
            Log.i("ASD", "Mean: " + "R: " + rgb[0] + ", G:" + rgb[1] + ", B:" + rgb[2]);

            double mR = 89.8;
            double mG = 86.8;
            double mB = 73.6;

            double[] linSrgbToXYZ = new double[] {
            0.4360747,  0.3850649,  0.1430804,
            0.2225045,  0.7168786,  0.0606169,
            0.0139322,  0.0971045,  0.7141733};

            double lR = invertsRGB(mR / 255.0);
            double lG = invertsRGB(mG / 255.0);
            double lB = invertsRGB(mB / 255.0);


            double[] XYZjpg = MathUtil.multiply3x3MatrixToVector3(linSrgbToXYZ, new double[]{lR, lG, lB});

            double[] RAWSpaceJpg = MathUtil.multiply3x3MatrixToVector3(
                    MathUtil.floatArrayToDouble(captureInfo.camera.getColor().getColorMatrix1()), XYZjpg);

            double[] ASN  = new double[] {
                    rgb[0] / RAWSpaceJpg[0],
                    rgb[1] / RAWSpaceJpg[1],
                    rgb[2] / RAWSpaceJpg[2],
            };

            ASN[0] = ASN[0] / ASN[1];
            ASN[1] = 1.0;
            ASN[2] = ASN[2] / ASN[1];


            Log.i("ASD", "Old ASN: " + Arrays.toString(captureInfo.whiteBalanceInfo.asShotNeutral));

            //captureInfo.whiteBalanceInfo.asShotNeutral = MathUtil.doubleArrayToFloat(ASN);

            //asShotNeutral[0] = asShotNeutral[0] / asShotNeutral[1];
            //asShotNeutral[1] = asShotNeutral[1] / asShotNeutral[1];
            //asShotNeutral[2] = asShotNeutral[2] / asShotNeutral[1];


            //Log.i("ASD", "ASN: " + Arrays.toString(captureInfo.whiteBalanceInfo.asShotNeutral));
            Log.i("ASD", "ASN: " + Arrays.toString(MathUtil.doubleArrayToFloat(ASN)));

            //Mean: 205.5449964172946, 153.28365577681714, 172.09180387247144, 204.64486334538068
            //62.81320325356893, 62.79025819100938, 62.41666469814723, 62.487114071764346

            RawImageData newData = bayerBitmap.getRawImageData();*/
            Log.i("asd", GainMapParser.class.getName());
            Log.i("asd", PerfInfo.class.getName());
            //captureInfo.imageSize = newData.getSize();
            writeMetadata(captureInfo);

            writer.writeImageData(tiffWriter, imageData);
            //writer.writeImageData(tiffWriter, newData);
            writeExifInfo(captureInfo);
        }
        catch (ArrayIndexOutOfBoundsException ioe)
        {
            Log.i("ASD", ioe.getMessage());
        }
        finally
        {
            close();
        }
    }

    private double invertsRGB(double value)
    {
        if (value <= 0.04045)
            return value / 12.92;
        else
            return Math.pow((value + 0.055) / 1.055, 2.4);
    }

    private void close()
    {
        tiffWriter.close();
        tiffWriter = null;
    }

    private void writeMetadata(CaptureInfo captureInfo)
    {
        writeBasicHeader(captureInfo.imageSize);
        captureInfo.camera.getSensor().writeTiffTags(tiffWriter);
        captureInfo.camera.writeTiffTags(tiffWriter);
        captureInfo.device.writeTiffTags(tiffWriter);
        captureInfo.writeTiffTags(tiffWriter);

        captureInfo.date.writeTiffTags(tiffWriter);
        captureInfo.camera.getColor().writeTiffTags(tiffWriter);
        captureInfo.camera.getNoise().writeTiffTags(tiffWriter);
        captureInfo.whiteBalanceInfo.writeTiffTags(tiffWriter);

        //OpcodeListWriter.writeOpcodeList3Tag(tiffWriter, Collections.singletonList(new GainMapOpcode(captureInfo.imageSize, 21, 27)));

        if (captureInfo.camera.getOpcodes() != null && captureInfo.camera.getOpcodes().length >= 1)
            captureInfo.camera.getOpcodes()[0].writeTiffTags(tiffWriter);
    }

    private void writeBasicHeader(RawImageSize rawImageSize)
    {
        tiffWriter.setField(TiffTag.TIFFTAG_SUBFILETYPE,            DngDefaults.RAW_SUB_FILE_TYPE);
        tiffWriter.setField(TiffTag.TIFFTAG_PHOTOMETRIC,            DngDefaults.RAW_PHOTOMETRIC);
        tiffWriter.setField(TiffTag.TIFFTAG_SAMPLESPERPIXEL,        DngDefaults.RAW_SAMPLES_PER_PIXEL);
        tiffWriter.setField(TiffTag.TIFFTAG_PLANARCONFIG,           DngDefaults.RAW_PLANAR_CONFIG);
        tiffWriter.setField(TiffTag.TIFFTAG_IMAGEWIDTH,             rawImageSize.getPaddedWidth());
        tiffWriter.setField(TiffTag.TIFFTAG_IMAGELENGTH,            rawImageSize.getPaddedHeight());

        DngDefaults.VERSION.writeDngVersionTag(tiffWriter);
        DngDefaults.BACKWARD_VERSION.writeDngBackwardVersionTag(tiffWriter);
    }

    private void writeExifInfo(CaptureInfo captureInfo)
    {
        ExifWriter exifWriter = new ExifWriter();
        exifWriter.createEXIFDirectory(tiffWriter);
        exifWriter.writeTiffExifTags(tiffWriter, captureInfo);
        exifWriter.closeEXIFDirectory(tiffWriter);
    }
}
