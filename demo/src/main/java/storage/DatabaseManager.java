package storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:library.db";

    public DatabaseManager() {
        createTables();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private void createTables() {
        String createBorrowersTable = "CREATE TABLE IF NOT EXISTS borrowers (" +
                "id TEXT PRIMARY KEY," +
                "name TEXT NOT NULL," +
                "password TEXT NOT NULL," +
                "regis_time TEXT NOT NULL" +
                ")";
        String createStoresTable = "CREATE TABLE IF NOT EXISTS stores (" +
                "id INTEGER PRIMARY KEY," +
                "author TEXT NOT NULL," +
                "title TEXT NOT NULL," +
                "size INTEGER," +
                "type INTEGER NOT NULL," +
                "avalibility INTEGER NOT NULL," +
                "year TEXT NOT NULL," +
                "borrow_time TEXT," +
                "borrower_id TEXT" +
                ")";
        String createBorrowsTable = "CREATE TABLE IF NOT EXISTS borrows (" +
                "borrower_id TEXT," +
                "store_id INTEGER," +
                "borrow_time TEXT NOT NULL," +
                "PRIMARY KEY (borrower_id, store_id)," +
                "FOREIGN KEY (borrower_id) REFERENCES borrowers(id)," +
                "FOREIGN KEY (store_id) REFERENCES stores(id)" +
                ")";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createBorrowersTable);
            stmt.execute(createStoresTable);
            stmt.execute(createBorrowsTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveBorrower(borrower borrower) {
        String sql = "INSERT OR REPLACE INTO borrowers (id, name, password, regis_time) VALUES (?,?,?,?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, borrower.recognization);
            pstmt.setString(2, borrower.name);
            pstmt.setString(3, borrower.password);
            pstmt.setString(4, borrower.regisTime.toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveStore(store store) {
        String sql = "INSERT OR REPLACE INTO stores (id, author, title, size, type, avalibility, year, borrow_time, borrower_id) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, store.recognization);
            pstmt.setString(2, store.author);
            pstmt.setString(3, store.title);
            pstmt.setInt(4, store.size);
            pstmt.setInt(5, store.type);
            pstmt.setInt(6, store.avalibility);
            pstmt.setString(7, store.year.toString());
            if (store.borrowTime != null) {
                pstmt.setString(8, store.borrowTime.toString());
            } else {
                pstmt.setString(8, null);
            }
            pstmt.setString(9, store.borrowerID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveBorrow(borrower borrower, store store) {
        String sql = "INSERT OR REPLACE INTO borrows (borrower_id, store_id, borrow_time) VALUES (?,?,?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, borrower.recognization);
            pstmt.setInt(2, store.recognization);
            pstmt.setString(3, store.borrowTime.toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<borrower> getAllBorrowers() {
        List<borrower> borrowers = new ArrayList<>();
        String sql = "SELECT * FROM borrowers";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String password = rs.getString("password");
                LocalDateTime regisTime = LocalDateTime.parse(rs.getString("regis_time"));
                borrower borrower = new borrower(name, id, password);
                borrower.regisTime = regisTime;
                borrowers.add(borrower);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return borrowers;
    }

    public List<store> getAllStores() {
        List<store> stores = new ArrayList<>();
        String sql = "SELECT * FROM stores";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String author = rs.getString("author");
                String title = rs.getString("title");
                int size = rs.getInt("size");
                int type = rs.getInt("type");
                int avalibility = rs.getInt("avalibility");
                LocalDateTime year = LocalDateTime.parse(rs.getString("year"));
                String borrowTimeStr = rs.getString("borrow_time");
                LocalDateTime borrowTime = borrowTimeStr != null ? LocalDateTime.parse(borrowTimeStr) : null;
                String borrowerID = rs.getString("borrower_id");
                store store = new store(author, title, size, type, id);
                store.avalibility = avalibility;
                store.year = year;
                store.borrowTime = borrowTime;
                store.borrowerID = borrowerID;
                stores.add(store);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stores;
    }
}    