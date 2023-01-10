package com.nike.ncp.common.model.proxy;

import com.nike.ncp.common.model.journey.Journey;
import com.nike.ncp.common.model.journey.JourneyActivity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class DispatchedActivity {
    @NonNull
    private Journey journey;
    @NonNull
    private JourneyActivity activity;
}
