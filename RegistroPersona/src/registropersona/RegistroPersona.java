package registropersona;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;

public class RegistroPersona extends Application {

    private TextField txtNombre, txtDireccion, txtTelefono;
    private ComboBox<String> comboVehiculo; // ComboBox para vehículo
    private TableView<Persona> tableView;
    private ObservableList<Persona> data;

    private static final String URL = "jdbc:mysql://localhost:3306/Personas";
    private static final String USER = "root"; 
    private static final String PASSWORD = "RaulRaul";

    @Override
    public void start(Stage primaryStage) {
        // Etiquetas para los campos del formulario de Persona
        Label lblNombre = new Label("Nombre:");
        Label lblDireccion = new Label("Dirección:");
        Label lblTelefono = new Label("Teléfono:");
        Label lblVehiculo = new Label("Vehículo:");

        // Ingresar datos de Persona
        txtNombre = new TextField();
        txtNombre.setPromptText("Ingresa nombre");

        txtDireccion = new TextField();
        txtDireccion.setPromptText("Ingresa dirección");

        txtTelefono = new TextField();
        txtTelefono.setPromptText("Ingresa teléfono");

        // ComboBox para seleccionar tipo de vehículo
        comboVehiculo = new ComboBox<>();
        comboVehiculo.setItems(FXCollections.observableArrayList("Automóvil", "Motocicleta", "Bicicleta", "Camión"));
        comboVehiculo.setPromptText("Selecciona un vehículo");

        // Botones de Persona
        Button btnGuardar = new Button("Guardar Persona");
        btnGuardar.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnGuardar.setOnAction(e -> guardarPersona());

        Button btnEliminar = new Button("Eliminar Persona");
        btnEliminar.setStyle("-fx-background-color: #E53935; -fx-text-fill: white;");
        btnEliminar.setOnAction(e -> eliminarPersona());

        Button btnActualizar = new Button("Actualizar Persona");
        btnActualizar.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        btnActualizar.setOnAction(e -> actualizarPersona());

        // Formulario Persona GridPane
        GridPane gridPersona = new GridPane();
        gridPersona.setPadding(new Insets(20));
        gridPersona.setVgap(10);
        gridPersona.setHgap(10);
        gridPersona.setAlignment(Pos.CENTER);
        gridPersona.add(lblNombre, 0, 0);
        gridPersona.add(txtNombre, 1, 0);
        gridPersona.add(lblDireccion, 0, 1);
        gridPersona.add(txtDireccion, 1, 1);
        gridPersona.add(lblTelefono, 0, 2);
        gridPersona.add(txtTelefono, 1, 2);
        gridPersona.add(lblVehiculo, 0, 3);
        gridPersona.add(comboVehiculo, 1, 3);
        gridPersona.add(btnGuardar, 0, 4);
        gridPersona.add(btnEliminar, 1, 4);
        gridPersona.add(btnActualizar, 0, 5);

        // TableView para mostrar los datos de las personas
        tableView = new TableView<>();
        TableColumn<Persona, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());

        TableColumn<Persona, String> colDireccion = new TableColumn<>("Dirección");
        colDireccion.setCellValueFactory(cellData -> cellData.getValue().direccionProperty());

        TableColumn<Persona, String> colTelefono = new TableColumn<>("Teléfono");
        colTelefono.setCellValueFactory(cellData -> cellData.getValue().telefonoProperty());

        TableColumn<Persona, String> colVehiculo = new TableColumn<>("Vehículo");
        colVehiculo.setCellValueFactory(cellData -> cellData.getValue().vehiculoProperty());

        // Se establece el ajuste de columnas para redimensionarse automáticamente
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Agregar columnas a la tabla
        tableView.getColumns().addAll(colNombre, colDireccion, colTelefono, colVehiculo);
        cargarDatos();  // Carga los datos desde la base de datos al iniciar

        // Escuchar la selección de elementos en la tabla y actualizar los campos de texto
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtNombre.setText(newSelection.getNombre());
                txtDireccion.setText(newSelection.getDireccion());
                txtTelefono.setText(newSelection.getTelefono());
                comboVehiculo.setValue(newSelection.getVehiculo());
            }
        });

        // Organizar el diseño con VBox
        VBox root = new VBox(10, gridPersona, tableView);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f4f4f4;");

        primaryStage.setTitle("Registro de Persona y Vehículo");
        primaryStage.setScene(new Scene(root, 600, 500));
        primaryStage.show();
    }

    // Método para cargar los datos de la base de datos
    private void cargarDatos() {
        data = FXCollections.observableArrayList();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Personas")) {

            while (rs.next()) {
                data.add(new Persona(rs.getString("nombre"), rs.getString("direccion"), rs.getString("telefono"), rs.getString("vehiculo")));
            }
            tableView.setItems(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para guardar Persona
    private void guardarPersona() {
        String nombre = txtNombre.getText();
        String direccion = txtDireccion.getText();
        String telefono = txtTelefono.getText();
        String vehiculo = comboVehiculo.getValue();

        if (nombre.isEmpty() || telefono.isEmpty() || vehiculo == null) {
            mostrarAlerta("Error", "El nombre, teléfono y vehículo son obligatorios.");
            return;
        }

        String sql = "INSERT INTO Personas (nombre, direccion, telefono, vehiculo) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombre);
            pstmt.setString(2, direccion);
            pstmt.setString(3, telefono);
            pstmt.setString(4, vehiculo);
            pstmt.executeUpdate();
            mostrarAlerta("Éxito", "Persona guardada correctamente.");
            cargarDatos();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para eliminar Persona
    private void eliminarPersona() {
        Persona personaSeleccionada = tableView.getSelectionModel().getSelectedItem();
        if (personaSeleccionada == null) {
            mostrarAlerta("Error", "Selecciona una persona para eliminar.");
            return;
        }

        String sql = "DELETE FROM Personas WHERE nombre = ? AND telefono = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, personaSeleccionada.getNombre());
            pstmt.setString(2, personaSeleccionada.getTelefono());
            pstmt.executeUpdate();
            mostrarAlerta("Éxito", "Persona eliminada.");
            cargarDatos();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para actualizar Persona
    private void actualizarPersona() {
        Persona personaSeleccionada = tableView.getSelectionModel().getSelectedItem();
        if (personaSeleccionada == null) {
            mostrarAlerta("Error", "Selecciona una persona para actualizar.");
            return;
        }

        String sql = "UPDATE Personas SET nombre = ?, direccion = ?, telefono = ?, vehiculo = ? WHERE nombre = ? AND telefono = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, txtNombre.getText());
            pstmt.setString(2, txtDireccion.getText());
            pstmt.setString(3, txtTelefono.getText());
            pstmt.setString(4, comboVehiculo.getValue());
            pstmt.setString(5, personaSeleccionada.getNombre());
            pstmt.setString(6, personaSeleccionada.getTelefono());
            pstmt.executeUpdate();
            mostrarAlerta("Éxito", "Persona actualizada correctamente.");
            cargarDatos();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para mostrar alertas
    private void mostrarAlerta(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
