
import java.awt.*;
import java.awt.Checkbox;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Choice;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.List;
import java.awt.Panel;
import java.awt.Scrollbar;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;


/**
 * I N F  2 1 2 0
 *
 * Programme permettant de faire jouer une ou deux frequences. L'utilisateur doit cocher l'option "Deuxieme frequence" afin de pourvoir entendre la 
 * deuxieme frequence. De plus, on peut modifier les frequences des deux ondes, ainsi que quatre filtres qui donnent des effets un peu differents.
 * Lorsque les deux frequences jouent en meme temps, on a l'option de choisir a quel volume elles joueront.
 * Finalement, l'utilisateur choisit combien de temps (de 1 a 3 secondes) durera le son, apres avoir appuye sur "Play".
 * 
 * Samuel Ovalle Ortega 
 * 24 juillet 2013
 *
 * OVAS08129006
 * ovalle_ortega.samuel@courrier.uqam.ca
 */

public class Principal extends Frame implements ActionListener, WindowListener, AdjustmentListener,ItemListener {
    private static final long serialVersionUID = 1L;
    public static final int FREQUENCE_ECHANTILLONAGE = 44100;
    public static final int NB_CANALS                = 1;
    public static final int OCTETS_PAR_ECHANTILLON   = 2;
    public static final int TAILLE_CADRE             = NB_CANALS * OCTETS_PAR_ECHANTILLON;
    public static final int NB_OCTETS                = FREQUENCE_ECHANTILLONAGE * TAILLE_CADRE;
    public static final int AMPLITUDE_MAXIMUM        = (int)( Math.pow( 2, OCTETS_PAR_ECHANTILLON * 8 - 1 ) - 1 );

    public static final int BORDURE_X = 14;
    public static final int BORDURE_Y = 40;

    protected static Graphic dessin;

