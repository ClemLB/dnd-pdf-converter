package fr.kuremento.dnd.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constantes {

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class JobContext {
        public static final String CATEGORIES = "categories";
        public static final String NB_LIGNES_LUES = "nbLignesLues";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class JobParameters {
        public static final String INPUT_FILE = "inputFile";
        public static final String ID = "jobID";
    }
}
