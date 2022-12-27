package com.nike.ncp.common.model.proxy;

import com.nike.ncp.common.model.journey.Journey;
import com.nike.ncp.common.model.journey.JourneyActivity;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class DispatchedActivity {
    private final Journey journey;
    private final JourneyActivity activity;
}
