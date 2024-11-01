package cat.iesesteveterradas;

import java.sql.*;

public class Connexio {
    public static Connection connect(String arxiu){
        Connection  conn = null;
        try{
            String url = "jdbc:sqlite:"+arxiu;
            conn = DriverManager.getConnection(url);
            if (conn != null){
                DatabaseMetaData dbMetaData = conn.getMetaData();
                System.out.println(dbMetaData.getDriverName());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return conn;
    }
    public static void queryUpdate(Connection conn, String query){

        try {
            Statement statement = conn.createStatement();
            statement.executeUpdate(query);
            System.out.println("query be");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static void querySelect(Connection conn, String query){
        ResultSet rs = null;
        try{
            Statement statement = conn.createStatement();
            rs = statement.executeQuery(query);
            ResultSetMetaData rsmd = rs.getMetaData();
            System.out.printf("*******************************\nColumnes de la taula %s \n*******************************\n",rsmd.getTableName(1));
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                System.out.println(rsmd.getColumnName(i)+ " tipus: "+rsmd.getColumnTypeName(i) );

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}
