package com.itechart.warehouse.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.itechart.warehouse.deserializer.TrimmingJsonDeserializer;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Data transfer object containing criteria for searching goodsIdList.
 */
@Setter
@Getter
@lombok.ToString
public class GoodsSearchDTO {
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String name;
    private BigDecimal minQuantity;
    private BigDecimal maxQuantity;
    private BigDecimal minWeight;
    private BigDecimal maxWeight;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String storageType;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String quantityUnit;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String weightUnit;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String priceUnit;
    private List<GoodsStatusSearchDTO> statuses;
    @JsonDeserialize(using = TrimmingJsonDeserializer.class)
    private String currentStatus;
    private Long incomingInvoiceId;
    private Long outgoingInvoiceId;
    private Boolean actApplicable;

    public void addStatusSearchDTO(GoodsStatusSearchDTO statusSearchDTO) {
        if (statuses != null)
            statuses.add(statusSearchDTO);
        else throw new AssertionError();
    }

    public void removeStatusSearchDTO(GoodsStatusSearchDTO statusSearchDTO) {
        if (statuses != null)
            statuses.remove(statusSearchDTO);
        else throw new AssertionError();

    }


}
