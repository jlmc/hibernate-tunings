package io.costa.hibernatetunings.entities;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;


@Entity
@Table(name = "timesheet")
@SequenceGenerator(name = "default_pooled_timesheet_id_generator", sequenceName = "timesheet_id_sequence", allocationSize = 4)
public class Timesheet {

    @Id
    @GenericGenerator(
            name = "A",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {

                    @Parameter(name = "sequence_name", value = "timesheet_id_sequence"),
                    @Parameter(name = "initial_value", value = "1"),
                    @Parameter(name = "increment_size", value = "4"),
                    @Parameter(name = "optimizer", value = "pooled")
                    //@Parameter(name = "optimizer", value = "pooled-lo")
            })
    //@SequenceGenerator(name = "B", sequenceName = "timesheet_id_sequence", allocationSize = 4)
    @GeneratedValue(generator = "A", strategy = GenerationType.SEQUENCE)


    private Long id;

    @ManyToOne
    @JoinColumn(name = "developer_id", nullable = false)
    private Developer developer;

    @Embedded
    private TimePeriod timePeriod;

    @Column(name = "description")
    private String description;

    protected Timesheet() {
    }

    private Timesheet(final Developer developer, final TimePeriod timePeriod, final String description) {
        this.developer = developer;
        this.timePeriod = timePeriod;
        this.description = description;
    }

    private Timesheet(final Developer developer, final TimePeriod timePeriod) {
        this.developer = developer;
        this.timePeriod = timePeriod;
    }

    public static Timesheet of(final Developer developer, final TimePeriod timePeriod, final String description) {
        return new Timesheet(developer, timePeriod, description);
    }

    public static Timesheet of(final Developer developer, final TimePeriod timePeriod) {
        return new Timesheet(developer, timePeriod);
    }

    public Long getId() {
        return id;
    }

    public Developer getDeveloper() {
        return developer;
    }

    public TimePeriod getTimePeriod() {
        return timePeriod;
    }

    public String getDescription() {
        return description;
    }
}
