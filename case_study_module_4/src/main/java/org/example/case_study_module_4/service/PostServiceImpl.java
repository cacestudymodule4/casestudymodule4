package org.example.case_study_module_4.service;

import org.example.case_study_module_4.model.*;
import org.example.case_study_module_4.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class PostServiceImpl implements PostService {
    private final FileStorageService fileStorageService;
    private final PostRepository postRepository;
    private final FollowRepository followRepository;
    private final MediaRepository mediaRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final NotificationRepository notificationRepository;

    public PostServiceImpl(FileStorageService fileStorageService,
                           PostRepository postRepository,
                           FollowRepository followRepository,
                           MediaRepository mediaRepository,
                           CommentRepository commentRepository,
                           LikeRepository likeRepository,
                           NotificationRepository notificationRepository) {
        this.fileStorageService = fileStorageService;
        this.postRepository = postRepository;
        this.followRepository = followRepository;
        this.mediaRepository = mediaRepository;
        this.commentRepository = commentRepository;
        this.likeRepository = likeRepository;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public List<Post> findByUserId(User user) {
        List<Post> result = new ArrayList<>();
        List<User> followee = followRepository.findFolloweeByFollower(user.getId());
        for (User u : followee) {
            List<Post> posts = postRepository.findByUserId(u.getId());
            result.addAll(posts);
        }
        List<Post> posts = postRepository.findByUserId(user.getId());
        result.addAll(posts);
        result.sort(Comparator.comparing(Post::getCreatedAt).reversed());
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), result.size());
        Page<Post> pageResult = new PageImpl<>(result.subList(start, end), pageable, result.size());
        return result;
    }

    @Override
    public Map<Post, List<Media>> createPost(Post post, MultipartFile[] mediaFiles) {
        Post newPost = postRepository.save(post);
        List<Media> mediaUrls = new ArrayList<>();
        for (MultipartFile media : mediaFiles) {
            String mediaUrl = fileStorageService.storeFile(media);
            Media newMedia = new Media();
            newMedia.setMediaType("img");
            newMedia.setMediaUrl(mediaUrl);
            newMedia.setPost(newPost);
            mediaUrls.add(newMedia);
        }
        List<Media> mediaList = mediaRepository.saveAll(mediaUrls);
        Map<Post, List<Media>> map = new HashMap<>();
        map.put(newPost, mediaList);
        return map;
    }

    @Override
    public List<Post> findPostsByUser(User user) {
        return postRepository.findPostsByUser(user);
    }

    @Override
    public Post findPostById(Long postId) {
        return postRepository.findById(postId).orElse(null);
    }

    @Override
    public void deletePostById(Long postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);
        commentRepository.deleteAll(comments);
        List<Media> mediaList = mediaRepository.findByPostId(postId);
        mediaRepository.deleteAll(mediaList);
        List<Like> likes = likeRepository.findByPostId(postId);
        likeRepository.deleteAll(likes);
        List<Notification> notifications = notificationRepository.findByPostId(postId);
        notificationRepository.deleteAll(notifications);
        postRepository.deleteById(postId);
    }

    @Override
    public Post updatePost(Post post) {
        return postRepository.save(post);
    }

    @Override
    public List<Post> findAllByDeletedIsFalse() {
        return postRepository.findDeletedPosts();
    }

    @Override
    public void save(Post post) {
        postRepository.save(post);
    }
}
