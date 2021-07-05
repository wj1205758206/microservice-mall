package microservice.mall.ware.dao;

import microservice.mall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品库存
 * 
 * @author wjian
 * @email wjian@gmail.com
 * @date 2021-07-04 11:22:57
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {
	
}
