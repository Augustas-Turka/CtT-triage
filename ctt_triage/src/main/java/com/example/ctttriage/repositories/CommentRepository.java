package com.example.ctttriage.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ctttriage.model.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

//ootb- https://docs.spring.io/spring-data/jpa/docs/current/api/org/springframework/data/jpa/repository/JpaRepository.html

}


