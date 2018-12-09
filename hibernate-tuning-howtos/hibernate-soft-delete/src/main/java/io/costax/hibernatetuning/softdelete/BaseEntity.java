package io.costax.hibernatetuning.softdelete;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class BaseEntity {

    protected boolean deleted;

}
