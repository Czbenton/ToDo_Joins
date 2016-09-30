import org.h2.tools.Server;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Zach on 9/6/16.
 */
public class ToDo {

    public static void main(String[] args) throws SQLException {
        Server.createWebServer().start();
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        createTables(conn);

        Scanner scanner = new Scanner(System.in);

        System.out.println("Please enter your User Name");
        String userName = scanner.nextLine();
        User user = selectUser(conn, userName);
        if (user == null) {
            System.out.println("user name created, please enter password");
            String password = scanner.nextLine();
            insertUser(conn, userName, password);
            user = selectUser(conn,userName);
        }

        while (true) {
            System.out.println("\n1. Create to-do item");
            System.out.println("2. Toggle to-do item");
            System.out.println("3. List to-do items");
            System.out.println("4. Delete to-do item");

            String option = scanner.nextLine();


            switch (option) {
                case "1":
                    System.out.println("Enter your to-do item:");
                    String text = scanner.nextLine();

                    insertToDo(conn, user.id, text);
                    break;
                case "2": {
                    System.out.println("Enter the number of the item you want to toggle:");
                    int itemNum = Integer.valueOf(scanner.nextLine());

                    ArrayList<ToDoItem> todos = selectToDos(conn);

                    toggleToDo(conn, todos.get(itemNum - 1).id);
                    break;
                }
                case "3":
                    ArrayList<ToDoItem> items = selectToDos(conn);
                    int i = 1;
                    for (ToDoItem item : items) {
                        String checkbox = "[ ] ";
                        if (item.isDone) {
                            checkbox = "[x] ";
                        }
                        String line = String.format("%d. %s %s", i++, checkbox, item.text);
                        System.out.println(line);
                    }
                    break;
                case "4": {
                    System.out.println("Enter the number of the item you want to delete");
                    int del = Integer.parseInt(scanner.nextLine());
                    ArrayList<ToDoItem> todos = selectToDos(conn);

                    deleteItem(conn, todos.get(del - 1).id);
                    break;
                }
                default:
                    System.out.println("Invalid option");
                    break;
            }
        }
    }

    public static void createTables(Connection conn) throws SQLException {
        Statement statement = conn.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS todos (id IDENTITY , userId INT , text VARCHAR , is_done BOOLEAN)");
        statement.execute("CREATE TABLE IF NOT EXISTS users (id IDENTITY , name VARCHAR , password VARCHAR )");
    }


    public static void insertToDo(Connection conn, int userId, String text) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO todos VALUES (null, ?, ?, false)");
        stmt.setInt(1, userId );
        stmt.setString(2, text);
        stmt.execute();
    }


    public static void toggleToDo(Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("UPDATE todos TABLE SET is_done = NOT is_done WHERE id = ?");
        stmt.setInt(1, id);
        stmt.execute();
    }

    public static void deleteItem(Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM todos WHERE id = ?");
        stmt.setInt(1, id);
        stmt.execute();
    }

    public static void insertUser(Connection conn, String name, String password) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO users VALUES (NULL , ?, ?)");
        stmt.setString(1, name);
        stmt.setString(2, password);
        stmt.execute();
    }

    public static User selectUser(Connection conn, String name) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE name = ?");
        stmt.setString(1, name);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            int id = results.getInt("id");
            String password = results.getString("password");
            return new User(id, name, password);
        }
        return null;
    }

    public static ToDoItem selectTodo(Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM todos JOIN users ON todos.userId = users.id WHERE todos.id = ?");
        stmt.setInt(1, id);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            int userId = results.getInt("userId");
            String text = results.getString("text");
            boolean isDone = results.getBoolean("is_done");
            return new ToDoItem(id, userId, text, isDone);
        }
        return null;
    }

    public static ArrayList<ToDoItem> selectToDos(Connection conn) throws SQLException {
        ArrayList<ToDoItem> items = new ArrayList<>();
        Statement stmt = conn.createStatement();
        ResultSet results = stmt.executeQuery("SELECT * FROM todos");
        while (results.next()) {
            int id = results.getInt("id");
            String text = results.getString("text");
            boolean isDone = results.getBoolean("is_done");
            items.add(new ToDoItem(id, text, isDone));
        }
        return items;
    }

}
