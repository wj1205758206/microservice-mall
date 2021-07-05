package microservice.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import microservice.mall.common.utils.PageUtils;
import microservice.mall.ware.entity.UndoLogEntity;

import java.util.Map;

/**
 * 
 *
 * @author wjian
 * @email wjian@gmail.com
 * @date 2021-07-04 11:22:57
 */
public interface UndoLogService extends IService<UndoLogEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

