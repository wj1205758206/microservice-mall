package microservice.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import microservice.mall.common.utils.PageUtils;
import microservice.mall.product.entity.BrandEntity;

import java.util.Map;

/**
 * 品牌
 *
 * @author wjian
 * @email wjian@gmail.com
 * @date 2021-07-04 09:48:57
 */
public interface BrandService extends IService<BrandEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

