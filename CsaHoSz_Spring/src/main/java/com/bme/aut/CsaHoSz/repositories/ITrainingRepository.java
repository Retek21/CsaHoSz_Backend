package com.bme.aut.CsaHoSz.repositories;

import com.bme.aut.CsaHoSz.domain.Training;
import com.bme.aut.CsaHoSz.domain.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ITrainingRepository extends JpaRepository<Training, Long> {

    public List<Training> findAll();

    public Training findDistinctByDateAndCoach(Date date, User coach);

    public List<Training> findAllByCoach(User coach);


    //public List<Training> findAllByParticipantsIsContaining();

}
