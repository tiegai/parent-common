package com.nike.ncp.common.model.proxy;

import com.nike.ncp.common.model.journey.Journey;
import com.nike.ncp.common.model.journey.JourneyActivity;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class DispatchedActivity {
    @NonNull
    private final Journey journey;
    @NonNull
    private final JourneyActivity activity;
}
