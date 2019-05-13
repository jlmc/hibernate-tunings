package io.costax.model;

public class SerieDocNum {

    private final Integer serieDocumentoId;
    private final String title;
    private final Integer numDoc;

    public SerieDocNum(final Integer serieDocumentoId, final String title, final Integer numDoc) {
        this.serieDocumentoId = serieDocumentoId;
        this.title = title;
        this.numDoc = numDoc;
    }

    @Override
    public String toString() {
        return "SerieDocNum{" +
                "serieDocumentoId=" + serieDocumentoId +
                ", title='" + title + '\'' +
                ", numDoc=" + numDoc +
                '}';
    }

    public Integer getSerieDocumentoId() {
        return serieDocumentoId;
    }

    public String getTitle() {
        return title;
    }

    public Integer getNumDoc() {
        return numDoc;
    }
}
