package kid1999.upload.model;

import lombok.Data;

import java.sql.Time;

@Data
public class Student {
  private int id;
  private String name;
  private String classname;
  private int zyid;
  private Time time;

  @Override
  public String toString() {
    return "Student{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", classname='" + classname + '\'' +
            ", zyid=" + zyid +
            ", time=" + time +
            '}';
  }
}
