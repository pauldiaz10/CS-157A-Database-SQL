
public class SQL {

    // We drop pre-existing tables first to ensure that we can initialize new tables every time we run the program.
    static final String DROP_TABLES =
            "DROP TABLE IF EXISTS authorISBN, authors, titles, publishers;";

    /**
     * Each tuple in the 'authors' table will include a unique authorID that is automatically generated when we insert data.
     * We also have the first name and last name included inside the authors table.
     */
    static final String CREATE_AUTHORS_TABLE =
            "CREATE TABLE authors (\n" +
            " authorID INTEGER NOT NULL UNIQUE AUTO_INCREMENT PRIMARY KEY,\n" +
            " firstName CHAR(20) ,\n" +
            " lastName CHAR(20) \n" +
            " );";

    /**
     * The 'publishers' table will include a unique publisherID that is automatically generated when we insert a
     * tuple into the publishers table with the publisher's name.
     */
    static final String CREATE_PUBLISHERS_TABLE =
            "CREATE TABLE publishers (\n" +
            " publisherID INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,\n" +
            " publisherName CHAR(100) \n" +
            " );";

    /**
     * The ISBN, edition #, publication year, publisher ID, price of book, and book title are defined in the 'titles' table.
     * The primary key in this table is the ISBN.
     * The publisherID in this table is created as a reference to the publisherID in the 'publishers' table. If we
     *  delete or update the publisherID from the 'publishers' table, the publisherID will be updated the 'titles'
     *  table also.
     */
    static final String CREATE_TITLES_TABLE =
            "CREATE TABLE titles (\n" +
            " isbn VARCHAR(13) PRIMARY KEY,\n" +
            " editionNumber INTEGER ,\n" +
            " Year CHAR(4) ,\n" +
            " publisherID INTEGER NOT NULL,\n" +
            " price FLOAT(8,2) NOT NULL,\n" +
            " title VARCHAR(500) NOT NULL,\n" +
            " INDEX (publisherID),\n" +
            " FOREIGN KEY (publisherID) REFERENCES publishers(publisherID) ON DELETE CASCADE ON UPDATE CASCADE\n" +
            " );";

    /**
     * The 'authorISBN' table just includes the authorID and isbn.
     * The authorID in this table is a reference to the authorID in the 'authors' table.
     * Similarly, the isbn in this table is a reference to the isbn in the 'titles' table.
     * If either authorID or isbn is updated or deleted from the table they are referenced from,
     *  the authorID and isbn in this table will be updated or deleted, too.
     */
    static final String CREATE_AUTHOR_ISBN_TABLE =
            "CREATE TABLE authorISBN (\n" +
            " authorID INTEGER ,\n" +
            " isbn VARCHAR(13) ,\n" +
            " INDEX (authorID, isbn),\n" +
            " FOREIGN KEY (authorID) REFERENCES authors(authorID) ON DELETE CASCADE,\n" +
            " FOREIGN KEY (isbn) REFERENCES titles(isbn) ON DELETE CASCADE\n" +
            " );";

    /**
     * A book cannot exist without an author, so if we delete an author from the 'authors' table,
     *  we have to delete the tuple in the 'titles' that is associated with the deleted author.
     * We cannot do this with cascading, so we create trigger that does accomplishes this.
     */
    static final String CREATE_TRIGGER_DELETE_AUTHOR =
            "CREATE TRIGGER BOOKS.DeleteAuthor\n" +
            "BEFORE DELETE ON authors\n" +
            "FOR EACH ROW\n" +
            "BEGIN\n" +
            "   DELETE FROM titles\n" +
            "   WHERE isbn = (SELECT isbn FROM authorISBN\n" +
            "                 WHERE  authorID = (SELECT authorID\n" +
            "                                    FROM   authors\n" +
            "                                    WHERE  firstName = OLD.firstName\n" +
            "                                      AND  lastName  = OLD.lastName));\n" +
            "END;";

    /**
     * The 'authorISBN' table have fields that reference from the 'authors' and 'titles' table.
     * If we delete a data from the 'authorISBN' table, we have to delete the associated data
     *  in the 'authors' and 'titles' table, too.
     * Therefore, we create a trigger that deletes the 'authors' and 'titles' table if
     *  we delete something from the 'authorISBN' table.
     */
    static final String CREATE_TRIGGER_DELETE_AUTHOR_ISBN =
            "CREATE TRIGGER BOOKS.DeleteAuthorISBN\n" +
            "AFTER DELETE ON authorISBN\n" +
            "FOR EACH ROW\n" +
            "BEGIN\n" +
            "   DELETE FROM authors\n" +
            "   WHERE authorID = OLD.authorID;\n" +
            "   DELETE FROM titles\n" +
            "   WHERE isbn = OLD.isbn;\n" +
            "END;";

