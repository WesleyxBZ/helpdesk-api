package com.wesleyxbz.helpdesk.api.security.service;

import com.wesleyxbz.helpdesk.api.entity.User;
import com.wesleyxbz.helpdesk.api.security.jwt.JwtUserFactory;
import com.wesleyxbz.helpdesk.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsServiceImpl implements UserDetailsService {

    //SERVICO QUE IRÁ MANIPULAR A CLASSE USERDETAIS DO SPRIG

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userService.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", email));
        } else {
            return JwtUserFactory.create(user);
        }
    }

}
