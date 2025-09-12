package com.example.music.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Configuration
@EnableR2dbcRepositories(basePackages = "com.example.music.infrastructure.r2dbc.repository")
@EnableR2dbcAuditing
public class R2dbcConfig {

}
