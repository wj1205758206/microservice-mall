package microservice.mall.common.to;

import lombok.Data;

@Data
public class SkuHasStockTo {
    private Long skuId;
    private boolean hasStock;
}
