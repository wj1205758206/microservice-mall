package microservice.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import microservice.mall.common.utils.PageUtils;
import microservice.mall.ware.entity.PurchaseEntity;

import java.util.Map;

/**
 * 采购信息
 *
 * @author wjian
 * @email wjian@gmail.com
 * @date 2021-07-04 11:22:57
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

