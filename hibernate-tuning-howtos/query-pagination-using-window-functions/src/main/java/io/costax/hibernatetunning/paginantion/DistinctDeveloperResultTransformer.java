package io.costax.hibernatetunning.paginantion;

import org.hibernate.transform.BasicTransformerAdapter;

import java.io.Serializable;
import java.util.*;

public class DistinctDeveloperResultTransformer extends BasicTransformerAdapter {

    public static final DistinctDeveloperResultTransformer INSTANCE = new DistinctDeveloperResultTransformer();

    @Override
    public List transformList(List list) {
        Map<Serializable, Identifiable> identifiableMap = new LinkedHashMap<>(list.size());

        for (Object entityArray : list) {


            if (Object[].class.isAssignableFrom(entityArray.getClass())) {
                Developer developer = null;
                ProgrammingLanguage programmingLanguage = null;

                Object[] tuples = (Object[]) entityArray;

                for (Object tuple : tuples) {

                    if (tuple instanceof Developer) {
                        developer = (Developer) tuple;
                    } else if (tuple instanceof ProgrammingLanguage) {
                        programmingLanguage = (ProgrammingLanguage) tuple;
                    } else {
                        throw new UnsupportedOperationException(
                                "Tuple " + tuple.getClass() + " is not supported!"
                        );
                    }

                }


                Objects.requireNonNull(developer);
                Objects.requireNonNull(programmingLanguage);

                if (!identifiableMap.containsKey(developer.getId())) {
                    identifiableMap.put(developer.getId(), developer);
                    // developer.setComments( new ArrayList<>() );
                }
                //developer.add(programmingLanguage);
            }
        }
        return new ArrayList<>(identifiableMap.values());
    }
}
