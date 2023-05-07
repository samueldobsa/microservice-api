package com.example.microservice.controller;

import com.example.microservice.entity.Post;
import com.example.microservice.exception.BadRequestException;
import com.example.microservice.exception.NoContentException;
import com.example.microservice.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    PostService postService;

    @GetMapping
    public Iterable<Post> getAllPost(){
        return postService.getAllPost();
    }

    @GetMapping("/{id}")
    public Post getPostById(@PathVariable("id") Integer id) throws NoContentException {
        return postService.getPostById(id);
    }

    @GetMapping("/user/{id}")
    public Iterable<Post> getPostsByUserId(@PathVariable("id") Integer userId){
        return postService.getPostByUserId(userId);
    }

    @PostMapping
    public Post addPost(@RequestBody Post post) throws BadRequestException, NoContentException {
        return postService.createPost(post);
    }

    @PutMapping("/{id}")
    public Post updatePost(@PathVariable("id") Integer id, @RequestBody Post post) throws BadRequestException {
        return postService.updatePost(post, id);
    }

    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable("id") Integer id){
        postService.deletePost(id);
    }
}
