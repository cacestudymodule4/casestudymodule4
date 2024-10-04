package org.example.case_study_module_4.repository;

import org.example.case_study_module_4.model.Follow;
import org.example.case_study_module_4.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Integer> {
    @Query("SELECT f.followee FROM Follow f WHERE f.follower.id = :followerId")
    List<User> findFolloweeByFollower(Long followerId);
}
