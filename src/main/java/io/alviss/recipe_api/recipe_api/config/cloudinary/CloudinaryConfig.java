package io.alviss.recipe_api.recipe_api.config.cloudinary;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;

@Configuration
public class CloudinaryConfig {

    @Value("${cloudinary.cloudName}") private String cloudName;
    @Value("${cloudinary.apiKey}") private String apiKey;
    @Value("${cloudinary.apiSecret}") private String apiSecret;
    
    @Bean
    public Cloudinary cloudinaryConfig () {
        Cloudinary cloudinary = null;
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", cloudName);
        config.put("api_key", apiKey);
        config.put("api_secret", apiSecret);

        cloudinary = new Cloudinary(config);
        return cloudinary;
    }
}
