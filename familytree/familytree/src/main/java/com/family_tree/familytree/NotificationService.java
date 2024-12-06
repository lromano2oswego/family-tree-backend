package com.family_tree.familytree;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDateTime;

//handle the logic of creating,retrieving and deleting notifications
@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private FamilyTreeRepository familyTreeRepository;

    public void createNotification(User user, String message, String url, Integer treeId) {
        FamilyTree familyTree = familyTreeRepository.getFamilyTreeById(treeId);
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setUrl(url);
        notification.setTimestamp(LocalDateTime.now());
        notification.setTreeId(familyTree);
        notificationRepository.save(notification);
    }

    public List<Notification> getUserNotifications(Integer userId) {
        return notificationRepository.findByUserIdAndIsReadFalse(userId);
    }

    public void deleteNotification(Integer userId, Integer notificationId) {
        notificationRepository.deleteByUserIdAndId(userId, notificationId);
    }

}
