package com.bme.aut.CsaHoSz.repositories;

import com.bme.aut.CsaHoSz.domain.Competition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ICompetitionRepository extends JpaRepository<Competition, Long> {
    public List<Competition> findAll();
    public Competition findDistinctByName(String name);
}
