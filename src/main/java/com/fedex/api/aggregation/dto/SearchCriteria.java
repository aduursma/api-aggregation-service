package com.fedex.api.aggregation.dto;

import lombok.Data;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class SearchCriteria {

    private List<@Size(min = 2, max = 2) String> pricing;
    private List<@Pattern(regexp = "^\\d{9}$") String> track;
    private List<@Pattern(regexp = "^\\d{9}$") String> shipments;

}
