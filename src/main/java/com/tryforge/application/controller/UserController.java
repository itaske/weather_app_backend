package com.tryforge.application.controller;


import com.tryforge.application.model.User;
import com.tryforge.application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/")
    public ResponseEntity<?> getUsers(){
        List<User> users =userRepository.findAll();
        if (users.isEmpty()){
            return ResponseEntity.noContent().build();
        } else
            return ResponseEntity.ok(users);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable("id")Long id){
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()){
            return ResponseEntity.ok(user.get());
        } else{
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> createUser(@RequestBody User user){
        User u = userRepository.save(user);
        return ResponseEntity.ok(u);
    }

    @PutMapping("/")
    public ResponseEntity<?> editUser(@RequestBody User user){
        User u = userRepository.saveAndFlush(user);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id")Long id){
        userRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

}
