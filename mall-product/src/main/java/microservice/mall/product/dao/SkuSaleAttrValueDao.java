package microservice.mall.product.dao;

import microservice.mall.product.entity.SkuSaleAttrValueEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * sku销售属性&值
 * 
 * @author wjian
 * @email wjian@gmail.com
 * @date 2021-07-04 09:48:57
 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity> {
	
}
