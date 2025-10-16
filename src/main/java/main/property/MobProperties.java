package main.property;

import lombok.Data;
import main.config.YamlPropertySourceFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@ConfigurationProperties
@PropertySource(value = "mob-details.yaml", factory = YamlPropertySourceFactory.class)
public class MobProperties {

    // TODO: Load mob properties here.

}
