package microservice.mall.coupon.dao;

import microservice.mall.coupon.entity.SeckillPromotionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 秒杀活动
 * 
 * @author wjian
 * @email wjian@gmail.com
 * @date 2021-07-04 11:04:16
 */
@Mapper
public interface SeckillPromotionDao extends BaseMapper<SeckillPromotionEntity> {
	
}
