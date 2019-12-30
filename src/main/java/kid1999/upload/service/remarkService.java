package kid1999.upload.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import kid1999.upload.mapper.remarkMapper;
import kid1999.upload.model.Remark;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author kid1999
 * @title: remarkService
 * @date 2019/12/30 9:58
 */

@Slf4j
@Service
public class remarkService {

	@Autowired
	private remarkMapper remarkMapper;

	// 通过id 获取remarks
	public List<Remark> findRemarksByUserId(int userId) {
		QueryWrapper<Remark> wrapper = new QueryWrapper<>();
		wrapper.eq("user_id",userId);
		return remarkMapper.selectList(wrapper);
	}

	// 通过id修改是否已读
	public void changeReadById(int id) {
		Remark remark = remarkMapper.selectById(id);
		if(remark.getReaded() == 0){
			remark.setReaded(1);
			remarkMapper.updateById(remark);
		}else{
			remark.setReaded(0);
			remarkMapper.updateById(remark);
		}
	}

	// 类似上面只是不用查询
	public void changeReadByRemark(Remark remark) {
		if(remark.getReaded() == 0){
			remark.setReaded(1);
			remarkMapper.updateById(remark);
		}else{
			remark.setReaded(0);
			remarkMapper.updateById(remark);
		}
	}

	// 删除 remark
	public void deleteById(int id) {
		remarkMapper.deleteById(id);
	}
}
