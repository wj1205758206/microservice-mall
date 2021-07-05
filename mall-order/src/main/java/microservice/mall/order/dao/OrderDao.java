package microservice.mall.order.dao;

import microservice.mall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author wjian
 * @email wjian@gmail.com
 * @date 2021-07-04 11:20:10
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
