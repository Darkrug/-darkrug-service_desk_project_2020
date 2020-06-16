package com.darkcircle.crmProject.repositories;

import com.darkcircle.crmProject.models.Request;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findByName(String name, Pageable page);
    List<Request> findByResponsible(String responsible, Pageable page);

}