    @Override
    public void windowActivated(WindowEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void windowClosed(WindowEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void windowClosing(WindowEvent arg0) {
        System.exit(0);
    }

    @Override
    public void windowDeactivated(WindowEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void windowDeiconified(WindowEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void windowIconified(WindowEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void windowOpened(WindowEvent arg0) {
        // TODO Auto-generated method stub

    }

    public static Onde nouvelleOnde( TypeOnde typeOnde, double frequence ) {
        Onde onde = null;

        switch( typeOnde ) {
            case SIN :
            onde = new Sinusoidale( FREQUENCE_ECHANTILLONAGE, frequence );
            break;
            case CARRE :
            onde = new Carre( FREQUENCE_ECHANTILLONAGE, frequence );
            break;
            case SCIE_D :
            onde = new ScieD( FREQUENCE_ECHANTILLONAGE, frequence );
            break;
            case SCIE_M : 
            onde = new ScieM( FREQUENCE_ECHANTILLONAGE, frequence );
            break;
            case TRI : 
            onde = new Triangle( FREQUENCE_ECHANTILLONAGE, frequence );
            break;
            case BRUIT :
            onde = new Bruit();
            break;
        }

        return onde;
    }

    public static Onde construireOnde() {
        Onde ondeO;

        Onde onde1 = nouvelleOnde( typeOnde1, frequence1 );

        if( utiliseOnde2 ) {
            Onde onde2 = nouvelleOnde( typeOnde2, frequence2 );
            ondeO = new Mixe( onde1, ratioVolume, onde2, 1.0 - ratioVolume );
        } else {
            ondeO = onde1;
        }

        Onde filtre = new ADSR( FREQUENCE_ECHANTILLONAGE, 
                attaque_filtre, declain_filtre, maintien_filtre, relache_filtre );
        Onde onde = new Filtre( filtre, ondeO );

        onde.setDure( dureNote );

        return onde;
    }

    public static void jouerNote( Onde onde )
    {
        AudioFormat format = new AudioFormat( AudioFormat.Encoding.PCM_SIGNED,
                FREQUENCE_ECHANTILLONAGE, OCTETS_PAR_ECHANTILLON * 8, NB_CANALS, 
                OCTETS_PAR_ECHANTILLON * NB_CANALS, FREQUENCE_ECHANTILLONAGE, false);

        SourceDataLine line;
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, 
                format);
        if (!AudioSystem.isLineSupported(info)) {
            System.err.print( "Ligne non supporte par l'ordinateur." );
            System.exit( -1 );
        }
        try {
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);

            byte[] buffer = new byte[NB_OCTETS];

            line.start();

            int j = 0;

            for( Double a : onde )
            {
                short amp = (short)Math.floor( AMPLITUDE_MAXIMUM * a );
                buffer[j + 0] = (byte) (amp & 0xFF);
                buffer[j + 1] = (byte) ((amp >>> 8) & 0xFF);
                j += TAILLE_CADRE;
                if( j >= NB_OCTETS )
                {
                    line.write(buffer, 0, NB_OCTETS);
                    j = 0;
                }
            }

            if( j != 0 )
            {
                line.write(buffer, 0, j);
            }

            line.drain();
            line.stop();
            line.close();
        } catch (LineUnavailableException ex) {
            System.err.print( "Ligne non disponible." );
            System.exit( -1 );
        }
        line = null;
    }

    /** **************************************************
     * Ici commence l'information importante pour le tp3 :
     ************************************************** */

    public enum TypeOnde {
        SIN, CARRE, SCIE_D, SCIE_M, TRI, BRUIT
    }

    /**
     * dure de la note en seconde, entre 0 et 3.
     */
    public static double dureNote;

    /**
     * Type de la premiere onde.
     */
    public static TypeOnde typeOnde1;

    /**
     * Frequence de la premiere onde, entre 20 et 20000.
     */
    public static double frequence1;

    /**
     * a 'true' si nous voulons utiliser la deuxieme onde.    
     */
    public static boolean utiliseOnde2;

    /**
     * Type de la deuxieme onde.
     */
    public static TypeOnde typeOnde2;

    /**
     * Frequence de la deuxieme onde, entre 20 et 20000.
     */
    public static double frequence2;

    /**
     * Rapport de volume entre les deux ondes, entre 0 et 1.
     */
    public static double ratioVolume;

    /**
     * Variable decrivant le filtre :
     * attaque_filtre : temps d'attaque, entre 0 et 1.
     * declain_filtre : temps du declain de la note, entre 0 et 1.
     * maintien_filtre : volume auquel la note est maintenue, entre 0 et 1.
     * relache_filtre : temps de relache de la note, entre 0 et 1.
     */
    public static double attaque_filtre;
    public static double declain_filtre;
    public static double maintien_filtre;
    public static double relache_filtre;

    public Principal()
    {
        super("Synthetiseur");
        addWindowListener(this);

        dessin = new Graphic();

        /**
         * Valeurs de l'onde initiale.
         */
        dureNote = 1.0;
        typeOnde1 = TypeOnde.SIN;
        frequence1 = 440.0;
        typeOnde2 = TypeOnde.CARRE;
        frequence2 = 220.0;
        ratioVolume = 0.67;
        utiliseOnde2 = true;
        attaque_filtre = 0.1;
        declain_filtre = 0.1;
        maintien_filtre = 0.5;
        relache_filtre = 0.1;

        /**
         * placez vos modifications ici :
         * ( vous decidez ou vous placer le add( dessin ).
         */
        
        //Creation des panels, checkbox, et scrollbars.
        //On y determine egalement leur grandeur et emplacement.
        Panel p = new Panel();
        p.setBounds(0,320,550,143);

        coche = new Checkbox("Deuxième fréquence",true);
        coche.addItemListener(this);

        Panel f = new Panel();
        f.setBounds(155,575,450,200);

        Panel cocheFreq = new Panel();
        cocheFreq.setBounds(20,605,130,76);

        Panel panelFreq = new Panel(); 
        panelFreq.setBounds(10,490,200,150);

        Panel panelAttaque = new Panel(); 
        panelAttaque.setBounds(205,485,150,150);

        Panel panelDeclin = new Panel(); 
        panelDeclin.setBounds(360,485,150,150);

        Panel panelMaintien = new Panel(); 
        panelMaintien.setBounds(510,485,150,150);

        Panel panelRelache = new Panel(); 
        panelRelache.setBounds(660,485,150,150);

        modifFreq = new Scrollbar(Scrollbar.HORIZONTAL,0,1,20,20001);
        modifFreq.setValue(440);
        modifFreq2 = new Scrollbar(Scrollbar.HORIZONTAL,0,1,20,20001);
        modifFreq2.setValue(220);

        filtreA =   new Scrollbar(Scrollbar.HORIZONTAL,0,1,0,11);
        filtreA.setValue(1);
        filtreD =   new Scrollbar(Scrollbar.HORIZONTAL,0,1,0,11);
        filtreD.setValue(1);
        filtreM =   new Scrollbar(Scrollbar.HORIZONTAL,0,1,0,11);
        filtreM.setValue(5);
        filtreR =   new Scrollbar(Scrollbar.HORIZONTAL,0,1,0,11);
        filtreR.setValue(1);
        ratioVol =  new Scrollbar(Scrollbar.HORIZONTAL,0,1,0,101);
        ratioVol.setValue(67);

        
        //Ici on mets les Listeners
        
        modifFreq.addAdjustmentListener(this);
        modifFreq2.addAdjustmentListener(this);
        filtreA.addAdjustmentListener(this);
        filtreD.addAdjustmentListener(this);
        filtreM.addAdjustmentListener(this);
        filtreR.addAdjustmentListener(this);
        ratioVol.addAdjustmentListener(this);

        
        //Chaque element est ajoute a son panel respectif.
        cocheFreq.add(coche);
        panelFreq.add(modifFreq);
        panelAttaque.add(filtreA);
        panelDeclin.add(filtreD);
        panelMaintien.add(filtreM);
        panelRelache.add(filtreR);

        ondeL = new List(6, false);
        ondeL.add("SIN");
        ondeL.add("CARRE");
        ondeL.add("SCIE_D");
        ondeL.add("SCIE_M");
        ondeL.add("TRI");
        ondeL.add("BRUIT");
        ondeL.addActionListener(this);
        p.add(frPrem);
        p.add(ondeL);

        onde2 = new List(6, false);
        onde2.add("SIN");
        onde2.add("CARRE");
        onde2.add("SCIE_D");
        onde2.add("SCIE_M");
        onde2.add("TRI");
        onde2.add("BRUIT");
        onde2.addActionListener(this);
        f.add(onde2);
        f.add(ratioVol);
        f.add(nomRatio);
        f.add(modifFreq2);
        f.add(freqDeux);

        dureTemp = new List(3, false);
        dureTemp.add("1.0");
        dureTemp.add("2.0");
        dureTemp.add("3.0");
        dureTemp.addActionListener(this);
        p.add(dureTemp);
        p.add(nomDuree);

        panelFreq.add(nomFreq);
        panelAttaque.add(nomAttaque);
        panelDeclin.add(nomDeclin);
        panelMaintien.add(nomMaintien);
        panelRelache.add(nomRelache);

        play = new Button ("Play");
        play.addActionListener(this);
        p.add(play);
        
        
        //On ajoute les panels dans la fenetre.
        add(p);
        add(f);
        add(cocheFreq);
        add(panelFreq);
        add(panelAttaque);
        add(panelDeclin);
        add(panelMaintien);
        add(panelRelache);
        add( dessin );

        Onde onde = construireOnde();
        dessin.setFonction(onde);
    }
    List dureTemp;
    List ondeL;
    List onde2;
    Button play;
    Checkbox coche;
    Scrollbar modifFreq;
    Scrollbar modifFreq2;
    Scrollbar filtreA;
    Scrollbar filtreD;
    Scrollbar filtreM;
    Scrollbar filtreR;
    Scrollbar ratioVol;

    Label nomDuree = new Label("Durée");
    Label frPrem = new Label("Fréquence principale");
    Label freqDeux = new Label("Fréquence 2: 220.0");
    Label nomRatio = new Label("Ratio: 0.67");
    Label nomFreq = new Label("Fréquence: 440.0");
    Label nomAttaque = new Label("Attaque: 0.1");
    Label nomDeclin = new Label("Déclin: 0.1");
    Label nomMaintien = new Label("Maintien: 0.5");
    Label nomRelache = new Label("Relâche: 0.1");
    
    
    // Ici on gere l'action apres avoir clique sur la coche de la deuxieme frequence. 
    
    public void itemStateChanged(ItemEvent item){
        if (item.getSource() == coche) {
            if(coche.getState()){
                utiliseOnde2 = true;
            }else{
                utiliseOnde2 = false;
            }

            Onde onde = construireOnde();
            dessin.setFonction(onde);
        }

    }

    
    // Ici on gere l'action apres avoir clique sur les Scrollbars.
    public void adjustmentValueChanged(AdjustmentEvent e){
        if (e.getSource() == modifFreq) {
            frequence1 = modifFreq.getValue();
            nomFreq.setText("Fréquence: " + frequence1);
            Onde onde = construireOnde();
            dessin.setFonction(onde);
        }

        if (e.getSource() == modifFreq2) {
            frequence2 = modifFreq2.getValue();
            freqDeux.setText("Fréquence 2: " + frequence2);
            Onde onde = construireOnde();
            dessin.setFonction(onde);
        }

        if (e.getSource() == filtreA) {
            attaque_filtre = filtreA.getValue();
            nomAttaque.setText("Attaque: " + (attaque_filtre / 10));
            attaque_filtre = attaque_filtre/10;
            Onde onde = construireOnde();
            dessin.setFonction(onde);
        }

        if (e.getSource() == filtreD) {
            declain_filtre = filtreD.getValue();
            nomDeclin.setText("Déclin: " + (declain_filtre / 10));
            declain_filtre = declain_filtre /10;
            Onde onde = construireOnde();
            dessin.setFonction(onde);
        }

        if (e.getSource() == filtreM) {
            maintien_filtre = filtreM.getValue();
            nomMaintien.setText("Maintien: " + (maintien_filtre / 10));
            maintien_filtre = maintien_filtre/10;
            Onde onde = construireOnde();
            dessin.setFonction(onde);
        }

        if (e.getSource() == filtreR) {
            relache_filtre = filtreR.getValue();
            nomRelache.setText("Relâche: " + (relache_filtre/10));
            relache_filtre = relache_filtre/10;
            Onde onde = construireOnde();
            dessin.setFonction(onde);
        }

        if (e.getSource() == ratioVol) {
            ratioVolume = ratioVol.getValue();
            nomRatio.setText("Ratio: " + (ratioVolume)/100);
            ratioVolume = ratioVolume/100;

            Onde onde = construireOnde();
            dessin.setFonction(onde);
        }
    }

    
    // Ici on gere l'action apres avoir clique sur les listes de duree, des types d'ondes et du bouton play. 
    @Override
    public void actionPerformed(ActionEvent act) {

        if (act.getSource() == ondeL) {
            if (ondeL.getSelectedItem()   == "CARRE") {
                typeOnde1 = TypeOnde.CARRE;
            }else if (ondeL.getSelectedItem()   == "SCIE_D") {
                typeOnde1 = TypeOnde.SCIE_D;
            }else if (ondeL.getSelectedItem()   == "SCIE_M") {
                typeOnde1 = TypeOnde.SCIE_M;
            }else if (ondeL.getSelectedItem()   == "TRI") {
                typeOnde1 = TypeOnde.TRI;
            }else if (ondeL.getSelectedItem()   == "BRUIT") {
                typeOnde1 = TypeOnde.BRUIT;
            }else {
                typeOnde1 = TypeOnde.SIN;
            }

            Onde onde = construireOnde();
            dessin.setFonction(onde);
        }

        if (act.getSource() == onde2) {
            if (onde2.getSelectedItem()   == "CARRE") {
                typeOnde2 = TypeOnde.CARRE;
            }else if (onde2.getSelectedItem()   == "SCIE_D") {
                typeOnde2 = TypeOnde.SCIE_D;
            }else if (onde2.getSelectedItem()   == "SCIE_M") {
                typeOnde2 = TypeOnde.SCIE_M;
            }else if (onde2.getSelectedItem()   == "TRI") {
                typeOnde2 = TypeOnde.TRI;
            }else if (onde2.getSelectedItem()   == "BRUIT") {
                typeOnde2 = TypeOnde.BRUIT;
            }else {
                typeOnde2 = TypeOnde.SIN;
            }

            Onde onde = construireOnde();
            dessin.setFonction(onde);
        }

        if (act.getSource() == dureTemp) {
            if (dureTemp.getSelectedItem()   == "2.0") {
                dureNote = 2.0;
            }else if (dureTemp.getSelectedItem()   == "3.0") {
                dureNote = 3.0;
            }else {
                dureNote = 1.0;
            }
            Onde onde = construireOnde();
            dessin.setFonction(onde);
        }

        if (act.getSource() == play) {
            Onde onde = construireOnde();
            dessin.setFonction(onde);
            jouerNote( onde );

        }

    }

    /**
     * @param args
     */
    public static void main(String[] args) {    
        Frame ecran = new Principal();
        ecran.setVisible(true);
        ecran.setSize(900, 750);


    }
}
