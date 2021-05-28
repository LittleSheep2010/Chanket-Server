package Chanket.Server.Data.Entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@TableName(value = "Accounts")
public class AccountEntity {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String username;
    private String password;
    private String permission;
    private String uuid;

    private Long crtime;

    private Integer state;
}
