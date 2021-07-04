package microservice.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import microservice.mall.common.utils.PageUtils;
import microservice.mall.order.entity.OrderEntity;

import java.util.Map;

/**
 * 订单
 *
 * @author wjian
 * @email wjian@gmail.com
 * @date 2021-07-04 11:20:10
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

