package com.family_tree.familytree;

import java.util.List;

//This method will be used to send a response based on if there are orphaned family members
//  after deleting a family member
public class DeleteResponse {
    private String status;
    private String message;
    private List<FamilyMember> orphanedMembers;

    // Constructor for successful deletion with orphaned members
    public DeleteResponse(String status, String message, List<FamilyMember> orphanedMembers) {
        this.status = status;
        this.message = message;
        this.orphanedMembers = orphanedMembers;
    }

    // Constructor for responses without orphaned members
    public DeleteResponse(String status, String message) {
        this.status = status;
        this.message = message;
        this.orphanedMembers = null;
    }

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<FamilyMember> getOrphanedMembers() {
        return orphanedMembers;
    }

    public void setOrphanedMembers(List<FamilyMember> orphanedMembers) {
        this.orphanedMembers = orphanedMembers;
    }

    @Override
    public String toString() {
        return "DeleteResponse{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", orphanedMembers=" + orphanedMembers +
                '}';
    }
}
