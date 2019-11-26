package com.llc.community.mapper;

import com.llc.community.entity.Question;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface QuestionMapper {
    @Insert("insert into question (title,descrition,gmt_create,gmt_modified,creator,tag) values (#{title},#{descrition},#{gmtCreate},#{gmtModified},#{creator},#{tag})")
    void insert(Question question);
}
