package microservice.mall.product.dao;

import microservice.mall.product.entity.AttrGroupEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 属性分组
 * 
 * @author wjian
 * @email wjian@gmail.com
 * @date 2021-07-04 09:48:57
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {
	
}
