package com.geo.presto.dao;

import com.geo.presto.bean.UserSql;
import com.geo.presto.bean.UserSqlExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UserSqlMapper {
    int countByExample(UserSqlExample example);

    int deleteByExample(UserSqlExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(UserSql record);

    int insertSelective(UserSql record);

    List<UserSql> selectByExample(UserSqlExample example);

    UserSql selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") UserSql record, @Param("example") UserSqlExample example);

    int updateByExample(@Param("record") UserSql record, @Param("example") UserSqlExample example);

    int updateByPrimaryKeySelective(UserSql record);

    int updateByPrimaryKey(UserSql record);
}