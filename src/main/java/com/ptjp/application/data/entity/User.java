package com.ptjp.application.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ptjp.application.data.Role;

import java.util.Collections;
import java.util.Set;
import javax.persistence.*;


@Entity
@Table(name = "application_user")
public class User extends AbstractEntity {
    @Column(name = "username")
    private String username;
    @Column(name = "name")
    private String name;
    @JsonIgnore
    private String hashedPassword;

    private String passwordSalt;

    @Column(name = "favouriteRoutes")
    private String favouriteRoutes;
    private String home;
    private String work;
    private String profilePictureUrl;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Role> roles;


    public User(String username, String name, String password, Role role) {
        this.username = username;
        this.name = name;
        this.roles = Collections.singleton(role);
        this.hashedPassword = password;
        this.passwordSalt = password;
        this.favouriteRoutes = "";
    }
    public User(String username, String name, String password, Role role, String route) {
        this.username = username;
        this.name = name;
        this.roles = Collections.singleton(role);
        this.hashedPassword = password;
        this.passwordSalt = password;
        this.favouriteRoutes = route;
    }

    public User() {
    }

    // getters and setters
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getHashedPassword() {
        return hashedPassword;
    }
    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public String getFavouriteRoutes(){
        return favouriteRoutes;
    }
    public void setFavouriteRoutes(String route) {
        this.favouriteRoutes = favouriteRoutes + route;
    }
    public String[] getFavouriteRoutes_List() {
        String[] routes = getFavouriteRoutes().split(";");
        return routes;
    }
    public boolean hasRoutes(){
        if (getFavouriteRoutes().equals("")){
            return false;
        }
        return true;
    }

    public String getProfilePictureUrl() {
        return this.profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePicture) {
        this.profilePictureUrl = profilePicture;
    }

    public Set<Role> getRoles() {
        return roles;
    }
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getRole(){return roles.toString();}
    public void setRole(Role role){this.roles = Collections.singleton(role);}
}
