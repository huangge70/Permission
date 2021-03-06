package com.mmall.service;

import com.google.common.base.Preconditions;
import com.mmall.dao.SysDeptMapper;
import com.mmall.exception.ParamException;
import com.mmall.model.SysDept;
import com.mmall.param.DeptParam;
import com.mmall.util.BeanValidator;
import com.mmall.util.LevelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class SysDeptService {
    @Resource
    private SysDeptMapper sysDeptMapper;
    public void save(DeptParam param){
        BeanValidator.check(param);
        if(ckeckExist(param.getParentId(),param.getName(),param.getId())){
            throw new ParamException("该部门已存在");
        }
        SysDept dept=SysDept.builder().name(param.getName()).parentId(param.getParentId())
                     .seq(param.getSeq()).remark(param.getRemark()).build();
        dept.setLevel(LevelUtil.calculateLevel(getLevel(param.getParentId()),param.getParentId()));
        dept.setOperator("system");//TODO
        dept.setOperateIp("127.0.0.1");//TODO
        dept.setOperateTime(new Date());
        sysDeptMapper.insertSelective(dept);
    }
    public void update(DeptParam param){
        BeanValidator.check(param);
        if(ckeckExist(param.getParentId(),param.getName(),param.getId())){
            throw new ParamException("该部门已存在");
        }
        SysDept before=sysDeptMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before,"待更新的部门不存在");
        if(ckeckExist(param.getParentId(),param.getName(),param.getId())){
            throw new ParamException("该部门已存在");
        }
        SysDept after=SysDept.builder().id(param.getId()).name(param.getName()).parentId(param.getParentId())
                .seq(param.getSeq()).remark(param.getRemark()).build();
        after.setLevel(LevelUtil.calculateLevel(getLevel(param.getParentId()),param.getParentId()));
        after.setOperator("system-update");//TODO
        after.setOperateIp("127.0.0.1");//TODO
        after.setOperateTime(new Date());

        updateWithChild(before,after);
    }
    //要更新当前部门所在节点，以及更新当前部门的子部门
    //要保证更新要么全成功，要么都失败
    @Transactional
    void updateWithChild(SysDept before, SysDept after){
        String newLevelPrefix=after.getLevel();
        String oldLevelPrefix=before.getLevel();
        if(!newLevelPrefix.equals(oldLevelPrefix)){
            //取出当前部门的子部门,还可能有子部门的子部门
            List<SysDept> deptList=sysDeptMapper.getChildDeptListByLevel(before.getLevel());
            if(CollectionUtils.isNotEmpty(deptList)){
                for(SysDept dept:deptList){
                    String level=dept.getLevel();
                    if(level.indexOf(oldLevelPrefix)==0){
                        level=newLevelPrefix+ level.substring(oldLevelPrefix.length());
                        dept.setLevel(level);
                    }
                }
                sysDeptMapper.batchUpdateLevel(deptList);
            }
        }
        sysDeptMapper.updateByPrimaryKey(after);
    }
    private boolean ckeckExist(Integer parentId,String deptName,Integer deptId){
        return sysDeptMapper.countByNameAndParentId(parentId,deptName,deptId)>0;
    }
    private String getLevel(Integer deptId){
        SysDept dept=sysDeptMapper.selectByPrimaryKey(deptId);
        if(dept==null){
            return null;
        }
        return dept.getLevel();
    }
}
