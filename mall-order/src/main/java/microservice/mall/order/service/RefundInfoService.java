package microservice.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import microservice.mall.common.utils.PageUtils;
import microservice.mall.order.entity.RefundInfoEntity;

import java.util.Map;

/**
 * 退款信息
 *
 * @author wjian
 * @email wjian@gmail.com
 * @date 2021-07-04 11:20:10
 */
public interface RefundInfoService extends IService<RefundInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

