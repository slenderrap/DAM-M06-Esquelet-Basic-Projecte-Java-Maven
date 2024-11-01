package cat.iesesteveterradas;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        Path filePath = obtenirPathFitxer();
        try {
            List<String> lines = readFileContent(filePath);

            // Imprimir les línies a la consola
            lines.forEach(System.out::println);
        } catch (IOException e) {
            System.out.println("S'ha produït un error en llegir el fitxer: " + e.getMessage());
        }
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
        iniciarBBDD(pathBBDD);
        ResultSet rs = null;
        Connexio connexio= new Connexio();


    }

    public static Path obtenirPathFitxer() {
        return Paths.get(System.getProperty("user.dir"), "data", "bones_practiques_programacio.txt");
    }

    public static List<String> readFileContent(Path filePath) throws IOException {
        return Files.readAllLines(filePath);
    }
    public static void iniciarBBDD(String path){
        Connection conn = Connexio.connect(path);
        String query="Drop table if exists faccio";
        Connexio.queryUpdate(conn,query);
        query="Drop table if exists personatge";
        Connexio.queryUpdate(conn,query);
        query="Create table if not exists faccio('id' integer PRIMARY KEY AUTOINCREMENT,'nom' varchar(15),'resum' varchar(500))";
        Connexio.queryUpdate(conn,query);
        query ="Select * from faccio";
        Connexio.querySelect(conn,query);
        query="Create table if not exists personatge('id' integer PRIMARY KEY AUTOINCREMENT,'nom' varchar(15),'atac' integer,'defensa' integer," +
                " 'idFaccio' integer, foreign key (idFaccio) references faccio(id))";
        Connexio.queryUpdate(conn,query);
        query ="Select * from personatge";
        Connexio.querySelect(conn,query);

    }
}
