package com.nike.ncp.common.model;

import com.nike.ncp.common.model.journey.Journey;
import com.nike.ncp.common.model.journey.JourneyActivity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DispatchedActivity {
    private Journey journey;
    private JourneyActivity activity;
}
