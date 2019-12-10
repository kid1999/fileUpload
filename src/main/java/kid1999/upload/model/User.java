package kid1999.upload.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable {

  @TableId(type = IdType.AUTO)    // 自增
  private int id;
  private String name;
  private String password;
}
