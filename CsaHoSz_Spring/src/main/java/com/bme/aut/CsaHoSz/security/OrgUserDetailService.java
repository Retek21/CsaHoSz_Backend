package com.bme.aut.CsaHoSz.security;

import com.bme.aut.CsaHoSz.domain.User.User;
import com.bme.aut.CsaHoSz.repositories.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class OrgUserDetailService implements UserDetailsService {

    @Autowired
    private IUserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repository.findDistinctByUserName(username);
        if (user == null)
            throw new UsernameNotFoundException(username + " is an invalid username");
        else
            return new OrgUserDetails(user);
    }
}
