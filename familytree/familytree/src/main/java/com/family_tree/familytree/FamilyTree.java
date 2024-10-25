package com.family_tree.familytree;

import com.family_tree.enums.PrivacySetting;
import jakarta.persistence.*;

@Entity
@Table(name = "family_trees")
public class FamilyTree {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "family_tree_id")
    private Integer id;

    @Column(name = "tree_name", length = 200, nullable = false)
    private String treeName;

    @Column(name = "privacy_setting", columnDefinition = "ENUM('Public', 'Private') DEFAULT 'Private'")
    @Enumerated(EnumType.STRING)
    private PrivacySetting privacySetting = PrivacySetting.Private;

    // Establish a foreign key relationship with User (the owner)
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    // Getters and setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTreeName() {
        return treeName;
    }

    public void setTreeName(String treeName) {
        this.treeName = treeName;
    }

    public PrivacySetting getPrivacySetting() {
        return privacySetting;
    }

    public void setPrivacySetting(PrivacySetting privacySetting) {
        this.privacySetting = privacySetting;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

}
