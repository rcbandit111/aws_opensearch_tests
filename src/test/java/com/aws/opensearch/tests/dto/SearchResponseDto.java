package com.aws.opensearch.tests.dto;

import lombok.Data;

import java.util.List;

@Data
public class SearchResponseDto {
  private List<MerchantDto> merchants;
  private Integer offset;
  private Integer limit;
  private Long totalCount;
}
