package com.ulstu.pharmacy.report.facade.util;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Style {

    private Integer fontSize;

    private boolean isBold;

    private boolean isInCenter;
}