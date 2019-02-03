package io.costax.hibernatetuning.howtos.book;

import java.io.Serializable;
import java.util.Objects;

public class PK implements Serializable {

    private Long registrationNumber;

    private Integer publisherId;

    public PK(Long registrationNumber, Integer publisherId) {
        this.registrationNumber = registrationNumber;
        this.publisherId = publisherId;
    }

    private PK() {
    }

    public Long getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(Long registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public Integer getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(Integer publisherId) {
        this.publisherId = publisherId;
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }
        PK pk = (PK) o;
        return Objects.equals( registrationNumber, pk.registrationNumber ) &&
                Objects.equals( publisherId, pk.publisherId );
    }

    @Override
    public int hashCode() {
        return Objects.hash( registrationNumber, publisherId );
    }
}
