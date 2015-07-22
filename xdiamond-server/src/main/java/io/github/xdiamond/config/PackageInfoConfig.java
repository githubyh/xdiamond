package io.github.xdiamond.config;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

@Configuration
public class PackageInfoConfig {
  @Autowired
  ApplicationContext applicationContext;

  @Value("/META-INF/maven/${app.groupId}/${app.artifactId}/pom.properties")
  String mavenPomProperties;

  @Value("/META-INF/MAINFEST.MF")
  String mainfest;

  @Bean(name = "packageProperties")
  public Properties packageInfo() throws IOException {
    Properties packageProperties = new Properties();

    Resource pomResource = applicationContext.getResource(mavenPomProperties);
    if (pomResource.exists()) {
      // #Generated by Maven
      // #Wed Jul 22 13:09:50 CST 2015
      // version=0.0.1-SNAPSHOT
      // groupId=io.github.xdiamond
      // artifactId=xdiamond-server

      byte[] pomData = FileCopyUtils.copyToByteArray(pomResource.getInputStream());
      packageProperties.load(new ByteArrayInputStream(pomData));

      BufferedReader br = new BufferedReader(new StringReader(new String(pomData)));
      List<String> lines = new ArrayList<String>();
      for (String line = br.readLine(); line != null; line = br.readLine()) {
        lines.add(line);
      }
      // try to get package time
      if (lines.size() >= 2 && lines.get(1).startsWith("#")) {
        String time = lines.get(1).substring(1);
        packageProperties.put("packageTime", time);
      }
    }

    // Manifest-Version: 1.0
    // Archiver-Version: Plexus Archiver
    // Built-By: hengyunabc
    // Created-By: Apache Maven 3.3.1
    // Build-Jdk: 1.8.0_45
    Resource mainfestResource = applicationContext.getResource(mainfest);
    if (mainfestResource.exists()) {
      Properties mainfestProperties = new Properties();
      mainfestProperties.load(mainfestResource.getInputStream());
      packageProperties.putAll(mainfestProperties);
    }

    return packageProperties;
  }
}