    /**
     * SQL code that inserts data into the 'authors' table.
     * Note that the authorID is automatically generated with unique values.
     */
    static final String[] INSERT_INTO_AUTHORS = {
            "INSERT INTO authors (firstName, lastName) VALUES (\"Steven\", \"King\");\n",
            "INSERT INTO authors (firstName, lastName) VALUES (\"J.K.\", \"Rowling\");\n",
            "INSERT INTO authors (firstName, lastName) VALUES (\"Susan\", \"McBride\");\n",
            "INSERT INTO authors (firstName, lastName) VALUES (\"Christopher\", \"Mari\");\n",
            "INSERT INTO authors (firstName, lastName) VALUES (\"James\", \"Patterson\");\n",
            "INSERT INTO authors (firstName, lastName) VALUES (\"James\", \"Joyce\");\n",
            "INSERT INTO authors (firstName, lastName) VALUES (\"Scott\", \"Fitzgerald\");\n",
            "INSERT INTO authors (firstName, lastName) VALUES (\"Vladimir\", \"Nabokov\");\n",
            "INSERT INTO authors (firstName, lastName) VALUES (\"William\", \"Faulkner\");\n",
            "INSERT INTO authors (firstName, lastName) VALUES (\"Aldous\", \"Huxley\");\n",
            "INSERT INTO authors (firstName, lastName) VALUES (\"Arthur\", \"Koestler\");\n",
            "INSERT INTO authors (firstName, lastName) VALUES (\"Ayn\", \"Rand\");\n",
            "INSERT INTO authors (firstName, lastName) VALUES (\"Ron\", \"Hubbard\");\n",
            "INSERT INTO authors (firstName, lastName) VALUES (\"John\", \"Tolkein\");\n",
            "INSERT INTO authors (firstName, lastName) VALUES (\"Harper\", \"Lee\");"
    };

    /**
     * SQL code that inserts data into the 'publishers' table.
     * Note that the publisherID is automatically generated with unique values.
     */
    static final String[] INSERT_INTO_PUBLISHERS = {
            "INSERT INTO publishers (publisherName) VALUES (\"Signet Books\");\n",
            "INSERT INTO publishers (publisherName) VALUES (\"Pearson\");\n",
            "INSERT INTO publishers (publisherName) VALUES (\"ThomsonReuters\");\n",
            "INSERT INTO publishers (publisherName) VALUES (\"Penguin Random\");\n",
            "INSERT INTO publishers (publisherName) VALUES (\"Wiley\");\n",
            "INSERT INTO publishers (publisherName) VALUES (\"Nature America\");\n",
            "INSERT INTO publishers (publisherName) VALUES (\"Scholastic, Inc.\");\n",
            "INSERT INTO publishers (publisherName) VALUES (\"Warner Bros Global Publishing\");\n",
            "INSERT INTO publishers (publisherName) VALUES (\"Modern Library\");\n",
            "INSERT INTO publishers (publisherName) VALUES (\"Publishers Group West\");\n",
            "INSERT INTO publishers (publisherName) VALUES (\"Crown Publishers, Inc.\");\n",
            "INSERT INTO publishers (publisherName) VALUES (\"Harper Collins Publishing Co\");\n",
            "INSERT INTO publishers (publisherName) VALUES (\"St. Martin's Press\");\n",
            "INSERT INTO publishers (publisherName) VALUES (\"Allworth Press\");\n",
            "INSERT INTO publishers (publisherName) VALUES (\"German Publishers\");"
    };

