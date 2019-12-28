package kid1999.upload.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import kid1999.upload.model.HomeWork;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface homeworkMapper extends BaseMapper<HomeWork> {

	@Update("update homework set total = total +1 where id = #{workId}")
	void incHomeWorkCountById(int workId);

	@Update("update homework set total = total -1 where id = #{workId}")
	void minusHomeWorkCountById(int workId);
}


