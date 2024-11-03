package cat.iesesteveterradas;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        String[] opcionMenu= {"Menu principal","Mostrar una taula","Mostrar personatges per faccio","Mostrar el millor atacant per faccio",
                "Mostrar el millor defensor per faccio","Sortir"};
        String[] opcioSeleccio ={"Menu de seleccio del taula","Mostrar la taula faccio","Mostrar la taula personatge"};
        Path filePath = obtenirPathFitxer();
        try {
            List<String> lines = readFileContent(filePath);

            // Imprimir les línies a la consola
            lines.forEach(System.out::println);
        } catch (IOException e) {
            System.out.println("S'ha produït un error en llegir el fitxer: " + e.getMessage());
        }

        System.out.println("\n".repeat(3)+"Inici del programa");

        String pathBBDD = System.getProperty("user.dir")+"/data/bbdd.db";
        File arxiuBBDD = new File(pathBBDD);
        if (!arxiuBBDD.exists()){
            System.out.println("L'arxiu no existeix");
            try {
                if(arxiuBBDD.createNewFile()){
                    System.out.println("L'arxiu s'ha creat correctament");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Scanner sc = new Scanner(System.in);
        Connection con = iniciarBBDD(pathBBDD);
        int opt = -1;
        int opc = 0;
        String opcStr ="";
        String query="";
        while (opt!=5){
            System.out.println("\n".repeat(3));
            opt = menu(opcionMenu);
            System.out.println("\n".repeat(3));
            switch (opt){
                case 1:

                    opc = menu(opcioSeleccio);
                    if (opc==1){
                        query = "select * from faccio";
                    }else {
                        query = "select * from personatge";
                    }
                    System.out.println("\n".repeat(3));
                    Connexio.querySelect(con,query);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case 2:
                    System.out.print("Introdueix l'id o el nom de la faccio: ");
                    try{
                        opcStr = sc.nextLine();
                        opc = Integer.parseInt(opcStr);
                        query = "select * from personatge where idFaccio = "+opc;
                    }catch (NumberFormatException e){
                        query = "select p.* from personatge p join faccio f on p.idFaccio = f.idFaccio where f.nom like '%"+opcStr+"%'";
                    }
                    Connexio.querySelect(con,query);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case 3:
                    System.out.print("Introdueix l'id o el nom de la faccio: ");
                    try{
                        opcStr = sc.nextLine();
                        opc = Integer.parseInt(opcStr);
                        query = "select * from personatge where idFaccio = "+opc+" order by atac desc limit 1";
                    }catch (NumberFormatException e){
                        query = "select p.* from personatge p join faccio f on p.idFaccio = f.idFaccio where f.nom like '%"+opcStr+"%' order by p.atac desc limit 1";;
                    }
                    Connexio.querySelect(con,query);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    break;
                case 4:
                    System.out.print("Introdueix l'id o el nom de la faccio: ");
                    try{
                        opcStr = sc.nextLine();
                        opc = Integer.parseInt(opcStr);
                        query = "select * from personatge where idFaccio = "+opc+" order by defensa desc limit 1";
                    }catch (NumberFormatException e){
                        query = "select p.* from personatge p join faccio f on p.idFaccio = f.idFaccio where f.nom like '%"+opcStr+"%' " +
                                "order by p.defensa desc limit 1";;
                    }
                    Connexio.querySelect(con,query);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case 5:
                    System.out.println("Sortint del programa");
                    break;

            }
            System.out.println("\n".repeat(3));

        }


    }

    public static Path obtenirPathFitxer() {
        return Paths.get(System.getProperty("user.dir"), "data", "bones_practiques_programacio.txt");
    }

    public static List<String> readFileContent(Path filePath) throws IOException {
        return Files.readAllLines(filePath);
    }
    public static Connection iniciarBBDD(String path){
        // fem la llista de dades de la taula faccio;
        ArrayList<String[]> dadesFaccio = new ArrayList<>();
        dadesFaccio.add(new String[]{"Cavallers", "Though seen as a single group, the Knights are hardly unified. There are many Legions in Ashfeld, the most prominent being The Iron Legion."});
        dadesFaccio.add(new String[]{"Vikings", "The Vikings are a loose coalition of hundreds of clans and tribes, the most powerful being The Warborn."});
        dadesFaccio.add(new String[]{"Samurais","The Samurai are the most unified of the three factions, though this does not say much as the Daimyos were often battling each other for dominance."});
        // fem la llista de dades de la taula personatge;
        ArrayList<String[]> dadesPersonatge = new ArrayList<>();
        dadesPersonatge.add(new String[]{"Warden","1","3","1"});
        dadesPersonatge.add(new String[]{"Conqueror","2","2","1"});
        dadesPersonatge.add(new String[]{"Peacekeep","2","3","1"});

        dadesPersonatge.add(new String[]{"Raider","3","3","2"});
        dadesPersonatge.add(new String[]{"Warlord","2","2","2"});
        dadesPersonatge.add(new String[]{"Berserker","1","1","2"});

        dadesPersonatge.add(new String[]{"Kensei","3","2","3"});
        dadesPersonatge.add(new String[]{"Shugoki","2","1","3"});
        dadesPersonatge.add(new String[]{"Orochi","3","2","3"});

        //eliminem tables si existeixen
        Connection conn = Connexio.connect(path);
        String query="Drop table if exists faccio";
        Connexio.queryUpdate(conn,query);
        query="Drop table if exists personatge";
        Connexio.queryUpdate(conn,query);

        //creem les tables
        query="Create table if not exists faccio('idFaccio' integer not null PRIMARY KEY AUTOINCREMENT,'nom' varchar(15) not null,'resum' varchar(500))";
        Connexio.queryUpdate(conn,query);
        query="Create table if not exists personatge('idPersonatge' integer not null PRIMARY KEY AUTOINCREMENT,'nom' varchar(15) not null," +
                "'atac' integer(3) not null,'defensa' integer(3) not null, 'idFaccio' integer(3), foreign key (idFaccio) references faccio(id))";
        Connexio.queryUpdate(conn,query);

        //creem la query per la taula faccio
        String queryInsertFaccio = "insert into faccio(nom,resum) values";
        //System.out.println(queryInsertFaccio);
        for (int i = 0; i < dadesFaccio.size(); i++) {
            if (i!=0){
                queryInsertFaccio+=",('%s','%s')";
            }else {
                queryInsertFaccio+="('%s','%s')";
            }
            queryInsertFaccio=queryInsertFaccio.formatted(dadesFaccio.get(i)[0],dadesFaccio.get(i)[1]);

        }

        Connexio.queryUpdate(conn,queryInsertFaccio);
//        query ="Select * from faccio";
//        Connexio.querySelect(conn,query);

        //creem la query per la taula personatge
        String queryInsertPersonatge = "insert into personatge(nom,atac,defensa,idFaccio) values";
//        System.out.println(queryInsertPersonatge);
        for (int i = 0; i < dadesPersonatge.size(); i++) {
            if (i!=0){
                queryInsertPersonatge+=",('%s',%d,%d,%d)";
            }else {
                queryInsertPersonatge+="('%s',%d,%d,%d)";
            }
            queryInsertPersonatge=queryInsertPersonatge.formatted(dadesPersonatge.get(i)[0],Integer.parseInt(dadesPersonatge.get(i)[1])
                    ,Integer.parseInt(dadesPersonatge.get(i)[2]),Integer.parseInt(dadesPersonatge.get(i)[3]));


        }

        Connexio.queryUpdate(conn,queryInsertPersonatge);
//        query ="Select * from personatge";
//        Connexio.querySelect(conn,query);
        return conn;
    }


    public static int menu(String[] opcions){
        int opt=-1;
        for (int i = 0; i < opcions.length; i++) {
            if (i!=0){
                System.out.println(i+". "+opcions[i]);
            }else {
                System.out.printf("*".repeat(47)+"\n%"+(47+opcions[i].length())/2+"s \n"+"*".repeat(47)+"%n",opcions[i]);
            }

        }
        Scanner sc = new Scanner(System.in);
        System.out.print("Opcio: ");
        try {
            opt=Integer.parseInt(sc.nextLine());

            assert (1<=opt && opt <= opcions.length-1);


        }catch (NumberFormatException e){
            System.err.println("Has d'introduir un numero");
        }catch (AssertionError e){
            System.err.println("Has d'introduir un numero entre 1 y "+(opcions.length-1));
            opt=-1;
        }
        return opt;
    }


}
