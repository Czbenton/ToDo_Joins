import com.sun.tools.javac.comp.Todo;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by Zach on 9/28/16.
 */
public class ToDoTest {

    public Connection startConnection()throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:test");
        ToDo.createTables(conn);
        return conn;
    }

    @Test
    public void testUser() throws SQLException{
        Connection conn = startConnection();
        ToDo.insertUser(conn,"Alice","");
        User user = ToDo.selectUser(conn,"Alice");
        conn.close();
        assertTrue(user != null);
    }

    @Test
    public void testItems()throws SQLException{
        Connection conn = startConnection();
        ToDo.insertUser(conn, "Zach","");
        ToDo.insertUser(conn, "Sam","");
        User zach = ToDo.selectUser(conn,"Zach");
        User sam = ToDo.selectUser(conn, "Sam");
        ToDo.insertToDo(conn,zach.id, "This is a test");
        ToDo.insertToDo(conn,sam.id, "This is another test");
        ArrayList<ToDoItem> items = ToDo.selectToDos(conn);
        conn.close();

        assertTrue(items.size() == 2);
    }

    @Test
    public void testSelectTodo()throws SQLException{
        Connection conn = startConnection();
        ToDo.insertUser(conn, "Zach","");
        ToDo.insertUser(conn, "Sam","");
        User zach = ToDo.selectUser(conn,"Zach");
        User sam = ToDo.selectUser(conn, "Sam");
        ToDo.insertToDo(conn,zach.id, "This is a test");
        ToDo.insertToDo(conn,sam.id, "This is another test");
        ToDo.selectTodo(conn, zach.id);
    }


}