// Import SQL package
import java.sql.*;

public class JDBC {

    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/BOOKS";

    //  Database credentials
    static final String USER = "root";
    static final String PASS = "password";

    // NOTE: For our project, we set up a MySQL server on our local machine with
    //       the credentials "root" and "password" as the user and password, respectively.
    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        try {
            // Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            // Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // Create statement from connection
            System.out.println("Creating statement...");
            stmt = conn.createStatement();


            // We want to drop previously created tables because we are creating tables every time we run the program.
            System.out.println("Creating tables & triggers");
            stmt.executeUpdate(SQL.DROP_TABLES);
            stmt.execute(SQL.CREATE_AUTHORS_TABLE);
            stmt.execute(SQL.CREATE_PUBLISHERS_TABLE);
            stmt.execute(SQL.CREATE_TITLES_TABLE);
            stmt.execute(SQL.CREATE_AUTHOR_ISBN_TABLE);
            // Create triggers for authors and authorISBN tables
            stmt.execute(SQL.CREATE_TRIGGER_DELETE_AUTHOR);
            stmt.execute(SQL.CREATE_TRIGGER_DELETE_AUTHOR_ISBN);

            //** Refer to the SQL.java class for detailed descriptions on the SQL statements **

            // Insert authors data
            for (int i = 0; i < SQL.INSERT_INTO_AUTHORS.length; i++) {
                stmt.executeUpdate(SQL.INSERT_INTO_AUTHORS[i]);
            }
            // Insert publishers data
            for (int i = 0; i < SQL.INSERT_INTO_PUBLISHERS.length; i++) {
                stmt.executeUpdate(SQL.INSERT_INTO_PUBLISHERS[i]);
            }
            // Insert titles data
            for (int i = 0; i < SQL.INSERT_INTO_TITLE.length; i++) {
                stmt.executeUpdate(SQL.INSERT_INTO_TITLE[i]);
            }
            // Insert authorISBN data
            for (int i = 0; i < SQL.INSERT_INTO_AUTHOR_ISBN.length; i++) {
                stmt.executeUpdate(SQL.INSERT_INTO_AUTHOR_ISBN[i]);
            }

            System.out.println("\nQUERY: Printing all authors by last name");
            printAuthorsByLastName(stmt);

            System.out.println("\nQUERY: Printing all publishers");
            printAllPublishers(stmt);

            System.out.println("\nQUERY: Printing all books published by Pearson");
            printBooksByPublisher(stmt, "Pearson");

            System.out.println("\nAdding new author with the name 'Johnny Bravo' into the authors table.");
            String addAuthorSQL = "INSERT INTO authors (firstName, lastName) VALUES ('JOHNNY', 'BRAVO');\n";
            stmt.executeUpdate(addAuthorSQL);

            System.out.println("\nUpdating existing information about an author");
            String updateAuthorSQL = "UPDATE authors SET firstName = 'Blah blah' WHERE firstName = 'Susan'";
            stmt.executeUpdate(updateAuthorSQL);

            System.out.println("\nAdd a new title for author 'Steven King'");
            String addNewTitleForAuthorSQL = "INSERT INTO titles (isbn, editionNumber, Year, publisherID, price, Title) " +
                                             "VALUES(" +
                                                     "(SELECT isbn " +
                                                     "FROM authorISBN " +
                                                     "WHERE authorID = (SELECT authorID " +
                                                                       "FROM authors " +
                                                                       "WHERE firstName = 'Steven' " +
                                                                         "AND lastName = 'King'))," +
                                                     "1, \"2023\", 16, 10.11, \"New Book\");\n";

            System.out.println("\nAdding new publisher");
            String insertNewPublisherSql = "INSERT INTO publishers (publisherName) VALUES (\"New Publisher\");";
            stmt.executeUpdate(insertNewPublisherSql);

            System.out.println("\nUpdate existing information about publisher");
            String updatePublisherInfoSql = "UPDATE publishers SET publisherName = 'New Publisher Name' WHERE publisherName = 'Pearson'";
            stmt.executeUpdate(updatePublisherInfoSql);


            stmt.close();
            conn.close();
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            // By calling this in the finally block, we guarantee
            // that the statement and connection are closed.
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
            }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }

        System.out.println("Done.");
    }


    /**
     * Print all data in the authors table ordered by their last name
     * @param stmt the statement object created from Connection.createStatement()
     */
    private static void printAuthorsByLastName(Statement stmt) throws SQLException {
        String sql = "SELECT * FROM authors ORDER BY lastName, firstName ASC";
        ResultSet resultSet = stmt.executeQuery(sql);
        System.out.printf("%-9s| %-15s| %-12s\n", "authorID", "lastName", "firstName");
        System.out.println("---------------------------------------------------");
        while(resultSet.next()){
            //Retrieve by column name
            int authorID  = resultSet.getInt("authorID");
            String last = resultSet.getString("lastName");
            String first = resultSet.getString("firstName");
            //Display values
            System.out.printf("%-9s| %-15s| %-12s\n", authorID, last, first);
        }
        System.out.println();
    }

    /**
     * Print all data in the titles table
     * @param stmt the statement object created from Connection.createStatement()
     */
    private static void printTitles(Statement stmt) throws SQLException {
        String sql = "SELECT * FROM titles";
        ResultSet resultSet = stmt.executeQuery(sql);
        //STEP 5: Extract data from result set
        System.out.printf("%-13s| %-25s| %-14s| %-5s| %-12s| %-4s\n", "isbn", "title", "editionNumber", "year", "publisherID", "price");
        System.out.println("----------------------------------------------------------------------------------------------------");
        while(resultSet.next()){
            //Retrieve by column name
            String isbn  = resultSet.getString("isbn");
            String title = resultSet.getString("title");
            int editionNumber = resultSet.getInt("editionNumber");
            String year = resultSet.getString("Year");
            int publisherID = resultSet.getInt("publisherID");
            float price = resultSet.getFloat("price");
            //Display values
            System.out.printf("%-13s| %-25s| %-14s| %-5s| %-12s| %-4s\n", isbn, title, editionNumber, year, publisherID, price);
        }
        System.out.println();
    }

    /**
     * Print all publishers in the 'publishers' table
     * @param stmt the statement object created from Connection.createStatement()
     */
    private static void printAllPublishers(Statement stmt) throws SQLException {
        String sql = "SELECT * FROM publishers";
        ResultSet resultSet = stmt.executeQuery(sql);
        System.out.printf("%-12s| %-15s\n", "publisherID", "publisherName");
        System.out.println("-------------------------------------");
        while(resultSet.next()){
            //Retrieve by column name
            int publisherID  = resultSet.getInt("publisherID");
            String publisherName = resultSet.getString("publisherName");
            //Display values
            System.out.printf("%-12s| %-15s\n", publisherID, publisherName);
        }
        System.out.println();
    }

    /**
     * Selects a specific publisher and prints all books published by that publisher.
     * @param stmt the statement object created from Connection.createStatement()
     * @param publisherName the specific publisher we want books published by
     */
    private static void printBooksByPublisher(Statement stmt, String publisherName) throws SQLException {
        String publisherIdSql = "SELECT publisherID FROM publishers WHERE publisherName = \"" + publisherName + "\"";
        ResultSet result = stmt.executeQuery(publisherIdSql);
        System.out.printf("%-25s| %-5s| %-13s\n", "title", "year", "isbn");
        System.out.println("----------------------------------------------------------------------------------------------------");
        if (result.next()) {
            final int publisherID = result.getInt("publisherID");
            String booksSql = "SELECT title, year, isbn FROM titles WHERE publisherID = " + publisherID + " ORDER BY title ASC";
            ResultSet booksResultSet = stmt.executeQuery(booksSql);
            while(booksResultSet.next()){
                //Retrieve by column name
                String title = booksResultSet.getString("title");
                String year = booksResultSet.getString("year");
                String isbn = booksResultSet.getString("isbn");
                //Display values
                System.out.printf("%-25s| %-5s| %-13s\n", title, year, isbn);
            }
        }
    }

    /**
     * Print all data in the authorsISBN table
     * @param stmt the statement object created from Connection.createStatement()
     */
    private static void printAuthorISBNs(Statement stmt) throws SQLException {
        String sql = "SELECT * FROM authorISBN";
        ResultSet resultSet = stmt.executeQuery(sql);
        System.out.printf("%-9s| %-13s\n", "authorID", "ISBN");
        System.out.println("-------------------------------------");
        while(resultSet.next()){
            //Retrieve by column name
            int authorID  = resultSet.getInt("authorID");
            String isbn = resultSet.getString("isbn");
            //Display values
            System.out.printf("%-9s| %-13s\n", authorID, isbn);
        }
        System.out.println();
    }
}