package com.openjob.web.province;

import com.openjob.common.model.Province;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProvinceRepository extends JpaRepository<Province, Integer> {

    @Query("select p from Province p where p.name like %?1%")
    List<Province> findByName(String name);
}
