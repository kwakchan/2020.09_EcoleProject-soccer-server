package com.ksu.soccerserver.team;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "logo")
@Component
@Getter @Setter
public class TeamImageProperties {

    private String location;
}