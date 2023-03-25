package com.groupeight.citiesandactivities.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.groupeight.citiesandactivities.models.Comment;

@Repository
public interface CommentRepository extends CrudRepository<Comment, Long> {

	List<Comment> findAll();

	List<Comment> findByActivityId(Long id);

	List<Comment> findByUserId(Long id);

	Optional<Comment> findById(Long id);
}
