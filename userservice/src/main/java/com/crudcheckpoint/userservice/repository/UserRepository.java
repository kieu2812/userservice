package com.crudcheckpoint.userservice.repository;

import com.crudcheckpoint.userservice.bean.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("select u from User u where u.email=?1 and u.password=?2")
    List<User> findUsersByEmailAndPassword(String email, String password);

}
