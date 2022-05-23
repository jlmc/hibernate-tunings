package io.costax.hibernatetuning.howtos.ibook;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class IBookKey implements Serializable {

    @Column(name = "registration_number")
    private Long registrationNumber;

    @Column(name = "publisher_id")
    private Integer publisherId;

    public Long getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(Long registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public int getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(int publisherId) {
        this.publisherId = publisherId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IBookKey that = (IBookKey) o;



        return that.registrationNumber != null &&
                Objects.equals(registrationNumber, that.registrationNumber) &&
                Objects.equals(publisherId, that.publisherId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(registrationNumber, publisherId);
        //return 31;
    }

    @Override
    public String toString() {
        return "IBookKey{" +
                "registrationNumber=" + registrationNumber +
                ", publisherId=" + publisherId +
                '}';
    }
}
