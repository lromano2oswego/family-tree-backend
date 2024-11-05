package com.family_tree.familytree;

import jakarta.persistence.*;
import java.util.Date;
import com.family_tree.enums.Gender;

@Entity
@Table(name = "family_members")
public class FamilyMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer memberId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;  // User who owns the family tree

    @ManyToOne
    @JoinColumn(name = "tree_id")
    private FamilyTree familyTree;  // References the family tree

    private String name;

    @Temporal(TemporalType.DATE)
    private Date birthdate;

    @Temporal(TemporalType.DATE)
    private Date deathdate;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('Male', 'Female', 'Other')")
    private Gender gender;

    @ManyToOne
    @JoinColumn(name = "added_by")
    private User addedBy;  // Who added the family member

    @Column(length = 2000)
    private String additionalInfo;

    @Column(nullable = false)
    private boolean isPrivate; // Indicates if the family member's information is private

    // Getters and Setters

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public FamilyTree getFamilyTree() {
        return familyTree;
    }

    public void setFamilyTree(FamilyTree familyTree) {
        this.familyTree = familyTree;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public Date getDeathdate() {
        return deathdate;
    }

    public void setDeathdate(Date deathdate) {
        this.deathdate = deathdate;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public User getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(User addedBy) {
        this.addedBy = addedBy;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public boolean isPrivate() { return isPrivate; }

    public void setPrivate(boolean isPrivate) {this.isPrivate = isPrivate; }
}