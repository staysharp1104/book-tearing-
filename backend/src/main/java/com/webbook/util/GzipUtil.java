package com.webbook.util;

import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Component
public class GzipUtil {

    public String readFile(String filePath) throws IOException {
        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            return "";
        }
        byte[] rawBytes = Files.readAllBytes(path);
        if (rawBytes.length >= 2 && (rawBytes[0] & 0xFF) == 0x1F && (rawBytes[1] & 0xFF) == 0x8B) {
            try (GZIPInputStream gzis = new GZIPInputStream(new ByteArrayInputStream(rawBytes));
                 BufferedReader reader = new BufferedReader(new InputStreamReader(gzis, "UTF-8"))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                return sb.toString();
            }
        } else {
            return new String(rawBytes, "UTF-8");
        }
    }

    public void writeGzipFile(String filePath, String content) throws IOException {
        Path parentDir = Path.of(filePath).getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }
        try (GZIPOutputStream gzos = new GZIPOutputStream(new FileOutputStream(filePath));
             Writer writer = new OutputStreamWriter(gzos, "UTF-8")) {
            writer.write(content);
        }
    }
}
