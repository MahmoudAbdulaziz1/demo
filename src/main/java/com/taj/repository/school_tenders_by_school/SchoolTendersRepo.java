package com.taj.repository.school_tenders_by_school;

import com.taj.entity.school_tenders_by_school.SchoolTenderEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by User on 10/16/2018.
 */
public interface SchoolTendersRepo extends CrudRepository<SchoolTenderEntity, Integer> {
}
