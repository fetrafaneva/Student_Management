import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/EditEtudiantServlet")
public class EditEtudiantServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/projet_jsp?useSSL=false&characterEncoding=utf-8";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private static final String STYLE = "<style>"
            + "body { text-align: center; font-family: Arial, sans-serif; }"
            + "form { width: 40%; margin: 50px auto; padding: 20px; border: 1px solid #ccc; border-radius: 5px; }"
            + "input[type=text], input[type=number] { width: 100%; padding: 10px; margin: 8px 0; box-sizing: border-box; }"
            + "input[type=submit] { background-color: #4CAF50; color: white; padding: 12px 20px; margin: 8px 0; border: none; border-radius: 5px; cursor: pointer; }"
            + "input[type=submit]:hover { background-color: #45a049; }"
            + "h1 { margin-top: 50px; }"
            + "</style>";

    private static final String SCRIPT = "<script>"
            + "function confirmUpdate() {"
            + "  return confirm('Êtes-vous sûr de vouloir modifier cet étudiant ?');"
            + "}"
            + "</script>";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        String numEtudiant = request.getParameter("numEt");

        if (numEtudiant == null || numEtudiant.trim().isEmpty()) {
            showMessage(response, "Numéro d'étudiant non spécifié.");
            return;
        }

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            showMessage(response, "Driver JDBC introuvable : " + e.getMessage());
            return;
        }

        String query = "SELECT * FROM etudiant WHERE numEt = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, numEtudiant);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String nom = rs.getString("nom");
                    String moyenne = rs.getString("moyenne");
                    showEditForm(response, numEtudiant, nom, moyenne);
                } else {
                    showMessage(response, "Aucun étudiant trouvé avec ce numéro.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            showMessage(response, "Une erreur s'est produite : " + e.getMessage());
        }
    }

    private void showEditForm(HttpServletResponse response, String numEt, String nom, String moyenne) throws IOException {
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Modifier l'étudiant</title>");
            out.println(STYLE);
            out.println(SCRIPT);
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Modifier l'étudiant</h1>");
            out.println("<form action='UpdateEtudiantServlet' method='POST' onsubmit='return confirmUpdate()'>");
            out.println("<input type='hidden' name='numEt' value='" + numEt + "'>");
            out.println("Nom: <input type='text' name='nom' value='" + nom + "' required><br>");
            out.println("Moyenne: <input type='number' step='0.01' name='moyenne' value='" + moyenne + "' required><br>");
            out.println("<input type='submit' value='Enregistrer'>");
            out.println("</form>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    private void showMessage(HttpServletResponse response, String message) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head><title>Information</title>");
            out.println(STYLE);
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>" + message + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }
}
