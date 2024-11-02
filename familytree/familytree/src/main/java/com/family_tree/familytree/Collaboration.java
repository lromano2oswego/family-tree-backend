
package com.family_tree.familytree;

import com.family_tree.enums.Role;
import com.family_tree.enums.Status;
import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "collaborations")
public class Collaboration {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer collaborationId;

    @ManyToOne
    @JoinColumn(name = "tree_id", foreignKey = @ForeignKey(name = "fk_tree_collaboration"))
    private FamilyTree familyTree;

    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_user_collaboration"))
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.Viewer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.Pending;

    // when the collaboration was created
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // when the collaboration was last updated, changes on each update
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Timestamps
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters


    public Integer getCollaborationId() {
        return collaborationId;
    }

    public void setCollaborationId(Integer collaborationId) {
        this.collaborationId = collaborationId;
    }


    public FamilyTree getFamilyTree() {
        return familyTree;
    }

    public void setFamilyTree(FamilyTree familyTree) {
        this.familyTree = familyTree;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }


    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }


    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }


    public boolean isPending() {
        return this.status == Status.Pending;
    }

    public boolean isOwner() {
        return this.role == Role.Owner;
    }
}
