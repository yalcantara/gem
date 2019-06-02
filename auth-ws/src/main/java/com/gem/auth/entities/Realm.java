package com.gem.auth.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Cacheable(false)
@Table(name = "realms")
public class Realm implements Serializable {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Basic
    @Column(name = "name", nullable = false, length = 45)
    private String name;

    public Realm(){
        super();
    }

    public Realm(Integer id){
        this.id = (id == null)?null:id.longValue();
    }

    public Realm(Long id){
        this.id = id;
    }

    public Realm(String name){
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Realm realm = (Realm) o;
        return id == realm.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    @Override
    public String toString() {
        return "Realm{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
