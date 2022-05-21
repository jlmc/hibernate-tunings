package io.costax.hibernatetunnig.overrideIdstrategy.generators;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentityGenerator;

import java.io.Serializable;

/**
 * This is class is use to combine an identity column with the assigned identifier strategy.
 * We need to this Strategy when for example we need to define the Id in the business implementation.
 * <p/>
 * <b>WE MUST KEEP IN MIND THE FOLLOWING REQUIREMENTS</b>
 * <ul>
 * <li>Our entities must implement the {@code Identifiable} interface</li>
 * <li>The constant {@code AssignedIdentityGenerator.STRATEGY} is the complete class name of this <code>IdentityGenerator</code> implementation,
 * if we for some reason decide to refactoring this class and move it for a different package then <code>AssignedIdentityGenerator#STRATEGY</code>
 * should also be refactoring.</li>
 * </ul>
 * <p>
 * To use this identifier generator, the entity mapping looks as follows:
 * <pre>
 *
 * {@code
 *
 *   @Entity
 *   @Table(name = "comissao_documento")
 *   public class ComissaoDocumento implements Identifiable<Integer> {
 *      @Id
 *      @GenericGenerator(name = "comissao_documento_identity", strategy = AssignedIdentityGenerator.STRATEGY)
 *      @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "comissao_documento_identity")
 *      private Integer id;
 *
 *      // all other properties
 *
 *      public Integer getId() { return this.id; }
 *   }
 * }
 * </pre>
 * <p>
 * For mode details the full documentation of {@code IdentityGenerator} should be consulted.
 *
 * @see Identifiable
 */
public class AssignedIdentityGenerator extends IdentityGenerator {

    /**
     * this is the complete class name of this <code>IdentityGenerator</code> implementation,
     * if we for some reason decide to refactoring this class and move it for a different package then <code>AssignedIdentityGenerator#STRATEGY</code>
     * should also be refactoring.
     */
    public static final String STRATEGY = "io.costax.hibernatetunnig.overrideIdstrategy.generators.AssignedIdentityGenerator";

    @Override
    public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object object) {
        if (object instanceof Identifiable identifiable) {
            Serializable id = identifiable.getId();
            if (id != null) {
                return id;
            }
        }

        Object generate = super.generate(sharedSessionContractImplementor, object);

        return (Serializable) generate;
    }
}
