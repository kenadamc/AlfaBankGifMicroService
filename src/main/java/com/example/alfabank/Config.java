package com.example.alfabank;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties
public class Config {

    private String openexchangeratesServer;
    private String openexchangeratesId;
    private String giphyServer;
    private String giphyId;
    private String currency;

    public String getOpenexchangeratesid() {
        return openexchangeratesId;
    }

    public void setOpenexchangeratesid(String openexchangeratesid) {
        this.openexchangeratesId = openexchangeratesid;
    }

    public String getOpenexchangeratesServer() {
        return openexchangeratesServer;
    }

    public void setOpenexchangeratesServer(String openexchangeratesServer) {
        this.openexchangeratesServer = openexchangeratesServer;
    }
    public String getGiphyServer() {
        return giphyServer;
    }

    public void setGiphyServer(String giphyServer) {
        this.giphyServer = giphyServer;
    }

    public String getGiphyId() {
        return giphyId;
    }

    public void setGiphyId(String giphyId) {
        this.giphyId = giphyId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

}