package com.wesleyxbz.helpdesk.api.controller;

import com.wesleyxbz.helpdesk.api.entity.User;
import com.wesleyxbz.helpdesk.api.response.Response;
import com.wesleyxbz.helpdesk.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@RestController
@RequestMapping("api/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response<User>> create(HttpServletRequest requet, @RequestBody User user, BindingResult result) {

        Response<User> response = new Response<User>();

        try {
            validateCreateUser(user, result);
            if (result.hasErrors()) {
                result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
                return ResponseEntity.badRequest().body(response);
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User userPersisted = userService.createOrUpdate(user);
            response.setData(Optional.ofNullable(userPersisted));
        } catch (DuplicateKeyException dE) {
            response.getErrors().add("E-mail already registered !");
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }

    private void validateCreateUser(User user, BindingResult result) {
        if (user.getEmail() == null) {
            result.addError(new ObjectError("User", "Email não informado"));
        }
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response<User>> update(HttpServletRequest request, @RequestBody User user, BindingResult result) {
        Response<User> response = new Response<User>();

        try {
            validateUpdateUser(user, result);
            if (result.hasErrors()) {
                result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
                return ResponseEntity.badRequest().body(response);
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User userPersisted = userService.createOrUpdate(user);
            response.setData(Optional.ofNullable(userPersisted));
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }

    private void validateUpdateUser(User user, BindingResult result) {
        if (user.getId() == null) {
            result.addError(new ObjectError("User", "Id não informado"));
        }
        if (user.getEmail() == null) {
            result.addError(new ObjectError("User", "Email não informado"));
        }
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response<User>> findById(@PathVariable("id") Long id) {
        Response<User> response = new Response<User>();
        Optional<User> user = userService.findById(id);

        if (user == null) {
            response.getErrors().add("Usuário nao encontrado" + id);
            return ResponseEntity.badRequest().body(response);
        }

        response.setData(user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response<String>> delete(@PathVariable("id") Long id) {
        Response<String> response = new Response<String>();
        Optional<User> user = userService.findById(id);

        if (user == null) {
            response.getErrors().add("Usuário nao encontrado" + id);
            return ResponseEntity.badRequest().body(response);
        }

        userService.deleteById(id);
        return ResponseEntity.ok(new Response<String>());
    }

    @GetMapping(value = "{page}/{count}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response<Page<User>>> findAll(@PathVariable int page, @PathVariable int count) {
        Response<Page<User>> response = new Response<Page<User>>();
        Page<User> users = userService.findAll(page, count);
        response.setData(Optional.ofNullable(users));
        return ResponseEntity.ok(response);
    }

}