    /**
     * SQL code that inserts data into the 'titles' table.
     * To create a reference to publisherID in the 'publishers' table, we insert
     *  a SELECT statement inside each INSERT INTO statement. The SELECT statement
     *  gets a publisherID from the 'publishers' table and includes that value as
     *  part of the data inserted into the 'titles' table.
     */
    static final String[] INSERT_INTO_TITLE = {
            "INSERT INTO titles (isbn, editionNumber, Year, publisherID, price, Title) VALUES(\"9780451001234\", 1, \"2000\", (SELECT publisherID FROM publishers WHERE publisherName = \"Signet Books\"), 10.11, \"Harry Potter\");\n",
            "INSERT INTO titles (isbn, editionNumber, Year, publisherID, price, Title) VALUES(\"9780451115089\", 1, \"1982\", (SELECT publisherID FROM publishers WHERE publisherName = \"Pearson\"), 14.99, \"The Running Man\");\n",
            "INSERT INTO titles (isbn, editionNumber, Year, publisherID, price, Title) VALUES(\"9780451111111\", 1, \"1982\", (SELECT publisherID FROM publishers WHERE publisherName = \"Pearson\"), 15.99, \"A Running Man\");\n",
            "INSERT INTO titles (isbn, editionNumber, Year, publisherID, price, Title) VALUES(\"9780451110000\", 1, \"1982\", (SELECT publisherID FROM publishers WHERE publisherName = \"Pearson\"), 16.99, \"Zzz Running Man\");\n",
            "INSERT INTO titles (isbn, editionNumber, Year, publisherID, price, title) VALUES(\"9450260214523\", 1, \"1992\", (SELECT publisherID FROM publishers WHERE publisherName = \"ThomsonReuters\"), 26.00, \"Ulysses\");\n",
            "INSERT INTO titles (isbn, editionNumber, Year, publisherID, price, Title) VALUES(\"9780451240599\", 1, \"1933\", (SELECT publisherID FROM publishers WHERE publisherName = \"Penguin Random\"), 14.99, \"The Great Penguin\");\n",
            "INSERT INTO titles (isbn, editionNumber, Year, publisherID, price, title) VALUES(\"1024483866640\", 2, \"1925\", (SELECT publisherID FROM publishers WHERE publisherName = \"Wiley\"), 19.22, \"The Great Gatsby\");\n",
            "INSERT INTO titles (isbn, editionNumber, Year, publisherID, price, title) VALUES(\"4303004852112\", 1, \"1955\", (SELECT publisherID FROM publishers WHERE publisherName = \"Nature America\"), 19.1, \"Lolita\");\n",
            "INSERT INTO titles (isbn, editionNumber, Year, publisherID, price, title) VALUES(\"3459073487102\", 1, \"1932\", (SELECT publisherID FROM publishers WHERE publisherName = \"Scholastic, Inc.\"), 12.2, \"Brave New World\");\n",
            "INSERT INTO titles (isbn, editionNumber, Year, publisherID, price, title) VALUES(\"2374009213487\", 2, \"1931\", (SELECT publisherID FROM publishers WHERE publisherName = \"Warner Bros Global Publishing\"), 14.99, \"The Sound and the Fury\");\n",
            "INSERT INTO titles (isbn, editionNumber, Year, publisherID, price, title) VALUES(\"1742009834762\", 3, \"1940\", (SELECT publisherID FROM publishers WHERE publisherName = \"Modern Library\"), 10.02, \"Darkness at Noon\");\n",
            "INSERT INTO titles (isbn, editionNumber, Year, publisherID, price, title) VALUES(\"4902348764122\", 1, \"1957\", (SELECT publisherID FROM publishers WHERE publisherName = \"Publishers Group West\"), 12.22, \"Atlas Shrugged\");\n",
            "INSERT INTO titles (isbn, editionNumber, Year, publisherID, price, title) VALUES(\"2489092387645\", 3, \"1943\", (SELECT publisherID FROM publishers WHERE publisherName = \"Crown Publishers, Inc.\"), 11.4, \"The Fountainhead\");\n",
            "INSERT INTO titles (isbn, editionNumber, Year, publisherID, price, title) VALUES(\"9804383119811\", 1, \"1982\", (SELECT publisherID FROM publishers WHERE publisherName = \"Harper Collins Publishing Co\"), 15.93, \"Battlefield Earth\");\n",
            "INSERT INTO titles (isbn, editionNumber, Year, publisherID, price, title) VALUES(\"2984509238452\", 1, \"1954\", (SELECT publisherID FROM publishers WHERE publisherName = \"St. Martin's Press\"), 23, \"The Lord of the Rings\");\n",
            "INSERT INTO titles (isbn, editionNumber, Year, publisherID, price, title) VALUES(\"1345230982345\", 3, \"1960\", (SELECT publisherID FROM publishers WHERE publisherName = \"Allworth Press\"), 15.22, \"To Kill a Mockingbird\");\n",
            "INSERT INTO titles (isbn, editionNumber, Year, publisherID, price, title) VALUES(\"9302340119475\", 1, \"1966\", (SELECT publisherID FROM publishers WHERE publisherName = \"German Publishers\"), 15.99, \"Dune\");"
    };

