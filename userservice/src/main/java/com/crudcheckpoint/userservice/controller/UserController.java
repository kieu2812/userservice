package com.crudcheckpoint.userservice.controller;

import com.crudcheckpoint.userservice.bean.DeleteUserResponse;
import com.crudcheckpoint.userservice.bean.User;
import com.crudcheckpoint.userservice.bean.UserAuthenticateResponse;
import com.crudcheckpoint.userservice.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    private UserRepository userRepo;


    public UserController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @GetMapping("/users")
    public Iterable<User> getUsers(){
        return this.userRepo.findAll();
    }

    @PostMapping("/users")
    public User createUsers(@RequestBody User user){
        return this.userRepo.save(user);
    }
    @GetMapping("/users/{id}")
    public User getUser(@PathVariable Integer id){
        return this.userRepo.findById(id).orElse(new User());
    }

    @PatchMapping("/users/{id}")
    public User updateUser(@PathVariable Integer id, @RequestBody User user){
        User temp =  this.userRepo.findById(id).orElse(new User());
        if(temp.getEmail()==null) return temp;
        if(user.getPassword()!=null){
            temp.setPassword(user.getPassword());
        }
        if(user.getEmail()!=null){
            temp.setEmail(user.getEmail());
        }

        return this.userRepo.save(temp);
    }
    @DeleteMapping("/users/{id}")
    public DeleteUserResponse createUsers(@PathVariable Integer id){
        if(this.userRepo.findById(id).isPresent())  this.userRepo.deleteById(id);
        DeleteUserResponse response = new DeleteUserResponse();
        response.setCount(this.userRepo.count());
        return response;
    }

    @PostMapping("/users/authenticate")
    public UserAuthenticateResponse authenticateUser(@RequestBody User user){
        UserAuthenticateResponse userAuthenticateResponse = new UserAuthenticateResponse();
        List<User> users =  this.userRepo.findUsersByEmailAndPassword(user.getEmail(), user.getPassword());
        if(!users.isEmpty()){
           userAuthenticateResponse.setAuthenticated(true);
           userAuthenticateResponse.setUser(users.get(0));
        }
        else userAuthenticateResponse.setAuthenticated(false);

        return userAuthenticateResponse;
    }
}
