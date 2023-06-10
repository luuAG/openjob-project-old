package com.openjob.admin.district;

import com.openjob.common.model.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DistrictRepository extends JpaRepository<District, Integer> {

    @Query("select p from District p where p.name like %?1%")
    List<District> findByName(String name);

    @Query("select d from Province p join p.districts d where p.name like ?1 and d.name like %?2%")
    List<District> searchByProvinceAndName(String province, String keyword);
}
