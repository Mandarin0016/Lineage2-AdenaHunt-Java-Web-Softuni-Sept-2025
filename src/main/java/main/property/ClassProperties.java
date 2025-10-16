package main.property;

import lombok.Data;
import main.config.YamlPropertySourceFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@ConfigurationProperties
@PropertySource(value = "class-details.yaml", factory = YamlPropertySourceFactory.class)
public class ClassProperties {

    // TODO: Load class properties here.
}
