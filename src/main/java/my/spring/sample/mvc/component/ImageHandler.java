package my.spring.sample.mvc.component;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import my.spring.sample.mvc.consts.Constants;
import my.spring.sample.mvc.model.ImageModel;
import my.spring.sample.mvc.model.Dimension;
import my.spring.sample.mvc.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;


@Component
public class ImageHandler {

    @Autowired
    private AwsS3FileManager s3FileManager;

    @Value("${aws.cdn.url}")
    private String cdnUrl;

    public String genUrl(String path) {
        return cdnUrl + "/" + path;
    }

    private ImageModel doUploadImage(String image, String prefix, String imageFormat, boolean withThumbnail) throws IOException {
        String data = image.replaceAll("^data:image\\/\\w+;base64,", "");
        byte[] byteData = Base64.getDecoder().decode(data);
        Dimension imageDimension = this.getImageDimension(byteData);
        String fileName = StringUtils.genUuid();

        String key = prefix + "/" + fileName + "." + imageFormat;
        s3FileManager.uploadImage(key, byteData, imageFormat);

        ImageModel archiImage = new ImageModel();
        archiImage.setOriginal(genUrl(key));
        archiImage.setWidth(imageDimension.getWidth());
        archiImage.setHeight(imageDimension.getHeight());

        if(withThumbnail) {
            byte[] tData = this.thumbnail(byteData, imageFormat);
            String tKey = prefix + "/" + Constants.THUMBNAIL_PREFIX + fileName + "." + imageFormat;
            s3FileManager.uploadImage(tKey, tData, imageFormat);
            archiImage.setThumbnail(genUrl(tKey));
        }
        return archiImage;
    }

    public ImageModel uploadImage(String image, String prefix, boolean withThumbnail) throws Exception {
        if(image.startsWith("data:image/png")) {
            return doUploadImage(image, prefix, "png", withThumbnail);
        } else if(image.startsWith("data:image/jpeg") || image.startsWith("data:image/jpg")){
            return doUploadImage(image, prefix, "jpeg", withThumbnail);
        } else if(image.startsWith("data:image/svg+xml")) {
            return doUploadImage(image, prefix, "svg", withThumbnail);
        } else if(image.startsWith("data:image/gif")) {
            return doUploadImage(image, prefix, "gif", withThumbnail);
        }
        else {
            String[] arr = image.split(",");
            throw new Exception("Does not support this format of image: [" + arr[0] + "]");
        }
    }

    @Async("threadPoolTaskExecutor")
    public void deleteImageFromUrl(String url) {
        if(Strings.isNullOrEmpty(url))
            return;

        if(url.startsWith(cdnUrl)) {
            String key = retrieveKey(url);
            deleteImage(key);
        }
    }

    @Async("threadPoolTaskExecutor")
    public void deleteImageFromUrls(List<String> urls) {
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
            deleteImages(keys);
    }

    public void deleteImage(String key) {
        s3FileManager.delete(key);
    }

    public void deleteImages(List<String> keys) {
        s3FileManager.delete(keys);
    }

    public boolean copyImage(String sourceUrl, String destKey) {
        String sourceKey = retrieveKey(sourceUrl);
        if(Strings.isNullOrEmpty(sourceKey) || sourceKey.startsWith("http")) {
            return false;
        }
        if(s3FileManager.isObjExist(sourceKey)) {
            s3FileManager.copyObject(sourceKey, destKey);
            return true;
        }
        return false;
    }

    public String copyImageReturnDestUrl(String sourceUrl, String destPrefix) {
        String[] arr = sourceUrl.split("/");
        String destKey = destPrefix + "/" + arr[arr.length-1];
        if(this.copyImage(sourceUrl, destKey)) {
            return genUrl(destKey);
        }
        return null;
    }

    public String retrieveKey(String imageUrl) {
        return imageUrl.replaceAll(cdnUrl + "/", "");
    }

    private Dimension getImageDimension(byte[] imageData) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
        BufferedImage oImage = ImageIO.read(bais);
        Dimension imageDimension = new Dimension();
        imageDimension.setWidth(oImage.getWidth());
        imageDimension.setHeight(oImage.getHeight());
        return imageDimension;
    }

    private byte[] thumbnail(byte[] imageData, String ext) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
        BufferedImage oImage = ImageIO.read(bais);

        double ratio = 2; // 이미지 축소 비율
        int tWidth = oImage.getWidth(), tHeight = oImage.getHeight();
        while(true) {
            if(tWidth <= 200 || tHeight <= 200) {
                break;
            }
            tWidth = (int) (oImage.getWidth() / ratio); // 생성할 썸네일이미지의 너비
            tHeight = (int) (oImage.getHeight() / ratio); // 생성할 썸네일이미지의 높이
            ratio += 1;
        }

        int imageType = BufferedImage.TYPE_3BYTE_BGR;
        if(ext.equals("png")) {
            imageType = BufferedImage.TYPE_4BYTE_ABGR;
        }

        BufferedImage tImage = new BufferedImage(tWidth, tHeight, imageType); // 썸네일이미지
        Graphics2D graphic = tImage.createGraphics();
        Image image = oImage.getScaledInstance(tWidth, tHeight, Image.SCALE_SMOOTH);
        graphic.drawImage(image, 0, 0, tWidth, tHeight, null);
        graphic.dispose(); // 리소스를 모두 해제

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(tImage, ext, baos);

        return baos.toByteArray();
    }
}
