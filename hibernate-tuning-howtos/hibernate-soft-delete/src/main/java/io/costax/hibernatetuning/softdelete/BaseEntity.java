package io.costax.hibernatetuning.softdelete;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public class BaseEntity {

    protected boolean deleted;

}
