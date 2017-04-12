package us.pojo.silentauction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.opensaml.util.resource.ClasspathResource;
import org.opensaml.util.resource.ResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StreamUtils;

@SpringBootApplication
@EnableAutoConfiguration
@Configuration
public class App {
    
    private static final Logger log = LoggerFactory.getLogger(App.class);
    
    @Value("${silentauction.asset-dir}")
    private String assetDir;
    
    @PostConstruct
    public void createDirs() {
        Arrays.asList(assetDir, assetDir+"images", assetDir+"images/thumbs").forEach(dir -> {
            File directory = new File(dir);
            if (!directory.exists() && !directory.mkdirs()) {
                log.error("Unable to create asset directory '{}'", directory.getAbsolutePath());
            } else {
                log.info("Asset Directory created at '{}'", directory.getAbsolutePath());
            }
        });
        
        // copy the default image over
        try {
            StreamUtils.copy(new ClasspathResource("/static/item.jpg").getInputStream(), new FileOutputStream(assetDir + "images/-1.jpg"));
        } catch (Exception e) {
            log.warn("Unable to copy default item image.", e);
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
