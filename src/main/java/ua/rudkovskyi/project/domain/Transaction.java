package ua.rudkovskyi.project.domain;

import javax.persistence.*;

@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BALANCE_SEQUENCE_GENERATOR")
    @SequenceGenerator(name="BALANCE_SEQUENCE_GENERATOR", sequenceName="hibernate_sequence_3", initialValue=1, allocationSize=1)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "source_id")
    private Balance source;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "destination_id")
    private Balance destination;

    private Long amount;
    private Double doubleAmount;
    private boolean isSent;

    public Transaction() {
    }

    public Transaction(Balance source, Balance destination, Long amount) {
        this.source = source;
        this.destination = destination;
        this.amount = amount;
        this.doubleAmount = amount / 100.0;
        this.isSent = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Balance getSource() {
        return source;
    }

    public void setSource(Balance source) {
        this.source = source;
    }

    public Balance getDestination() {
        return destination;
    }

    public void setDestination(Balance destination) {
        this.destination = destination;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
        this.doubleAmount = amount / 100.0;
    }

    public Double getDoubleAmount() {
        return doubleAmount;
    }

    public boolean isSent() {
        return isSent;
    }

    public void setSent(boolean sent) {
        isSent = sent;
    }
}
