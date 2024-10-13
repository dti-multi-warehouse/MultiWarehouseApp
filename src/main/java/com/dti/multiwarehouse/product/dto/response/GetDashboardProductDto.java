package com.dti.multiwarehouse.product.dto.response;

import java.util.List;

public record GetDashboardProductDto(int totalPage, List<ProductSummaryResponseDto> products) {
}
