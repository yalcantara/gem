package com.gem.auth.entities;

import javax.persistence.*;
import java.time.Instant;
import java.util.Objects;


//This class should not be serializable
@Entity
@Cacheable(false)
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "realm_id")
    private Realm realm;

    @Column(name = "name", nullable = false, length = 45)
    private String name;

    @Column(name = "pass", nullable = false, length = 64)
    private String pass;

    @Column(name = "prev_pass", nullable = false, length = 64)
    private String prevPass;

    @Column(name = "req_pass_change", nullable = false)
    private boolean reqPassChange;

    @Column(name = "activated", nullable = false)
    private boolean activated;

    @Column(name = "created_by", nullable = false, length = 60)
    private String createdBy;

    @Column(name = "created_date", nullable = false)
    private Instant createdDate;

    @Column(name = "last_modified_by", length = 60)
    private String lastModifiedBy;

    @Column(name = "last_modified_date")
    private Instant lastModifiedDate;


    public User(){
        super();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Realm getRealm() {
        return realm;
    }

    public void setRealm(Realm realm) {
        this.realm = realm;
    }

    public Long getRealmId(){
        return (realm == null)?null:realm.getId();
    }

    public String getRealmName(){
        return (realm == null)?null:realm.getName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }


    public String getPrevPass() {
        return prevPass;
    }

    public void setPrevPass(String prevPass) {
        this.prevPass = prevPass;
    }


    public boolean isReqPassChange() {
        return reqPassChange;
    }

    public void setReqPassChange(boolean reqPassChange) {
        this.reqPassChange = reqPassChange;
    }


    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }


    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }


    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }


    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }


    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", realm=" + realm +
                ", name='" + name + '\'' +
                ", activated=" + activated +
                ", createdBy='" + createdBy + '\'' +
                ", createdDate=" + createdDate +
                ", lastModifiedBy='" + lastModifiedBy + '\'' +
                ", lastModifiedDate=" + lastModifiedDate +
                '}';
    }
}
