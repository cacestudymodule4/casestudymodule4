package org.example.case_study_module_4.restful;

import org.example.case_study_module_4.model.Media;
import org.example.case_study_module_4.model.Post;
import org.example.case_study_module_4.model.User;
import org.example.case_study_module_4.service.MediaService;
import org.example.case_study_module_4.service.PostService;
import org.example.case_study_module_4.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/post")
public class PostRestfulController {
    private final PostService postService;
    private final UserService userService;

    public PostRestfulController(PostService postService,
                                 UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<Map<Post, List<Media>>> createPost(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("media") MultipartFile[] media,
            Principal principal) {
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        User user = userService.findUserByEmail(principal.getName());
        post.setUser(user);
        return ResponseEntity.ok(postService.createPost(post, media));
    }
}
