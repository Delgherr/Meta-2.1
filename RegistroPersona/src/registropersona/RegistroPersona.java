package registropersona;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.*;
import java.util.Arrays;
import java.util.List;

public class RegistroPersona extends Application {

    
    
    private TableView<Persona> tableView = new TableView<>();
    private ObservableList<Persona> personas = FXCollections.observableArrayList();
    private Connection conn;

    @Override
    public void start(Stage primaryStage) {
        conectarDB();
        cargarPersonas();

        TableColumn<Persona, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());

        TableColumn<Persona, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNombre()));

        TableColumn<Persona, String> colDireccion = new TableColumn<>("Dirección");
        colDireccion.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDireccion()));

        TableColumn<Persona, String> colTipoVehiculo = new TableColumn<>("Tipo Vehículo");
        colTipoVehiculo.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTipoVehiculo()));

        TableColumn<Persona, String> colTelefonos = new TableColumn<>("Teléfonos");
        colTelefonos.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(String.join(", ", data.getValue().getTelefonos())));

        tableView.getColumns().addAll(colId, colNombre, colDireccion, colTipoVehiculo, colTelefonos);
        tableView.setItems(personas);

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        colId.setPrefWidth(50);  // ID no necesita mucho espacio
        colNombre.setPrefWidth(150);
        colDireccion.setPrefWidth(200);
        colTipoVehiculo.setPrefWidth(120);
        colTelefonos.setPrefWidth(180);

        Button btnAgregar = new Button("Agregar");
        btnAgregar.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 5px;");
        btnAgregar.setOnMouseEntered(e -> btnAgregar.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 5px;"));
        btnAgregar.setOnMouseExited(e -> btnAgregar.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 5px;"));

        Button btnEliminar = new Button("Eliminar");
        btnEliminar.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 5px;");
        btnEliminar.setOnMouseEntered(e -> btnEliminar.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 5px;"));
        btnEliminar.setOnMouseExited(e -> btnEliminar.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 5px;"));

        Button btnEditar = new Button("Editar");
        btnEditar.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 5px;");
        btnEditar.setOnMouseEntered(e -> btnEditar.setStyle("-fx-background-color: #1976D2; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 5px;"));
        btnEditar.setOnMouseExited(e -> btnEditar.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 5px;"));

        btnAgregar.setOnAction(e -> agregarPersona());
        btnEliminar.setOnAction(e -> eliminarPersona());
        btnEditar.setOnAction(e -> editarPersona());
        
        VBox vbox = new VBox(10, tableView, btnAgregar, btnEliminar, btnEditar);

        vbox.setAlignment(Pos.CENTER);

        primaryStage.setScene(new Scene(vbox, 600, 400));
        primaryStage.setTitle("CRUD Personas");
        primaryStage.show();
    }

    private void conectarDB() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Personas", "root", "RaulRaul");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void cargarPersonas() {
        personas.clear();
        String query = "SELECT p.id_persona, p.nombre, p.direccion, "
                + "IFNULL(v.tipo, '') AS tipo, "
                + "(SELECT GROUP_CONCAT(t.numero SEPARATOR ', ') FROM Telefono t WHERE t.id_persona = p.id_persona) AS telefonos "
                + "FROM Persona p "
                + "LEFT JOIN Vehiculo v ON p.id_persona = v.id_persona";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                List<String> telefonos = rs.getString("telefonos") != null
                        ? Arrays.asList(rs.getString("telefonos").split(", "))
                        : List.of();
                personas.add(new Persona(
                        rs.getInt("id_persona"),
                        rs.getString("nombre"),
                        rs.getString("direccion"),
                        rs.getString("tipo"),
                        telefonos
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void agregarPersona() {
        TextField txtNombre = new TextField();
        TextField txtDireccion = new TextField();
        TextField txtTelefonos = new TextField();
        TextField txtTipoVehiculo = new TextField();
        Button btnGuardar = new Button("Guardar");

        btnGuardar.setOnAction(e -> {
            try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO Persona (nombre, direccion) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, txtNombre.getText());
                stmt.setString(2, txtDireccion.getText());
                stmt.executeUpdate();

                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int idPersona = generatedKeys.getInt(1);
                    guardarTelefonos(idPersona, txtTelefonos.getText());
                    guardarVehiculo(idPersona, txtTipoVehiculo.getText());
                }

                cargarPersonas();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        VBox vbox = new VBox(10, new Label("Nombre:"), txtNombre, new Label("Dirección:"), txtDireccion, new Label("Teléfonos (separados por comas):"), txtTelefonos, new Label("Tipo Vehículo:"), txtTipoVehiculo, btnGuardar);
        Stage stage = new Stage();
        stage.setScene(new Scene(vbox, 350, 300));
        stage.show();
    }

    private void guardarTelefonos(int idPersona, String telefonos) {
        String[] numeros = telefonos.split(",");
        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO Telefono (id_persona, numero) VALUES (?, ?)");) {
            for (String numero : numeros) {
                stmt.setInt(1, idPersona);
                stmt.setString(2, numero.trim());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void guardarVehiculo(int idPersona, String tipoVehiculo) {
        if (!tipoVehiculo.isEmpty()) {
            try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO Vehiculo (id_persona, marca, anio, tipo) VALUES (?, 'Desconocido', 2000, ?)");) {
                stmt.setInt(1, idPersona);
                stmt.setString(2, tipoVehiculo);
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void eliminarPersona() {
        Persona seleccionada = tableView.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM Persona WHERE id_persona = ?")) {
                stmt.setInt(1, seleccionada.getId());
                stmt.executeUpdate();
                cargarPersonas();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void actualizarVehiculo(int idPersona, String tipoVehiculo) {
        if (!tipoVehiculo.isEmpty()) {
            try (PreparedStatement stmt = conn.prepareStatement("UPDATE Vehiculo SET tipo = ? WHERE id_persona = ?")) {
                stmt.setString(1, tipoVehiculo);
                stmt.setInt(2, idPersona);
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void actualizarTelefonos(int idPersona, String telefonos) {
        try (PreparedStatement stmtDelete = conn.prepareStatement("DELETE FROM Telefono WHERE id_persona = ?")) {
            stmtDelete.setInt(1, idPersona);
            stmtDelete.executeUpdate();

            String[] numeros = telefonos.split(",");
            try (PreparedStatement stmtInsert = conn.prepareStatement("INSERT INTO Telefono (id_persona, numero) VALUES (?, ?)")) {
                for (String numero : numeros) {
                    stmtInsert.setInt(1, idPersona);
                    stmtInsert.setString(2, numero.trim());
                    stmtInsert.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void editarPersona() {
        Persona seleccionada = tableView.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            TextField txtNombre = new TextField(seleccionada.getNombre());
            TextField txtDireccion = new TextField(seleccionada.getDireccion());
            TextField txtTelefonos = new TextField(String.join(", ", seleccionada.getTelefonos()));
            TextField txtTipoVehiculo = new TextField(seleccionada.getTipoVehiculo());
            Button btnGuardar = new Button("Guardar");

            btnGuardar.setOnAction(e -> {
                try (PreparedStatement stmt = conn.prepareStatement("UPDATE Persona SET nombre = ?, direccion = ? WHERE id_persona = ?")) {
                    stmt.setString(1, txtNombre.getText());
                    stmt.setString(2, txtDireccion.getText());
                    stmt.setInt(3, seleccionada.getId());
                    stmt.executeUpdate();

                    // Actualizar teléfonos
                    actualizarTelefonos(seleccionada.getId(), txtTelefonos.getText());
                    // Actualizar vehículo
                    actualizarVehiculo(seleccionada.getId(), txtTipoVehiculo.getText());

                    cargarPersonas(); // Recargar la lista
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            });

            VBox vbox = new VBox(10, new Label("Nombre:"), txtNombre, new Label("Dirección:"), txtDireccion, new Label("Teléfonos (separados por comas):"), txtTelefonos, new Label("Tipo Vehículo:"), txtTipoVehiculo, btnGuardar);
            Stage stage = new Stage();
            stage.setScene(new Scene(vbox, 350, 300));
            stage.show();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
