package com.itechart.warehouse.dto;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WarehouseCustomerCompanyDTO {
    private Long id;
    private String name;
    private Float x;
    private Float y;
    private Long warehouseCompanyId;
}
