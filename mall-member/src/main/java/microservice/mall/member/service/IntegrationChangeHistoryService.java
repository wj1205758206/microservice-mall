package microservice.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import microservice.mall.common.utils.PageUtils;
import microservice.mall.member.entity.IntegrationChangeHistoryEntity;

import java.util.Map;

/**
 * 积分变化历史记录
 *
 * @author wjian
 * @email wjian@gmail.com
 * @date 2021-07-04 11:12:41
 */
public interface IntegrationChangeHistoryService extends IService<IntegrationChangeHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

