package com.family_tree.familytree;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByUserIdAndIsReadFalse(Integer userId);
    void deleteByUserIdAndId(Integer userId, Integer notificationId);
    Notification findByUser_IdAndTreeId_Id(Integer userId, Integer treeId);
    void deleteById(Integer notificationId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Notification n WHERE n.treeId.id = :treeId")
    void deleteByTreeId(@Param("treeId") Integer treeId);
}
