package com.pokotilov.finaltask.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String email;
    private String password;
    private String phone;
    private String firstName;
    private String lastName;
    private String description;
//    @Formula("(SELECT AVG(r.votes) FROM votes AS r WHERE r.recipient_id = id)")
    private Float rating;
    private Boolean ban;

    @Enumerated(EnumType.STRING)
    private Role role;
    @OneToMany(mappedBy = "user")
    @OrderBy("date ASC")
    private List<Advert> adverts = new ArrayList<>();
    @OneToMany(mappedBy = "author")
    @OrderBy("date ASC")
    private List<Comment> comments = new ArrayList<>();
    @OneToMany(mappedBy = "author")
    private Set<Vote> votes = new HashSet<>();
    @OneToMany(mappedBy = "buyer")
    private Set<Chat> chats = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
//        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getRole())).collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !ban;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !ban;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !ban;
    }

    @Override
    public boolean isEnabled() {
        return !ban;
    }
}
