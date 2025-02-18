package com.saz.se.goat.requestModel;

import com.saz.se.goat.model.Category;
import com.saz.se.goat.model.Tags;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequest
{

    private String title;
    private String description;
    private Category category;
    private long price;
    private long discountPercentage;
    private long rating;
    private int stock;
    private Tags tags;
    private String brand;
    private String size;
    private long weight;
    private String thumbnail;
    private boolean bestSeller;
    private boolean newArrival;
}
