package com.wesleyxbz.helpdesk.api.service;

import com.wesleyxbz.helpdesk.api.entity.User;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface UserService {

    User findByEmail(String email);

    User createOrUpdate(User user);

    Optional<User> findById(Long id);

    void deleteById(Long id);

    Page<User> findAll(int page, int count);

}
