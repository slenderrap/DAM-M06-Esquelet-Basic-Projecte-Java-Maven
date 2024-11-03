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