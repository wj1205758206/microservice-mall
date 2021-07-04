package microservice.mall.coupon.dao;

import microservice.mall.coupon.entity.SpuBoundsEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品spu积分设置
 * 
 * @author wjian
 * @email wjian@gmail.com
 * @date 2021-07-04 11:04:16
 */
@Mapper
public interface SpuBoundsDao extends BaseMapper<SpuBoundsEntity> {
	
}
