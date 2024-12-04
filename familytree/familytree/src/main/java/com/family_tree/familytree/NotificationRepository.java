package com.family_tree.familytree;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByUserIdAndIsReadFalse(Integer userId);
    void deleteByUserIdAndId(Integer userId, Integer notificationId);
    Notification findByUser_IdAndTreeId_Id(Integer userId, Integer treeId);
    void deleteById(Integer notificationId);
}
