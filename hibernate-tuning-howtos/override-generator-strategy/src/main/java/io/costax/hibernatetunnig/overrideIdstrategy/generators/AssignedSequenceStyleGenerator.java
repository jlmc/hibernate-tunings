package io.costax.hibernatetunnig.overrideIdstrategy.generators;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

import java.io.Serializable;

/**
 * This is class is use to combine an identity column with the assigned identifier strategy.
 * We need to this Strategy when for example we need to define the Id in the business implementation.
 * <p/>
 * <b>WE MUST KEEP IN MIND THE FOLLOWING REQUIREMENTS</b>
 * <ul>
 * <li>Our entities must implement the {@code Identifiable} interface</li>
 * <li>The constant {@code AssignedIdentityGenerator.STRATEGY} is the complete class name of this <code>SequenceStyleGenerator</code> implementation,
 * if we for some reason decide to refactoring this class and move it for a different package then <code>AssignedSequenceStyleGenerator#STRATEGY</code>
 * should also be refactoring.</li>
 * </ul>
 * <p>
 * To use this identifier generator, the entity mapping looks as follows:
 * <pre>
 *
 * {@code
 *
 *   @Entity
 *   @Table(name = "produto")
 *   public class Produto implements Identifiable<Integer> {
 *
 *      @Id
 *      @GenericGenerator(
 *         name = "produto_seq_generator",
 *         strategy = AssignedSequenceStyleGenerator.STRATEGY,
 *         parameters = {
 *             @Parameter(name = "sequence_name", value = "produto_id_seq"),
 *             @Parameter(name = "allocation_size", value = "1")
 *      })
 *      @GeneratedValue(generator = "produto_seq_generator", strategy = GenerationType.SEQUENCE)
 *      private Integer id;
 *
 *      // all the other fields
 *
 *      @Override
 *      public Integer getId() { return this.id; }
 * }
 * </pre>
 * <p>
 * For mode details the full documentation of {@code SequenceStyleGenerator} should be consulted.
 *
 * @see Identifiable
 * @see SequenceStyleGenerator
 */
public class AssignedSequenceStyleGenerator extends SequenceStyleGenerator {

    /**
     * this is the complete class name of this <code>IdentityGenerator</code> implementation,
     * if we for some reason decide to refactoring this class and move it for a different package then <code>AssignedSequenceStyleGenerator#STRATEGY</code>
     * should also be refactoring.
     */
    public static final String STRATEGY = "io.costax.hibernatetunnig.overrideIdstrategy.generators.AssignedSequenceStyleGenerator";

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object obj) {
        if (obj instanceof Identifiable) {
            Identifiable identifiable = (Identifiable) obj;
            Serializable id = identifiable.getId();
            if (id != null) {
                return id;
            }
        }
        return super.generate(session, obj);
    }
}
