package com.example.music.config;

import lombok.Getter;

@Getter
public enum CacheType {

  TRENDS("trends", 5);

  private final String name;
  private final long secondsTTL;

  CacheType(String name, long secondsTTL) {
    this.name = name;
    this.secondsTTL = secondsTTL;
  }

}
