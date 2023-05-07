package com.example.microservice.service;

import com.example.microservice.entity.Post;
import com.example.microservice.exception.BadRequestException;
import com.example.microservice.exception.NoContentException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public interface PostService {
    Iterable<Post> getAllPost();

    Post getPostById(Integer postId) throws NoContentException;

    Iterable<Post> getPostByUserId(Integer userId);

    Post createPost(Post post) throws NoContentException, ResourceNotFoundException, BadRequestException;

    Post updatePost(Post post, Integer postId) throws ResourceNotFoundException, BadRequestException;

    void deletePost(Integer postId) throws ResourceNotFoundException;
}
