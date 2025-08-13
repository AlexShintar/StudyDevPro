package ru.otus.hw.model;

import java.util.Collections;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("client")
@RequiredArgsConstructor
public class Client implements Persistable<Long> {

    @Id
    private Long id;

    private String name;

    @MappedCollection(idColumn = "id")
    private Address address;

    @MappedCollection(idColumn = "client_id")
    private Set<Phone> phones;

    public Client(Long id, String name, Address address) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phones = Collections.emptySet();
    }

    @Override
    public boolean isNew() {
        return this.id == null || this.id < 1;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Client{" + "id= " + id + ", name='" + name + '\'' + ", address=" + address + ", phones=" + phones + '}';
    }
}
