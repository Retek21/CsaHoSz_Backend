package com.bme.aut.CsaHoSz.repositories;


import com.bme.aut.CsaHoSz.domain.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {


    public User findDistinctByUserName(String username);

    public User findDistinctByEmail(String email);

    public List<User> findAll();

    @Transactional
    public long deleteUserByUserName(String username);

    @Transactional
    @Modifying
    @Query("update User u set u.userName = ?1 where u.userName = ?2")
    int setUserName(String newUserName, String oldUserName);

    @Transactional
    @Modifying
    @Query("update User u set u.name = ?1 where u.userName = ?2")
    int setName(String newName, String userName);

    @Transactional
    @Modifying
    @Query("update User u set u.email = ?1 where u.userName = ?2")
    int setEmail(String newEmail, String userName);

    @Transactional
    @Modifying
    @Query("update User u set u.password = ?1 where u.userName = ?2")
    int setPassword(String newPassword, String userName);

    @Transactional
    @Modifying
    @Query("update User u set u.role = ?1 where u.userName = ?2")
    int setRole(String newRole, String userName);

    @Transactional
    @Modifying
    @Query("update User u set u.newsletterSubscription = ?1 where u.userName = ?2")
    int setSubscription(boolean subscription, String userName);
}
