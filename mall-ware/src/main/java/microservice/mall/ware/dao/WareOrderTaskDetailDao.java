package microservice.mall.ware.dao;

import microservice.mall.ware.entity.WareOrderTaskDetailEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 库存工作单
 * 
 * @author wjian
 * @email wjian@gmail.com
 * @date 2021-07-04 11:22:57
 */
@Mapper
public interface WareOrderTaskDetailDao extends BaseMapper<WareOrderTaskDetailEntity> {
	
}
