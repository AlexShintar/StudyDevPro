package ru.otus.crm.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "client")
public class Client implements Cloneable {

    @Id
    @SequenceGenerator(name = "client_gen", sequenceName = "client_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_gen")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToOne(cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;

    @OneToMany(mappedBy = "client", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Phone> phones = new ArrayList<>();

    public Client(String name) {
        this.id = null;
        this.name = name;
    }

    public Client(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @SuppressWarnings("this-escape")
    public Client(Long id, String name, Address address, List<Phone> phones) {
        this.id = id;
        this.name = name;
        this.address = address != null ? new Address(address.getId(), address.getStreet()) : null;
        this.phones = phones != null
                ? phones.stream()
                        .map(phone -> new Phone(phone.getId(), phone.getNumber()))
                        .collect(Collectors.toList())
                : new ArrayList<>();
        this.phones.forEach(phone -> phone.setClient(this));
    }

    @Override
    @SuppressWarnings({"java:S2975", "java:S1182"})
    public Client clone() {
        Address clonedAddress =
                this.address != null ? new Address(this.address.getId(), this.address.getStreet()) : null;

        List<Phone> clonedPhones = this.phones != null
                ? this.phones.stream()
                        .map(phone -> new Phone(phone.getId(), phone.getNumber()))
                        .collect(Collectors.toList())
                : new ArrayList<>();

        return new Client(this.id, this.name, clonedAddress, clonedPhones);
    }

    @Override
    public String toString() {
        return "Client{" + "id=" + id + ", name='" + name + '\'' + '}';
    }

    @PrePersist
    private void prePersist() {
        syncPhoneReferences();
    }

    private void syncPhoneReferences() {
        if (this.phones != null) {
            this.phones.forEach(phone -> phone.setClient(this));
        }
    }
}
