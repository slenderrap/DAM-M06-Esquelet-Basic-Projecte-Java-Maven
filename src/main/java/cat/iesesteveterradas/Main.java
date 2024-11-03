package cat.iesesteveterradas;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static cat.iesesteveterradas.Connexio.iniciarBBDD;

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
                    sc.close();
                    try {
                        con.close();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
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
