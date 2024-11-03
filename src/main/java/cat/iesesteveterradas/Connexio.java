package cat.iesesteveterradas;

import java.sql.*;
import java.util.ArrayList;

public class Connexio {
    public static Connection connect(String arxiu) {
        Connection conn = null;
        try {
            String url = "jdbc:sqlite:" + arxiu;
            conn = DriverManager.getConnection(url);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return conn;
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

    public static void queryUpdate(Connection conn, String query) {

        try {
            Statement statement = conn.createStatement();
            statement.executeUpdate(query);
//          System.out.println("query " + query.substring(0, 11) + " be");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static void querySelect(Connection conn, String query) {
        ResultSet rs = null;
        try {
//          System.out.println("\n\nconsulta: " + query);
            Statement statement = conn.createStatement();
            rs = statement.executeQuery(query);
            ResultSetMetaData rsmd = rs.getMetaData();
            //nom de les columnes
            String columnes = "";
            ArrayList<Integer> midaColumna = new ArrayList<>();

            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                int espai = 0;

                if (i != 1) {
                    espai = rsmd.getPrecision(i);
                } else {
                    espai = 3;
                }
                String cadena = "%-" + espai + "s |";
                cadena = cadena.formatted(rsmd.getColumnName(i));//+ " tipus: "+rsmd.getColumnTypeName(i);
                midaColumna.add(cadena.length());

                columnes += cadena;
            }
            //crear capcelera
            int width = columnes.length();
            String capcelera = "%s%n%" + ((width / 5) + rsmd.getTableName(1).length() / 2) + "s Taula %s%n%s%n";

            System.out.printf(capcelera, "*".repeat(width), " ", rsmd.getTableName(1), "*".repeat(width));
            System.out.println(columnes + "\n" + "-".repeat(columnes.length()));
            //mostrar dades
            boolean resultats = false;
            while (rs.next()){

                String fila = "";
                    for (int i = 1; i <= rsmd.getColumnCount(); i++) {

                        int espai = 0;

                        if (i != 1) {

                            if (espai< midaColumna.get(i-1)){
                                espai=midaColumna.get(i-1)-2;
                            }else {
                                espai = rsmd.getPrecision(i);
                            }
                        } else {
                            resultats=true;
                            if (espai< midaColumna.get(i-1)){
                                espai= -midaColumna.get(i-1)+2;
                            }else {
                                espai = -rsmd.getPrecision(i);
                            }
                        }


                        if (rsmd.getColumnTypeName(i).equals("INTEGER")) {
                            String cadena = "%" + espai + "s |";
                            fila += cadena.formatted(rs.getInt(i));
                        } else if (rsmd.getColumnTypeName(i).equals("VARCHAR")) {
                            String cadena = "%-" + espai + "s |";
                            fila += cadena.formatted(rs.getString(i));
                        }
                    }
                System.out.println(fila);
            }

            if  (!resultats){
                System.out.println("No hi han resultats");
            }

            System.out.println("-".repeat(columnes.length()));
            rs.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}