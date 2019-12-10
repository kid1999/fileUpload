package kid1999.upload.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.omg.CORBA.IDLType;

@Data
@TableName("homework")
public class HomeWork {

  @TableId(type = IdType.AUTO)    // 自增
  private int id;
  private String title;
  private String infomation;
  private long createtime;
  private long endtime;
  private String type;
  private String addr;
}
