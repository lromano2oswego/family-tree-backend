package com.family_tree.familytree;

import com.family_tree.enums.Role;
import com.family_tree.enums.Status;
import jakarta.persistence.*;

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
    @Column(nullable = false, columnDefinition = "ENUM('Owner', 'Editor', 'Viewer')")
    private Role role = Role.Viewer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('Accepted', 'Declined', 'Pending')")
    private Status status = Status.Pending;

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
}
