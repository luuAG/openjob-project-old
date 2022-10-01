package com.openjob.common.util;

import com.cloudinary.*;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class CloudinaryUtils {

    private static Cloudinary cloudinary;

    public static synchronized void getInstance() {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dqi96cb2c",
                "api_key", "943146573899571",
                "api_secret", "NUIdNva-bdwwYmpyL7gHJvktl10"));

    }

    public static String upload(MultipartFile file, String objectId) throws IOException {
        if (Objects.isNull(cloudinary))
            throw new IOException("Cloudinary instance is null");
        try {
            Map uploadResults = cloudinary.uploader().upload(
                    file.getBytes(), ObjectUtils.asMap("public_id", objectId)
            );
            return uploadResults.get("url").toString();
        } catch (Exception ex) {
            throw new IOException(NestedExceptionUtils.getMostSpecificCause(ex).getMessage());
        }
    }

    public static void delete(String objectId) throws IOException {
        if (Objects.isNull(cloudinary))
            throw new IOException("Cloudinary instance is null");
        try {
            cloudinary.uploader().destroy(objectId, ObjectUtils.emptyMap());
        } catch (Exception ex) {
            throw new IOException(NestedExceptionUtils.getMostSpecificCause(ex).getMessage());
        }
    }
}
