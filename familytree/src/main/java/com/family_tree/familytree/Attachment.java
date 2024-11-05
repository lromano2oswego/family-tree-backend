package com.family_tree.familytree;

import jakarta.persistence.*;
import com.family_tree.familytree.User;
import com.family_tree.familytree.FamilyMember;

@Entity
@Table(name = "attachments")
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer mediaId;

    @ManyToOne
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(name = "fk_member_attachment"))
    private FamilyMember member; // The family member to whom this attachment belongs

    private String typeOfFile;

    @Lob
    private byte[] fileData;

    @ManyToOne
    @JoinColumn(name = "uploaded_by", foreignKey = @ForeignKey(name = "fk_user_attachment"))
    private User uploadedBy; // The user who uploaded the file

    // Getters and Setters
    public Integer getMediaId() {
        return mediaId;
    }

    public void setMediaId(Integer mediaId) {
        this.mediaId = mediaId;
    }

    public FamilyMember getMember() {
        return member;
    }

    public void setMember(FamilyMember member) {
        this.member = member;
    }

    public String getTypeOfFile() {
        return typeOfFile;
    }

    public void setTypeOfFile(String typeOfFile) {
        this.typeOfFile = typeOfFile;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    public User getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(User uploadedBy) {
        this.uploadedBy = uploadedBy;
    }
}