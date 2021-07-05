package microservice.mall.ware.dao;

import microservice.mall.ware.entity.WareInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 仓库信息
 * 
 * @author wjian
 * @email wjian@gmail.com
 * @date 2021-07-04 11:22:57
 */
@Mapper
public interface WareInfoDao extends BaseMapper<WareInfoEntity> {
	
}
