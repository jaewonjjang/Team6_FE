package com.kaspi.backend.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FindGasStationReqDto {
    private Long stationNo;
    private String area;
    private String name;
    private String address;
    private String brand;
}
