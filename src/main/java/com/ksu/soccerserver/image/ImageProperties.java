package com.ksu.soccerserver.image;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "image")
@Component
@Getter @Setter
public class ImageProperties {

    private String location;
}