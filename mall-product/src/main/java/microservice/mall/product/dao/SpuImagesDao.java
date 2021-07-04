package microservice.mall.product.dao;

import microservice.mall.product.entity.SpuImagesEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * spu图片
 * 
 * @author wjian
 * @email wjian@gmail.com
 * @date 2021-07-04 09:48:57
 */
@Mapper
public interface SpuImagesDao extends BaseMapper<SpuImagesEntity> {
	
}
