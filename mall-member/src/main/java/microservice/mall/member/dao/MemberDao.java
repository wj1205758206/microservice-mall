package microservice.mall.member.dao;

import microservice.mall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author wjian
 * @email wjian@gmail.com
 * @date 2021-07-04 11:12:41
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
