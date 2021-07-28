package my.spring.sample.mvc.component;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import my.spring.sample.mvc.utils.CastUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AwsS3FileManager {

    @Autowired
    private AmazonS3 s3client;

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    @Value("${aws.cdn.url}")
    private String cdnUrl;

    public PutObjectResult uploadGzip(String key, byte[] data) {
        ObjectMetadata omd = new ObjectMetadata();
        omd.setContentType("text/plain");
        omd.setCacheControl("max-age=0");
        omd.setContentLength(data.length);
        omd.setContentEncoding("gzip");

        return upload(omd, key, data);
    }

    public PutObjectResult uploadJson(String key, byte[] data) {
        ObjectMetadata omd = new ObjectMetadata();
        omd.setContentType("json");
        omd.setCacheControl("max-age=0");
        omd.setContentLength(data.length);

        return upload(omd, key, data);
    }

    public PutObjectResult uploadImage(String key, byte[] data, String imageFormat) {
        ObjectMetadata omd = new ObjectMetadata();
        omd.setContentType("image/" + imageFormat);
        omd.setCacheControl("max-age=0");
        omd.setContentLength(data.length);

        return upload(omd, key, data);
    }

    @Async("threadPoolTaskExecutor")
    public PutObjectResult uploadImageAsync(String key, byte[] data, String imageFormat) {
        ObjectMetadata omd = new ObjectMetadata();
        omd.setContentType("image/" + imageFormat);
        omd.setCacheControl("max-age=0");
        omd.setContentLength(data.length);

        return upload(omd, key, data);
    }

    public PutObjectResult uploadJson(String key, String data) {
        byte[] byteData = data.getBytes();

        ObjectMetadata omd = new ObjectMetadata();
        omd.setContentType("json");
        omd.setCacheControl("max-age=0");
        omd.setContentLength(byteData.length);

        return upload(omd, key, data.getBytes());
    }

    public PutObjectResult uploadPng(String key, byte[] data) {
        ObjectMetadata omd = new ObjectMetadata();
        omd.setContentType("image/png");
        omd.setCacheControl("max-age=0");
        omd.setContentLength(data.length);

        return upload(omd, key, data);
    }

    public PutObjectResult uploadJpg(String key, byte[] data) {
        ObjectMetadata omd = new ObjectMetadata();
        omd.setContentType("image/jpeg");
        omd.setCacheControl("max-age=0");
        omd.setContentLength(data.length);

        return upload(omd, key, data);
    }

    public PutObjectResult uploadSvg(String key, byte[] data) {
        ObjectMetadata omd = new ObjectMetadata();
        omd.setContentType("image/svg");
        omd.setCacheControl("max-age=0");
        omd.setContentLength(data.length);

        return upload(omd, key, data);
    }

    public PutObjectResult uploadGif(String key, byte[] data) {
        ObjectMetadata omd = new ObjectMetadata();
        omd.setContentType("image/gif");
        omd.setCacheControl("max-age=0");
        omd.setContentLength(data.length);

        return upload(omd, key, data);
    }

    public PutObjectResult upload(String key, byte[] data) {
        ObjectMetadata omd = new ObjectMetadata();
        omd.setCacheControl("max-age=0");
        omd.setContentLength(data.length);

        return upload(omd, key, data);
    }

    @Async("threadPoolTaskExecutor")
    public void uploadAsync(String key, byte[] data) {
        ObjectMetadata omd = new ObjectMetadata();
        omd.setCacheControl("max-age=0");
        omd.setContentLength(data.length);

        upload(omd, key, data);
    }

    private PutObjectResult upload(ObjectMetadata omd, String key, byte[] data) {
        log.info("key: [{}]", key);

        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, stream, omd);
        putObjectRequest.setCannedAcl(CannedAccessControlList.PublicRead); // file permission

        return s3client.putObject(putObjectRequest); // upload file
    }

    public String getObjectAsStr(String key) throws IOException {
        S3Object res = s3client.getObject(new GetObjectRequest(bucketName, key));
        return retrieveTextFromStream(res.getObjectContent());
    }

    public Object getObject(String key) throws IOException {
        S3Object res = s3client.getObject(new GetObjectRequest(bucketName, key));
        String result = retrieveTextFromStream(res.getObjectContent());
        return CastUtils.jsonToObj(result);
    }

    public Object getObjectGzip(String key) throws IOException {
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);

        S3Object res = s3client.getObject(getObjectRequest);
        String result = retrieveTextFromStream(res.getObjectContent());
        return CastUtils.jsonToObj(result);
    }

    private String retrieveTextFromStream(InputStream input) throws IOException {
        StringBuilder sb = new StringBuilder();
        // Read the text input stream one line at a time and display each line.
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            char[] cbuf = new char[16384];
            int len;
            while ((len = reader.read(cbuf)) > 0) {
                sb.append(Arrays.copyOf(cbuf, len));
            }
        } finally {
            input.close();
        }
        return sb.toString();
    }

    public InputStream getObjectAsInputStream(String key) {
        S3Object res = s3client.getObject(new GetObjectRequest(bucketName, key));
        return res.getObjectContent();
    }

    public ObjectMetadata hasObject(String key) {
        if(s3client.doesObjectExist(bucketName, key)) {
            return s3client.getObjectMetadata(bucketName, key);
        } else {
            return null;
        }
    }

    public boolean isObjExist(String key) {
        return s3client.doesObjectExist(bucketName, key);
    }

    public CopyObjectResult copyObject(String sourceKey, String destKey) {
        return s3client.copyObject(bucketName, sourceKey, bucketName, destKey);
    }

    public List<String> listObjects(String prefix) {
        ObjectListing ol = s3client.listObjects(bucketName, prefix);
        List<S3ObjectSummary> list = ol.getObjectSummaries();
        return list.stream()
                .map(e -> e.getKey())
                .collect(Collectors.toList());
    }

    public String retrieveKey(String fileUrl) {
        return fileUrl.replaceAll(cdnUrl + "/", "");
    }

    @Async("threadPoolTaskExecutor")
    public void deleteFileFromCdnUrl(String url) {
        if(Strings.isNullOrEmpty(url))
            return;

        if(url.startsWith(cdnUrl)) {
            String key = retrieveKey(url);
            delete(key);
        }
    }

    @Async("threadPoolTaskExecutor")
    public void delete(String key) {
        try {
            s3client.deleteObject(bucketName, key);
        } catch(Exception e) {
            log.error("error occurred while delete file in S3.", e);
        }
    }

    @Async("threadPoolTaskExecutor")
    public void deleteFilesFromCdnUrls(List<String> urls) {
        if(urls == null || urls.isEmpty()) {
            return;
        }

        List<String> keys = Lists.newArrayList();
        urls.forEach(url -> {
            if(Strings.isNullOrEmpty(url))
                return;
            if(url.startsWith(cdnUrl)) {
                String key = retrieveKey(url);
                keys.add(key);
            }
        });

        if(keys.isEmpty() == false)
            delete(keys);
    }

    @Async("threadPoolTaskExecutor")
    public void delete(List<String> keys) {
        List<DeleteObjectsRequest.KeyVersion> keyList = Lists.newArrayList();
        for(String key : keys) {
            keyList.add(new DeleteObjectsRequest.KeyVersion(key));
        }

        DeleteObjectsRequest req = new DeleteObjectsRequest(bucketName).withKeys(keyList);
        s3client.deleteObjects(req);
    }
}
