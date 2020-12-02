package ua.rudkovskyi.project.domain;

import javax.persistence.*;

@Entity
public class Balance {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BALANCE_SEQUENCE_GENERATOR")
    @SequenceGenerator(name="BALANCE_SEQUENCE_GENERATOR", sequenceName="hibernate_sequence_2", initialValue=1, allocationSize=1)
    private Long id;
    private String name;
    private Long amount;
    private Double doubleAmount;
    private boolean isLocked;
    private boolean isRequested;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User owner;

    public Balance() {
    }

    public Balance(String name, Long balance, User owner) {
        this.name = name;
        this.amount = balance;
        this.doubleAmount = balance / 100.0;
        this.isLocked = false;
        this.isRequested = false;
        this.owner = owner;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getAmount() {
        return amount;
    }

    public Double getDoubleAmount() {
        return doubleAmount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
        this.doubleAmount = amount / 100.0;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getOwnerName(){
        return owner != null ? owner.getUsername() : "<none>";
    }

    public boolean isRequested() {
        return isRequested;
    }

    public void setRequested(boolean requested) {
        isRequested = requested;
    }
}
