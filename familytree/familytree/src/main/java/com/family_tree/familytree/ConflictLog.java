package com.family_tree.familytree;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "conflict_logs")
public class ConflictLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tree_id", nullable = false)
    private Integer treeId;

    @Column(name = "member1_id", nullable = false)
    private Integer member1Id;

    @Column(name = "member1_name", nullable = false)
    private String member1Name;

    @Temporal(TemporalType.DATE)
    @Column(name = "member1_birthdate", nullable = false)
    private Date member1Birthdate;

    @Temporal(TemporalType.DATE)
    @Column(name = "member1_deathdate")
    private Date member1Deathdate; // deathdate for member1

    @Column(name = "member1_additional_info", length = 2000)
    private String member1AdditionalInfo; // additional info for member1

    @Column(name = "member2_id", nullable = false)
    private Integer member2Id;

    @Column(name = "member2_name", nullable = false)
    private String member2Name;

    @Temporal(TemporalType.DATE)
    @Column(name = "member2_birthdate", nullable = false)
    private Date member2Birthdate;

    @Temporal(TemporalType.DATE)
    @Column(name = "member2_deathdate")
    private Date member2Deathdate; // deathdate for member2

    @Column(name = "member2_additional_info", length = 2000)
    private String member2AdditionalInfo; // additional info for member2

    @Column(name = "status", nullable = false)
    private String status = "Pending"; // Default status: Pending

    @Column(name = "notes")
    private String notes; // Notes for conflict resolution

    // Constructors
    public ConflictLog() {}

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTreeId() {
        return treeId;
    }

    public void setTreeId(Integer treeId) {
        this.treeId = treeId;
    }

    public Integer getMember1Id() {
        return member1Id;
    }

    public void setMember1Id(Integer member1Id) {
        this.member1Id = member1Id;
    }

    public String getMember1Name() {
        return member1Name;
    }

    public void setMember1Name(String member1Name) {
        this.member1Name = member1Name;
    }

    public Date getMember1Birthdate() {
        return member1Birthdate;
    }

    public void setMember1Birthdate(Date member1Birthdate) {
        this.member1Birthdate = member1Birthdate;
    }

    public Date getMember1Deathdate() {
        return member1Deathdate;
    }

    public void setMember1Deathdate(Date member1Deathdate) {
        this.member1Deathdate = member1Deathdate;
    }

    public String getMember1AdditionalInfo() {
        return member1AdditionalInfo;
    }

    public void setMember1AdditionalInfo(String member1AdditionalInfo) {
        this.member1AdditionalInfo = member1AdditionalInfo;
    }

    public Integer getMember2Id() {
        return member2Id;
    }

    public void setMember2Id(Integer member2Id) {
        this.member2Id = member2Id;
    }

    public String getMember2Name() {
        return member2Name;
    }

    public void setMember2Name(String member2Name) {
        this.member2Name = member2Name;
    }

    public Date getMember2Birthdate() {
        return member2Birthdate;
    }

    public void setMember2Birthdate(Date member2Birthdate) {
        this.member2Birthdate = member2Birthdate;
    }

    public Date getMember2Deathdate() {
        return member2Deathdate;
    }

    public void setMember2Deathdate(Date member2Deathdate) {
        this.member2Deathdate = member2Deathdate;
    }

    public String getMember2AdditionalInfo() {
        return member2AdditionalInfo;
    }

    public void setMember2AdditionalInfo(String member2AdditionalInfo) {
        this.member2AdditionalInfo = member2AdditionalInfo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