    /**
     * SQL code that inserts data into the 'authorISBN' table.
     * This table generates data that already exists in the 'authors' and 'titles' table; it includes the authorID
     * and isbn data from 'authors' and 'titles', respectively. We do this by including two SELECT statements in
     * our insertion statements.
     * The first SELECT statement gets an authorID from the 'authors' table.
     * The second SELECT statement gets an ISBN from the 'titles' table.
     */
    static final String[] INSERT_INTO_AUTHOR_ISBN = {
            "INSERT INTO authorISBN (authorID, isbn) VALUES ((SELECT authorID FROM authors WHERE firstName = \"Steven\" AND lastName = \"King\"), (SELECT isbn FROM titles WHERE title = \"Harry Potter\"));\n",
            "INSERT INTO authorISBN (authorID, isbn) VALUES ((SELECT authorID FROM authors WHERE firstName = \"J.K.\" AND lastName = \"Rowling\"), (SELECT isbn FROM titles WHERE title = \"The Running Man\"));\n",
            "INSERT INTO authorISBN (authorID, isbn) VALUES ((SELECT authorID FROM authors WHERE firstName = \"Susan\" AND lastName = \"McBride\"), (SELECT isbn FROM titles WHERE title = \"Ulysses\"));\n",
            "INSERT INTO authorISBN (authorID, isbn) VALUES ((SELECT authorID FROM authors WHERE firstName = \"Christopher\" AND lastName = \"Mari\"), (SELECT isbn FROM titles WHERE title = \"The Great Gatsby\"));\n",
            "INSERT INTO authorISBN (authorID, isbn) VALUES ((SELECT authorID FROM authors WHERE firstName = \"James\" AND lastName = \"Patterson\"), (SELECT isbn FROM titles WHERE title = \"Lolita\"));\n",
            "INSERT INTO authorISBN (authorID, isbn) VALUES ((SELECT authorID FROM authors WHERE firstName = \"James\" AND lastName = \"Joyce\"), (SELECT isbn FROM titles WHERE title = \"Brave New World\"));\n",
            "INSERT INTO authorISBN (authorID, isbn) VALUES ((SELECT authorID FROM authors WHERE firstName = \"Scott\" AND lastName = \"Fitzgerald\"), (SELECT isbn FROM titles WHERE title = \"The Sound and the Fury\"));\n",
            "INSERT INTO authorISBN (authorID, isbn) VALUES ((SELECT authorID FROM authors WHERE firstName = \"Vladimir\" AND lastName = \"Nabokov\"), (SELECT isbn FROM titles WHERE title = \"Darkness at Noon\"));\n",
            "INSERT INTO authorISBN (authorID, isbn) VALUES ((SELECT authorID FROM authors WHERE firstName = \"William\" AND lastName = \"Faulkner\"), (SELECT isbn FROM titles WHERE title = \"Atlas Shrugged\"));\n",
            "INSERT INTO authorISBN (authorID, isbn) VALUES ((SELECT authorID FROM authors WHERE firstName = \"Aldous\" AND lastName = \"Huxley\"), (SELECT isbn FROM titles WHERE title = \"The Fountainhead\"));\n",
            "INSERT INTO authorISBN (authorID, isbn) VALUES ((SELECT authorID FROM authors WHERE firstName = \"Arthur\" AND lastName = \"Koestler\"), (SELECT isbn FROM titles WHERE title = \"Battlefield Earth\"));\n",
            "INSERT INTO authorISBN (authorID, isbn) VALUES ((SELECT authorID FROM authors WHERE firstName = \"Ayn\" AND lastName = \"Rand\"), (SELECT isbn FROM titles WHERE title = \"The Lord of the Rings\"));\n",
            "INSERT INTO authorISBN (authorID, isbn) VALUES ((SELECT authorID FROM authors WHERE firstName = \"Ron\" AND lastName = \"Hubbard\"), (SELECT isbn FROM titles WHERE title = \"To Kill a Mockingbird\"));\n",
            "INSERT INTO authorISBN (authorID, isbn) VALUES ((SELECT authorID FROM authors WHERE firstName = \"John\" AND lastName = \"Tolkein\"), (SELECT isbn FROM titles WHERE title = \"Dune\"));\n",
            "INSERT INTO authorISBN (authorID, isbn) VALUES ((SELECT authorID FROM authors WHERE firstName = \"Harper\" AND lastName = \"Lee\"), (SELECT isbn FROM titles WHERE title = \"The Great Penguin\"));"
    };

    private SQL() {
        // Should not instantiate this class.
    }
}
