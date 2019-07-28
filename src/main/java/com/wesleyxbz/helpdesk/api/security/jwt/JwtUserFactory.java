package com.wesleyxbz.helpdesk.api.security.jwt;

import com.wesleyxbz.helpdesk.api.entity.User;
import com.wesleyxbz.helpdesk.api.enums.ProfileEnum;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

public class JwtUserFactory {

    private JwtUserFactory() {
    }

    // CONVERTE O E GERA UM JWT USER COM BASE NOS DADOS DE UM USUÁRIO
    public static JwtUser create(User user) {
        return new JwtUser(user.getId(), user.getEmail(), user.getPassword(), mapToGrantedAuthorities(user.getProfile()));
    }

    // CONVERTE O PERFIL DE USUÁRIO PAR AO FORMATO UTILIZADO PELO SPRING SECURITY
    private static List<GrantedAuthority> mapToGrantedAuthorities(ProfileEnum profileEnum) {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority(profileEnum.toString()));
        return authorities;
    }

}
