package com.example.microservice.service;

import com.example.microservice.entity.Post;
import com.example.microservice.exception.BadRequestException;
import com.example.microservice.exception.NoContentException;
import com.example.microservice.repository.PostRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@Service
public class PostServiceImpl implements PostService {

    private static final String URL = "https://jsonplaceholder.typicode.com/";
    private static final String PATH_POSTS = "posts/";
    private static final String PATH_USERS = "users/";
    private static final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    PostRepository postRepository;

    @Autowired
    Validator validator;

    @Override
    public Iterable<Post> getAllPost(){
        return postRepository.findAll();
    }

    @Override
    public Post getPostById(Integer postId) {
        Optional<Post> id = postRepository.findById(postId);

        if (id.isPresent()){
            return id.get();
        }
            try {
                ResponseEntity<Post> responseEntity = restTemplate.getForEntity(String.format("%s%s%s", URL, PATH_POSTS, id), Post.class);
                Post postBody = responseEntity.getBody();
                if (postBody == null){
                    throw new NoContentException(String.format("No response for body with this id = " + id));
                }
                    postRepository.save(postBody);
                    return postBody;

            } catch (HttpClientErrorException | NoContentException e) {
                throw new ResourceNotFoundException(String.format("Post does not exist with id = " + id));
            }
    }

    @Override
    public Iterable<Post> getPostByUserId(Integer userId) throws ResourceNotFoundException{
        Iterable<Post> byUserId = postRepository.findPostByUserId(userId);
        if (CollectionUtils.isEmpty(Collections.singleton(byUserId))){
            throw new ResourceNotFoundException(String.format("No post with user id = " + userId));
        }
        return byUserId;
    }

    @Override
    public Post createPost(Post post) throws NoContentException, ResourceNotFoundException, BadRequestException{
        Set<ConstraintViolation<Post>> violations = validator.validate(post);

        if(!CollectionUtils.isEmpty(violations)){
            throw new BadRequestException(String.format("%s must be set!", violations.iterator().next().getPropertyPath().toString()));
        }

        Integer userId = post.getUserId();
        try {
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(String.format("%s%s%s", URL, PATH_USERS, userId), String.class);
            String body = responseEntity.getBody();

            if (body == null){
                throw new NoContentException(String.format("No response for userId '%s'", userId));
            }
        } catch (HttpClientErrorException e){
            throw new ResourceNotFoundException(String.format("User with id '%s' not found!", userId));
        }
        return postRepository.save(post);
    }

    @Override
    public Post updatePost(Post post, Integer postId) throws ResourceNotFoundException, BadRequestException{
        Post postUpdate = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Post with this id '%s' now found", postId)));

        if (post.getUserId() != null && !post.getUserId().equals(postUpdate.getUserId())){
            throw new BadRequestException(String.format("userId can not be changed"));
        }
        if (post.getTitle() != null && !post.getTitle().isBlank()){
            postUpdate.setTitle(post.getTitle());
        }
        if (post.getBody() != null && !post.getBody().isBlank()){
            postUpdate.setBody(post.getBody());
        }
        return postRepository.save(postUpdate);
    }

    @Override
    public void deletePost(Integer postId) throws ResourceNotFoundException {
        Post postDelete = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Post with this id '%s' now found", postId)));
        postRepository.delete(postDelete);
    }

}
