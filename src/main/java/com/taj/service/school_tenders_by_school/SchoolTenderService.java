package com.taj.service.school_tenders_by_school;

import com.taj.entity.school_tenders_by_school.SchoolTenderEntity;
import com.taj.repository.school_tenders_by_school.SchoolTendersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 10/16/2018.
 */
@Service
public class SchoolTenderService {

    @Autowired
    SchoolTendersRepo repo;
    public List<SchoolTenderEntity> getAll(){
        List<SchoolTenderEntity> res = new ArrayList<>();
        repo.findAll().forEach(res::add);
        return  res;
    }
}
