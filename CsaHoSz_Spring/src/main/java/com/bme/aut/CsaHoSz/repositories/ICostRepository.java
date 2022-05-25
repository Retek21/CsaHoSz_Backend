package com.bme.aut.CsaHoSz.repositories;

import com.bme.aut.CsaHoSz.domain.Cost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICostRepository extends JpaRepository<Cost, Long> {

    public List<Cost> findAll();

    public Cost findDistinctByDescription(String description);

}
