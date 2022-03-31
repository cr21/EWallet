package com.example.ewallet.users;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Builder
public class User implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int  id;


    @NotNull
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String mobile;

    @Column(nullable = false)
    private String country;

    @Column(unique = true,nullable = false)
    private String kycId;


    @Column(nullable = false)
    private String password;

    @CreationTimestamp
    private Date createdOn;

    @UpdateTimestamp
    private Date updatedOn;

}
