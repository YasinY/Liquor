package com.liquor.launcher.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class CiscoCommand {

    @Builder.Default
    private String name = "";

    @Builder.Default
    private String description = "";
}
