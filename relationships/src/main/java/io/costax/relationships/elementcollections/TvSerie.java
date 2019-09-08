package io.costax.relationships.elementcollections;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
public class TvSerie {

    @Id
    private Integer id;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "tv_serie_episode", joinColumns = @JoinColumn(name = "tv_serie_id"))
    @MapKeyColumn(name = "field_key", length = 50)
    @Column(name = "field_value", length = 100)
    // @BatchSize(size = 20)
    private Map<String, String> episodes = new HashMap<>();


    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "tv_serie_prize", joinColumns = @JoinColumn(name = "tv_serie_id"))
    //@MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "field_key", length = 50)
    @AttributeOverrides({
            @AttributeOverride(name = "at", column = @Column(name = "recived_at", nullable = false, updatable = false)),
            @AttributeOverride(name = "value", column = @Column(name = "prize_value", nullable = false, updatable = false))
    })

    //@Column(name = "field_value", length = 100)
    // @BatchSize(size = 20)
    private Map<String, Prize> prizes = new HashMap<>();


    public TvSerie() {
    }

    public TvSerie(final Integer id) {
        this.id = id;
    }

    public void putEpisode(String key, String value) {
        this.episodes.put(key, value);
    }

    public void putPrize(String key, Prize value) {
        this.prizes.put(key, value);
    }


}
