package com.aws.opensearch.tests.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.opensearch.client.opensearch._types.SortOrder;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequestDto {
  private SearchOperator operator;

  private Map<String, List<String>> filterParams;
  private Map<String, String> searchParams;
  private Map<String, List<String>> multiMatchParams;
  private List<String> existsParams;
  private Map<String, RangeParamMinMax> rangeParams;

  private List<String> responseFields;
  private List<String> downloadFields;
  
  private String sortField;
  private SortOrder sortOrder;

  private Integer offset;
  private Integer limit;
  
  @Data
  @NotNull
  public static class RangeParamMinMax {
    @NotEmpty
    private String gte;
    @NotEmpty
    private String lte;
  }
}
