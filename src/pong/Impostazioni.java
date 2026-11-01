// Pong — Programmazione Orientata agli Oggetti e Laboratori, Udine, a.a. 2026-27
// Consegna 2: le impostazioni. Scadenza: domenica 1 novembre 2026.
package pong;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Le impostazioni di una partita di Pong: quanti punti servono per vincere, con quale scarto
 * minimo, a che velocità va la palla. È un record (lezione 4): una volta create, non cambiano.
 *
 * IL PEZZO MANCANTE: il costruttore che rifiuta i valori sbagliati e il metodo leggi.
 * Ogni metodo ha il suo contratto, nel commento sopra. I test ufficiali (cartella test)
 * controllano i contratti.
 */
public record Impostazioni(int puntiPerVincere, int scartoMinimo, int velocitaPalla) {

    /** Le impostazioni standard: 11 punti per vincere, scarto minimo 2, velocità della palla 5. */
    public static final Impostazioni STANDARD = new Impostazioni(11, 2, 5);


    // Controlla valori 
    private static boolean puntiValidi(int valore) {
        return valore >= 1 && valore <= 99;
    }

    private static boolean scartoValido(int valore) {
        return valore >= 1 && valore <= 10;
    }

    private static boolean velocitaValida(int valore) {
        return valore >= 1 && valore <= 20;
    }

    /**
     * Il costruttore (lezione 7: un record che nasce giusto).
     * Promette: se i tre valori sono nei loro intervalli, le impostazioni con quei valori.
     * Lancia IllegalArgumentException se puntiPerVincere non è fra 1 e 99, se scartoMinimo
     * non è fra 1 e 10, o se velocitaPalla non è fra 1 e 20 (estremi compresi). Il messaggio
     * dice quale valore è sbagliato e perché.
     */
    public Impostazioni {
        if (!puntiValidi(puntiPerVincere)) {
            throw new IllegalArgumentException("puntiPerVincere fuori intervallo");
        }

        if (!scartoValido(scartoMinimo)) {
            throw new IllegalArgumentException("scartoMinimo fuori intervallo");
        }

        if (!velocitaValida(velocitaPalla)) {
            throw new IllegalArgumentException("velocitaPalla fuori intervallo");
        }
    }

    /**
     * Legge le impostazioni dalle righe di un file, una impostazione per riga.
     * Pretende: righe non è null e non contiene null.
     * Promette:
     *  - le righe vuote (o di soli spazi) e quelle che, tolti gli spazi iniziali, cominciano
     *    con # sono ignorate;
     *  - ogni altra riga è nella forma  chiave = valore , con spazi qualsiasi attorno alla chiave,
     *    al segno = e al valore; le chiavi sono puntiPerVincere, scartoMinimo, velocitaPalla;
     *  - un'impostazione che nelle righe non c'è prende il valore standard;
     *  - il risultato sono le impostazioni lette.
     * Lancia IllegalArgumentException, con un messaggio che comincia con «riga N: » (N è il numero
     * della riga, contando da 1 tutte le righe, anche quelle vuote e i commenti), se in una riga:
     *  1. manca il segno = ;
     *  2. la chiave non è una delle tre;
     *  3. il valore non è un numero intero;
     *  4. la stessa chiave c'era già in una riga precedente;
     *  5. il valore è fuori dal suo intervallo (gli intervalli del costruttore).
     * Se ci sono errori in più righe, il messaggio è quello della riga più in alto.
     */
    public static Impostazioni leggi(List<String> righe) {
        int p = STANDARD.puntiPerVincere();
        int s = STANDARD.scartoMinimo();
        int vel = STANDARD.velocitaPalla();

        //variabili controllo
        boolean punti = false;
        boolean scarto = false;
        boolean velocita = false;

        for (int i = 0; i < righe.size(); i++){
            int nRighe = i + 1;

            //rimuovo evenutali spazi dalla riga
            String riga = righe.get(i).trim();

            if(riga.isEmpty() || riga.startsWith("#")){
                continue;
            }

            int pos = riga.indexOf("=");
            //non trovato
            if (pos == -1){
                throw new IllegalArgumentException("riga " + nRighe + ": manca =");
            }

            //parte prima =, nome impostazione
            String key = riga.substring(0,pos).trim();

            //parte dopo =, valore impostazione
            String val = riga.substring(pos + 1).trim();

            //controllo chiave
            if (!key.equals("puntiPerVincere") && !key.equals("scartoMinimo") && !key.equals("velocitaPalla")){
                throw new IllegalArgumentException("riga " + nRighe + ": chiave non trovata");
            }

            int valore;

            //val non intero
            try {
                valore = Integer.parseInt(val);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("riga " + nRighe + ": valore non intero", e);
            }

            //ripetizione chiave
            if(key.equals("puntiPerVincere") && punti || key.equals("scartoMinimo") && scarto || key.equals("velocitaPalla") && velocita){
                throw new IllegalArgumentException("riga " + nRighe + ": chiave ripetuta");
            }

            //valore fuori intervallo
            if(key.equals("puntiPerVincere")){
                if(!puntiValidi(valore)){
                    throw new IllegalArgumentException("riga " + nRighe + ": puntiPerVincere fuori intervallo");
                }

                p = valore;
                punti = true;

            }else if (key.equals("scartoMinimo")){
                if(!scartoValido(valore)){
                    throw new IllegalArgumentException("riga " + nRighe + ": scartoMinimo fuori intervallo");
                }

                s = valore;
                scarto = true;

            }else if (key.equals("velocitaPalla")){
                if(!velocitaValida(valore)){
                    throw new IllegalArgumentException("riga " + nRighe + ": velocitaPalla fuori intervallo");
                }

                vel = valore;
                velocita = true;
            }
        }

        return new Impostazioni(p, s, vel);
    }

    /**
     * Legge le impostazioni da un file di testo.
     * Promette: come leggi, con le righe del file.
     * Lancia IOException se il file non si può leggere (per esempio non esiste): non la cattura,
     * la lascia a chi chiama. Lancia IllegalArgumentException come leggi.
     */
    public static Impostazioni daFile(Path file) throws IOException {
        return leggi(Files.readAllLines(file));
    }
}
