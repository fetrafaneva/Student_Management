import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/AjoutEtudiantServlet")
public class AjoutEtudiantServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Configuration de la base de données
    private static final String DB_URL = "jdbc:mysql://localhost:3306/projet_jsp?useSSL=false&characterEncoding=utf-8";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    // CSS pour la page HTML
    private static final String STYLE = "<style>"
            + "body { text-align: center; font-family: Arial, sans-serif; }"
            + "form { width: 40%; margin: 50px auto; padding: 20px; border: 1px solid #ccc; border-radius: 5px; }"
            + "input[type=text], input[type=number] { width: 100%; padding: 10px; margin: 8px 0; box-sizing: border-box; }"
            + "input[type=submit] { background-color: #4CAF50; color: white; padding: 12px 20px; margin: 8px 0; border: none; border-radius: 5px; cursor: pointer; }"
            + "input[type=submit]:hover { background-color: #45a049; }"
            + "h1 { margin-top: 50px; }"
            + "</style>";

    // Affichage du formulaire d'ajout d'étudiant
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Ajouter un nouvel étudiant</title>");
            out.println(STYLE);
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Ajouter un nouvel étudiant</h1>");
            out.println("<form action='AjoutEtudiantServlet' method='post'>");
            out.println("<label for='numEt'>Numéro d'étudiant:</label><br>");
            out.println("<input type='text' id='numEt' name='numEt' required><br>");
            out.println("<label for='nom'>Nom:</label><br>");
            out.println("<input type='text' id='nom' name='nom' required><br>");
            out.println("<label for='moyenne'>Moyenne:</label><br>");
            out.println("<input type='number' step='0.01' id='moyenne' name='moyenne' required><br>");
            out.println("<input type='submit' value='Ajouter'>");
            out.println("</form>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // Traitement du formulaire et ajout en base
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String numEtudiant = request.getParameter("numEt");
        String nom = request.getParameter("nom");
        String moyenne = request.getParameter("moyenne");

        // Vérification simple des champs
        if (numEtudiant == null || numEtudiant.isEmpty() ||
            nom == null || nom.isEmpty() ||
            moyenne == null || moyenne.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Tous les champs sont obligatoires !");
            return;
        }

        // Connexion à la base et insertion
        String query = "INSERT INTO etudiant (numEt, nom, moyenne) VALUES (?, ?, ?)";
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new ServletException("Driver JDBC introuvable", e);
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, numEtudiant);
            preparedStatement.setString(2, nom);
            preparedStatement.setString(3, moyenne);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                // Redirection après succès
                response.sendRedirect("index.jsp");
            } else {
                sendError(response, "Erreur lors de l'ajout de l'étudiant !");
            }

        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, "Une erreur s'est produite : " + e.getMessage());
        }
    }

    // Méthode utilitaire pour afficher un message d'erreur
    private void sendError(HttpServletResponse response, String message) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Résultat de l'ajout</title>");
            out.println(STYLE);
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>" + message + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }
}
