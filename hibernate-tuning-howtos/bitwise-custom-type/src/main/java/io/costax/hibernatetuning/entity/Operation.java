package io.costax.hibernatetuning.entity;

import io.costax.hibernatetuning.bitwise.Bitwise;
import io.costax.hibernatetuning.entity.options.Options;
import io.costax.hibernatetuning.entity.options.OptionsConverter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "operation")
public class Operation {

    @Id
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "personal_options")
    @Type(type = "bitwise")
    private Bitwise<PersonalOption> personalOptions = Bitwise.noneOf(PersonalOption.class);

    @Convert(converter = OptionsConverter.class)
    @Column(name = "enterprise_options")
    private Options<EnterpriseOption> enterpriseOptions = Options.empty(EnterpriseOption.class);

    protected Operation() {
    }

    private Operation(Long id, final String name, PersonalOption... pos) {
        this.id = id;
        this.name = name;
        this.personalOptions = Bitwise.noneOf(PersonalOption.class);
        if (pos != null) {
            this.personalOptions.add(pos);
        }
    }

    public static Operation of(Long id, final String name, PersonalOption... personalOptions) {
        return new Operation(id, name, personalOptions);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String s) {
        this.name = s;
    }

    public Set<PersonalOption> getPersonalOptions() {
        return this.personalOptions.values(PersonalOption.class);
    }

    public void addOption(PersonalOption o) {
        this.personalOptions = this.personalOptions.add(o);
    }

    public void removeOption(PersonalOption o) {
        this.personalOptions = this.personalOptions.remove(o);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Operation operation = (Operation) o;
        return Objects.equals(id, operation.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void addOptionEE(EnterpriseOption e) {
        this.enterpriseOptions.add(e);
    }

    public void removeEE(EnterpriseOption e) {
        this.enterpriseOptions.remove(e);
    }

    public Options<EnterpriseOption> getEnterpriseOptions() {
        return enterpriseOptions;
    }
}