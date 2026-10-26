package com.jml.reconciliation.entity;

import jakarta.persistence.*;

/**
 * Corporate Directory / LDAP Group Membership Entity.
 * 
 * Maps to database table 'directory_groups'.
 * Tracks group associations (e.g. 'engineering-dev', 'hr-managers', 'finance-team', 'sysadmins')
 * tied to employee usernames.
 */
@Entity
@Table(name = "directory_groups")
public class DirectoryGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String groupName;

    @Column(nullable = false)
    private String username;

    public DirectoryGroup() {}

    public DirectoryGroup(String groupName, String username) {
        this.groupName = groupName;
        this.username = username;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
