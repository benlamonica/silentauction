package us.pojo.silentauction;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StreamUtils;

import us.pojo.silentauction.service.UserDetailService;

@SpringBootApplication
@EnableAutoConfiguration
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class App extends WebSecurityConfigurerAdapter {
    
    private static final Logger log = LoggerFactory.getLogger(App.class);
    
    @Value("${silentauction.asset-dir}")
    private String assetDir;

    @Value("${silentauction.secret-key}")
    private String secretKey;

    @Autowired
    private UserDetailService userDetailsService;
     
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeRequests()
                .antMatchers("/webjars/**", "/site.css", "/create-account.html", "/login.html").permitAll()
                .anyRequest().authenticated()
                .and()
            .rememberMe()
                .alwaysRemember(true)
                .tokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(14))
                .key(secretKey)
                .and()
            .formLogin()
                .loginPage("/login.html")
                .permitAll()
                .and()
            .logout()
                .permitAll();
    }
    
    /**
     * sets up the external directories in preparation for running the first time
     */
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
            StreamUtils.copy(new ClassPathResource("/static/item.jpg").getInputStream(), new FileOutputStream(assetDir + "images/-1.jpg"));
        } catch (Exception e) {
            log.warn("Unable to copy default item image.", e);
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
