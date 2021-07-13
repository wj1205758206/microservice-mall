package microservice.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import microservice.mall.common.utils.PageUtils;
import microservice.mall.product.entity.SkuInfoEntity;
import microservice.mall.product.entity.SpuInfoEntity;
import microservice.mall.product.vo.Skus;

import java.util.List;
import java.util.Map;

/**
 * sku信息
 *
 * @author wjian
 * @email wjian@gmail.com
 * @date 2021-07-04 09:48:57
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuInfo(SpuInfoEntity spuInfoEntity, List<Skus> skus);

    PageUtils queryPageByCondition(Map<String, Object> params);
}

