package kid1999.upload.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@TableName("userswork")
public class Userwork {
  @TableId(type = IdType.AUTO)
  private int id;
  private int workid;
  private int userid;
}
