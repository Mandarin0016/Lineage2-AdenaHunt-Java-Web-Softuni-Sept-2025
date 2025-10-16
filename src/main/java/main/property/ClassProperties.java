package main.property;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import main.config.YamlPropertySourceFactory;
import main.model.BoosterType;
import main.model.PlayerClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties
@PropertySource(value = "class-details.yaml", factory = YamlPropertySourceFactory.class)
public class ClassProperties {

    private List<ClassDetails> classes;

    // Много важно!!
    @Data
    public static class ClassDetails {

        private PlayerClass playerClass;
        private int healthFactor;
        private int attackFactor;
        private int defenseFactor;
        private String bannerImg;
        private List<Booster> boosters;
    }

    @Data
    public static class Booster {

        private String name;
        private double value;
        private BoosterType type;
        private String icon;
    }

    public ClassDetails getDetailsByPlayerClass(PlayerClass playerClass) {

        return this.classes.stream().filter(d -> d.getPlayerClass() == playerClass).findFirst().get();
    }

//    @PostConstruct
//    public void test() {
//        System.out.println();
//    }
}
