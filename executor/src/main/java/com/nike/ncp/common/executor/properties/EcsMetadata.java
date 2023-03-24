package com.nike.ncp.common.executor.properties;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * EcsMetadata is for metadata of ecs container and task.
 * The empty metadata will be available in local.
 * Refer to <a href="https://docs.aws.amazon.com/AmazonECS/latest/userguide/task-metadata-endpoint-v4-fargate.html" />
 */
@Setter
public final class EcsMetadata {
    private static EcsMetadata instance;

    static void setInstance(EcsMetadata metaData) {
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

    private EcsMetadata() {
    }
}

@Slf4j
@Component
class EcsMetadataService {
    @PostConstruct
    public void init() {
        String url = System.getenv("ECS_CONTAINER_METADATA_URI_V4");
        if (url == null) {
            log.info("ECS_CONTAINER_METADATA_URI_V4 not found.");
            Resource resource = new ClassPathResource("ecs-metadata-empty.json");
            try {
                EcsMetadata ecsMetadata = new ObjectMapper().readValue(resource.getInputStream(), EcsMetadata.class);
                EcsMetadata.setInstance(ecsMetadata);
            } catch (IOException e) {
                log.error("EcsEnvMetadata init error : " + e.getMessage(), e);
            }
        } else {
            EcsMetadata.setInstance(new RestTemplate().getForEntity(url, EcsMetadata.class).getBody());
        }
    }
}
