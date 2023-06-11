package com.openjob.common.model;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table
public class OpenjobBusiness  {
    @Id
    private int id;

    private Integer freeJob;
    private Integer maxTimeForFreeJobInDays;

    private Integer freeCvView;

    private Double baseJobPricePerDay;
    private Double baseCvViewPrice;
    private Double internWeight;
    private Double fresherWeight;
    private Double juniorWeight;
    private Double middleWeight;
    private Double seniorWeight;
    private Double highPositionWeight;

    // premium
    private Integer premiumFreeJob;
    private Integer premiumFreeViewCv;
    private Double premiumPrice;
}