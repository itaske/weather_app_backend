package com.tryforge.application.controller;


import com.tryforge.application.config.SecurityConstants;
import com.tryforge.application.model.User;
import com.tryforge.application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.*;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserRepository userRepository;

    private  final PasswordEncoder passwordEncoder;

    public UserController(PasswordEncoder passwordEncoder){
        this.passwordEncoder = passwordEncoder;
    }


    @GetMapping("/")
    public ResponseEntity<?> getUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else
            return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable("id") Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }




    @PostMapping("/")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        return createNewUser(user);
    }

    public ResponseEntity<?> createNewUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User u = userRepository.save(user);
        return ResponseEntity.ok(u);
    }

    @PutMapping("/")
    public ResponseEntity<?> editUser(@RequestBody User user) {
        User u = userRepository.saveAndFlush(user);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signup(@RequestBody User user) {
        return createNewUser(user);
    }

//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody Map<String, Object> loginDetails) {
//        String email = (String) loginDetails.get("email");
//        if (!email.isEmpty()) {
//            Optional<User> optionalUser = userRepository.findByEmail(email);
//            if (optionalUser.isPresent()) {
//                String inputPassword = (String) loginDetails.get("password");
//                String realPassword = optionalUser.get().getPassword();
//                if (User.bCryptPasswordEncoder.matches(inputPassword, realPassword)) {
//                    String token = com.auth0.jwt.JWT.create()
//                            .withSubject(email)
//                            .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
//                            .sign(HMAC512(SecurityConstants.SECRET.getBytes()));
//                    Map<String, Object> response = new HashMap<>();
//                    response.put("id", optionalUser.get().getId());
//                    response.put("token", token);
//                    return ResponseEntity.ok(response);
//                } else {
//                    return ResponseEntity.notFound().build();
//                }
//            } else {
//                return ResponseEntity.notFound().build();
//            }
//
//        }
//        return ResponseEntity.notFound().build();
//    }

}