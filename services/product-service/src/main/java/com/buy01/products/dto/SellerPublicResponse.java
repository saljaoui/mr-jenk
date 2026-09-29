package com.buy01.products.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SellerPublicResponse {
    private String id;
    private String name;
    private String avatar;
}
