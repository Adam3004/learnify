package com.brightpath.learnify.persistance.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {

    @Query("""
           SELECT u
           FROM UserEntity u
           WHERE (:#{#filter.email} IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :#{#filter.email}, '%')))
           AND (:#{#filter.displayName} IS NULL OR LOWER(u.displayName) LIKE LOWER(CONCAT('%', :#{#filter.displayName}, '%')))
           """)
    Page<UserEntity> findByFilter(@Param("filter") UserQueryFilter filter, Pageable pageable);

    @Query("SELECT u FROM UserEntity u WHERE u.id IN :ids")
    List<UserEntity> findAllByIds(@Param("ids") List<String> ids);
}
