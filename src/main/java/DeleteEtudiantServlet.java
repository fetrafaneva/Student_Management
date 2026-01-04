import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/DeleteEtudiantServlet")
public class DeleteEtudiantServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Configuration de la base de données
    private static final String DB_URL = "jdbc:mysql://localhost:3306/projet_jsp?useSSL=false&characterEncoding=utf-8";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String numEtudiant = request.getParameter("numEt");

        // Vérification des données
        if (numEtudiant == null || numEtudiant.trim().isEmpty()) {
            redirectWithMessage(response, "index.jsp", "error=missing");
            return;
        }

        // Chargement du driver JDBC
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            redirectWithMessage(response, "index.jsp", "error=driver");
            return;
        }

        // Suppression de l'étudiant avec try-with-resources
        String query = "DELETE FROM etudiant WHERE numEt = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, numEtudiant);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                redirectWithMessage(response, "index.jsp", "success=true");
            } else {
                redirectWithMessage(response, "index.jsp", "error=notfound");
            }

        } catch (Exception e) {
            e.printStackTrace();
            redirectWithMessage(response, "index.jsp", "error=delete");
        }
    }

    /**
     * Méthode utilitaire pour rediriger avec un paramètre de message
     */
    private void redirectWithMessage(HttpServletResponse response, String url, String message) throws IOException {
        response.sendRedirect(url + "?" + message);
    }
}
