package my.spring.sample.mvc.utils;

import com.google.common.io.BaseEncoding;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CompressUtils {

    private static final int BUF_SIZE = 16384;

    public static byte[] decompressGzip(String base64Str) throws IOException {
        byte[] data = BaseEncoding.base64().decode(base64Str);
        byte[] result = null;
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            GZIPInputStream gis = new GZIPInputStream(bais);) {
            byte[] buffer = new byte[BUF_SIZE];
            int len;
            while((len = gis.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            result = baos.toByteArray();
        }
        return result;
    }

    public static String compressGzip(byte[] data) throws IOException {
        byte[] result = null;
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream(data.length);
            GZIPOutputStream zos = new GZIPOutputStream(baos)) {
            zos.write(data);
            zos.close();
            result = baos.toByteArray();
        }
        return BaseEncoding.base64().encode(result);
    }

    public static void main(String[] args) throws IOException {
//		File file = new File("/Users/timpark/Desktop/multi-test.dwg");
        File file = new File("/Users/timpark/Downloads/9BD6B4B32C924DA1.obj");
        byte[] bytes= Files.readAllBytes(file.toPath());
        String b = compressGzip(bytes);
        System.out.println(b);
    }
}
