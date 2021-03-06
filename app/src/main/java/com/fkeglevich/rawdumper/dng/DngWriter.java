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

import com.fkeglevich.rawdumper.raw.capture.CaptureInfo;
import com.fkeglevich.rawdumper.raw.data.RawImageSize;
import com.fkeglevich.rawdumper.raw.data.buffer.RawImageData;
import com.fkeglevich.rawdumper.tiff.TiffTag;
import com.fkeglevich.rawdumper.tiff.TiffWriter;

import java.io.IOException;

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
            writeMetadata(captureInfo);
            writer.writeImageData(tiffWriter, imageData);
            writeExifInfo(captureInfo);
        }
        finally
        {
            close();
        }
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
