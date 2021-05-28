package Chanket.Server.Data.WrapperMapper;

import Chanket.Server.Data.Entities.AccountEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AccountMapper extends BaseMapper<AccountEntity> {

    @Select("SELECT * FROM Accounts WHERE username=#{username}")
    AccountEntity username2entity(String username);
}
