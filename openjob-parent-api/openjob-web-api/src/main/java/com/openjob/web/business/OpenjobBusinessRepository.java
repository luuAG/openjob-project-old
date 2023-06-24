package com.openjob.web.business;

import com.openjob.common.model.OpenjobBusiness;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpenjobBusinessRepository extends JpaRepository<OpenjobBusiness, Integer> {
}
