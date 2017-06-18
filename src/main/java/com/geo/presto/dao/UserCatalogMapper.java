package com.geo.presto.dao;

import com.geo.presto.bean.UserCatalogExample;
import com.geo.presto.bean.UserCatalogKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UserCatalogMapper {
    int countByExample(UserCatalogExample example);

    int deleteByExample(UserCatalogExample example);

    int deleteByPrimaryKey(UserCatalogKey key);

    int insert(UserCatalogKey record);

    int insertSelective(UserCatalogKey record);

    List<UserCatalogKey> selectByExample(UserCatalogExample example);

    int updateByExampleSelective(@Param("record") UserCatalogKey record, @Param("example") UserCatalogExample example);

    int updateByExample(@Param("record") UserCatalogKey record, @Param("example") UserCatalogExample example);
}