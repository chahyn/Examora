CREATE TABLE cours_document (
                                id                      BIGSERIAL PRIMARY KEY,
                                nom_fichier             VARCHAR(255) NOT NULL,
                                chemin_stockage         VARCHAR(500) NOT NULL UNIQUE,
                                taille_fichier_octets   BIGINT,
                                date_upload             TIMESTAMP NOT NULL,
                                visible_etudiants       BOOLEAN NOT NULL DEFAULT FALSE,
                                traite_par_ia           BOOLEAN NOT NULL DEFAULT FALSE,
                                matiere_id              BIGINT NOT NULL REFERENCES matiere(id),
                                professeur_id           BIGINT NOT NULL REFERENCES utilisateur(id)
);

CREATE TABLE examen (
                        id                  BIGSERIAL PRIMARY KEY,
                        matiere_id          BIGINT NOT NULL REFERENCES matiere(id),
                        professeur_id       BIGINT NOT NULL REFERENCES utilisateur(id),
                        cours_document_id   BIGINT REFERENCES cours_document(id),
                        statut              VARCHAR(20) NOT NULL DEFAULT 'EN_ATTENTE',
                        date_creation       TIMESTAMP,
                        date_debut          TIMESTAMP,
                        date_fin            TIMESTAMP,
                        note_totale         DOUBLE PRECISION,
                        duree_minutes       INTEGER,
                        bareme              DOUBLE PRECISION,
                        nb_qcm              INTEGER,
                        nb_ouverte          INTEGER,
                        niveau              VARCHAR(20)
);

CREATE TABLE banque_question (
                                 id                  BIGSERIAL PRIMARY KEY,
                                 matiere_id          BIGINT NOT NULL REFERENCES matiere(id),
                                 session_academique  VARCHAR(20) NOT NULL,
                                 date_creation       TIMESTAMP,
                                 UNIQUE (matiere_id, session_academique)
);

CREATE TABLE question (
                          id                  BIGSERIAL PRIMARY KEY,
                          examen_id           BIGINT NOT NULL REFERENCES examen(id) ON DELETE CASCADE,
                          banque_question_id  BIGINT REFERENCES banque_question(id),
                          type                VARCHAR(20) NOT NULL,
                          enonce              TEXT NOT NULL,
                          option_a            VARCHAR(500),
                          option_b            VARCHAR(500),
                          option_c            VARCHAR(500),
                          option_d            VARCHAR(500),
                          reponse_correcte    VARCHAR(5),
                          points              DOUBLE PRECISION NOT NULL,
                          difficulte          VARCHAR(20),
                          validee_professeur  BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE variante (
                          id              BIGSERIAL PRIMARY KEY,
                          examen_id       BIGINT NOT NULL REFERENCES examen(id) ON DELETE CASCADE,
                          code_variante   VARCHAR(5) NOT NULL
);

CREATE TABLE variante_questions (
                                    variante_id     BIGINT NOT NULL REFERENCES variante(id) ON DELETE CASCADE,
                                    question_id     BIGINT NOT NULL REFERENCES question(id) ON DELETE CASCADE,
                                    position        INTEGER NOT NULL,
                                    PRIMARY KEY (variante_id, position)
);