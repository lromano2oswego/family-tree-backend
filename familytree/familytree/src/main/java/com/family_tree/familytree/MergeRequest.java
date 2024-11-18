package com.family_tree.familytree;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "merge_requests")
public class MergeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "requester_tree_id", nullable = false, foreignKey = @ForeignKey(name = "fk_requester_tree"))
    private FamilyTree requesterTree; // The tree initiating the merge

    @ManyToOne
    @JoinColumn(name = "target_tree_id", nullable = false, foreignKey = @ForeignKey(name = "fk_target_tree"))
    private FamilyTree targetTree; // The tree to be merged into

    @ManyToOne
    @JoinColumn(name = "initiator_id", nullable = false, foreignKey = @ForeignKey(name = "fk_initiator_user"))
    private User initiator; // The user who initiated the merge request

    @Column(name = "status", nullable = false)
    private String status = "Pending"; // Default status for a new merge request

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "requested_at", nullable = false, updatable = false)
    private Date requestedAt; // Timestamp for when the merge request was created

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "resolved_at")
    private Date resolvedAt; // Timestamp for when the request was accepted/denied

    // Constructors, Getters, and Setters
    public MergeRequest() {}

    @PrePersist
    protected void onCreate() {
        this.requestedAt = new Date();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public FamilyTree getRequesterTree() {
        return requesterTree;
    }

    public void setRequesterTree(FamilyTree requesterTree) {
        this.requesterTree = requesterTree;
    }

    public FamilyTree getTargetTree() {
        return targetTree;
    }

    public void setTargetTree(FamilyTree targetTree) {
        this.targetTree = targetTree;
    }

    public User getInitiator() {
        return initiator;
    }

    public void setInitiator(User initiator) {
        this.initiator = initiator;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getRequestedAt() {
        return requestedAt;
    }

    public Date getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(Date resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
}
