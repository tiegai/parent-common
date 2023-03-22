package com.nike.ncp.common.executor.properties;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Setter
public final class EcsEnvMetaData {
    private static EcsEnvMetaData instance;

    static void setInstance(EcsEnvMetaData metaData) {
        instance = metaData;
    }

    @JsonProperty("DockerId")
    private String dockerId;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("DockerName")
    private String dockerName;
    @JsonProperty("Image")
    private String image;
    @JsonProperty("ImageID")
    private String imageID;
    @JsonProperty("Labels")
    private Map<String, String> labels;
    @JsonProperty("DesiredStatus")
    private String desiredStatus;
    @JsonProperty("KnownStatus")
    private String knownStatus;
    @JsonProperty("Limits")
    private Map<String, Integer> limits;
    @JsonProperty("CreatedAt")
    private String createdAt;
    @JsonProperty("StartedAt")
    private String startedAt;
    @JsonProperty("Type")
    private String type;
    @JsonProperty("Networks")
    private List<Network> networks;
    @JsonProperty("ContainerARN")
    private String containerARN;
    @JsonProperty("LogOptions")
    private Map<String, String> logOptions;
    @JsonProperty("LogDriver")
    private String logDriver;

    public static String getDockerId() {
        return instance.dockerId;
    }

    public static String getName() {
        return instance.name;
    }

    public static String getDockerName() {
        return instance.dockerName;
    }

    public static String getImage() {
        return instance.image;
    }

    public static String getImageID() {
        return instance.imageID;
    }

    public static Map<String, String> getLabels() {
        return instance.labels;
    }

    public static String getDesiredStatus() {
        return instance.desiredStatus;
    }

    public static String getKnownStatus() {
        return instance.knownStatus;
    }

    public static Map<String, Integer> getLimits() {
        return instance.limits;
    }

    public static String getCreatedAt() {
        return instance.createdAt;
    }

    public static String getStartedAt() {
        return instance.startedAt;
    }

    public static String getType() {
        return instance.type;
    }

    public static List<Network> getNetworks() {
        return instance.networks;
    }

    public static String getContainerARN() {
        return instance.containerARN;
    }

    public static Map<String, String> getLogOptions() {
        return instance.logOptions;
    }

    public static String getLogDriver() {
        return instance.logDriver;
    }

    @Data
    public static class Network {
        @JsonProperty("NetworkMode")
        private String networkMode;
        @JsonProperty("IPv4Addresses")
        private List<String> iPv4Addresses;
        @JsonProperty("AttachmentIndex")
        private Integer attachmentIndex;
        @JsonProperty("MACAddress")
        private String mACAddress;
        @JsonProperty("IPv4SubnetCIDRBlock")
        private String iPv4SubnetCIDRBlock;
        @JsonProperty("DomainNameServers")
        private List<String> domainNameServers;
        @JsonProperty("DomainNameSearchList")
        private List<String> domainNameSearchList;
        @JsonProperty("PrivateDNSName")
        private String privateDNSName;
        @JsonProperty("SubnetGatewayIpv4Address")
        private String subnetGatewayIpv4Address;
    }

    private EcsEnvMetaData() {
    }
}

@Component
class EcsEnvMetaDataService {
    @PostConstruct
    public void metadata() {
        String url = System.getenv("ECS_CONTAINER_METADATA_URI_V4");
        EcsEnvMetaData.setInstance(new RestTemplate().getForEntity(url, EcsEnvMetaData.class).getBody());
    }
}
