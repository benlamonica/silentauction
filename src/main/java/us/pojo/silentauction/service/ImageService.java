package us.pojo.silentauction.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import javax.imageio.ImageIO;

import org.hibernate.engine.jdbc.StreamUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import net.coobird.thumbnailator.Thumbnailator;

@Service
public class ImageService {

    @Value("${silentauction.asset-dir}")
    private String assetDir;
    
    public void save(String id, MultipartFile file) {
        try {
            if (!file.isEmpty()) {
                String imageFilename = assetDir + "images/" + id + ".jpg";
                String thumbFilename = assetDir + "images/thumbs/" + id + ".jpg";
                FileOutputStream image = new FileOutputStream(imageFilename);
                ByteArrayOutputStream mem = new ByteArrayOutputStream();
                StreamUtils.copy(file.getInputStream(), mem);
                BufferedImage bimg = ImageIO.read(new ByteArrayInputStream(mem.toByteArray()));
                int width = bimg.getWidth();
                int height = bimg.getHeight();
                if (width > 800 || height > 1600) {
                    Thumbnailator.createThumbnail(new ByteArrayInputStream(mem.toByteArray()), image, "jpeg", 800, 1600);
                } else {
                    ImageIO.write(bimg, "jpeg", new File(imageFilename));
                }
                
                if (width > 300 || height > 600) {
                    Thumbnailator.createThumbnail(new File(imageFilename), new File(thumbFilename), 300, 600);
                } else {
                    ImageIO.write(bimg, "jpeg", new File(thumbFilename));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to convert image.", e);
        }
    }
}
