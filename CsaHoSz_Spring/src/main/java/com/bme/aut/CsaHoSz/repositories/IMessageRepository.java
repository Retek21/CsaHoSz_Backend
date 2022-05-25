package com.bme.aut.CsaHoSz.repositories;

import com.bme.aut.CsaHoSz.domain.Message;
import com.bme.aut.CsaHoSz.domain.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IMessageRepository extends JpaRepository<Message, Long> {

    public List<Message> findAll();

    public List<Message> findAllBySender(User sender);

    public List<Message> findAllByReceiver(User receiver);

    public List<Message> findAllBySenderAndAndReceiver(User sender, User receiver);

}
