package com.example.microservice.repository;

import com.example.microservice.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    Iterable<Post> findPostByUserId(Integer userId);
}
